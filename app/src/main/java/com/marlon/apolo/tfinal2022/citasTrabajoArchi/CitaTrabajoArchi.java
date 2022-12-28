package com.marlon.apolo.tfinal2022.citasTrabajoArchi;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class CitaTrabajoArchi implements Serializable {
    private String id;
    private String observaciones;
    private Date fechaCita;

    public CitaTrabajoArchi() {

    }

    public CitaTrabajoArchi(String varObservaciones) {
        this.observaciones = varObservaciones;
    }

    public CitaTrabajoArchi(String id, String varObsv) {
        this.id = id;
        this.observaciones = varObsv;
    }

    public Date getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(Date fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CitaTrabajoArchi{" +
                "id='" + id + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", fechaCita=" + fechaCita +
                '}';
    }
}
