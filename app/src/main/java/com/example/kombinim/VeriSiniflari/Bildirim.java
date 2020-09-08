package com.example.kombinim.VeriSiniflari;

import com.example.kombinim.BaslaActivity;

import java.sql.Timestamp;
import java.util.UUID;

public class Bildirim {

    private String link;
    private String tur;
    private long tarih;
    private String icerik;
    private String id;
    private String kimden;

    public String getKimden() {
        return kimden;
    }

    public void setKimden(String kimden) {
        this.kimden = kimden;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public long getTarih() {
        return tarih;
    }

    public void setTarih(long tarih) {
        this.tarih = tarih;
    }

    public Bildirim() {
    }

    public Bildirim(String link, String tur, String icerik) {
        this.link = link;
        this.tur = tur;
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        this.tarih = timestamp.getTime();
        this.icerik=icerik;
        this.id= UUID.randomUUID().toString();
        this.kimden= BaslaActivity.vb.getUserID();
    }
}
