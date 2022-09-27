package com.marlon.apolo.tfinal2022.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginGoogleActivity extends AppCompatActivity {

    private static final int REQ_ONE_TAP = 1002;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String loginRev;
    private ProgressDialog progressDialog;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private String TAG = LoginGoogleActivity.class.getSimpleName();
    private BienvenidoViewModel bienvenidoViewModel;
    private ArrayList<Empleador> empleadorsByEmail;
    private ArrayList<Trabajador> trabajadorsByEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_google);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mode = mPrefs.getBoolean("sync_theme", false);
        if (mode) {
//            ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(getResources().getColor(R.color.white));
            ((ImageView) findViewById(R.id.acLoginImageViewLogo)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(true)
                .build();

        loginRev = getIntent().getStringExtra("loginRev");
        if (loginRev.equals("cuentaExiste")) {
            if (firebaseUser != null) {
                String title = "Por favor espere";
                String message = "Iniciando sesión...";
                showProgress(title, message);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {

                        }
                        Intent intent = new Intent(LoginGoogleActivity.this, MainNavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }, 1500);
            }
        } else {
            oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                        @Override
                        public void onSuccess(BeginSignInResult result) {
                            try {
                                startIntentSenderForResult(
                                        result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                        null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // No saved credentials found. Launch the One Tap sign-up flow, or
                            // do nothing and continue presenting the signed-out UI.
                            Log.d(TAG, e.getLocalizedMessage());
                        }
                    });

        }

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


    private boolean showOneTapUI = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Log.d(TAG, "Got ID token.");
                        firebaseAuthWithGoogle(idToken);

                    } else if (password != null) {
                        // Got a saved username and password. Use them to authenticate
                        // with your backend.
                        Log.d(TAG, "Got password.");
                    }
                } catch (ApiException e) {
                    // ...
                }
                break;
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String title = "Por favor espere";
            String message = "Iniciando sesión...";
            showProgress(title, message);


            boolean estedoReg = false;

            try {
                for (Empleador e : empleadorsByEmail) {
                    if (e.getEmail() != null) {
                        if (e.getEmail().equals(user.getEmail())) {
                            estedoReg = true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            try {
                for (Trabajador t : trabajadorsByEmail) {
                    if (t.getEmail() != null) {
                        if (t.getEmail().equals(user.getEmail())) {
                            estedoReg = true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }


            if (estedoReg) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {

                        }
                        finishAffinity();
                        Intent intent = new Intent(LoginGoogleActivity.this, MainNavigationActivity.class);
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


        }
    }

    private void deleteAccountAndSignOut(FirebaseUser user) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
        Log.d(TAG, "Eliminando cuenta de autenticación");
        Toast.makeText(getApplicationContext(), "El usuario no se encuentra registrado.", Toast.LENGTH_LONG).show();

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
        FirebaseAuth.getInstance().signOut();
    }

}