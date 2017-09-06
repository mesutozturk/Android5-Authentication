package com.wissen.mesut.j6_5authentication.model;

import java.util.UUID;

/**
 * Created by Mesut on 6.09.2017.
 */

public class Yapilacak {
    private String icerik, ekleyen, eklenmeZamani, yapilmaZamani,id;

    public Yapilacak() {
        this.id= UUID.randomUUID().toString();
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

    public String getEkleyen() {
        return ekleyen;
    }

    public void setEkleyen(String ekleyen) {
        this.ekleyen = ekleyen;
    }

    public String getEklenmeZamani() {
        return eklenmeZamani;
    }

    public void setEklenmeZamani(String eklenmeZamani) {
        this.eklenmeZamani = eklenmeZamani;
    }

    public String getYapilmaZamani() {
        return yapilmaZamani;
    }

    public void setYapilmaZamani(String yapilmaZamani) {
        this.yapilmaZamani = yapilmaZamani;
    }

    @Override
    public String toString() {
        return icerik;
    }
}
