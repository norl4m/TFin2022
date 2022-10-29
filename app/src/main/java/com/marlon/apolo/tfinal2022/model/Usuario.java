package com.marlon.apolo.tfinal2022.model;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.Mensajito;
import com.marlon.apolo.tfinal2022.individualChat.repository.MensajitoRepository;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.individualChat.view.location.LocationActivity;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;
import com.marlon.apolo.tfinal2022.ui.eliminarCuenta.EliminarInfoEmailActivity;
import com.marlon.apolo.tfinal2022.ui.eliminarCuenta.EliminarInfoGoogleActivity;
import com.marlon.apolo.tfinal2022.ui.eliminarCuenta.EliminarInfoPhoneActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import io.agora.rtc.RtcEngine;

public abstract class Usuario implements Serializable {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String celular;
    private String fotoPerfil;
    private static final String TAG = Usuario.class.getSimpleName();

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

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", celular=" + celular +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                '}';
    }

    public abstract void registrarseEnFirebase(Activity activity, int metodoReg);

    public abstract void registrarseEnFirebaseConFoto(Activity activity, int metodoReg);

    public abstract void actualizarInfo(Activity activity);

    public abstract void actualizarInfoConFoto(Activity activity, Uri uri);

    public abstract void eliminarInfo(Activity activity);

    public abstract void setDeleteUserOnFirebase(String idUsuario);
    public abstract void cleanFirebaseDeleteUser(String idUsuario);


//    public String getFullName() {
//        return String.format("%s %s", this.getNombre(), this.getApellido());
//    }
    /*Comunicación*/

    public void enviarMensaje(Chat chat, MensajeNube mensajeNube, Activity activity) {
        String idMensaje = FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(chat.getIdChat())
                .push().getKey();

        mensajeNube.setIdMensaje(idMensaje);
        mensajeNube.setIdChat(chat.getIdChat());

        Timestamp timestamp = new Timestamp(new Date());
        mensajeNube.setTimeStamp(timestamp.toString());


        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        StorageMetadata storageMetadata = null;

        String baseReference = "";
        String imagePath = "";
        StorageReference storageRef = null;
        UploadTask uploadTask = null;
        switch (mensajeNube.getType()) {
            case 0:
//                Toast.makeText(activity,"Texto",Toast.LENGTH_LONG).show();
                FirebaseDatabase.getInstance().getReference()
                        .child("mensajes")
                        .child(mensajeNube.getIdChat())
                        .child(mensajeNube.getIdMensaje())
                        .setValue(mensajeNube)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    /*GUARDAR LOCALMENTE
                                     *
                                     * +
                                     *
                                     * */
                                    //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();

                                    DataValidation dataValidation = new DataValidation();
                                    String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
                                    String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
                                    //  Log.d("TAG", String.format("%s %s", sec, nansec));
                                    long seconds = Long.parseLong(sec);
                                    int nanoseconds = Integer.parseInt(nansec);
                                    Timestamp timestamp = new Timestamp(seconds, nanoseconds);
                                    // timestamp.toDate()
                                    //Returns a new Date corresponding to this timestamp. This may lose precision.
                                    Date date = timestamp.toDate();
                                    Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
                                            mensajeNube.getContenido(),
                                            date);
                                    mensajeLocal.setFrom(mensajeNube.getFrom());
                                    mensajeLocal.setTo(mensajeNube.getTo());
                                    mensajeLocal.setIdChat(mensajeNube.getIdChat());
                                    mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
                                    mensajeLocal.setTypeContent(0);

                                    //activity.getMensajeLocalViewModel().insert(mensajeLocal);

                                    ((IndividualChatActivity) activity).clearText();

                                    enviarNotificacion(mensajeNube, activity);
                                    actualizarChat(chat, mensajeNube, activity);
                                } else {

                                }
                            }
                        });

                break;
            case 1:
//                Toast.makeText(activity, "Imagen", Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, mensajeNube.toString(), Toast.LENGTH_LONG).show();
                Uri imageUri = Uri.parse(mensajeNube.getContenido());
                String fileExtensionImage = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString());
                String mimeTypeImage = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionImage);
                storageMetadata = new StorageMetadata.Builder()
                        .setContentType(mimeTypeImage)
                        .build();
//
//                Toast.makeText(activity, mimeType, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, fileExtension, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, mensajeNube.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, chat.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, selectedUri.toString(), Toast.LENGTH_LONG).show();


                baseReference = "gs://tfinal2022-afc91.appspot.com";
                imagePath = baseReference + "/" + "mensajes" + "/" + chat.getIdChat() + "/" + mensajeNube.getIdMensaje() + "." + fileExtensionImage;
                Log.d(TAG, "Path reference on fireStorage");
                storageRef = firebaseStorage.getReferenceFromUrl(imagePath);


