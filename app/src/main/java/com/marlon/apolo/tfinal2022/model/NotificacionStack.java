package com.marlon.apolo.tfinal2022.model;

import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;

import java.util.ArrayList;

public class NotificacionStack {
    ArrayList<MessageCloudPoc> mensajeNubes;
    private int idNotification;
    private long numberMessages;

    public NotificacionStack() {
    }

    public ArrayList<MessageCloudPoc> getMensajeNubes() {
        return mensajeNubes;
    }

    public void setMensajeNubes(ArrayList<MessageCloudPoc> mensajeNubes) {
        this.mensajeNubes = mensajeNubes;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }

    public long getNumberMessages() {
        return numberMessages;
    }

    public void setNumberMesssages(long numberMessages) {
        this.numberMessages = numberMessages;
    }

    @Override
    public String toString() {
        return "NotificacionCustom{" +
                "mensajeNubes=" + mensajeNubes +
                ", idNotification=" + idNotification +
                ", numberMessages=" + numberMessages +
                '}';
    }
}
