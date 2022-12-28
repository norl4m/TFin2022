package com.marlon.apolo.tfinal2022.ui.authentication;

import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.Collections;
import java.util.Comparator;

public class AuthenticationFragment extends Fragment {

    private AuthenticationViewModel mViewModel;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private AuthUserListAdapter authUserListAdapter;

    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_authentication, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        recyclerView = root.findViewById(R.id.recyclerViewUsuarios);
        authUserListAdapter = new AuthUserListAdapter(requireActivity());

        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
            if (trabajadors != null) {

                Collections.sort(trabajadors, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

                authUserListAdapter.setTrabajadorArrayList(trabajadors);
//                    cleanInvitadoUI(root);
            }
        });

        EmpleadorViewModel mViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        // TODO: Use the ViewModel
        /*for (Empleador e : empleadors) {
                Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }*/
        mViewModel.getAllEmpleadores().observe(getViewLifecycleOwner(), empleadors -> {
            Collections.sort(empleadors, (t1, t2) -> (t1.getApellido()).compareTo(t2.getApellido()));
            authUserListAdapter.setEmpleadorArrayList(empleadors);

        });


        // Set up the RecyclerView.
        recyclerView.setAdapter(authUserListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        // TODO: Use the ViewModel


        AuthAsyncTask authAsyncTask = new AuthAsyncTask(requireActivity(), progressBar, authUserListAdapter);
        authAsyncTask.execute();
    }

}