//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()), storageMetadata);
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));
                if (mensajeNube.getContenido().contains("content:")) {
                    uploadTask = storageRef.putFile(imageUri, storageMetadata);

                } else {
                    Uri imageUriSend = Uri.fromFile(new File(mensajeNube.getContenido()));
                    uploadTask = storageRef.putFile(imageUriSend, storageMetadata);

                }
//                uploadTask = storageRef.putFile(imageUri, storageMetadata);
                // Listen for state changes, errors, and completion of the upload.
                StorageReference finalStorageRef = storageRef;
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
                        return finalStorageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
//                            Toast.makeText(activity, downloadUri.toString(), Toast.LENGTH_LONG).show();

                            mensajeNube.setContenido(downloadUri.toString());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("mensajes")
                                    .child(mensajeNube.getIdChat())
                                    .child(mensajeNube.getIdMensaje())
                                    .setValue(mensajeNube)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                /*GUARDAR LOCALMENTE
                                                 *
                                                 * +
                                                 *
                                                 * */
                                                //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();

                                                DataValidation dataValidation = new DataValidation();
                                                String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
                                                String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
                                                //  Log.d("TAG", String.format("%s %s", sec, nansec));
                                                long seconds = Long.parseLong(sec);
                                                int nanoseconds = Integer.parseInt(nansec);
                                                Timestamp timestamp = new Timestamp(seconds, nanoseconds);
                                                // timestamp.toDate()
                                                //Returns a new Date corresponding to this timestamp. This may lose precision.
                                                Date date = timestamp.toDate();
                                                Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
                                                        mensajeNube.getContenido(),
                                                        date);
                                                mensajeLocal.setFrom(mensajeNube.getFrom());
                                                mensajeLocal.setTo(mensajeNube.getTo());
                                                mensajeLocal.setIdChat(mensajeNube.getIdChat());
                                                mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
                                                mensajeLocal.setTypeContent(0);

                                                //activity.getMensajeLocalViewModel().insert(mensajeLocal);
//                                                activity.clearText();
                                                enviarNotificacion(mensajeNube, activity);
                                                actualizarChat(chat, mensajeNube, activity);
                                            } else {

                                            }
                                        }
                                    });


                        } else {
                            // Handle failures

                        }
                    }
                });
                break;
            case 2:
//                Toast.makeText(activity,"Audio",Toast.LENGTH_LONG).show();

                Log.d(TAG, "##########################################");
                Log.d(TAG, "ENVIANDO MENSAJE DE AUDIO");
                Log.d(TAG, "##########################################");
                Log.d(TAG, mensajeNube.getContenido());
                Uri selectedUri = Uri.parse(mensajeNube.getContenido());
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                storageMetadata = new StorageMetadata.Builder()
                        .setContentType(mimeType)
                        .build();
                Log.d(TAG, String.format("File extension: %s", fileExtension));
                Log.d(TAG, String.format("MimeType: %s", mimeType));

//
//                Toast.makeText(activity, mimeType, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, fileExtension, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, mensajeNube.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, chat.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, selectedUri.toString(), Toast.LENGTH_LONG).show();


                baseReference = "gs://tfinal2022-afc91.appspot.com";
                String audioPath = baseReference + "/" + "mensajes" + "/" + chat.getIdChat() + "/" + mensajeNube.getIdMensaje() + "." + fileExtension;
                Log.d(TAG, "Path reference on fireStorage");
                Log.d(TAG, String.format("Audio path: %s", audioPath));

                storageRef = firebaseStorage.getReferenceFromUrl(audioPath);

//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()), storageMetadata);
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));

//                mensajeNube.setContenido("content://com.android.providers.media.documents/document/audio%3A1391");

                if (mensajeNube.getContenido().contains("content:")) {
                    Uri uricont = Uri.parse(mensajeNube.getContenido());
                    Log.d(TAG, uricont.toString());
                    Log.d(TAG, String.format("Uri content:// : %s", uricont.toString()));

//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));
                    uploadTask = storageRef.putFile(uricont,
                            storageMetadata);
                } else {
                    Uri uriAudio = Uri.fromFile(new File(mensajeNube.getContenido()));
                    uploadTask = storageRef.putFile(uriAudio,
                            storageMetadata);
                    Log.d(TAG, String.format("Uri audio: %s", uriAudio.toString()));

                }


//                Log.d(TAG, uricont.toString());
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));

                // Listen for state changes, errors, and completion of the upload.
                StorageReference finalStorageRef1 = storageRef;
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
                        Log.d(TAG, "on failure audio complete...");

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
                        return finalStorageRef1.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
