package com.marlon.apolo.tfinal2022.model;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.ui.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.NuevaCitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivityAdmin;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;
import com.marlon.apolo.tfinal2022.ui.oficios.viewModel.OficioViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Trabajador extends Usuario {

    private static final String TAG = Trabajador.class.getSimpleName();
    private boolean estadoRrcordP;
    private double calificacion;
    private ArrayList<String> idOficios;

    public Trabajador() {
        estadoRrcordP = true;
    }

    public boolean isEstadoRrcordP() {
        return estadoRrcordP;
    }

    public void setEstadoRrcordP(boolean estadoRrcordP) {
        this.estadoRrcordP = estadoRrcordP;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public ArrayList<String> getIdOficios() {
        return idOficios;
    }

    public void setIdOficios(ArrayList<String> idOficios) {
        this.idOficios = idOficios;
    }

    public void enviarCita(Cita cita, NuevaCitaTrabajoActivity citaActivity) {
        Cita citaAux = cita;
        ArrayList<Item> itemArrayList = cita.getItems();

        cita.setItems(null);
        cita.setStateReceive(false);
        String idCita = FirebaseDatabase.getInstance().getReference().child("citas").push().getKey();
        cita.setIdCita(idCita);

        Map<String, Object> postValues = cita.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/citas/" + idCita, postValues);
        childUpdates.put("/citaItems/" + idCita + "/", itemArrayList);

        FirebaseDatabase.getInstance().getReference()
                .updateChildren(childUpdates)
                .addOnSuccessListener(unused -> {
                    Log.d("TAG", "Registro de cita exitoso");
//                        citaActivity.programarAlarmaLocal(cita);
                    citaActivity.programarAlarmaLocalCustomLoco(cita);
                    Toast.makeText(citaActivity, "Cita enviada!", Toast.LENGTH_LONG).show();
                    citaActivity.finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, e.toString()));
    }

    public void actualizarCita(Cita cita, DetalleServicioActivity detalleServicioActivity) {

        //ArrayList<String> idParticipants = cita.getParticipants();
        ArrayList<Item> itemArrayList = cita.getItems();
        //cita.setParticipants(null);
        cita.setItems(null);

//        String idCita = FirebaseDatabase.getInstance().getReference().child("citas").push().getKey();
        String idCita = cita.getIdCita();
        //cita.setIdCita(idCita);


        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        // String key = mDatabase.child("posts").push().getKey();
        //Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = cita.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/citas/" + idCita, postValues);
//        childUpdates.put("/citasIds/" + idParticipants.get(0) + "/citaId/", idCita);
//        childUpdates.put("/citasIds/" + idParticipants.get(1) + "/citaId/", idCita);
//        childUpdates.put("/citasIds/" + idCita + "/", idParticipants);
        childUpdates.put("/citaItems/" + idCita + "/", itemArrayList);

        FirebaseDatabase
                .getInstance()
                .getReference()
                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d("TAG", "Cita actualizada");

                        Toast.makeText(detalleServicioActivity, "Cita actualizada!", Toast.LENGTH_LONG).show();
//                        citaActivity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });


    }

    public void eliminarCita(Cita cita, DetalleServicioActivity detalleServicioActivity) {

        //ArrayList<String> idParticipants = cita.getParticipants();
        ArrayList<Item> itemArrayList = cita.getItems();
        //cita.setParticipants(null);
        cita.setItems(null);

//        String idCita = FirebaseDatabase.getInstance().getReference().child("citas").push().getKey();
        String idCita = cita.getIdCita();
        //cita.setIdCita(idCita);


        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        // String key = mDatabase.child("posts").push().getKey();
        //Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = cita.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/citas/" + idCita, null);
//        childUpdates.put("/citasIds/" + idParticipants.get(0) + "/citaId/", idCita);
//        childUpdates.put("/citasIds/" + idParticipants.get(1) + "/citaId/", idCita);
//        childUpdates.put("/citasIds/" + idCita + "/", idParticipants);
        childUpdates.put("/citaItems/" + idCita + "/", null);

        FirebaseDatabase
                .getInstance()
                .getReference()
                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d("TAG", "Cita actualizada");

                        Toast.makeText(detalleServicioActivity, "Cita eliminada!", Toast.LENGTH_LONG).show();
//                        citaActivity.finish();
                        detalleServicioActivity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });


    }

    public void crearOficio(OficioViewModel oficioViewModel, Activity activity, Oficio oficio) {
        oficioViewModel.addOficioToFirebase(activity, oficio);
    }

    public void sendEmailVerification(FirebaseAuth mAuth, Activity activity) {
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String message = "Se ha enviado un correo electrónico de confirmación al e-mail: "+user.getEmail();
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void registrarseEnFirebase(Activity activity) {
        SharedPreferences myPreferences = activity.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        boolean adminFlag = myPreferences.getBoolean("adminFlag", false);
        Log.d(TAG, "Iniciando registro");
        Log.d(TAG, "Registrando Trabajador en Firebase");
        this.setCalificacion(0.0);
        Trabajador trabajador = this;
        Log.d(TAG, this.toString());
        FirebaseDatabase.getInstance().getReference().child("trabajadores")
                .child(this.getIdUsuario())
                .setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Registro de trabajador completado");
                            trabajador.sendEmailVerification(FirebaseAuth.getInstance(),activity);
                                    try {
                                        if (adminFlag) {
                                            RegWithEmailPasswordActivityAdmin regWithEmailPasswordActivity = (RegWithEmailPasswordActivityAdmin) activity;
                                            regWithEmailPasswordActivity.closeProgress();
                                        } else {
                                            RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
                                            regWithEmailPasswordActivity.closeProgress();
                                        }
                                    } catch (Exception e) {

                                    }

                            activity.finishAffinity();
                            Intent intent = new Intent(activity, MainNavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            try {
                                activity.finish();
                            } catch (Exception e) {

                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    @Override
    public void registrarseEnFirebaseConFoto(Activity activity) {
        Trabajador trabajadorReg = this;
        Log.d("TAG", "Trabajador");
        Log.d("TAG", "registrarseEnFirebaseConFoto");
        Log.d("TAG", this.toString());


        Log.e(TAG, "REGISTRANDO FOTO");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        String baseReference = "gs://tfinal2022-afc91.appspot.com";
        String imagePath = baseReference + "/" + "trabajadores" + "/" + this.getIdUsuario() + "/" + "fotoPerfil.jpg";
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

        UploadTask uploadTask = storageRef.putFile(Uri.parse(trabajadorReg.getFotoPerfil()), metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            Log.d(TAG, "Upload is " + progress + "% done");

        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "on failure Foto complete...");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
                Log.d(TAG, "Upload is complete...");
                //  registroActivity.limpiarUI();
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    /*Tercer mensaje*/
                    String title = "Por favor espere";
                    String message = "Finalizando registro...";
                    Uri downloadUri = task.getResult();
                    trabajadorReg.setFotoPerfil(downloadUri.toString());
                    registrarseEnFirebase(activity);
                } else {
                    // Handle failures

                }
            }
        });

    }

    @Override
    public void actualizarInfo(Activity activity) {
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores").child(this.getIdUsuario()).setValue(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, "Infomación actualizada", Toast.LENGTH_LONG).show();
                        try {
                            EditarDataActivity editarDataActivity = (EditarDataActivity) activity;
                            editarDataActivity.closeProgressDialog();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                });
    }

    @Override
    public void actualizarInfoConFoto(Activity activity, Uri uri) {
        Trabajador empleadorReg = this;
        Log.d("TAG", "Empleador");
        Log.d("TAG", "registrarseEnFirebaseConFoto");
        Log.d("TAG", this.toString());


        Log.e(TAG, "REGISTRANDO FOTO");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        String baseReference = "gs://tfinal2022-afc91.appspot.com";
        String imagePath = baseReference + "/" + "trabajadores" + "/" + this.getIdUsuario() + "/" + "fotoPerfil.jpg";
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

//        UploadTask uploadTask = storageRef.putFile(Uri.parse(empleadorReg.getFotoPerfil()), metadata);
        UploadTask uploadTask = storageRef.putFile(uri, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            Log.d(TAG, "Upload is " + progress + "% done");

        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "on failure Foto complete...");
                Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                try {
                    EditarDataActivity editarDataActivity = (EditarDataActivity) activity;
                    editarDataActivity.closeProgressDialog();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
                Log.d(TAG, "Upload is complete...");
                //  registroActivity.limpiarUI();
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    /*Tercer mensaje*/
                    String title = "Por favor espere";
                    String message = "Finalizando registro...";
                    Uri downloadUri = task.getResult();
                    empleadorReg.setFotoPerfil(downloadUri.toString());
                    actualizarInfo(activity);
                } else {
                    // Handle failures

                }
            }
        });


    }

    @Override
    public void eliminarInfo(Activity activity) {

        //String baseReference = "gs://tfinal2022-afc91.appspot.com";
        //String imagePath = baseReference + "/" + "empleadores" + "/" + this.getIdUsuario() + "/" + "fotoPerfil.jpg";
        //Log.d(TAG, "Path reference on fireStorage");

        if (this.getFotoPerfil() != null) {
            // Create a storage reference from our app
            //StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to the file to delete
            //StorageReference desertRef = storageRef.;

// Delete the file

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(this.getFotoPerfil());


            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d(TAG, "Foto eliminada de firestorage");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.e(TAG, exception.toString());

                }
            });
        }
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(this.getIdUsuario())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Trabajador eliminado", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void setDeleteUserOnFirebase(String idUsuario) {
        FirebaseDatabase.getInstance().getReference()
                .child("usuariosEliminados")
                .child(idUsuario)
                .setValue(idUsuario);
    }

    @Override
    public void cleanFirebaseDeleteUser(String idUsuario) {
        FirebaseDatabase.getInstance().getReference()
                .child("usuariosEliminados")
                .child(idUsuario)
                .setValue(null);
    }

}
