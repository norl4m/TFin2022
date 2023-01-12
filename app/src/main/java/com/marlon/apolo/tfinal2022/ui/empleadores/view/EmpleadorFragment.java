package com.marlon.apolo.tfinal2022.ui.empleadores.view;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.registro.view.RegDatoPersonalActivity;
import com.marlon.apolo.tfinal2022.ui.empleadores.viewModel.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.empleadores.adapters.EmpleadorCRUDListAdapter;

import java.util.Collections;
import java.util.Comparator;

public class EmpleadorFragment extends Fragment {

    private EmpleadorViewModel mViewModel;

    private View root;

    public static EmpleadorFragment newInstance() {
        return new EmpleadorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.empleador_fragment, container, false);
        FloatingActionButton floatingActionButton = root.findViewById(R.id.fabAddEmp);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegUsuario = new Intent(requireActivity(), RegDatoPersonalActivity.class);
                intentRegUsuario.putExtra("usuario", 1);
                startActivity(intentRegUsuario);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewEmpleadores);
        final EmpleadorCRUDListAdapter adapter = new EmpleadorCRUDListAdapter(requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        // TODO: Use the ViewModel
        /*for (Empleador e : empleadors) {
                Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }*/
        mViewModel.getAllEmpleadores().observe(getViewLifecycleOwner(), adapter::setEmpleadores);
        mViewModel.getAllEmpleadores().observe(getViewLifecycleOwner(), empleadors -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(empleadors, Comparator.comparing(Usuario::getApellido));
//                Collections.sort(empleadors, Comparator.comparing(t -> (t.getNombre() + " " + t.getApellido())));
            } else {
//                Collections.sort(empleadors, (t1, t2) -> (t1.getNombre() + " " + t1.getApellido()).compareTo(t2.getNombre() + " " + t2.getApellido()));
                Collections.sort(empleadors, (t1, t2) -> (t1.getApellido()).compareTo(t2.getApellido()));

            }
            adapter.setEmpleadores(empleadors);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_empleadores, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.mnu_add_empleador:
                Intent intentRegUsuario = new Intent(requireActivity(), RegDatoPersonalActivity.class);
                intentRegUsuario.putExtra("usuario", 1);
                startActivity(intentRegUsuario);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}