//                            Toast.makeText(activity, downloadUri.toString(), Toast.LENGTH_LONG).show();

                            mensajeNube.setContenido(downloadUri.toString());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("mensajes")
                                    .child(mensajeNube.getIdChat())
                                    .child(mensajeNube.getIdMensaje())
                                    .setValue(mensajeNube)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                /*GUARDAR LOCALMENTE
                                                 *
                                                 * +
                                                 *
                                                 * */
                                                //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();

                                                DataValidation dataValidation = new DataValidation();
                                                String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
                                                String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
                                                //  Log.d("TAG", String.format("%s %s", sec, nansec));
                                                long seconds = Long.parseLong(sec);
                                                int nanoseconds = Integer.parseInt(nansec);
                                                Timestamp timestamp = new Timestamp(seconds, nanoseconds);
                                                // timestamp.toDate()
                                                //Returns a new Date corresponding to this timestamp. This may lose precision.
                                                Date date = timestamp.toDate();
                                                Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
                                                        mensajeNube.getContenido(),
                                                        date);
                                                mensajeLocal.setFrom(mensajeNube.getFrom());
                                                mensajeLocal.setTo(mensajeNube.getTo());
                                                mensajeLocal.setIdChat(mensajeNube.getIdChat());
                                                mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
                                                mensajeLocal.setTypeContent(0);

                                                //activity.getMensajeLocalViewModel().insert(mensajeLocal);
