package com.marlon.apolo.tfinal2022.individualChat.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private MutableLiveData<List<Chat>> allChats;
    private ChildEventListener childEventListenerChats;

    public ChatRepository() {
    }

    public MutableLiveData<List<Chat>> getAllChats() {
        if (allChats==null){
            allChats = new MutableLiveData<>();
            loadChats();
        }
        return allChats;
    }

    private void loadChats() {

        ArrayList<Chat> chatArrayList  = new ArrayList<>();
        childEventListenerChats = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Chat chat = snapshot.getValue(Chat.class);
                    for (Participante p: chat.getParticipantes()) {
                        if (p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            chatArrayList.add(chat);
                            break;
                        }

                    }
                    allChats.setValue(chatArrayList);
                }catch (Exception e){

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (chatArrayList.size() > 0) {
                    try {
                        Chat trabajadorChanged = snapshot.getValue(Chat.class);
                        for (Chat trabajadorDB : chatArrayList) {
                            try {
                                if (trabajadorDB.getIdChat().equals(trabajadorChanged.getIdChat())) {
                                    chatArrayList.set(chatArrayList.indexOf(trabajadorDB), trabajadorChanged);
                                    break;
                                }
                            } catch (Exception e) {

                            }

                        }
                        allChats.setValue(chatArrayList);
                        //allTrabajadores.setValue(trabajadorArrayList);
                    } catch (Exception e) {
                        // Log.d(TAG, "onChildChanged ERROR");
                        //Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (chatArrayList.size() > 0) {
                    try {
                        Chat trabajadorRemoved = snapshot.getValue(Chat.class);
                        for (Chat trabajadorDB : chatArrayList) {
                            try {
                                if (trabajadorDB.getIdChat().equals(trabajadorRemoved.getIdChat())) {
                                    chatArrayList.remove(chatArrayList.indexOf(trabajadorDB));
                                    break;
                                }
                            } catch (Exception e) {

                            }

                        }
                        allChats.setValue(chatArrayList);
                        //allTrabajadores.setValue(trabajadorArrayList);
                    } catch (Exception e) {
                        //Log.d(TAG, "onChildChanged ERROR");
                        //Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .addChildEventListener(childEventListenerChats);
    }
}
