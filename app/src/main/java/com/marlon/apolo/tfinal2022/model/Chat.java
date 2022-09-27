package com.marlon.apolo.tfinal2022.model;


import com.google.firebase.database.IgnoreExtraProperties;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Chat implements Serializable {
    private String idChat;
    private MensajeNube mensajeNube;
    private ArrayList<Participante> participantes;

    public Chat() {
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public MensajeNube getMensajeNube() {
        return mensajeNube;
    }

    public void setMensajeNube(MensajeNube mensajeNube) {
        this.mensajeNube = mensajeNube;
    }

    public ArrayList<Participante> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(ArrayList<Participante> participantes) {
        this.participantes = participantes;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "idChat='" + idChat + '\'' +
                ", mensajeNube=" + mensajeNube +
                ", participantes=" + participantes +
                '}';
    }

    //    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("idChat", idChat);
//        result.put("lastMessage", idLastMessage);
//        result.put("timestamp", timestamp);
//        result.put("messagesIDs", messagesIDs);
//        result.put("participantsIDs", participantsIDs);
//
//
//        return result;
//    }
}

