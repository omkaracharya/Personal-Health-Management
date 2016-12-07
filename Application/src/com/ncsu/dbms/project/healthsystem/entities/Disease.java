package com.ncsu.dbms.project.healthsystem.entities;

/**
 * Created by watve on 10/12/2016.
 */
public class Disease {

    private Integer diseaseID;
    private String diseaseName;

    public Disease() {
    }

    public Disease(Integer diseaseID, String diseaseName) {
        this.diseaseID = diseaseID;
        this.diseaseName = diseaseName;
    }

    public Integer getDiseaseID() {
        return diseaseID;
    }

    public void setDiseaseID(Integer diseaseID) {
        this.diseaseID = diseaseID;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }
}
