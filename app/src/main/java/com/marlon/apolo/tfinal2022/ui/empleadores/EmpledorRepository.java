package com.marlon.apolo.tfinal2022.ui.empleadores;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.model.Empleador;

import java.util.ArrayList;
import java.util.List;

public class EmpledorRepository {
    private static final String TAG = EmpledorRepository.class.getSimpleName();
    private MutableLiveData<List<Empleador>> mAllEmpleadores;
    private MutableLiveData<Empleador> empleador;
    private MutableLiveData<Integer> verificadorDeUsuarioRegistrado;
    private ChildEventListener empleadorChildEventListener;
    private MutableLiveData<Empleador> oneEmpleador;
    private MutableLiveData<Empleador> auxEmpleador;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public EmpledorRepository(Application application) {
//        mAllWords = mWordDao.getAlphabetizedWords();
//        if (verificadorDeUsuarioRegistrado == null) {
//            verificadorDeUsuarioRegistrado = new MutableLiveData<>();
//            verificadorDeUsuarioRegistrado.setValue(-1);
//        }
        ArrayList<Empleador> empleadorArrayList = new ArrayList<>();

        empleadorChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    Log.e(TAG, empleador.toString());
                    empleadorArrayList.add(empleador);
                    mAllEmpleadores.setValue(empleadorArrayList);
                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (empleadorArrayList.size() > 0) {
                    try {
                        Empleador trabajadorChanged = snapshot.getValue(Empleador.class);
                        for (Empleador trabajadorDB : empleadorArrayList) {
                            try {
                                if (trabajadorDB.getIdUsuario().equals(trabajadorChanged.getIdUsuario())) {
                                    empleadorArrayList.set(empleadorArrayList.indexOf(trabajadorDB), trabajadorChanged);
                                    break;
                                }
                            } catch (Exception e) {

                            }

                        }
                        mAllEmpleadores.setValue(empleadorArrayList);
                    } catch (Exception e) {
                        Log.d(TAG, "onChildChanged ERROR");
                        Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (empleadorArrayList.size() > 0) {
                    try {
                        Empleador trabajadorChanged = snapshot.getValue(Empleador.class);
                        for (Empleador trabajadorDB : empleadorArrayList) {
                            try {
                                if (trabajadorDB.getIdUsuario().equals(trabajadorChanged.getIdUsuario())) {
                                    empleadorArrayList.remove(empleadorArrayList.indexOf(trabajadorDB));
                                    break;
                                }
                            } catch (Exception e) {

                            }

                        }
                        mAllEmpleadores.setValue(empleadorArrayList);
                    } catch (Exception e) {
                        Log.d(TAG, "onChildChanged ERROR");
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

    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Empleador>> getAllEmpleadores() {
        if (mAllEmpleadores == null) {
            mAllEmpleadores = new MutableLiveData<>();
            loadAllEmpleadores();

        }
        return mAllEmpleadores;
    }

    private void loadAllEmpleadores() {
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .addChildEventListener(empleadorChildEventListener);
    }

    public void removeChildListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("trabajadores")
                .removeEventListener(empleadorChildEventListener);
    }

    public MutableLiveData<Empleador> getEmpleador(String idUsuario) {
        if (empleador == null) {
            empleador = new MutableLiveData<>();
            loadEmpleador(idUsuario);
        }
        return empleador;
    }


    private void loadEmpleador(String idUsuario) {
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(idUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleadorDB = snapshot.getValue(Empleador.class);
                            empleador.setValue(empleadorDB);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public MutableLiveData<Integer> getVerificadorDeUsuarioRegistrado(String emailCelular) {
        if (verificadorDeUsuarioRegistrado.getValue() == null) {
            verificadorDeUsuarioRegistrado = new MutableLiveData<>();
            buscarPorEmailCelular(emailCelular);
        }
        return verificadorDeUsuarioRegistrado;
    }

    private void buscarPorEmailCelular(String emailCelular) {
        Log.d(TAG, "BUSCANDO POR EMAIL");


        Query q = FirebaseDatabase.getInstance().getReference().child("empleadores").orderByChild("email").equalTo(emailCelular);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, snapshot.toString());
                verificadorDeUsuarioRegistrado.setValue(0);
//                for (DataSnapshot snapShot : snapshot.getChildren()) {
//                    if (snapShot.equals(doc))
//                        cont++;
//                    verificadorDeUsuarioRegistrado.setValue(0);
//
////                                                     Toast.makeText(PollaMundialista.this, "Encontrado "+cont, Toast.LENGTH_LONG).show();
//                }
//                if (cont < 0) {
////                                                     Toast.makeText(PollaMundialista.this, "El valor no se encuentra registrado"+cont, Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//
//        FirebaseDatabase.getInstance().getReference()
//                .child("empleadores")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            try {
//                                Empleador empleadorDB = data.getValue(Empleador.class);
//                                Log.d(TAG, "****************************************");
//                                Log.d(TAG, empleadorDB.toString());
//                                Log.d(TAG, "****************************************");
//
//                                if (empleadorDB.getEmail() != null) {
//                                    if (empleadorDB.getEmail().equals(emailCelular)) {
//                                        verificadorDeUsuarioRegistrado.setValue(0);
//                                    }
//                                }
//                                if (empleadorDB.getCelular() != null) {
//                                    if (empleadorDB.getCelular().equals(emailCelular)) {
//                                        verificadorDeUsuarioRegistrado.setValue(0);
//                                    }
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }

    public MutableLiveData<Empleador> getOneEmpleador(String idEmpleador) {
        if (oneEmpleador == null) {
            oneEmpleador = new MutableLiveData<>();
            loadOneEmpleador(idEmpleador);
        }
        return oneEmpleador;
    }

    public MutableLiveData<Empleador> getAuxEmpleador(String idEmpleador) {
        //if (auxEmpleador == null) {
        auxEmpleador = new MutableLiveData<>();
        loadAuxTrabajador(idEmpleador);
        //}
        return auxEmpleador;
    }

    private void loadAuxTrabajador(String idEmpleador) {
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(idEmpleador)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            auxEmpleador.setValue(empleador);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOneEmpleador(String idEmpleador) {
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(idEmpleador)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            oneEmpleador.setValue(empleador);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
//    public void insert (Empleador empleador) {
//        new insertAsyncTask(mWordDao).execute(empleador);
//    }
//
//    private static class insertAsyncTask extends AsyncTask<Word, Void, Void> {
//
//        private WordDao mAsyncTaskDao;
//
//        insertAsyncTask(WordDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(final Word... params) {
//            mAsyncTaskDao.insert(params[0]);
//            return null;
//        }
//    }
}
