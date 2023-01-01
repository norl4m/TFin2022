package com.marlon.apolo.tfinal2022.ui.datosPersonales.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.OficioPoc;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.view.RegistroOficioActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores.EditarOficioSuperSpecialListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;

import java.util.ArrayList;

public class EditarOficioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditarOficioActivity.class.getSimpleName();
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
    private EditarOficioSuperSpecialListAdapter editarOficioSuperSpecialListAdapter;
    private String idOficioSelected;
    private String nombreHab;
    private ChildEventListener childEventListenerOficios;


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
    }


    private void loadOficios() {
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
                trabajador.setIdHabilidades(idsHab);
                if (!trabajador.getIdOficios().isEmpty()) {
                    trabajador.actualizarInfo(this);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_editar_oficios_y_habilidades, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.mnu_edit_oficio:
////                Toast.makeText(getApplicationContext(), "Nuevo oficio", Toast.LENGTH_LONG).show();
//                alertDialogNuevoOficio();
//
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//
//    }

    //    public void alertDialogNuevoOficio() {
//        Log.d("TAG", "Registrando nuevo oficio....");
//        final EditText input = new EditText(this);
//        input.setHint("Nombre de oficio");
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        dialogNuevoOficio = new AlertDialog.Builder(this)
//                .setIcon(R.drawable.ic_oficios)
//                .setTitle("Nuevo oficio:")
//                .setView(input)
//                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Oficio oficio = new Oficio();
//                        oficio.setNombre(input.getText().toString());
//                        if (!input.getText().toString().equals("")) {
////                            mViewModel.registerJobOnFirebase(job, jobAdapter);
//                            int exit = 0;
//
//                            if (editarOficioSuperSpecialListAdapter.getOficioArrayList() != null) {
//                                for (Oficio o : editarOficioSuperSpecialListAdapter.getOficioArrayList()) {
//                                    if (o.getNombre().toUpperCase().equals(oficio.getNombre().toUpperCase())) {
//                                        exit = 1;
//                                        break;
//                                    }
//                                }
//                            } else {
//                                exit = 0;
//                            }
//
//                            if (exit == 1) {
//                                Toast.makeText(getApplicationContext(), "Registro fallido!", Toast.LENGTH_LONG).show();
//                                exit = 0;
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Registrando...", Toast.LENGTH_LONG).show();
//                                oficioViewModel.addOficioToFirebase(EditarOficioActivity.this, oficio);
////                                String idOficio = FirebaseDatabase.getInstance().getReference().child("oficios").push().getKey();
////                                oficio.setIdOficio(idOficio);
////                                FirebaseDatabase.getInstance().getReference()
////                                        .child("oficios")
////                                        .child(idOficio)
////                                        .setValue(oficio)
////                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                            @Override
////                                            public void onComplete(@NonNull Task<Void> task) {
////                                                if (task.isSuccessful()) {
////                                                    Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
////                                                } else {
////                                                    Toast.makeText(getApplicationContext(), "Registro fallido", Toast.LENGTH_SHORT).show();
////
////                                                }
////                                            }
////                                        }).addOnFailureListener(new OnFailureListener() {
////                                    @Override
////                                    public void onFailure(@NonNull Exception e) {
////                                        Toast.makeText(getApplicationContext(), "Registro fallido: " + e.toString(), Toast.LENGTH_SHORT).show();
////                                    }
////                                });
//                                //Toast.makeText(getApplicationContext(), "Registro exitoso!", Toast.LENGTH_LONG).show();
////                                oficioViewModel.guadarOficioEnFirebase(oficio);
//                                //oficioViewModel.guardarOficioEnFirebase(oficio, getApplicationContext());
//                            }
//                        }
//                        try {
//                            dialogNuevoOficio.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                        input.setText("");
//                    }
//
//
//                })
//                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        input.setText("");
//                        try {
//                            dialogNuevoOficio.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }).create();
//        dialogNuevoOficio.show();
//    }
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


}