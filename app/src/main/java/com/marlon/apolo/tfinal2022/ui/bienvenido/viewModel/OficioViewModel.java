package com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.ArrayList;
import java.util.List;

public class OficioViewModel extends ViewModel {
    private static final String TAG = OficioViewModel.class.getSimpleName();
    private MutableLiveData<List<Oficio>> allOficios;

    public LiveData<List<Oficio>> getAllOficios() {
        if (allOficios == null) {
            allOficios = new MutableLiveData<List<Oficio>>();
            loadOficios();
        }
        return allOficios;
    }

    private void loadOficios() {
        ArrayList<Oficio> oficioArrayList = new ArrayList<>();
        // Do an asynchronous operation to fetch data.
        ChildEventListener childEventListenerOficios = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded");
                Oficio oficioAdded = snapshot.getValue(Oficio.class);
                if (oficioAdded != null) {
                    Log.d(TAG, oficioAdded.toString());
                    oficioArrayList.add(oficioAdded);
                    allOficios.setValue(oficioArrayList);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged");
                Oficio oficioChanged = snapshot.getValue(Oficio.class);

                if (oficioChanged != null) {
                    int index = 0;
                    Log.d(TAG, oficioChanged.toString());
                    for (Oficio oficioDB : oficioArrayList) {
                        if (oficioDB.getIdOficio().equals(oficioChanged.getIdOficio())) {
                            oficioDB.setNombre(oficioChanged.getNombre());
                            oficioArrayList.set(index, oficioDB);
                            allOficios.setValue(oficioArrayList);
                            break;
                        }
                        index++;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved");
                Oficio oficioRemoved = snapshot.getValue(Oficio.class);
                if (oficioRemoved != null) {
                    int index = 0;
                    Log.d(TAG, oficioRemoved.toString());
                    for (Oficio oficioDB : oficioArrayList) {
                        if (oficioDB.getIdOficio().equals(oficioRemoved.getIdOficio())) {
                            oficioArrayList.remove(index);
                            allOficios.setValue(oficioArrayList);
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
                .child("oficios")
                .addChildEventListener(childEventListenerOficios);
    }

}
