package ch.zhaw.gpi.referralprocessapplication;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Named("GetAppointmentDateRangeAdapter")
public class GetAppointmentDateRangeDelegate implements JavaDelegate {

    @Autowired
    private RestTemplate kisRestTemplate;


    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Fall-ID auslesen
        Long caseId = (Long) execution.getVariable("case_id");

        try {
            // Fall via Fall-ID aus KIS erhalten
            ResponseEntity<MedicalCaseRepresentation> kisResponse = kisRestTemplate.exchange("http://localhost:8090/api/medicalCases/{caseId}", HttpMethod.GET, null, MedicalCaseRepresentation.class, caseId);
            
            // Frühst- und spätmöglichstes Datum als Prozessvariablen speichern
            execution.setVariable("case_appointment_earliest", kisResponse.getBody().getDateEarliest());
            execution.setVariable("case_appointment_latest", kisResponse.getBody().getDateLatest());
        } catch (Exception e) {
            throw e;
        }
        
    }
    
}
