package com.marlon.apolo.tfinal2022.ui.oficioArchi.model;

import com.marlon.apolo.tfinal2022.model.Habilidad;

import java.io.Serializable;
import java.util.ArrayList;

public class OficioArchiModel implements Serializable {
    private String idOficio;
    private String nombre;
    private String uriPhoto;
    private boolean estadoRegistro;
    private ArrayList<Habilidad> habilidadArrayList;


    public OficioArchiModel() {
    }

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

    public boolean isEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(boolean estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }

    public ArrayList<Habilidad> getHabilidadArrayList() {
        return habilidadArrayList;
    }

    public void setHabilidadArrayList(ArrayList<Habilidad> habilidadArrayList) {
        this.habilidadArrayList = habilidadArrayList;
    }

    @Override
    public String toString() {
        return "OficioArchiModel{" +
                "idOficio='" + idOficio + '\'' +
                ", nombre='" + nombre + '\'' +
                ", uriPhoto='" + uriPhoto + '\'' +
                '}';
    }
}