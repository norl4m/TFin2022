package com.marlon.apolo.tfinal2022.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Item implements Serializable {
    private String detail;
    private Double price;
    private String priceFormat;

    public Item() {
    }

    public Item(String detail, Double price) {
        this.detail = detail;
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPriceFormat() {
        return priceFormat;
    }


    public void setPriceFormat(String priceFormat) {
        this.priceFormat = priceFormat;
    }

    @Override
    public String toString() {
        return "Item{" +
                "detail='" + detail + '\'' +
                ", price=" + price +
                ", priceFormat='" + priceFormat + '\'' +
                '}';
    }
}
