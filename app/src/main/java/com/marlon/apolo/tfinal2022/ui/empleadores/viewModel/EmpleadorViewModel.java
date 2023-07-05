package com.marlon.apolo.tfinal2022.ui.empleadores.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.ui.empleadores.repository.EmpledorRepository;

import java.util.List;

public class EmpleadorViewModel extends AndroidViewModel {

    private EmpledorRepository mRepository;
    private LiveData<List<Empleador>> mAllEmpleadores;
    private LiveData<Empleador> oneEmpleador;

    public EmpleadorViewModel(Application application) {
        super(application);
        mRepository = new EmpledorRepository(application);
        mAllEmpleadores = mRepository.
                getAllEmpleadores();
    }

    public LiveData<List<Empleador>> getAllEmpleadores() {
        return mAllEmpleadores;
    }

    public LiveData<Empleador> getOneEmpleador(String idEmpleador) {
        oneEmpleador = mRepository.getOneEmpleador(idEmpleador);
        return oneEmpleador;
    }

}