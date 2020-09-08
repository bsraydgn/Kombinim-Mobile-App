package com.example.kombinim.VeriSiniflari;

import com.example.kombinim.BaslaActivity;

public class Rapor {

    private String paylasimsahibi, id, sebep, raporlayan;

    public String getPaylasimsahibi() {
        return paylasimsahibi;
    }

    public void setPaylasimsahibi(String paylasimsahibi) {
        this.paylasimsahibi = paylasimsahibi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSebep() {
        return sebep;
    }

    public void setSebep(String sebep) {
        this.sebep = sebep;
    }

    public String getRaporlayan() {
        return raporlayan;
    }

    public void setRaporlayan(String raporlayan) {
        this.raporlayan = raporlayan;
    }

    public Rapor() {
    }

    public Rapor(String paylasimsahibi, String id, String sebep) {
        this.paylasimsahibi = paylasimsahibi;
        this.id = id;
        this.sebep = sebep;
        this.raporlayan= BaslaActivity.vb.getUserID();
    }
}
