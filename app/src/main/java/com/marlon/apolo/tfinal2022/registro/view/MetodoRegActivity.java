/*
 * @(#)MetodoRegActivity.java        1.0 2022/09/30
 *
 * Copyright (C) 2022 Marlon Apolo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marlon.apolo.tfinal2022.registro.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.login.LoginEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.view.regMethod.RegistrarseConCelularActivity;
import com.marlon.apolo.tfinal2022.registro.view.regMethod.RegistrarseConEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.regMethod.RegistrarseConGoogleActivity;

/**
 * Esta clase permite seleccionar el método de autenticación para los usuarios que se
 * encuentran realizando el proceso de registro.
 *
 * @author Marlon Apolo
 * @version 1.0 30 Sep 2022
 */
public class MetodoRegActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MetodoRegActivity.class.getSimpleName();
    private Button buttonNext;
    private Dialog dialogInfo;
    private int optionReg;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private int colorTheme;

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

    /**
     * Este método permite inicializar los componentes de la interfaz gráfica para la
     * selección del método de autenticación
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_metodo_reg);
        hideSystemBars();
        setContentView(R.layout.activity_metodo_reg_poc);

        optionReg = 0;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        findViewById(R.id.radioBtnCorreoPassword).setOnClickListener(this);
//        findViewById(R.id.radioBtnGoogle).setOnClickListener(this);
//        findViewById(R.id.radioBtnCelular).setOnClickListener(this);
//
//        findViewById(R.id.buttonInfo).setOnClickListener(this);
//
//        buttonNext = findViewById(R.id.buttonNext);
//        buttonNext.setEnabled(false);
//        buttonNext.setOnClickListener(this);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                Log.d(TAG, empleador.toString());
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                Log.d(TAG, trabajador.toString());
                break;
        }
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//

        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorTheme = typedValue.data;
        /*Esto es una maravilla*/


        buttonNext = findViewById(R.id.buttonNext);
//        buttonRecord = findViewById(R.id.buttonRecordPolicial);
//
//        findViewById(R.id.buttonInfo).setOnClickListener(this);
        findViewById(R.id.cardViewEmailPass).setOnClickListener(this);
        ImageView imageViewEmail = findViewById(R.id.imgViewEmailPass);
        imageViewEmail.setColorFilter(colorTheme);

        findViewById(R.id.cardViewGoogle).setOnClickListener(this);
        ImageView imageViewGoogle = findViewById(R.id.imgViewGoogle);
        imageViewGoogle.setColorFilter(colorTheme);

        findViewById(R.id.cardViewPhone).setOnClickListener(this);
        ImageView imageViewPhone = findViewById(R.id.imgViewPhone);
        imageViewPhone.setColorFilter(colorTheme);
//        buttonRecord.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
//        imageButtonFotoRecord.setOnClickListener(this);
//
        buttonNext.setEnabled(false);

        SharedPreferences myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        int u = myPreferences.getInt("methodTemp", -1);
        try {
            switch (u) {
                case 1:
//                    ((RadioButton) findViewById(R.id.radioBtnCorreoPassword)).setChecked(true);
                    optionReg = 1;
                    buttonNext.setEnabled(true);
                    break;
                case 2:
//                    ((RadioButton) findViewById(R.id.radioBtnGoogle)).setChecked(true);
                    optionReg = 2;
                    buttonNext.setEnabled(true);
                    break;
                case 3:
//                    ((RadioButton) findViewById(R.id.radioBtnCelular)).setChecked(true);
                    optionReg = 3;
                    buttonNext.setEnabled(true);
                    break;
            }
        } catch (Exception e) {

        }
        String email = myPreferences.getString("emailTemp", null);
        String password = myPreferences.getString("passTemp", null);
