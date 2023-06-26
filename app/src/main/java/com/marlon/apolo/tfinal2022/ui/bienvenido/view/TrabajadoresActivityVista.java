package com.marlon.apolo.tfinal2022.ui.bienvenido.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.buscador.view.BuscadorActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.TrabajadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.adapters.TrabajadorListAdapter;

public class TrabajadoresActivityVista extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajadores_vista);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TrabajadorListAdapter trabajadorListAdapter = new TrabajadorListAdapter(this);
        RecyclerView recyclerViewTrabajadores = findViewById(R.id.recyclerViewTrabajadores);
        recyclerViewTrabajadores.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrabajadores.setAdapter(trabajadorListAdapter);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(this, oficios -> {
            if (oficios != null) {
                trabajadorListAdapter.setOficioList(oficios);
            }
        });


        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(this, trabajadors -> {
            if (trabajadors != null) {
                trabajadorListAdapter.setTrabajadores(trabajadors);
                progressBar.setVisibility(View.GONE);
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
//                startActivity(new Intent(TrabajadoresActivityVista.this, ConfiguracionActivity.class));
                break;
            case R.id.mnu_nav_search:
                startActivity(new Intent(TrabajadoresActivityVista.this, BuscadorActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}