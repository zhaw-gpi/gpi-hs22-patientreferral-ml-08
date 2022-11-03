package ch.zhaw.gpi.referralprocessapplication;

import java.util.Date;
import java.util.Optional;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.gpi.referralprocessapplication.kis_mock.MedicalCase;
import ch.zhaw.gpi.referralprocessapplication.kis_mock.MedicalCaseRepository;
import ch.zhaw.gpi.referralprocessapplication.kis_mock.Patient;
import ch.zhaw.gpi.referralprocessapplication.kis_mock.PatientRepository;

@Named("CreatePatientAndMedicalCaseAdapter")
public class CreatePatientAndMedicalCaseDelegate implements JavaDelegate {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalCaseRepository medicalCaseRepository;

    @Override
    public void execute(DelegateExecution de) throws Exception {
        // Prozessvariablen Patient:in auslesen
        Long patInsuranceNumber = (Long) de.getVariable("pat_insurance_number");
        String patFirstname = (String) de.getVariable("pat_firstname");
        String patLastname = (String) de.getVariable("pat_lastname");
        Date patBirthday = (Date) de.getVariable("pat_birthday");
        Long patPlz = (Long) de.getVariable("pat_plz");
        String patInsuranceType = (String) de.getVariable("pat_insurance_type");

        // Prozessvariablen Fall auslesen
        Long caseRefId = (Long) de.getVariable("case_ref_id");
        String caseRefReasons = (String) de.getVariable("case_ref_reasons");
        Boolean caseIsEmergency = (Boolean) de.getVariable("case_is_emergency");
        Date caseDesiredDate = (Date) de.getVariable("case_desired_date");
        String caseDepartment = (String) de.getVariable("case_department");

        // Prüfen, ob die Patient:in bereits im KIS erfasst ist anhand
        // Versichertennummer
        Optional<Patient> patientInDb = patientRepository.findById(patInsuranceNumber);
        Patient patientFoundOrCreated;

        if (patientInDb.isEmpty()) {
            // Falls nicht vorhanden: Patient:in neu anlegen
            Patient patientNew = new Patient();
            patientNew.setInsuranceNumber(patInsuranceNumber);
            patientNew.setFirstName(patFirstname);
            patientNew.setLastName(patLastname);
            patientNew.setBirthDate(patBirthday);
            patientNew.setPlz(patPlz);
            switch (patInsuranceType) {
                case "V1":
                    patientNew.setInsuranceType("privat");
                    break;

                case "V2":
                    patientNew.setInsuranceType("halbprivat");
                    break;

                case "V3":
                    patientNew.setInsuranceType("allgemein");
                    break;
            }
            patientFoundOrCreated = patientRepository.save(patientNew);
        } else {
            patientFoundOrCreated = patientInDb.get();
        }

        // Einen neuen Fall anlegen im KIS sowie diesen Fall mit der Patient:in verknüpfen
        MedicalCase medicalCaseNew = new MedicalCase();
        medicalCaseNew.setPatient(patientFoundOrCreated);
        medicalCaseNew.setReferrerId(caseRefId);
        medicalCaseNew.setReasons(caseRefReasons);
        medicalCaseNew.setIsEmergency(caseIsEmergency);
        medicalCaseNew.setDateDesired(caseDesiredDate);
        medicalCaseNew.setResponsibleDepartment(caseDepartment);
        MedicalCase medicalCaseCreated = medicalCaseRepository.save(medicalCaseNew);

        // Die erhaltene Fall-ID in der Prozessvariable case_id persistieren
        de.setVariable("case_id", medicalCaseCreated.getCaseId());
    }

}
