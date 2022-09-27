package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.receivers.NetworkReceiver;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegWithEmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegWithEmailPasswordActivity.class.getSimpleName();
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private Button buttonFinish;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private String email;
    private String password;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private Dialog dialogInfo;
    private Dialog dialogInfoError;
    private Dialog dialogInfoEmailVeri;
    private ArrayList<Empleador> empleadors;


    //    private TextView textViewContador;
    private boolean isWifiConn;
    private boolean isMobileConn;
    private Dialog dialogInfoInternet;
    private SharedPreferences defaultSharedPreferences;
    private boolean networkFlag;
    private SharedPreferences myPreferences;
    private Usuario usuarioTrabajador;
//    private ContadorAyncTask contadorAyncTask;


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

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
    public static boolean sPref = true;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver;

    private NetworkTool networkTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_with_email_password);

        networkTool = new NetworkTool(this);

        empleadors = new ArrayList<>();

        // Gets the user's network preference settings
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = RegWithEmailPasswordActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


//        textViewContador = findViewById(R.id.textViewContador);
//        startContador();
//        checkInternetConnection();


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
        buttonFinish = findViewById(R.id.buttonfinish);
        buttonFinish.setEnabled(false);
        buttonFinish.setOnClickListener(this);

        findViewById(R.id.buttonInfo).setOnClickListener(this);

        email = "";
        password = "";

        textInputEditTextEmail = findViewById(R.id.editTextEmail);
        textInputEditTextPassword = findViewById(R.id.editTextPassword);


        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }

        textInputEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
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

        textInputEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
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

//        Toast.makeText(getApplicationContext(), empleador.getNombre() + empleador.getApellido(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), empleador.getEmail(), Toast.LENGTH_SHORT).show();

    }

    private void checkInternetConnection() {
        String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isMobileConn = networkInfo.isConnected();


        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
        //Toast.makeText(getApplicationContext(), String.format("Wifi connected %b", isWifiConn), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), String.format("Mobile connected %s", String.valueOf(isMobileConn)), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonfinish:
                switch (regUsuario) {
                    case 1:
                        empleador.setEmail(email);
//                    Toast.makeText(getApplicationContext(), empleador.getNombre() + empleador.getApellido(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), empleador.getEmail(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), empleador.getFotoPerfil(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), empleador.toString(), Toast.LENGTH_SHORT).show();
                        String title = "Por favor espere";
                        String message = "Cachuelito se encuentra verificando su información personal...";
//                    showProgress(title, message);

                        AtomicBoolean estadoEmpleador = new AtomicBoolean(false);
                        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(RegWithEmailPasswordActivity.this).get(EmpleadorViewModel.class);

                        String contactoVeri = "";
                        if (empleador.getEmail() != null) {
                            contactoVeri = empleador.getEmail();
                        }

                        for (Empleador e : empleadors) {
                            if (e.getEmail() != null) {
                                if (e.getEmail().equals(contactoVeri)) {
                                    estadoEmpleador.set(true);
                                }
                            }
                        }

                        if (estadoEmpleador.get()) {
                            this.alertDialogInfoError(0);
//                            Toast.makeText(RegWithEmailPasswordActivity.this, "El empleador no puede ser registrado. Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
                        } else {

                            networkFlag = myPreferences.getBoolean("networkFlag", false);
                            sPref = defaultSharedPreferences.getBoolean("sync_network", true);

//                            Toast.makeText(getApplicationContext(), String.format("Preferencia: %s", String.valueOf(sPref)), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), String.format("Red: %s", String.valueOf(networkFlag)), Toast.LENGTH_SHORT).show();


                            if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                                // AsyncTask subclass
                                //new DownloadXmlTask().execute(URL);

                                if (networkTool.isOnlineWithWifi()) {
                                    //empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
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
                        trabajador.setEmail(email);
                        //Toast.makeText(getApplicationContext(), "Funciona Trabajador", Toast.LENGTH_SHORT).show();

                        //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
//                        trabajador.crearCuentaConEmailYPassword();

                        usuarioTrabajador = new Trabajador();
                        usuarioTrabajador = trabajador;
//                        usuario.registrarseEnFirebase(this);


                        networkFlag = myPreferences.getBoolean("networkFlag", false);
                        sPref = defaultSharedPreferences.getBoolean("sync_network", true);

//                            Toast.makeText(getApplicationContext(), String.format("Preferencia: %s", String.valueOf(sPref)), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), String.format("Red: %s", String.valueOf(networkFlag)), Toast.LENGTH_SHORT).show();


                        if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                            // AsyncTask subclass
                            //new DownloadXmlTask().execute(URL);

                            if (networkTool.isOnlineWithWifi()) {
                                usuarioTrabajador.registrarseEnFirebase(RegWithEmailPasswordActivity.this,1);
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


                        break;
                }
                break;

            case R.id.buttonInfo:
                dialogInfo = alertDialogInfo();
                dialogInfo.show();
                break;
        }


    }

//    public void startContador() {
//        // Put a message in the text view.
////        mTextView.setText(R.string.napping);
//
//        // Start the AsyncTask.
//        // The AsyncTask has a callback that will update the text view.
//        contadorAyncTask = new ContadorAyncTask(textViewContador);
//        contadorAyncTask.execute();
//        contadorAyncTask.setOnItemClickListener(new ContadorAyncTask.ClickListener() {
//            @Override
//            public void onTokenListener(String token) {
//                if (token.length() > 0) {
//                    Toast.makeText(getApplicationContext(), "Por favor el tiempo de espera se ha terminado, inténtelo mas tarde", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public Dialog alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_reg_email_password));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons
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
        return builder.create();
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
                            Intent intent = new Intent(RegWithEmailPasswordActivity.this, LoginActivity.class);
//                            intent.putExtra("email", email);
//                            intent.putExtra("password", password);
                            startActivity(intent);
                            dialogInfoError.dismiss();

                        } catch (Exception e) {

                        }
                    }
                })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
                            dialogInfoError.dismiss();
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
                            dialogInfoError.dismiss();

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


    public void alertDialogEmailVerificationInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_email_verification));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfoEmailVeri.dismiss();
                } catch (Exception e) {

                }

                Intent intent = new Intent(RegWithEmailPasswordActivity.this, MainNavigationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setCancelable(false);


        dialogInfoEmailVeri = builder.create();
        dialogInfoEmailVeri.show();

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
                switch (regUsuario) {
                    case 1:
                        //empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
                        break;
                    case 2:
                        usuarioTrabajador.registrarseEnFirebase(RegWithEmailPasswordActivity.this,1);
                        break;
                }
                try {
                    dialogInfoInternet.dismiss();
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

            }
        });

        builder.setCancelable(false);


        dialogInfoInternet = builder.create();
        dialogInfoInternet.show();

    }

    public void blockbuttonFinalizar() {
        buttonFinish.setEnabled(false);
    }

    public void unlockbuttonFinalizar() {
        buttonFinish.setEnabled(true);
    }

    public void controlErrorsUI() {
        try {
            unlockbuttonFinalizar();
        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // Unregisters BroadcastReceiver when app is destroyed.
            if (receiver != null) {
                this.unregisterReceiver(receiver);
            }
        } catch (Exception e) {

        }

    }

}