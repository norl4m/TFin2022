package com.marlon.apolo.tfinal2022.model;


import java.io.Serializable;

public class OficioPoc extends Oficio {

    private boolean estadoRegistro;

    public OficioPoc() {
        estadoRegistro = false;
    }


    public boolean isEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(boolean estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }


    @Override
    public String toString() {
        return "OficioPoc{" +
                "estadoRegistro=" + estadoRegistro +
                "} " + super.toString();
    }
}
