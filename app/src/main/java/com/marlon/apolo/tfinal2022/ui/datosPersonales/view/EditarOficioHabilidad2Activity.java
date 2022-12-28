package com.marlon.apolo.tfinal2022.ui.datosPersonales.view;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores.SpecialOficio2ListAdapter;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores.OficioSuperSpecialListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;

public class EditarOficioHabilidad2Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditarOficioHabilidad2Activity.class.getSimpleName();
    //    private SpecialOficio2ListAdapter specialOficioListAdapter;
    private RecyclerView recyclerViewOficiosHabilidades;
    private BienvenidoViewModel bienvenidoViewModel;
    private Trabajador trabajador;
    private ArrayList<Oficio> oficiosDB;
    private ArrayList<Oficio> oficioArrayListFilter;
    private AlertDialog dialogNuevoOficio;
    private OficioViewModel oficioViewModel;
    private String oficioSelected;
    private int positionSelected;
    private OficioSuperSpecialListAdapter oficioSuperSpecialListAdapter;
    private String idOficioSelected;
    private String nombreHab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_oficio_habilidad);

        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);

        trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
        //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
        oficioArrayListFilter = new ArrayList<>();
        Log.d(TAG, trabajador.toString());
        oficioSelected = "";
        positionSelected = -1;


        recyclerViewOficiosHabilidades = findViewById(R.id.recyclerViewOficiosYHabilidades);
        findViewById(R.id.buttonUpdateOficiosHabilidades).setOnClickListener(this);

