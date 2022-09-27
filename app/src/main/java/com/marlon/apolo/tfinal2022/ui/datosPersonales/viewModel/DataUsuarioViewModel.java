package com.marlon.apolo.tfinal2022.ui.datosPersonales.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataUsuarioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DataUsuarioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Data Usuario");
    }

    public LiveData<String> getText() {
        return mText;
    }
}