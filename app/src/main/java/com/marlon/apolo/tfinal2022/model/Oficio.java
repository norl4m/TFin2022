package com.marlon.apolo.tfinal2022.model;


import java.io.Serializable;
import java.util.ArrayList;

public class Oficio implements Serializable {
    private String idOficio;
    private String nombre;
    private String uriPhoto;


    public String getIdOficio() {
        return idOficio;
    }

    public void setIdOficio(String idOficio) {
        this.idOficio = idOficio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    @Override
    public String toString() {
        return "Oficio{" +
                "idOficio='" + idOficio + '\'' +
                ", nombre='" + nombre + '\'' +
                ", uriPhoto='" + uriPhoto + '\'' +
                '}';
    }

}
