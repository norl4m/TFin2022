/*
 * @(#)MainActivity.java        1.0 2022/09/30
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
package com.marlon.apolo.tfinal2022.puntoEntrada.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.marlon.apolo.tfinal2022.BuildConfig;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajoArchi.CitaTrabajoArchiActivity;
import com.marlon.apolo.tfinal2022.infoInicial.InformacionInicialActivity;
import com.marlon.apolo.tfinal2022.ui.trabajadores.eliminar.DeleteUserAsyncActivity;

import java.util.Locale;

/**
 * Esta clase es el punto de entrada a la aplicación.
 * <p>
 * La clase permite seleccionar entre las interfaces gráficas de información inicial, inicio de sesión
 * y navegación principal de la app.
 */

public class MainActivity extends AppCompatActivity {

    private final static int TIME_SPLASH = 2500;


    /**
     * Este método permite iniciar los componentes de la interfaz gráfica principal
     *
     * @param savedInstanceState objeto Bundle que almacena información en el caso de que se prodruzca un cambio
     *                           de configuración en el dispositivo como: cambiar de orientación la pantalla
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cargamos una referencia a la preferencia para cambiar el tema
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean mode = mPrefs.getBoolean("sync_theme", false);
//        if (mode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);/* recrea las actividades*/
//        }
//        hideSystemBars();

        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_main);

        // Animations
        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView textViewWelcome = findViewById(R.id.textView1);
        TextView textViewSlogan = findViewById(R.id.textView2);
        TextView textViewVersionCode = findViewById(R.id.textViewVersionCode);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        // Setting animations
        imageViewLogo.setAnimation(topAnimation);
//        textViewWelcome.setAnimation(bottomAnimation);
        textViewWelcome.setAnimation(topAnimation);
        textViewSlogan.setAnimation(bottomAnimation);
//        textViewVersionCode.setText(String.format(Locale.getDefault(), "Versión: %d", versionCode));
        textViewVersionCode.setText(String.format("Versión: %s", versionName));
//        if (mode) {
        /* Permite cambiar el ìcono de color dentro del ícono en un ImageView*/
//            imageViewLogo.setColorFilter(getResources().getColor(R.color.white));
//        }

        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        imageViewLogo.setColorFilter(colorNight);
        /*Esto es una maravilla*/


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getInfoInicialActivityFlag()) {
                    startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
//                    startActivity(new Intent(MainActivity.this, CitaTrabajoArchiActivity.class));
//                    startActivity(new Intent(MainActivity.this, DeleteUserAsyncActivity.class));
//                    startActivity(new Intent(MainActivity.this, VideoCallUIKitActivity.class));
//                    startActivity(new Intent(MainActivity.this, AgoraVideoCallActivity.class));
//                    startActivity(new Intent(MainActivity.this, PerfilActivity.class));
//                    startActivity(new Intent(MainActivity.this, MetodoRegActivity.cla   ss));
//                    startActivity(new Intent(MainActivity.this, RegNombreUsuarioActivity.class));
//                    startActivity(new Intent(MainActivity.this, OficioArchiActivity.class));
//                    startActivity(new Intent(MainActivity.this, LoginPoCActivity.class));
//                    startActivity(new Intent(MainActivity.this, RegWithEmailPasswordActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, InformacionInicialActivity.class));
                }
                finish();
            }
        }, TIME_SPLASH);
    }

    /**
     * Este método permite saltar el Activity que coresponde a la información inicial.
     * <p>
     * El Activity de información inicial aparece solo la primera vez al instalar la aplicación, cuando
     * la bandera se encuentra en false.
     */
    public boolean getInfoInicialActivityFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return prefs.getBoolean("infoInicialActivityFlag", false);
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


}