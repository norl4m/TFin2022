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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.NuevaCitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;

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

    @Override
    public String toString() {
        return "Trabajador{" +
                "estadoRrcordP=" + estadoRrcordP +
                ", calificacion=" + calificacion +
                ", idOficios=" + idOficios +
                "} " + super.toString();
    }

    private void signInAdmin(Activity activity, SharedPreferences myPreferences) {
        FirebaseAuth.getInstance().signOut();
        String email = myPreferences.getString("email", null);
        String password = myPreferences.getString("key", null);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Log.d(TAG, "ADMIN");

                            SharedPreferences.Editor editorPref = myPreferences.edit();
                            editorPref.putInt("usuario", 0);
                            editorPref.apply();
                            activity.finishAffinity();
                            Intent intent = new Intent(activity, MainNavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            try {
                                activity.finish();
                            } catch (Exception e) {

                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, activity.getString(R.string.error_inesperado),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void registrarseEnFirebase(Activity activity, int metodoReg) {
        SharedPreferences myPreferences = activity.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        boolean adminFlag = myPreferences.getBoolean("adminFlag", false);
        Log.d(TAG, "Iniciando registro");
        Log.d(TAG, "Registrando Trabajador en Firebase");
        this.setCalificacion(0.0);
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

                            switch (metodoReg) {
                                case 1:/*email*/
                                    RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
                                    regWithEmailPasswordActivity.closeProgress();
                                    break;


                            }
                            if (adminFlag) {
                                signInAdmin(activity, myPreferences);
                            } else {
                                activity.finishAffinity();
                                Intent intent = new Intent(activity, MainNavigationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                                try {
                                    activity.finish();
                                } catch (Exception e) {

                                }
                            }


//                            Intent intent = new Intent(activity, MainNavigationActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            activity.startActivity(intent);
//                            try {
//                                activity.finish();
//                            } catch (Exception e) {
//
//                            }
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
    public void registrarseEnFirebaseConFoto(Activity activity, int metodoReg) {
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
                    registrarseEnFirebase(activity, metodoReg);
                } else {
                    // Handle failures

                }
            }
        });

    }

    @Override
    public void actualizarInfo(Activity activity) {
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(this.getIdUsuario())
                .setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Infomación actualizada", Toast.LENGTH_LONG).show();
                            try {
                                EditarDataActivity editarDataActivity = (EditarDataActivity) activity;
                                editarDataActivity.closeProgressDialog();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
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

    public ArrayList<String> getIdOficios() {
        return idOficios;
    }

    public void setIdOficios(ArrayList<String> idOficios) {
        this.idOficios = idOficios;
    }


    public void enviarCita(Cita cita, NuevaCitaTrabajoActivity citaActivity) {

        Cita citaAux = cita;
        Log.d(TAG, "ENVIANDO CITA DE TRABAJO");
        //ArrayList<String> idParticipants = cita.getParticipants();
        ArrayList<Item> itemArrayList = cita.getItems();
        //cita.setParticipants(null);
        cita.setItems(null);
        cita.setStateReceive(false);

        String idCita = FirebaseDatabase.getInstance().getReference().child("citas").push().getKey();
        cita.setIdCita(idCita);


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
                        Log.d("TAG", "Registro de cita exitoso");
//                        citaActivity.programarAlarmaLocal(cita);
                        citaActivity.programarAlarmaLocalCustomLoco(cita);
                        Toast.makeText(citaActivity, "Cita enviada!", Toast.LENGTH_LONG).show();
                        citaActivity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });


//        FirebaseDatabase.getInstance().getReference()
//                .child("citas")
//                .child(idCita)
//                .setValue(cita)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d("TAG", "Registro exitoso");
//                        Toast.makeText(citaActivity, "Cita enviada!", Toast.LENGTH_LONG).show();
//                        citaActivity.finish();
////                        registroActivity.getProgressBar().setVisibility(View.GONE);
////                        registroActivity.getTextViewSavingData().setText(R.string.reg_exitoso);
////                        registroActivity.limpiarUI();
//
//                    }
//                });
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

}
