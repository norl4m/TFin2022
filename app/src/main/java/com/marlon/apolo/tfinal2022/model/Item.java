package com.marlon.apolo.tfinal2022.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Item implements Serializable {
    private String detail;
    private float price;

    public Item() {
    }

    public Item(String detail, float price) {
        this.detail = detail;
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "detail='" + detail + '\'' +
                ", price=" + price +
                '}';
    }
}
