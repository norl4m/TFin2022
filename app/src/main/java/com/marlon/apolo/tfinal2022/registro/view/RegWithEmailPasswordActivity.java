package com.marlon.apolo.tfinal2022.registro.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.PostAsyncTask;
import com.marlon.apolo.tfinal2022.login.LoginActivity;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.receivers.NetworkReceiver;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegWithEmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegWithEmailPasswordActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private List<Trabajador> trabajadorListByEmail;
    private List<Empleador> empleadorListByEmail;
    private int regUsuario;

    private Empleador empleador;
    private Trabajador trabajador;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private String email;
    private String password;
    private Button buttonFinish;
    private Dialog dialogInfo;
    private ProgressDialog progressDialog;
    private NetworkReceiver receiver;

    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences myPreferences;
    private NetworkTool networkTool;
    private boolean networkFlag;
    public static boolean sPref;
    private Usuario usuarioTrabajador;
    private String title;
    private String message;
    private Usuario usuarioEmpleador;
    private AlertDialog alertDialogVar;
    private LinearLayout linearLayout;
    private TextView textViewHeading;

    private RelativeLayout relativeLayoutBack;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    public static boolean issPref() {
        return sPref;
    }

    public static void setsPref(boolean sPref) {
        RegWithEmailPasswordActivity.sPref = sPref;
    }


    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_email_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        networkTool = new NetworkTool(this);

        linearLayout = findViewById(R.id.linLytMain);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true);
        int colorPrimaryVariant = typedValue.data;
        //linearLayout.setBackgroundColor(colorSecondaryVariant);

        textViewHeading = findViewById(R.id.textViewHeading);
        relativeLayoutBack = findViewById(R.id.rltLytItems);

        getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int colorOnPrimary = typedValue.data;

        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        textViewHeading.setTextColor(ContextCompat.getColor(this, R.color.white_smoke));

        getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        int colorSecondary = typedValue.data;

        getTheme().resolveAttribute(R.attr.colorSecondaryVariant, typedValue, true);
        int colorSecondaryVariant = typedValue.data;

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.card_background);
        drawable.setTint(colorOnPrimary);
        relativeLayoutBack.setBackground(drawable);

        linearLayout.setBackgroundColor(colorPrimaryVariant);


        trabajadorListByEmail = new ArrayList<>();
        empleadorListByEmail = new ArrayList<>();

        buttonFinish = findViewById(R.id.buttonfinish);
        buttonFinish.setEnabled(false);
        buttonFinish.setOnClickListener(this);

        textInputEditTextEmail = findViewById(R.id.editTextEmail);
        textInputLayoutEmail = findViewById(R.id.textFieldEmail);
        textInputLayoutPassword = findViewById(R.id.textFieldPassword);
        textInputEditTextPassword = findViewById(R.id.editTextPassword);

