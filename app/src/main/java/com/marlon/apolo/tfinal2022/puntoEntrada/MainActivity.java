package com.marlon.apolo.tfinal2022.puntoEntrada;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.infoInicial.InformacionInicialActivity;
/*
 * Copyright (C) 2018 Google Inc.
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

/**
 * Esta clase es el punto de entrada de la aplicación, permite iniciar las interfaces
 * gráficas de inicio de sesión y navegación principal
 */

public class MainActivity extends AppCompatActivity {

    private final static int TIME_SPLASH = 2500;
    private ImageView imageViewLogo;
    private boolean mode;


    /**
     * Este método permite iniciar los componentes de la interfaz gráfica principal
     *
     * @param savedInstanceState objeto Bundle que almacena información en el caso de que se prodruzca un cambio
     *                           de configuración en el dispositivo como: cambiar de orientación la pantalla
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // cargamos la preferencia
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mode = mPrefs.getBoolean("sync_theme", false);
        if (mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);/* recrea las actividades*/
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Esto permite que al inicar el splashScreen desaparezcan las barras del nombre de la app, donde se encuentra información de red y batería etc
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //enablePersistence();/*Utilizado antes de cualquier instancia*/

        // Animations
        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView textViewWelcome = findViewById(R.id.textView1);
        TextView textViewSlogan = findViewById(R.id.textView2);

        // Setting animations
        imageViewLogo.setAnimation(topAnimation);
//        textViewWelcome.setAnimation(bottomAnimation);
        textViewWelcome.setAnimation(topAnimation);
        textViewSlogan.setAnimation(bottomAnimation);
        if (mode) {
            imageViewLogo.setColorFilter(getResources().getColor(R.color.white));/* Permite cambiar el ìcono de color dentro del ícono en un ImageView*/
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                if (getInfoInicialActivityFlag()) {
                    startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
//                    startActivity(new Intent(MainActivity.this, RegistroOficioActivity.class));
//                    startActivity(new Intent(MainActivity.this, RegistroOficioActivityPoc.class));
//                    startActivity(new Intent(MainActivity.this, PocRegWithGoogleActivity.class));
//                    startActivity(new Intent(MainActivity.this, PocActivity6.class));
//                    startActivity(new Intent(MainActivity.this, PoCActivity.class));
//                    startActivity(new Intent(MainActivity.this, VideoLlamadaActivity.class));
//                    startActivity(new Intent(MainActivity.this, LlamadaVozActivity.class));
//                    startActivity(new Intent(MainActivity.this, IndividualChatActivity.class));
//                    startActivity(new Intent(MainActivity.this, CitaTrabajoActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, InformacionInicialActivity.class));
                }
                finish();
            }
        }, TIME_SPLASH);
    }

    public boolean getInfoInicialActivityFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return prefs.getBoolean("infoInicialActivityFlag", false);
    }

    private void enablePersistence() {
        // [START rtdb_enable_persistence]
        Log.d("TAG", "enablePersistence");

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }
        // [END rtdb_enable_persistence]
    }
}