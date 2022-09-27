package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class PocRegWithGoogleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PocRegWithGoogleActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private AlertDialog dialogInfo;
    private int regUsuario;

    private Empleador empleador;
    private Trabajador trabajador;

    private ArrayList<Trabajador> trabajadorListByEmail;
    private ArrayList<Empleador> empleadorListByEmail;
    private AlertDialog dialogInfoError;

    private NetworkTool networkTool;

    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences myPreferences;

    private boolean networkFlag;
    private boolean sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poc_reg_with_google);

        networkTool = new NetworkTool(this);
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        findViewById(R.id.buttonSelecctionaCuenta).setOnClickListener(this);
        findViewById(R.id.buttonInfo).setOnClickListener(this);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = PocRegWithGoogleActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }

        trabajadorListByEmail = new ArrayList<>();
        empleadorListByEmail = new ArrayList<>();

        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(this, trabajadors -> {
            if (trabajadors != null) {
                for (Trabajador tr : trabajadors) {
                    Log.d(TAG, tr.toString());
                    if (tr.getEmail() != null) {
                        trabajadorListByEmail.add(tr);
                    }
                }
            }
        });

        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getAllEmpleadores().observe(this, empleadors -> {
            if (empleadors != null) {
                for (Empleador em : empleadors) {
                    Log.d(TAG, em.toString());
                    if (em.getEmail() != null) {
                        empleadorListByEmail.add(em);
                    }
                }
            }
        });


    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
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
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, user.getEmail());
            Log.d(TAG, user.getDisplayName());
            switch (regUsuario) {
                case 1:
                    empleador.setEmail(user.getEmail());


                    AtomicBoolean estadoEmpleador = new AtomicBoolean(false);

                    String contactoVeri = "";
                    if (empleador.getEmail() != null) {
                        contactoVeri = empleador.getEmail();
                    }

                    for (Empleador e : empleadorListByEmail) {
                        if (e.getEmail() != null) {
                            if (e.getEmail().equals(contactoVeri)) {
                                estadoEmpleador.set(true);
                            }
                        }
                    }

                    for (Trabajador t : trabajadorListByEmail) {
                        if (t.getEmail() != null) {
                            if (t.getEmail().equals(contactoVeri)) {
                                estadoEmpleador.set(true);
                            }
                        }
                    }

                    if (estadoEmpleador.get()) {
//                        this.alertDialogInfoError(0);
                        this.alertDialogInfoIniciarSesion(0);

                    } else {

                        networkFlag = myPreferences.getBoolean("networkFlag", false);
                        sPref = defaultSharedPreferences.getBoolean("sync_network", true);

                        if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                            // AsyncTask subclass
                            //new DownloadXmlTask().execute(URL);

                            if (networkTool.isOnlineWithWifi()) {

                                registrarEnFirebase(user);

                            } else {
                                if (networkTool.isOnlineWithData()) {
                                    if (!sPref) {
                                        alertDialogContinuarRegistroConDatos();
                                    } else {

                                        networkTool.alertDialogNoConectadoWifiInfo();
                                    }
//                                        networkTool.alertDialogNoConectadoWifiInfo();
                                } else {
                                    networkTool.alertDialogNoConectadoInfo();
                                }
                            }
                        } else {
                            networkTool.alertDialogNoConectadoInfo();
                        }

                    }


                    break;
                case 2:
                    trabajador.setEmail(user.getEmail());

                    networkFlag = myPreferences.getBoolean("networkFlag", false);
                    sPref = defaultSharedPreferences.getBoolean("sync_network", true);


                    AtomicBoolean estadoTrabajador = new AtomicBoolean(false);

                    String contactoTrabajador = "";
                    if (trabajador.getEmail() != null) {
                        contactoTrabajador = trabajador.getEmail();
                    }

                    for (Empleador e : empleadorListByEmail) {
                        if (e.getEmail() != null) {
                            if (e.getEmail().equals(contactoTrabajador)) {
                                estadoTrabajador.set(true);
                            }
                        }
                    }

                    for (Trabajador t : trabajadorListByEmail) {
                        if (t.getEmail() != null) {
                            if (t.getEmail().equals(contactoTrabajador)) {
                                estadoTrabajador.set(true);
                            }
                        }
                    }

                    if (estadoTrabajador.get()) {
                        this.alertDialogInfoIniciarSesion(0);
                    } else {

                        if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                            // AsyncTask subclass
                            if (networkTool.isOnlineWithWifi()) {
                                registrarEnFirebase(user);
                            } else {
                                if (networkTool.isOnlineWithData()) {
                                    if (!sPref) {
                                        alertDialogContinuarRegistroConDatos();
                                    } else {
                                        networkTool.alertDialogNoConectadoWifiInfo();
                                    }
//                                        networkTool.alertDialogNoConectadoWifiInfo();
                                } else {
                                    networkTool.alertDialogNoConectadoInfo();
                                }
                            }
                        } else {
                            networkTool.alertDialogNoConectadoInfo();
                        }


                    }


                    break;
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelecctionaCuenta:
                signIn();
                break;
            case R.id.buttonInfo:
                dialogInfo = alertDialogInfoGoogle();
                dialogInfo.show();
                break;
        }
    }

    public AlertDialog alertDialogInfoGoogle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_reg_with_google));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(promptsView)
                // Add action buttons
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
                            dialogInfo.dismiss();
                        } catch (Exception e) {

                        }
