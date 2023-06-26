package com.marlon.apolo.tfinal2022.admin.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.admin.model.repository.AdminRepository;
import com.marlon.apolo.tfinal2022.model.Administrador;

public class AdminViewModel extends AndroidViewModel {
    private AdminRepository adminRepository;
    private LiveData<Administrador> administradorLiveData;

    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminRepository = new AdminRepository();
    }

    public LiveData<Administrador> getAdministradorLiveData(String idUsuario) {
        administradorLiveData = adminRepository.getAdministradorMutableLiveData(idUsuario);
        return administradorLiveData;
    }

    public void removeValueEventListener() {
        adminRepository.removeValueListener();
    }

}
