package com.marlon.apolo.tfinal2022.registro.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.OficioPoc;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.view.adapters.OficioCrazyRegistroListAdapter;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.viewModel.OficioViewModel;


import java.util.ArrayList;

public class RegistroOficioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroOficioActivity.class.getSimpleName();
    private int regUsuario;
    private Trabajador trabajador;
    private OficioViewModel oficioViewModel;
    //    private OficioRegistroListAdapter oficioRegistroListAdapter;
    private Dialog dialogInfo;
    private Dialog dialogNuevoOficio;
    private OficioCrazyRegistroListAdapter oficioCrazyRegistroListAdapter;
    private ChildEventListener childEventListenerOficios;
    private SharedPreferences myPreferences;

    public void loadOficios() {
        childEventListenerOficios = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Oficio oficioDb = snapshot.getValue(Oficio.class);
                OficioPoc oficioPoc = new OficioPoc();
                oficioPoc.setIdOficio(oficioDb.getIdOficio());
                oficioPoc.setNombre(oficioDb.getNombre());
                oficioPoc.setUriPhoto(oficioDb.getUriPhoto());

                oficioCrazyRegistroListAdapter.addOficio(oficioPoc);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Oficio oficioDbChanged = snapshot.getValue(Oficio.class);
                int index = 0;
                for (OficioPoc ofPoc : oficioCrazyRegistroListAdapter.getOficioPocArrayList()) {
                    if (ofPoc.getIdOficio().equals(oficioDbChanged.getIdOficio())) {
                        ofPoc.setNombre(oficioDbChanged.getNombre());
                        ofPoc.setUriPhoto(oficioDbChanged.getUriPhoto());
                        oficioCrazyRegistroListAdapter.updateOficioArrayList(index, ofPoc);
                        break;
                    }
                    index++;
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Oficio oficioDbRemoved = snapshot.getValue(Oficio.class);
                int index = 0;
                for (OficioPoc ofPoc : oficioCrazyRegistroListAdapter.getOficioPocArrayList()) {
                    if (ofPoc.getIdOficio().equals(oficioDbRemoved.getIdOficio())) {
                        oficioCrazyRegistroListAdapter.removeOficioArrayList(index);
                        break;
                    }
                    index++;
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("oficios")
                .orderByChild("nombre")
                .addChildEventListener(childEventListenerOficios);
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
        textViewInfo.setText(getResources().getString(R.string.text_select_oficios));


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

    public void alertDialogNuevoOficio() {

        Log.d("TAG", "Registrando nuevo oficio....");
        LayoutInflater inflater = LayoutInflater.from(this);


        View promptsView = inflater.inflate(R.layout.textinput_custom, null);


        dialogNuevoOficio = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nuevo oficio:")
                .setView(promptsView)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Oficio oficio = new Oficio();
                        final TextInputEditText input = (TextInputEditText) promptsView;
                        oficio.setNombre(input.getText().toString());
                        if (!input.getText().toString().equals("")) {
//                            mViewModel.registerJobOnFirebase(job, jobAdapter);
                            int exit = 0;
                            oficio.setNombre(oficio.getNombre().trim());

                            if (oficioCrazyRegistroListAdapter.getOficioPocArrayList() != null) {
                                for (Oficio o : oficioCrazyRegistroListAdapter.getOficioPocArrayList()) {
                                    if (o.getNombre().toUpperCase().equals(oficio.getNombre().toUpperCase())) {
                                        exit = 1;
                                        break;
                                    }
                                }
                            } else {
                                exit = 0;
                            }

                            if (exit == 1) {
                                Toast.makeText(getApplicationContext(), "Registro fallido!", Toast.LENGTH_LONG).show();
                                exit = 0;
                            } else {
//                                oficioViewModel.addOficioToFirebase(RegistroOficioActivity.this, oficio);
                                trabajador.crearOficio(oficioViewModel, RegistroOficioActivity.this, oficio);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Nombre de oficio inválido.", Toast.LENGTH_LONG).show();
                        }
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                        input.setText("");
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final TextInputEditText input = (TextInputEditText) promptsView;
                        input.setText("");
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                    }
                }).create();
        dialogNuevoOficio.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_oficio);
        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewOficios);
//        oficioRegistroListAdapter = new OficioRegistroListAdapter(this);
//        recyclerView.setAdapter(oficioRegistroListAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);


        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);

        oficioCrazyRegistroListAdapter = new OficioCrazyRegistroListAdapter(this);
        recyclerView.setAdapter(oficioCrazyRegistroListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOficios();


        findViewById(R.id.buttonNext).setOnClickListener(this);
        findViewById(R.id.buttonInfo).setOnClickListener(this);
        findViewById(R.id.fabANuevoOficio).setOnClickListener(this);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:


                ArrayList<Oficio> oficiosReg = new ArrayList<>();
                ArrayList<String> idOficiosReg = new ArrayList<>();
                ArrayList<String> idHabilidadesReg = new ArrayList<>();
                if (oficioCrazyRegistroListAdapter.getItemCount() > 0) {
                    for (OficioPoc o : oficioCrazyRegistroListAdapter.getOficioPocArrayList()) {
                        if (o.isEstadoRegistro()) {
                            oficiosReg.add(o);
                            idOficiosReg.add(o.getIdOficio());

                        }
                    }
                    if (oficiosReg.size() > 0) {
                        trabajador.setIdOficios(idOficiosReg);
//                        Intent intent = new Intent(RegistroOficioActivity.this, MetodoRegActivity.class);

//        startActivity(new Intent(this, PoCActivity.class));
                        int checkAdmin = myPreferences.getInt("usuario", -1);
                        Intent intent = new Intent(RegistroOficioActivity.this, RegWithEmailPasswordActivity.class);

                        if (checkAdmin == 0) {
                            intent = new Intent(RegistroOficioActivity.this, RegWithEmailPasswordActivityAdmin.class);
                        }

//                        Intent intent = new Intent(RegistroOficioActivity.this, RegWithEmailPasswordActivity.class);
//                        Intent intent = new Intent(RegistroOficioActivity.this, RegistroHabilidadActivity.class);
                        switch (regUsuario) {
//                            case 1:/*empleador*/
//
//                                intent.putExtra("usuario", regUsuario);
//                                intent.putExtra("empleador", empleador);
//                                break;
                            case 2:/*trabajador*/

                                intent.putExtra("usuario", regUsuario);
                                intent.putExtra("trabajador", trabajador);
                                break;
                        }
                        startActivity(intent);


                        Log.d(TAG, String.valueOf(oficiosReg.size()));
                        Log.d(TAG, String.valueOf(idHabilidadesReg.size()));
                    } else {
                        Toast.makeText(getApplicationContext(), "No ha seleccionado ningún oficio", Toast.LENGTH_LONG).show();
                    }

                } else {
//                    Toast.makeText(getApplicationContext(), "No ha seleccionado ningún oficio", Toast.LENGTH_LONG).show();
                }


                break;
            case R.id.buttonInfo:
                alertDialogInfo();
                break;
            case R.id.fabANuevoOficio:
                alertDialogNuevoOficio();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            oficioViewModel.removeChildListener();
        } catch (Exception e) {

        }
        try {
            FirebaseDatabase.getInstance().getReference().child("oficios")
                    .orderByChild("nombre")
                    .removeEventListener(childEventListenerOficios);
        } catch (Exception e) {

        }
    }
}