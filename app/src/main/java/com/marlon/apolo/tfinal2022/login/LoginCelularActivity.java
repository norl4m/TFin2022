package com.marlon.apolo.tfinal2022.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.view.PerfilActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginCelularActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginCelularActivity.class.getSimpleName();


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutPassword;
    private ArrayList<Trabajador> trabajadorArrayList;
    private ArrayList<Empleador> empleadorArrayList;
    private ArrayList<Administrador> administradorArrayList;
    private BienvenidoViewModel bienvenidoViewModel;
    private ArrayList<Trabajador> trabajadorListByCelular;
    private ArrayList<Empleador> empleadorListByCelular;
    private ProgressDialog progressDialog;
    private AlertDialog dialogInfoError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_celular);
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean mode = mPrefs.getBoolean("sync_theme", false);
//        if (mode) {
////            ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(getResources().getColor(R.color.white));
//            ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
//
//        }

        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(colorNight);


        getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int colorOnPrimary = typedValue.data;

        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout linearLayout = findViewById(R.id.linLytBack);


        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.card_background);
        drawable.setTint(colorOnPrimary);
        scrollView.setBackground(drawable);

        linearLayout.setBackgroundColor(colorOnPrimary);


        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);

        bienvenidoViewModel.getAllEmpleadoresByPhone().observe(this, empleadors -> {
            if (empleadors != null) {
                empleadorListByCelular = empleadors;
            }
        });

        bienvenidoViewModel.getAllTrabajadoresByPhone().observe(this, trabajadors -> {
            if (trabajadors != null) {
                trabajadorListByCelular = trabajadors;
            }
        });


//        FirebaseDatabase.getInstance().getReference()
//                .child("trabajadores")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        trabajadorListByCelular = new ArrayList<>();
//
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Trabajador trabajador = data.getValue(Trabajador.class);
//                            try {
//                                if (trabajador.getCelular() != null) {
//                                    Log.d(TAG, trabajador.toString());
//                                    trabajadorListByCelular.add(trabajador);
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

//        FirebaseDatabase.getInstance().getReference()
//                .child("empleadores")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        empleadorListByCelular = new ArrayList<>();
//
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Empleador empleador = data.getValue(Empleador.class);
//                            try {
//                                if (empleador.getCelular() != null) {
//                                    Log.d(TAG, empleador.toString());
//                                    empleadorListByCelular.add(empleador);
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputLayoutPassword.setEnabled(false);
        textInputLayoutPassword.setVisibility(View.GONE);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
                textInputLayoutPhone.setEnabled(false);
                textInputLayoutPassword.setEnabled(false);

                findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
                findViewById(R.id.btnLogin).setEnabled(false);

                findViewById(R.id.btnContinue).setVisibility(View.GONE);
                findViewById(R.id.btnResend).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();

//                findViewById(R.id.btnContinue).setVisibility(View.GONE);
//                findViewById(R.id.btnResend).setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                textInputLayoutPhone.setEnabled(false);
//                textInputLayoutPassword.setVisibility(View.VISIBLE);
                textInputLayoutPassword.setEnabled(true);
                textInputLayoutPassword.setVisibility(View.VISIBLE);

                findViewById(R.id.btnLogin).setVisibility(View.GONE);
                findViewById(R.id.btnContinue).setVisibility(View.VISIBLE);
                findViewById(R.id.btnResend).setVisibility(View.GONE);
                textInputLayoutPassword.getEditText().setText("");
            }


            /*
             * Opcional. Este método se llama después de que haya pasado el tiempo de espera especificado en verifyPhoneNumber
             * sin que se active onVerificationCompleted primero. En los dispositivos que no tienen tarjeta SIM, este método se llama de inmediato,
             * debido a que no se puede realizar una recuperación automática de SMS.
             *
             * Algunas apps bloquean los datos de entrada del usuario hasta que termine el tiempo de espera del período de verificación automática
             * y solo en ese momento muestran una IU que le pide al usuario escribir el código de verificación que recibió por SMS (no se recomienda).
             *
             * */
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                try {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    } else {
                        Toast.makeText(getApplicationContext(), "Su código ha expirado, por favor solicite un nuevo código", Toast.LENGTH_LONG).show();

                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                } catch (Exception e) {

                }

                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "onCodeAutoRetrievalTimeOut");
                Log.d(TAG, s);
                textInputLayoutPhone.setEnabled(true);
                textInputLayoutPassword.getEditText().setText("");
                textInputLayoutPassword.setVisibility(View.GONE);


                findViewById(R.id.btnLogin).setVisibility(View.GONE);
                findViewById(R.id.btnContinue).setVisibility(View.GONE);
                findViewById(R.id.btnResend).setVisibility(View.VISIBLE);

            }
        };
        // [END phone_auth_callbacks]

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnContinue).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
    // [END on_start_check_user]


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        phoneNumber = "+593" + phoneNumber;
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        phoneNumber = "+593" + phoneNumber;

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        String title = "Iniciando sesión";
        String message = "Por favor espere...";
        showProgress(title, message);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            updateUI(user);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            Log.w(TAG, "signInWithPhone:failure -- " + errorCode);
                            switch (errorCode) {
                                case "ERROR_SESSION_EXPIRED":
                                    Toast.makeText(getApplicationContext(), "La sesión ha caducado, por favor solicite un nuevo código.", Toast.LENGTH_LONG).show();
                                    textInputLayoutPhone.setEnabled(false);
                                    textInputLayoutPassword.setEnabled(true);
                                    findViewById(R.id.btnLogin).setVisibility(View.GONE);
                                    findViewById(R.id.btnContinue).setVisibility(View.GONE);
                                    findViewById(R.id.btnResend).setVisibility(View.VISIBLE);
                                    break;
                                case "ERROR_INVALID_VERIFICATION_CODE":
                                    Toast.makeText(getApplicationContext(), "El código ingresado es incorrecto.", Toast.LENGTH_LONG).show();
                                    break;
//                                case "ERROR_INVALID_EMAIL":
//                                    regWithEmailPasswordActivity.alertDialogInfoError(2);
//                                    break;
                            }
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "El código ingresado es inválido", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // [END sign_in_with_phone]
    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
