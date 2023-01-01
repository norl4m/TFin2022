package com.marlon.apolo.tfinal2022.ui.oficios.adaptadores;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.ArrayList;
import java.util.List;

public class OficioViewModelPoc extends ViewModel {

    private static final String TAG = OficioViewModelPoc.class.getSimpleName();
    private OficioRepositoryPoc oficioRepositoryPoc;
    // ...
    // Expose screen UI state
    private MutableLiveData<ArrayList<Oficio>> oficios;

    public OficioViewModelPoc() {
        oficioRepositoryPoc = new OficioRepositoryPoc();
        oficios = oficioRepositoryPoc.getOficios();
    }


    public MutableLiveData<ArrayList<Oficio>> getOficios() {
//        if (oficios == null) {
//            oficios = new MutableLiveData<>();
//            oficios = oficioRepositoryPoc.getOficios();
//
//        }
        return oficios;
    }

}