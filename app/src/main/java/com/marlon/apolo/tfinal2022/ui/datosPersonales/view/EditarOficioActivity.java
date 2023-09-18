package com.marlon.apolo.tfinal2022.ui.datosPersonales.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.OficioPoc;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.receivers.NetworkReceiver;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.adapters.EditarOficioSuperSpecialListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.viewModel.OficioViewModel;

import java.util.ArrayList;

public class EditarOficioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditarOficioActivity.class.getSimpleName();
    private RecyclerView recyclerViewOficiosHabilidades;
    private Trabajador trabajador;
    private AlertDialog dialogNuevoOficio;
    private OficioViewModel oficioViewModel;
    private EditarOficioSuperSpecialListAdapter editarOficioSuperSpecialListAdapter;
    private ChildEventListener childEventListenerOficios;
    private NetworkReceiver receiver;

    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences myPreferences;
    private boolean networkFlag;
    public static boolean sPref;
    private NetworkTool networkTool;
    private AlertDialog dialogInfo;

    public void loadOficios() {
        childEventListenerOficios = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Oficio oficioDb = snapshot.getValue(Oficio.class);
                OficioPoc oficioPoc = new OficioPoc();
                oficioPoc.setIdOficio(oficioDb.getIdOficio());
                oficioPoc.setNombre(oficioDb.getNombre());
                oficioPoc.setUriPhoto(oficioDb.getUriPhoto());
                for (String idOf : trabajador.getIdOficios()) {
                    if (idOf.equals(oficioDb.getIdOficio())) {
                        oficioPoc.setEstadoRegistro(true);
                        break;
                    }
                }
                editarOficioSuperSpecialListAdapter.addOficio(oficioPoc);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Oficio oficioDbChanged = snapshot.getValue(Oficio.class);
                int index = 0;
                for (OficioPoc ofPoc : editarOficioSuperSpecialListAdapter.getOficioArrayList()) {
                    if (ofPoc.getIdOficio().equals(oficioDbChanged.getIdOficio())) {
                        ofPoc.setNombre(oficioDbChanged.getNombre());
                        ofPoc.setUriPhoto(oficioDbChanged.getUriPhoto());
                        editarOficioSuperSpecialListAdapter.updateOficioArrayList(index, ofPoc);
                        break;
                    }
                    index++;
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Oficio oficioDbRemoved = snapshot.getValue(Oficio.class);
                int index = 0;
                for (OficioPoc ofPoc : editarOficioSuperSpecialListAdapter.getOficioArrayList()) {
                    if (ofPoc.getIdOficio().equals(oficioDbRemoved.getIdOficio())) {
                        editarOficioSuperSpecialListAdapter.removeOficioArrayList(index);
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

                            if (editarOficioSuperSpecialListAdapter.getOficioArrayList() != null) {
                                for (Oficio o : editarOficioSuperSpecialListAdapter.getOficioArrayList()) {
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
                                oficioViewModel.addOficioToFirebase(EditarOficioActivity.this, oficio);
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
                trabajador.actualizarInfo(EditarOficioActivity.this);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_oficio_habilidad);

        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);

        trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
        //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, trabajador.toString());


        recyclerViewOficiosHabilidades = findViewById(R.id.recyclerViewOficiosYHabilidades);
        findViewById(R.id.buttonUpdateOficios).setOnClickListener(this);
        findViewById(R.id.fabANuevoOficio).setOnClickListener(this);

//        specialOficioListAdapter = new SpecialOficio2ListAdapter(this);


        editarOficioSuperSpecialListAdapter = new EditarOficioSuperSpecialListAdapter(this);
        recyclerViewOficiosHabilidades.setAdapter(editarOficioSuperSpecialListAdapter);
        recyclerViewOficiosHabilidades.setLayoutManager(new LinearLayoutManager(EditarOficioActivity.this));


        loadOficios();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /*****************************/
        networkTool = new NetworkTool(this);

        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        // Gets the user's network preference settings
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences = EditarOficioActivity.this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        /*******************************/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdateOficios:
                ArrayList<String> idsOfi = new ArrayList<>();
                ArrayList<String> idsHab = new ArrayList<>();
                for (OficioPoc o : editarOficioSuperSpecialListAdapter.getOficioArrayList()) {
                    if (o.isEstadoRegistro()) {
                        idsOfi.add(o.getIdOficio());
                        try {

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
//                    Log.d(TAG, o.getNombre());
//                    Log.d(TAG, o.getHabilidadArrayList().toString());
                }
                trabajador.setIdOficios(idsOfi);
                if (!trabajador.getIdOficios().isEmpty()) {

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
                            trabajador.actualizarInfo(this);
                        }

                    } else {

                        networkTool.alertDialogNoConectadoInfo();
                    }


                    //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Para continuar por favor seleccione al menos un oficio de los registrados en la lista.", Toast.LENGTH_LONG).show();
                }
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
            // Unregisters BroadcastReceiver when app is destroyed.
            if (receiver != null) {
                this.unregisterReceiver(receiver);
            }
        } catch (Exception e) {

        }

    }
}