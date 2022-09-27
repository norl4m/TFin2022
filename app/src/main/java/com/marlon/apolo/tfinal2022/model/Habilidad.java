package com.marlon.apolo.tfinal2022.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Habilidad implements Serializable {
    private String idHabilidad;
    private String nombreHabilidad;
    private boolean habilidadSeleccionada;

    public Habilidad() {
    }

    public String getIdHabilidad() {
        return idHabilidad;
    }

    public void setIdHabilidad(String idHabilidad) {
        this.idHabilidad = idHabilidad;
    }

    public String getNombreHabilidad() {
        return nombreHabilidad;
    }

    public void setNombreHabilidad(String nombreHabilidad) {
        this.nombreHabilidad = nombreHabilidad;
    }

    public boolean isHabilidadSeleccionada() {
        return habilidadSeleccionada;
    }

    public void setHabilidadSeleccionada(boolean habilidadSeleccionada) {
        this.habilidadSeleccionada = habilidadSeleccionada;
    }

    @Override
    public String toString() {
        return "Habilidad{" +
                "idHabilidad='" + idHabilidad + '\'' +
                ", nombreHabilidad='" + nombreHabilidad + '\'' +
                ", habilidadSeleccionada=" + habilidadSeleccionada +
                '}';
    }
}
