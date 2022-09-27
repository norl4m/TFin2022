package com.marlon.apolo.tfinal2022.individualChat.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MessageCloudPoc {
    private String idMensaje;
    private String from;
    private String to;
    private int type;/*0 mensaje -1 imagen- 2 audio-3 video 5MB -4 Location*/
    private String contenido;
    private String timeStamp;
    private boolean estadoLectura; /*leído - no  leído*/

    private double latitude;
    private double longitude;

    private String audioDuration;
    private String mimeType;

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

    /**
     * Este método permite obtener el tipo de mensaje recibido
     * <p>
     * 0 mensaje -1 imagen- 2 audio-3 video 5MB -4 Location
     */
    public int getType() {
        return type;
    }


    /**
     * Este método permite setear el tipo de mensaje enviado
     * <p>
     * 0 mensaje -1 imagen- 2 audio-3 video 5MB -4 Location
     */
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

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "MessageCloudPoc{" +
                "idMensaje='" + idMensaje + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", type=" + type +
                ", contenido='" + contenido + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", estadoLectura=" + estadoLectura +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", audioDuration='" + audioDuration + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("idMensaje", idMensaje);
        result.put("from", from);
        result.put("to", to);
        result.put("type", type);
        result.put("contenido", contenido);
        result.put("timeStamp", timeStamp);
        result.put("estadoLectura", estadoLectura);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("audioDuration", audioDuration);
        result.put("mimeType", mimeType);

        return result;
    }
}
