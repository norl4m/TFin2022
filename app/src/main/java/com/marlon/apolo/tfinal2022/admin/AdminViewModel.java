package com.marlon.apolo.tfinal2022.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.model.Administrador;

public class AdminViewModel extends AndroidViewModel {
    private AdminRepository adminRepository;
    private LiveData<Administrador> administradorLiveData;
    private LiveData<Administrador> auxAdmin;
    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminRepository = new AdminRepository();
    }

    public LiveData<Administrador> getAdministradorLiveData(String idUsuario) {
        administradorLiveData =  adminRepository.getAdministradorMutableLiveData(idUsuario);
        return administradorLiveData;
    }

    public void removeValueEventListener(){
        adminRepository.removeValueListener();
    }

    public LiveData<Administrador> getAuxAdmin(String idUsuario) {
        auxAdmin =  adminRepository.getAuxAdmin(idUsuario);
        return auxAdmin;
    }

}
