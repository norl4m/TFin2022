package com.marlon.apolo.tfinal2022.model;

public class NotificacionCustomLlamada {
    private String idCall;
    private int idNotification;
    private LlamadaVoz llamadaVoz;

    public NotificacionCustomLlamada() {
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

    public LlamadaVoz getLlamadaVoz() {
        return llamadaVoz;
    }

    public void setLlamadaVoz(LlamadaVoz llamadaVoz) {
        this.llamadaVoz = llamadaVoz;
    }
}
