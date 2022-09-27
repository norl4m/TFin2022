package com.marlon.apolo.tfinal2022.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class LlamadaVoz implements Serializable {
    private String id;
    private String accessToken;
    private int uidCaller;
    private int uidDestiny;
    private boolean callerStatus;
    private boolean destinyStatus;
    private String callerToken;
    private String destinyToken;
    Participante participanteCaller;
    Participante participanteDestiny;
    private boolean channelConnectedStatus;
    private boolean rejectCallStatus;
    private boolean finishCall;

    public LlamadaVoz() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getUidCaller() {
        return uidCaller;
    }

    public void setUidCaller(int uidCaller) {
        this.uidCaller = uidCaller;
    }

    public int getUidDestiny() {
        return uidDestiny;
    }

    public void setUidDestiny(int uidDestiny) {
        this.uidDestiny = uidDestiny;
    }

    public boolean isCallerStatus() {
        return callerStatus;
    }

    public void setCallerStatus(boolean callerStatus) {
        this.callerStatus = callerStatus;
    }

    public boolean isDestinyStatus() {
        return destinyStatus;
    }

    public void setDestinyStatus(boolean destinyStatus) {
        this.destinyStatus = destinyStatus;
    }

    public String getCallerToken() {
        return callerToken;
    }

    public void setCallerToken(String callerToken) {
        this.callerToken = callerToken;
    }

    public String getDestinyToken() {
        return destinyToken;
    }

    public void setDestinyToken(String destinyToken) {
        this.destinyToken = destinyToken;
    }

    public Participante getParticipanteCaller() {
        return participanteCaller;
    }

    public void setParticipanteCaller(Participante participanteCaller) {
        this.participanteCaller = participanteCaller;
    }

    public Participante getParticipanteDestiny() {
        return participanteDestiny;
    }

    public void setParticipanteDestiny(Participante participanteDestiny) {
        this.participanteDestiny = participanteDestiny;
    }

    public boolean isChannelConnectedStatus() {
        return channelConnectedStatus;
    }

    public void setChannelConnectedStatus(boolean channelConnectedStatus) {
        this.channelConnectedStatus = channelConnectedStatus;
    }

    public boolean isRejectCallStatus() {
        return rejectCallStatus;
    }

    public void setRejectCallStatus(boolean rejectCallStatus) {
        this.rejectCallStatus = rejectCallStatus;
    }

    @Override
    public String toString() {
        return "LlamadaVoz{" +
                "id='" + id + '\'' +
                ", uidCaller=" + uidCaller +
                ", uidDestiny=" + uidDestiny +
                ", callerStatus=" + callerStatus +
                ", destinyStatus=" + destinyStatus +
                ", callerToken='" + callerToken + '\'' +
                ", destinyToken='" + destinyToken + '\'' +
                ", participanteCaller=" + participanteCaller +
                ", participanteDestiny=" + participanteDestiny +
                ", channelConnectedStatus=" + channelConnectedStatus +
                ", rejectCallStatus=" + rejectCallStatus +
                '}';
    }

    public boolean isFinishCall() {
        return finishCall;
    }

    public void setFinishCall(boolean finishCall) {
        this.finishCall = finishCall;
    }
}
