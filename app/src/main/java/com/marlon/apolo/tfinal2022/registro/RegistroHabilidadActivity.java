package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.HabilidadListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioRegistroListAdapter;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.Length;

import java.util.ArrayList;

public class RegistroHabilidadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroHabilidadActivity.class.getSimpleName();
    private Trabajador trabajador;
    private int regUsuario;
    private Dialog dialogInfo;
    private ArrayList<Oficio> oficiosDB;
    private BienvenidoViewModel bienvenidoViewModel;
    private int positionSelected;
    private String oficioSelected;
    private ArrayList<Oficio> oficioArrayListFilter;
    private OficioRegistroListAdapter2 oficioRegistroListAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_habilidad);

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

        RecyclerView recyclerViewHabilidades = findViewById(R.id.recyclerViewHabilidades);


        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);

        HabilidadListAdapter habilidadListAdapter = new HabilidadListAdapter(RegistroHabilidadActivity.this);
        oficioRegistroListAdapter2 = new OficioRegistroListAdapter2(RegistroHabilidadActivity.this);
//        recyclerViewHabilidades.setAdapter(habilidadListAdapter);
        recyclerViewHabilidades.setAdapter(oficioRegistroListAdapter2);
        recyclerViewHabilidades.setLayoutManager(new LinearLayoutManager(this));


        regUsuario = getIntent().getIntExtra("usuario", -1);
        trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");

        ArrayList<String> stringsOficios = trabajador.getIdOficios();
        oficioArrayListFilter = new ArrayList<>();

        bienvenidoViewModel.getAllOficios().observe(this, oficios -> {
            if (oficios != null) {
                oficioArrayListFilter = new ArrayList<>();
                ArrayList<String> idOficios = new ArrayList<>();
                oficiosDB = oficios;
                for (String idOf : trabajador.getIdOficios()) {
                    for (Oficio o : oficiosDB) {
//                    oficioArrayListFilter.add(o);
                        if (o.getIdOficio().equals(idOf)) {
                            o.setEstadoRegistro(true);
                            oficioArrayListFilter.add(o);
                            break;

                        }
                    }
                }

                oficioRegistroListAdapter2.setOficios(oficioArrayListFilter);

                for (String idOficio : trabajador.getIdOficios()) {
//                for (String idOficio : idOficios) {
                    ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
                    FirebaseDatabase.getInstance().getReference()
                            .child("habilidades")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    String key = snapshot.getKey();
                                    if (key.equals(idOficio)) {
                                        Oficio oficio = new Oficio();
                                        for (Oficio o : oficiosDB) {
                                            if (o.getIdOficio().equals(idOficio)) {
                                                o.setEstadoRegistro(true);
                                                oficio = o;
                                                break;
                                            }
                                        }
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            Habilidad habilidad = data.getValue(Habilidad.class);
                                            habilidadArrayList.add(habilidad);
                                        }
                                        oficio.setHabilidadArrayList(habilidadArrayList);
                                        try {
                                            int index = 0;
                                            for (Oficio ox : oficioArrayListFilter) {
                                                if (ox.getIdOficio().equals(oficio.getIdOficio())) {
                                                    oficioArrayListFilter.set(index, oficio);
                                                    break;
                                                }
                                                index++;
                                            }
                                        } catch (Exception e) {

                                        }
                                        oficioRegistroListAdapter2.setOficios(oficioArrayListFilter);
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
                }
            }
        });


        switch (regUsuario) {
            case 1:/*empleador*/
                break;
            case 2:/*trabajador*/

//                intent.putExtra("usuario", regUsuario);
//                intent.putExtra("trabajador", trabajador);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:
                ArrayList<String> idOficios = new ArrayList<>();
                ArrayList<String> idHabilidades = new ArrayList<>();
                for (Oficio ofS : oficioRegistroListAdapter2.getOficios()) {
                    if (ofS.isEstadoRegistro()) {
                        idOficios.add(ofS.getIdOficio());
//                        Toast.makeText(getApplicationContext(), ofS.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            for (Habilidad h : ofS.getHabilidadArrayList()) {
                                if (h.isHabilidadSeleccionada()) {
                                    idHabilidades.add(h.getIdHabilidad());
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }

                trabajador.setIdOficios(idOficios);
                trabajador.setIdHabilidades(idHabilidades);

                Intent intent = new Intent(RegistroHabilidadActivity.this, MetodoRegActivity.class);

                switch (regUsuario) {
                    case 1:/*empleador*/
//
//                        intent.putExtra("usuario", regUsuario);
//                        intent.putExtra("empleador", empleador);
                        break;
                    case 2:/*trabajador*/

                        intent.putExtra("usuario", regUsuario);
                        intent.putExtra("trabajador", trabajador);
                        break;
                }
                startActivity(intent);

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
        textViewInfo.setText(getResources().getString(R.string.text_select_habilidades));


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_habilidades, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_add_habilidad:
                alertDialogNuevaHabilidad().show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public android.app.AlertDialog alertDialogNuevaHabilidad() {

        Log.d("TAG", "Registrando nueva habilidad....");
        final EditText input = new EditText(RegistroHabilidadActivity.this);
        input.setHint("Nombre de habilidad");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        return new android.app.AlertDialog.Builder(RegistroHabilidadActivity.this)
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

                            if (oficioArrayListFilter.size() > 0) {
                                spinnerSelectOficio(habilidad);
                            } else {
                                Toast.makeText(RegistroHabilidadActivity.this, "No existen oficios registrados!.", Toast.LENGTH_LONG).show();
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

        Context mContext = RegistroHabilidadActivity.this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.spinner, null);

        String array_spinner[];
        array_spinner = new String[oficioArrayListFilter.size()];

        int index = 0;
        for (Oficio o : oficioArrayListFilter) {
            array_spinner[index] = o.getNombre();
            index++;
        }
//        array_spinner[0] = "US";
//        array_spinner[1] = "Japan";
//        array_spinner[2] = "China";
//        array_spinner[3] = "India";
//        array_spinner[4] = "Vietnam";

        Spinner s = (Spinner) layout.findViewById(R.id.Spinner01);

//        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, array_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(RegistroHabilidadActivity.this, android.R.layout.simple_spinner_item, array_spinner);
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

        builder = new android.app.AlertDialog.Builder(RegistroHabilidadActivity.this);
        builder.setMessage("Por favor seleccione el oficio donde desea registrar su habilidad");
        builder.setView(layout);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialogConfirmar(oficioArrayListFilter.get(positionSelected).getIdOficio(), habilidad).show();
//                habilidadViewModel.guardarHabilidadEnFirebase(allOficios.get(positionSelected).getIdOficio(), habilidad, getApplicationContext());
            }
        });

        alertDialog = builder.create();
        alertDialog.show();


    }

    public android.app.AlertDialog alertDialogConfirmar(String idOficio, Habilidad habilidad) {

        return new android.app.AlertDialog.Builder(RegistroHabilidadActivity.this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setMessage("¿Está seguro que desea guardar su habilidad?")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //habilidadViewModel.guardarHabilidadEnFirebase(idOficio, habilidad, requireActivity());
                        Oficio oficioUpdate = new Oficio();
                        for (Oficio o : oficioArrayListFilter) {
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
                            bienvenidoViewModel.addHabilidadToOficioTofirebase(RegistroHabilidadActivity.this, oficioUpdate, habilidad);
                        } else {
                            Toast.makeText(RegistroHabilidadActivity.this, "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }


}