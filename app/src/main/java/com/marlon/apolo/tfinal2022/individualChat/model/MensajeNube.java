package com.marlon.apolo.tfinal2022.individualChat.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class MensajeNube implements Serializable {
    private String idMensaje;
    private String idChat;
    private String from;
    private String to;
    private int type;/*0 mensaje -1 imagen- 2 audio -4 Location*/
    private String contenido;
    private String timeStamp;
    private boolean estadoLectura; /*leído - no  leído*/
    private double latitude;
    private double longitude;

    private String audioDuration;


    public MensajeNube() {
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isEstadoLectura() {
        return estadoLectura;
    }

    public void setEstadoLectura(boolean estadoLectura) {
        this.estadoLectura = estadoLectura;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    @Override
    public String toString() {
        return "MensajeNube{" +
                "idMensaje='" + idMensaje + '\'' +
                ", idChat='" + idChat + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", type=" + type +
                ", contenido='" + contenido + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", estadoLectura=" + estadoLectura +
                '}';
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



}

