package com.marlon.apolo.tfinal2022.ui.oficios.adaptadores;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.ArrayList;

public class OficioRepositoryPoc {
    private MutableLiveData<ArrayList<Oficio>> oficios;
    private static final String TAG = OficioViewModelPoc.class.getSimpleName();
    private DatabaseReference mDatabase;
    private final static String OFICIOS_PATH = "oficios";

    public OficioRepositoryPoc() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(OFICIOS_PATH);
    }

    public MutableLiveData<ArrayList<Oficio>> getOficios() {
        if (oficios == null) {
            oficios = new MutableLiveData<>();
            loadOficios();
        }
        return oficios;
    }

    private void loadOficios() {
        // Do an asynchronous operation to fetch data.
        ArrayList<Oficio> oficiosDB = new ArrayList<Oficio>();

        Query myTopPostsQuery = mDatabase.orderByChild("nombre");
//        final Context mContext = this;
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Oficio oficio = dataSnapshot.getValue(Oficio.class);
                Log.d(TAG, oficio.toString());
                oficiosDB.add(oficio);
                oficios.setValue(oficiosDB);
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Oficio oficioChanged = dataSnapshot.getValue(Oficio.class);
                String commentKey = dataSnapshot.getKey();
                int index = 0;
                for (Oficio ofDB : oficiosDB) {
                    if (ofDB.getIdOficio().equals(oficioChanged.getIdOficio())) {
                        ofDB.setNombre(oficioChanged.getNombre());
                        ofDB.setUriPhoto(oficioChanged.getUriPhoto());
                        oficiosDB.set(index, ofDB);
                        break;
                    }
                    index++;
                }

                oficios.setValue(oficiosDB);
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                Oficio oficioRemoved = dataSnapshot.getValue(Oficio.class);
                int index = 0;
                for (Oficio ofDB : oficiosDB) {
                    if (ofDB.getIdOficio().equals(oficioRemoved.getIdOficio())) {
                        oficiosDB.remove(index);
                        break;
                    }
                    index++;
                }

                oficios.setValue(oficiosDB);
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Oficio movedComment = dataSnapshot.getValue(Oficio.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };
        myTopPostsQuery.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]
    }


}
