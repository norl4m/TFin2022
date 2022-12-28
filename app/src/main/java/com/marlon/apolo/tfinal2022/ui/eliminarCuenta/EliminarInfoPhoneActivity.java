package com.marlon.apolo.tfinal2022.ui.eliminarCuenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.login.LoginCelularActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EliminarInfoPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EliminarInfoPhoneActivity.class.getSimpleName();
    private AlertDialog dialogInfo;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutPassword;
    private ProgressDialog progressDialog;
    private Usuario usuarioLocal;

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        String title = "Iniciando sesión";
//        String message = "Por favor espere...";
//        showProgress(title, message);
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


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String title = "Por favor espere";
            String message = "En estos momentos nos encontramos eliminando su infomación...";
            showProgress(title, message);
            deleteAccount(usuarioLocal);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        progressDialog.dismiss();
//                    } catch (Exception e) {
//
//                    }
//                    Intent intent = new Intent(EliminarInfoPhoneActivity.this, MainNavigationActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }
//            }, 1000);

        } else {
            try {
                //deleteAccountAndSignOut(user);
            } catch (Exception e) {

            }
        }

    }

    private void deleteAccount(Usuario usuario) {
        Log.d(TAG, "Delete account get object instance");
        String locationToFirebase = "";
        if (usuario instanceof Administrador) {
            Log.d(TAG, "Administrador");
            locationToFirebase = "administrador";
        }
        if (usuario instanceof Empleador) {
            Log.d(TAG, "Empleador");
            locationToFirebase = "empleadores";

        }
        if (usuario instanceof Trabajador) {
            Log.d(TAG, "Trabajador");
            locationToFirebase = "trabajadores";
        }
        Log.d(TAG, locationToFirebase);
        Log.d(TAG, mAuth.getCurrentUser().getUid());
        Log.d(TAG, usuario.toString());
        usuario.eliminarInfo(locationToFirebase, this);
    }

    public void closeProgress() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_info_phone);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.buttonInfo).setOnClickListener(this);

        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(colorNight);


        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputLayoutPassword.setEnabled(false);
        textInputLayoutPassword.setVisibility(View.GONE);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                            }
                            //Log.d(TAG, trabajador.toString());
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                            }
                            //Log.d(TAG, empleador.toString());
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonInfo:
                alertDialogInfo();
                break;

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

    public void alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
//
//        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_eliminar_info_con_phone));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }
            }
        });
        dialogInfo = builder.create();
        dialogInfo.show();
    }

}