package com.marlon.apolo.tfinal2022.ui.datosPersonales.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores.SpecialOficioListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;

import java.util.ArrayList;

public class EditarOficioHabilidadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditarOficioHabilidadActivity.class.getSimpleName();
    private SpecialOficioListAdapter specialOficioListAdapter;
    private RecyclerView recyclerViewOficiosHabilidades;
    private BienvenidoViewModel bienvenidoViewModel;
    private Trabajador trabajador;
    private ArrayList<Oficio> oficiosDB;
    private ArrayList<Oficio> oficioArrayListFilter;
    private AlertDialog dialogNuevoOficio;
    private OficioViewModel oficioViewModel;
    private String oficioSelected;
    private int positionSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_oficio_habilidad);

        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);

        trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
        Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
        oficioArrayListFilter = new ArrayList<>();
        Log.d(TAG, trabajador.toString());
        oficioSelected = "";
        positionSelected = -1;


        recyclerViewOficiosHabilidades = findViewById(R.id.recyclerViewOficiosYHabilidades);
        findViewById(R.id.buttonUpdateOficiosHabilidades).setOnClickListener(this);

        specialOficioListAdapter = new SpecialOficioListAdapter(this);
        recyclerViewOficiosHabilidades.setAdapter(specialOficioListAdapter);
        recyclerViewOficiosHabilidades.setLayoutManager(new LinearLayoutManager(this));


//        FirebaseDatabase.getInstance().getReference().child("oficios")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Log.e(TAG, "Oficio - onChildAdded ");
//                        try {
//                            Oficio oficioDB = snapshot.getValue(Oficio.class);
//                            FirebaseDatabase.getInstance().getReference().child("habilidades")
//                                    .child(oficioDB.getIdOficio())
//                                    .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//                                            for (DataSnapshot data : snapshot.getChildren()) {
//                                                Habilidad habilidad = data.getValue(Habilidad.class);
//                                                for (String idHabiliades : trabajador.getIdHabilidades()) {
//                                                    if (idHabiliades.equals(habilidad.getIdHabilidad())) {
//                                                        habilidad.setHabilidadSeleccionada(true);
//                                                        break;
//                                                    }
//                                                }
//                                                habilidadArrayList.add(habilidad);
//                                            }
//                                            oficioDB.setHabilidadArrayList(habilidadArrayList);
//
//
//                                            /***************************************************************************************/
//
//
//
//                                            /***************************************************************************************/
//
//
//                                            boolean flagExit = false;
//                                            if (oficioArrayListFilter != null) {
//                                                int index = 0;
//                                                for (Oficio ox : oficioArrayListFilter) {
//                                                    if (ox.getIdOficio().equals(oficioDB.getIdOficio())) {
//                                                        oficioArrayListFilter.set(index, oficioDB);
//                                                        flagExit = true;
//                                                        break;
//                                                    }
//                                                    index++;
//                                                }
//                                            }
//
//                                            if (flagExit) {
//
//                                            } else {
//                                                oficioArrayListFilter.add(oficioDB);
//                                            }
//
//                                            specialOficioListAdapter.setOficios(oficioArrayListFilter);
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//                        } catch (Exception e) {
//                            Log.d(TAG, e.toString());
//                        }
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Log.e(TAG, "Oficio - onChildChanged ");
//
//                        try {
//                            Oficio oficioChanged = snapshot.getValue(Oficio.class);
//                            int index = 0;
////                            for (Oficio ox : oficioArrayListFilter) {
//                            for (Oficio ox : specialOficioListAdapter.getOficios()) {
//                                if (ox.getIdOficio().equals(oficioChanged.getIdOficio())) {
//                                    ArrayList<Habilidad> habilidadAux = ox.getHabilidadArrayList();
//                                    oficioChanged.setHabilidadArrayList(habilidadAux);
//                                    oficioArrayListFilter.set(index, oficioChanged);
//                                    break;
//                                }
//                                index++;
//                            }
//                            specialOficioListAdapter.setOficios(oficioArrayListFilter);
//
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        Log.e(TAG, "Oficio - onChildRemoved ");
//
//                        try {
//                            Oficio oficioRemoved = snapshot.getValue(Oficio.class);
//                            int index = 0;
//                            for (Oficio ox : oficioArrayListFilter) {
//                                if (ox.getIdOficio().equals(oficioRemoved.getIdOficio())) {
//                                    oficioArrayListFilter.remove(index);
//                                    break;
//                                }
//                                index++;
//                            }
//                            specialOficioListAdapter.setOficios(oficioArrayListFilter);
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        try {
                            Oficio oficio = snapshot.getValue(Oficio.class);
                            Log.d(TAG, oficio.toString());
                            for (String idOfx : trabajador.getIdOficios()) {
                                if (oficio.getIdOficio().equals(idOfx)) {
                                    oficio.setEstadoRegistro(true);
                                    break;
                                }
                            }
                            oficioArrayListFilter.add(oficio);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("habilidades")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "######################################");
                        Log.d(TAG, "hadilidades - onChildAdded");
                        Log.d(TAG, snapshot.getKey());
                        Log.d(TAG, "######################################");
