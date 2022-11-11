package com.marlon.apolo.tfinal2022.registro.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.receivers.NetworkReceiver;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegWithCelularActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegWithCelularActivity.class.getSimpleName();

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextInputLayout textInputLayoutCelular;
    private TextInputLayout textInputLayoutCode;

    private Button buttonVerificarCelular;
    private Button buttonSolicitarNuevoCode;
    private Button buttonEnviarCode;
    private Dialog dialogInfo;
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private ArrayList<Empleador> empleadors;
    private AlertDialog dialogInfoError;
    private ProgressDialog progressDialog;
    //    private TemporizadorAyncTask temporizadorAyncTask;
    private TextView textViewContador;
    private Dialog dialogInfoInternet;

    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    private RelativeLayout relativeLayoutProgressBarCustom;
    private ArrayList<Trabajador> trabajadorListByCelular;
    private ArrayList<Empleador> empleadorListByCelular;
    private NetworkReceiver receiver;

    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences myPreferences;
    private boolean networkFlag;
    private boolean sPref;
    private NetworkTool networkTool;
    private String phoneNumberVerif;
    private String title;
    private String message;
    private AlertDialog alertDialogVar;
    private AlertDialog alertDialogVarAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_with_celular);

        networkTool = new NetworkTool(this);
        empleadors = new ArrayList<>();
        trabajadorListByCelular = new ArrayList<>();
        empleadorListByCelular = new ArrayList<>();

        txtProgress = (TextView) findViewById(R.id.tv);
        progressBar = (ProgressBar) findViewById(R.id.circularProgressbar);

        relativeLayoutProgressBarCustom = findViewById(R.id.progressBarCustom);
        relativeLayoutProgressBarCustom.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference().child("empleadores")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        empleadors = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            try {
                                Empleador empleador = data.getValue(Empleador.class);
                                if (empleador.getCelular() != null) {
                                    Log.d(TAG, empleador.toString());
                                    empleadors.add(empleador);
                                }
                            } catch (Exception e) {

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        // Gets the user's network preference settings
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = RegWithCelularActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(this, trabajadors -> {
            if (trabajadors != null) {
                for (Trabajador tr : trabajadors) {
                    Log.d(TAG, tr.toString());
                    if (tr.getCelular() != null) {
                        trabajadorListByCelular.add(tr);
                    }
                }
            }
        });

        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getAllEmpleadores().observe(this, empleadors -> {
            if (empleadors != null) {
                for (Empleador em : empleadors) {
                    Log.d(TAG, em.toString());
                    if (em.getCelular() != null) {
                        empleadorListByCelular.add(em);
                    }
                }
            }
        });

        textViewContador = findViewById(R.id.textViewContador);
        textViewContador.setVisibility(View.GONE);
        textInputLayoutCelular = findViewById(R.id.textFieldCelular);
        textInputLayoutCode = findViewById(R.id.textFieldCode);
        textInputLayoutCode.setEnabled(false);

        buttonEnviarCode = findViewById(R.id.buttonEnviarCode);
        buttonSolicitarNuevoCode = findViewById(R.id.buttonSolicitarNuevoCode);
        buttonVerificarCelular = findViewById(R.id.buttonVerificarCelular);

        buttonEnviarCode.setVisibility(View.GONE);
        buttonSolicitarNuevoCode.setVisibility(View.GONE);
        buttonVerificarCelular.setVisibility(View.VISIBLE);

        buttonVerificarCelular.setEnabled(false);
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
                    //Toast.makeText(getApplicationContext(), "Se ha producido un error inesperado. Por favor inténtelo más tarde", Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    //Toast.makeText(getApplicationContext(), "Se ha producido un error inesperado. Por favor inténtelo más tarde", Toast.LENGTH_SHORT).show();
                    buttonEnviarCode.setVisibility(View.GONE);
                    buttonSolicitarNuevoCode.setVisibility(View.GONE);
                    buttonVerificarCelular.setVisibility(View.GONE);
                    textInputLayoutCode.setEnabled(false);
                }

                Toast.makeText(getApplicationContext(), "Se ha producido un error inesperado. Por favor inténtelo más tarde", Toast.LENGTH_SHORT).show();
                buttonEnviarCode.setVisibility(View.GONE);
                buttonSolicitarNuevoCode.setVisibility(View.GONE);
                buttonVerificarCelular.setVisibility(View.GONE);
                textInputLayoutCode.setEnabled(false);
                try {
                    relativeLayoutProgressBarCustom.setVisibility(View.GONE);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "verificationId:" + verificationId);
                Log.d(TAG, "token:" + token);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