//        findViewById(R.id.buttonInfo).setOnClickListener(this);

        email = "";
        password = "";

        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        // Gets the user's network preference settings
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = RegWithEmailPasswordActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(this, trabajadors -> {
            if (trabajadors != null) {
                for (Trabajador tr : trabajadors) {
                    Log.d(TAG, tr.toString());
                    if (tr.getEmail() != null) {
                        trabajadorListByEmail.add(tr);
                    }
                }
            }
        });

        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getAllEmpleadores().observe(this, empleadors -> {
            if (empleadors != null) {
                for (Empleador em : empleadors) {
                    Log.d(TAG, em.toString());
                    if (em.getEmail() != null) {
                        empleadorListByEmail.add(em);
                    }
                }
            }
        });

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                break;
        }

        try {
            String emailTemp = myPreferences.getString("emailTemp", null);


            if (emailTemp != null) {
                textInputEditTextEmail.setText(emailTemp);

                email = emailTemp;

            }
            String passwordTemp = myPreferences.getString("passTemp", null);

            if (passwordTemp != null) {
                textInputEditTextPassword.setText(passwordTemp);
                password = passwordTemp;
            }

            if (!email.isEmpty() && !password.isEmpty()) {
                buttonFinish.setEnabled(true);
            } else {
                buttonFinish.setEnabled(false);
            }

//            textInputEditTextPassword.setText(passwordTemp);
//            password = passwordTemp;
//            buttonFinish.setEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


        //textInputEditTextEmail.setTextColor(colorNight);
//        try {
        // field = textInputLayoutEmail.getClass().getDeclaredField("mFocusedTextColor");
        //field.setAccessible(true);
        int[][] states = new int[][]{
                new int[]{}
        };
        int[] colors = new int[]{
                colorSecondaryVariant
        };
        ColorStateList myList = new ColorStateList(states, colors);


        textInputEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
                try {
                    if (!email.isEmpty() && !password.isEmpty()) {
                        buttonFinish.setEnabled(true);
                    } else {
                        buttonFinish.setEnabled(false);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        int[][] states1 = new int[][]{
                new int[]{}
        };
        int[] colors1 = new int[]{
                R.color.white
        };
        ColorStateList myList2 = new ColorStateList(states1, colors1);
//            textInputLayoutEmail.set(textInputLayoutEmail, myList);

        //textInputLayoutPassword.setDefaultHintTextColor(myList);

        textInputEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
                try {
                    if (!email.isEmpty() && !password.isEmpty()) {
                        buttonFinish.setEnabled(true);
                    } else {
                        buttonFinish.setEnabled(false);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*Paso 1*/
    private void createAccount(String email, String password) {
//        title = "Por favor espere " + "(Rev)";
        title = "Por favor espere ";
//        message = "Cachuelito se encuentra verificando su información personal..." + "(Rev)";
        message = "Cachuelito se encuentra verificando su información personal...";

        showProgress(title, message);
//        showCustomProgressDialog(title, message);
//            Toast.makeText(getApplicationContext(), "Normalin", Toast.LENGTH_LONG).show();

        if (email != null && password != null) {
            normalReg(email, password);
        } else {
            Toast.makeText(getApplicationContext(), "Se produjo un error, por favor revise que la información ingresada sea correcta.", Toast.LENGTH_SHORT).show();
            closeProgress();
        }

        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            SharedPreferences myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editorPref = myPreferences.edit();
            int u = myPreferences.getInt("usuario", -1);
            if (u == 0) {
                Toast.makeText(getApplicationContext(), "Admin", Toast.LENGTH_LONG).show();

                regWithAdmin(email, password);
            }
        } else {
            showCustomProgressDialog(title, message);
//            Toast.makeText(getApplicationContext(), "Normalin", Toast.LENGTH_LONG).show();
            normalReg(email, password);
        }*/
//
//        showProgress(title, message);

    }

    public void regWithAdmin(String email, String password) {

        JSONObject jsonObject = new JSONObject();
        PostAsyncTask postAsyncTask = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("uid", "xxxxxxxxxxxx");
            jsonObject.put("displayName", usuarioEmpleador.getNombre() + " " + usuarioEmpleador.getApellido());
            jsonObject.put("email", email);
//                            jsonObject.put("phoneNumber", "+593983228466");
            jsonObject.put("phoneNumber", null);
            jsonObject.put("password", password);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        postAsyncTask = new PostAsyncTask(jsonObject.toString(), this);
        postAsyncTask.execute();
        postAsyncTask.setOnListenerAsyncTask(new PostAsyncTask.ClickListener() {
            @Override
            public void onTokenListener(String publicKey) {
                if (publicKey.equals("1")) {

                    /**
                     * Scar uid y respuetras
                     *
                     * */
                    boolean photoFlag = false;
//                    switch (regUsuario) {
//                        case 1:
//                            photoFlag = false;
//                            if (usuarioEmpleador.getFotoPerfil() != null) {
//                                Uri returnUri = Uri.parse(usuarioEmpleador.getFotoPerfil().toString());
//                                Cursor returnCursor = EmailPasswordActivity.this.getContentResolver().query(returnUri, null, null, null, null);
//                                /*
//                                 * Get the column indexes of the data in the Cursor,
//                                 * move to the first row in the Cursor, get the data,
//                                 * and display it.
//                                 */
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                returnCursor.moveToFirst();
//                                Log.d(TAG, returnCursor.getString(nameIndex));
//                                Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//
//                                if (returnCursor.getLong(sizeIndex) > 0) {
//                                    //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
//                                    Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//                                    photoFlag = true;
//                                } else {
//                                    //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
//                                    photoFlag = false;
//
//                                }
//                            } else {
//                                photoFlag = false;
//                            }
//                            /*Paso 2*/
//                            usuarioEmpleador.setIdUsuario(user.getUid());
//                            if (photoFlag) {
////                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
////                        closeProgress();
////                        closeCustomAlertDialog();
//                                title = "Por favor espere";
//                                message = "Su cuenta ya casi está lista!";
////                        showProgress(title, message);
//                                showCustomProgressDialog(title, message);
//
//                                usuarioEmpleador.registrarseEnFirebaseConFoto(EmailPasswordActivity.this, 1);
//                            } else {
//                                usuarioEmpleador.setFotoPerfil(null);
////                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                                usuarioEmpleador.registrarseEnFirebase(EmailPasswordActivity.this, 1);
//                            }
//                            break;
//                        case 2:
//                            photoFlag = false;
//                            if (usuarioTrabajador.getFotoPerfil() != null) {
//                                Uri returnUri = Uri.parse(usuarioTrabajador.getFotoPerfil().toString());
//                                Cursor returnCursor = EmailPasswordActivity.this.getContentResolver().query(returnUri, null, null, null, null);
//                                /*
//                                 * Get the column indexes of the data in the Cursor,
//                                 * move to the first row in the Cursor, get the data,
//                                 * and display it.
//                                 */
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                returnCursor.moveToFirst();
//                                Log.d(TAG, returnCursor.getString(nameIndex));
//                                Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//
//                                if (returnCursor.getLong(sizeIndex) > 0) {
//                                    //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
//                                    Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//                                    photoFlag = true;
//                                } else {
//                                    //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
//                                    photoFlag = false;
//                                }
//                            } else {
//                                photoFlag = false;
//                            }
//                            /*Paso 2*/
//                            usuarioTrabajador.setIdUsuario(user.getUid());
//                            if (photoFlag) {
////                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
////                        closeProgress();
////                        closeCustomAlertDialog();
//                                title = "Por favor espere";
//                                message = "Su cuenta ya casi está lista!";
////                        showProgress(title, message);
//                                showCustomProgressDialog(title, message);
//
//                                usuarioTrabajador.registrarseEnFirebaseConFoto(EmailPasswordActivity.this, 1);
//                            } else {
//                                usuarioTrabajador.setFotoPerfil(null);
////                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                                usuarioTrabajador.registrarseEnFirebase(EmailPasswordActivity.this, 1);
//                            }
//                            break;
//                    }

                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_inesperado, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void normalReg(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            Log.w(TAG, "signInWithPhone:failure -- " + errorCode);
                            switch (errorCode) {
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    alertDialogInfoError(0);
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    alertDialogInfoError(1);
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    alertDialogInfoError(2);
                                    break;
                            }

                            try {
                                closeProgress();
                            } catch (Exception exception) {

                            }

                        }
                    }
                });
        // [END create_user_with_email]
    }

    public void controlErrorsUI() {
        try {
            unlockbuttonFinalizar();
        } catch (Exception e) {

        }
    }

    private void unlockbuttonFinalizar() {
        buttonFinish.setEnabled(true);
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
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(RegWithEmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void updateUI(FirebaseUser user) {
        boolean photoFlag = false;
        if (user != null) {
            switch (regUsuario) {
                case 1:
                    photoFlag = false;
                    if (usuarioEmpleador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioEmpleador.getFotoPerfil().toString());
                        Cursor returnCursor = RegWithEmailPasswordActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                    usuarioEmpleador.setIdUsuario(user.getUid());
                    if (photoFlag) {
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
                        closeProgress();
//                        closeCustomAlertDialog();
                        title = "Por favor espere";
                        message = "Su cuenta ya casi está lista...";
                        showProgress(title, message);

                        usuarioEmpleador.registrarseEnFirebaseConFoto(RegWithEmailPasswordActivity.this, 1);
                    } else {
                        usuarioEmpleador.setFotoPerfil(null);
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
                        usuarioEmpleador.registrarseEnFirebase(RegWithEmailPasswordActivity.this, 1);
                    }
                    break;
                case 2:
                    photoFlag = false;
                    if (usuarioTrabajador.getFotoPerfil() != null) {
                        Uri returnUri = Uri.parse(usuarioTrabajador.getFotoPerfil().toString());
                        Cursor returnCursor = RegWithEmailPasswordActivity.this.getContentResolver().query(returnUri, null, null, null, null);
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
                    usuarioTrabajador.setIdUsuario(user.getUid());
                    if (photoFlag) {
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
                        closeProgress();
//                        closeCustomAlertDialog();
                        title = "Por favor espere";
                        message = "Su cuenta ya casi está lista...";
                        showProgress(title, message);

                        usuarioTrabajador.registrarseEnFirebaseConFoto(RegWithEmailPasswordActivity.this, 1);
                    } else {
                        usuarioTrabajador.setFotoPerfil(null);
//                        Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
                        usuarioTrabajador.registrarseEnFirebase(RegWithEmailPasswordActivity.this, 1);
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
            case R.id.buttonfinish:
                switch (regUsuario) {
                    case 1:
                        empleador.setEmail(email);
                        usuarioEmpleador = new Empleador();
                        usuarioEmpleador = empleador;

                        AtomicBoolean estadoEmpleador = new AtomicBoolean(false);
                        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(RegWithEmailPasswordActivity.this).get(EmpleadorViewModel.class);

                        String contactoVeri = "";
                        if (empleador.getEmail() != null) {
                            contactoVeri = empleador.getEmail();
                        }

                        for (Empleador e : empleadorListByEmail) {
                            if (e.getEmail() != null) {
                                if (e.getEmail().equals(contactoVeri)) {
                                    estadoEmpleador.set(true);
                                }
                            }
                        }

                        for (Trabajador t : trabajadorListByEmail) {
                            if (t.getEmail() != null) {
                                if (t.getEmail().equals(contactoVeri)) {
                                    estadoEmpleador.set(true);
                                }
                            }
                        }

                        if (estadoEmpleador.get()) {
                            this.alertDialogInfoError(0);
                        } else {

                            networkFlag = myPreferences.getBoolean("networkFlag", false);
                            sPref = defaultSharedPreferences.getBoolean("sync_network", true);

                            Log.d(TAG, String.valueOf(sPref));
                            Log.d(TAG, String.valueOf(networkFlag));

                            if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                                // AsyncTask subclass
                                //new DownloadXmlTask().execute(URL);
                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                                boolean isMetered = cm.isActiveNetworkMetered();


                                if (isMetered) {
                                    alertDialogContinuarRegistroConDatos();
                                } else {
                                    createAccount(usuarioEmpleador.getEmail(), password);
                                }

//                                if (networkTool.isOnlineWithWifi()) {
//                                    //empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
//                                    createAccount(usuarioEmpleador.getEmail(), password);
//                                } else {
//                                    if (networkTool.isOnlineWithData()) {
//                                        if (!sPref) {
//                                            alertDialogContinuarRegistroConDatos();
//                                        } else {
//
//                                            networkTool.alertDialogNoConectadoWifiInfo();
//                                        }
////                                        networkTool.alertDialogNoConectadoWifiInfo();
//                                    } else {
//
//                                        networkTool.alertDialogNoConectadoInfo();
//                                    }
//                                }
                            } else {

                                networkTool.alertDialogNoConectadoInfo();
                            }

                        }

                        break;
                    case 2:
                        trabajador.setEmail(email);
                        usuarioTrabajador = new Trabajador();
                        usuarioTrabajador = trabajador;

                        networkFlag = myPreferences.getBoolean("networkFlag", false);
                        sPref = defaultSharedPreferences.getBoolean("sync_network", true);


                        AtomicBoolean estadoTrabajador = new AtomicBoolean(false);

                        String contactoTrabajador = "";
                        if (usuarioTrabajador.getEmail() != null) {
                            contactoTrabajador = usuarioTrabajador.getEmail();
                        }

                        for (Empleador e : empleadorListByEmail) {
                            if (e.getEmail() != null) {
                                if (e.getEmail().equals(contactoTrabajador)) {
                                    estadoTrabajador.set(true);
                                }
                            }
                        }

                        for (Trabajador t : trabajadorListByEmail) {
                            if (t.getEmail() != null) {
                                if (t.getEmail().equals(contactoTrabajador)) {
                                    estadoTrabajador.set(true);
                                }
                            }
                        }

                        if (estadoTrabajador.get()) {
                            this.alertDialogInfoError(0);
                        } else {

                            if (((!sPref) && (networkFlag)) || ((sPref) && (networkFlag))) {
                                // AsyncTask subclass
//                                if (networkTool.isOnlineWithWifi()) {
//                                    createAccount(usuarioTrabajador.getEmail(), password);
//                                    //Toast.makeText(getApplicationContext(), usuarioTrabajador.toString(), Toast.LENGTH_LONG).show();
//                                    Log.d(TAG, usuarioTrabajador.toString());
//                                } else {
//                                    if (networkTool.isOnlineWithData()) {
//                                        if (!sPref) {
//                                            alertDialogContinuarRegistroConDatos();
//                                        } else {
//                                            networkTool.alertDialogNoConectadoWifiInfo();
//                                        }
////                                        networkTool.alertDialogNoConectadoWifiInfo();
//                                    } else {
//                                        networkTool.alertDialogNoConectadoInfo();
//                                    }
//                                }

                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                                boolean isMetered = cm.isActiveNetworkMetered();


                                if (isMetered) {
                                    alertDialogContinuarRegistroConDatos();
                                } else {
                                    createAccount(usuarioTrabajador.getEmail(), password);
                                }

                            } else {
                                networkTool.alertDialogNoConectadoInfo();
                            }


                        }


                        break;
                }
                break;

            case R.id.buttonInfo:

                dialogInfo = alertDialogInfo();
                dialogInfo.show();
                break;
        }
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
            case 0:/*email repetido*/
                textViewInfo.setText(getResources().getString(R.string.text_error_email_repetido));
                builder.setPositiveButton("Iniciar sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sign in the user ...

                                try {
//                            Intent intent = new Intent(RegWithEmailPasswordActivity.this, LoginEmailPasswordActivity.class);
                                    Intent intent = new Intent(RegWithEmailPasswordActivity.this, LoginActivity.class);
//                            intent.putExtra("email", email);
//                            intent.putExtra("password", password);
                                    startActivity(intent);
                                    dialogInfo.dismiss();

                                } catch (Exception e) {

                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeProgress();
//                                closeCustomAlertDialog();

                            }
                        });
                break;
            case 1:/*clave insegura*/
                textViewInfo.setText(getResources().getString(R.string.text_error_password));
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
                break;
            case 2:/*email inválido*/
                textViewInfo.setText(getResources().getString(R.string.text_error_email));
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
                break;
        }


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

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

    public void closeProgress() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }


    public Dialog alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_reg_email_password));


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
        return builder.create();
    }

    public Dialog alertDialogInfoNoSePuedeRegistrar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_error_email_repetido));


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
        return builder.create();
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
                switch (regUsuario) {
                    case 1:
                        //empleador.crearCuentaConEmailPassword(mAuth, password, (Activity) RegWithEmailPasswordActivity.this);
                        //usuarioEmpleador.registrarseEnFirebase(EmailPasswordActivity.this,1);
                        createAccount(usuarioEmpleador.getEmail(), password);
                        break;
                    case 2:
                        createAccount(usuarioTrabajador.getEmail(), password);
                        //usuarioTrabajador.registrarseEnFirebase(EmailPasswordActivity.this,1);
                        break;
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // Unregisters BroadcastReceiver when app is destroyed.
            if (receiver != null) {
                this.unregisterReceiver(receiver);
            }
        } catch (Exception e) {

        }

    }

}