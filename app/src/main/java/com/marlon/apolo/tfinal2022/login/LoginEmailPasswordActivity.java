package com.marlon.apolo.tfinal2022.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.view.PerfilActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;

import java.util.ArrayList;
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
    private BienvenidoViewModel bienvenidoViewModel;
    private ArrayList<Empleador> empleadorsByEmail;
    private ArrayList<Trabajador> trabajadorsByEmail;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;

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
        setContentView(R.layout.activity_login_email_password);

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean mode = mPrefs.getBoolean("sync_theme", false);
//        if (mode) {
////            ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(getResources().getColor(R.color.white));
//            ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
//        }
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(colorNight);


        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        bienvenidoViewModel.getAllEmpleadoresByEmail().observe(this, empleadors -> {
            if (empleadors != null) {
                empleadorsByEmail = empleadors;
            }
        });
        bienvenidoViewModel.getAllTrabajadoresbyEmail().observe(this, trabajadors -> {
            if (trabajadors != null) {
                trabajadorsByEmail = trabajadors;
            }
        });


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
                if (!email.isEmpty() && !password.isEmpty()) {
                    buttonLogin.setEnabled(true);
                } else {
                    buttonLogin.setEnabled(false);
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
                if (!email.isEmpty() && !password.isEmpty()) {
                    buttonLogin.setEnabled(true);
                } else {
                    buttonLogin.setEnabled(false);
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
        }
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(LoginEmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            Log.w(TAG, "signInWithPhone:failure -- " + errorCode);
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {

                            }
                            switch (errorCode) {
                                case "ERROR_USER_NOT_FOUND":
                                    alertDialogInfoError(0);
//                                    Toast.makeText(activity, "Parece que ya posees una cuenta en", Toast.LENGTH_SHORT).show();
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
        // [END sign_in_with_email]
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

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    progressDialog.dismiss();
//                } catch (Exception e) {
//
//                }
//                Intent intent = new Intent(LoginEmailPasswordActivity.this, MainNavigationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        }, 1500);
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
//
//        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                // sign in the user ...
//                try {
//                    dialogInfoError.dismiss();
//
//                } catch (Exception e) {
//
//                }
//            }
//        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

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
}