//                startTemporizador();


                buttonEnviarCode.setVisibility(View.VISIBLE);
                buttonSolicitarNuevoCode.setVisibility(View.GONE);
                buttonVerificarCelular.setVisibility(View.GONE);
                textInputLayoutCode.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "Se ha enviado un código a su aplicación de mensajes. Para continuar ingrese el código de 6 dìgitos ", Toast.LENGTH_LONG).show();
                closeCustomAlertDialogAuth();

                alertDialogInfoEnvioCodigo();

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
                //Toast.makeText(getApplicationContext(), "onCodeAutoRetrievalTimeOut", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "onCodeAutoRetrievalTimeOut");
                Log.d(TAG, s);


                try {
                    relativeLayoutProgressBarCustom.setVisibility(View.GONE);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }

                textViewContador.setText("Su código ha expirado, por favor solicite un nuevo código.");
                textViewContador.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "El tiempo de espera se ha terminado, por favor inténtelo mas tarde", Toast.LENGTH_LONG).show();
                buttonEnviarCode.setVisibility(View.GONE);
                buttonSolicitarNuevoCode.setVisibility(View.VISIBLE);


            }
        };
        // [END phone_auth_callbacks]

        buttonEnviarCode.setOnClickListener(this);
        buttonSolicitarNuevoCode.setOnClickListener(this);
        buttonVerificarCelular.setOnClickListener(this);

        regUsuario = getIntent().getIntExtra("usuario", -1);


        try {
            String phoneTemp = myPreferences.getString("celularTemp", null);

            if (phoneTemp != null) {
                textInputLayoutCelular.getEditText().setText(phoneTemp);
            }


//            textInputEditTextPassword.setText(passwordTemp);
//            password = passwordTemp;
            buttonVerificarCelular.setEnabled(true);
        } catch (Exception e) {

        }

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }


        textInputLayoutCelular.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString();
                if (!phone.isEmpty()) {
                    buttonVerificarCelular.setEnabled(true);
                } else {
                    buttonVerificarCelular.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputLayoutCode.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = s.toString();
                if (!code.isEmpty()) {
                    buttonEnviarCode.setEnabled(true);
                } else {
                    buttonEnviarCode.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.buttonInfo).setOnClickListener(this);

    }


    private void startPhoneNumberVerification(String phoneNumber) {

        // [START verify_with_code]
        title = "Por favor espere";
        message = "Cachuelito se encuentra verificando su número de teléfono...";
//
        showCustomProgressDialogAuth(title, message);

//        startContador();
        phoneNumberVerif = phoneNumber;

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
        title = "Por favor espere";
        message = "Cachuelito se encuentra verificando el código ingresado...";
//
        showCustomProgressDialog(title, message);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]
    }

    public void showCustomProgressDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.custom_progress_dialog, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
//        return builder.create();
        final TextView textViewTitle = promptsView.findViewById(R.id.textViewTitle);
        final TextView textViewMessage = promptsView.findViewById(R.id.textViewMessage);

        textViewTitle.setText(title);
        textViewMessage.setText(message);

        alertDialogVar = builder.create();
        alertDialogVar.show();
