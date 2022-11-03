package ch.zhaw.gpi.referralprocessapplication.kis_mock;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MedicalCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;
    @ManyToOne
    private Patient patient;
    private String reasons;
    private Long referrerId;
    private Boolean isEmergency;
    @Temporal(TemporalType.DATE)
    private Date dateDesired;
    private String responsibleDepartment;
    @Temporal(TemporalType.DATE)
    private Date dateEarliest;
    @Temporal(TemporalType.DATE)
    private Date dateLatest;

    public Long getCaseId() {
        return caseId;
    }
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    public String getReasons() {
        return reasons;
    }
    public void setReasons(String reasons) {
        this.reasons = reasons;
    }
    public Long getReferrerId() {
        return referrerId;
    }
    public void setReferrerId(Long referrerId) {
        this.referrerId = referrerId;
    }
    public Boolean getIsEmergency() {
        return isEmergency;
    }
    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }
    public Date getDateDesired() {
        return dateDesired;
    }
    public void setDateDesired(Date dateDesired) {
        this.dateDesired = dateDesired;
    }
    public String getResponsibleDepartment() {
        return responsibleDepartment;
    }
    public void setResponsibleDepartment(String responsibleDepartment) {
        this.responsibleDepartment = responsibleDepartment;
    }
    public Date getDateEarliest() {
        return dateEarliest;
    }
    public void setDateEarliest(Date dateEarliest) {
        this.dateEarliest = dateEarliest;
    }
    public Date getDateLatest() {
        return dateLatest;
    }
    public void setDateLatest(Date dateLatest) {
        this.dateLatest = dateLatest;
    }        
}
