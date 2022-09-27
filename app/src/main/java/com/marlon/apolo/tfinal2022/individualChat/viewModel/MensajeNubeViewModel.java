package com.marlon.apolo.tfinal2022.individualChat.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNubeRepository;

import java.util.ArrayList;
import java.util.List;

public class MensajeNubeViewModel extends AndroidViewModel {
    private final MensajeNubeRepository mensajeNubeRepository;
    private LiveData<ArrayList<MensajeNube>> allMensajesNube;


    public MensajeNubeViewModel(Application application) {
        super(application);
        this.mensajeNubeRepository = new MensajeNubeRepository();
    }

    public LiveData<ArrayList<MensajeNube>> getAllMensajesNube(String idChat) {
        allMensajesNube = mensajeNubeRepository.getAllMensajes(idChat);
        return allMensajesNube;
    }

    public void removeChildListener(String idUsuario) {
        mensajeNubeRepository.removeChilLestenerMensajes(idUsuario);
    }

}
