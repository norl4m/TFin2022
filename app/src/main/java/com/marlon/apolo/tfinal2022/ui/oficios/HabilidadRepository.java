package com.marlon.apolo.tfinal2022.ui.oficios;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class HabilidadRepository {
    private static final String TAG = HabilidadRepository.class.getSimpleName();
    public MutableLiveData<List<Habilidad>> allHabilidades;
    private ChildEventListener habilidadChildEventListener;

    public HabilidadRepository() {

    }


    public MutableLiveData<List<Habilidad>> getAllHabilidades(String idOficio) {
        if (allHabilidades == null) {
            allHabilidades = new MutableLiveData<>();
            loadAllHabilidades(idOficio);
        }
        return allHabilidades;
    }

    private void loadAllHabilidades(String idOficio) {

        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
        habilidadChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded");
                try {
                    Habilidad habilidad = snapshot.getValue(Habilidad.class);
                    habilidadArrayList.add(habilidad);
                    allHabilidades.setValue(habilidadArrayList);
                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged");

                if (habilidadArrayList.size() > 0) {
                    try {
                        Habilidad oficioChanged = snapshot.getValue(Habilidad.class);
                        int index = 0;
                        for (Habilidad oficioDB : habilidadArrayList) {
                            try {
                                if (oficioDB.getIdHabilidad().equals(oficioChanged.getIdHabilidad())) {
                                    habilidadArrayList.set(index, oficioChanged);
                                }
                            } catch (Exception e) {

                            }
                            index++;

                        }
                        allHabilidades.setValue(habilidadArrayList);
                    } catch (Exception e) {
                        Log.d("TAG", "onChildChanged ERROR");
                        Log.d("TAG", e.toString());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved");

//                if (habilidadArrayList.size() > 0) {
//                    try {
//                        Habilidad trabajadorRemoved = snapshot.getValue(Habilidad.class);
//                        int index = 0;
//                        for (Habilidad trabajadorDB : habilidadArrayList) {
//                            try {
//                                if (trabajadorDB.getIdHabilidad().equals(trabajadorRemoved.getIdHabilidad())) {
//                                    habilidadArrayList.remove(index);
//                                }

//                            } catch (Exception e) {
//
//                            }
//                            index++;
//                        }
//                        allHabilidades.setValue(habilidadArrayList);
//                    } catch (Exception e) {
//                        Log.d("TAG", "onChildChanged ERROR");
//                        Log.d("TAG", e.toString());
//                    }
//                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .child(idOficio)
                .child("habilidadArrayList")
                .addChildEventListener(habilidadChildEventListener);
    }


}
