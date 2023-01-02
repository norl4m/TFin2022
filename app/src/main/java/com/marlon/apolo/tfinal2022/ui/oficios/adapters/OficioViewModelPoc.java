package com.marlon.apolo.tfinal2022.ui.oficios.adapters;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.ui.oficios.repository.OficioRepositoryPoc;

import java.util.ArrayList;

public class OficioViewModelPoc extends ViewModel {

    private static final String TAG = OficioViewModelPoc.class.getSimpleName();
    private OficioRepositoryPoc oficioRepositoryPoc;
    // ...
    // Expose screen UI state
    private MutableLiveData<ArrayList<Oficio>> oficios;

    public OficioViewModelPoc() {
        oficioRepositoryPoc = new OficioRepositoryPoc();
        oficios = oficioRepositoryPoc.getOficios();
    }


    public MutableLiveData<ArrayList<Oficio>> getOficios() {
//        if (oficios == null) {
//            oficios = new MutableLiveData<>();
//            oficios = oficioRepositoryPoc.getOficios();
//
//        }
        return oficios;
    }

}