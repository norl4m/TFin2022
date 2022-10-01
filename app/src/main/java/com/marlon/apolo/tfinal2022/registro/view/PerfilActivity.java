package com.marlon.apolo.tfinal2022.registro.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.registro.RegNombreUsuarioActivity;

public class PerfilActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intentRegUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnRegEmpleador).setOnClickListener(this);
        findViewById(R.id.btnRegTrabajador).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegEmpleador:
//                intentRegUsuario = new Intent(PerfilActivity.this, RegistroDataEmpleadorActivity.class);
//                intentRegUsuario = new Intent(PerfilActivity.this, MetodoRegActivity.class);
                intentRegUsuario = new Intent(PerfilActivity.this, RegNombreUsuarioActivity.class);
                intentRegUsuario.putExtra("usuario", 1);
                startActivity(intentRegUsuario);
                break;
            case R.id.btnRegTrabajador:
//                intentRegUsuario = new Intent(PerfilActivity.this, RegistroDataTrabajadorActivity.class);
                intentRegUsuario = new Intent(PerfilActivity.this, RegNombreUsuarioActivity.class);
                intentRegUsuario.putExtra("usuario", 2);
                startActivity(intentRegUsuario);
                break;
        }
    }
}