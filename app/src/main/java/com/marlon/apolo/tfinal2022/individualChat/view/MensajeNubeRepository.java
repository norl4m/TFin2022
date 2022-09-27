package com.marlon.apolo.tfinal2022.individualChat.view;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MensajeNubeRepository {
    private static final String TAG = MensajeNubeRepository.class.getSimpleName();
    private MutableLiveData<ArrayList<MensajeNube>> allMensajes;
    private ChildEventListener childEventListenerMensajes;

    public MensajeNubeRepository() {
    }

    public MutableLiveData<ArrayList<MensajeNube>> getAllMensajes(String idChat) {
        if (allMensajes == null) {
            allMensajes = new MutableLiveData<>();
            loadAllMessages(idChat);
        }
        return allMensajes;
    }

    private void loadAllMessages(String idChat) {


        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
        childEventListenerMensajes = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded");
                try {
                    MensajeNube mensajeNube = snapshot.getValue(MensajeNube.class);
                    Log.d(TAG, mensajeNube.toString());
                    mensajeNubeArrayList.add(mensajeNube);
                    allMensajes.setValue(mensajeNubeArrayList);
                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MensajeNube mensajeNube = snapshot.getValue(MensajeNube.class);
                int index = 0;
                for (MensajeNube m : mensajeNubeArrayList) {
                    if (m.getIdMensaje().equals(mensajeNube.getIdMensaje())) {
                        if (mensajeNube.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            mensajeNubeArrayList.set(index, mensajeNube);
                            allMensajes.setValue(mensajeNubeArrayList);
                            break;
                        }
                    }
                    index++;
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(idChat)
                .addChildEventListener(childEventListenerMensajes);


    }


    public void removeChilLestenerMensajes(String idChat) {
        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(idChat)
                .removeEventListener(childEventListenerMensajes);
    }

    public void updateReadState(MensajeNube m) {
        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(m.getIdChat())
                .child(m.getIdMensaje())
                .child("estadoLectura")
                .setValue(true);
    }
}
