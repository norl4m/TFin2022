package com.marlon.apolo.tfinal2022.ui.empleadores.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.ui.empleadores.repository.EmpledorRepository;

import java.util.List;

public class EmpleadorViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    private EmpledorRepository mRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Empleador>> mAllEmpleadores;
    private LiveData<Empleador> oneEmpleador;
    private LiveData<Empleador> auxEmpleador;
    private LiveData<Empleador> empleadorLiveData;
    private LiveData<Integer> verficiadorDeUsuario;
    private String TAG = EmpleadorViewModel.class.getSimpleName();

    public EmpleadorViewModel(Application application) {
        super(application);
        mRepository = new EmpledorRepository(application);
        mAllEmpleadores = mRepository.
                getAllEmpleadores();
    }

    public LiveData<List<Empleador>> getAllEmpleadores() {
        return mAllEmpleadores;
    }

    public LiveData<Empleador> getEmpleadorLiveData(String idUsuario) {
        empleadorLiveData = mRepository.getEmpleador(idUsuario);
        return empleadorLiveData;
    }

    public LiveData<Integer> getVerficiadorDeUsuario(String emailCelular) {
        Log.d(TAG, "VERIFICANDO USUARIO");
        verficiadorDeUsuario = mRepository.getVerificadorDeUsuarioRegistrado(emailCelular);
        return verficiadorDeUsuario;
    }

    public void removeChildListener() {
        mRepository.removeChildListener();
    }

    public LiveData<Empleador> getOneEmpleador(String idEmpleador) {
        oneEmpleador = mRepository.getOneEmpleador(idEmpleador);
        return oneEmpleador;
    }

    public LiveData<Empleador> getAuxEmpleador(String idEmpleador) {
        auxEmpleador = mRepository.getAuxEmpleador(idEmpleador);
        return auxEmpleador;
    }


//    public void insert(Word word) { mRepository.insert(word); }

}