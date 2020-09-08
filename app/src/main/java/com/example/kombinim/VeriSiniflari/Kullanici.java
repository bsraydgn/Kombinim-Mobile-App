package com.example.kombinim.VeriSiniflari;

public class Kullanici {

    private String email;
    private String kullaniciadi;
    private String sifre;
    private String durum="Hi there ! I' m using Kombinim";
    private String profilresmi="default";
    private String userID;
    private Boolean profilgorunurlugu=true;
    private String hakkimda="";

    public Boolean getProfilgorunurlugu() {
        return profilgorunurlugu;
    }

    public void setProfilgorunurlugu(Boolean profilgorunurlugu) {
        this.profilgorunurlugu = profilgorunurlugu;
    }

    public String getHakkimda() {
        return hakkimda;
    }

    public void setHakkimda(String hakkimda) {
        this.hakkimda = hakkimda;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getProfilresmi() {
        return profilresmi;
    }

    public void setProfilresmi(String profilresmi) {
        this.profilresmi = profilresmi;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Kullanici() {
    }

    public Kullanici(String email, String kullaniciadi, String sifre) {
        this.email = email;
        this.kullaniciadi = kullaniciadi;
        this.sifre = sifre;
    }

    public Kullanici(String email, String kullaniciadi, String sifre, String durum) {
        this.email = email;
        this.kullaniciadi = kullaniciadi;
        this.sifre = sifre;
        this.durum = durum;
    }

    public Kullanici(String email, String kullaniciadi, String sifre, String durum, String profilresmi) {
        this.email = email;
        this.kullaniciadi = kullaniciadi;
        this.sifre = sifre;
        this.durum = durum;
        this.profilresmi = profilresmi;
    }
}
