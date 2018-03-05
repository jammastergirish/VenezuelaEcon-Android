package com.girish.venecon.api.models;


public class OilData {
    private String date;
    private double wti, opec, brent, ven;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWti() {
        return wti;
    }

    public void setWti(double wti) {
        this.wti = wti;
    }

    public double getOpec() {
        return opec;
    }

    public void setOpec(double opec) {
        this.opec = opec;
    }

    public double getBrent() {
        return brent;
    }

    public void setBrent(double brent) {
        this.brent = brent;
    }

    public double getVen() {
        return ven;
    }

    public void setVen(double ven) {
        this.ven = ven;
    }
}
