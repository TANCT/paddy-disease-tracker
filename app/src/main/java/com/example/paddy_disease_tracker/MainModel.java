package com.example.paddy_disease_tracker;

public class MainModel {
    Integer diseaseLogo;
    String diseaseName;


    public MainModel(Integer diseaseLogo, String diseaseName) {
        this.diseaseLogo = diseaseLogo;
        this.diseaseName = diseaseName;
    }

    public Integer getDiseaseLogo() {
        return diseaseLogo;
    }

    public String getDiseaseName() {
        return diseaseName;
    }
}
