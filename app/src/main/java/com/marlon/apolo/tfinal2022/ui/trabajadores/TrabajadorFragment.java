package com.marlon.apolo.tfinal2022.ui.trabajadores;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.PerfilActivity;
import com.marlon.apolo.tfinal2022.registro.RegNombreUsuarioActivity;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioVistaListAdapter;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;

public class TrabajadorFragment extends Fragment {

    private TrabajadorViewModel mViewModel;
    private View root;
    private TrabajadorViewModel trabajadorViewModel;

    public static TrabajadorFragment newInstance() {
        return new TrabajadorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.trabajador_fragment, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        // TODO: Use the ViewModel
        RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerViewTrabajadores);

        TrabajadorCRUDListAdapter trabajadorListAdapter = new TrabajadorCRUDListAdapter(requireActivity());
        recyclerView1.setAdapter(trabajadorListAdapter);
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));

//        FirebaseDatabase.getInstance().getReference()
//                .child("oficios")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<Oficio> oficioArrayList = new ArrayList<>();
//                        Toast.makeText(requireActivity(),"Load oficios",Toast.LENGTH_LONG).show();
//                        for (DataSnapshot data: snapshot.getChildren()) {
//                            Oficio oficio = data.getValue(Oficio.class);
//                            oficioArrayList.add(oficio);
//                        }
//                        trabajadorListAdapter.setOficioList(oficioArrayList);
//
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
//                .child("trabajadores")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<Trabajador> trabajadorArrayLista = new ArrayList<>();
//                        Toast.makeText(requireActivity(),"Load trabajdores",Toast.LENGTH_LONG).show();
//
//                        for (DataSnapshot data: snapshot.getChildren()) {
//                            Trabajador trabajador = data.getValue(Trabajador.class);
//                            trabajadorArrayLista.add(trabajador);
//                        }
//                        trabajadorListAdapter.setTrabajadores(trabajadorArrayLista);
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        /*Carga de datos oficios*/
        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(requireActivity(), oficios -> {

            trabajadorListAdapter.setOficioList(oficios);
            /*Carga de datos trabajadores*/
//            TrabajadorCRUDListAdapter trabajadorListAdapter = new TrabajadorCRUDListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);
//            recyclerView1.setAdapter(trabajadorListAdapter);
//            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
            trabajadorViewModel.getAllTrabajadores().observe(requireActivity(), trabajadors -> {
                if (trabajadors != null) {
                    Collections.sort(trabajadors, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

                    trabajadorListAdapter.setTrabajadores(trabajadors);

                }
            });

            /*Carga de datos trabajadores*/
        });
        /*Carga de datos oficios*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trabajadores, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.mnu_add_trabajador:
//                Toast.makeText(requireActivity(), "Add trabajdor", Toast.LENGTH_SHORT).show();
                Intent intentRegUsuario = new Intent(requireActivity(), RegNombreUsuarioActivity.class);
                intentRegUsuario.putExtra("usuario", 2);
                startActivity(intentRegUsuario);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}