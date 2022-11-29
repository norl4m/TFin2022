package com.marlon.apolo.tfinal2022.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.registro.view.PerfilActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private void setTempFlags() {
        SharedPreferences myPreferences = LoginActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        editorPref.putInt("methodTemp", -1);
        editorPref.putString("emailTemp", null);
        editorPref.putString("passTemp", null);
        editorPref.putString("celularTemp", null);
        editorPref.apply();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_login);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mode = mPrefs.getBoolean("sync_theme", false);
//        if (mode) {
////            ((ImageView) findViewById(R.id.imageView)).setColorFilter(getResources().getColor(R.color.white));
//            ((ImageView) findViewById(R.id.imageView)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
//
//        }
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        ((ImageView) findViewById(R.id.imageView)).setColorFilter(colorNight);

        setTempFlags();
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

        if (getOmitirLoginFlag()) {
            findViewById(R.id.buttonContinuar).setVisibility(View.GONE);
        } else {
            findViewById(R.id.buttonContinuar).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonContinuar).setOnClickListener(this);
        }

//        findViewById(R.id.buttonContinuar).setOnClickListener(this);


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
//                finishAffinity();
                setOmitirLoginFlag();
                Intent intentConti = new Intent(LoginActivity.this, MainNavigationActivity.class);
//                intentConti.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                finishAffinity();
                finish();
                startActivity(intentConti);
                break;
        }
    }

    /**
     * Este método permite saltar el Activity que coresponde a la información inicial.
     * <p>
     * El Activity de información inicial aparece solo la primera vez al instalar la aplicación, cuando
     * la bandera se encuentra en false.
     */
    public boolean getOmitirLoginFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return prefs.getBoolean("omitirLogin", false);
    }

    public void setOmitirLoginFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("omitirLogin", true);
        editor.apply();
    }
}