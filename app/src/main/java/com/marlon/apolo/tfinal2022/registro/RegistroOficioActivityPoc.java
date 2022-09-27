package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.adaptadores.HabilidadListAdapterPoc;
import com.marlon.apolo.tfinal2022.registro.adaptadores.OficioListAdapterPoc;

import java.util.ArrayList;

public class RegistroOficioActivityPoc extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroOficioActivityPoc.class.getSimpleName();
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private AlertDialog dialogInfo;
    private ProgressBar progressBar;
    private OficioListAdapterPoc oficioListAdapterPoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_oficio_poc);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.buttonNext).setOnClickListener(this);
        findViewById(R.id.buttonInfo).setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_SHORT).show();
                break;
        }

        oficioListAdapterPoc = new OficioListAdapterPoc(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOficios);
        recyclerView.setAdapter(oficioListAdapterPoc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//        HabilidadListAdapterPoc habilidadListAdapterPoc = new HabilidadListAdapterPoc(this);


        ArrayList<Oficio> oficioArrayList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("oficios").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded");
                //Log.d(TAG, snapshot.getKey());
                Oficio oficio = snapshot.getValue(Oficio.class);
//                Log.d(TAG, oficio.toString());
                FirebaseDatabase.getInstance().getReference().child("habilidades")
                        .child(oficio.getIdOficio())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d(TAG, oficio.toString());
                                ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Habilidad habilidad = data.getValue(Habilidad.class);
                                    habilidadArrayList.add(habilidad);
                                    //Log.w(TAG, habilidad.toString());
                                }
//                                recyclerView.setAdapter(habilidadListAdapterPoc);
//                                recyclerView.setLayoutManager(new LinearLayoutManager(RegistroOficioActivityPoc.this));


                                oficio.setHabilidadArrayList(habilidadArrayList);
                                Log.w(TAG, oficio.getHabilidadArrayList().toString());
//                                if (oficio.getHabilidadArrayList() != null) {
//                                    if (oficio.getHabilidadArrayList().size()>5){
//                                        habilidadListAdapterPoc.setHabillidades(oficio.getHabilidadArrayList());
//
//                                    }
//                                }
                                oficioArrayList.add(oficio);
                                oficioListAdapterPoc.setOficios(oficioArrayList);

                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged");
                //Log.d(TAG, snapshot.getKey());
                Oficio oficio = snapshot.getValue(Oficio.class);
                Log.d(TAG, oficio.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved");
                //Log.d(TAG, snapshot.getKey());
                Oficio oficio = snapshot.getValue(Oficio.class);
                Log.d(TAG, oficio.toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:


                ArrayList<Oficio> oficiosReg = new ArrayList<>();
                ArrayList<String> idOficiosReg = new ArrayList<>();
                ArrayList<String> idHabilidadesReg = new ArrayList<>();
                if (oficioListAdapterPoc.getItemCount() > 0) {
                    for (Oficio o : oficioListAdapterPoc.getOficioList()) {
                        if (o.isEstadoRegistro()) {
                            oficiosReg.add(o);
                            idOficiosReg.add(o.getIdOficio());
                            try {
                                for (Habilidad h : o.getHabilidadArrayList()) {
                                    if (h.isHabilidadSeleccionada()) {
                                        idHabilidadesReg.add(h.getIdHabilidad());
                                    }
                                }
                            } catch (Exception e) {

                            }

                        }
                    }
                }
                Log.d(TAG, idOficiosReg.toString());
                Log.d(TAG, String.valueOf(idOficiosReg.size()));
//                    if (oficiosReg.size() > 0) {
//                        trabajador.setIdOficios(idOficiosReg);
//                        trabajador.setIdHabilidades(idHabilidadesReg);
//                        Intent intent = new Intent(RegistroOficioActivityPoc.this, MetodoRegActivity.class);
////                        Intent intent = new Intent(RegistroOficioActivity.this, RegistroHabilidadActivity.class);
//                        switch (regUsuario) {
//                            case 1:/*empleador*/
//
//                                intent.putExtra("usuario", regUsuario);
//                                intent.putExtra("empleador", empleador);
//                                break;
//                            case 2:/*trabajador*/
//
//                                intent.putExtra("usuario", regUsuario);
//                                intent.putExtra("trabajador", trabajador);
//                                break;
//                        }
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No ha seleccionado ningún oficio", Toast.LENGTH_LONG).show();
//                    }
//
//                } else {
////                    Toast.makeText(getApplicationContext(), "No ha seleccionado ningún oficio", Toast.LENGTH_LONG).show();
//                }


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

}