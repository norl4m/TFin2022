package com.marlon.apolo.tfinal2022.ui.oficios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.model.Habilidad;

import java.util.List;


public class HabilidadViewModel extends AndroidViewModel {
    private LiveData<List<Habilidad>> allHabilidades;
    private HabilidadRepository habilidadRepository;
    public HabilidadViewModel(@NonNull Application application) {
        super(application);
        habilidadRepository = new HabilidadRepository();
    }

    public LiveData<List<Habilidad>> getAllHabilidades(String idOficio) {
        allHabilidades = habilidadRepository.getAllHabilidades(idOficio);
        return allHabilidades;
    }
}