//        builder.show();
    }

    public void showCustomProgressDialogAuth(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.custom_progress_dialog, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
//        return builder.create();
        final TextView textViewTitle = promptsView.findViewById(R.id.textViewTitle);
        final TextView textViewMessage = promptsView.findViewById(R.id.textViewMessage);

        textViewTitle.setText(title);
        textViewMessage.setText(message);

        alertDialogVarAuth = builder.create();
        alertDialogVarAuth.show();
//        builder.show();
    }

    public void closeCustomAlertDialog() {
        try {
            alertDialogVar.dismiss();
        } catch (Exception e) {

        }
    }

    public void closeCustomAlertDialogAuth() {
        try {
            alertDialogVarAuth.dismiss();
        } catch (Exception e) {

        }
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        phoneNumberVerif = phoneNumber;

        phoneNumber = "+593" + phoneNumber;
        Toast.makeText(getApplicationContext(), "Solicitando nuevo código de verificación para el número: " + phoneNumber, Toast.LENGTH_SHORT).show();

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
        closeCustomAlertDialog();
        title = "Por favor espere";
        message = "Cachuelito se encuentra verificando su información personal...";
        showCustomProgressDialog(title, message);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

//                            Toast.makeText(getApplicationContext(), "Sign In", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            //Toast.makeText(getApplicationContext(), "Cuenta creada", Toast.LENGTH_SHORT).show();
//                            Empleador empleador= new Empleador();
//                            empleador.setCelular(textInputLayoutCelular.getEditText().getText().toString());
//                            if (empleador.getFotoPerfil() != null) {
//                                empleador.registrarEmpleadorConFoto(Uri.parse(empleador.getFotoPerfil()), "empleadores", mAuth, RegWithCelularActivity.this, empleador, 3);
//                            } else {
//                                empleador.regEmpleadorEnFirebase(empleador, RegWithCelularActivity.this, mAuth, 3);
//                            }
//
//


                            updateUI(user);


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            Log.w(TAG, "signInWithPhone:failure -- " + errorCode);
                            switch (errorCode) {
                                case "ERROR_SESSION_EXPIRED":
                                    Toast.makeText(getApplicationContext(), "La sesión ha caducado, por favor solicite un nuevo código.", Toast.LENGTH_SHORT).show();
                                    buttonEnviarCode.setVisibility(View.GONE);
                                    buttonSolicitarNuevoCode.setVisibility(View.VISIBLE);
                                    buttonVerificarCelular.setVisibility(View.GONE);
                                    textInputLayoutCode.setEnabled(false);
                                    break;
                                case "ERROR_INVALID_VERIFICATION_CODE":
                                    Toast.makeText(getApplicationContext(), "El código ingresado es incorrecto.", Toast.LENGTH_SHORT).show();
                                    break;
//                                case "ERROR_INVALID_EMAIL":
//                                    regWithEmailPasswordActivity.alertDialogInfoError(2);
//                                    break;
                            }

                            try {
//                                closeProgressDialog();

                                closeCustomAlertDialog();
                            } catch (Exception e) {

                            }


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void updateUI(FirebaseUser user) {
        boolean photoFlag = false;
        if (user != null) {
            closeCustomAlertDialog();
            switch (regUsuario) {
                case 1:
                    photoFlag = false;
                    empleador.setCelular(phoneNumberVerif);
                    if (empleador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(empleador.getFotoPerfil().toString());
                        Cursor returnCursor = RegWithCelularActivity.this.getContentResolver().query(returnUri, null, null, null, null);
                        /*
                         * Get the column indexes of the data in the Cursor,
                         * move to the first row in the Cursor, get the data,
                         * and display it.
                         */
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        Log.d(TAG, returnCursor.getString(nameIndex));
                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                        if (returnCursor.getLong(sizeIndex) > 0) {
                            //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                            photoFlag = true;
                        } else {
                            //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                            photoFlag = false;
                        }
                    } else {
                        photoFlag = false;
                    }
                    /*Paso 2*/
                    empleador.setIdUsuario(user.getUid());
//                    title = "Por favor espere";
//                    message = "Su cuenta ya casi está lista...";
//                    showCustomProgressDialog(title, message);
                    if (photoFlag) {
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                        closeProgress();

                        empleador.registrarseEnFirebaseConFoto(RegWithCelularActivity.this, 3);
                    } else {
                        empleador.setFotoPerfil(null);
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
                        empleador.registrarseEnFirebase(RegWithCelularActivity.this, 3);
                    }
                    break;
                case 2:
                    trabajador.setCelular(phoneNumberVerif);
                    photoFlag = false;
                    if (trabajador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(trabajador.getFotoPerfil().toString());
                        Cursor returnCursor = RegWithCelularActivity.this.getContentResolver().query(returnUri, null, null, null, null);
                        /*
                         * Get the column indexes of the data in the Cursor,
                         * move to the first row in the Cursor, get the data,
                         * and display it.
                         */
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        Log.d(TAG, returnCursor.getString(nameIndex));
                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                        if (returnCursor.getLong(sizeIndex) > 0) {
                            //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                            photoFlag = true;
                        } else {
                            //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                            photoFlag = false;
                        }
                    } else {
                        photoFlag = false;
                    }
                    /*Paso 2*/
                    trabajador.setIdUsuario(user.getUid());
//                    title = "Por favor espere";
//                    message = "Su cuenta ya casi está lista...";
//                    showCustomProgressDialog(title, message);
                    if (photoFlag) {
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                        closeProgress();

                        trabajador.registrarseEnFirebaseConFoto(RegWithCelularActivity.this, 3);
                    } else {
                        trabajador.setFotoPerfil(null);
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
                        trabajador.registrarseEnFirebase(RegWithCelularActivity.this, 3);
                    }
                    break;
            }
//            if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
//                // AsyncTask subclass
//                if (networkTool.isOnlineWithWifi()) {
//                    usuarioTrabajador.registrarseEnFirebase(EmailPasswordActivity.this);
//                } else {
//                    if (networkTool.isOnlineWithData()) {
//                        if (!sPref) {
//                            alertDialogContinuarRegistroConDatos();
//                        } else {
//                            networkTool.alertDialogNoConectadoWifiInfo();
//                        }
////                                        networkTool.alertDialogNoConectadoWifiInfo();
//                    } else {
//                        networkTool.alertDialogNoConectadoInfo();
//                    }
//                }
//            } else {
//                networkTool.alertDialogNoConectadoInfo();
//            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonInfo:
                alertDialogInfo();
                break;
            case R.id.buttonEnviarCode:
                String code = textInputLayoutCode.getEditText().getText().toString();
                if (code.length() == 6) {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.codigo_verif_invalido), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonSolicitarNuevoCode:
                String phone = textInputLayoutCelular.getEditText().getText().toString();
                textInputLayoutCode.getEditText().setText("");
                if (phone.length() == 10) {
                    resendVerificationCode(phone, mResendToken);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.celular_invalido), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonVerificarCelular:


//                if (isOnlineWithWifi()) {
//                    if (textInputLayoutCelular.getEditText().getText().length() == 10) {
//                        AtomicBoolean estadoEmpleador = new AtomicBoolean(false);
//
//                        empleador.setCelular(textInputLayoutCelular.getEditText().getText().toString());
//
//
//                        String contactoVeri = "";
//                        if (empleador.getCelular() != null) {
//                            contactoVeri = empleador.getCelular();
//                        }
//
//                        for (Empleador e : empleadors) {
//                            if (e.getCelular() != null) {
//                                if (e.getCelular().equals(contactoVeri)) {
//                                    estadoEmpleador.set(true);
//                                }
//                            }
//                        }
//
//                        //Toast.makeText(getApplicationContext(), String.valueOf(estadoEmpleador.get()), Toast.LENGTH_SHORT).show();
//                        //Toast.makeText(getApplicationContext(), String.valueOf(contactoVeri), Toast.LENGTH_SHORT).show();
//                        if (estadoEmpleador.get()) {
//                            this.alertDialogInfoIniciarSesion(0);
////                            Toast.makeText(RegWithEmailPasswordActivity.this, "El empleador no puede ser registrado. Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
//                        } else {
////                        if (empleador.getFotoPerfil() != null) {
////                            //empleador.registrarEmpleadorConFoto(Uri.parse(empleador.getFotoPerfil()), "empleadores", mAuth, RegWithCelularActivity.this, empleador, 3);
////                        } else {
//                            startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
////                        }
//                        }
//
//
//                        //startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
//                    } else {
//                        Toast.makeText(getApplicationContext(), getString(R.string.celular_invalido), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    if (isOnlineWithData()) {
//                        alertDialogCont();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Parece que no se encuentra conectado a Internet.", Toast.LENGTH_LONG).show();
//                    }
//                }


                AtomicBoolean estadoEmpleador = new AtomicBoolean(false);

                String contactoVeri = "";

                if (textInputLayoutCelular.getEditText().getText().length() == 10) {
                    contactoVeri = textInputLayoutCelular.getEditText().getText().toString();
                    //startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());


                    for (Empleador e : empleadorListByCelular) {
                        if (e.getCelular() != null) {
                            if (e.getCelular().equals(contactoVeri)) {
                                estadoEmpleador.set(true);
                            }
                        }
                    }

                    for (Trabajador t : trabajadorListByCelular) {
                        if (t.getCelular() != null) {
                            if (t.getCelular().equals(contactoVeri)) {
                                estadoEmpleador.set(true);
                            }
                        }
                    }

                    if (estadoEmpleador.get()) {
                        this.alertDialogInfoIniciarSesion(0);
                    } else {
                        networkFlag = myPreferences.getBoolean("networkFlag", false);
                        sPref = defaultSharedPreferences.getBoolean("sync_network", true);

                        if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                            // AsyncTask subclass
                            //new DownloadXmlTask().execute(URL);


                            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                            boolean isMetered = cm.isActiveNetworkMetered();


                            if (isMetered) {
                                alertDialogContinuarRegistroConDatos();
                            } else {
//                                createAccount(usuarioEmpleador.getEmail(), password);
                                if (textInputLayoutCelular.getEditText().getText().length() == 10) {
                                    startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
                                    //startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.celular_invalido), Toast.LENGTH_SHORT).show();
                                }
                            }


//                            if (networkTool.isOnlineWithWifi()) {
//                                //empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
//                                if (textInputLayoutCelular.getEditText().getText().length() == 10) {
//                                    startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
//                                    //startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
//                                } else {
//                                    Toast.makeText(getApplicationContext(), getString(R.string.celular_invalido), Toast.LENGTH_SHORT).show();
//                                }
//
//                            } else {
//                                if (networkTool.isOnlineWithData()) {
//                                    if (!sPref) {
//                                        alertDialogContinuarRegistroConDatos();
//                                    } else {
//
//                                        networkTool.alertDialogNoConectadoWifiInfo();
//                                    }
//                                } else {
//                                    networkTool.alertDialogNoConectadoInfo();
//                                }
//                            }
                        } else {
                            networkTool.alertDialogNoConectadoInfo();
                        }
                    }


                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.celular_invalido), Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    public void alertDialogContinuarRegistroConDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_error_conexion_internet_pero_si_datos));
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }
//                empleador.setFotoPerfil(null);

