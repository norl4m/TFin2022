/*
 * @(#)InformacionInicialActivity.java        1.0 2022/09/30
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
package com.marlon.apolo.tfinal2022.infoInicial.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.infoInicial.adaptaders.SliderAdapter;
import com.marlon.apolo.tfinal2022.login.LoginActivity;

/**
 * Esta clase permite mostrar cuatro interfaces que contienen
 * información acerca del uso de la aplicación.
 * <p>
 * Esta clase es creada en dos casos:
 * Al instalar la aplicación desde cero
 * Y cuando el usuario borra todos los datos de la aplicación
 */
public class InformacionInicialActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;
    TextView[] dots;
    private Button btnLogin, btnChangeSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_informacion_inicial);
        //Hooks
        viewPager = findViewById(R.id.acComicViewPager);
        dotsLayout = findViewById(R.id.obaLltDots);
        btnLogin = findViewById(R.id.btnLogin);
        btnChangeSlide = findViewById(R.id.btnChangeSlide);

        //Call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
        btnLogin.setOnClickListener(this);
        btnChangeSlide.setOnClickListener(this);
        findViewById(R.id.btnChangeSlide1).setOnClickListener(this);
        btnLogin.setEnabled(false);

    }

    private void addDots(int position) {
        //Toast.makeText(getApplicationContext(), "addDots", Toast.LENGTH_LONG).show();
        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
//            dots[i].setText(Html.fromHtml("&#8226;"));
//            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);

        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.purple_200));
            if (position == dots.length - 1) {
                btnLogin.setEnabled(true);
            }
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        int idBoton = v.getId();
        if (idBoton == R.id.btnLogin) {
            startActivity(new Intent(InformacionInicialActivity.this, LoginActivity.class));
            setComicActivityFlag();
            finish();
        }
        if (idBoton == R.id.btnChangeSlide) {
            Log.e("Cambiaste de -->", " página");
            if (viewPager.getCurrentItem() == 3) {

            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

            }
        }
        if (idBoton == R.id.btnChangeSlide1) {
            Log.e("Cambiaste de -->", " página");
            if (viewPager.getCurrentItem() == 0) {

            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

            }
        }
    }

    public void setComicActivityFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("infoInicialActivityFlag", true);
        editor.apply();
    }
}