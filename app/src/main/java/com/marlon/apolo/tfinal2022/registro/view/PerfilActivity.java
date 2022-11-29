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

    private static final String DEBUG_TAG = PerfilActivity.class.getSimpleName();
    private Intent intentRegUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
//        setContentView(R.layout.activity_perfil);
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
//        ((ImageButton)findViewById(R.id.imgBtnRegTrabajador))

//        toolbar.seton


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
////                    getSupportActionBar().hide();
//                    hideSystemBars();
//                } catch (Exception e) {
//                    Log.d("TAG", e.toString());
//                }
//            }
//        }, 3000);

//        findViewById(R.id.btnRegEmpleador).setOnClickListener(this);
        findViewById(R.id.cardViewEmpleador).setOnClickListener(this);
        findViewById(R.id.imgBtnRegEmpleador).setOnClickListener(this);
//        findViewById(R.id.btnRegTrabajador).setOnClickListener(this);
        findViewById(R.id.cardViewTrabajdor).setOnClickListener(this);
        findViewById(R.id.imgBtnRegTrabajador).setOnClickListener(this);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btnRegEmpleador:
            case R.id.cardViewEmpleador:
            case R.id.imgBtnRegEmpleador:
//                intentRegUsuario = new Intent(PerfilActivity.this, RegistroDataEmpleadorActivity.class);
//                intentRegUsuario = new Intent(PerfilActivity.this, MetodoRegActivity.class);
                intentRegUsuario = new Intent(PerfilActivity.this, RegNombreUsuarioActivity.class);
                intentRegUsuario.putExtra("usuario", 1);
                startActivity(intentRegUsuario);
                break;
//            case R.id.btnRegTrabajador:
            case R.id.cardViewTrabajdor:
            case R.id.imgBtnRegTrabajador:
//                intentRegUsuario = new Intent(PerfilActivity.this, RegistroDataTrabajadorActivity.class);
                intentRegUsuario = new Intent(PerfilActivity.this, RegNombreUsuarioActivity.class);
                intentRegUsuario.putExtra("usuario", 2);
                startActivity(intentRegUsuario);
                break;
        }
    }

}