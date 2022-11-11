package com.marlon.apolo.tfinal2022.ui.authentication;

import androidx.lifecycle.ViewModelProvider;

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

import com.marlon.apolo.tfinal2022.R;

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


        AuthAsyncTask authAsyncTask = new AuthAsyncTask(requireActivity(), progressBar,authUserListAdapter);
        authAsyncTask.execute();
    }

}