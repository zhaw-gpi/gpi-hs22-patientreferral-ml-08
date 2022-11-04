package ch.zhaw.gpi.referralprocessapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import camundajar.impl.com.google.gson.JsonObject;

@Named("CreatePatientAndMedicalCaseAdapter")
public class CreatePatientAndMedicalCaseDelegate implements JavaDelegate {

    @Autowired
    private RestTemplate kisRestClient;

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

        // Für Datumsumwandlung in String erforderliche Zeile
        SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Für HTTP-Requests erforderlichen Header zusammenstellen
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prüfen, ob die Patient:in bereits im KIS erfasst ist anhand
        // Versichertennummer
        try {
            kisRestClient.exchange("http://localhost:8090/api/patients/{insuranceNumber}", HttpMethod.GET, null, Void.class, patInsuranceNumber);
            // Falls ja, betreffend Patient:in nichts zu tun
        } catch (Exception getException) {
            // Vereinfachte Annahme, dass jede Exception = 404 Exception ist
            // Falls also Patient:in nicht in KIS vorhanden ist, Patient:in neu anlegen als JsonObject
            JsonObject patientNew = new JsonObject();
            patientNew.addProperty("insuranceNumber", patInsuranceNumber);
            patientNew.addProperty("firstName", patFirstname);
            patientNew.addProperty("lastName", patLastname);            
            patientNew.addProperty("birthDate", yearMonthDayFormat.format((patBirthday == null ? new Date() : patBirthday)));
            patientNew.addProperty("plz", patPlz);
            switch (patInsuranceType) {
                case "V1":
                    patientNew.addProperty("insuranceType", "privat");
                    break;

                case "V2":
                patientNew.addProperty("insuranceType", "halbprivat");
                    break;

                case "V3":
                patientNew.addProperty("insuranceType", "allgemein");
                    break;
            }

            // Http Request-Content für das Hinzufügen der Patient:in zusammenbauen
            HttpEntity<String> requestEntity = new HttpEntity<String>(patientNew.toString(), headers);

            // Http Request für das Hinzufügen der Patient:in ausführen
            kisRestClient.exchange("http://localhost:8090/api/patients", HttpMethod.POST, requestEntity, Void.class);            
        }

        // Einen neuen Fall anlegen als JjsonObject
        JsonObject medicalCaseNew = new JsonObject();
        medicalCaseNew.addProperty("reasons", caseRefReasons);
        medicalCaseNew.addProperty("referrerId", caseRefId);
        medicalCaseNew.addProperty("isEmergency", caseIsEmergency);
        medicalCaseNew.addProperty("dateDesired", yearMonthDayFormat.format(caseDesiredDate));
        medicalCaseNew.addProperty("responsibleDepartment", caseDepartment);
        medicalCaseNew.addProperty("patient", "http://localhost:8090/api/patients/" + patInsuranceNumber);

        // Http Request-Content für das Hinzufügen des Falls zusammenbauen
        HttpEntity<String> requestEntity = new HttpEntity<String>(medicalCaseNew.toString(), headers);

        // Http Request für das Hinzufügen des Falls ausführen
        ResponseEntity<String> response = kisRestClient.exchange("http://localhost:8090/api/medicalCases", HttpMethod.POST, requestEntity, String.class);

        // Fall-ID aus dem Location-Attribut des Response-Headers extrahieren
        String caseUrl = response.getHeaders().getLocation().toString();
        String caseId = caseUrl.substring(caseUrl.lastIndexOf("/") + 1, caseUrl.length());

        // Die erhaltene Fall-ID in der Prozessvariable case_id persistieren
        de.setVariable("case_id", Long.valueOf(caseId));
    }

}
