package com.marlon.apolo.tfinal2022.individualChat.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.marlon.apolo.tfinal2022.individualChat.model.Mensajito;
import com.marlon.apolo.tfinal2022.individualChat.repository.MensajitoRepository;

import java.util.List;

public class MensajitoViewModel extends AndroidViewModel {
    private MensajitoRepository mRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Mensajito>> mAllWords;
    private String TAG = MensajitoViewModel.class.getSimpleName();

    public MensajitoViewModel(Application application) {
        super(application);
        mRepository = new MensajitoRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public LiveData<List<Mensajito>> getAllWords() {
        return mAllWords;
    }

    public LiveData<List<Mensajito>> getFilterMessages(String from, String to) {
        return mRepository.getFilterMessages(from, to);
    }

    public LiveData<List<Mensajito>> getFilterMessagesByChat(String idChat) {
        return mRepository.getFilterMessagesByChat(idChat);
    }

    public Mensajito getLocalMessage(String id) {
        return mRepository.getLocalMessage(id);
    }

    public void deleteFilterMessages(String from, String to) {
        mRepository.deleteFilterMessage(from, to);
    }

    public void insert(Mensajito word) {
        Log.d(TAG, "#########################################");
        Log.d(TAG, "GUARDANDO MENSAJITO EN LA BASE DE DATOS");
        Log.d(TAG, word.toString());
        Log.d(TAG, "###########################################");
        mRepository.insert(word);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteWord(Mensajito word) {
        mRepository.deleteWord(word);
    }

    public void update(Mensajito word) {
        Log.d(TAG, "######################################################");
        Log.d(TAG, "ACTUALIZANDO MENSAJITO EN LA BASE DE DATOS");
        Log.d(TAG, word.toString());
        Log.d(TAG, "######################################################");

        mRepository.update(word);
    }

}
