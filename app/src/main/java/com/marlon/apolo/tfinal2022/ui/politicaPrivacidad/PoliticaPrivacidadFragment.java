package com.marlon.apolo.tfinal2022.ui.politicaPrivacidad;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marlon.apolo.tfinal2022.R;

public class PoliticaPrivacidadFragment extends Fragment {

    private PoliticaPrivacidadViewModel mViewModel;

    public static PoliticaPrivacidadFragment newInstance() {
        return new PoliticaPrivacidadFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.politica_privacidad_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PoliticaPrivacidadViewModel.class);
        // TODO: Use the ViewModel
    }

}