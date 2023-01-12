package com.marlon.apolo.tfinal2022.model;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

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
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.CrazyService;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;
import com.marlon.apolo.tfinal2022.ui.eliminarCuenta.EliminarInfoEmailActivity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public abstract class Usuario implements Serializable {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String celular;
    private String fotoPerfil;
    private static final String TAG = Usuario.class.getSimpleName();
    private String password;

    public Usuario() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public abstract void registrarseEnFirebase(Activity activity, int metodoReg);

    public abstract void registrarseEnFirebaseConFoto(Activity activity, int metodoReg);

    public abstract void actualizarInfo(Activity activity);

    public abstract void actualizarInfoConFoto(Activity activity, Uri uri);

    public abstract void eliminarInfo(Activity activity);

    public abstract void setDeleteUserOnFirebase(String idUsuario);

    public abstract void cleanFirebaseDeleteUser(String idUsuario);
    /*Métodos normales*/

    public void updateCompleteInfo(String locationToFirebase, Activity activity, int metodoReg, String password, ProgressDialog progressDialog) {


        Usuario usuarioUpdate = this;
        Log.d(TAG, usuarioUpdate.toString());
        if (usuarioUpdate.getFotoPerfil() != null) {
            if (usuarioUpdate.toString().contains("https:")) {
                updateNormalInfo(locationToFirebase, activity, metodoReg, password, usuarioUpdate, progressDialog);

            } else {
                Log.e(TAG, "ACTUALIZANDO INFORMACION CON FOTO");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                // Create the file metadata
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .build();

                String baseReference = "gs://tfinal2022-afc91.appspot.com";
                String imagePath = baseReference + "/" + locationToFirebase + "/" + usuarioUpdate.getIdUsuario() + "/" + "fotoPerfil.jpg";
                Log.d(TAG, "Path reference on fireStorage");
                StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

                UploadTask uploadTask = storageRef.putFile(Uri.parse(usuarioUpdate.getFotoPerfil()), metadata);

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
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();


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
                            usuarioUpdate.setFotoPerfil(downloadUri.toString());

                            updateNormalInfo(locationToFirebase, activity, metodoReg, password, usuarioUpdate, progressDialog);

                        } else {
                            // Handle failures

                        }
                    }
                });
            }
        } else {
            updateNormalInfo(locationToFirebase, activity, metodoReg, password, usuarioUpdate, progressDialog);
        }

    }

    public void updateNormalInfo(String locationToFirebase, Activity activity, int metodoReg, String password, Usuario usuarioUpdate, ProgressDialog progressDialog) {

        FirebaseDatabase.getInstance().getReference()
                .child(locationToFirebase)/*administrador - trabajadores - empleadores*/
                .child(usuarioUpdate.getIdUsuario())
                .setValue(usuarioUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            switch (metodoReg) {
                                case 0:/*email*/
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    try {
                                        if (!password.isEmpty()) {
                                            firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Password actualizado");
                                                    } else {
                                                        Log.d(TAG, "Error al actualizar password");
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al actualizar password: " + e.toString());

                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }

                                    break;
                                case 1:/*phone*/
                                    break;
                                case 2:/*google*/
                                    break;

                            }
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                            Toast.makeText(activity, "Información actualizada", Toast.LENGTH_LONG).show();


                            updatePartcipantOnChat(usuarioUpdate);


                        } else {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                            Toast.makeText(activity, activity.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void updatePartcipantOnChat(Usuario usuarioUpdate) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Chat> chatArrayList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    for (Participante p : chat.getParticipantes()) {
                        if (p.getIdParticipante().equals(usuarioUpdate.getIdUsuario())) {
                            Log.d(TAG, "Chat antes: " + chat.toString());
                            chatArrayList.add(chat);
                            break;
                        }
                    }
                }
                for (Chat chat : chatArrayList) {
                    int indexP = 0;
                    for (Participante p : chat.getParticipantes()) {
                        if (p.getIdParticipante().equals(usuarioUpdate.getIdUsuario())) {
                            p.setIdParticipante(usuarioUpdate.getIdUsuario());
                            p.setNombreParticipante(usuarioUpdate.getNombre() + " " + usuarioUpdate.getApellido());
                            p.setUriFotoParticipante(usuarioUpdate.getFotoPerfil());
                            chat.getParticipantes().set(indexP, p);
                            break;
                        }
                        indexP++;
                    }
                }

                for (Chat chat : chatArrayList) {
                    Log.d(TAG, "Actualizando chat: " + chat.toString());
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(chat.getIdChat())
                            .setValue(chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void eliminarInfo(String locationInFirebase, Activity activity) {
        Usuario usuarioEliminado = this;


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
                .child(locationInFirebase)
                .child(this.getIdUsuario())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(activity, "Usuario eliminado", Toast.LENGTH_LONG).show();
                            stopService(activity);
                            cancelAllLocalNotifications(activity);
                            eliminarInfoFromChats(usuarioEliminado, activity);
//                            activity.finish();
                        }
                    }
                });
    }

    public void eliminarInfoFromChats(Usuario usuarioEliminado, Activity activity) {
        //setFlagParticipanteEliminado(usuarioEliminado, activity);
        setFlagParticipanteEliminadoCrazyChat(usuarioEliminado, activity);
    }

    public void stopService(Activity activity) {
        Log.d(TAG, "Stoping service");
        try {
            Intent stopIntent = new Intent(activity, CrazyService.class);
            activity.stopService(stopIntent);
        } catch (Exception e) {

        }

    }

    public void cancelAllLocalNotifications(Activity activity) {
        Log.d(TAG, "Cancel all notifications");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

            NotificationManager notificationManagerX = activity.getSystemService(NotificationManager.class);
            notificationManagerX.cancelAll();

        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManagerX = activity.getSystemService(NotificationManager.class);
                notificationManagerX.cancelAll();

            } else {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
                notificationManager.cancelAll();

            }
        }
    }

    public void setFlagParticipanteEliminadoCrazyChat(Usuario usuarioEliminado, Activity activity) {

        FirebaseDatabase.getInstance().getReference()
                .child("crazyChats")
                .child(usuarioEliminado.getIdUsuario())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            ChatPoc chatPoc = data.getValue(ChatPoc.class);
                            if (chatPoc != null) {
                                //Log.d(TAG, chatPoc.toString());

                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("crazyChats")
                                        .child(chatPoc.getIdRemoteUser())
                                        .child(usuarioEliminado.getIdUsuario())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                ChatPoc chatPoc2 = snapshot.getValue(ChatPoc.class);
                                                if (chatPoc2 != null) {
                                                    Log.d(TAG, "/----------------------------/");
                                                    Log.d(TAG, chatPoc2.toString());
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child("crazyChats")
                                                            .child(chatPoc.getIdRemoteUser())
                                                            .child(usuarioEliminado.getIdUsuario())
                                                            .child("stateRemoteUser")
                                                            .setValue("eliminado");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                FirebaseDatabase.getInstance().getReference()
                                        .child("crazyMessages")
                                        .child(chatPoc.getIdRemoteUser())
                                        .child(usuarioEliminado.getIdUsuario())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Log.d(TAG, "Message remote: " + String.valueOf(snapshot.getChildrenCount()));
                                                if (snapshot.getChildrenCount() == 0) {
                                                    try {
                                                        cleanMultimediaMessage(usuarioEliminado.getIdUsuario(), chatPoc.getIdRemoteUser());
                                                        cleanMultimediaMessageBackward(usuarioEliminado.getIdUsuario(), chatPoc.getIdRemoteUser());
                                                    } catch (Exception e) {
                                                        Log.e(TAG, e.toString());
                                                    }
                                                } else {
//                                                    FirebaseDatabase.getInstance()
//                                                            .getReference()
//                                                            .child("crazyChats")
//                                                            .child(chatPoc.getIdRemoteUser())
//                                                            .child(usuarioEliminado.getIdUsuario())
//                                                            .child("stateRemoteUser")
//                                                            .setValue("eliminado");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }


                        }

                        deleteUserFromChats(usuarioEliminado);
                        deleteUserFromMessage(usuarioEliminado);


                        Toast.makeText(activity, "Usuario eliminado", Toast.LENGTH_LONG).show();


                        try {
                            EliminarInfoEmailActivity eliminarInfoEmailActivity = (EliminarInfoEmailActivity) activity;
                            eliminarInfoEmailActivity.closeProgress();
                        } catch (Exception e) {

                        }


                        try {
                            FirebaseAuth.getInstance().getCurrentUser().delete();
                        } catch (Exception e) {

                        }
                        FirebaseAuth.getInstance().signOut();

                        //FirebaseAuth.getInstance().getCurrentUser().delete();

                        activity.finishAffinity();


                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void cleanMultimediaMessage(String usuarioDel, String idRemoteUser) {
        Log.d(TAG, "cleanMultimediaMessage");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

//        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + usuarioDel + "/" + idRemoteUser;
//        StorageReference listRef = storage.getReference().child("files/uid");
        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, prefix.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, item.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            String baseReference = item.toString();
                            Log.d(TAG, "Path reference on fireStorage");
                            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
// Delete the file
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d(TAG, "File delete");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "Error delete");
                                    Log.d(TAG, exception.toString());
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


    }

    public void cleanMultimediaMessageBackward(String idDel, String idRemoteUser) {
        Log.d(TAG, "cleanMultimediaMessageBackward");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

//        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + idRemoteUser;
        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + idRemoteUser + "/" + idDel;
//        StorageReference listRef = storage.getReference().child("files/uid");
        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, prefix.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, item.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            String baseReference = item.toString();
                            Log.d(TAG, "Path reference on fireStorage");
                            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
// Delete the file
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d(TAG, "File delete");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "Error delete");
                                    Log.d(TAG, exception.toString());
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });

    }

    public void deleteUserFromMessage(Usuario usuarioEliminado) {
        FirebaseDatabase.getInstance().getReference()
                .child("crazyMessages")
                .child(usuarioEliminado.getIdUsuario())
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "MENSAJES ELIMINADOS");
                        }
                    }
                });
    }

    public void deleteUserFromChats(Usuario usuarioEliminado) {
        FirebaseDatabase.getInstance().getReference()
                .child("crazyChats")
                .child(usuarioEliminado.getIdUsuario())
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "CHATS ELIMINADOS");
                        }
                    }
                });
    }

}
