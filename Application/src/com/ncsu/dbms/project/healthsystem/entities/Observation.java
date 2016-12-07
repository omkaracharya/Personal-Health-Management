package com.ncsu.dbms.project.healthsystem.entities;

import java.util.Date;

/**
 * Created by watve on 10/12/2016.
 */
public class Observation {

    private Integer observationID;
    private Integer value;
    private String description;
    private Date recordedTime;
    private ObservationType observationType;
    private Patient patient;

    public Observation() {
    }

    public Observation(Integer observationID, Integer value, String description, Date recordedTime, ObservationType observationType, Patient patient) {
        this.observationID = observationID;
        this.value = value;
        this.description = description;
        this.recordedTime = recordedTime;
        this.observationType = observationType;
        this.patient = patient;
    }

    public Integer getObservationID() {
        return observationID;
    }

    public void setObservationID(Integer observationID) {
        this.observationID = observationID;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(Date recordedTime) {
        this.recordedTime = recordedTime;
    }

    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
