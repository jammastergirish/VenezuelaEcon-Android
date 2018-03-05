package com.girish.venecon.api.models;


public class CrudeProductionData {
    private String date;
    private double direct, secondary;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDirect() {
        return direct;
    }

    public void setDirect(double direct) {
        this.direct = direct;
    }

    public double getSecondary() {
        return secondary;
    }

    public void setSecondary(double secondary) {
        this.secondary = secondary;
    }
}