//                        signIn();
                    }
                });
        return builder.create();
    }

    public void alertDialogContinuarRegistroConDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_error_conexion_internet_pero_si_datos));
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...

                registrarEnFirebase(FirebaseAuth.getInstance().getCurrentUser());


                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }
//                empleador.setFotoPerfil(null);

//                Intent intent = new Intent(RegWithEmailPasswordActivity.this, MainNavigationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }
            }
        });

        builder.setCancelable(false);


        dialogInfo = builder.create();
        dialogInfo.show();

    }


    public void alertDialogInfoIniciarSesion(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

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
//                            Intent intent = new Intent(RegWithGoogleActivity.this, LoginGoogleActivity.class);
                            try {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    mAuth.signOut();
                                }
                            } catch (Exception e) {

                            }
                            Intent intent = new Intent(PocRegWithGoogleActivity.this, LoginActivity.class);
//                            intent.putExtra("loginRev", "cuentaExiste");
                            startActivity(intent);
                            dialogInfoError.dismiss();

                        } catch (Exception e) {

                        }
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                mAuth.signOut();
                            }
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

        dialogInfoError = builder.create();
        dialogInfoError.show();

    }

    private void registrarEnFirebase(FirebaseUser firebaseUser) {
        boolean photoFlag = false;

        switch (regUsuario) {
            case 1:

                if (empleador.getFotoPerfil() != null) {
                    Uri returnUri = Uri.parse(empleador.getFotoPerfil().toString());
                    Cursor returnCursor = PocRegWithGoogleActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                empleador.setIdUsuario(firebaseUser.getUid());
                if (photoFlag) {
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                    closeProgress();
//                    title = "Por favor espere";
//                    message = "Su cuenta ya casi está lista!";
//                    showProgress(title, message);
                    empleador.registrarseEnFirebaseConFoto(PocRegWithGoogleActivity.this, 2);
                } else {
                    empleador.setFotoPerfil(null);
                    empleador.registrarseEnFirebase(PocRegWithGoogleActivity.this, 2);
                }

//                empleador.regEmpleadorEnFirebase(empleador, RegWithGoogleActivity.this, mAuth, 2);

                break;
            case 2:
                if (trabajador.getFotoPerfil() != null) {
                    Uri returnUri = Uri.parse(trabajador.getFotoPerfil().toString());
                    Cursor returnCursor = PocRegWithGoogleActivity.this.getContentResolver().query(returnUri, null, null, null, null);
                    /*
                     * Get the column indexes of the data in the Cursor,
                     * move to the first row in the Cursor, get the data,
                     * and display it.
                     */
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();


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
                trabajador.setIdUsuario(firebaseUser.getUid());
                trabajador.setCalificacion(0.5);
                if (photoFlag) {
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                    closeProgress();
//                    title = "Por favor espere";
//                    message = "Su cuenta ya casi está lista!";
//                    showProgress(title, message);
                    trabajador.registrarseEnFirebaseConFoto(PocRegWithGoogleActivity.this, 2);
                } else {
                    trabajador.setFotoPerfil(null);
                    trabajador.registrarseEnFirebase(PocRegWithGoogleActivity.this, 2);
                }
                break;
        }

    }


}