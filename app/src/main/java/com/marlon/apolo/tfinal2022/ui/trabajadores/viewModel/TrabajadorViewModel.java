package com.marlon.apolo.tfinal2022.ui.trabajadores.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.trabajadores.repository.TrabajadorRepository;

import java.util.List;

public class TrabajadorViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel


    private final TrabajadorRepository trabajadorRepository;
    private LiveData<List<Trabajador>> allTrabajadores;
    private LiveData<Trabajador> oneTrabajador;
    private LiveData<Trabajador> auxTrabajador;

    public TrabajadorViewModel(@NonNull Application application) {
        super(application);
        trabajadorRepository = new TrabajadorRepository();
        allTrabajadores = trabajadorRepository.getAllTrabajadores();
    }

    public LiveData<List<Trabajador>> getAllTrabajadores() {
        return allTrabajadores;
    }

    public void removeChildListener() {
        trabajadorRepository.removeChildListener();
    }

    public LiveData<Trabajador> getOneTrabajador(String idTrabajador) {
        oneTrabajador = trabajadorRepository.getOneTrabajador(idTrabajador);
        return oneTrabajador;
    }

    public LiveData<Trabajador> getAuxTrabajador(String idTrabajador) {
        auxTrabajador = trabajadorRepository.getAuxTrabajador(idTrabajador);
        return auxTrabajador;
    }

}