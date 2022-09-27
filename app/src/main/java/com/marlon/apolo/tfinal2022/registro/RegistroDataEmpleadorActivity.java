package com.marlon.apolo.tfinal2022.registro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.marlon.apolo.tfinal2022.R;

public class RegistroDataEmpleadorActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_data_empleador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.buttonNext).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:
//                Intent intent = new Intent(RegistroDataTrabajadorActivity.this, RegistroRecordPolicialActivity.class);
//                startActivity(intent);
                Toast.makeText(getApplicationContext(), "finalizar registro", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}