package com.ncsu.dbms.project.healthsystem.entities;

/**
 * Created by watve on 10/12/2016.
 */
public class ObservationType {

    private Integer observationTypeID;
    private String observationTypeName;
    private String description;
    private String metric;

    public ObservationType() {
    }

    public ObservationType(Integer observationTypeID, String observationTypeName, String description, String metric) {
        this.observationTypeID = observationTypeID;
        this.observationTypeName = observationTypeName;
        this.description = description;
        this.metric = metric;
    }

    public Integer getObservationTypeID() {
        return observationTypeID;
    }

    public void setObservationTypeID(Integer observationTypeID) {
        this.observationTypeID = observationTypeID;
    }

    public String getObservationTypeName() {
        return observationTypeName;
    }

    public void setObservationTypeName(String observationTypeName) {
        this.observationTypeName = observationTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
