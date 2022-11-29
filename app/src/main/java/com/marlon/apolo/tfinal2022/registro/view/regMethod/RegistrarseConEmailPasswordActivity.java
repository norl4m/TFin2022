package com.marlon.apolo.tfinal2022.registro.view.regMethod;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.registro.view.RegWithCelularActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.RegWithGoogleActivity;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class RegistrarseConEmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrarseConEmailPasswordActivity.class.getSimpleName();
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private String email;
    private String password;
    private Button buttonFinish;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;

    private Trabajador usuarioTrabajador;
    private Empleador usuarioEmpleador;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private AlertDialog dialogInfo;

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

    private void createFirebaseAccountWithEmailAndPassword(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            //Log.e(TAG, task.getException().toString());
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            Log.e(TAG, "signInWithPhone:failure -- " + errorCode);
                            switch (errorCode) {
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    alertDialogInfoError(0);
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    textInputLayoutPassword.setError("Error: la constraseña debe contener al menos 6 caracteres");
//                                    alertDialogInfoError(1);
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    textInputLayoutEmail.setError("Error: El correo electrónico ingresado es inválido.");
//                                    alertDialogInfoError(2);
                                    break;
                            }


//                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    public void alertDialogInfoError(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        switch (error) {
            case 0:/*email repetido*/
                textViewInfo.setText(getResources().getString(R.string.text_error_email_repetido));
                builder.setPositiveButton("Iniciar sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sign in the user ...

                                try {
//                            Intent intent = new Intent(RegWithEmailPasswordActivity.this, LoginEmailPasswordActivity.class);
                                    Intent intent = new Intent(RegistrarseConEmailPasswordActivity.this, LoginActivity.class);
//                            intent.putExtra("email", email);
//                            intent.putExtra("password", password);
                                    startActivity(intent);
                                    dialogInfo.dismiss();

                                } catch (Exception e) {

                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeProgressDialog();
                            }
                        });
                break;
            case 1:/*clave insegura*/
                textViewInfo.setText(getResources().getString(R.string.text_error_password));
                builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...

                        try {
                            dialogInfo.dismiss();
                        } catch (Exception e) {

                        }
                    }
                });
                break;
            case 2:/*email inválido*/
                textViewInfo.setText(getResources().getString(R.string.text_error_email));
                builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
                            dialogInfo.dismiss();

                        } catch (Exception e) {

                        }
                    }
                });
                break;
        }


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

        dialogInfo = builder.create();
        dialogInfo.show();

    }


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

                    empleador.setEmail(email);
                    usuarioEmpleador = empleador;

                    if (usuarioEmpleador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioEmpleador.getFotoPerfil().toString());
                        Cursor returnCursor = RegistrarseConEmailPasswordActivity
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
                                                    Toast.makeText(getApplicationContext(), RegistrarseConEmailPasswordActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                                    finishAffinity();
                                                    startActivity(new Intent(RegistrarseConEmailPasswordActivity.this, MainNavigationActivity.class));
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
                                                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                                                            finishAffinity();
                                                                            startActivity(new Intent(RegistrarseConEmailPasswordActivity.this, MainNavigationActivity.class));
                                                                        }
                                                                    }
                                                                });


                                                    } else {
                                                        // Handle failures

                                                    }
                                                }
                                            });


                                        } else {
                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, RegistrarseConEmailPasswordActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                        Toast.makeText(RegistrarseConEmailPasswordActivity.this, RegistrarseConEmailPasswordActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                            startActivity(new Intent(RegistrarseConEmailPasswordActivity.this, MainNavigationActivity.class));

                                        }
                                    }
                                });
                    }
                    break;
                case 2:
                    photoFlag = false;
                    trabajador.setEmail(email);
                    usuarioTrabajador = trabajador;

                    if (usuarioTrabajador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioTrabajador.getFotoPerfil().toString());
                        Cursor returnCursor = RegistrarseConEmailPasswordActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                                                    Toast.makeText(getApplicationContext(), RegistrarseConEmailPasswordActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                                    finishAffinity();
                                                    startActivity(new Intent(RegistrarseConEmailPasswordActivity.this, MainNavigationActivity.class));
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
                                                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                                                            finishAffinity();
                                                                            startActivity(new Intent(RegistrarseConEmailPasswordActivity.this, MainNavigationActivity.class));
                                                                        }
                                                                    }
                                                                });

                                                    } else {
                                                        // Handle failures

                                                    }
                                                }
                                            });


                                        } else {
                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, RegistrarseConEmailPasswordActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                        Toast.makeText(RegistrarseConEmailPasswordActivity.this, RegistrarseConEmailPasswordActivity.this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(RegistrarseConEmailPasswordActivity.this, "Su cuenta ha sido creada de manera exitosa!", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                            startActivity(new Intent(RegistrarseConEmailPasswordActivity.this, MainNavigationActivity.class));

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
        setContentView(R.layout.activity_registrarse_con_email_password);

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

        email = "";
        password = "";

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        buttonFinish = findViewById(R.id.buttonfinish);
        buttonFinish.setEnabled(false);
        buttonFinish.setOnClickListener(this);


        textInputLayoutEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();

                if (!email.isEmpty())
                    textInputLayoutEmail.setError(null);

//                if (email.isEmpty()) {
//                    textInputLayoutEmail.setError("Error: email inválido");
//                } else {
//                    textInputLayoutEmail.setError(null);
//                }

                if (!email.isEmpty() && !password.isEmpty()) {
                    buttonFinish.setEnabled(true);
                } else {
                    buttonFinish.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputLayoutPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();

                if (!password.isEmpty())
                    textInputLayoutPassword.setError(null);

                if (!email.isEmpty() && !password.isEmpty()) {
                    buttonFinish.setEnabled(true);
                } else {
                    buttonFinish.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onClick(View v) {

        String title = "Por favor espere";
        String message = "Cachuelito se encuentra verficando su información personal...";
        switch (v.getId()) {
            case R.id.buttonfinish:

                showProgress(title, message);
                createFirebaseAccountWithEmailAndPassword(email, password);


//                switch (regUsuario) {
//                    case 1:
//                        empleador.setEmail(email);
//                        usuarioEmpleador = empleador;
//                        Log.d(TAG, usuarioEmpleador.toString());
//
//
////                        Toast.makeText(getApplicationContext(), usuarioEmpleador.toString(), Toast.LENGTH_LONG).show();
//
//                        showProgress(title, message);
//                        createFirebaseAccountWithEmailAndPassword(email, password);
//
//
//                        break;
//                    case 2:
//                        trabajador.setEmail(email);
//                        usuarioTrabajador = trabajador;
//                        Log.d(TAG, usuarioTrabajador.toString());
////                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//
//                        showProgress(title, message);
//                        createFirebaseAccountWithEmailAndPassword(email, password);
//
//
//                        break;
//                }

                break;
        }
    }
}