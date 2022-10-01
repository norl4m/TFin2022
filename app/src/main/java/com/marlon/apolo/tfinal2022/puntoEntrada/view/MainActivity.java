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

/**
 * Esta clase es el punto de entrada a la aplicación.
 * <p>
 * La clase permite seleccionar entre las interfaces gráficas de información inicial, inicio de sesión
 * y navegación principal de la app.
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
        // Cargamos una referencia a la preferencia para cambiar el tema
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
            /* Permite cambiar el ìcono de color dentro del ícono en un ImageView*/
            imageViewLogo.setColorFilter(getResources().getColor(R.color.white));
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                if (getInfoInicialActivityFlag()) {
                    startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
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

    /**
     * Este método permite habilidar la persistencia de datos en la memoria cachè del dispositivo.
     * Los datos obtenidos desde Firebase Database no sobrepsasan los 10MB
     */
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