//                                                activity.clearText();
                                                enviarNotificacion(mensajeNube, activity);
                                                actualizarChat(chat, mensajeNube, activity);
                                            } else {

                                            }
                                        }
                                    });


                        } else {
                            // Handle failures

                        }
                    }
                });


                break;
            case 3:
                break;
            case 4:
                FirebaseDatabase.getInstance().getReference()
                        .child("mensajes")
                        .child(mensajeNube.getIdChat())
                        .child(mensajeNube.getIdMensaje())
                        .setValue(mensajeNube)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    /*GUARDAR LOCALMENTE
                                     *
                                     * +
                                     *
                                     * */
                                    //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();

                                    DataValidation dataValidation = new DataValidation();
                                    String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
                                    String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
                                    //  Log.d("TAG", String.format("%s %s", sec, nansec));
                                    long seconds = Long.parseLong(sec);
                                    int nanoseconds = Integer.parseInt(nansec);
                                    Timestamp timestamp = new Timestamp(seconds, nanoseconds);
                                    // timestamp.toDate()
                                    //Returns a new Date corresponding to this timestamp. This may lose precision.
                                    Date date = timestamp.toDate();
                                    Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
                                            mensajeNube.getContenido(),
                                            date);
                                    mensajeLocal.setFrom(mensajeNube.getFrom());
                                    mensajeLocal.setTo(mensajeNube.getTo());
                                    mensajeLocal.setIdChat(mensajeNube.getIdChat());
                                    mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
                                    mensajeLocal.setTypeContent(0);

                                    //activity.getMensajeLocalViewModel().insert(mensajeLocal);

                                    try {
                                        ((LocationActivity) activity).finish();
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());

                                    }

                                    enviarNotificacion(mensajeNube, activity);
                                    actualizarChat(chat, mensajeNube, activity);
                                } else {

                                }
                            }
                        });
                break;
        }


    }

    public void enviarMensaje(Chat chat, MensajeNube mensajeNube, Context activity) {
        String idMensaje = FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(chat.getIdChat())
                .push().getKey();

        mensajeNube.setIdMensaje(idMensaje);
        mensajeNube.setIdChat(chat.getIdChat());

        Timestamp timestamp = new Timestamp(new Date());
        mensajeNube.setTimeStamp(timestamp.toString());

        switch (mensajeNube.getType()) {
            case 0:
                FirebaseDatabase.getInstance().getReference()
                        .child("mensajes")
                        .child(mensajeNube.getIdChat())
                        .child(mensajeNube.getIdMensaje())
                        .setValue(mensajeNube)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    /*GUARDAR LOCALMENTE
                                     *
                                     * +
                                     *
                                     * */
                                    //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();

                                    DataValidation dataValidation = new DataValidation();
                                    String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
                                    String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
                                    //  Log.d("TAG", String.format("%s %s", sec, nansec));
                                    long seconds = Long.parseLong(sec);
                                    int nanoseconds = Integer.parseInt(nansec);
                                    Timestamp timestamp = new Timestamp(seconds, nanoseconds);
                                    // timestamp.toDate()
                                    //Returns a new Date corresponding to this timestamp. This may lose precision.
                                    Date date = timestamp.toDate();
                                    Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
                                            mensajeNube.getContenido(),
                                            date);
                                    mensajeLocal.setFrom(mensajeNube.getFrom());
                                    mensajeLocal.setTo(mensajeNube.getTo());
                                    mensajeLocal.setIdChat(mensajeNube.getIdChat());
                                    mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
                                    mensajeLocal.setTypeContent(4);

                                    //activity.getMensajeLocalViewModel().insert(mensajeLocal);

//                                    try {
//                                        ((LocationActivity) activity).finish();
//                                    } catch (Exception e) {
//                                        Log.d(TAG, e.toString());
//
//                                    }

                                    enviarNotificacion(mensajeNube, activity);
                                    actualizarChat(chat, mensajeNube, activity);
                                } else {

                                }
                            }
                        });
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:

                break;
        }


    }

    public void enviarMensaje(Chat chat, MensajeNube mensajeNube, Context context, MensajitoRepository mensajitoRepository) {
        String idMensaje = FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(chat.getIdChat())
                .push().getKey();

        mensajeNube.setIdMensaje(idMensaje);
        mensajeNube.setIdChat(chat.getIdChat());

        Timestamp timestamp = new Timestamp(new Date());
        mensajeNube.setTimeStamp(timestamp.toString());

        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(mensajeNube.getIdChat())
                .child(mensajeNube.getIdMensaje())
                .setValue(mensajeNube)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            /*GUARDAR LOCALMENTE
                             *
                             * +
                             *
                             * */
                            //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();

                            DataValidation dataValidation = new DataValidation();
                            String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
                            String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
                            //  Log.d("TAG", String.format("%s %s", sec, nansec));
                            long seconds = Long.parseLong(sec);
                            int nanoseconds = Integer.parseInt(nansec);
                            Timestamp timestamp = new Timestamp(seconds, nanoseconds);
                            // timestamp.toDate()
                            //Returns a new Date corresponding to this timestamp. This may lose precision.
                            Date date = timestamp.toDate();
                            Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
                                    mensajeNube.getContenido(),
                                    date);
                            mensajeLocal.setFrom(mensajeNube.getFrom());
                            mensajeLocal.setTo(mensajeNube.getTo());
                            mensajeLocal.setIdChat(mensajeNube.getIdChat());
                            mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
                            mensajeLocal.setTypeContent(0);

                            guardarMensajeLocalmente(context, mensajeLocal, mensajitoRepository);
                            enviarNotificacion(mensajeNube, context);
                            actualizarChat(chat, mensajeNube, context);

                        } else {

                        }
                    }
                });

    }

    public void guardarMensajeLocalmente(Context context, Mensajito mensajeLocal, MensajitoRepository mensajitoRepository) {
        mensajitoRepository.insert(mensajeLocal);
    }

    public void enviarNotificacion(MensajeNube mensajeNube, Activity activity) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(mensajeNube.getTo())
                .child(mensajeNube.getFrom())
                .child(mensajeNube.getIdMensaje())
                .setValue(mensajeNube)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                             Toast.makeText(activity, "Notificación enviada", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "########################################");
                            Log.d(TAG, "NOTIFICACION GLOBAL ENVIADA");
                            Log.d(TAG, "########################################");
                            FirebaseDatabase.getInstance().getReference()
                                    .child("sms")
                                    .child(mensajeNube.getTo())
                                    .child(mensajeNube.getFrom())
                                    .child(mensajeNube.getIdMensaje())
                                    .setValue(mensajeNube)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                             Toast.makeText(activity, "Notificación enviada", Toast.LENGTH_LONG).show();
                                                Log.d(TAG, "########################################");
                                                Log.d(TAG, "MENSAJE GLOBAL ENVIAD AL CHAT DEL OTRO USUARIO");
                                                Log.d(TAG, "########################################");
                                            } else {

                                            }
                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference()
                                    .child("sms")
                                    .child(mensajeNube.getFrom())
                                    .child(mensajeNube.getTo())
                                    .child(mensajeNube.getIdMensaje())
                                    .setValue(mensajeNube)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                             Toast.makeText(activity, "Notificación enviada", Toast.LENGTH_LONG).show();
                                                Log.d(TAG, "########################################");
                                                Log.d(TAG, "MENSAJE GLOBAL ENVIADA AL CHAT LOCAL");
                                                Log.d(TAG, "########################################");
                                            } else {

                                            }
                                        }
                                    });
                        } else {

                        }
                    }
                });
    }

    public void enviarNotificacion(MensajeNube mensajeNube, Context context) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(mensajeNube.getTo())
                .child(mensajeNube.getFrom())
                .child(mensajeNube.getIdMensaje())
                .setValue(mensajeNube)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Toast.makeText(context, "Notificación enviada", Toast.LENGTH_LONG).show();
                        } else {

                        }
                    }
                });
    }

    //    public void responderNotificacion(Chat chat, MensajeNube mensajeNube, Context context, MensajitoRepository mensajitoRepository, String idTo) {
    public void responderNotificacion(String idTo, Context context, Chat chat, MensajeNube mensajeNube, MensajitoRepository mensajitoRepository) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idTo)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(context, "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
                        } else {

                        }
                    }
                });
        enviarMensaje(chat, mensajeNube, context, mensajitoRepository);
    }

    public void responderNotificacion(String idTo, Context context, Chat chat, MensajeNube mensajeNube) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idTo)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Toast.makeText(context, "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "NOTIFICACIONES ELIMINADAS");
                        } else {

                        }
                    }
                });
        enviarMensaje(chat, mensajeNube, context);
    }

    public void eliminarNotificaciones(String idTo, Context context) {
        Log.d(TAG, "ELIMINANDO NOTIFICACIONES");
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idTo)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
                        } else {

                        }
                    }
                });
    }


    public void actualizarEstadoLecturaMensaje(MensajeNube mensajeNube, Activity activity) {
        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(mensajeNube.getIdChat())
                .child(mensajeNube.getIdMensaje())
                .child("estadoLectura")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Estado de mensaje actualizado");
                        }
                    }
                });
    }

    public void crearChat(Chat chat, MensajeNube mensajeNube, Activity activity) {
        String idChat = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .push().getKey();

        chat.setIdChat(idChat);

        Timestamp timestamp = new Timestamp(new Date());
        mensajeNube.setTimeStamp(timestamp.toString());

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chat.getIdChat())
                .setValue(chat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Chat Creado");

                            try {
                                IndividualChatActivity individualChatActivity = (IndividualChatActivity) activity;
                                //individualChatActivity.setChatLocal(chat);
                                individualChatActivity.listeneMensajesNube(idChat);
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
//                            IndividualChatActivity individualChatActivity = (IndividualChatActivity) activity;
                            //individualChatActivity.setChatLocal(chat);

//                            individualChatActivity.listeneMensajesNube(idChat);

                            //individualChatActivity.dowloadMensajes(chat.getIdChat());
                            //individualChatActivity.cargarMensajesLocales(chat.getIdChat());
                            //individualChatActivity.descargarMensajesDesdeLaNube(chat.getIdChat());

//                            enviarMensaje(chat, mensajeNube, (IndividualChatActivity) activity);
                            enviarMensaje(chat, mensajeNube, activity);
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void actualizarChat(Chat chat, MensajeNube mensajeNube, Activity activity) {
        chat.setMensajeNube(mensajeNube);
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chat.getIdChat())
                .setValue(chat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Chat actualizado");
                            //IndividualChatActivity individualChatActivity = (IndividualChatActivity) activity;
                            //individualChatActivity.setChatLocal(chat);
                            //Toast.makeText(activity, "Chat actualizado", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void actualizarChat(Chat chat, MensajeNube mensajeNube, Context context) {
        chat.setMensajeNube(mensajeNube);
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chat.getIdChat())
                .setValue(chat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Chat actualizado");
                            //IndividualChatActivity individualChatActivity = (IndividualChatActivity) activity;
                            //individualChatActivity.setChatLocal(chat);
                            //Toast.makeText(activity, "Chat actualizado", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void realizarllamadaDeVoz(Usuario usuarioTo, int uidLocal, RtcEngine mRtcEngine, String accessToken, String channelNameShare) {
        Usuario usuarioCaller = this;
        //String idLlamadaVoz = FirebaseDatabase.getInstance().getReference().child("llamadasDeVoz").push().getKey();
        LlamadaVoz llamadaVoz = new LlamadaVoz();
        llamadaVoz.setId(channelNameShare);

        llamadaVoz.setUidCaller(uidLocal);
        llamadaVoz.setUidDestiny(0);

        Participante participanteCaller = new Participante();
        participanteCaller.setIdParticipante(usuarioCaller.getIdUsuario());
        participanteCaller.setNombreParticipante(String.format("%s %s", usuarioCaller.getNombre(), usuarioCaller.getApellido()));
        participanteCaller.setUriFotoParticipante(usuarioCaller.getFotoPerfil());

        Participante participanteDestiny = new Participante();
        participanteDestiny.setIdParticipante(usuarioTo.getIdUsuario());
        participanteDestiny.setNombreParticipante(String.format("%s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
        participanteDestiny.setUriFotoParticipante(usuarioTo.getFotoPerfil());

        llamadaVoz.setParticipanteCaller(participanteCaller);

        llamadaVoz.setParticipanteDestiny(participanteDestiny);

        llamadaVoz.setCallerStatus(true);
        llamadaVoz.setDestinyStatus(false);

        llamadaVoz.setChannelConnectedStatus(false);
        llamadaVoz.setRejectCallStatus(false);

        llamadaVoz.setFinishCall(false);


        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(llamadaVoz.getId())
                .setValue(llamadaVoz);

        mRtcEngine.joinChannel(accessToken, channelNameShare, "Extra Optional Data", uidLocal); // if you do not specify the uid, we will generate the uid for you

    }

    public void realizarllamadaDeVideo(Usuario usuarioTo, int uidLocal, RtcEngine mRtcEngine, String accessToken, String channelNameShare) {
        Usuario usuarioCaller = this;
        //String idLlamadaVoz = FirebaseDatabase.getInstance().getReference().child("llamadasDeVoz").push().getKey();
        LlamadaVideo llamadaVoz = new LlamadaVideo();
        llamadaVoz.setId(channelNameShare);

        llamadaVoz.setUidCaller(uidLocal);
        llamadaVoz.setUidDestiny(0);

        Participante participanteCaller = new Participante();
        participanteCaller.setIdParticipante(usuarioCaller.getIdUsuario());
        participanteCaller.setNombreParticipante(String.format("%s %s", usuarioCaller.getNombre(), usuarioCaller.getApellido()));
        participanteCaller.setUriFotoParticipante(usuarioCaller.getFotoPerfil());

        Participante participanteDestiny = new Participante();
        participanteDestiny.setIdParticipante(usuarioTo.getIdUsuario());
        participanteDestiny.setNombreParticipante(String.format("%s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
        participanteDestiny.setUriFotoParticipante(usuarioTo.getFotoPerfil());

        llamadaVoz.setParticipanteCaller(participanteCaller);

        llamadaVoz.setParticipanteDestiny(participanteDestiny);

        llamadaVoz.setCallerStatus(true);
        llamadaVoz.setDestinyStatus(false);

        llamadaVoz.setChannelConnectedStatus(false);

        llamadaVoz.setFinishCall(false);


        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(llamadaVoz.getId())
                .setValue(llamadaVoz);

        mRtcEngine.joinChannel(accessToken, channelNameShare, "Extra Optional Data", uidLocal); // if you do not specify the uid, we will generate the uid for you

    }

    public void cancelarLlamadaDeVoz(String idLlamada) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(idLlamada)
                .removeValue();
    }

    public void rechazarLlamadaDeVoz(String idLlamada) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(idLlamada)
                .child("rejectCallStatus")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    public void rechazarLlamadaDeVideo(String idLlamada) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(idLlamada)
                .child("rejectCallStatus")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Llamada de video rechazada");
                        }
                    }
                });
    }


    public void finalizarLlamadaDeVoz(String idLlamada) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(idLlamada)
                .child("finishCall")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    public void finalizarLlamadaDeVideo(String idLlamada) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(idLlamada)
                .child("finishCall")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }


    public void constestarLlamadaDeVoz(String idLlamada, RtcEngine mRtcEngine, String accessToken, String channelNameShare, int uidLocal) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(idLlamada)
                .child("uidDestiny")
                .setValue(uidLocal);

        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(idLlamada)
                .child("destinyStatus")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("llamadasDeVoz")
                                    .child(idLlamada)
                                    .child("channelConnectedStatus")
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                mRtcEngine.joinChannel(accessToken, channelNameShare, "Extra Optional Data", uidLocal); // if you do not specify the uid, we will generate the uid for you

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    public void constestarLlamadaDeVideo(String idLlamada, RtcEngine mRtcEngine, String accessToken, String channelNameShare, int uidLocal) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(idLlamada)
                .child("uidDestiny")
                .setValue(uidLocal);

        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(idLlamada)
                .child("destinyStatus")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("llamadasDeVideo")
                                    .child(idLlamada)
                                    .child("channelConnectedStatus")
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                mRtcEngine.joinChannel(accessToken, channelNameShare, "Extra Optional Data", uidLocal); // if you do not specify the uid, we will generate the uid for you

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    public void changeGear() {
        System.out.println("gear changed");
    }

    public void cancelarLlamadaDeVideo(String channelNameShare) {
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(channelNameShare)
                .removeValue();
    }

    public void addToEmailUsers(Activity activity, Usuario usuario) {

        FirebaseDatabase.getInstance().getReference()
                .child("emailUsers")
                .child(usuario.getIdUsuario())
                .child("email")
                .setValue(usuario.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Registro de empleador completado");
                        Intent intent = new Intent(activity, MainNavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        try {
                            activity.finish();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void addToPhoneUsers(Activity activity, Usuario usuario) {
        FirebaseDatabase.getInstance().getReference()
                .child("phoneUsers")
                .child(usuario.getIdUsuario())
                .child("phone")
                .setValue(usuario.getCelular())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Registro de empleador completado");
                        Intent intent = new Intent(activity, MainNavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        try {
                            activity.finish();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void addToGoogleUsers(Activity activity, Usuario usuario) {

        FirebaseDatabase.getInstance().getReference()
                .child("googleUsers")
                .child(usuario.getIdUsuario())
                .child("google")
                .setValue(usuario.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Registro de empleador completado");
                        Intent intent = new Intent(activity, MainNavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        try {
                            activity.finish();
                        } catch (Exception e) {

                        }
                    }
                });
    }

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

    private void updatePartcipantOnChat(Usuario usuarioUpdate) {
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

    private void eliminarInfoFromChats(Usuario usuarioEliminado, Activity activity) {
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

    private void cancelAllLocalNotifications(Activity activity) {
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

    private void setFlagParticipanteEliminadoCrazyChat(Usuario usuarioEliminado, Activity activity) {
//        try {
//            Intent stopIntent = new Intent(activity, CrazyService.class);
//            activity.stopService(stopIntent);
//        } catch (Exception e) {
//
//        }


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//
//            NotificationManager notificationManagerX = activity.getSystemService(NotificationManager.class);
//            notificationManagerX.cancelAll();
//
//        } else {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                NotificationManager notificationManagerX = activity.getSystemService(NotificationManager.class);
//                notificationManagerX.cancelAll();
//
//            } else {
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
//                notificationManager.cancelAll();
//
//            }
//        }


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
                            EliminarInfoGoogleActivity eliminarInfoGoogleActivity = (EliminarInfoGoogleActivity) activity;
                            eliminarInfoGoogleActivity.closeProgress();
                        } catch (Exception e) {

                        }

                        try {
                            EliminarInfoEmailActivity eliminarInfoEmailActivity = (EliminarInfoEmailActivity) activity;
                            eliminarInfoEmailActivity.closeProgress();
                        } catch (Exception e) {

                        }


                        try {
                            EliminarInfoPhoneActivity eliminarInfoPhoneActivity = (EliminarInfoPhoneActivity) activity;
                            eliminarInfoPhoneActivity.closeProgress();
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


//    private void cleanMultimediaMessage(String idRemoteUser) {
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        Log.d(TAG, "cleanMultimediaMessage");
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + idRemoteUser;
////        StorageReference listRef = storage.getReference().child("files/uid");
//        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                            Log.d(TAG, prefix.toString());
//                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.
//                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
//                            Log.d(TAG, item.toString());
//                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
//                            String baseReference = item.toString();
//                            Log.d(TAG, "Path reference on fireStorage");
//                            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
//// Delete the file
//                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    // File deleted successfully
//                                    Log.d(TAG, "File delete");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception exception) {
//                                    // Uh-oh, an error occurred!
//                                    Log.d(TAG, "Error delete");
//                                    Log.d(TAG, exception.toString());
//                                }
//                            });
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//
//
////
////
////
////        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
////
//////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
////        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/7Om2riDJ5YQtB0sW8P5pFiBSQXs1";
//////        StorageReference listRef = storage.getReference().child("files/uid");
////        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);
////
////        listRef.listAll()
////                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
////                    @Override
////                    public void onSuccess(ListResult listResult) {
////                        for (StorageReference prefix : listResult.getPrefixes()) {
////                            // All the prefixes under listRef.
////                            // You may call listAll() recursively on them.
////                            Log.d(TAG, prefix.toString());
////                        }
////
////                        for (StorageReference item : listResult.getItems()) {
////                            // All the items under listRef.
////                            Log.d(TAG, item.toString());
////                        }
////                    }
////                })
////                .addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        // Uh-oh, an error occurred!
////                    }
////                });
////
////
////        Log.d(TAG, "cleanMultimediaMessage");
////        for (MessageCloudPoc me : messageCloudPocsMultimedia) {
////            Log.d(TAG, me.toString());
////            //6String baseReference = "gs://tfinal2022-afc91.appspot.com";
//////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/7Om2riDJ5YQtB0sW8P5pFiBSQXs1/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/-NBKEUq0Tit_XihDpPAY.mp3";
//////            String baseReference = "https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/mensajes%2F7Om2riDJ5YQtB0sW8P5pFiBSQXs1%2F9JeHodaM0kOVyEuDWvAVyOiB2Qb2%2F-NBKJNguUWtJWJsNpu6Z.mp3?alt=media&token=e78d17d6-f362-4b33-8bc1-afc9d488f7c7";
//////            String imagePath = baseReference + "/" + "mensajes" + "/" + me.getFrom() + "/" + me.getTo() + "/" + me.getIdMensaje();
//////            String imagePath = me.getContenido();
////            String baseReference = me.getContenido();
////            Log.d(TAG, "Path reference on fireStorage");
////            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
////// Delete the file
////            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
////                @Override
////                public void onSuccess(Void aVoid) {
////                    // File deleted successfully
////                    Log.d(TAG, "File delete");
////                }
////            }).addOnFailureListener(new OnFailureListener() {
////                @Override
////                public void onFailure(@NonNull Exception exception) {
////                    // Uh-oh, an error occurred!
////                    Log.d(TAG, "Error delete");
////                    Log.d(TAG, exception.toString());
////                }
////            });
////        }
//
//    }


    private void cleanMultimediaMessage(String usuarioDel, String idRemoteUser) {
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


//
//
//
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/7Om2riDJ5YQtB0sW8P5pFiBSQXs1";
////        StorageReference listRef = storage.getReference().child("files/uid");
//        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                            Log.d(TAG, prefix.toString());
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.
//                            Log.d(TAG, item.toString());
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//
//
//        Log.d(TAG, "cleanMultimediaMessage");
//        for (MessageCloudPoc me : messageCloudPocsMultimedia) {
//            Log.d(TAG, me.toString());
//            //6String baseReference = "gs://tfinal2022-afc91.appspot.com";
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/7Om2riDJ5YQtB0sW8P5pFiBSQXs1/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/-NBKEUq0Tit_XihDpPAY.mp3";
////            String baseReference = "https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/mensajes%2F7Om2riDJ5YQtB0sW8P5pFiBSQXs1%2F9JeHodaM0kOVyEuDWvAVyOiB2Qb2%2F-NBKJNguUWtJWJsNpu6Z.mp3?alt=media&token=e78d17d6-f362-4b33-8bc1-afc9d488f7c7";
////            String imagePath = baseReference + "/" + "mensajes" + "/" + me.getFrom() + "/" + me.getTo() + "/" + me.getIdMensaje();
////            String imagePath = me.getContenido();
//            String baseReference = me.getContenido();
//            Log.d(TAG, "Path reference on fireStorage");
//            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
//// Delete the file
//            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    // File deleted successfully
//                    Log.d(TAG, "File delete");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Uh-oh, an error occurred!
//                    Log.d(TAG, "Error delete");
//                    Log.d(TAG, exception.toString());
//                }
//            });
//        }

    }

    private void cleanMultimediaMessageBackward(String idDel, String idRemoteUser) {
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


//
//
//
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/7Om2riDJ5YQtB0sW8P5pFiBSQXs1";
////        StorageReference listRef = storage.getReference().child("files/uid");
//        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                            Log.d(TAG, prefix.toString());
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.
//                            Log.d(TAG, item.toString());
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//
//
//        Log.d(TAG, "cleanMultimediaMessage");
//        for (MessageCloudPoc me : messageCloudPocsMultimedia) {
//            Log.d(TAG, me.toString());
//            //6String baseReference = "gs://tfinal2022-afc91.appspot.com";
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/7Om2riDJ5YQtB0sW8P5pFiBSQXs1/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/-NBKEUq0Tit_XihDpPAY.mp3";
////            String baseReference = "https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/mensajes%2F7Om2riDJ5YQtB0sW8P5pFiBSQXs1%2F9JeHodaM0kOVyEuDWvAVyOiB2Qb2%2F-NBKJNguUWtJWJsNpu6Z.mp3?alt=media&token=e78d17d6-f362-4b33-8bc1-afc9d488f7c7";
////            String imagePath = baseReference + "/" + "mensajes" + "/" + me.getFrom() + "/" + me.getTo() + "/" + me.getIdMensaje();
////            String imagePath = me.getContenido();
//            String baseReference = me.getContenido();
//            Log.d(TAG, "Path reference on fireStorage");
//            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
//// Delete the file
//            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    // File deleted successfully
//                    Log.d(TAG, "File delete");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Uh-oh, an error occurred!
//                    Log.d(TAG, "Error delete");
//                    Log.d(TAG, exception.toString());
//                }
//            });
//        }

    }


    private void deleteUserFromMessage(Usuario usuarioEliminado) {
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


    private void setFlagParticipanteEliminado(Usuario usuarioEliminado, Activity activity) {
        String estadoEnApp = "eliminado";
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Chat> chatArrayList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    for (Participante p : chat.getParticipantes()) {
                        if (p.getIdParticipante().equals(usuarioEliminado.getIdUsuario())) {
                            Log.d(TAG, "Chat antes: " + chat.toString());
                            chatArrayList.add(chat);
                            break;
                        }
                    }
                }
                for (Chat chat : chatArrayList) {
                    int indexP = 0;
                    for (Participante p : chat.getParticipantes()) {
                        if (p.getIdParticipante().equals(usuarioEliminado.getIdUsuario())) {
                            p.setIdParticipante(usuarioEliminado.getIdUsuario());
                            p.setNombreParticipante(usuarioEliminado.getNombre() + " " + usuarioEliminado.getApellido());
//                            p.setUriFotoParticipante(usuarioEliminado.getFotoPerfil());
                            p.setUriFotoParticipante(null);
                            p.setEstadoEnApp(estadoEnApp);
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
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Usuario eliminado", Toast.LENGTH_LONG).show();
                            try {
                                EliminarInfoGoogleActivity eliminarInfoGoogleActivity = (EliminarInfoGoogleActivity) activity;
                                eliminarInfoGoogleActivity.closeProgress();
                            } catch (Exception e) {

                            }

                            try {
                                EliminarInfoEmailActivity eliminarInfoEmailActivity = (EliminarInfoEmailActivity) activity;
                                eliminarInfoEmailActivity.closeProgress();
                            } catch (Exception e) {

                            }


                            try {
                                EliminarInfoPhoneActivity eliminarInfoPhoneActivity = (EliminarInfoPhoneActivity) activity;
                                eliminarInfoPhoneActivity.closeProgress();
                            } catch (Exception e) {

                            }


                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .addListenerForSingleValueEvent(valueEventListener);
    }

    //public abstract void eliminarChat(Chat chat);


    //public abstract void crearCuentaConEmailYPassword(Activity activity, FirebaseAuth firebaseAuth, String password);
}
