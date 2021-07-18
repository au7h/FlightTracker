package com.nowinski.kamil.flighttracker.Entities;

import java.util.List;

public class Radar {
    private int allAirPlanes;
    private int version;
    private List<FlightInfo> flightInfoList;

    public Radar(int allAirPlanes, int version, List<FlightInfo> flightInfoList) {
        this.allAirPlanes = allAirPlanes;
        this.version = version;
        this.flightInfoList = flightInfoList;
    }

    public int getAllAirPlanes() {
        return allAirPlanes;
    }

    public void setAllAirPlanes(int allAirPlanes) {
        this.allAirPlanes = allAirPlanes;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<FlightInfo> getFlightInfoList() {
        return flightInfoList;
    }

    public void setFlightInfoList(List<FlightInfo> flightInfoList) {
        this.flightInfoList = flightInfoList;
    }

    @Override
    public String toString() {
        return "Radar{" +
                "allAirPlanes=" + allAirPlanes +
                ", version=" + version +
                ", flightInfoList=" + flightInfoList +
                '}';
    }
}
