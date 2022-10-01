package com.marlon.apolo.tfinal2022.ui.eliminarCuenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

public class EliminarInfoEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EliminarInfoEmailActivity.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private Usuario usuarioLocal;
    private String password;
    private String email;
    private Button buttonEliminar;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextEmail;
    private ProgressDialog progressDialog;
    private AlertDialog dialogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_info_email);


        firebaseAuth = FirebaseAuth.getInstance();
        email = "";
        password = "";

        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(firebaseAuth.getCurrentUser().getUid())
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
                .child(firebaseAuth.getCurrentUser().getUid())
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonEliminar = findViewById(R.id.btnEliminar);

        findViewById(R.id.buttonInfo).setOnClickListener(this);
        buttonEliminar.setEnabled(false);
        buttonEliminar.setOnClickListener(this);

        textInputEditTextEmail = findViewById(R.id.editTextEmail);
        textInputEditTextPassword = findViewById(R.id.editTextPassword);

        textInputEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    buttonEliminar.setEnabled(true);
                } else {
                    buttonEliminar.setEnabled(false);
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
                    buttonEliminar.setEnabled(true);
                } else {
                    buttonEliminar.setEnabled(false);
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
            case R.id.btnEliminar:
//                Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), password, Toast.LENGTH_LONG).show();
                if (!email.isEmpty() && !password.isEmpty()) {

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(EliminarInfoEmailActivity.this, "La tarea no ha podido ser completada.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(EliminarInfoEmailActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                        updateUI(null);
                                    }
                                }
                            });
                }
                break;
            case R.id.buttonInfo:
                alertDialogInfo();
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
        textViewInfo.setText(getResources().getString(R.string.text_eliminar_info_con_email));


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


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String title = "Por favor espere";
            String message = "En estos momentos nos encontramos eliminando su infomación...";
            showProgress(title, message);
            deleteAccount(usuarioLocal);
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
        Log.d(TAG, firebaseAuth.getCurrentUser().getUid());
        Log.d(TAG, usuario.toString());
        usuario.eliminarInfo(locationToFirebase, this);
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
}