//                Intent intent = new Intent(RegWithEmailPasswordActivity.this, MainNavigationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }
            }
        });

        builder.setCancelable(false);


        dialogInfo = builder.create();
        dialogInfo.show();

    }


    public void alertDialogCont() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_error_conexion_internet));
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfoInternet.dismiss();
                } catch (Exception e) {

                }
                switch (regUsuario) {
                    case 1:
                        empleador.setFotoPerfil(null);
                        break;
                    case 2:
                        trabajador.setFotoPerfil(null);
                        break;
                }


                if (textInputLayoutCelular.getEditText().getText().length() == 10) {
                    AtomicBoolean estadoEmpleador = new AtomicBoolean(false);

                    empleador.setCelular(textInputLayoutCelular.getEditText().getText().toString());


                    String contactoVeri = "";
                    if (empleador.getCelular() != null) {
                        contactoVeri = empleador.getCelular();
                    }

                    for (Empleador e : empleadors) {
                        if (e.getCelular() != null) {
                            if (e.getCelular().equals(contactoVeri)) {
                                estadoEmpleador.set(true);
                            }
                        }
                    }

                    //Toast.makeText(getApplicationContext(), String.valueOf(estadoEmpleador.get()), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), String.valueOf(contactoVeri), Toast.LENGTH_SHORT).show();
                    if (estadoEmpleador.get()) {
                        RegWithCelularActivity.this.alertDialogInfoIniciarSesion(0);
//                            Toast.makeText(RegWithEmailPasswordActivity.this, "El empleador no puede ser registrado. Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
                    } else {
//                        if (empleador.getFotoPerfil() != null) {
//                            //empleador.registrarEmpleadorConFoto(Uri.parse(empleador.getFotoPerfil()), "empleadores", mAuth, RegWithCelularActivity.this, empleador, 3);
//                        } else {
                        startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
//                        }
                    }


                    //startPhoneNumberVerification(textInputLayoutCelular.getEditText().getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.celular_invalido), Toast.LENGTH_SHORT).show();
                }


