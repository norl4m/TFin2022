package com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorViewModel extends ViewModel {
    private static final String TAG = TrabajadorViewModel.class.getSimpleName();

    private MutableLiveData<List<Trabajador>> allTrabajadores;

    public MutableLiveData<List<Trabajador>> getAllTrabajadores() {
        if (allTrabajadores == null) {
            allTrabajadores = new MutableLiveData<>();
            loadTrabajadores();
        }
        return allTrabajadores;
    }

    private void loadTrabajadores() {
        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();

        ChildEventListener childEventListenerTrabajadores = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded");
                Trabajador trabajadorAdded = snapshot.getValue(Trabajador.class);
                if (trabajadorAdded != null) {
                    Log.d(TAG, trabajadorAdded.toString());
                    trabajadorArrayList.add(trabajadorAdded);
                    allTrabajadores.setValue(trabajadorArrayList);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged");
                Trabajador trabajadorChanged = snapshot.getValue(Trabajador.class);

                if (trabajadorChanged != null) {
                    int index = 0;
                    Log.d(TAG, trabajadorChanged.toString());
                    for (Trabajador trabajadorDB : trabajadorArrayList) {
                        if (trabajadorDB.getIdUsuario().equals(trabajadorChanged.getIdUsuario())) {
                            trabajadorDB = trabajadorChanged;
                            trabajadorArrayList.set(index, trabajadorDB);
                            allTrabajadores.setValue(trabajadorArrayList);
                            break;
                        }
                        index++;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved");
                Trabajador trabajadorRemoved = snapshot.getValue(Trabajador.class);
                if (trabajadorRemoved != null) {
                    int index = 0;
                    Log.d(TAG, trabajadorRemoved.toString());
                    for (Trabajador trabajadorDB : trabajadorArrayList) {
                        if (trabajadorDB.getIdUsuario().equals(trabajadorRemoved.getIdUsuario())) {
                            trabajadorArrayList.remove(index);
                            allTrabajadores.setValue(trabajadorArrayList);
                            break;
                        }
                        index++;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildMoved");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled");

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .addChildEventListener(childEventListenerTrabajadores);

    }
}