//        specialOficioListAdapter = new SpecialOficio2ListAdapter(this);


        oficioSuperSpecialListAdapter = new OficioSuperSpecialListAdapter(this);
        recyclerViewOficiosHabilidades.setAdapter(oficioSuperSpecialListAdapter);
        recyclerViewOficiosHabilidades.setLayoutManager(new LinearLayoutManager(EditarOficioHabilidad2Activity.this));

        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Oficio> oficios = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Oficio oficio = data.getValue(Oficio.class);
                            for (String idOf : trabajador.getIdOficios()) {
                                if (idOf.equals(oficio.getIdOficio())) {
                                    oficio.setEstadoRegistro(true);
                                    break;
                                }
                            }
                            oficios.add(oficio);

                            Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));


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
                        String key = snapshot.getKey();
                        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
                        try {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Habilidad habilidad = data.getValue(Habilidad.class);
                                Log.i(TAG, habilidad.toString());
                                if (trabajador.getIdHabilidades() != null) {
                                    for (String idHab : trabajador.getIdHabilidades()) {
                                        if (idHab.equals(habilidad.getIdHabilidad())) {
                                            habilidad.setHabilidadSeleccionada(true);
                                            break;
                                        }
                                    }
                                }

                                habilidadArrayList.add(habilidad);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        try {
                            for (Oficio oficio : oficioSuperSpecialListAdapter.getOficioArrayList()) {
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
                            Log.e(TAG, e.toString());

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


//        recyclerViewOficiosHabilidades.setAdapter(specialOficioListAdapter);
        //      recyclerViewOficiosHabilidades.setLayoutManager(new LinearLayoutManager(this));


//        FirebaseDatabase.getInstance().getReference()
//                .child("oficios")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Log.e(TAG, "Oficio - onChildAdded ");
//                        try {
//                            Oficio oficioDB = snapshot.getValue(Oficio.class);
//                            FirebaseDatabase.getInstance().getReference()
//                                    .child("habilidades")
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

//
//        FirebaseDatabase.getInstance().getReference()
//                .child("oficios")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Log.d(TAG, "######################################");
//                        Log.d(TAG, "oficios - onChildAdded");
//                        Log.d(TAG, snapshot.getKey());
//                        Log.d(TAG, "######################################");
//                        try {
//                            Oficio oficioDB = snapshot.getValue(Oficio.class);
//                            Log.d(TAG, oficioDB.toString());
//                            for (String idOfx : trabajador.getIdOficios()) {
//                                if (oficioDB.getIdOficio().equals(idOfx)) {
//                                    oficioDB.setEstadoRegistro(true);
//                                    break;
//                                }
//                            }
////                            oficioArrayListFilter.add(oficio);
//                            specialOficioListAdapter.addOficio(oficioDB);
//
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
////                        specialOficioListAdapter.addOficio(oficioDB);
//                        try {
//                            for (Oficio o : specialOficioListAdapter.getOficios()) {
////                                Log.e(TAG, "###################################");
////                                Log.e(TAG, o.toString());
////                                Log.e(TAG, "###################################");
//                            }
//                        } catch (Exception e) {
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                        Log.d(TAG, "######################################");
//                        Log.d(TAG, "oficios - onChildChanged");
//                        Log.d(TAG, snapshot.getKey());
//                        Log.d(TAG, "######################################");
//
//                        try {
//                            Oficio oficioChanged = snapshot.getValue(Oficio.class);
//                            Log.d(TAG, oficioChanged.toString());
//                            int index = 0;
//                            for (Oficio oficio : specialOficioListAdapter.getOficios()) {
//                                if (oficioChanged.getIdOficio().equals(oficio.getIdOficio())) {
//                                    oficio.setNombre(oficioChanged.getNombre());
//                                    specialOficioListAdapter.getOficios().set(index, oficio);
//                                    break;
//                                }
//                                index++;
//                            }
////                            oficioArrayListFilter.add(oficioChanged);
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                        //specialOficioListAdapter.setOficios(oficioArrayListFilter);
//                        specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
//
//                        try {
//                            for (Oficio o : specialOficioListAdapter.getOficios()) {
////                                Log.e(TAG, "###################################");
////                                Log.e(TAG, o.toString());
////                                Log.e(TAG, "###################################");
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        Log.d(TAG, "######################################");
//                        Log.d(TAG, "oficios - onChildRemoved");
//                        Log.d(TAG, snapshot.getKey());
//                        Log.d(TAG, "######################################");
//                        try {
//                            Oficio oficioChanged = snapshot.getValue(Oficio.class);
//                            Log.d(TAG, oficioChanged.toString());
//                            int index = 0;
//                            for (Oficio oficio : specialOficioListAdapter.getOficios()) {
//                                if (oficioChanged.getIdOficio().equals(oficio.getIdOficio())) {
//                                    specialOficioListAdapter.getOficios().remove(index);
//                                    break;
//                                }
//                                index++;
//                            }
////                            oficioArrayListFilter.add(oficioChanged);
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                        specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
//                        try {
//                            for (Oficio o : specialOficioListAdapter.getOficios()) {
////                                Log.e(TAG, "###################################");
////                                Log.e(TAG, o.toString());
////                                Log.e(TAG, "###################################");
//                            }
//                        } catch (Exception e) {
//
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

//        FirebaseDatabase.getInstance().getReference()
//                .child("habilidades")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String key = snapshot.getKey();
//                        int indexOficio = 0;
//                        for (Oficio oficioLocal : specialOficioListAdapter.getOficios()) {
//                            if (oficioLocal.getIdOficio().equals(key)) {
//                                ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    try {
//                                        Habilidad habilidad = data.getValue(Habilidad.class);
//                                        habilidadArrayList.add(habilidad);
//                                    } catch (Exception e) {
//
//                                    }
//                                }
////                                if (oficioLocal.getHabilidadArrayList() != null) {
////                                    if (oficioLocal.getHabilidadArrayList().size() == habilidadArrayList.size()) {
////
////                                    }
////                                    if (oficioLocal.getHabilidadArrayList().size() > habilidadArrayList.size()) {
////
////                                    }
////                                    if (oficioLocal.getHabilidadArrayList().size() < habilidadArrayList.size()) {
////
////                                    }
////                                }
//                                try {
//                                    for (Habilidad hGet : habilidadArrayList) {
//                                        for (String id : trabajador.getIdHabilidades()) {
//                                            if (id.equals(hGet.getIdHabilidad())) {
//                                                hGet.setHabilidadSeleccionada(true);
//                                            }
//                                        }
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                oficioLocal.setHabilidadArrayList(habilidadArrayList);
//
//                                specialOficioListAdapter.getOficios().set(indexOficio, oficioLocal);
//
////                                break;
//                            }
//
//                            indexOficio++;
//
//                        }
////                        ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
////                        for (DataSnapshot data : snapshot.getChildren()) {
////                            try {
////                                Habilidad habilidad = data.getValue(Habilidad.class);
////                                habilidadArrayList.add(habilidad);
////                            } catch (Exception e) {
////
////                            }
////                        }
//                        try {
//                            specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
//                        } catch (Exception e) {
//
//                        }
////                        specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String key = snapshot.getKey();
//                        int indexOficio = 0;
//                        for (Oficio oficioLocal : specialOficioListAdapter.getOficios()) {
//                            Log.d(TAG, oficioLocal.toString());
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
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


//
//        FirebaseDatabase.getInstance().getReference()
//                .child("habilidades")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Log.d(TAG, "######################################");
//                        Log.d(TAG, "hadilidades - onChildAdded");
//                        Log.d(TAG, snapshot.getKey());
//                        Log.d(TAG, "######################################");
////                        try {
//
//
//                        int index = 0;
//                        for (Oficio oficio : specialOficioListAdapter.getOficios()) {
//                            Log.d(TAG, oficio.toString());
//                            if (oficio.getIdOficio().equals(snapshot.getKey())) {
//                                ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
////                                try {
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    Habilidad habilidad = data.getValue(Habilidad.class);
////                                    Log.d(TAG, habilidad.toString());
//                                    try {/*el trabajador puede tener oficios en null y por lo tanto se presentaba un error*/
//                                        for (String idHab : trabajador.getIdHabilidades()) {
//                                            if (idHab.equals(habilidad.getIdHabilidad())) {
//                                                habilidad.setHabilidadSeleccionada(true);
//                                                Log.d(TAG, habilidad.toString());
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        Log.e(TAG, e.toString());
////                                        e.printStackTrace();
//                                    }
//
//                                    habilidadArrayList.add(habilidad);
//                                    Log.d(TAG, habilidad.toString());
//                                }
//                                oficio.setHabilidadArrayList(habilidadArrayList);
//                                specialOficioListAdapter.getOficios().set(index, oficio);
//                                break;
////                                } catch (Exception e) {
////                                    Log.e(TAG, e.toString());
////                                }
//                            }
//                            index++;
//                        }
//                        specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
//                        oficioArrayListFilter = new ArrayList<>();
//                        oficioArrayListFilter = (ArrayList<Oficio>) specialOficioListAdapter.getOficios();
////                        } catch (Exception e) {
////                            Log.e(TAG, e.toString());
////                        }
//                        try {
//                            for (Oficio o : oficioArrayListFilter) {
//                                Log.e(TAG, "###################################");
//                                Log.e(TAG, o.toString());
//                                Log.e(TAG, "###################################");
//                            }
//                        } catch (Exception e) {
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Log.d(TAG, "######################################");
//                        Log.d(TAG, "HABILIDADES - onChildChanged");
//                        Log.d(TAG, snapshot.getKey());
//                        Log.d(TAG, "######################################");
//                        ArrayList<Habilidad> habilidadArrayListDB = new ArrayList<>();
//
//                        Log.d(TAG, "**************************************************");
//                        Log.d(TAG, "**************************************************");
//
//                        for (Oficio oficio : oficioArrayListFilter) {
//                            Log.i(TAG, "###################################");
//                            Log.i(TAG, oficio.toString());
//                            Log.i(TAG, "###################################");
//                        }
//
//                        for (Oficio oficio : specialOficioListAdapter.getOficios()) {
//                            Log.w(TAG, "###################################");
//                            Log.w(TAG, oficio.toString());
//                            Log.w(TAG, "###################################");
//                        }
//
//                        Log.d(TAG, "**************************************************");
//                        Log.d(TAG, "**************************************************");
//
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Habilidad habilidad = data.getValue(Habilidad.class);
//                            Log.d(TAG, "H DB: " + habilidad.toString());
//                            habilidadArrayListDB.add(habilidad);
//                        }
//
//                        try {
//                            int indexOf = 0;
////                            for (Oficio o : specialOficioListAdapter.getOficios()) {
////                                Log.e(TAG, "###################################");
////                                Log.e(TAG, o.toString());
////                                Log.e(TAG, "###################################");
////
////                                if (o.getIdOficio().equals(snapshot.getKey())) {
////                                    try {
////                                        int indexDB = 0;
////                                        if (o.getHabilidadArrayList() != null) {
////                                            if (o.getHabilidadArrayList().size() > 0) {
////                                                int indexLocal = 0;
////                                                for (Habilidad h : o.getHabilidadArrayList()) {
////                                                    for (Habilidad hAux : habilidadArrayListDB) {
////                                                        if (h.getIdHabilidad().equals(hAux.getIdHabilidad())) {
////                                                            h.setNombreHabilidad(hAux.getNombreHabilidad());
////                                                            o.getHabilidadArrayList().set(indexLocal, h);
////                                                            break;
////                                                        }
////                                                    }
////                                                    //h.setNombreHabilidad(habilidadArrayListDB.get(indexDB).getNombreHabilidad());
////                                                    indexLocal++;
////                                                }
////                                            }
////                                        } else {
////                                            o.setHabilidadArrayList(habilidadArrayListDB);
////                                        }
////
////                                    } catch (Exception e) {
////
////                                    }
////                                    specialOficioListAdapter.getOficios().set(indexOf, o);
////
////                                    break;
////
////                                }
////                                indexOf++;
////                            }
//                        } catch (Exception e) {
//
//                        }
//
////                        specialOficioListAdapter.setOficios(specialOficioListAdapter.getOficios());
//                    }
//
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        Log.d(TAG, "######################################");
//                        Log.d(TAG, "hadilidades - onChildRemoved");
//                        Log.d(TAG, snapshot.getKey());
//                        Log.d(TAG, "######################################");
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
                for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
                    if (o.isEstadoRegistro()) {
                        idsOfi.add(o.getIdOficio());
                        try {
                            for (Habilidad h : o.getHabilidadArrayList()) {
                                if (h.isHabilidadSeleccionada()) {
                                    idsHab.add(h.getIdHabilidad());
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
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

    public static boolean contieneSoloLetras(String cadena) {
        for (int x = 0; x < cadena.length(); x++) {
            char c = cadena.charAt(x);
            // Si no está entre a y z, ni entre A y Z, ni es un espacio
//            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ')) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return false;
            }
        }
        return true;
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


                        alertDialogConfirmar(idOficio, habilidad).show();


                    }
                });
                builder2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.create();


//                builder2.show();


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
                                Toast.makeText(getApplicationContext(), "Registrando...", Toast.LENGTH_LONG).show();
                                oficioViewModel.addOficioToFirebase(EditarOficioHabilidad2Activity.this, oficio);
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
        final EditText input = new EditText(EditarOficioHabilidad2Activity.this);
        input.setHint("Nombre de habilidad");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        return new android.app.AlertDialog.Builder(EditarOficioHabilidad2Activity.this)
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
                                Toast.makeText(EditarOficioHabilidad2Activity.this, "No existen oficios registrados!.", Toast.LENGTH_LONG).show();
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

        Context mContext = EditarOficioHabilidad2Activity.this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.spinner, null);

        String array_spinner[];
        array_spinner = new String[oficioSuperSpecialListAdapter.getOficioArrayList().size()];

        int index = 0;
        for (Oficio o : oficioSuperSpecialListAdapter.getOficioArrayList()) {
            array_spinner[index] = o.getNombre();
            index++;
        }


        Spinner s = (Spinner) layout.findViewById(R.id.Spinner01);

//        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, array_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(EditarOficioHabilidad2Activity.this, android.R.layout.simple_spinner_item, array_spinner);
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

        builder = new android.app.AlertDialog.Builder(EditarOficioHabilidad2Activity.this);
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

    }

    public android.app.AlertDialog alertDialogConfirmar(String idOficio, Habilidad habilidad) {

        return new android.app.AlertDialog.Builder(EditarOficioHabilidad2Activity.this)
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
//                            if (oficioUpdate.getHabilidadArrayList() != null) {
//                                oficioUpdate.getHabilidadArrayList().add(habilidad);
//                            } else {
//                                oficioUpdate.setHabilidadArrayList(new ArrayList<>());
//                                oficioUpdate.getHabilidadArrayList().add(habilidad);
//                            }

                            FirebaseDatabase.getInstance().getReference()
                                    .child("habilidades")
                                    .child(idOficio)
                                    .child(habilidad.getIdHabilidad())
                                    .setValue(habilidad).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Registro existoso", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Registro fallido", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

//                            oficioViewModel.addHabilidadToOficioTofirebase(EditarOficioHabilidad2Activity.this, oficioUpdate, habilidad);
                        } else {
                            Toast.makeText(EditarOficioHabilidad2Activity.this, "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }


}