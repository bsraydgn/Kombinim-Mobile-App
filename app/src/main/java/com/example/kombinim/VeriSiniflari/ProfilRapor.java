package com.example.kombinim.VeriSiniflari;

import com.example.kombinim.BaslaActivity;

public class ProfilRapor {
    private String profilsahibi, raporlayan, sebep;

    public String getProfilsahibi() {
        return profilsahibi;
    }

    public void setProfilsahibi(String profilsahibi) {
        this.profilsahibi = profilsahibi;
    }

    public String getRaporlayan() {
        return raporlayan;
    }

    public void setRaporlayan(String raporlayan) {
        this.raporlayan = raporlayan;
    }

    public String getSebep() {
        return sebep;
    }

    public void setSebep(String sebep) {
        this.sebep = sebep;
    }

    public ProfilRapor(String profilsahibi, String sebep) {
        this.profilsahibi = profilsahibi;
        this.raporlayan = BaslaActivity.vb.getUserID();
        this.sebep = sebep;
    }
}
