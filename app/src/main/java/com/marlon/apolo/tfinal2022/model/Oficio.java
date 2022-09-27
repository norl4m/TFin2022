package com.marlon.apolo.tfinal2022.model;


import java.io.Serializable;
import java.util.ArrayList;

public class Oficio implements Serializable {
    private String idOficio;
    private String nombre;
    private boolean estadoRegistro;
    private ArrayList<Habilidad> habilidadArrayList;

    public Oficio() {
        estadoRegistro = false;
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


    public boolean isEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(boolean estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }

    @Override
    public String toString() {
        return "Oficio{" +
                "idOficio='" + idOficio + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estadoRegistro=" + estadoRegistro +
                ", habilidadArrayList=" + habilidadArrayList +
                '}';
    }

    public ArrayList<Habilidad> getHabilidadArrayList() {
        return habilidadArrayList;
    }

    public void setHabilidadArrayList(ArrayList<Habilidad> habilidadArrayList) {
        this.habilidadArrayList = habilidadArrayList;
    }
}
