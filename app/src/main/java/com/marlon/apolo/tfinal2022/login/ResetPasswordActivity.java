package com.marlon.apolo.tfinal2022.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private ProgressDialog progressDialog;
    private TextInputLayout textInputLayout;

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void sendEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Se ha enviando un correo electrónico con las instrucciones para recuperar la contraseña");
// Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                finish();
            }
        });
// Set other dialog properties

// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
        setContentView(R.layout.activity_reset_password);
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

        textInputLayout = findViewById(R.id.textInputLayoutEmail);
        findViewById(R.id.resetPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = textInputLayout.getEditText().getText().toString();

                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(ResetPasswordActivity.this, "Email inválido", Toast.LENGTH_SHORT).show();

                } else {
                    String title = "Por favor espere";
                    String message = "Enviano correo electrónico...";
                    showProgress(title, message);
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                    Log.d(TAG, "Email sent.");
//                                        finish();
                                        try {
                                            progressDialog.dismiss();

                                        } catch (Exception e) {

                                        }
                                        sendEmailDialog();
//                                    Toast.makeText(ResetPasswordActivity.this, "Se ha envidado un correo electrónico a la cuenta xx con instrucciones para restablecer su contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                try {

                } catch (Exception e) {

                }

            }
        });
    }

}