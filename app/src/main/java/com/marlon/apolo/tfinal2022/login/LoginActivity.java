package com.marlon.apolo.tfinal2022.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.registro.view.PerfilActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mode = mPrefs.getBoolean("sync_theme", false);
        if (mode) {
//            ((ImageView) findViewById(R.id.imageView)).setColorFilter(getResources().getColor(R.color.white));
            ((ImageView) findViewById(R.id.imageView)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));

        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btnLoginWithEmail).setOnClickListener(this);
        findViewById(R.id.btnLoginWithGoogle).setOnClickListener(this);
        findViewById(R.id.btnLoginWithPhone).setOnClickListener(this);
        findViewById(R.id.textViewNuevoReg).setOnClickListener(this);
        findViewById(R.id.buttonContinuar).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginWithEmail:
                Intent intent = new Intent(LoginActivity.this, LoginEmailPasswordActivity.class);
                intent.putExtra("email", "");
                intent.putExtra("password", "");
                startActivity(intent);
                break;
            case R.id.btnLoginWithGoogle:
                Intent intentGoogle = new Intent(LoginActivity.this, LoginGoogleActivity.class);
                intentGoogle.putExtra("loginRev", "");
                startActivity(intentGoogle);
                break;
            case R.id.btnLoginWithPhone:
                startActivity(new Intent(LoginActivity.this, LoginCelularActivity.class));
                break;
            case R.id.textViewNuevoReg:
                startActivity(new Intent(LoginActivity.this, PerfilActivity.class));

                break;
            case R.id.buttonContinuar:
                Intent intentConti = new Intent(LoginActivity.this, MainNavigationActivity.class);
                intentConti.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentConti);
                break;
        }
    }
}