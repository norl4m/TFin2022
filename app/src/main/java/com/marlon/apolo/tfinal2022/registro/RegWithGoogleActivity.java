package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArraySet;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.login.LoginGoogleActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.receivers.NetworkReceiver;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegWithGoogleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    private Dialog dialogInfo;

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;

    private Button buttonSelectAccount;
    private Dialog dialogInfoVerif;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private ArrayList<Empleador> empleadors;
    private Dialog dialogInfoError;
    private ProgressDialog progressDialog;
    private Dialog dialogInfoInternet;
    private ArrayList<Trabajador> trabajadorListByEmail;
    private ArrayList<Empleador> empleadorListByEmail;
    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences myPreferences;
    private boolean networkFlag;
    private boolean sPref;
    private NetworkTool networkTool;
    private Usuario usuarioReg;
    private NetworkReceiver receiver;
    private AlertDialog alertDialogVar;
    private String title;
    private String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_with_google);

        empleadors = new ArrayList<>();
        trabajadorListByEmail = new ArrayList<>();
        empleadorListByEmail = new ArrayList<>();
        networkTool = new NetworkTool(this);


        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        // Gets the user's network preference settings
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = RegWithGoogleActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        FirebaseDatabase.getInstance().getReference().child("empleadores")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        empleadors = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            try {
                                Empleador empleador = data.getValue(Empleador.class);
                                empleadors.add(empleador);
                            } catch (Exception e) {

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialogInfo = alertDialogInfo();
        dialogInfo.show();


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        buttonSelectAccount = findViewById(R.id.buttonSelecctionaCuenta);
        buttonSelectAccount.setOnClickListener(this);

        buttonSelectAccount.setEnabled(false);
        buttonSelectAccount.setVisibility(View.GONE);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }


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

        findViewById(R.id.buttonInfo).setOnClickListener(this);
    }


    public void alertDialogCont() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_error_conexion_internet));
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfoInternet.dismiss();
                } catch (Exception e) {

                }
                switch (regUsuario) {
                    case 1:
                        empleador.setFotoPerfil(null);
                        break;
                    case 2:
                        trabajador.setFotoPerfil(null);
                        break;
                }
                signIn();

//                empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
//                empleador.setFotoPerfil(null);
//                Intent intent = new Intent(RegWithEmailPasswordActivity.this, MainNavigationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setCancelable(false);


        dialogInfoInternet = builder.create();
        dialogInfoInternet.show();

    }


    public boolean isOnlineWithWifi() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean online = false;


//        if (sPref && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            online = true;
        }


//        return (networkInfo != null && networkInfo.isConnected());
        return online;
    }

    public boolean isOnlineWithData() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean online = false;


        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            online = true;
        }


//        return (networkInfo != null && networkInfo.isConnected());
        return online;
    }

    public Dialog alertDialogInfo() {
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


                        if (isOnlineWithWifi()) {
                            signIn();
                        } else {
                            if (isOnlineWithData()) {
                                alertDialogCont();
                            } else {
                                Toast.makeText(getApplicationContext(), "Parece que no se encuentra conectado a Internet.", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
        return builder.create();
    }

    public void showCustomProgressDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.custom_progress_dialog, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
//        return builder.create();
        final TextView textViewTitle = promptsView.findViewById(R.id.textViewTitle);
        final TextView textViewMessage = promptsView.findViewById(R.id.textViewMessage);

        textViewTitle.setText(title);
        textViewMessage.setText(message);

        alertDialogVar = builder.create();
        alertDialogVar.show();
//        builder.show();
    }


    public void closeCustomAlertDialog() {
        try {
            alertDialogVar.dismiss();
        } catch (Exception e) {

        }
    }
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        title = "Por favor espere";
        message = "Cachuelito se encuentra verificando su información personal...";
//
//        showProgress(title, message);
        showCustomProgressDialog(title, message);
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
                            closeCustomAlertDialog();
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        signInIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivityForResult(signInIntent, RC_SIGN_IN);

        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(result.getPendingIntent().getIntentSender(), RC_SIGN_IN,
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
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                        closeProgressDialog();
                    }
                });
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            switch (regUsuario) {
                case 1:
                    empleador.setEmail(user.getEmail());

//                    AtomicBoolean estadoEmpleador = new AtomicBoolean(false);
//                    EmpleadorViewModel empleadorViewModel = new ViewModelProvider(RegWithGoogleActivity.this).get(EmpleadorViewModel.class);
//
//                    String contactoVeri = "";
//                    if (empleador.getEmail() != null) {
//                        contactoVeri = empleador.getEmail();
//                    }
//
//                    for (Empleador e : empleadors) {
//                        if (e.getEmail() != null) {
//                            if (e.getEmail().equals(contactoVeri)) {
//                                estadoEmpleador.set(true);
//                            }
//                        }
//                    }
//
//                    if (estadoEmpleador.get()) {
//                        this.alertDialogInfoIniciarSesion(0);
////                            Toast.makeText(RegWithEmailPasswordActivity.this, "El empleador no puede ser registrado. Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
//                    } else {
//                        if (empleador.getFotoPerfil() != null) {
//                            empleador.registrarEmpleadorConFoto(Uri.parse(empleador.getFotoPerfil()), "empleadores", mAuth, RegWithGoogleActivity.this, empleador, 2);
//                        } else {
//                            empleador.regEmpleadorEnFirebase(empleador, RegWithGoogleActivity.this, mAuth, 2);
//
//                        }
//                    }


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


//                        if (sPref) {
//                            // AsyncTask subclass
//                            //new DownloadXmlTask().execute(URL);
//
//                            if (networkTool.isOnlineWithWifi()) {
//
//                                registrarEnFirebase(user);
//
//                            } else {
////                                if (networkTool.isOnlineWithData()) {
////                                    if (!sPref) {
////                                        alertDialogContinuarRegistroConDatos();
////                                    } else {
////
////                                        networkTool.alertDialogNoConectadoWifiInfo();
////                                    }
//////                                        networkTool.alertDialogNoConectadoWifiInfo();
////                                } else {
////                                    Log.d(TAG, "###############################");
////                                    Log.d(TAG, "Akaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
////                                    Log.d(TAG, "###############################");
////                                    networkTool.alertDialogNoConectadoInfo();
////                                }
//                            }
//                        } else {
//                            Log.d(TAG, "###############################");
//                            Log.d(TAG, "Akiiiiiiiiiiiiiiiiiiiiiiiii");
//                            Log.d(TAG, "###############################");
//
//                            Log.d(TAG, String.valueOf(networkFlag));
//                            Log.d(TAG, String.valueOf(sPref));
//
//                            if (networkTool.isOnlineWithData()) {
//                                if (!sPref) {
//                                    alertDialogContinuarRegistroConDatos();
//                                } else {
//
//                                    networkTool.alertDialogNoConectadoWifiInfo();
//                                }
////                                        networkTool.alertDialogNoConectadoWifiInfo();
//                            } else {
//                                Log.d(TAG, "###############################");
//                                Log.d(TAG, "Akaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//                                Log.d(TAG, "###############################");
//                                networkTool.alertDialogNoConectadoInfo();
//                            }
////                            networkTool.alertDialogNoConectadoInfo();
//                        }

                    }


                    break;
                case 2:
                    trabajador.setEmail(user.getEmail());

