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
import com.marlon.apolo.tfinal2022.citasTrabajo.DetalleServicioActivity;

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

//    @Override
//    public void enviarMensaje(Chat chat, MensajeNube mensajeNube, IndividualChatActivity activity) {
//
//    }
//
//    @Override
//    public void responderNotificacion(Chat chat, MensajeNube mensajeNube, Context context) {
//
//    }
//
//    @Override
//    public void enviarNotificacion(MensajeNube mensajeNube, Activity activity) {
//
//    }
//
//    @Override
//    public void actualizarEstadoLecturaMensaje(MensajeNube mensajeNube, Activity activity) {
//
//    }
//
//    @Override
//    public void crearChat(Chat chat, MensajeNube mensajeNube, Activity activity) {
//
//    }
//
//    @Override
//    public void actualizarChat(Chat chat, MensajeNube mensajeNube, Activity activity) {
//
//    }

//    @Override
//    public void crearCuentaConEmailYPassword(Activity activity, FirebaseAuth firebaseAuth, String password) {
//
//    }

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

    public void calificarTrabajador(Cita cita, Context context) {

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

                        Toast.makeText(context, "Cita actualizada!", Toast.LENGTH_LONG).show();
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
