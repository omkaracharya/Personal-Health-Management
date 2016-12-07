package com.ncsu.dbms.project.healthsystem.entities;

/**
 * Created by watve on 10/12/2016.
 */
public class Recommendation {

    private Integer recommendationID;
    private Integer frequency;
    private Integer lowerValue;
    private Integer higherValue;
    private ObservationType observationType;

    public Recommendation() {
    }

    public Recommendation(Integer recommendationID, Integer frequency, Integer lowerValue, Integer higherValue, ObservationType observationType) {
        this.recommendationID = recommendationID;
        this.frequency = frequency;
        this.lowerValue = lowerValue;
        this.higherValue = higherValue;
        this.observationType = observationType;
    }

    public Integer getRecommendationID() {
        return recommendationID;
    }

    public void setRecommendationID(Integer recommendationID) {
        this.recommendationID = recommendationID;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getLowerValue() {
        return lowerValue;
    }

    public void setLowerValue(Integer lowerValue) {
        this.lowerValue = lowerValue;
    }

    public Integer getHigherValue() {
        return higherValue;
    }

    public void setHigherValue(Integer higherValue) {
        this.higherValue = higherValue;
    }

    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }
}
