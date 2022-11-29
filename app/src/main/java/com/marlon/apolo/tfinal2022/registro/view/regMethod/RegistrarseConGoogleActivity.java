package com.marlon.apolo.tfinal2022.registro.view.regMethod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;

public class RegistrarseConGoogleActivity extends AppCompatActivity {
    private static final String TAG = RegistrarseConGoogleActivity.class.getSimpleName();
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private Empleador usuarioEmpleador;
    private Trabajador usuarioTrabajador;

    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
//        dialog.setTitle("Por favor espere");
        progressDialog.setTitle(title);
//        dialog.setMessage("Trabix se encuentra verificando su nùmero celular...");
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    public void closeProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        String title = "Por favor espere";
        String message = "Cachuelito se encuentra verificando su información personal...";
//
        showProgress(title, message);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                            updateUI(null);
                            closeProgressDialog();
                        }
                    }
                });
    }
    // [END auth_with_google]


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "Usuario registrado en Firebase Authentication");
            closeProgressDialog();

            String title = "Por favor espere";
            String message = "Su cuenta ya casi está lista...";

            boolean photoFlag = false;

            switch (regUsuario) {
                case 1:
                    photoFlag = false;

                    empleador.setEmail(user.getEmail());
                    usuarioEmpleador = empleador;

                    if (usuarioEmpleador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioEmpleador.getFotoPerfil().toString());
                        Cursor returnCursor = RegistrarseConGoogleActivity
                                .this.getContentResolver().query(returnUri, null, null, null, null);
                        /*
                         * Get the column indexes of the data in the Cursor,
                         * move to the first row in the Cursor, get the data,
                         * and display it.
                         */
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        Log.d(TAG, returnCursor.getString(nameIndex));
                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                        if (returnCursor.getLong(sizeIndex) > 0) {
                            //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                            photoFlag = true;
                        } else {
                            //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                            photoFlag = false;
                        }
                    } else {
                        photoFlag = false;
                    }
                    /*Paso 2*/
                    usuarioEmpleador.setIdUsuario(user.getUid());
                    if (photoFlag) {

                        showProgress(title, message);

//                        usuarioEmpleador.registrarseEnFirebaseConFoto(RegistrarseConEmailPasswordActivity.this, 1);


                        FirebaseDatabase.getInstance().getReference().child("empleadores")
                                .child(usuarioEmpleador.getIdUsuario())
                                .setValue(usuarioEmpleador)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Registro exitoso", Toast.LENGTH_LONG).show();
                                            Log.d(TAG, "Trabajador registrado en Firebase RealTime Database");


                                            Empleador empleadorReg = (Empleador) usuarioEmpleador;
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
                                            String imagePath = baseReference + "/" + "trabajadores" + "/" + empleadorReg.getIdUsuario() + "/" + "fotoPerfil.jpg";
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
                                                public void onFailure(@androidx.annotation.NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    Log.d(TAG, "on failure Foto complete...");
                                                    closeProgressDialog();
                                                    Toast.makeText(getApplicationContext(), RegistrarseConGoogleActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                                    finishAffinity();
                                                    startActivity(new Intent(RegistrarseConGoogleActivity.this, MainNavigationActivity.class));
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
                                                public Task<Uri> then(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    }

                                                    // Continue with the task to get the download URL
                                                    return storageRef.getDownloadUrl();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        closeProgressDialog();
                                                        /*Tercer mensaje*/
                                                        String title = "Por favor espere";
                                                        String message = "Finalizando registro...";
                                                        Uri downloadUri = task.getResult();
                                                        empleadorReg.setFotoPerfil(downloadUri.toString());

                                                        showProgress(title, message);
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("empleadores")
                                                                .child(empleadorReg.getIdUsuario())
                                                                .setValue(empleadorReg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            closeProgressDialog();
                                                                            Toast.makeText(RegistrarseConGoogleActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                                                            finishAffinity();
                                                                            startActivity(new Intent(RegistrarseConGoogleActivity.this, MainNavigationActivity.class));
                                                                        }
                                                                    }
                                                                });


                                                    } else {
                                                        // Handle failures

                                                    }
                                                }
                                            });


                                        } else {
                                            Toast.makeText(RegistrarseConGoogleActivity.this, RegistrarseConGoogleActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                        Toast.makeText(RegistrarseConGoogleActivity.this, RegistrarseConGoogleActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

                                    }
                                });


                    } else {
                        usuarioEmpleador.setFotoPerfil(null);
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                        usuarioEmpleador.registrarseEnFirebase(RegistrarseConEmailPasswordActivity.this, 1);


                        FirebaseDatabase.getInstance().getReference()
                                .child("empleadores")
                                .child(usuarioEmpleador.getIdUsuario())
                                .setValue(usuarioEmpleador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            closeProgressDialog();
                                            Toast.makeText(RegistrarseConGoogleActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                            startActivity(new Intent(RegistrarseConGoogleActivity.this, MainNavigationActivity.class));

                                        }
                                    }
                                });
                    }
                    break;
                case 2:
                    photoFlag = false;
                    trabajador.setEmail(user.getEmail());
                    usuarioTrabajador = trabajador;

                    if (usuarioTrabajador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioTrabajador.getFotoPerfil().toString());
                        Cursor returnCursor = RegistrarseConGoogleActivity.this.getContentResolver().query(returnUri, null, null, null, null);
                        /*
                         * Get the column indexes of the data in the Cursor,
                         * move to the first row in the Cursor, get the data,
                         * and display it.
                         */
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        Log.d(TAG, returnCursor.getString(nameIndex));
                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                        if (returnCursor.getLong(sizeIndex) > 0) {
                            //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                            photoFlag = true;
                        } else {
                            //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                            photoFlag = false;
                        }
                    } else {
                        photoFlag = false;
                    }
                    /*Paso 2*/
                    usuarioTrabajador.setIdUsuario(user.getUid());
                    if (photoFlag) {

                        showProgress(title, message);

//                        usuarioTrabajador.registrarseEnFirebaseConFoto(RegistrarseConEmailPasswordActivity.this, 1);


                        usuarioTrabajador.setCalificacion(0.0);
                        Log.d(TAG, this.toString());

                        FirebaseDatabase.getInstance().getReference().child("trabajadores")
                                .child(usuarioTrabajador.getIdUsuario())
                                .setValue(usuarioTrabajador)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Registro exitoso", Toast.LENGTH_LONG).show();
                                            Log.d(TAG, "Trabajador registrado en Firebase RealTime Database");


                                            Trabajador trabajadorReg = (Trabajador) usuarioTrabajador;
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
                                            String imagePath = baseReference + "/" + "trabajadores" + "/" + trabajadorReg.getIdUsuario() + "/" + "fotoPerfil.jpg";
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
                                                public void onFailure(@androidx.annotation.NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    Log.d(TAG, "on failure Foto complete...");
                                                    closeProgressDialog();
                                                    Toast.makeText(getApplicationContext(), RegistrarseConGoogleActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                                    finishAffinity();
                                                    startActivity(new Intent(RegistrarseConGoogleActivity.this, MainNavigationActivity.class));
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
                                                public Task<Uri> then(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    }

                                                    // Continue with the task to get the download URL
                                                    return storageRef.getDownloadUrl();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        closeProgressDialog();
                                                        /*Tercer mensaje*/
                                                        String title = "Por favor espere";
                                                        String message = "Finalizando registro...";
                                                        Uri downloadUri = task.getResult();
                                                        trabajadorReg.setFotoPerfil(downloadUri.toString());

                                                        showProgress(title, message);
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("trabajadores")
                                                                .child(trabajadorReg.getIdUsuario())
                                                                .setValue(trabajadorReg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            closeProgressDialog();
                                                                            Toast.makeText(RegistrarseConGoogleActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                                                            finishAffinity();
                                                                            startActivity(new Intent(RegistrarseConGoogleActivity.this, MainNavigationActivity.class));
                                                                        }
                                                                    }
                                                                });

                                                    } else {
                                                        // Handle failures

                                                    }
                                                }
                                            });


                                        } else {
                                            Toast.makeText(RegistrarseConGoogleActivity.this, RegistrarseConGoogleActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                        Toast.makeText(RegistrarseConGoogleActivity.this, RegistrarseConGoogleActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

                                    }
                                });


                    } else {
                        usuarioTrabajador.setFotoPerfil(null);
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                        usuarioTrabajador.registrarseEnFirebase(RegistrarseConEmailPasswordActivity.this, 1);
                        FirebaseDatabase.getInstance().getReference()
                                .child("trabajadores")
                                .child(usuarioTrabajador.getIdUsuario())
                                .setValue(usuarioTrabajador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            closeProgressDialog();
                                            Toast.makeText(RegistrarseConGoogleActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                            startActivity(new Intent(RegistrarseConGoogleActivity.this, MainNavigationActivity.class));

                                        }
                                    }
                                });
                    }
                    break;
            }


        } else {
            closeProgressDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse_con_google);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }


        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.gg_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
    }


    // ...
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    // ...

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Log.d(TAG, "Got ID token.");
                        firebaseAuthWithGoogle(idToken);
                    }
                } catch (ApiException e) {
                    // ...
                    Log.w(TAG, "signInWithPhone:failure -- " + e.toString());
                    Log.w(TAG, "signInWithPhone:failure -- " + e.getMessage());
                    Log.w(TAG, "signInWithPhone:failure -- " + e.getStatus());

                    Toast.makeText(getApplicationContext(), "Por favor seleccione una cuenta de Google para completar su registro", Toast.LENGTH_LONG).show();
//                    buttonSelectAccount.setEnabled(true);
//                    buttonSelectAccount.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}