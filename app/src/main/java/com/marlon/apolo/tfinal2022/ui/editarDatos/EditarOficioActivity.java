package com.marlon.apolo.tfinal2022.ui.editarDatos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioRegistroListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;

import java.util.ArrayList;
import java.util.List;

public class EditarOficioActivity extends AppCompatActivity implements View.OnClickListener {


    private Trabajador trabajador;
    private OficioViewModel oficioViewModel;
    private OficioEditarListAdapter oficioRegistroListAdapter;
    private Dialog dialogNuevoOficio;
    private String oficioSelected;
    private int positionSelected;
    private boolean click;
    private int c;
    private ArrayList<Oficio> oficioArrayListSelected;
    private ArrayList<String> idOficiosReg;
    private ArrayList<String> idHabilidadesReg;

    private Button buttonUpdateOficios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_oficio);

        trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
        idOficiosReg = trabajador.getIdOficios();
        idHabilidadesReg = trabajador.getIdHabilidades();

        //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_SHORT).show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonUpdateOficios = findViewById(R.id.buttonUpdate);
        buttonUpdateOficios.setOnClickListener(this);


        RecyclerView recyclerView = findViewById(R.id.recyclerViewOficios);
        oficioRegistroListAdapter = new OficioEditarListAdapter(this);
        recyclerView.setAdapter(oficioRegistroListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(this, oficios -> {
            ArrayList<Oficio> oficiFilter = new ArrayList<>();
            if (oficios != null) {
                for (Oficio oficioDb : oficios) {
                    if (idOficiosReg.contains(oficioDb.getIdOficio())) {
                        oficioDb.setEstadoRegistro(true);
                        Log.d("TAG", oficioDb.toString());
                    }
                    oficiFilter.add(oficioDb);
                }
//                oficioRegistroListAdapter.setOficios(oficios);
                oficioRegistroListAdapter.setOficios(oficiFilter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdate:
                ArrayList<Oficio> oficiosSelected = new ArrayList<>();
                ArrayList<String> idOfs = new ArrayList<>();
                ArrayList<String> idHabs = new ArrayList<>();
                trabajador.setIdHabilidades(null);
                if (oficioRegistroListAdapter.getOficios() != null) {

                    for (Oficio ofSelect : oficioRegistroListAdapter.getOficios()) {
                        if (ofSelect.isEstadoRegistro()) {
                            //Toast.makeText(getApplicationContext(), ofSelect.toString(), Toast.LENGTH_SHORT).show();
                            oficiosSelected.add(ofSelect);
                            idOfs.add(ofSelect.getIdOficio());
                            if (ofSelect.getHabilidadArrayList() != null) {
                                for (Habilidad h : ofSelect.getHabilidadArrayList()) {
                                    if (h.isHabilidadSeleccionada()) {
                                        idHabs.add(h.getIdHabilidad());
                                    }
                                }
                            }
                        }
                    }
                }

                if (oficiosSelected.size() > 0) {
                    trabajador.setIdOficios(idOfs);
                    if (idHabs.size() > 0) {
                        trabajador.setIdHabilidades(idHabs);
                    }
                    trabajador.actualizarInfo(EditarOficioActivity.this);
                } else {
                    Toast.makeText(getApplicationContext()
                            , "Por favor seleccione al menos un oficio"
                            , Toast.LENGTH_LONG).show();
                }

                break;
        }
    }
}