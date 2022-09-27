package com.marlon.apolo.tfinal2022.ui.citaTrabajo;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaListAdapter;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaViewModel;
import com.marlon.apolo.tfinal2022.model.Cita;

import java.util.ArrayList;
import java.util.Collections;

public class CitaTrabajoFragment extends Fragment {

    private static final String TAG = CitaTrabajoFragment.class.getSimpleName();
    //    private CitaTrabajoViewModel mViewModel;
    private CitaViewModel mViewModel;
    private ArrayList<Cita> citasDB;
    private CitaListAdapter citaListAdapter;
    private ArrayList<Cita> citas;
    private RecyclerView recyclerView;
    private int usuario;


    public static CitaTrabajoFragment newInstance() {
        return new CitaTrabajoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cita_trabajo_fragment, container, false);
        recyclerView = root.findViewById(R.id.fragHomeRecyclerViewCitas);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CitaViewModel.class);

        citasDB = new ArrayList<>();
        citas = new ArrayList<>();
        mViewModel
                .getCitas()
                .observe(getViewLifecycleOwner(), new Observer<ArrayList<Cita>>() {
                    @Override
                    public void onChanged(ArrayList<Cita> citas) {
                        if (citas != null) {
                            citasDB.clear();
//                            citasDB = citas;
                            citasDB.addAll(citas);
                            citaListAdapter = new CitaListAdapter(requireActivity());
                            recyclerView.setAdapter(citaListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                            citaListAdapter.setCitas(citas);
                        }
                    }

                });
//        mViewModel = new ViewModelProvider(this).get(CitaTrabajoViewModel.class);
        // TODO: Use the ViewModel
//        requireActivity().startActivity(new Intent(requireActivity(), CitaTrabajoActivity.class));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_citas, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_filter_completados:
//                Toast.makeText(getActivity(), "Completados!", Toast.LENGTH_SHORT).show();
                citas.clear();
                for (Cita cita : citasDB) {
                    Log.e(TAG, cita.toString());
                    if (cita.isState()) {
                        citas.add(cita);
                    }
                }
                citaListAdapter.setCitas(citas);
                return true;
            case R.id.menu_filter_pendientes:
//                Toast.makeText(getActivity(), "Pendientes!", Toast.LENGTH_SHORT).show();
                citas.clear();
                for (Cita cita : citasDB) {
                    Log.e(TAG, cita.toString());
                    if (!cita.isState()) {
                        citas.add(cita);
                    }
                }
                citaListAdapter.setCitas(citas);
                return true;

            case R.id.menu_all_citas:
//                Toast.makeText(getActivity(), "Todos!", Toast.LENGTH_SHORT).show();
                citaListAdapter.setCitas(citasDB);
                return true;

            case R.id.menu_filter_by_date:
//                Toast.makeText(getActivity(), "Todos!", Toast.LENGTH_SHORT).show();
                citaListAdapter.setCitas(citasDB);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        usuario = prefs.getInt("usuario", -1);
        Log.i(TAG, String.format("USUARIO: %d", usuario));
        setHasOptionsMenu(true);
    }

}