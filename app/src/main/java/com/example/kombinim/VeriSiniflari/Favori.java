package com.example.kombinim.VeriSiniflari;

public class Favori {
    private String favorileyen,tarih;

    public String getFavorileyen() {
        return favorileyen;
    }

    public void setFavorileyen(String favorileyen) {
        this.favorileyen = favorileyen;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public Favori(String favorileyen, String tarih) {
        this.favorileyen = favorileyen;
        this.tarih = tarih;
    }

    public Favori() {
    }
}
