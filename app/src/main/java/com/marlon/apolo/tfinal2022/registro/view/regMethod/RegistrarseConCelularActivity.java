package com.marlon.apolo.tfinal2022.registro.view.regMethod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegistrarseConCelularActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrarseConCelularActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private String celular;
    private String code;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;

    private TextInputLayout textInputLayoutCelular;
    private TextInputLayout textInputLayoutCode;

    private Button buttonVerificarCelular;
    private Button buttonSolicitarNuevoCode;
    private Button buttonCrearCuenta;


    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog progressDialog;
    private Empleador usuarioEmpleador;
    private Trabajador usuarioTrabajador;


    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        WindowInsetsControllerCompat windowInsetsController =
//                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
//        if (windowInsetsController == null) {
//            return;
//        }
//        // Configure the behavior of the hidden system bars
//        windowInsetsController.setSystemBarsBehavior(
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        );
//        // Hide both the status bar and the navigation bar
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]

        phoneNumber = "+593" + phoneNumber;
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            updateUI(user);
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            Log.e(TAG, "signInWithPhone:failure -- " + errorCode);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                updateUI(null);
                            }
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

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

                    empleador.setCelular(celular);
                    usuarioEmpleador = empleador;

                    if (usuarioEmpleador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioEmpleador.getFotoPerfil().toString());
                        Cursor returnCursor = RegistrarseConCelularActivity
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
                                                    Toast.makeText(getApplicationContext(), RegistrarseConCelularActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                                    finishAffinity();
                                                    startActivity(new Intent(RegistrarseConCelularActivity.this, MainNavigationActivity.class));
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
                                                                            Toast.makeText(RegistrarseConCelularActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                                                            finishAffinity();
                                                                            startActivity(new Intent(RegistrarseConCelularActivity.this, MainNavigationActivity.class));
                                                                        }
                                                                    }
                                                                });


                                                    } else {
                                                        // Handle failures

                                                    }
                                                }
                                            });


                                        } else {
                                            Toast.makeText(RegistrarseConCelularActivity.this, RegistrarseConCelularActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                        Toast.makeText(RegistrarseConCelularActivity.this, RegistrarseConCelularActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(RegistrarseConCelularActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                            startActivity(new Intent(RegistrarseConCelularActivity.this, MainNavigationActivity.class));

                                        }
                                    }
                                });
                    }
                    break;
                case 2:
                    photoFlag = false;
                    trabajador.setCelular(celular);
                    usuarioTrabajador = trabajador;

                    if (usuarioTrabajador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioTrabajador.getFotoPerfil().toString());
                        Cursor returnCursor = RegistrarseConCelularActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                                                    Toast.makeText(getApplicationContext(), RegistrarseConCelularActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                                    finishAffinity();
                                                    startActivity(new Intent(RegistrarseConCelularActivity.this, MainNavigationActivity.class));
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
                                                                            Toast.makeText(RegistrarseConCelularActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                                                            finishAffinity();
                                                                            startActivity(new Intent(RegistrarseConCelularActivity.this, MainNavigationActivity.class));
                                                                        }
                                                                    }
                                                                });

                                                    } else {
                                                        // Handle failures

                                                    }
                                                }
                                            });


                                        } else {
                                            Toast.makeText(RegistrarseConCelularActivity.this, RegistrarseConCelularActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                        Toast.makeText(RegistrarseConCelularActivity.this, RegistrarseConCelularActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(RegistrarseConCelularActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                            startActivity(new Intent(RegistrarseConCelularActivity.this, MainNavigationActivity.class));

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


    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
//        title = "Por favor espere";
//        message = "Cachuelito se encuentra verificando el código ingresado...";
////
//        showCustomProgressDialog(title, message);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]
    }


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
        progressDialog.dismiss();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_registrarse_con_celular);


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

        celular = "";
        code = "";

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }

        textInputLayoutCelular = findViewById(R.id.textInputLayoutCelular);
        textInputLayoutCode = findViewById(R.id.textInputLayoutCode);


        buttonVerificarCelular = findViewById(R.id.buttonVerificarCelular);
        buttonSolicitarNuevoCode = findViewById(R.id.buttonSolicitarNuevoCode);
        buttonCrearCuenta = findViewById(R.id.buttonfinish);


        textInputLayoutCode.setVisibility(View.GONE);
        buttonVerificarCelular.setEnabled(false);
        buttonSolicitarNuevoCode.setVisibility(View.GONE);
        buttonCrearCuenta.setVisibility(View.GONE);

        buttonVerificarCelular.setOnClickListener(this);
        buttonSolicitarNuevoCode.setOnClickListener(this);
        buttonCrearCuenta.setOnClickListener(this);

        textInputLayoutCelular.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                celular = s.toString();
                Log.d(TAG, String.valueOf(celular.length()));
                if (celular.length() == 10) {
                    buttonVerificarCelular.setEnabled(true);
                    buttonSolicitarNuevoCode.setEnabled(true);
                    textInputLayoutCelular.setError(null);
                } else {
                    buttonVerificarCelular.setEnabled(false);
                    buttonSolicitarNuevoCode.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputLayoutCode.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code = s.toString();
//                if (!code.isEmpty()) {
                if (code.length() == 6) {
                    //buttonVerificarCelular.setEnabled(true);
                    textInputLayoutCode.setError(null);
                } else {
                    buttonVerificarCelular.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e(TAG, "/************* onVerificationFailed *****************/", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e(TAG, e.toString());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e(TAG, e.toString());
                }

                String errorCode = ((FirebaseAuthException) Objects.requireNonNull(e)).getErrorCode();
                Log.e(TAG, "signInWithPhone:failure -- " + errorCode);
                Log.e(TAG, e.toString());

                switch (errorCode) {
                    case "ERROR_INVALID_PHONE_NUMBER":
                        textInputLayoutCelular.setError("Error: El número celular ingresado es inválido.");
                        break;
                }

                buttonCrearCuenta.setEnabled(false);
                buttonCrearCuenta.setVisibility(View.GONE);

                closeProgressDialog();
                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                closeProgressDialog();

                buttonSolicitarNuevoCode.setVisibility(View.GONE);
                buttonSolicitarNuevoCode.setEnabled(true);

                buttonCrearCuenta.setVisibility(View.VISIBLE);
                buttonCrearCuenta.setEnabled(true);


                buttonVerificarCelular.setVisibility(View.GONE);
                buttonVerificarCelular.setEnabled(true);

                textInputLayoutCelular.setEnabled(false);

                textInputLayoutCode.setVisibility(View.VISIBLE);
                textInputLayoutCode.setEnabled(true);


                alertdialogInfo();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(), "El tiempo de su solicitud ha expirado", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Por favor solicite un nuevo código de verificación!", Toast.LENGTH_LONG).show();
                buttonSolicitarNuevoCode.setVisibility(View.VISIBLE);
                buttonSolicitarNuevoCode.setEnabled(true);

                buttonCrearCuenta.setVisibility(View.GONE);
                buttonCrearCuenta.setEnabled(true);


                buttonVerificarCelular.setVisibility(View.GONE);
                buttonVerificarCelular.setEnabled(true);

                textInputLayoutCelular.setEnabled(true);
                textInputLayoutCode.setEnabled(false);

            }
        };
        // [END phone_auth_callbacks]

    }

    public void alertdialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información:")
                .setMessage(getResources().getString(R.string.text_info_enviado_code))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    @Override
    public void onClick(View v) {
        String title = "";
        String message = "";
        switch (v.getId()) {
            case R.id.buttonVerificarCelular:
                celular = textInputLayoutCelular.getEditText().getText().toString();
                if (celular.length() == 10) {
                    title = "Por favor espere";
                    message = "Cachuelito se encuentra verficando su número de teléfono móvil...";
                    showProgress(title, message);
                    startPhoneNumberVerification(celular);
                } else {
                    textInputLayoutCelular.setError("Error: El número celular ingresado es inválido.");
                }
                break;
            case R.id.buttonSolicitarNuevoCode:
                celular = textInputLayoutCelular.getEditText().getText().toString();
                if (celular.length() == 10) {
                    title = "Por favor espere";
                    message = "Cachuelito está solicitando un nuevo código de verificación...";
                    showProgress(title, message);
                    buttonVerificarCelular.setVisibility(View.GONE);
                    buttonVerificarCelular.setEnabled(false);
                    resendVerificationCode(celular, mResendToken);
                } else {
                    textInputLayoutCelular.setError("Error: El número celular ingresado es inválido.");
                }
                break;
            case R.id.buttonfinish:
                code = textInputLayoutCode.getEditText().getText().toString();
                if (code.length() == 6) {
                    title = "Por favor espere";
                    message = "Cachuelito se encuentra verficando su información personal...";
                    showProgress(title, message);

                    buttonVerificarCelular.setVisibility(View.GONE);
                    buttonVerificarCelular.setEnabled(false);
                    verifyPhoneNumberWithCode(mVerificationId, code);
                } else {
                    textInputLayoutCelular.setError("Error: El código ingresado es inválido.");
                }
                break;
        }
    }
}