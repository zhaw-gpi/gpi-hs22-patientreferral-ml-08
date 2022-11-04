package ch.zhaw.gpi.referralprocessapplication;

import java.util.Date;

public class MedicalCaseRepresentation {
    private Date dateEarliest;
    private Date dateLatest;
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
