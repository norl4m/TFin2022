package com.marlon.apolo.tfinal2022.ui.trabajadores;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorRepository {

    private static final String TAG = TrabajadorRepository.class.getSimpleName();
    private MutableLiveData<List<Trabajador>> allTrabajadores;
    private MutableLiveData<Trabajador> oneTrabajador;
    private MutableLiveData<Trabajador> auxTrabajador;
    private ChildEventListener trabajadorChildEventListener;

    public TrabajadorRepository() {

    }

    public MutableLiveData<List<Trabajador>> getAllTrabajadores() {
        if (allTrabajadores == null) {
            allTrabajadores = new MutableLiveData<>();
            loadAllTrabajadores();
        }
        return allTrabajadores;
    }

    private void loadAllTrabajadores() {
        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();
        trabajadorChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    trabajadorArrayList.add(trabajador);
                    allTrabajadores.setValue(trabajadorArrayList);
                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (trabajadorArrayList.size() > 0) {
                    try {
                        Trabajador trabajadorChanged = snapshot.getValue(Trabajador.class);
                        int index = 0;
                        for (Trabajador trabajadorDB : trabajadorArrayList) {
                            try {
                                if (trabajadorDB.getIdUsuario().equals(trabajadorChanged.getIdUsuario())) {
                                    trabajadorArrayList.set(index, trabajadorChanged);
                                    break;
                                }
                            } catch (Exception e) {

                            }
                            index++;
                        }
                        allTrabajadores.setValue(trabajadorArrayList);
                    } catch (Exception e) {
                        Log.d(TAG, "onChildChanged ERROR");
                        Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (trabajadorArrayList.size() > 0) {
                    try {
                        Trabajador trabajadorRemoved = snapshot.getValue(Trabajador.class);
                        int index = 0;
                        for (Trabajador trabajadorDB : trabajadorArrayList) {
                            try {
                                if (trabajadorDB.getIdUsuario().equals(trabajadorRemoved.getIdUsuario())) {
                                    trabajadorArrayList.remove(index);
                                    break;
                                }
                            } catch (Exception e) {

                            }
                            index++;
                        }
                        allTrabajadores.setValue(trabajadorArrayList);
                    } catch (Exception e) {
                        Log.d(TAG, "onChildChanged ERROR");
                        Log.d(TAG, e.toString());
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
                .child("trabajadores")
                .addChildEventListener(trabajadorChildEventListener);
    }

    public void removeChildListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("trabajadores")
                .removeEventListener(trabajadorChildEventListener);
    }

    public MutableLiveData<Trabajador> getOneTrabajador(String idTrabajador) {
        if (oneTrabajador == null) {
            oneTrabajador = new MutableLiveData<>();
            loadOneTrabajador(idTrabajador);
        }
        return oneTrabajador;
    }

    public MutableLiveData<Trabajador> getAuxTrabajador(String idTrabajador) {
        //if (auxTrabajador==null){
        auxTrabajador = new MutableLiveData<>();
        loadOneAuxTrabajador(idTrabajador);
        //6}
        return auxTrabajador;
    }

    private void loadOneTrabajador(String idTrabajador) {
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(idTrabajador)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            oneTrabajador.setValue(trabajador);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOneAuxTrabajador(String idTrabajador) {
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(idTrabajador)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            auxTrabajador.setValue(trabajador);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
