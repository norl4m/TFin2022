package com.marlon.apolo.tfinal2022.model;

import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;

import java.util.ArrayList;

public class NotificacionCustom {
    ArrayList<MensajeNube> mensajeNubes;
    private int idNotification;
    private String idFrom;

    public NotificacionCustom() {
    }

    public ArrayList<MensajeNube> getMensajeNubes() {
        return mensajeNubes;
    }

    public void setMensajeNubes(ArrayList<MensajeNube> mensajeNubes) {
        this.mensajeNubes = mensajeNubes;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }


    public String getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(String idFrom) {
        this.idFrom = idFrom;
    }

    @Override
    public String toString() {
        return "NotificacionCustom{" +
                "mensajeNubes=" + mensajeNubes +
                ", idNotification=" + idNotification +
                ", idFrom='" + idFrom + '\'' +
                '}';
    }
}
