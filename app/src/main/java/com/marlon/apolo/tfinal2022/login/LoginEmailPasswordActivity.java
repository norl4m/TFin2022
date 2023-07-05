package com.marlon.apolo.tfinal2022.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.ui.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.registro.view.PerfilActivity;

import java.util.Objects;

public class LoginEmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginEmailPasswordActivity.class.getSimpleName();
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private String email;
    private String password;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private Dialog dialogInfoError;
    private ProgressDialog progressDialog;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;
    private ScrollView scrollView;
    private LinearLayout linearLayout;


    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            String errorCode = ((FirebaseAuthException) Objects
                                    .requireNonNull(task.getException())).getErrorCode();
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            switch (errorCode) {
                                case "ERROR_USER_NOT_FOUND":
                                    alertDialogInfoError(0);
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    alertDialogInfoError(1);
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    alertDialogInfoError(2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {

            }
            FirebaseDatabase.getInstance().getReference()
                    .child("administrador")
                    .child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                Administrador administrador = snapshot.getValue(Administrador.class);
                                if (administrador != null) {
                                    setLocalAdminDevice(administrador);
                                }
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            finishAffinity();
            Intent intent = new Intent(LoginEmailPasswordActivity.this, MainNavigationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    public void setLocalAdminDevice(Administrador administrador) {
        Log.d(TAG, "Setting admin device");
//        Toast.makeText(getApplicationContext(), "Setting admin device", Toast.LENGTH_LONG).show();
        myPreferences = LoginEmailPasswordActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editorPref = myPreferences.edit();
        editorPref.putBoolean("adminFlag", true);
        editorPref.putString("key", password);
        editorPref.putString("email", administrador.getEmail());
        editorPref.apply();
    }

    public void alertDialogInfoError(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        switch (error) {
            case 0:/*usuario no registrado*/
                textViewInfo.setText(getResources().getString(R.string.text_error_user_no_found) + getResources().getString(R.string.text_error_user_no_found_concat));
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
//                        Toast.makeText(getApplicationContext(), "AAAAAAAAAAAAAAAAA", Toast.LENGTH_LONG).show();

                        setOnSharedPreferences();

                        Intent intentExtraData = new Intent(LoginEmailPasswordActivity.this, PerfilActivity.class);

                        startActivity(intentExtraData);
                        try {
                            dialogInfoError.dismiss();
                        } catch (Exception e) {

                        }
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
                            dialogInfoError.dismiss();

                        } catch (Exception e) {

                        }
                    }
                });

                break;
            case 1:/*clave equivocada*/
                textViewInfo.setText(getResources().getString(R.string.text_error_password_email));
                builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
                            dialogInfoError.dismiss();

                        } catch (Exception e) {

                        }
                    }
                });
                break;
            case 2:/*email inválido*/
                textViewInfo.setText(getResources().getString(R.string.text_error_email));
                builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
                            dialogInfoError.dismiss();

                        } catch (Exception e) {

                        }
                    }
                });
                break;
        }

        dialogInfoError = builder.create();
        dialogInfoError.show();
    }

    private void setOnSharedPreferences() {
        myPreferences = LoginEmailPasswordActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editorPref = myPreferences.edit();
        editorPref.putInt("methodTemp", 1);
        editorPref.putString("emailTemp", email);
        editorPref.putString("passTemp", password);
        editorPref.apply();
    }

    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
//        dialog.setTitle("Por favor espere");
        progressDialog.setTitle(title);
//        dialog.setMessage("Trabix se encuentra verificando su nùmero celular...");
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_login_email_password);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int colorOnPrimary = typedValue.data;

        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linLytBack);


        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.card_background);
        drawable.setTint(colorOnPrimary);
        scrollView.setBackground(drawable);

        linearLayout.setBackgroundColor(colorOnPrimary);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");


        textInputEditTextEmail = findViewById(R.id.editTextEmail);
        textInputEditTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);

        textInputEditTextEmail.setText(email);
        textInputEditTextPassword.setText(password);


        buttonLogin.setEnabled(false);
        findViewById(R.id.textViewSubTitle).setOnClickListener(this);
        findViewById(R.id.textViewRecuperar).setOnClickListener(this);

        try {
            if (!email.isEmpty() && !password.isEmpty()) {
                buttonLogin.setEnabled(true);
            }
        } catch (Exception e) {

        }

        buttonLogin.setOnClickListener(this);

        textInputEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();

                if (TextUtils.isEmpty(email)) {
//                    textInputEditTextEmail.setError("Error: por favor ingrese un correo electrónico.");/*tambien funciona pero en el EDT*/
                    ((TextInputLayout) findViewById(R.id.textFieldEmail)).setError("Error: por favor ingrese un correo electrónico.");
                } else {
                    ((TextInputLayout) findViewById(R.id.textFieldEmail)).setError(null);
                }

                try {
//                    if (!email.isEmpty() && !password.isEmpty()) {
                    if (!email.isEmpty() && password.length() >= 6) {
                        buttonLogin.setEnabled(true);
                    } else {
                        buttonLogin.setEnabled(false);
                    }
                } catch (Exception e) {

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
//                try {
//                    if (!email.isEmpty() && !password.isEmpty()) {
//                        buttonLogin.setEnabled(true);
//                    } else {
//                        buttonLogin.setEnabled(false);
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }


                if (TextUtils.isEmpty(password)) {
                    ((TextInputLayout) findViewById(R.id.textFieldPassword)).setError("Error: su clave o contraseña debe contener al menos 6 letras");
                } else {
                    ((TextInputLayout) findViewById(R.id.textFieldPassword)).setError(null);
                }


                try {
//                    if (!email.isEmpty() && !password.isEmpty()) {
                    if (!email.isEmpty() && password.length() >= 6) {
                        buttonLogin.setEnabled(true);
                    } else {
                        buttonLogin.setEnabled(false);
                    }
                } catch (Exception e) {

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String title = "Por favor espere";
                String message = "Iniciando sesión...";
                showProgress(title, message);
                signIn(email, password);
                break;
            case R.id.textViewSubTitle:
                startActivity(new Intent(LoginEmailPasswordActivity.this, PerfilActivity.class));
                break;
            case R.id.textViewRecuperar:
                startActivity(new Intent(LoginEmailPasswordActivity.this, ResetPasswordActivity.class));
                break;
        }
    }


}