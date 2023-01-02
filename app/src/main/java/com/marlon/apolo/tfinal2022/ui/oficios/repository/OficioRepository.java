package com.marlon.apolo.tfinal2022.ui.oficios.repository;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.ui.empleadores.repository.EmpledorRepository;

import java.util.ArrayList;
import java.util.List;

public class OficioRepository {


    private static final String TAG = EmpledorRepository.class.getSimpleName();
    private MutableLiveData<List<Oficio>> allOficios;
    private MutableLiveData<Oficio> oneOficio;

    private ChildEventListener oficioChildEventListener;

    public OficioRepository() {
//        enablePersistence();
//        keepSynced();

    }


    public MutableLiveData<List<Oficio>> getAllOficios() {
        if (allOficios == null) {
            allOficios = new MutableLiveData<>();
            loadAllOficios();
        }
        return allOficios;
    }

    private void loadAllOficios() {
        ArrayList<Oficio> oficios = new ArrayList<>();
        oficioChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Oficio oficio = snapshot.getValue(Oficio.class);
                    oficios.add(oficio);
                    allOficios.setValue(oficios);
                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (oficios.size() > 0) {
                    try {
                        Oficio oficioChanged = snapshot.getValue(Oficio.class);
                        int index = 0;
                        for (Oficio oficioDB : oficios) {
                            try {
                                if (oficioDB.getIdOficio().equals(oficioChanged.getIdOficio())) {
                                    oficios.set(index, oficioChanged);
                                    break;
                                }
                            } catch (Exception e) {

                            }
                            index++;

                        }
                        allOficios.setValue(oficios);
                    } catch (Exception e) {
                        Log.d(TAG, "onChildChanged ERROR");
                        Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved");
                if (oficios.size() > 0) {
                    try {
                        Oficio oficioRemoved = snapshot.getValue(Oficio.class);

                        int inde = 0;

                        for (Oficio oficioDB : oficios) {
                            try {
                                if (oficioDB.getIdOficio().equals(oficioRemoved.getIdOficio())) {
//                                            oficios.set(oficios.indexOf(oficioDB), null);
                                    //oficios.remove(oficios.indexOf(oficioDB));
                                    oficios.remove(inde);
                                    break;

                                }
                            } catch (Exception e) {

                            }
                            inde++;
                        }
                        allOficios.setValue(oficios);
                    } catch (Exception e) {
                        Log.d(TAG, "onChildRemoved ERROR");
                        Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .addChildEventListener(oficioChildEventListener);
    }

    public void removeChildListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("oficios")
                .removeEventListener(oficioChildEventListener);
    }

    public void addOficioTofirebase(Activity activity, Oficio oficio) {

        String idOficio = FirebaseDatabase.getInstance().getReference().child("oficios").push().getKey();
        oficio.setIdOficio(idOficio);
        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .child(idOficio)
                .setValue(oficio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Registro fallido", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Registro fallido: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public MutableLiveData<Oficio> getOneOficio(String id) {
        if (oneOficio == null) {
            oneOficio = new MutableLiveData<>();
            loadOficio(id);
        }
        return oneOficio;
    }

    private void loadOficio(String id) {
        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Oficio oficio = snapshot.getValue(Oficio.class);
                            oneOficio.setValue(oficio);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    // [START rtdb_undo_keep_synced]
//        scoresRef.keepSynced(false);
    // [END rtdb_undo_keep_synced]


}
