package com.marlon.apolo.tfinal2022.ui.oficios.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.registro.view.RegistroOficioActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.adaptadores.HabilidadCRUDListAdapterAaaaaaaa;
import com.marlon.apolo.tfinal2022.ui.oficios.HabilidadViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;

public class HabilidadActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHab;
    private TrabajadorViewModel trabajadorViewModel;
    private Oficio oficio;
    private HabilidadViewModel habilidadViewModel;
    //    private OficioViewModel oficioViewModel;
    private BienvenidoViewModel bienvenidoViewModel;
    private ArrayList<Oficio> oficiosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habilidad);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerViewHab = findViewById(R.id.recyclerViewHabilidades);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);


        oficio = (Oficio) getIntent().getSerializableExtra("oficio");
//        Toast.makeText(getApplicationContext(), oficio.toString(), Toast.LENGTH_LONG).show();


//        oficioRegistroListAdapter = new OficioRegistroCRUDListAdapter(requireActivity(), trabajadorArrayList);
//        recyclerView.setAdapter(oficioRegistroListAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
//        oficioRegistroListAdapter.setOficios(new ArrayList<>());/*Siempre inicializar*/
//        oficioViewModel.getAllOficios().observe(this, oficios -> {
//            oficioRegistroListAdapter.setOficios(oficios);
//            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
//            trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
//                if (trabajadors != null) {
//                    oficioRegistroListAdapter.setTrabajadors(trabajadors);
//                }
//            });
//        });


//        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
//        oficioViewModel.getOneOficio(oficio.getIdOficio()).observe(this, oficio1 -> {
//            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
//            trabajadorViewModel.getAllTrabajadores().observe(this, trabajadors -> {
//                if (trabajadors != null) {
//                    HabilidadCRUDListAdapter habilidadCRUDListAdapter = new HabilidadCRUDListAdapter(this, oficio1, (ArrayList<Trabajador>) trabajadors);
//                    recyclerViewHab.setAdapter(habilidadCRUDListAdapter);
//                    recyclerViewHab.setLayoutManager(new LinearLayoutManager(this));
//                    habilidadCRUDListAdapter.setHabilidades(oficio.getHabilidadArrayList());
//
//                    habilidadCRUDListAdapter.setTrabajadorList(trabajadors);
//                }
//            });
//
//
//        });