//                        try {

                        for (Oficio oficio : oficioArrayListFilter) {
                            Log.d(TAG, oficio.toString());
                            if (oficio.getIdOficio().equals(snapshot.getKey())) {
                                ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//                                try {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Habilidad habilidad = data.getValue(Habilidad.class);
//                                    Log.d(TAG, habilidad.toString());
                                    try {/*el trabajador puede tener oficios en null y por lo tanto se presentaba un error*/
                                        for (String idHab : trabajador.getIdHabilidades()) {
                                            if (idHab.equals(habilidad.getIdHabilidad())) {
                                                habilidad.setHabilidadSeleccionada(true);
                                                Log.d(TAG, habilidad.toString());

                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
//                                        e.printStackTrace();
                                    }

                                    habilidadArrayList.add(habilidad);
                                    Log.d(TAG, habilidad.toString());
                                }
                                oficio.setHabilidadArrayList(habilidadArrayList);
                                break;
//                                } catch (Exception e) {
//                                    Log.e(TAG, e.toString());
//                                }
                            }
                        }
                        specialOficioListAdapter.setOficios(oficioArrayListFilter);
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "######################################");
                        Log.d(TAG, "hadilidades - onChildChanged");
                        Log.d(TAG, snapshot.getKey());
                        Log.d(TAG, "######################################");

                        int sizeLocal = 0;
                        int sizeDb = 0;
                        ArrayList<Habilidad> habilidadArrayListVerificado = new ArrayList<>();

                        try {
                            for (Oficio ox : specialOficioListAdapter.getOficios()) {
                                if (ox.getIdOficio().equals(snapshot.getKey())) {
                                    sizeLocal = ox.getHabilidadArrayList().size();
                                    habilidadArrayListVerificado = new ArrayList<>();
                                    for (Habilidad haux : ox.getHabilidadArrayList()) {
                                        Log.d(TAG, "H Local: " + haux.toString());
                                        habilidadArrayListVerificado.add(haux);
                                    }
                                }

                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                        ArrayList<Habilidad> habilidadArrayListChanged = new ArrayList<>();
                        try {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Habilidad habilidad = data.getValue(Habilidad.class);
                                Log.d(TAG, "H DB: " + habilidad.toString());
                                habilidadArrayListChanged.add(habilidad);
                            }
                            sizeDb = habilidadArrayListChanged.size();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }


                        ArrayList<Habilidad> habilidadArrayListAuxiliar = new ArrayList<>();
                        if (sizeLocal == sizeDb) {
                            Log.d(TAG, "sizeLocal == sizeDb");
//                            for (Habilidad hC : habilidadArrayListChanged) {
//                                Log.d(TAG, "H CDB: " + hC.toString());
//                            }

                            habilidadArrayListAuxiliar = new ArrayList<>();
                            for (Habilidad hV : habilidadArrayListVerificado) {
//                                Log.d(TAG, "Haux: " + hV.toString());
                                for (Habilidad hC : habilidadArrayListChanged) {
//                                    Log.d(TAG, "H CDB: " + hC.toString());
                                    if (hV.getIdHabilidad().equals(hC.getIdHabilidad())) {
                                        hV.setNombreHabilidad(hC.getNombreHabilidad());
                                        habilidadArrayListAuxiliar.add(hV);
                                        break;
                                    }
                                }
                            }

//                            for (Habilidad hV : habilidadArrayListVerificado) {
//                                Log.d(TAG, "H Filtrados: " + hV.toString());
//                            }

                            for (Habilidad hF : habilidadArrayListAuxiliar) {
                                Log.d(TAG, "H Filtrados: " + hF.toString());
                            }

                            try {/*el trabajador puede tener oficios en null y por lo tanto se presentaba un error*/
                                for (String idHab : trabajador.getIdHabilidades()) {
                                    for (Habilidad hF : habilidadArrayListAuxiliar) {
                                        Log.d(TAG, "H Filtrados: " + hF.toString());
                                        if (idHab.equals(hF.getIdHabilidad())) {
                                            hF.setHabilidadSeleccionada(true);
//                                            Log.d(TAG, habilidad.toString());

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
//                                        e.printStackTrace();
                            }

                        }

                        if (sizeDb > sizeLocal) {
                            Log.d(TAG, "sizeDb > sizeLocal");
//
//                            for (Habilidad hC : habilidadArrayListChanged) {
//                                Log.d(TAG, "H DB: " + hC.toString());
//                            }


//                            for (Habilidad hC : habilidadArrayListChanged) {
//                                Log.d(TAG, "H DB: " + hC.toString());
//                                for (Habilidad hV : habilidadArrayListVerificado) {
//                                    Log.d(TAG, "Haux: " + hV.toString());
//                                    if (hC.getIdHabilidad().equals(hV.getIdHabilidad())) {
//                                        hV.setNombreHabilidad(hC.getNombreHabilidad());
//                                        habilidadArrayListAuxiliar.add(hV);
//                                    }
//                                }
//                            }

                            habilidadArrayListVerificado.add(habilidadArrayListChanged.get(habilidadArrayListChanged.size() - 1));
                            for (Habilidad hV : habilidadArrayListVerificado) {
//                                Log.d(TAG, "HauxV: " + hV.toString());
                                Log.d(TAG, "H Filtrados: " + hV.toString());
                            }
                            habilidadArrayListAuxiliar = new ArrayList<>();
                            habilidadArrayListAuxiliar.addAll(habilidadArrayListVerificado);

//
//                            for (Habilidad hF : habilidadArrayListAuxiliar) {
//                                Log.d(TAG, "Filtrados: " + hF.toString());
//                            }
                        }
                        if (sizeDb < sizeLocal) {
//                            Log.d(TAG, "sizeDb < sizeLocal");
                            for (Habilidad hC : habilidadArrayListChanged) {
//                                Log.d(TAG, "H DB: " + hC.toString());
                                for (Habilidad hV : habilidadArrayListVerificado) {
//                                    Log.d(TAG, "Haux: " + hV.toString());
                                    if (hC.getIdHabilidad().equals(hV.getIdHabilidad())) {
                                        hV.setNombreHabilidad(hC.getNombreHabilidad());
                                        habilidadArrayListAuxiliar.add(hV);
                                    }
                                }
                            }

//                            for (Habilidad hV : habilidadArrayListVerificado) {
//                                Log.d(TAG, "Haux: " + hV.toString());
//                            }
//
                            for (Habilidad hF : habilidadArrayListAuxiliar) {
                                Log.d(TAG, "H Filtrados: " + hF.toString());
                            }


                        }

                        try {
                            for (Oficio of : specialOficioListAdapter.getOficios()) {
                                if (of.getIdOficio().equals(snapshot.getKey())) {
                                    of.setHabilidadArrayList(habilidadArrayListAuxiliar);
                                    break;
                                }
                            }
                            specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                    }


                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "######################################");
                        Log.d(TAG, "hadilidades - onChildRemoved");
                        Log.d(TAG, snapshot.getKey());
                        Log.d(TAG, "######################################");
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdateOficiosHabilidades:
                ArrayList<String> idsOfi = new ArrayList<>();
                ArrayList<String> idsHab = new ArrayList<>();
                for (Oficio o : specialOficioListAdapter.getOficios()) {
                    if (o.isEstadoRegistro()) {
                        idsOfi.add(o.getIdOficio());
                        for (Habilidad h : o.getHabilidadArrayList()) {
                            if (h.isHabilidadSeleccionada()) {
                                idsHab.add(h.getIdHabilidad());
                            }
                        }
                    }
//                    Log.d(TAG, o.getNombre());
//                    Log.d(TAG, o.getHabilidadArrayList().toString());
                }
                trabajador.setIdOficios(idsOfi);
                trabajador.setIdHabilidades(idsHab);
                if (!trabajador.getIdOficios().isEmpty()) {
                    trabajador.actualizarInfo(this);
                    //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Para continuar por favor seleccione al menos un oficio de los registrados en la lista.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editar_oficios_y_habilidades, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_edit_oficio:
//                Toast.makeText(getApplicationContext(), "Nuevo oficio", Toast.LENGTH_LONG).show();
                alertDialogNuevoOficio();
                break;
            case R.id.mnu_edit_habilidad:
//                Toast.makeText(getApplicationContext(), "Nueva habilidad", Toast.LENGTH_LONG).show();
                alertDialogNuevaHabilidad().show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void alertDialogNuevoOficio() {
        Log.d("TAG", "Registrando nuevo oficio....");
        final EditText input = new EditText(this);
        input.setHint("Nombre de oficio");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dialogNuevoOficio = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nuevo oficio:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Oficio oficio = new Oficio();
                        oficio.setNombre(input.getText().toString());
                        if (!input.getText().toString().equals("")) {
//                            mViewModel.registerJobOnFirebase(job, jobAdapter);
                            int exit = 0;

                            if (specialOficioListAdapter.getOficios() != null) {
                                for (Oficio o : specialOficioListAdapter.getOficios()) {
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
                                oficioViewModel.addOficioToFirebase(EditarOficioHabilidadActivity.this, oficio);
//                                String idOficio = FirebaseDatabase.getInstance().getReference().child("oficios").push().getKey();
//                                oficio.setIdOficio(idOficio);
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("oficios")
//                                        .child(idOficio)
//                                        .setValue(oficio)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    Toast.makeText(getApplicationContext(), "Registro fallido", Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(getApplicationContext(), "Registro fallido: " + e.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                                //Toast.makeText(getApplicationContext(), "Registro exitoso!", Toast.LENGTH_LONG).show();
//                                oficioViewModel.guadarOficioEnFirebase(oficio);
                                //oficioViewModel.guardarOficioEnFirebase(oficio, getApplicationContext());
                            }
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
                        input.setText("");
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                    }
                }).create();
        dialogNuevoOficio.show();
    }

    public android.app.AlertDialog alertDialogNuevaHabilidad() {

        Log.d("TAG", "Registrando nueva habilidad....");
        final EditText input = new EditText(EditarOficioHabilidadActivity.this);
        input.setHint("Nombre de habilidad");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        return new android.app.AlertDialog.Builder(EditarOficioHabilidadActivity.this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Habilidad habilidad = new Habilidad();
                        habilidad.setNombreHabilidad(input.getText().toString());
                        if (!input.getText().toString().equals("")) {
//                            mViewModel.registerJobOnFirebase(job, jobAdapter);

//                            LinearLayout layout = new LinearLayout(this);

                            if (specialOficioListAdapter.getOficios().size() > 0) {
                                spinnerSelectOficio(habilidad);
                            } else {
                                Toast.makeText(EditarOficioHabilidadActivity.this, "No existen oficios registrados!.", Toast.LENGTH_LONG).show();
//                                oficioViewModel.addHabilidadToOficioTofirebase(requireActivity(),oficio);
                                //habilidadViewModel.guardarHabilidadEnFirebase(allOficios.get(0).getIdOficio(), habilidad, getApplicationContext());
                            }
                        }
                        input.setText("");
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        input.setText("");
                    }
                }).create();
    }

    private void spinnerSelectOficio(Habilidad habilidad) {
        android.app.AlertDialog.Builder builder;
        android.app.AlertDialog alertDialog;

        Context mContext = EditarOficioHabilidadActivity.this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.spinner, null);

        String array_spinner[];
        array_spinner = new String[specialOficioListAdapter.getOficios().size()];

        int index = 0;
        for (Oficio o : specialOficioListAdapter.getOficios()) {
            array_spinner[index] = o.getNombre();
            index++;
        }


        Spinner s = (Spinner) layout.findViewById(R.id.Spinner01);

//        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, array_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(EditarOficioHabilidadActivity.this, android.R.layout.simple_spinner_item, array_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oficioSelected = array_spinner[position];
                positionSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder = new android.app.AlertDialog.Builder(EditarOficioHabilidadActivity.this);
        builder.setMessage("Por favor seleccione el oficio donde desea registrar su habilidad");
        builder.setView(layout);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialogConfirmar(specialOficioListAdapter.getOficios().get(positionSelected).getIdOficio(), habilidad).show();
//                habilidadViewModel.guardarHabilidadEnFirebase(allOficios.get(positionSelected).getIdOficio(), habilidad, getApplicationContext());
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }

    public android.app.AlertDialog alertDialogConfirmar(String idOficio, Habilidad habilidad) {

        return new android.app.AlertDialog.Builder(EditarOficioHabilidadActivity.this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setMessage("¿Está seguro que desea guardar su habilidad?")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //habilidadViewModel.guardarHabilidadEnFirebase(idOficio, habilidad, requireActivity());
                        Oficio oficioUpdate = new Oficio();
                        for (Oficio o : specialOficioListAdapter.getOficios()) {
                            if (o.getIdOficio().equals(idOficio)) {
                                oficioUpdate = o;
                            }
                        }
                        String idHabilidad = FirebaseDatabase.getInstance().getReference().child("oficios").child(idOficio).child("habilidades").push().getKey();
                        habilidad.setIdHabilidad(idHabilidad);

                        int exitFlag = 0;
                        try {
                            for (Habilidad h : oficioUpdate.getHabilidadArrayList()) {
                                if (h.getNombreHabilidad().toUpperCase().equals(habilidad.getNombreHabilidad().toUpperCase())) {
                                    exitFlag = 1;
                                    break;
                                }
                            }
                        } catch (Exception e) {

                        }

                        if (exitFlag == 0) {
                            if (oficioUpdate.getHabilidadArrayList() != null) {
                                oficioUpdate.getHabilidadArrayList().add(habilidad);
                            } else {
                                oficioUpdate.setHabilidadArrayList(new ArrayList<>());
                                oficioUpdate.getHabilidadArrayList().add(habilidad);
                            }
                            oficioViewModel.addHabilidadToOficioTofirebase(EditarOficioHabilidadActivity.this, oficioUpdate, habilidad);
                        } else {
                            Toast.makeText(EditarOficioHabilidadActivity.this, "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }


}