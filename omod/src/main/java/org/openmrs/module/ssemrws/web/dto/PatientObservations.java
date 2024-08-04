package org.openmrs.module.ssemrws.web.dto;

public class PatientObservations {
    private String enrollmentDate;

    private String lastRefillDate;

    private String dateOfinitiation;

    private String arvRegimen;

    private Double lastCD4Count;

    private String tbStatus;

    private String arvRegimenDose;

    private String whoClinicalStage;

    private String dateVLResultsReceived;

    private String chwName;

    private String chwPhone;

    private String chwAddress;

    private String vlResults;

    private Double bmiMuac;

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getLastRefillDate() {
        return lastRefillDate;
    }

    public void setLastRefillDate(String lastRefillDate) {
        this.lastRefillDate = lastRefillDate;
    }

    public String getDateOfInitiation() {
        return dateOfinitiation;
    }

    public void setDateOfinitiation(String dateOfinitiation) {
        this.dateOfinitiation = dateOfinitiation;
    }

    public String getArvRegimen() {
        return arvRegimen;
    }

    public void setArvRegimen(String arvRegimen) {
        this.arvRegimen = arvRegimen;
    }

    public Double getLastCD4Count() {
        return lastCD4Count;
    }

    public void setLastCD4Count(Double lastCD4Count) {
        this.lastCD4Count = lastCD4Count;
    }

    public String getTbStatus() {
        return tbStatus;
    }

    public void setTbStatus(String tbStatus) {
        this.tbStatus = tbStatus;
    }

    public String getArvRegimenDose() {
        return arvRegimenDose;
    }

    public void setArvRegimenDose(String arvRegimenDose) {
        this.arvRegimenDose = arvRegimenDose;
    }

    public String getWhoClinicalStage() {
        return whoClinicalStage;
    }

    public void setWhoClinicalStage(String whoClinicalStage) {
        this.whoClinicalStage = whoClinicalStage;
    }

    public String getDateVLResultsReceived() {
        return dateVLResultsReceived;
    }

    public void setDateVLResultsReceived(String dateVLResultsReceived) {
        this.dateVLResultsReceived = dateVLResultsReceived;
    }

    public String getChwName() {
        return chwName;
    }

    public void setChwName(String chwName) {
        this.chwName = chwName;
    }

    public String getChwPhone() {
        return chwPhone;
    }

    public void setChwPhone(String chwPhone) {
        this.chwPhone = chwPhone;
    }

    public String getChwAddress() {
        return chwAddress;
    }

    public void setChwAddress(String chwAddress) {
        this.chwAddress = chwAddress;
    }

    public String getVlResults() {
        return vlResults;
    }

    public void setVlResults(String vlResults) {
        this.vlResults = vlResults;
    }

    public Double getBmiMuac() {
        return bmiMuac;
    }

    public void setBmiMuac(Double bmiMuac) {
        this.bmiMuac = bmiMuac;
    }
}