//                    networkFlag = myPreferences.getBoolean("networkFlag", false);
//                    sPref = defaultSharedPreferences.getBoolean("sync_network", true);
//

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
                        networkFlag = myPreferences.getBoolean("networkFlag", false);
                        sPref = defaultSharedPreferences.getBoolean("sync_network", true);


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

    private void registrarEnFirebase(FirebaseUser firebaseUser) {
        boolean photoFlag = false;

        switch (regUsuario) {
            case 1:

                if (empleador.getFotoPerfil() != null) {
                    Uri returnUri = Uri.parse(empleador.getFotoPerfil().toString());
                    Cursor returnCursor = RegWithGoogleActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                    empleador.registrarseEnFirebaseConFoto(RegWithGoogleActivity.this, 2);
                } else {
                    empleador.setFotoPerfil(null);
                    empleador.registrarseEnFirebase(RegWithGoogleActivity.this, 2);
                }

//                empleador.regEmpleadorEnFirebase(empleador, RegWithGoogleActivity.this, mAuth, 2);

                break;
            case 2:
                if (trabajador.getFotoPerfil() != null) {
                    Uri returnUri = Uri.parse(trabajador.getFotoPerfil().toString());
                    Cursor returnCursor = RegWithGoogleActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                    trabajador.registrarseEnFirebaseConFoto(RegWithGoogleActivity.this, 2);
                } else {
                    trabajador.setFotoPerfil(null);
                    trabajador.registrarseEnFirebase(RegWithGoogleActivity.this, 2);
                }
                break;
        }

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
                            Intent intent = new Intent(RegWithGoogleActivity.this, LoginActivity.class);
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


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//            }

            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token.");
//                    Toast.makeText(getApplicationContext(), "Token", Toast.LENGTH_SHORT).show();


                    firebaseAuthWithGoogle(idToken);
                }
            } catch (ApiException e) {
                // ...
//                String errorCode = ((FirebaseAuthException) Objects.requireNonNull(e.getException())).getErrorCode();
                Log.w(TAG, "signInWithPhone:failure -- " + e.toString());
                Log.w(TAG, "signInWithPhone:failure -- " + e.getMessage());
                Log.w(TAG, "signInWithPhone:failure -- " + e.getStatus());

                Toast.makeText(getApplicationContext(), "Por favor seleccione una cuenta de Google para completar su registro", Toast.LENGTH_SHORT).show();
                buttonSelectAccount.setEnabled(true);
                buttonSelectAccount.setVisibility(View.VISIBLE);

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
    // [END onactivityresult]

    public void alertDialogVerificationInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_google_verification));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfoVerif.dismiss();
                } catch (Exception e) {

                }

                Intent intent = new Intent(RegWithGoogleActivity.this, MainNavigationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setCancelable(false);


        dialogInfoVerif = builder.create();
        dialogInfoVerif.show();

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
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

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
                        signIn();
                    }
                });
        return builder.create();
    }

}