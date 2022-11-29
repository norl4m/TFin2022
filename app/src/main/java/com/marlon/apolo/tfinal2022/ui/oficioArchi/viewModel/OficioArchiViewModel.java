package com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel;

import android.app.Application;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlon.apolo.tfinal2022.ui.oficioArchi.model.OficioArchiModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.repository.OficioArchiRepository;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.NuevoOficioArchiActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.OficioArchiEditDeleteActivity;

import java.util.List;

public class OficioArchiViewModel extends AndroidViewModel {

    private OficioArchiRepository mRepository;

    private LiveData<List<OficioArchiModel>> allOficios;
    private MutableLiveData<Integer> numberOficios;

    public OficioArchiViewModel(@NonNull Application application) {
        super(application);
        mRepository = new OficioArchiRepository();
        numberOficios = mRepository.getTotalSize();
        allOficios = mRepository.getAllOficios();
    }

    public LiveData<List<OficioArchiModel>> getAllOficios() {
        return allOficios;
    }

    public void insert(OficioArchiModel oficioArchiModel, NuevoOficioArchiActivity nuevoOficioArchiActivity, ProgressDialog progressDialog) {
        mRepository.writeNewOficioWithTaskListeners(oficioArchiModel, nuevoOficioArchiActivity, progressDialog);
    }
    public void delete(OficioArchiModel oficioArchiModel, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
        mRepository.deleteOficio(oficioArchiModel, oficioArchiEditDeleteActivity, progressDialog);
    }
    public void update(OficioArchiModel oficioArchiModel, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
        mRepository.updateOficio(oficioArchiModel, oficioArchiEditDeleteActivity, progressDialog);
    }
//
//    public int getNumberOficios() {
//        return numberOficios;
//    }


    public MutableLiveData<Integer> getNumberOficios() {
        return numberOficios;
    }
}
