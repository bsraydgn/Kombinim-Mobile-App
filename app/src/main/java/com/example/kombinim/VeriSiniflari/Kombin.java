package com.example.kombinim.VeriSiniflari;

import com.example.kombinim.BaslaActivity;

import java.sql.Timestamp;
import java.util.UUID;

public class Kombin {
    private String aciklama, resim, id, tarz, kimden;
    private long tarih;
    private Magaza magaza;

    public Magaza getMagaza() {
        return magaza;
    }

    public void setMagaza(Magaza magaza) {
        this.magaza = magaza;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTarz() {
        return tarz;
    }

    public void setTarz(String tarz) {
        this.tarz = tarz;
    }

    public String getKimden() {
        return kimden;
    }

    public void setKimden(String kimden) {
        this.kimden = kimden;
    }

    public long getTarih() {
        return tarih;
    }

    public void setTarih(long tarih) {
        this.tarih = tarih;
    }

    public Kombin() {
        this.id = UUID.randomUUID().toString();
        this.kimden = BaslaActivity.vb.getUserID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.tarih = timestamp.getTime();
    }

    public Kombin(String aciklama, String resim, String tarz, Magaza magaza) {
        this.aciklama = aciklama;
        this.resim = resim;
        this.id = UUID.randomUUID().toString();
        this.tarz = tarz;
        this.kimden = BaslaActivity.vb.getUserID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.tarih = timestamp.getTime();
        this.magaza = magaza;
    }
}
