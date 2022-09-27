package com.marlon.apolo.tfinal2022.ui.oficios;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpledorRepository;

import java.util.ArrayList;
import java.util.List;

public class OficioViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    private final OficioRepository mRepository;
    private LiveData<List<Oficio>> allOficios;
    private LiveData<Oficio> oneOficio;


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

    public void addHabilidadToOficioTofirebase(Activity activity, Oficio oficio, Habilidad habilidad) {
        mRepository.addHabilidadToOficioTofirebase(activity, oficio, habilidad);
    }


    public LiveData<Oficio> getOneOficio(String id) {
        oneOficio = mRepository.getOneOficio(id);
        return oneOficio;
    }
}