package com.marlon.apolo.tfinal2022.citasTrabajo.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Item;
import com.marlon.apolo.tfinal2022.model.Participante;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CitaViewModel extends ViewModel {
    private static final String TAG = CitaViewModel.class.getSimpleName();
    private MutableLiveData<ArrayList<Cita>> citas;
    private ArrayList<Cita> citaArrayList;
    private MutableLiveData<ArrayList<Item>> items;
    private ArrayList<Item> itemArrayList;
    private ChildEventListener childEventListenerCita = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            Log.i(TAG, "onChildAdded: " + snapshot.getKey());
            //for (Chat chat : chatsDBLocal) {
            //Log.i(TAG, "onChildAdded: " + chat.getIdChat());
            //if (snapshot.getKey().equals(chat.getIdChat())) {
            try {
                Cita citaDB = snapshot.getValue(Cita.class);
                //Log.e(TAG, citaDB.toString());
                Log.e(TAG, "|----------------------------------------------|");
                Log.e(TAG, "|------------------MATCHING--------------------|");
                Log.e(TAG, "|----------------------------------------------|");
//                            citaArrayList.add(citaDB);

                if (citaDB.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    String fechaYHora = citaDB.getFechaCita();
//                    String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
                    String patronFechaYHora = "dd MMMM yyyy HH:mm";

                    Locale locale = new Locale("es", "ES");
                    SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
                    Date date = formatFecha.parse(fechaYHora);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    citaDB.setCalendaHoraCita(cal);
                    citaArrayList.add(citaDB);

                }
                if (citaDB.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    String fechaYHora = citaDB.getFechaCita();
//                    String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
                    String patronFechaYHora = "dd MMMM yyyy HH:mm";

                    Locale locale = new Locale("es", "ES");
                    SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
                    Date date = formatFecha.parse(fechaYHora);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    citaDB.setCalendaHoraCita(cal);
                    citaArrayList.add(citaDB);

                }

                citas.setValue(citaArrayList);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, e.toString());
            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.e(TAG, "*************************************************");
            Log.i(TAG, "onChildChanged: " + snapshot.getKey());
            Log.e(TAG, "*************************************************\n");

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            //Trabajador tr = dataSnapshot.getValue(Trabajador.class);
            Cita citaDB = snapshot.getValue(Cita.class);
            String commentKey = snapshot.getKey();

            int index = 0;
            for (Cita citaLocal : citaArrayList) {
                if (citaLocal.getIdCita().equals(commentKey)) {
                    citaArrayList.set(index, citaDB);
                    citas.setValue(citaArrayList);
                    break;
                }
                index++;
            }


        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            Log.i(TAG, "onChildRemoved: " + snapshot.getKey());
            Cita citaDB = snapshot.getValue(Cita.class);
            String commentKey = snapshot.getKey();

            int index = 0;
            for (Cita citaLocal : citaArrayList) {
                if (citaLocal.getIdCita().equals(commentKey)) {
//                    citaArrayList.set(index, citaDB);
                    citaArrayList.remove(index);
                    citas.setValue(citaArrayList);
                    break;
                }
                index++;
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.i(TAG, "onChildMoved: " + snapshot.getKey());

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void loadCitas() {
        citaArrayList = new ArrayList<>();
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("citas")
                .addChildEventListener(childEventListenerCita);
    }

    private void loadItems(String idCita) {


        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("citaItems")
                .child(idCita)
//                .addChildEventListener(childEventListenerItem);
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            itemArrayList = new ArrayList<>();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Item item = data.getValue(Item.class);
                                itemArrayList.add(item);
                            }
                            items.setValue(itemArrayList);
                        } catch (Exception ex) {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public MutableLiveData<ArrayList<Cita>> getCitas() {
        if (citas == null) {
            citas = new MutableLiveData<>();
            citaArrayList = new ArrayList<>();
            loadCitas();
        }
        return citas;
    }

    public MutableLiveData<ArrayList<Item>> getItems(String idCita) {
        if (items == null) {
            itemArrayList = new ArrayList<>();
            items = new MutableLiveData<>();
            loadItems(idCita);
        }

        return items;
    }



}