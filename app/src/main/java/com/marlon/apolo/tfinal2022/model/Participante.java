package com.marlon.apolo.tfinal2022.model;

import java.io.Serializable;


public class Participante implements Serializable {
    private String idParticipante;
    private String uriFotoParticipante;
    private String nombreParticipante;

    public Participante() {
    }

    public String getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(String idParticipante) {
        this.idParticipante = idParticipante;
    }

    public String getUriFotoParticipante() {
        return uriFotoParticipante;
    }

    public void setUriFotoParticipante(String uriFotoParticipante) {
        this.uriFotoParticipante = uriFotoParticipante;
    }

    public String getNombreParticipante() {
        return nombreParticipante;
    }

    public void setNombreParticipante(String nombreParticipante) {
        this.nombreParticipante = nombreParticipante;
    }

    @Override
    public String toString() {
        return "Participante{" +
                "idParticipante='" + idParticipante + '\'' +
                ", uriFotoParticipante='" + uriFotoParticipante + '\'' +
                ", nombreParticipante='" + nombreParticipante + '\'' +
                '}';
    }


}
