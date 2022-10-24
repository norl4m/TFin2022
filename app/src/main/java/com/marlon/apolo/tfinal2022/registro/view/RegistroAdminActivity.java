package com.marlon.apolo.tfinal2022.registro.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.VideoCapture;

import android.app.AutomaticZenRule;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.material.textfield.TextInputLayout;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.PostAsyncTask;

import org.json.JSONObject;

public class RegistroAdminActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroAdminActivity.class.getSimpleName();
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutLastname;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutPassword;
    private int optionReg;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_admin);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutLastname = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        optionReg = 1;
        buttonNext = findViewById(R.id.buttonFinish);
        buttonNext.setEnabled(false);
        findViewById(R.id.radioButtonEmail).setOnClickListener(this);
//        findViewById(R.id.radioButtonGoogle).setOnClickListener(this);
        findViewById(R.id.radioButtonPhone).setOnClickListener(this);
        findViewById(R.id.buttonFinish).setOnClickListener(this);

        textInputLayoutEmail.setVisibility(View.GONE);
        textInputLayoutPassword.setVisibility(View.GONE);
        textInputLayoutPhone.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        try {
            boolean checked = ((RadioButton) v).isChecked();
            // Check which radio button was clicked.
            switch (v.getId()) {
                case R.id.radioButtonEmail:
                    if (checked) {
                        optionReg = 1;
                        buttonNext.setEnabled(true);
                        textInputLayoutEmail.setVisibility(View.VISIBLE);
                        textInputLayoutPassword.setVisibility(View.VISIBLE);
                        textInputLayoutPhone.setVisibility(View.GONE);

                    }
                    break;
                /*case R.id.radioButtonGoogle:
                    if (checked) {
                        optionReg = 2;
                        buttonNext.setEnabled(true);
                    }
                    break;*/
                case R.id.radioButtonPhone:
                    if (checked) {
                        optionReg = 3;
                        buttonNext.setEnabled(true);
                        textInputLayoutEmail.setVisibility(View.GONE);
                        textInputLayoutPassword.setVisibility(View.GONE);
                        textInputLayoutPhone.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        try {
            if (v.getId() == R.id.buttonFinish) {
                JSONObject jsonObject = new JSONObject();
                PostAsyncTask postAsyncTask = null;
                switch (optionReg) {
                    case 1:
                        try {
                            jsonObject = new JSONObject();

                            jsonObject.put("uid", "dasdq42tgdfg");
                            jsonObject.put("displayName", "Paúl Apolo");
                            jsonObject.put("email", "marlonp@gmail.com");
//                            jsonObject.put("phoneNumber", "+593983228466");
                            jsonObject.put("phoneNumber", null);
                            jsonObject.put("password", "87654321");

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        postAsyncTask = new PostAsyncTask(jsonObject.toString(), this);
                        postAsyncTask.execute();

                        break;
                    /*case 2:
                        try {
                            jsonObject = new JSONObject();

                            jsonObject.put("uid", "dasdq42tgdfg");
                            jsonObject.put("displayName", "Paúl Apolo");
                            jsonObject.put("email", "marlonp@gmail.com");
//                            jsonObject.put("phoneNumber", "+593983228466");
                            jsonObject.put("phoneNumber", null);
//                            jsonObject.put("password", "87654321");
                            jsonObject.put("password", null);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        postAsyncTask = new PostAsyncTask(jsonObject.toString(), this);
                        postAsyncTask.execute();


                        break;*/
                    case 3:
                        try {
                            jsonObject = new JSONObject();

                            jsonObject.put("uid", "dasdq42tgdfg");
                            jsonObject.put("displayName", "Paúl Apolo");
                            jsonObject.put("email", null);
                            jsonObject.put("phoneNumber", "+593983228466");
                            jsonObject.put("password", null);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        postAsyncTask = new PostAsyncTask(jsonObject.toString(), this);
                        postAsyncTask.execute();

                        break;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}