package com.marlon.apolo.tfinal2022.infoInicial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.adaptadores.SliderAdapter;
import com.marlon.apolo.tfinal2022.login.LoginActivity;

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

        btnLogin.setEnabled(false);
    }

    private void addDots(int position) {
        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
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
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void setComicActivityFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("infoInicialActivityFlag", true);
        editor.apply();
    }
}