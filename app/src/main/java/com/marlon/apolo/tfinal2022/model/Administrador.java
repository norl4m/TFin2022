package com.marlon.apolo.tfinal2022.model;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Administrador extends Usuario {
    @Override
    public void registrarseEnFirebase(Activity activity, int metodoReg) {

    }

    @Override
    public void registrarseEnFirebaseConFoto(Activity activity, int metodoReg) {

    }

    @Override
    public void actualizarInfo(Activity activity) {

    }

    @Override
    public void actualizarInfoConFoto(Activity activity, Uri uri) {

    }

    @Override
    public void eliminarInfo(Activity activity) {

    }

    @Override
    public void setDeleteUserOnFirebase(String idUsuario) {
//        FirebaseDatabase.getInstance().getReference()
//                .child("usuariosEliminados")
//                .setValue(idUsuario);
    }

    @Override
    public void cleanFirebaseDeleteUser(String idUsuario) {

    }


    public void calificarTrabajador(Cita cita, DetalleServicioActivity detalleServicioActivity) {

        ArrayList<Item> itemArrayList = cita.getItems();
        //cita.setParticipants(null);
        cita.setItems(null);

        String idCita = cita.getIdCita();
        //cita.setIdCita(idCita);

        Map<String, Object> postValues = cita.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/citas/" + idCita, postValues);


        FirebaseDatabase
                .getInstance()
                .getReference()
                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d("TAG", "Cita actualizada");

//                        Toast.makeText(detalleServicioActivity, "Cita actualizada!", Toast.LENGTH_LONG).show();
//                        citaActivity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", e.toString());
                    }
                });


    }
}
