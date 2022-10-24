package com.marlon.apolo.tfinal2022.registro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.view.RegFotoPerfilActivity;

public class RegNombreUsuarioActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonNext;
    private Dialog dialogInfo;
    TextInputEditText textInputEditTextNombre;
    TextInputEditText textInputEditTextApellido;
    private String apellido;
    private String nombre;
    private int regUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_nombre_usuario);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        nombre = "";
        apellido = "";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.buttonInfo).setOnClickListener(this);

        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(this);

        textInputEditTextNombre = findViewById(R.id.editTextNombre);
        textInputEditTextApellido = findViewById(R.id.editTextApellido);


        textInputEditTextNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nombre = s.toString();
                if (!nombre.isEmpty() && !apellido.isEmpty()) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputEditTextApellido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                apellido = s.toString();
                if (!nombre.isEmpty() && !apellido.isEmpty()) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
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
//                Intent intent = new Intent(RegNombreUsuarioActivity.this, MetodoRegActivity.class);
                Intent intent = new Intent(RegNombreUsuarioActivity.this, RegFotoPerfilActivity.class);
                switch (regUsuario) {
                    case 1:
                        Empleador empleador = new Empleador();
                        empleador.setNombre(nombre);
                        empleador.setApellido(apellido);
                        intent.putExtra("usuario",regUsuario);
                        intent.putExtra("empleador", empleador);
                        break;
                    case 2:
                        Trabajador trabajador = new Trabajador();
                        trabajador.setNombre(nombre);
                        trabajador.setApellido(apellido);
                        intent.putExtra("usuario",regUsuario);
                        intent.putExtra("trabajador", trabajador);
                        break;
                }
                startActivity(intent);

            }
        } catch (Exception e) {

        }
    }

    public Dialog alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_nombre_usuario));


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

}