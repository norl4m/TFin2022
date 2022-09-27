package com.marlon.apolo.tfinal2022.model;

public class NotificacionCustomVideoLlamada {
    private String idCall;
    private int idNotification;
    private LlamadaVideo videoLlamada;

    public NotificacionCustomVideoLlamada() {
    }

    public String getIdCall() {
        return idCall;
    }

    public void setIdCall(String idCall) {
        this.idCall = idCall;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }


    public LlamadaVideo getVideoLlamada() {
        return videoLlamada;
    }

    public void setVideoLlamada(LlamadaVideo videoLlamada) {
        this.videoLlamada = videoLlamada;
    }
}
