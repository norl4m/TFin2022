package com.marlon.apolo.tfinal2022.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivityAdmin;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.NuevoOficioArchiActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.OficioArchiEditDeleteActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel.OficioArchiViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.viewModel.OficioViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongFunction;

public class Administrador extends Usuario {

    public Administrador() {
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


    @Override
    public void registrarseEnFirebase(Activity activity) {

    }

    @Override
    public void registrarseEnFirebaseConFoto(Activity activity) {

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


    public void crearOficio(OficioArchiViewModel oficioArchiViewModel, Oficio oficioArchiModel, NuevoOficioArchiActivity activity, ProgressDialog progressDialog) {
        oficioArchiViewModel.insert(oficioArchiModel, activity, progressDialog);
    }


    public void eliminarOficio(OficioArchiViewModel oficioArchiViewModel, Oficio oficioArchiModelSelected, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
        oficioArchiViewModel.delete(oficioArchiModelSelected, oficioArchiEditDeleteActivity, progressDialog);
    }

    public void updateOficio(OficioArchiViewModel oficioArchiViewModel, Oficio oficioArchiModelSelected, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
        oficioArchiViewModel.update(oficioArchiModelSelected, oficioArchiEditDeleteActivity, progressDialog);
    }

    public void regEmpWithFoto(Usuario usuarioEmpleador, RegWithEmailPasswordActivityAdmin regWithEmailPasswordActivityAdmin) {
        usuarioEmpleador.registrarseEnFirebaseConFoto(regWithEmailPasswordActivityAdmin);
    }

    public void regEmpNormal(Usuario usuarioEmpleador, RegWithEmailPasswordActivityAdmin regWithEmailPasswordActivityAdmin) {
        usuarioEmpleador.registrarseEnFirebase(regWithEmailPasswordActivityAdmin);
    }

    public void regTrabWithFoto(Usuario usuarioTrabajador, RegWithEmailPasswordActivityAdmin regWithEmailPasswordActivityAdmin) {
        usuarioTrabajador.registrarseEnFirebaseConFoto(regWithEmailPasswordActivityAdmin);
    }

    public void regTraNormal(Usuario usuarioTrabajador, RegWithEmailPasswordActivityAdmin regWithEmailPasswordActivityAdmin) {
        usuarioTrabajador.registrarseEnFirebase(regWithEmailPasswordActivityAdmin);

    }
}
