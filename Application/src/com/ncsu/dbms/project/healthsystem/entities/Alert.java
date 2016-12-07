package com.ncsu.dbms.project.healthsystem.entities;

/**
 * Created by watve on 10/12/2016.
 */
public class Alert {

    private Integer alertID;
    private String alertType;
    private ObservationType observationType;

    public Alert() {
    }

    public Alert(Integer alertID, String alertType, ObservationType observationType) {
        this.alertID = alertID;
        this.alertType = alertType;
        this.observationType = observationType;
    }

    public Integer getAlertID() {
        return alertID;
    }

    public void setAlertID(Integer alertID) {
        this.alertID = alertID;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }
}