//            int u = myPreferences.getInt("usuario", -1);
//            if (u == 0) {
//                findViewById(R.id.radioBtnGoogle).setVisibility(View.GONE);
////                Toast.makeText(getApplicationContext(), "Admin", Toast.LENGTH_LONG).show();
//            }
//        }
    }

    /**
     * Este método permite escuchar el evento click en 3 radioButton. Cada radioButton informa
     * al usuario el tipo de método de autenticación con el cuál puede registrarse.
     * <p>
     * El método tambièn permite escuchar el evento click del botón Siguiente y el evento
     * click del botón de información.
     *
     * @param v objeto View que permite identificar el id del botón que ha sido presionado por el usuario.
     */
    @Override
    public void onClick(View v) {
        // Check to see if a button has been clicked.
        try {
            //boolean checked = ((RadioButton) v).isChecked();
            // Check which radio button was clicked.
            switch (v.getId()) {
                case R.id.cardViewEmailPass:
                case R.id.imgViewEmailPass:
                    Intent intentEmail = new Intent(MetodoRegActivity.this, RegWithEmailPasswordActivity.class);
//                    Intent intentEmail = new Intent(MetodoRegActivity.this, RegWithEmailPocActivity.class);
//                    Intent intentEmail = new Intent(MetodoRegActivity.this, RegistrarseConEmailPasswordActivity.class);

                    intentEmail.putExtra("usuario", regUsuario);
                    switch (regUsuario) {
                        case 1:
                            intentEmail.putExtra("empleador", empleador);
                            break;
                        case 2:
                            intentEmail.putExtra("trabajador", trabajador);
                            break;
                    }
                    startActivity(intentEmail);
                    break;
                case R.id.cardViewGoogle:
                case R.id.imgViewGoogle:
                    Intent intentGoogle = new Intent(MetodoRegActivity.this, RegWithGoogleActivity.class);
//                    Intent intentGoogle = new Intent(MetodoRegActivity.this, RegistrarseConGoogleActivity.class);
                    intentGoogle.putExtra("usuario", regUsuario);
                    switch (regUsuario) {
                        case 1:
                            intentGoogle.putExtra("empleador", empleador);
                            break;
                        case 2:
                            intentGoogle.putExtra("trabajador", trabajador);
                            break;
                    }
                    startActivity(intentGoogle);
                    break;
                case R.id.cardViewPhone:
                case R.id.imgViewPhone:
                    Intent intentCelular = new Intent(MetodoRegActivity.this, RegWithCelularActivity.class);
//                    Intent intentCelular = new Intent(MetodoRegActivity.this, RegistrarseConCelularActivity.class);
                    intentCelular.putExtra("usuario", regUsuario);
                    switch (regUsuario) {
                        case 1:
                            intentCelular.putExtra("empleador", empleador);
                            break;
                        case 2:
                            intentCelular.putExtra("trabajador", trabajador);
                            break;
                    }
                    startActivity(intentCelular);
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        try {
            if (v.getId() == R.id.buttonInfo) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    SharedPreferences myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editorPref = myPreferences.edit();
                    int u = myPreferences.getInt("usuario", -1);
                    if (u == 0) {
                        dialogInfo = alertDialogInfoAdmin();
                        dialogInfo.show();
                    }
                } else {
                    dialogInfo = alertDialogInfo();
                    dialogInfo.show();
                }

            }
        } catch (Exception e) {

        }
        try {
            if (v.getId() == R.id.buttonNext) {
                switch (optionReg) {
                    case 1:
                        Intent intentEmail = new Intent(MetodoRegActivity.this, RegWithEmailPasswordActivity.class);
//                        Intent intentEmail = new Intent(MetodoRegActivity.this, RegWithEmailPocActivity.class);
                        intentEmail.putExtra("usuario", regUsuario);
                        switch (regUsuario) {
                            case 1:
                                intentEmail.putExtra("empleador", empleador);
                                break;
                            case 2:
                                intentEmail.putExtra("trabajador", trabajador);
                                break;
                        }
                        startActivity(intentEmail);
                        break;
                    case 2:
                        Intent intentGoogle = new Intent(MetodoRegActivity.this, RegWithGoogleActivity.class);
                        intentGoogle.putExtra("usuario", regUsuario);
                        switch (regUsuario) {
                            case 1:
                                intentGoogle.putExtra("empleador", empleador);
                                break;
                            case 2:
                                intentGoogle.putExtra("trabajador", trabajador);
                                break;
                        }
                        startActivity(intentGoogle);
                        break;
                    case 3:
                        Intent intentCelular = new Intent(MetodoRegActivity.this, RegWithCelularActivity.class);
                        intentCelular.putExtra("usuario", regUsuario);
                        switch (regUsuario) {
                            case 1:
                                intentCelular.putExtra("empleador", empleador);
                                break;
                            case 2:
                                intentCelular.putExtra("trabajador", trabajador);
                                break;
                        }
                        startActivity(intentCelular);
                        break;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }


    /**
     * Este método devuelve un objeto del tipo Dialog que contiene información acerca
     * de los diferentes métodos de autenticación (email-password, cuenta de Google, número celular)
     *
     * @return un objeto AlertDialog.Bulder que continene información acerca de los
     * 3 métodos de autenticación.
     */
    public Dialog alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_info, null))
                // Add action buttons
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            dialogInfo.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                });
        return builder.create();
    }

    public Dialog alertDialogInfoAdmin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_info_admin, null))
                // Add action buttons
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            dialogInfo.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                });
        return builder.create();
    }
}