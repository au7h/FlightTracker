package com.nowinski.kamil.flighttracker.Entities;

import java.util.List;

public class Aircraft {
    private String airCraftModel;
    private String airCraftRegistration;
    private List<String> airCraftImages;
    private String airLineName;

    public String getAirCraftModel() {
        return airCraftModel;
    }

    public void setAirCraftModel(String airCraftModel) {
        this.airCraftModel = airCraftModel;
    }

    public String getAirCraftRegistration() {
        return airCraftRegistration;
    }

    public void setAirCraftRegistration(String airCraftRegistration) {
        this.airCraftRegistration = airCraftRegistration;
    }

    public List<String> getAirCraftImages() {
        return airCraftImages;
    }

    public void setAirCraftImages(List<String> airCraftImages) {
        this.airCraftImages = airCraftImages;
    }

    public String getAirLineName() {
        return airLineName;
    }

    public void setAirLineName(String airLineName) {
        this.airLineName = airLineName;
    }
}
