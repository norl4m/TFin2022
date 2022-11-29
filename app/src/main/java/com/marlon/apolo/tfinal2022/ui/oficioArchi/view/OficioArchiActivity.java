package com.marlon.apolo.tfinal2022.ui.oficioArchi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.adapters.OficioArchiListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.model.OficioArchiModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel.OficioArchiViewModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OficioArchiActivity extends AppCompatActivity {

    private static final String TAG = OficioArchiActivity.class.getSimpleName();
    private OficioArchiViewModel oficioArchiViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficio_archi);


        oficioArchiViewModel = new ViewModelProvider(this).get(OficioArchiViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final OficioArchiListAdapter adapter = new OficioArchiListAdapter(this);
        recyclerView.setAdapter(adapter);
        // recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));

        ProgressBar progressBar = findViewById(R.id.progressBar);


//

//        oficioArchiViewModel.getAllOficios().observe(this, new Observer<List<OficioArchiModel>>() {
//            @Override
//            public void onChanged(List<OficioArchiModel> oficioArchiModels) {
//
//            }
//        });

//        oficioArchiViewModel.getAllOficios().observe(this, new Observer<List<OficioArchiModel>>() {
//            @Override
//            public void onChanged(List<OficioArchiModel> oficioArchiModels) {
//                adapter.setOficios(oficioArchiModels);
//            }
//        });

        Log.d(TAG, String.valueOf(oficioArchiViewModel.getNumberOficios()));

        oficioArchiViewModel.getNumberOficios().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    ArrayList<OficioArchiModel> oficioArchiModels = new ArrayList<>();
                    OficioArchiModel oficioArchiModel = new OficioArchiModel();
                    oficioArchiModel.setIdOficio("noResultados");
                    oficioArchiModel.setNombre("Lo sentimos no se encontraron resultados.");
                    oficioArchiModels.add(oficioArchiModel);
                    adapter.setOficios(oficioArchiModels);
                    progressBar.setVisibility(View.GONE);

                } else {
                    oficioArchiViewModel.getAllOficios().observe(OficioArchiActivity.this, new Observer<List<OficioArchiModel>>() {
                        @Override
                        public void onChanged(List<OficioArchiModel> oficioArchiModels) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(oficioArchiModels, Comparator.comparing(OficioArchiModel::getNombre));
                            } else {
                                Collections.sort(oficioArchiModels, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));
                            }

                            adapter.setOficios(oficioArchiModels);
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }
            }
        });
//        if (oficioArchiViewModel.get() == 0) {
//            //Toast.makeText(getApplicationContext(), "AAAAAAAAAAAA", Toast.LENGTH_SHORT).show();
//            ArrayList<OficioArchiModel> oficioArchiModels = new ArrayList<>();
//            OficioArchiModel oficioArchiModel = new OficioArchiModel();
//            oficioArchiModel.setIdOficio("noResultados");
//            oficioArchiModel.setNombre("No se encontraron resultados.");
//            oficioArchiModels.add(oficioArchiModel);
//            adapter.setOficios(oficioArchiModels);
//        } else {
//
//            oficioArchiViewModel.getAllOficios().observe(this, new Observer<List<OficioArchiModel>>() {
//                @Override
//                public void onChanged(List<OficioArchiModel> oficioArchiModels) {
//                    adapter.setOficios(oficioArchiModels);
//                }
//            });
//        }

//        FloatingActionButton floatingActionButton = findViewById(R.id.fabNuevoOficio);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(OficioArchiActivity.this, NuevoOficioArchiActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_oficio_archi, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_nuevo:
                Intent intent = new Intent(OficioArchiActivity.this, NuevoOficioArchiActivity.class);
                startActivity(intent);
                return true;
//            case R.id.action_linear_layout:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}