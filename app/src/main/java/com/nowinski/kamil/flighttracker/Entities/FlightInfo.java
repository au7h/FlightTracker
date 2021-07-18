package com.nowinski.kamil.flighttracker.Entities;

public class FlightInfo {
    private String id;
    private String icao24bitAddr;
    private Double actualLatitude;
    private Double actualLongitude;
    private Integer trackDirectionAircraft;
    private Integer altitude;
    private Integer speed;
    private Integer someValue1;
    private String someValue2;
    private String airCraftType;
    private String registrationNumber;
    private Integer someValue3;
    private String countryFrom;
    private String countryTo;
    private String trackFlight;
    private Integer someValue4;
    private Integer someValue5;
    private String airLine;
    private Integer someValue6;
    private String airLineShortName;
    private long angle1;
    private long angle2;

    public FlightInfo(String id, String icao24bitAddr, Double actualLatitude, Double actualLongitude, Integer trackDirectionAircraft, Integer altitude, Integer speed, Integer someValue1, String someValue2, String airCraftType, String registrationNumber, Integer someValue3, String countryFrom, String countryTo, String trackFlight, Integer someValue4, Integer someValue5, String airLine, Integer someValue6, String airLineShortName) {
        this.id = id;
        this.icao24bitAddr = icao24bitAddr;
        this.actualLatitude = actualLatitude;
        this.actualLongitude = actualLongitude;
        this.trackDirectionAircraft = trackDirectionAircraft;
        this.altitude = altitude;
        this.speed = speed;
        this.someValue1 = someValue1;
        this.someValue2 = someValue2;
        this.airCraftType = airCraftType;
        this.registrationNumber = registrationNumber;
        this.someValue3 = someValue3;
        this.countryFrom = countryFrom;
        this.countryTo = countryTo;
        this.trackFlight = trackFlight;
        this.someValue4 = someValue4;
        this.someValue5 = someValue5;
        this.airLine = airLine;
        this.someValue6 = someValue6;
        this.airLineShortName = airLineShortName;
    }

    public String getIcao24bitAddr() {
        return icao24bitAddr;
    }

    public void setIcao24bitAddr(String icao24bitAddr) {
        this.icao24bitAddr = icao24bitAddr;
    }

    public Double getActualLatitude() {
        return actualLatitude;
    }

    public void setActualLatitude(Double actualLatitude) {
        this.actualLatitude = actualLatitude;
    }

    public Double getActualLongitude() {
        return actualLongitude;
    }

    public void setActualLongitude(Double actualLongitude) {
        this.actualLongitude = actualLongitude;
    }

    public Integer getTrackDirectionAircraft() {
        return trackDirectionAircraft;
    }

    public void setTrackDirectionAircraft(Integer trackDirectionAircraft) {
        this.trackDirectionAircraft = trackDirectionAircraft;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getSomeValue1() {
        return someValue1;
    }

    public void setSomeValue1(Integer someValue1) {
        this.someValue1 = someValue1;
    }

    public String getSomeValue2() {
        return someValue2;
    }

    public void setSomeValue2(String someValue2) {
        this.someValue2 = someValue2;
    }

    public String getAirCraftType() {
        return airCraftType;
    }

    public void setAirCraftType(String airCraftType) {
        this.airCraftType = airCraftType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Integer getSomeValue3() {
        return someValue3;
    }

    public void setSomeValue3(Integer someValue3) {
        this.someValue3 = someValue3;
    }

    public String getCountryFrom() {
        return countryFrom;
    }

    public void setCountryFrom(String countryFrom) {
        this.countryFrom = countryFrom;
    }

    public String getCountryTo() {
        return countryTo;
    }

    public void setCountryTo(String countryTo) {
        this.countryTo = countryTo;
    }

    public String getTrackFlight() {
        return trackFlight;
    }

    public void setTrackFlight(String trackFlight) {
        this.trackFlight = trackFlight;
    }

    public Integer getSomeValue4() {
        return someValue4;
    }

    public void setSomeValue4(Integer someValue4) {
        this.someValue4 = someValue4;
    }

    public Integer getSomeValue5() {
        return someValue5;
    }

    public void setSomeValue5(Integer someValue5) {
        this.someValue5 = someValue5;
    }

    public String getAirLine() {
        return airLine;
    }

    public void setAirLine(String airLine) {
        this.airLine = airLine;
    }

    public Integer getSomeValue6() {
        return someValue6;
    }

    public void setSomeValue6(Integer someValue6) {
        this.someValue6 = someValue6;
    }

    public String getAirLineShortName() {
        return airLineShortName;
    }

    public void setAirLineShortName(String airLineShortName) {
        this.airLineShortName = airLineShortName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAngle1() {
        return angle1;
    }

    public void setAngle1(long angle1) {
        this.angle1 = angle1;
    }

    public long getAngle2() {
        return angle2;
    }

    public void setAngle2(long angle2) {
        this.angle2 = angle2;
    }

    @Override
    public String toString() {
        return "FlightInfo{" +
                "id='" + id + '\'' +
                ", icao24bitAddr='" + icao24bitAddr + '\'' +
                ", actualLatitude=" + actualLatitude +
                ", actualLongitude=" + actualLongitude +
                ", trackDirectionAircraft=" + trackDirectionAircraft +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", someValue1=" + someValue1 +
                ", someValue2='" + someValue2 + '\'' +
                ", airCraftType='" + airCraftType + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", someValue3=" + someValue3 +
                ", countryFrom='" + countryFrom + '\'' +
                ", countryTo='" + countryTo + '\'' +
                ", trackFlight='" + trackFlight + '\'' +
                ", someValue4=" + someValue4 +
                ", someValue5=" + someValue5 +
                ", airLine='" + airLine + '\'' +
                ", someValue6=" + someValue6 +
                ", airLineShortName='" + airLineShortName + '\'' +
                '}';
    }
}
