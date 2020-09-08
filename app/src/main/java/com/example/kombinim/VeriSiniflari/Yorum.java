package com.example.kombinim.VeriSiniflari;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class Yorum {

    private String yorum_icerigi;
    private String kimden;
    private String yorum_tarihi;
    private String id;

    public String getId() {
        return id;
    }

    public String getYorum_icerigi() {
        return yorum_icerigi;
    }

    public void setYorum_icerigi(String yorum_icerigi) {
        this.yorum_icerigi = yorum_icerigi;
    }

    public String getKimden() {
        return kimden;
    }

    public void setKimden(String kimden) {
        this.kimden = kimden;
    }

    public String getYorum_tarihi() {
        return yorum_tarihi;
    }

    public void setYorum_tarihi(String yorum_tarihi) {
        this.yorum_tarihi = yorum_tarihi;
    }

    public Yorum(String yorum_icerigi, String kimden) {
        this.yorum_icerigi = yorum_icerigi;
        this.kimden = kimden;
        this.yorum_tarihi = DateFormat.getDateTimeInstance().format(new Date());
        this.id= UUID.randomUUID().toString(); //firebasein kendi ürettiği keyi bulmak için uniq id üretme
    }

    public Yorum() {
    }
}
