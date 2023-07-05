package com.marlon.apolo.tfinal2022.ui.oficios.viewModel;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.ui.oficios.repository.OficioRepository;

import java.util.List;

public class OficioViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    private final OficioRepository mRepository;
    private LiveData<List<Oficio>> allOficios;

    public OficioViewModel(Application application) {
        super(application);
        mRepository = new OficioRepository();
        //allOficios = mRepository.getAllOficios();
    }

    public LiveData<List<Oficio>> getAllOficios() {
        allOficios = mRepository.getAllOficios();
        return allOficios;
    }

    public void removeChildListener() {
        mRepository.removeChildListener();
    }

    public void addOficioToFirebase(Activity activity, Oficio oficio) {
        mRepository.addOficioTofirebase(activity, oficio);
    }

}