//                empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
//                empleador.setFotoPerfil(null);
//                Intent intent = new Intent(RegWithEmailPasswordActivity.this, MainNavigationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setCancelable(false);


        dialogInfoInternet = builder.create();
        dialogInfoInternet.show();

    }

    public void alertDialogInfoIniciarSesion(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        switch (error) {
            case 0:/*email repetido*/
                textViewInfo.setText(getResources().getString(R.string.text_error_email_repetido));
                builder.setPositiveButton("Iniciar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
//                            Intent intent = new Intent(RegWithCelularActivity.this, LoginCelularActivity.class);
                            Intent intent = new Intent(RegWithCelularActivity.this, LoginActivity.class);
//                            intent.putExtra("loginRev", "cuentaExiste");
                            startActivity(intent);
                            dialogInfoError.dismiss();

                        } catch (Exception e) {

                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                mAuth.signOut();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                break;
        }


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

        dialogInfoError = builder.create();
        dialogInfoError.show();

    }


    public void alertDialogVerificationInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(getResources().getString(R.string.text_celular_verification));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

                Intent intent = new Intent(RegWithCelularActivity.this, MainNavigationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setCancelable(false);


        dialogInfo = builder.create();
        dialogInfo.show();

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

    public void closeProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCustomAlertDialog();
    }

    public boolean isOnlineWithWifi() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean online = false;


//        if (sPref && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            online = true;
        }


//        return (networkInfo != null && networkInfo.isConnected());
        return online;
    }

    public boolean isOnlineWithData() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean online = false;


        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            online = true;
        }


//        return (networkInfo != null && networkInfo.isConnected());
        return online;
    }


    public void alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_reg_phone));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons
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

    public void alertDialogInfoEnvioCodigo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_enviado_code));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons
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