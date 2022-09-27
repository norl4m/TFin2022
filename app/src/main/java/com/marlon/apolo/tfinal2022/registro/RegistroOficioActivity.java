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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores.OficioSuperSpecialListAdapter;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.view.EditarOficioHabilidad2Activity;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioRegistroListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;

import java.util.ArrayList;

public class RegistroOficioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroOficioActivity.class.getSimpleName();
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private OficioViewModel oficioViewModel;
    //    private OficioRegistroListAdapter oficioRegistroListAdapter;
    private Dialog dialogInfo;
    private Dialog dialogNuevoOficio;
    private String oficioSelected;
    private int positionSelected;
    private boolean click;
    private int c;
    private ArrayList<Oficio> oficioArrayListSelected;
    private OficioSuperSpecialListAdapter oficioSuperSpecialListAdapter;
    private String idOficioSelected;
    private String nombreHab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_oficio);

        oficioArrayListSelected = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewOficios);
//        oficioRegistroListAdapter = new OficioRegistroListAdapter(this);
//        recyclerView.setAdapter(oficioRegistroListAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);

        BienvenidoViewModel bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        bienvenidoViewModel.getAllOficios().observe(this, oficios -> {
            if (oficios != null) {
//                try {
//
//                    for (Oficio o : oficios) {
////                    bienvenidoViewModel.getAllHabilidades(o.getIdOficio()).observe(this, habilidads -> {
////                        o.setHabilidadArrayList(habilidads);
////                    });
//                        Log.d(TAG, o.toString());
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//                oficioRegistroListAdapter.setOficios(oficios);

            }
        });


        oficioSuperSpecialListAdapter = new OficioSuperSpecialListAdapter(this);
        recyclerView.setAdapter(oficioSuperSpecialListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Oficio> oficios = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Oficio oficio = data.getValue(Oficio.class);
//                            for (String idOf : trabajador.getIdOficios()) {
//                                if (idOf.equals(oficio.getIdOficio())) {
//                                    oficio.setEstadoRegistro(true);
//                                    break;
//                                }
//                            }
                            oficios.add(oficio);

                        }
                        if (oficioSuperSpecialListAdapter.getOficioArrayList() != null) {

                            /*Si son iguales**************************/
                            if (oficios.size() == oficioSuperSpecialListAdapter.getOficioArrayList().size()) {

                                int indexOf = 0;
                                for (Oficio oxOne : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                                    for (Oficio oDB : oficios) {
                                        if (oxOne.getIdOficio().equals(oDB.getIdOficio())) {
                                            oxOne.setNombre(oDB.getNombre());
//                                            oficioSuperSpecialListAdapter.getOficioArrayList().set(indexOf, oxOne);
                                            oficioSuperSpecialListAdapter.updateViewWithOficio(indexOf, oxOne);
                                            break;
                                        }
                                    }
                                    indexOf++;
                                }


//                                for (Oficio ox : oficios) {
//                                    int index = 0;
//                                    for (Oficio oxOne : oficioSuperSpecialListAdapter.getOficioArrayList()) {
//                                        if (ox.getIdOficio().equals(oxOne.getIdOficio())) {
//                                            oxOne.setNombre(ox.getNombre());
//                                            oficioSuperSpecialListAdapter.getOficioArrayList().set(index, oxOne);
//                                            break;
//                                        }
//                                        index++;
//                                    }
//                                }
                            }

                            if (oficios.size() > oficioSuperSpecialListAdapter.getOficioArrayList().size()) {
                                oficioSuperSpecialListAdapter.getOficioArrayList().add(oficios.get(oficios.size() - 1));
                            }

                            ArrayList<Oficio> oficioArrayListFiltrados = new ArrayList<>();
                            if (oficios.size() < oficioSuperSpecialListAdapter.getOficioArrayList().size()) {
                                for (Oficio ha : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                                    for (Oficio hag : oficios) {
                                        if (hag.getIdOficio().equals(ha.getIdOficio())) {
                                            oficioArrayListFiltrados.add(ha);
                                        }
                                    }
                                }
//                                oficio.setHabilidadArrayList(habilidadArrayList);
                                oficioSuperSpecialListAdapter.setOficioArrayList(oficioArrayListFiltrados);

                            }
                        } else {
                            oficioSuperSpecialListAdapter.setOficioArrayList(oficios);
                        }


                        FirebaseDatabase.getInstance().getReference()
                                .child("habilidades")
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        String key = snapshot.getKey();
                                        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
                                        try {
                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                Habilidad habilidad = data.getValue(Habilidad.class);
                                                Log.i(TAG, habilidad.toString());
//                                if (trabajador.getIdHabilidades() != null) {
//                                    for (String idHab : trabajador.getIdHabilidades()) {
//                                        if (idHab.equals(habilidad.getIdHabilidad())) {
//                                            habilidad.setHabilidadSeleccionada(true);
//                                            break;
//                                        }
//                                    }
//                                }

                                                habilidadArrayList.add(habilidad);
                                            }
                                        } catch (Exception e) {
                                            Log.d(TAG + "HAB", e.toString());
                                        }
                                        try {
                                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@");
                                            Log.d(TAG, String.valueOf(oficioSuperSpecialListAdapter.getOficioArrayList()));

                                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                                                Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@");

                                                if (oficio.getIdOficio().equals(key)) {
                                                    if (oficio.getHabilidadArrayList() != null) {
//                                        if (oficio.getHabilidadArrayList().size() == habilidadArrayList.size()) {
//
//                                        }
//                                        if (oficio.getHabilidadArrayList().size() < habilidadArrayList.size()) {
//                                            for (Habilidad ha : oficio.getHabilidadArrayList()) {
//                                                for (Habilidad hag : habilidadArrayList) {
//                                                    if (hag.getIdHabilidad().equals(ha.getIdHabilidad())) {
//                                                        ha.setHabilidadSeleccionada(hag.isHabilidadSeleccionada());
//                                                    }
//                                                }
//                                            }
//                                        }
                                                        if (oficio.getHabilidadArrayList().size() < habilidadArrayList.size()) {
                                                            oficio.getHabilidadArrayList().add(habilidadArrayList.get(habilidadArrayList.size() - 1));
                                                        }
                                                    } else {
                                                        oficio.setHabilidadArrayList(habilidadArrayList);
                                                    }
                                                    Log.w(TAG, oficio.toString());
                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.e(TAG, "########################");
                                            Log.e(TAG, e.toString());
                                            Log.e(TAG, "########################");

                                        }
                                        oficioSuperSpecialListAdapter.setOficioArrayList(oficioSuperSpecialListAdapter.getOficioArrayList());
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        String key = snapshot.getKey();
                                        Log.d(TAG, "onChildChanged");

                                        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
                                        try {
                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                Habilidad habilidad = data.getValue(Habilidad.class);
                                                Log.i(TAG, habilidad.toString());
                                                habilidadArrayList.add(habilidad);
                                            }
                                        } catch (Exception e) {
                                            Log.d(TAG, e.toString());
                                        }
                                        try {
                                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                                                if (oficio.getIdOficio().equals(key)) {
                                                    if (oficio.getHabilidadArrayList() != null) {
                                                        if (oficio.getHabilidadArrayList().size() == habilidadArrayList.size()) {
                                                            for (Habilidad haX : oficio.getHabilidadArrayList()) {
                                                                for (Habilidad haY : habilidadArrayList) {
                                                                    if (haX.getIdHabilidad().equals(haY.getIdHabilidad())) {
                                                                        haX.setNombreHabilidad(haY.getNombreHabilidad());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (oficio.getHabilidadArrayList().size() > habilidadArrayList.size()) {
                                                            Log.d(TAG, "oficio.getHabilidadArrayList().size() > habilidadArrayList.size()");
                                                            ArrayList<Habilidad> habilidadsFiltradas = new ArrayList<>();

                                                            for (Habilidad ha : oficio.getHabilidadArrayList()) {
                                                                for (Habilidad hag : habilidadArrayList) {
                                                                    if (hag.getIdHabilidad().equals(ha.getIdHabilidad())) {
                                                                        hag.setHabilidadSeleccionada(ha.isHabilidadSeleccionada());
                                                                        habilidadsFiltradas.add(ha);
                                                                    }
                                                                }
                                                            }

//                                            oficio.setHabilidadArrayList(habilidadArrayList);
                                                            if (habilidadArrayList.size() == 0) {
                                                                habilidadsFiltradas = new ArrayList<>();
                                                            }
                                                            oficio.setHabilidadArrayList(habilidadsFiltradas);

                                                        }

                                                        if (oficio.getHabilidadArrayList().size() < habilidadArrayList.size()) {
                                                            oficio.getHabilidadArrayList().add(habilidadArrayList.get(habilidadArrayList.size() - 1));
                                                        }
                                                    } else {
//                                        oficio.setHabilidadArrayList(habilidadArrayList);
                                                    }
                                                    Log.w(TAG, oficio.toString());
                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());

                                        }
                                        oficioSuperSpecialListAdapter.setOficioArrayList(oficioSuperSpecialListAdapter.getOficioArrayList());

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                        String key = snapshot.getKey();

                                        Log.d(TAG, "onChildRemoved");
                                        try {
                                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                                                if (oficio.getIdOficio().equals(key)) {
                                                    oficio.getHabilidadArrayList().clear();
                                                    break;
                                                }
                                                Log.w(TAG, oficio.toString());
                                            }

                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());

                                        }
                                        oficioSuperSpecialListAdapter.setOficioArrayList(oficioSuperSpecialListAdapter.getOficioArrayList());

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
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

/************************************************/
//        FirebaseDatabase.getInstance().getReference()
//                .child("habilidades")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String key = snapshot.getKey();
//                        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//                        try {
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                Habilidad habilidad = data.getValue(Habilidad.class);
//                                Log.i(TAG, habilidad.toString());
////                                if (trabajador.getIdHabilidades() != null) {
////                                    for (String idHab : trabajador.getIdHabilidades()) {
////                                        if (idHab.equals(habilidad.getIdHabilidad())) {
////                                            habilidad.setHabilidadSeleccionada(true);
////                                            break;
////                                        }
////                                    }
////                                }
//
//                                habilidadArrayList.add(habilidad);
//                            }
//                        } catch (Exception e) {
//                            Log.d(TAG + "HAB", e.toString());
//                        }
//                        try {
//                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@");
//                            Log.d(TAG, String.valueOf(oficioSuperSpecialListAdapter.getOficioArrayList()));
//
//                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
//                                Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//                                if (oficio.getIdOficio().equals(key)) {
//                                    if (oficio.getHabilidadArrayList() != null) {
////                                        if (oficio.getHabilidadArrayList().size() == habilidadArrayList.size()) {
////
////                                        }
////                                        if (oficio.getHabilidadArrayList().size() < habilidadArrayList.size()) {
////                                            for (Habilidad ha : oficio.getHabilidadArrayList()) {
////                                                for (Habilidad hag : habilidadArrayList) {
////                                                    if (hag.getIdHabilidad().equals(ha.getIdHabilidad())) {
////                                                        ha.setHabilidadSeleccionada(hag.isHabilidadSeleccionada());
////                                                    }
////                                                }
////                                            }
////                                        }
//                                        if (oficio.getHabilidadArrayList().size() < habilidadArrayList.size()) {
//                                            oficio.getHabilidadArrayList().add(habilidadArrayList.get(habilidadArrayList.size() - 1));
//                                        }
//                                    } else {
//                                        oficio.setHabilidadArrayList(habilidadArrayList);
//                                    }
//                                    Log.w(TAG, oficio.toString());
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "########################");
//                            Log.e(TAG, e.toString());
//                            Log.e(TAG, "########################");
//
//                        }
//                        oficioSuperSpecialListAdapter.setOficioArrayList(oficioSuperSpecialListAdapter.getOficioArrayList());
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String key = snapshot.getKey();
//                        Log.d(TAG, "onChildChanged");
//
//                        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//                        try {
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                Habilidad habilidad = data.getValue(Habilidad.class);
//                                Log.i(TAG, habilidad.toString());
//                                habilidadArrayList.add(habilidad);
//                            }
//                        } catch (Exception e) {
//                            Log.d(TAG, e.toString());
//                        }
//                        try {
//                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
//                                if (oficio.getIdOficio().equals(key)) {
//                                    if (oficio.getHabilidadArrayList() != null) {
//                                        if (oficio.getHabilidadArrayList().size() == habilidadArrayList.size()) {
//                                            for (Habilidad haX : oficio.getHabilidadArrayList()) {
//                                                for (Habilidad haY : habilidadArrayList) {
//                                                    if (haX.getIdHabilidad().equals(haY.getIdHabilidad())) {
//                                                        haX.setNombreHabilidad(haY.getNombreHabilidad());
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        if (oficio.getHabilidadArrayList().size() > habilidadArrayList.size()) {
//                                            Log.d(TAG, "oficio.getHabilidadArrayList().size() > habilidadArrayList.size()");
//                                            ArrayList<Habilidad> habilidadsFiltradas = new ArrayList<>();
//
//                                            for (Habilidad ha : oficio.getHabilidadArrayList()) {
//                                                for (Habilidad hag : habilidadArrayList) {
//                                                    if (hag.getIdHabilidad().equals(ha.getIdHabilidad())) {
//                                                        hag.setHabilidadSeleccionada(ha.isHabilidadSeleccionada());
//                                                        habilidadsFiltradas.add(ha);
//                                                    }
//                                                }
//                                            }
//
////                                            oficio.setHabilidadArrayList(habilidadArrayList);
//                                            if (habilidadArrayList.size() == 0) {
//                                                habilidadsFiltradas = new ArrayList<>();
//                                            }
//                                            oficio.setHabilidadArrayList(habilidadsFiltradas);
//
//                                        }
//
//                                        if (oficio.getHabilidadArrayList().size() < habilidadArrayList.size()) {
//                                            oficio.getHabilidadArrayList().add(habilidadArrayList.get(habilidadArrayList.size() - 1));
//                                        }
//                                    } else {
////                                        oficio.setHabilidadArrayList(habilidadArrayList);
//                                    }
//                                    Log.w(TAG, oficio.toString());
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//
//                        }
//                        oficioSuperSpecialListAdapter.setOficioArrayList(oficioSuperSpecialListAdapter.getOficioArrayList());
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        String key = snapshot.getKey();
//
//                        Log.d(TAG, "onChildRemoved");
//                        try {
//                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
//                                if (oficio.getIdOficio().equals(key)) {
//                                    oficio.getHabilidadArrayList().clear();
//                                    break;
//                                }
//                                Log.w(TAG, oficio.toString());
//                            }
//
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//
//                        }
//                        oficioSuperSpecialListAdapter.setOficioArrayList(oficioSuperSpecialListAdapter.getOficioArrayList());
//
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

/**************************************************************/
//        oficioViewModel.getAllOficios().observe(this, oficios -> {
//            if (oficioRegistroListAdapter.getOficios() != null) {
//                if (oficioRegistroListAdapter.getOficios().size() > 0) {
//                    int index = 0;
//                    for (Oficio o : oficioRegistroListAdapter.getOficios()) {
//                        if (o.isEstadoRegistro()) {
//                            if (o.getIdOficio().equals(oficios.get(index).getIdOficio())) {
//                                ArrayList<Habilidad> habilidadArrayList = oficios.get(index).getHabilidadArrayList();
//                                o.setHabilidadArrayList(habilidadArrayList);
//                            }
//                        }
//                        index++;
//                    }
//                    oficioArrayListSelected = (ArrayList<Oficio>) oficios;
//                } else {
//                    oficioArrayListSelected = (ArrayList<Oficio>) oficios;
//                }
//            } else {
//                oficioArrayListSelected = (ArrayList<Oficio>) oficios;
//            }
////            oficioRegistroListAdapter.setOficios(oficios);
//            oficioRegistroListAdapter.setOficios(oficioArrayListSelected);
//
//        });


//        mViewModel.getAllOficios().observe(this, oficios -> {
//            for (Oficio o : oficios) {
//                Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:


                ArrayList<Oficio> oficiosReg = new ArrayList<>();
                ArrayList<String> idOficiosReg = new ArrayList<>();
                ArrayList<String> idHabilidadesReg = new ArrayList<>();
                if (oficioSuperSpecialListAdapter.getItemCount() > 0) {
                    for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
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
                    if (oficiosReg.size() > 0) {
                        trabajador.setIdOficios(idOficiosReg);
                        trabajador.setIdHabilidades(idHabilidadesReg);
                        Intent intent = new Intent(RegistroOficioActivity.this, MetodoRegActivity.class);
//                        Intent intent = new Intent(RegistroOficioActivity.this, RegistroHabilidadActivity.class);
                        switch (regUsuario) {
                            case 1:/*empleador*/

                                intent.putExtra("usuario", regUsuario);
                                intent.putExtra("empleador", empleador);
                                break;
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
                alertDialogNuevoOficio();
                break;
            case R.id.mnu_edit_habilidad:
//                alertDialogNuevaHabilidad().show();


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

                final EditText input = new EditText(this);
                input.setHint("Nombre de habilidad");
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
// Add the buttons
                builder.setView(input);
                builder.setTitle("Nueva habilidad:");
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.d(TAG, "##########################################");
                        Log.d(TAG, String.valueOf(start));
                        Log.d(TAG, String.valueOf(before));
                        Log.d(TAG, String.valueOf(count));
                        if (start == 0 && before == 1 && count == 0) {

                        } else {

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
//                        Habilidad habilidad = new Habilidad();
//                        String idOficio = "-N8e9umihnROcD8OH9F1";
//                        String idHabilidad = FirebaseDatabase.getInstance().getReference()
//                                .child("habilidades")
//                                .child(idOficio)
//                                .push().getKey();
//                        habilidad.setIdHabilidad(idHabilidad);
//                        habilidad.setNombreHabilidad("uwuuuuuuuuuuuuuuuuuuuuuuuu");
//                        habilidad.setHabilidadSeleccionada(false);
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("habilidades")
//                                .child(idOficio)
//                                .child(habilidad.getIdHabilidad())
//                                .setValue(habilidad);
                        if (input.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "EL nombre de la habilidad es incorrecto.", Toast.LENGTH_LONG).show();
                        } else {
                            nombreHab = input.getText().toString();
                            builder2.show();
//                            if (contieneSoloLetras(nombreHab)) {
//                                builder2.show();
//                            } else {
//                                Toast.makeText(getApplicationContext(), "EL nombre de la habilidad es incorrecto.", Toast.LENGTH_LONG).show();
//                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                String[] values = new String[oficioSuperSpecialListAdapter.getOficioArrayList().size()];
                String[] ids = new String[oficioSuperSpecialListAdapter.getOficioArrayList().size()];
                int index = 0;
                for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                    values[index] = o.getNombre();
                    ids[index] = o.getIdOficio();
                    index++;
                }


//                final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
//                        android.R.layout.simple_spinner_item, values);
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                        R.layout.simple_spinner_custom_item, values);

                final Spinner sp = new Spinner(this);

                adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                sp.setAdapter(adp);
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idOficioSelected = ids[position];
//                        positionSelected = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                builder2.setView(sp);
                builder2.setTitle("Por favor seleccione el oficio donde desea registrar su habilidad");

                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Habilidad habilidad = new Habilidad();
//                        String idOficio = "-N8e9umihnROcD8OH9F1";
                        String idOficio = idOficioSelected;
                        String idHabilidad = FirebaseDatabase.getInstance().getReference()
                                .child("habilidades")
                                .child(idOficio)
                                .push().getKey();
                        habilidad.setIdHabilidad(idHabilidad);
                        habilidad.setNombreHabilidad(nombreHab);
                        habilidad.setHabilidadSeleccionada(false);

                        FirebaseDatabase.getInstance().getReference()
                                .child("habilidades")
                                .child(idOficio)
                                .child(habilidad.getIdHabilidad())
                                .setValue(habilidad);
                    }
                });
                builder2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.create();


                break;
        }
        return super.onOptionsItemSelected(item);
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
        /*arrayListIdJob = new ArrayList<>();
        for (Oficio oficio : oficioListAdapter.getJobModelList()) {
            if (oficio.isEstadoRegistro()) {
                arrayListIdJob.add(oficio.getIdOficio());
            }
        }*/
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

                            if (oficioSuperSpecialListAdapter.getOficioArrayList() != null) {
                                for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
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
                                oficioViewModel.addOficioToFirebase(RegistroOficioActivity.this, oficio);
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
        final EditText input = new EditText(RegistroOficioActivity.this);
        input.setHint("Nombre de habilidad");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        return new android.app.AlertDialog.Builder(RegistroOficioActivity.this)
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

                            if (oficioSuperSpecialListAdapter.getOficioArrayList().size() > 0) {
                                spinnerSelectOficio(habilidad);
                            } else {
                                Toast.makeText(RegistroOficioActivity.this, "No existen oficios registrados!.", Toast.LENGTH_LONG).show();
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

        Context mContext = RegistroOficioActivity.this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.spinner, null);

        String array_spinner[];
        array_spinner = new String[oficioSuperSpecialListAdapter.getOficioArrayList().size()];

        int index = 0;
        for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
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
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(RegistroOficioActivity.this, android.R.layout.simple_spinner_item, array_spinner);
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

        builder = new android.app.AlertDialog.Builder(RegistroOficioActivity.this);
        builder.setMessage("Por favor seleccione el oficio donde desea registrar su habilidad");
        builder.setView(layout);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialogConfirmar(oficioSuperSpecialListAdapter.getOficioArrayList().get(positionSelected).getIdOficio(), habilidad).show();
//                habilidadViewModel.guardarHabilidadEnFirebase(allOficios.get(positionSelected).getIdOficio(), habilidad, getApplicationContext());
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        click = false;
        c = 0;


    }

    public android.app.AlertDialog alertDialogConfirmar(String idOficio, Habilidad habilidad) {

        return new android.app.AlertDialog.Builder(RegistroOficioActivity.this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setMessage("¿Está seguro que desea guardar su habilidad?")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //habilidadViewModel.guardarHabilidadEnFirebase(idOficio, habilidad, requireActivity());
                        Oficio oficioUpdate = new Oficio();
                        for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
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
                            oficioViewModel.addHabilidadToOficioTofirebase(RegistroOficioActivity.this, oficioUpdate, habilidad);
                        } else {
                            Toast.makeText(RegistroOficioActivity.this, "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            oficioViewModel.removeChildListener();
        } catch (Exception e) {

        }
    }
}