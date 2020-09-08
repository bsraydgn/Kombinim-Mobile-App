package com.example.kombinim.VeriSiniflari;

public class Magaza {

    private String ismi, link;

    public String getIsmi() {
        return ismi;
    }

    public void setIsmi(String ismi) {
        this.ismi = ismi;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Magaza() {
    }

    public Magaza(String ismi, String link) {
        this.ismi = ismi;
        this.link = link;
    }
}
