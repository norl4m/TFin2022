package com.marlon.apolo.tfinal2022.registro.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.marlon.apolo.tfinal2022.R;

public class PerfilActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intentRegUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_perfil_poc);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        ((ImageView) findViewById(R.id.imgBtnRegTrabajador)).setColorFilter(colorPrimary);
        ((ImageView) findViewById(R.id.imgBtnRegEmpleador)).setColorFilter(colorPrimary);

        findViewById(R.id.cardViewEmpleador).setOnClickListener(this);
        findViewById(R.id.imgBtnRegEmpleador).setOnClickListener(this);
        findViewById(R.id.cardViewTrabajdor).setOnClickListener(this);
        findViewById(R.id.imgBtnRegTrabajador).setOnClickListener(this);
    }

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardViewEmpleador:
            case R.id.imgBtnRegEmpleador:
                intentRegUsuario = new Intent(PerfilActivity.this, RegDatoPersonalActivity.class);
                intentRegUsuario.putExtra("usuario", 1);
                startActivity(intentRegUsuario);
                break;
            case R.id.cardViewTrabajdor:
            case R.id.imgBtnRegTrabajador:
                intentRegUsuario = new Intent(PerfilActivity.this, RegDatoPersonalActivity.class);
                intentRegUsuario.putExtra("usuario", 2);
                startActivity(intentRegUsuario);
                break;
        }
    }

}