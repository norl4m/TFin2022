package com.marlon.apolo.tfinal2022.admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Trabajador;

public class AdminRepository {
    private MutableLiveData<Administrador> administradorMutableLiveData;
    private MutableLiveData<Administrador> auxAdmin;
    private ValueEventListener valueEventListenerAdmin;

    public AdminRepository() {

    }

    public MutableLiveData<Administrador> getAdministradorMutableLiveData(String idUsuario) {
        if (administradorMutableLiveData == null) {
            administradorMutableLiveData = new MutableLiveData<>();
            loadAdmin(idUsuario);
        }
        return administradorMutableLiveData;
    }

    private void loadAdmin(String idUsuario) {
        valueEventListenerAdmin = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Administrador administrador = snapshot.getValue(Administrador.class);
                    administradorMutableLiveData.setValue(administrador);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(idUsuario)
                .addValueEventListener(valueEventListenerAdmin);
    }

    public void removeValueListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("administrador")
                .removeEventListener(valueEventListenerAdmin);
    }


    public MutableLiveData<Administrador> getAuxAdmin(String idUsuario) {
        //if (auxAdmin == null) {
        auxAdmin = new MutableLiveData<>();
        loadAuxAdmin(idUsuario);
        //}
        return auxAdmin;
    }

    private void loadAuxAdmin(String idUsuario) {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(idUsuario)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            auxAdmin.setValue(administrador);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
