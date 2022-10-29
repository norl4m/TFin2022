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
import com.google.firebase.auth.AuthResult;
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
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.registro.view.EmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithCelularActivity;
import com.marlon.apolo.tfinal2022.registro.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithGoogleActivity;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Empleador extends Usuario {
    private static final String TAG = Empleador.class.getSimpleName();

    @Override
    public String toString() {
        return "Empleador{} " + super.toString();
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

    @Override
    public void registrarseEnFirebase(Activity activity, int metodoReg) {
        SharedPreferences myPreferences = activity.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        boolean adminFlag = myPreferences.getBoolean("adminFlag", false);

        Log.d(TAG, "Iniciando registro");
        Log.d(TAG, "Registrando emplador en Firebase");
        Log.d(TAG, String.valueOf(adminFlag));
        Log.d(TAG, String.valueOf(myPreferences.getString("key", null)));
        Log.d(TAG, String.valueOf(myPreferences.getString("email", null)));

        Empleador empleador = this;
        empleador.setIdUsuario(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        Log.d(TAG, this.toString());
        Log.d(TAG, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        FirebaseDatabase.getInstance().getReference().child("empleadores")
                .child(this.getIdUsuario())
                .setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Registro de empleador completado");

                            switch (metodoReg) {
                                case 1:/*email*/
                                    EmailPasswordActivity emailPasswordActivity = (EmailPasswordActivity) activity;
                                    emailPasswordActivity.closeCustomAlertDialog();
                                    break;
                                case 2:/*google*/
                                    RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
                                    regWithGoogleActivity.closeCustomAlertDialog();
//                                    addToGoogleUsers(activity, empleador);
                                    break;
                                case 3:/*phone*/
//                                    addToPhoneUsers(activity, empleador);
                                    RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
                                    regWithCelularActivity.closeCustomAlertDialog();
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
    public void registrarseEnFirebaseConFoto(Activity activity, int metodoReg) {
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

//    @Override
//    public void enviarMensaje(Chat chat, MensajeNube mensajeNube, IndividualChatActivity activity) {
//        String idMensaje = FirebaseDatabase.getInstance().getReference()
//                .child("mensajes")
//                .child(chat.getIdChat())
//                .push().getKey();
//
//        mensajeNube.setIdMensaje(idMensaje);
//        mensajeNube.setIdChat(chat.getIdChat());
//
//        Timestamp timestamp = new Timestamp(new Date());
//        mensajeNube.setTimeStamp(timestamp.toString());
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("mensajes")
//                .child(mensajeNube.getIdChat())
//                .child(mensajeNube.getIdMensaje())
//                .setValue(mensajeNube)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            /*GUARDAR LOCALMENTE
//                             *
//                             * +
//                             *
//                             * */
//                            //Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_LONG).show();
//
//                            DataValidation dataValidation = new DataValidation();
//                            String sec = dataValidation.splitterData(mensajeNube.getTimeStamp(), "(seconds=", ",");
//                            String nansec = dataValidation.splitterData(mensajeNube.getTimeStamp(), ", nanoseconds=", ")");
//                            //  Log.d("TAG", String.format("%s %s", sec, nansec));
//                            long seconds = Long.parseLong(sec);
//                            int nanoseconds = Integer.parseInt(nansec);
//                            Timestamp timestamp = new Timestamp(seconds, nanoseconds);
//                            // timestamp.toDate()
//                            //Returns a new Date corresponding to this timestamp. This may lose precision.
//                            Date date = timestamp.toDate();
//                            Mensajito mensajeLocal = new Mensajito(mensajeNube.getIdMensaje(),
//                                    mensajeNube.getContenido(),
//                                    date);
//                            mensajeLocal.setFrom(mensajeNube.getFrom());
//                            mensajeLocal.setTo(mensajeNube.getTo());
//                            mensajeLocal.setIdChat(mensajeNube.getIdChat());
//                            mensajeLocal.setReadStatus(mensajeNube.isEstadoLectura());
//                            mensajeLocal.setTypeContent(0);
//
//                            activity.getMensajeLocalViewModel().insert(mensajeLocal);
//                            activity.clearTextView();
//                            enviarNotificacion(mensajeNube, activity);
//                            actualizarChat(chat, mensajeNube, activity);
//                        } else {
//
//                        }
//                    }
//                });
//
//    }
//
//    @Override
//    public void enviarNotificacion(MensajeNube mensajeNube, Activity activity) {
//        FirebaseDatabase.getInstance().getReference()
//                .child("notificaciones")
//                .child(mensajeNube.getTo())
//                .child(mensajeNube.getFrom())
//                .child(mensajeNube.getIdMensaje())
//                .setValue(mensajeNube)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(activity, "Notificación enviada", Toast.LENGTH_LONG).show();
//                        } else {
//
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public void responderNotificacion(Chat chat, MensajeNube mensajeNube, Context context) {
//
//    }
//
//    @Override
//    public void actualizarEstadoLecturaMensaje(MensajeNube mensajeNube, Activity activity) {
//        FirebaseDatabase.getInstance().getReference()
//                .child("mensajes")
//                .child(mensajeNube.getIdChat())
//                .child(mensajeNube.getIdMensaje())
//                .child("estadoLectura")
//                .setValue(true)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Estado de mensaje actualizado");
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public void crearChat(Chat chat, MensajeNube mensajeNube, Activity activity) {
//        String idChat = FirebaseDatabase.getInstance().getReference()
//                .child("chats")
//                .push().getKey();
//
//        chat.setIdChat(idChat);
//
//        Timestamp timestamp = new Timestamp(new Date());
//        mensajeNube.setTimeStamp(timestamp.toString());
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("chats")
//                .child(chat.getIdChat())
//                .setValue(chat)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Chat Creado");
//
//                            IndividualChatActivity individualChatActivity = (IndividualChatActivity) activity;
//                            individualChatActivity.setChatLocal(chat);
//
//                            //individualChatActivity.dowloadMensajes(chat.getIdChat());
//                            individualChatActivity.cargarMensajesLocales(chat.getIdChat());
//                            individualChatActivity.descargarMensajesDesdeLaNube(chat.getIdChat());
//
//                            enviarMensaje(chat, mensajeNube, (IndividualChatActivity) activity);
//                        } else {
//                            Toast.makeText(activity, activity.getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public void actualizarChat(Chat chat, MensajeNube mensajeNube, Activity activity) {
//        chat.setMensajeNube(mensajeNube);
//        FirebaseDatabase.getInstance().getReference()
//                .child("chats")
//                .child(chat.getIdChat())
//                .setValue(chat)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Chat actualizado");
//                            //IndividualChatActivity individualChatActivity = (IndividualChatActivity) activity;
//                            //individualChatActivity.setChatLocal(chat);
//                            //Toast.makeText(activity, "Chat actualizado", Toast.LENGTH_LONG).show();
//
//                        } else {
//                            Toast.makeText(activity, activity.getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }


//    public void crearCuentaConEmailPassword(FirebaseAuth firebaseAuth, String password, Activity activity) {
//
//        RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//        regWithEmailPasswordActivity.blockbuttonFinalizar();/*Bloqueamos el botón para que el usuario no pueda interactuar hasta que se produzca un error o se complete la creación de la cuenta*/
//
//        Empleador empleadorReg = this;
//        // [START create_user_with_email]
//        firebaseAuth.createUserWithEmailAndPassword(this.getEmail(), password)
//                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
////                            Toast.makeText(activity, "Cuenta creada.", Toast.LENGTH_SHORT).show();
//                            RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//
//                            String title = "";
//                            String message = "";
//
//                            boolean estadoFoto = false;
//                            /*Verificamos si el usuario ha subido una imagen*/
//                            if (empleadorReg.getFotoPerfil() != null) {
//                                title = "Por favor espere";
//                                message = "Finalizando registro...";
//                                estadoFoto = true;
//                            } else {
//                                title = "Por favor espere";
//                                message = "En estos momentos nos encontramos validando su información personal...";
//                                estadoFoto = false;
//                            }
//                            regWithEmailPasswordActivity.showProgress(title, message);
//
//                            if (estadoFoto) {
//                                registrarEmpleadorConFoto(Uri.parse(empleadorReg.getFotoPerfil()), "empleadores", firebaseAuth, activity, empleadorReg, 1);
//                            } else {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            regWithEmailPasswordActivity.closeProgressDialog();
//                                        } catch (Exception e) {
//
//                                        }
//                                        regEmpleadorEnFirebase(empleadorReg, activity, firebaseAuth, 1);
//                                    }
//                                }, 1500);
//                            }
//
//
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
//                            Log.w(TAG, "signInWithPhone:failure -- " + errorCode);
//                            RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//                            switch (errorCode) {
//                                case "ERROR_EMAIL_ALREADY_IN_USE":
//                                    regWithEmailPasswordActivity.alertDialogInfoError(0);
////                                    Toast.makeText(activity, "Parece que ya posees una cuenta en", Toast.LENGTH_SHORT).show();
//                                    break;
//                                case "ERROR_WEAK_PASSWORD":
//                                    regWithEmailPasswordActivity.alertDialogInfoError(1);
//                                    break;
//                                case "ERROR_INVALID_EMAIL":
//                                    regWithEmailPasswordActivity.alertDialogInfoError(2);
//                                    break;
//                            }
//                            regWithEmailPasswordActivity.controlErrorsUI();
////                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        // [END create_user_with_email]
//
//    }

    private void sendEmailVerification(FirebaseAuth mAuth, Activity activity) {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                        RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
                        regWithEmailPasswordActivity.alertDialogEmailVerificationInfo();
//                        Toast.makeText(activity, "Se ha enviado un mensaje a su correo electrónico. Para completar el registro por favor revise su bandeja de entrada", Toast.LENGTH_SHORT).show();
                    }
                });
        // [END send_email_verification]
    }

    public void regEmpleadorEnFirebase(Empleador empleadorReg, Activity activity, FirebaseAuth firebaseAuth, int regMethod) {
        String idUsuario = FirebaseDatabase.getInstance().getReference().child("empleadores").push().getKey();
        empleadorReg.setIdUsuario(idUsuario);

//        AtomicBoolean estadoEmpleador = new AtomicBoolean(false);
//
//        EmpleadorViewModel empleadorViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(EmpleadorViewModel.class);
//
////        empleadorViewModel.getEmpleadorLiveData("-N3fLkg4y_eVuxpVaeYE").observe((LifecycleOwner) activity, empleador -> {
//
//
//        String contactoVeri = "";
//        if (empleadorReg.getEmail() != null) {
//            contactoVeri = empleadorReg.getEmail();
//        }
//        if (empleadorReg.getCelular() != null) {
//            contactoVeri = empleadorReg.getCelular();
//        }
//
//        empleadorViewModel.getVerficiadorDeUsuario(contactoVeri).observe((LifecycleOwner) activity, empleador -> {
//            if (empleador != null) {
//                estadoEmpleador.set(true);
////                Log.d(TAG, "Empleador encontrado");
////                Toast.makeText(activity, "El empleador no puede ser registrado. Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
//            }
//        });

        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(idUsuario)
                .setValue(empleadorReg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            if (empleadorReg.getFotoPerfil() != null) {
//                                Uri uriFoto = Uri.parse(empleadorReg.toString());
//                                registrarUsuarioConFoto(uriFoto,activity,idUsuario,empleadorReg);
//                            } else {

                            switch (regMethod) {
                                case 1:
//                                    if (estadoEmpleador.get()) {
//                                    Toast.makeText(activity, "El empleador no puede ser registrado. Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
//                                    } else {
                                    sendEmailVerification(firebaseAuth, activity);
//                                    }
                                    break;
                                case 2:
                                    RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
                                    regWithGoogleActivity.alertDialogVerificationInfo();
                                    break;
                                case 3:
                                    RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
                                    regWithCelularActivity.alertDialogVerificationInfo();

                                    break;
                            }

//                            }
                        } else {
                            switch (regMethod) {
                                case 1:
                                    RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
                                    regWithEmailPasswordActivity.controlErrorsUI();
                                    break;
                                case 2:
                                    RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
                                    break;
                                case 3:
                                    RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
                                    break;
                            }


                        }
                    }
                });
    }

//    public void registrarEmpleadorConFoto(Uri uri, String refUser, FirebaseAuth firebaseAuth, Activity activity, Empleador empleador, int regMethod) {
//
//        String title = "Por favor espere";
//        String message = "Su cuenta ya casi está lista...";
//        switch (regMethod) {
//            case 1:
//
//                RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//                try {
//                    /*Cerrando primer mensaje*/
//                    regWithEmailPasswordActivity.closeProgressDialog();
//                } catch (Exception e) {
//
//                }
//                /*segundo mensaje*/
//                regWithEmailPasswordActivity.showProgress(title, message);
//                break;
//            case 2:
//                RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
//                regWithGoogleActivity.showProgress(title, message);
//
//                break;
//            case 3:
//                RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
//                regWithCelularActivity.showProgress(title, message);
//                break;
//        }
//
//
//        Log.e(TAG, "REGISTRANDO FOTO");
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        // Create the file metadata
//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setContentType("image/jpg")
//                .build();
//
//        String baseReference = "gs://tfinal2022-afc91.appspot.com";
//        String imagePath = baseReference + "/" + refUser + "/" + firebaseAuth.getCurrentUser().getUid() + "/" + "fotoPerfil.jpg";
//        Log.d(TAG, "Path reference on fireStorage");
//        StorageReference storageRef = storage.getReferenceFromUrl(imagePath);
//
//
//
//
//        /*
//         * Get the file's content URI from the incoming Intent,
//         * then query the server app to get the file's display name
//         * and size.
//         */
//        Uri returnUri = uri;
//        Cursor returnCursor = activity.getContentResolver().query(returnUri, null, null, null, null);
//        /*
//         * Get the column indexes of the data in the Cursor,
//         * move to the first row in the Cursor, get the data,
//         * and display it.
//         */
//        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//        returnCursor.moveToFirst();
//        Log.d(TAG, returnCursor.getString(nameIndex));
//
//        Log.d(TAG, Long.toString(returnCursor.getLong(sizeIndex)));
//        if (returnCursor.getLong(sizeIndex) > 0) {
//            UploadTask uploadTask = storageRef.putFile(uri, metadata);
//
//            // Listen for state changes, errors, and completion of the upload.
//            uploadTask.addOnProgressListener(taskSnapshot -> {
//                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                Log.d(TAG, "Upload is " + progress + "% done");
//
//            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                    Log.d(TAG, "Upload is paused");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    Log.d(TAG, "on failure Foto complete...");
//
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Handle successful uploads on complete
//                    // ...
//                    Log.d(TAG, "Upload is complete...");
//                    //  registroActivity.limpiarUI();
//                }
//            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//
//                    // Continue with the task to get the download URL
//                    return storageRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        /*Tercer mensaje*/
//                        String title = "Por favor espere";
//                        String message = "Finalizando registro...";
//                        Uri downloadUri = task.getResult();
//                        empleador.setFotoPerfil(downloadUri.toString());
//
//                        switch (regMethod) {
//                            case 1:
//                                RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//                                try {
//                                    /*Cerrando segundo mensaje*/
//                                    regWithEmailPasswordActivity.closeProgressDialog();
//                                } catch (Exception e) {
//
//                                }
//
//
//                                regWithEmailPasswordActivity.showProgress(title, message);
//
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            /*Cerrando tecer mensaje*/
//                                            regWithEmailPasswordActivity.closeProgressDialog();
//                                        } catch (Exception e) {
//
//                                        }
//                                        regEmpleadorEnFirebase(empleador, activity, firebaseAuth, regMethod);
//                                    }
//                                }, 1500);
//                                break;
//                            case 2:
//                                RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
//                                try {
//                                    /*Cerrando segundo mensaje*/
//                                    regWithGoogleActivity.closeProgressDialog();
//                                } catch (Exception e) {
//
//                                }
//
//                                /*Tercer mensaje*/
//                                regWithGoogleActivity.showProgress(title, message);
//
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            /*Cerrando tecer mensaje*/
//                                            regWithGoogleActivity.closeProgressDialog();
//                                        } catch (Exception e) {
//
//                                        }
//                                        regEmpleadorEnFirebase(empleador, activity, firebaseAuth, regMethod);
//                                    }
//                                }, 1500);
//                                break;
//                            case 3:
//                                RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
//                                try {
//                                    /*Cerrando segundo mensaje*/
//                                    regWithCelularActivity.closeProgressDialog();
//                                } catch (Exception e) {
//
//                                }
//
//                                /*Tercer mensaje*/
//                                regWithCelularActivity.showProgress(title, message);
//
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            /*Cerrando tecer mensaje*/
//                                            regWithCelularActivity.closeProgressDialog();
//                                        } catch (Exception e) {
//
//                                        }
//                                        regEmpleadorEnFirebase(empleador, activity, firebaseAuth, regMethod);
//                                    }
//                                }, 1500);
//                                break;
//                        }
//
//                    } else {
//                        // Handle failures
//                        // ...
//
//                        switch (regMethod) {
//                            case 1:
//                                RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//                                regWithEmailPasswordActivity.controlErrorsUI();
//                                break;
//                            case 2:
//                                RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
//                                break;
//                            case 3:
//                                RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
//                                break;
//                        }
//                    }
//                }
//            });
//        } else {
//            empleador.setFotoPerfil(null);
//            switch (regMethod) {
//                case 1:
//                    RegWithEmailPasswordActivity regWithEmailPasswordActivity = (RegWithEmailPasswordActivity) activity;
//                    try {
//                        /*Cerrando segundo mensaje*/
//                        regWithEmailPasswordActivity.closeProgressDialog();
//                    } catch (Exception e) {
//
//                    }
//
//
//                    regWithEmailPasswordActivity.showProgress(title, message);
//
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                /*Cerrando tecer mensaje*/
//                                regWithEmailPasswordActivity.closeProgressDialog();
//                            } catch (Exception e) {
//
//                            }
//                            regEmpleadorEnFirebase(empleador, activity, firebaseAuth, regMethod);
//                        }
//                    }, 1500);
//                    break;
//                case 2:
//                    RegWithGoogleActivity regWithGoogleActivity = (RegWithGoogleActivity) activity;
//                    try {
//                        /*Cerrando segundo mensaje*/
//                        regWithGoogleActivity.closeProgressDialog();
//                    } catch (Exception e) {
//
//                    }
//
//                    /*Tercer mensaje*/
//                    regWithGoogleActivity.showProgress(title, message);
//
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                /*Cerrando tecer mensaje*/
//                                regWithGoogleActivity.closeProgressDialog();
//                            } catch (Exception e) {
//
//                            }
//                            regEmpleadorEnFirebase(empleador, activity, firebaseAuth, regMethod);
//                        }
//                    }, 1500);
//                    break;
//                case 3:
//                    RegWithCelularActivity regWithCelularActivity = (RegWithCelularActivity) activity;
//                    try {
//                        /*Cerrando segundo mensaje*/
//                        regWithCelularActivity.closeProgressDialog();
//                    } catch (Exception e) {
//
//                    }
//
//                    /*Tercer mensaje*/
//                    regWithCelularActivity.showProgress(title, message);
//
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                /*Cerrando tecer mensaje*/
//                                regWithCelularActivity.closeProgressDialog();
//                            } catch (Exception e) {
//
//                            }
//                            regEmpleadorEnFirebase(empleador, activity, firebaseAuth, regMethod);
//                        }
//                    }, 1500);
//                    break;
//            }
//        }
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
}
