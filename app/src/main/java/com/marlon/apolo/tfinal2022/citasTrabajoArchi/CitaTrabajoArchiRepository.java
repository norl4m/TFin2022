package com.marlon.apolo.tfinal2022.citasTrabajoArchi;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CitaTrabajoArchiRepository {
    private final DatabaseReference databaseReference;
    //    private WordDao mWordDao;
    private MutableLiveData<List<CitaTrabajoArchi>> mAllCitas;
    private final static String CITA_LOCATION_ON_FIREBASE = "citas";
    private final static String TAG = CitaTrabajoArchiRepository.class.getSimpleName();
    private ChildEventListener childEventListenerCitas;

    public CitaTrabajoArchiRepository(Application application) {
//        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
//        mWordDao = db.wordDao();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<List<CitaTrabajoArchi>> getmAllCitas() {
        if (mAllCitas == null) {
            mAllCitas = new MutableLiveData<>();
            readCitasFromFirebase();
        }
        return mAllCitas;
    }

    private void readCitasFromFirebase() {
        ArrayList<CitaTrabajoArchi> arrayList = new ArrayList<>();
        childEventListenerCitas = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "---------------" + "onChildAdded" + "---------------");
                CitaTrabajoArchi citaTrabajoArchi = snapshot.getValue(CitaTrabajoArchi.class);
                Log.d(TAG, citaTrabajoArchi.toString());
                arrayList.add(citaTrabajoArchi);
                mAllCitas.setValue(arrayList);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "---------------" + "onChildChanged" + "---------------");
                CitaTrabajoArchi citaTrabajoArchiChanged = snapshot.getValue(CitaTrabajoArchi.class);
                Log.d(TAG, citaTrabajoArchiChanged.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "---------------" + "onChildRemoved" + "---------------");
                CitaTrabajoArchi citaTrabajoArchiRemoved = snapshot.getValue(CitaTrabajoArchi.class);
                Log.d(TAG, citaTrabajoArchiRemoved.toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(CITA_LOCATION_ON_FIREBASE).addChildEventListener(childEventListenerCitas);
    }

    public void removeChildEventListenerCitas() {
        databaseReference.child(CITA_LOCATION_ON_FIREBASE).removeEventListener(childEventListenerCitas);
    }

    public void createCitaTrabajoArchi(CitaTrabajoArchi citaTrabajoArchi, Activity activity) {
//        new insertAsyncTask(mWordDao).execute(word);

        String idCita = databaseReference.child(CITA_LOCATION_ON_FIREBASE).push().getKey();
        citaTrabajoArchi.setId(idCita);
        citaTrabajoArchi.setObservaciones(citaTrabajoArchi.getObservaciones().trim());

        databaseReference.child(CITA_LOCATION_ON_FIREBASE)
                .child(citaTrabajoArchi.getId())
                .setValue(citaTrabajoArchi)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Cita de trabajo insertada");
                            Log.d(TAG, "Cita de trabajo insertada" + citaTrabajoArchi.toString());
                            Toast.makeText(activity, citaTrabajoArchi.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "Error al insertar cita de trabajo");
                        }
                    }
                });
    }

    public void updateCitaTrabajoArchi(CitaTrabajoArchi citaTrabajoArchi) {
        citaTrabajoArchi.setObservaciones(citaTrabajoArchi.getObservaciones().trim());

        databaseReference.child(CITA_LOCATION_ON_FIREBASE)
                .child(citaTrabajoArchi.getId())
                .setValue(citaTrabajoArchi)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Cita de trabajo actualizada");
                        } else {
                            Log.d(TAG, "Error al actualizar cita de trabajo");
                        }
                    }
                });
//        new updateWordAsyncTask(mWordDao).execute(word);
    }

    public void deleteAllCitasTrabajoArchi() {
//        new deleteAllWordsAsyncTask(mWordDao).execute();
    }

    // Must run off main thread
    public void deleteCitaTrabajoArchi(CitaTrabajoArchi word) {
//        new deleteWordAsyncTask(mWordDao).execute(word);

        databaseReference.child(CITA_LOCATION_ON_FIREBASE)
                .child(word.getId())
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Cita de trabajo eliminada");
                        } else {
                            Log.d(TAG, "Error al eliminar cita de trabajo");
                        }
                    }
                });
    }

    // Static inner classes below here to run database interactions in the background.
//
//    /**
//     * Inserts a word into the database.
//     */
//    private static class insertAsyncTask extends AsyncTask<CitaTrabajoArchi, Void, Void> {
//
////        private WordDao mAsyncTaskDao;
//
//        public insertAsyncTask(CitaTrabajoArchi dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(final CitaTrabajoArchi... params) {
//            mAsyncTaskDao.insert(params[0]);
//            return null;
//        }
//    }
//
//    /**
//     * Deletes all words from the database (does not delete the table).
//     */
//    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
//        private WordDao mAsyncTaskDao;
//
//        deleteAllWordsAsyncTask(WordDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mAsyncTaskDao.deleteAll();
//            return null;
//        }
//    }
//
//    /**
//     * Deletes a single word from the database.
//     */
//    private static class deleteWordAsyncTask extends AsyncTask<Word, Void, Void> {
//        private WordDao mAsyncTaskDao;
//
//        deleteWordAsyncTask(WordDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(final Word... params) {
//            mAsyncTaskDao.deleteWord(params[0]);
//            return null;
//        }
//    }
//
//    /**
//     * Updates a word in the database.
//     */
//    private static class updateWordAsyncTask extends AsyncTask<Word, Void, Void> {
//        private WordDao mAsyncTaskDao;
//
//        updateWordAsyncTask(WordDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(final Word... params) {
//            mAsyncTaskDao.update(params[0]);
//            return null;
//        }
//    }
}
