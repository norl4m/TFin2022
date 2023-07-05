package com.marlon.apolo.tfinal2022.ui.bienvenido.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.buscador.view.BuscadorActivity;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.bienvenido.adaptaders.OficioArchiVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.bienvenido.adaptaders.OficioListAdapter;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.OficioViewModel;

import java.util.Collections;

public class OficiosActivityVista extends AppCompatActivity {

    private BienvenidoViewModel bienvenidoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficios_vista);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        OficioListAdapter oficioVistaListAdapter = new OficioListAdapter(this);
        RecyclerView recyclerViewOficios = findViewById(R.id.recyclerViewOficios);
        recyclerViewOficios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOficios.setAdapter(oficioVistaListAdapter);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.

        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);


        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadOficiosWithArchi(recyclerViewOficios, progressBar);
        } else {
            oficioViewModel.getAllOficios().observe(this, oficios -> {
                // update UI
//            oficios = null;
//            oficioVistaListAdapter.setOficios(null);

                if (oficios != null) {


                    Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));

                    oficioVistaListAdapter.setWords(oficios);
                    progressBar.setVisibility(View.GONE);
                    oficioVistaListAdapter.setOnItemClickListener(new OficioListAdapter.ClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Oficio oficioSlected = oficioVistaListAdapter.getWordAtPosition(position);
                            Intent intent = new Intent(getApplicationContext(), BuscadorActivity.class);
                            intent.setAction("android.intent.action.SEARCH");
                            intent.putExtra(SearchManager.QUERY, oficioSlected.getNombre());
                            intent.putExtra("offset", 1);
                            intent.putExtra("searchMode", 0);
                            startActivity(intent);
                        }
                    });
                } else {
                    //oficioVistaListAdapter.setNoResultados();
//                Toast.makeText(getApplicationContext(), R.string.no_resultados, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void loadOficiosWithArchi(RecyclerView recyclerView3, ProgressBar progressBar) {
//        RecyclerView recyclerView3 = root.findViewById(R.id.fragHomeRecyclerView3);
//        recyclerView3.setVisibility(View.GONE);
//        ProgressBar progressBar3 = root.findViewById(R.id.fragHomeProgressBar3);

        OficioArchiVistaListAdapter oficioArchiVistaListAdapter = new OficioArchiVistaListAdapter(this);
        recyclerView3.setAdapter(oficioArchiVistaListAdapter);
//        recyclerView3.setLayoutManager(new LinearLayoutManager(requireActivity()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(layoutManager);


        bienvenidoViewModel.getAllOficios().observe(this, oficios -> {
            if (oficios != null) {
                Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));


                oficioArchiVistaListAdapter.setOficios(oficios);
                progressBar.setVisibility(View.GONE);
                recyclerView3.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
//                startActivity(new Intent(OficiosActivityVista.this, ConfiguracionActivity.class));
                break;
            case R.id.mnu_nav_search:
                startActivity(new Intent(OficiosActivityVista.this, BuscadorActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}