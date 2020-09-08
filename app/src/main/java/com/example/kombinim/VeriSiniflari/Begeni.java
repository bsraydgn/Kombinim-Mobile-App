package com.example.kombinim.VeriSiniflari;

public class Begeni {
    private String begenen,tarih;

    public String getBegenen() {
        return begenen;
    }

    public void setBegenen(String begenen) {
        this.begenen = begenen;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public Begeni(String begenen, String tarih) {
        this.begenen = begenen;
        this.tarih = tarih;
    }

}
