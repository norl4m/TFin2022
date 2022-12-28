package com.marlon.apolo.tfinal2022.citasTrabajoArchi;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CitaTrabajoArchiViewModel extends AndroidViewModel {

    private CitaTrabajoArchiRepository mRepository;

    private LiveData<List<CitaTrabajoArchi>> mAllCitas;

    public CitaTrabajoArchiViewModel(Application application) {
        super(application);
        mRepository = new CitaTrabajoArchiRepository(application);
        mAllCitas = mRepository.getmAllCitas();
    }

    LiveData<List<CitaTrabajoArchi>> getAllWords() {
        return mAllCitas;
    }

    public void insert(CitaTrabajoArchi word, Activity activity) {
        mRepository.createCitaTrabajoArchi(word, activity);
    }

    public void deleteAll() {
        mRepository.deleteAllCitasTrabajoArchi();
    }

    public void deleteWord(CitaTrabajoArchi word) {
        mRepository.deleteCitaTrabajoArchi(word);
    }

    public void update(CitaTrabajoArchi word) {
        mRepository.updateCitaTrabajoArchi(word);
    }
}