//        dialog.setTitle("Por favor espere");
        progressDialog.setTitle(title);
//        dialog.setMessage("Trabix se encuentra verificando su nùmero celular...");
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

//            bienvenidoViewModel.getAllTrabajadores().observe(this, trabajadors -> {


            String contactoVeri = user.getPhoneNumber();
            Log.d(TAG, contactoVeri);
            String phone = contactoVeri.substring(4);
            Log.d(TAG, phone);
            contactoVeri = "0" + phone;
            Log.d(TAG, contactoVeri);

//            });
            AtomicBoolean estedoReg = new AtomicBoolean(false);

            try {
                for (Empleador e : empleadorListByCelular) {
                    if (e.getCelular() != null) {
                        if (e.getCelular().equals(contactoVeri)) {
                            estedoReg.set(true);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            try {
                for (Trabajador t : trabajadorListByCelular) {
                    if (t.getCelular() != null) {
                        if (t.getCelular().equals(contactoVeri)) {
                            estedoReg.set(true);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }


            if (estedoReg.get()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {

                        }
                        finishAffinity();
                        Intent intent = new Intent(LoginCelularActivity.this, MainNavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }, 1000);

            } else {
                try {
                    deleteAccountAndSignOut(user);
                } catch (Exception e) {

                }
            }
        } else {

        }


    }

    private void deleteAccountAndSignOut(FirebaseUser user) {
        setPhoneOnPreferences(textInputLayoutPhone.getEditText().getText().toString());

        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
        Log.d(TAG, "Eliminando cuenta de autenticación");
        Toast.makeText(getApplicationContext(), "El usuario no se encuentra registrado.", Toast.LENGTH_LONG).show();
        textInputLayoutPhone.setEnabled(false);
        textInputLayoutPassword.setEnabled(false);

        findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.btnLogin).setEnabled(false);

        findViewById(R.id.btnContinue).setVisibility(View.GONE);
        findViewById(R.id.btnResend).setVisibility(View.GONE);
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Cuenta eliminada de Firebase Authentication");
                } else {
                    Log.d(TAG, "Error al eliminar cuenta de Firebase Authentication");
                }
            }
        });
        mAuth.signOut();

        alertDialogInfoError(0);
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

                        Intent intentExtraData = new Intent(LoginCelularActivity.this, PerfilActivity.class);

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
        SharedPreferences myPreferences = LoginCelularActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        editorPref.putInt("methodTemp", 3);
        editorPref.putString("celularTemp", textInputLayoutPhone.getEditText().getText().toString());
        editorPref.apply();
    }


    private void setPhoneOnPreferences(String celular) {

        SharedPreferences myPreferences = LoginCelularActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        editorPref.putInt("methodTemp", 3);
        editorPref.putString("celularTemp", celular);
        editorPref.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String number = textInputLayoutPhone.getEditText().getText().toString();
                if (number.length() == 10) {
                    startPhoneNumberVerification(number);
                } else {
                    Toast.makeText(getApplicationContext(), "El número ingresado es inválido", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnContinue:
                String code = textInputLayoutPassword.getEditText().getText().toString();
                if (code.length() >= 6) {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                } else {
                    Toast.makeText(getApplicationContext(), "El código ingresado es inválido", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnResend:
                String numberResend = textInputLayoutPhone.getEditText().getText().toString();
                if (numberResend.length() == 10) {
                    resendVerificationCode(numberResend, mResendToken);
                } else {
                    Toast.makeText(getApplicationContext(), "El número ingresado es inválido", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}