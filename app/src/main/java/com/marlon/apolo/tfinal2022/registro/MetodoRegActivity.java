package com.marlon.apolo.tfinal2022.registro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;

public class MetodoRegActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MetodoRegActivity.class.getSimpleName();
    private Button buttonNext;
    private Dialog dialogInfo;
    private int optionReg;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_reg);

        optionReg = 0;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.radioBtnCorreoPassword).setOnClickListener(this);
        findViewById(R.id.radioBtnGoogle).setOnClickListener(this);
        findViewById(R.id.radioBtnCelular).setOnClickListener(this);

        findViewById(R.id.buttonInfo).setOnClickListener(this);

        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(this);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        Toast.makeText(getApplicationContext(), "Metodo registro", Toast.LENGTH_LONG).show();

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
//                Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, trabajador.toString());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        // Check to see if a button has been clicked.
        try {
            boolean checked = ((RadioButton) v).isChecked();
            // Check which radio button was clicked.
            switch (v.getId()) {
                case R.id.radioBtnCorreoPassword:
                    if (checked) {
                        optionReg = 1;
                        buttonNext.setEnabled(true);
//                        Toast.makeText(getApplicationContext(), "Email y password", Toast.LENGTH_SHORT).show();
                    }
                    // Code for same day service ...
                    break;
                case R.id.radioBtnGoogle:
                    if (checked) {
                        optionReg = 2;
                        buttonNext.setEnabled(true);
//                        Toast.makeText(getApplicationContext(), "Google", Toast.LENGTH_SHORT).show();
                    }
                    // Code for next day delivery ...
                    break;
                case R.id.radioBtnCelular:
                    if (checked) {
                        optionReg = 3;
                        buttonNext.setEnabled(true);
//                        Toast.makeText(getApplicationContext(), "Celular", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (Exception e) {

        }

        try {
            if (v.getId() == R.id.buttonInfo) {
                //Toast.makeText(getApplicationContext(), "Info", Toast.LENGTH_LONG).show();
                dialogInfo = alertDialogInfo();
                dialogInfo.show();
            }
        } catch (Exception e) {

        }
        try {
            if (v.getId() == R.id.buttonNext) {
//                Toast.makeText(getApplicationContext(), "Siguiente", Toast.LENGTH_LONG).show();

                switch (optionReg) {
                    case 1:
//                        Intent intentEmail = new Intent(MetodoRegActivity.this, RegWithEmailPasswordActivity.class);
                        Intent intentEmail = new Intent(MetodoRegActivity.this, EmailPasswordActivity.class);
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
//                        Intent intentGoogle = new Intent(MetodoRegActivity.this, PocRegWithGoogleActivity.class);
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

        }

    }

    public Dialog alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

//        View promptsView = inflater.inflate(R.layout.dialog_info, null);
//        builder.setView(promptsView);
//
//        // set prompts.xml to alertdialog builder
//        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
//        textViewInfo.setText(getResources().getString(R.string.text_info_metodo_reg));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_info, null))
                // Add action buttons
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
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
}