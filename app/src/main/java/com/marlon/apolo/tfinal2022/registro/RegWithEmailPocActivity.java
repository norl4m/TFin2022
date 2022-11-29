package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.config.ConfiguracionActivity;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.receivers.NetworkReceiver;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegWithEmailPocActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegWithEmailPocActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences myPreferences;
    private List<Trabajador> trabajadorListByEmail;
    private List<Empleador> empleadorListByEmail;
    private int regUsuario;

    private Empleador empleador;
    private Trabajador trabajador;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private String email;
    private String password;
    private Button buttonFinish;

    private Dialog dialogInfo;

    private Usuario usuarioTrabajador;
    private Usuario usuarioEmpleador;

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL = "http://stackoverflow.com/feeds/tag?tagnames=android&sort;=newest";

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
    public static boolean sPref;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
    private NetworkTool networkTool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_with_email_poc);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        networkTool = new NetworkTool(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        // Gets the user's network preference settings
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = RegWithEmailPocActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


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

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }

        SharedPreferences myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();


        textInputEditTextEmail = findViewById(R.id.editTextEmail);
        textInputEditTextPassword = findViewById(R.id.editTextPassword);

        buttonFinish = findViewById(R.id.buttonfinish);
        buttonFinish.setEnabled(false);
        buttonFinish.setOnClickListener(this);
        findViewById(R.id.buttonInfo).setOnClickListener(this);

        try {
            String emailTemp = myPreferences.getString("emailTemp", null);
            textInputEditTextEmail.setText(emailTemp);
            email = emailTemp;
            String passwordTemp = myPreferences.getString("passTemp", null);
            textInputEditTextPassword.setText(passwordTemp);
            password = passwordTemp;
            buttonFinish.setEnabled(true);
        } catch (Exception e) {

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
    }


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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonfinish:
                switch (regUsuario) {
                    case 1:
                        empleador.setEmail(email);
                        usuarioEmpleador = new Empleador();
                        usuarioEmpleador = empleador;

                        AtomicBoolean estadoEmpleador = new AtomicBoolean(false);
                        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(RegWithEmailPocActivity.this).get(EmpleadorViewModel.class);

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
                            this.alertDialogInfoError(0);
                        } else {
//                            createAccount(usuarioEmpleador.getEmail(), password);
                            Toast.makeText(getApplicationContext(), "GG-EMPLEADOR", Toast.LENGTH_LONG).show();
                        }

                        break;
                    case 2:
                        trabajador.setEmail(email);
                        usuarioTrabajador = new Trabajador();
                        usuarioTrabajador = trabajador;


                        AtomicBoolean estadoTrabajador = new AtomicBoolean(false);

                        String contactoTrabajador = "";
                        if (usuarioTrabajador.getEmail() != null) {
                            contactoTrabajador = usuarioTrabajador.getEmail();
                        }

                        try {
                            for (Empleador e : empleadorListByEmail) {
                                if (e.getEmail() != null) {
                                    if (e.getEmail().equals(contactoTrabajador)) {
                                        estadoTrabajador.set(true);
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }

                        try {
                            for (Trabajador t : trabajadorListByEmail) {
                                if (t.getEmail() != null) {
                                    if (t.getEmail().equals(contactoTrabajador)) {
                                        estadoTrabajador.set(true);
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }


                        if (estadoTrabajador.get()) {
                            this.alertDialogInfoError(0);
                        } else {
                            Toast.makeText(getApplicationContext(), "GG-TRABAJADOR", Toast.LENGTH_LONG).show();
//                            createAccount(usuarioTrabajador.getEmail(), password);

                            boolean networkFlag = myPreferences.getBoolean("networkFlag", false);
                            sPref = defaultSharedPreferences.getBoolean("sync_network", true);

                            Log.d(TAG, "Preference (Wifi=true)");
                            Log.d(TAG, "Preference (Wifi-Any=false)");
                            Log.d(TAG, "************************************************");
                            Log.d(TAG, "Preference(Wifi-Any): " + String.valueOf(sPref));
                            Log.d(TAG, "Red: " + String.valueOf(networkFlag));

                            if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {

                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


                                boolean isMetered = cm.isActiveNetworkMetered();
                                Log.d(TAG, "isConnected: " + String.valueOf(isConnected));
                                Log.d(TAG, "isMetered: " + String.valueOf(isMetered));


                                if (networkTool.isOnlineWithDataAndWifi()) {
                                    //Toast.makeText(getApplicationContext(), "Datos móviles o Wifi", Toast.LENGTH_LONG).show();
//                                    if (networkTool.isOnlineWithWifi()) {
//                                        Toast.makeText(getApplicationContext(), "Wifi", Toast.LENGTH_LONG).show();
//                                        Toast.makeText(getApplicationContext(), "Continuar", Toast.LENGTH_LONG).show();
////
//                                    }


                                    if (isMetered) {
                                        //Toast.makeText(getApplicationContext(), "Datos móviles", Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(), "Pueden existir valores de cobro acorde con su operadora móvil", Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(), "Le recomendamos utilizar una red Wifi para evitar cobros de saldo innecesarios", Toast.LENGTH_LONG).show();
                                    } else {

                                    }


//                                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//
//
//                                    boolean isMetered = cm.isActiveNetworkMetered();
//                                    Log.d(TAG, String.valueOf(isConnected));
//                                    Log.d(TAG, String.valueOf(isMetered));

//
//                                    if (networkTool.isOnlineWithData()) {
//                                        Toast.makeText(getApplicationContext(), "Datos móviles", Toast.LENGTH_LONG).show();
//                                        Toast.makeText(getApplicationContext(), "Pueden existir valores de cobro acorde con su operadora móvil", Toast.LENGTH_LONG).show();
//                                        Toast.makeText(getApplicationContext(), "Le recomendamos utilizar una red Wifi para evitar cobros de saldo innecesarios", Toast.LENGTH_LONG).show();
//
//                                    }
                                }
//                                else {
////                                    if (networkTool.isOnlineWithData()) {
////                                        if (!sPref) {
////                                            alertDialogContinuarRegistroConDatos();
////                                        } else {
////
////                                            networkTool.alertDialogNoConectadoWifiInfo();
////                                        }
//////                                        networkTool.alertDialogNoConectadoWifiInfo();
////                                    } else {
////
////                                        networkTool.alertDialogNoConectadoInfo();
//                                }

                            } else {
                                networkTool.alertDialogNoConectadoInfo();
                            }


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
                                    Intent intent = new Intent(RegWithEmailPocActivity.this, LoginActivity.class);
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
//                                closeProgress();
//                                closeCustomAlertDialog();

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

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

//    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
//    public void loadPage() {
//        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
//                || ((sPref.equals(WIFI)) && (wifiConnected))) {
//            // AsyncTask subclass
////            new DownloadXmlTask().execute(URL);
//            Toast.makeText(getApplicationContext(), "LOAD", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "ERRORRRR", Toast.LENGTH_LONG).show();
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    // Refreshes the display if the network connection and the
    // pref settings allow it.

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Gets the user's network preference settings
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//        // Retrieves a string value for the preferences. The second parameter
//        // is the default value to use if a preference value is not found.
//        sPref = sharedPrefs.getString("listPref", "Wi-Fi");
//
//        updateConnectedFlags();
//
//        if (refreshDisplay) {
//            loadPage();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(RegWithEmailPocActivity.this, ConfiguracionActivity.class));
//                startActivity(new Intent(MainNavigationActivity.this, SettingsActivity.class));
                break;
//            case R.id.mnu_nav_search:
//                startActivity(new Intent(RegWithEmailPocActivity.this, BuscadorActivity.class));
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.mnu_nav_search);
        search.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
}