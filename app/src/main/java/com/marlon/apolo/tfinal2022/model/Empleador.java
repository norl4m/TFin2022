package com.marlon.apolo.tfinal2022.model;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.ui.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivityAdmin;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Empleador extends Usuario {
    private static final String TAG = Empleador.class.getSimpleName();

    public Empleador() {
    }

//    public void sendEmailVerification(FirebaseAuth mAuth, Activity activity) {
//        // Send verification email
//        // [START send_email_verification]
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Email sent
//               }
//                });
//        // [END send_email_verification]
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


        FirebaseDatabase.getInstance().getReference().child("citas")
                .child(cita.getIdCita())
                .child("calificacion")
                .setValue(cita.getCalificacion())

//        FirebaseDatabase
//                .getInstance()
//                .getReference()
//                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d("TAG", "Cita actualizada");

                        Toast.makeText(detalleServicioActivity, "Cita actualizada!", Toast.LENGTH_LONG).show();
//                        citaActivity.finish();
                        updateCalifOnTrabajador(cita.getFrom());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });


    }

    private void updateCalifOnTrabajador(String from) {

        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Cita> citaArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Cita cita = data.getValue(Cita.class);
                            if (cita.getFrom().equals(from)) {
                                citaArrayList.add(cita);
                            }
                        }

                        float suma = 0;
                        float index = 0;
                        for (Cita citaDB : citaArrayList) {
                            if (citaDB.getCalificacion() > 0.0f) {
                                suma = suma + citaDB.getCalificacion();
                                index++;
                            }
                        }
//                        float calif = suma / citaArrayList.size();
                        float calif = suma / index;
                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        Log.d(TAG, String.valueOf(suma));
                        Log.d(TAG, String.valueOf(index));
                        Log.d(TAG, String.valueOf(calif));

                        FirebaseDatabase.getInstance().getReference()
                                .child("trabajadores")
                                .child(citaArrayList.get(0).getFrom())
                                .child("calificacion")
                                .setValue(calif);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

//                        Toast.makeText(context, "Cita actualizada!", Toast.LENGTH_LONG).show();
//                        citaActivity.finish();

                        updateCalifOnTrabajador(cita.getFrom());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });


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
        Empleador empleador = this;
        FirebaseDatabase.getInstance().getReference().child("empleadores").child(this.getIdUsuario()).setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"Registro exitoso");
                            empleador.sendEmailVerification(FirebaseAuth.getInstance(), activity);
                                    try {
                                        if (adminFlag) {
                                            RegWithEmailPasswordActivityAdmin regWithEmailPasswordActivity = (RegWithEmailPasswordActivityAdmin) activity;
                                            regWithEmailPasswordActivity.closeProgress();
                                        } else {
                                            RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
                                            regWithEmailPasswordActivity.closeProgress();
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                            activity.finishAffinity();
                            Intent intent = new Intent(activity, MainNavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            try {
                                activity.finish();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void registrarseEnFirebaseConFoto(Activity activity) {
        Empleador empleadorReg = this;
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
        String imagePath = baseReference + "/" + "empleadores" + "/" + this.getIdUsuario() + "/" + "fotoPerfil.jpg";
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

        UploadTask uploadTask = storageRef.putFile(Uri.parse(empleadorReg.getFotoPerfil()), metadata);

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
                    empleadorReg.setFotoPerfil(downloadUri.toString());
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
                .child("empleadores")
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
        Empleador empleadorReg = this;
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
        String imagePath = baseReference + "/" + "empleadores" + "/" + this.getIdUsuario() + "/" + "fotoPerfil.jpg";
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

//        UploadTask uploadTask = storageRef.putFile(Uri.parse(empleadorReg.getFotoPerfil()), metadata);

//        UploadTask uploadTask = storageRef.putBytes(data);
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
                .child("empleadores")
                .child(this.getIdUsuario())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Empleador eliminado", Toast.LENGTH_LONG).show();
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
