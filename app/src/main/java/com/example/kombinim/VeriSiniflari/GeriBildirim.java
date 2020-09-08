package com.example.kombinim.VeriSiniflari;

import java.util.UUID;

public class GeriBildirim {

    private String geribildirim, eposta, tur , yardimid;

    public GeriBildirim() {
    }

    public GeriBildirim(String geribildirim, String eposta, String tur) {
        this.geribildirim = geribildirim;
        this.eposta = eposta;
        this.tur = tur;
        this.yardimid = UUID.randomUUID().toString();
    }

    public String getGeribildirim() {
        return geribildirim;
    }

    public void setGeribildirim(String geribildirim) {
        this.geribildirim = geribildirim;
    }

    public String getEposta() {
        return eposta;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public String getYardimid() {
        return yardimid;
    }

    public void setYardimid(String yardimid) {
        this.yardimid = yardimid;
    }
}
