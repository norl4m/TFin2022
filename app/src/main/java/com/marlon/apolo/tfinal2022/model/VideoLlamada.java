package com.marlon.apolo.tfinal2022.model;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoChatViewActivity;

import java.io.Serializable;

import io.agora.rtc2.RtcEngine;


@IgnoreExtraProperties
public class VideoLlamada implements Serializable {
    private String id;
    private String caller;
    private String destiny;
    private int uidCaller;
    private int uidDestiny;
    private boolean callerStatus;
    private boolean destinyStatus;
    private boolean channelConnectedStatus;
    private String callerToken;
    private String destinyToken;
    private boolean llamadaRechazada;
    private String nameCaller;
    private String nameDestiny;
    private boolean finishCall;

    private Participante participanteCaller;
    private Participante participanteDestiny;

    public VideoLlamada() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
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

    public boolean isChannelConnectedStatus() {
        return channelConnectedStatus;
    }

    public void setChannelConnectedStatus(boolean channelConnectedStatus) {
        this.channelConnectedStatus = channelConnectedStatus;
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

    public String getNameCaller() {
        return nameCaller;
    }

    public void setNameCaller(String nameCaller) {
        this.nameCaller = nameCaller;
    }

    public String getNameDestiny() {
        return nameDestiny;
    }

    public void setNameDestiny(String nameDestiny) {
        this.nameDestiny = nameDestiny;
    }

    public void llamar(RtcEngine rtcEngine, String token, String channelNameShare, String extraData, int uidUser, VideoChatViewActivity videoChatViewActivity) {
        try {
            VideoLlamada videoLlamadaLocal = this;
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("videoLlamadas")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int exit = 0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                try {
                                    VideoLlamada videoLlamada = data.getValue(VideoLlamada.class);
                                    if (videoLlamada.getDestiny().equals(videoLlamadaLocal.getDestiny())) {
                                        if (videoLlamada.isChannelConnectedStatus()) {
                                            Log.d("TAG", "Usuario ocupado!");
                                            exit = 1;
                                            break;
                                        }
                                        if (exit == 1) {
                                            break;
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }

                            if (exit == 1) {
                                Log.d("TAG", "El usuario no se encuentra disponible!");
                                Log.d("TAG", "Por favor inténtelo más tarde!");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(videoChatViewActivity,
                                                "El usuario no se encuentra disponible!" +
                                                        "\nPor favor inténtelo más tarde!", Toast.LENGTH_LONG).show();
                                        videoChatViewActivity.finish();
                                    }
                                }, 1000);
//                                Log.d("TAG","Usuario ocupado!");
                            } else {
                                try {
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("videoLlamadas")
                                            .child(videoLlamadaLocal.id)
                                            .setValue(videoLlamadaLocal)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        rtcEngine.joinChannel(token, channelNameShare, extraData, uidUser);
                                                        //Toast.makeText(getApplicationContext(), "LLamando a " + nameTo, Toast.LENGTH_SHORT).show();
                                                    } else {

                                                    }
                                                }
                                            });
                                } catch (Exception e) {

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        } catch (Exception e) {
        }

    }


    public void contestar(RtcEngine rtcEngine, String token, String channelNameShare, String extraData, int uidUser) {

        FirebaseDatabase.getInstance()
                .getReference()
                .child("videoLlamadas")
                .child(this.id)
                .setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            rtcEngine.joinChannel(token, channelNameShare, extraData, uidUser);
//                            Toast.makeText(getApplicationContext(), "LLamando a " + nameTo, Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                });
    }

    public void colgar(String chatID) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("videoLlamadas")
                .child(chatID)
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(), "LLamando a " + nameTo, Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                });
    }

    public void rechazar(String chatID, VideoChatViewActivity videoChatViewActivity) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("videoLlamadas")
                .child(chatID)
                .child("llamadaRechazada")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(videoChatViewActivity, "LLamada rechazada", Toast.LENGTH_SHORT).show();

                        } else {

                        }
                    }
                });
    }

    public boolean isLlamadaRechazada() {
        return llamadaRechazada;
    }

    public void setLlamadaRechazada(boolean llamadaRechazada) {
        this.llamadaRechazada = llamadaRechazada;
    }

    @Override
    public String toString() {
        return "VideoLlamada{" +
                "id='" + id + '\'' +
                ", caller='" + caller + '\'' +
                ", destiny='" + destiny + '\'' +
                ", uidCaller=" + uidCaller +
                ", uidDestiny=" + uidDestiny +
                ", callerStatus=" + callerStatus +
                ", destinyStatus=" + destinyStatus +
                ", channelConnectedStatus=" + channelConnectedStatus +
                ", callerToken='" + callerToken + '\'' +
                ", destinyToken='" + destinyToken + '\'' +
                ", llamadaRechazada=" + llamadaRechazada +
                ", nameCaller='" + nameCaller + '\'' +
                ", nameDestiny='" + nameDestiny + '\'' +
                '}';
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

    public boolean isFinishCall() {
        return finishCall;
    }

    public void setFinishCall(boolean finishCall) {
        this.finishCall = finishCall;
    }
}