//        habilidadViewModel = new ViewModelProvider(this).get(HabilidadViewModel.class);
        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        HabilidadCRUDListAdapterAaaaaaaa habilidadCRUDListAdapter = new HabilidadCRUDListAdapterAaaaaaaa(HabilidadActivity.this, oficio);
        recyclerViewHab.setAdapter(habilidadCRUDListAdapter);
        recyclerViewHab.setLayoutManager(new LinearLayoutManager(HabilidadActivity.this));
        bienvenidoViewModel.getAllTrabajadores().observe(this, trabajadors -> {
            if (trabajadors != null) {
                habilidadCRUDListAdapter.setTrabajadorList(trabajadors);
            }
        });

        bienvenidoViewModel.getHabilidadesByOficio(oficio.getIdOficio()).observe(this, habilidads -> {
            if (habilidads != null) {

                habilidadCRUDListAdapter.setHabilidades(habilidads);

            }
        });

        bienvenidoViewModel.getAllOficios().observe(this, oficios -> {
            if (oficios != null) {
                oficiosLocal = oficios;
            }
        });

        FirebaseDatabase.getInstance().getReference().child("habilidades").child(oficio.getIdOficio()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Habilidad h = data.getValue(Habilidad.class);
                    habilidadArrayList.add(h);
                }
                oficio.setHabilidadArrayList(habilidadArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        bienvenidoViewModel.getHabilidadesByOficio(oficio.getIdOficio()).observe(HabilidadActivity.this, habilidads -> {
//            if (habilidads != null) {
////                Toast.makeText(getApplicationContext(), "hanilida", Toast.LENGTH_LONG).show();
//
//                habilidadCRUDListAdapter.setHabilidades(habilidads);
//
////                trabajadorViewModel = new ViewModelProvider(HabilidadActivity.this).get(TrabajadorViewModel.class);
////                trabajadorViewModel.getAllTrabajadores().observe(HabilidadActivity.this, trabajadors -> {
////                    if (trabajadors != null) {
////                        habilidadCRUDListAdapter.setTrabajadorList(trabajadors);
////                    }
////                });
//            }
//    });

//
//        FirebaseDatabase.getInstance().getReference()
//                .child("oficios")
//                .child(oficio.getIdOficio())
//                .child("habilidadArrayList")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                Habilidad habilidad = data.getValue(Habilidad.class);
//                                habilidadArrayList.add(habilidad);
//                            }
//                            HabilidadCRUDListAdapterAaaaaaaa habilidadCRUDListAdapter = new HabilidadCRUDListAdapterAaaaaaaa(HabilidadActivity.this, oficio);
//                            recyclerViewHab.setAdapter(habilidadCRUDListAdapter);
//                            recyclerViewHab.setLayoutManager(new LinearLayoutManager(HabilidadActivity.this));
//                            habilidadCRUDListAdapter.setHabilidades(habilidadArrayList);
//
//                            trabajadorViewModel = new ViewModelProvider(HabilidadActivity.this).get(TrabajadorViewModel.class);
//                            trabajadorViewModel.getAllTrabajadores().observe(HabilidadActivity.this, trabajadors -> {
//                                if (trabajadors != null) {
//                                    habilidadCRUDListAdapter.setTrabajadorList(trabajadors);
//                                }
//                            });
//                            //habilidadCRUDListAdapter.setTrabajadorList(trabajadors);
//                        } catch (Exception e) {
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


    }

    /*
     * se llama cuando el usuario abandona su Activity(en algún momento antes del onStop()método).
     * */


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
//
//    @Override
//    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
//        MenuItem item = menu.findItem(R.id.mnu_add_oficio);
//        item.setVisible(false);
//
//        return super.onPrepareOptionsMenu(menu);
//
//    }


    public android.app.AlertDialog alertDialogNuevaHabilidad() {

        Log.d("TAG", "Registrando nueva habilidad....");
        final EditText input = new EditText(HabilidadActivity.this);
        input.setHint("Nombre de habilidad");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        return new android.app.AlertDialog.Builder(HabilidadActivity.this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Habilidad habilidad = new Habilidad();
                        habilidad.setNombreHabilidad(input.getText().toString());
                        if (!input.getText().toString().equals("")) {


                            String idHabilidad = FirebaseDatabase.getInstance().getReference().child("habilidades").child(oficio.getIdOficio()).push().getKey();
                            habilidad.setIdHabilidad(idHabilidad);
                            alertDialogConfirmar(oficio.getIdOficio(), habilidad).show();

//                            int exitFlag = 0;
//                            try {
//                                for (Habilidad h : oficioUpdate.getHabilidadArrayList()) {
//                                    if (h.getNombreHabilidad().toUpperCase().equals(habilidad.getNombreHabilidad().toUpperCase())) {
//                                        exitFlag = 1;
//                                        break;
//                                    }
//                                }
//                            } catch (Exception e) {
//
//                            }
//
//                            if (exitFlag == 0) {
//                                if (oficioUpdate.getHabilidadArrayList() != null) {
//                                    oficioUpdate.getHabilidadArrayList().add(habilidad);
//                                } else {
//                                    oficioUpdate.setHabilidadArrayList(new ArrayList<>());
//                                    oficioUpdate.getHabilidadArrayList().add(habilidad);
//                                }
//                                bienvenidoViewModel.addHabilidadToOficioTofirebase(HabilidadActivity.this, oficioUpdate, habilidad);
//                            } else {
//                                Toast.makeText(HabilidadActivity.this, "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
//                            }


                        } else {
                            Toast.makeText(HabilidadActivity.this, "El nombre ingresado es inválido", Toast.LENGTH_LONG).show();
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


    public android.app.AlertDialog alertDialogConfirmar(String idOficio, Habilidad habilidad) {

        return new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setMessage("¿Está seguro que desea guardar su habilidad?")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //habilidadViewModel.guardarHabilidadEnFirebase(idOficio, habilidad, requireActivity());
//                        Oficio oficioUpdate = new Oficio();
//                        for (Oficio o : oficiosLocal) {
//                            if (o.getIdOficio().equals(idOficio)) {
//                                oficioUpdate = o;
//                            }
//                        }
                        String idHabilidad = FirebaseDatabase.getInstance().getReference().child("oficios").child(idOficio).child("habilidades").push().getKey();
                        habilidad.setIdHabilidad(idHabilidad);

                        int exitFlag = 0;
                        try {
                            for (Habilidad h : oficio.getHabilidadArrayList()) {
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
//                            oficioViewModel.addHabilidadToOficioTofirebase(RegistroOficioActivity.this, oficioUpdate, habilidad);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("habilidades")
                                    .child(idOficio)
                                    .child(habilidad.getIdHabilidad())
                                    .setValue(habilidad)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Registro existoso", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Registro fallido", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });


                        } else {
                            Toast.makeText(HabilidadActivity.this, "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }

}