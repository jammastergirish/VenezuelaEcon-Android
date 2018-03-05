package com.girish.venecon.api.models;

import com.google.gson.annotations.SerializedName;

public class ExchangeData {
    // Note for Girish: I could've named the variable whatever I wanted, like myDate
    // And then I would need @SerializedName("date"), and GSON would know how to deserialize it
    // If the names are the same, like they are here, I can omit  @SerializedName("date"), it'll use variable name by default
    @SerializedName("date")
    private String date;
    // Here I named the variables exactly what their json names are, but these names are bad cause they don't represent anything
    // I have no idea what sicad1 or m2_res is, or any of them. Rather when you have time, you can rename variables to something
    // more meaningful, and put  @SerializedName("sicad2") above them like in my example above
    private double official, supp, sitme, sicad1, sicad2, simadi, dicom, m2_res, bm;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOfficial() {
        return official;
    }

    public void setOfficial(double official) {
        this.official = official;
    }

    public double getSupp() {
        return supp;
    }

    public void setSupp(double supp) {
        this.supp = supp;
    }

    public double getSitme() {
        return sitme;
    }

    public void setSitme(double sitme) {
        this.sitme = sitme;
    }

    public double getSicad1() {
        return sicad1;
    }

    public void setSicad1(double sicad1) {
        this.sicad1 = sicad1;
    }

    public double getSicad2() {
        return sicad2;
    }

    public void setSicad2(double sicad2) {
        this.sicad2 = sicad2;
    }

    public double getSimadi() {
        return simadi;
    }

    public void setSimadi(double simadi) {
        this.simadi = simadi;
    }

    public double getDicom() {
        return dicom;
    }

    public void setDicom(double dicom) {
        this.dicom = dicom;
    }

    public double getM2_res() {
        return m2_res;
    }

    public void setM2_res(double m2_res) {
        this.m2_res = m2_res;
    }

    public double getBm() {
        return bm;
    }

    public void setBm(double bm) {
        this.bm = bm;
    }
}
