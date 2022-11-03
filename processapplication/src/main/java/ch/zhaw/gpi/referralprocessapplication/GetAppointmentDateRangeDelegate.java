package ch.zhaw.gpi.referralprocessapplication;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.gpi.referralprocessapplication.kis_mock.MedicalCase;
import ch.zhaw.gpi.referralprocessapplication.kis_mock.MedicalCaseRepository;

@Named("GetAppointmentDateRangeAdapter")
public class GetAppointmentDateRangeDelegate implements JavaDelegate {

    @Autowired
    private MedicalCaseRepository medicalCaseRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Fall-ID auslesen
        Long caseId = (Long) execution.getVariable("case_id");
        
        // Fall mittels Repository erhalten
        MedicalCase medicalCase = medicalCaseRepository.getById(caseId);

        // Frühst- und spätmöglichstes Datum als Prozessvariablen speichern
        execution.setVariable("case_appointment_earliest", medicalCase.getDateEarliest());
        execution.setVariable("case_appointment_latest", medicalCase.getDateLatest());
    }
    
}
