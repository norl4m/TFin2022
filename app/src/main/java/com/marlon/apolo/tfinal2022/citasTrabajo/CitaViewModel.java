package com.marlon.apolo.tfinal2022.citasTrabajo;

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
    // TODO: Implement the ViewModel
    // Create a LiveData with a String
    private MutableLiveData<String> currentName;
    private MutableLiveData<ArrayList<Chat>> chats;
    private ArrayList<Chat> chatsDBLocal;
    private ArrayList<Cita> citaArrayListFilter;
    private MutableLiveData<ArrayList<Cita>> citas;
    private ArrayList<Chat> chatArrayList;
    private ArrayList<Cita> citaArrayList;
    private MutableLiveData<ArrayList<Item>> items;
    ArrayList<Item> itemArrayList;


    public MutableLiveData<ArrayList<Cita>> getCitas() {
        if (citas == null) {
            citas = new MutableLiveData<>();
            chatsDBLocal = new ArrayList<>();
            citaArrayList = new ArrayList<>();
            citaArrayListFilter = new ArrayList<>();
            loadCitas();
        }
        return citas;
    }


    public void removeChildLister() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("citas")
                .removeEventListener(childEventListenerCita);

    }

    private void loadCitas() {
        citaArrayList = new ArrayList<>();
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("citas")
                .addChildEventListener(childEventListenerCita);
    }

    ChildEventListener childEventListenerCita = new ChildEventListener() {
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
//                for (String id : citaDB.getParticipants()) {
//                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        String fechaYHora = citaDB.getFechaCita();
//                        String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
//                        Locale locale = new Locale("es", "ES");
//                        SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
//                        Date date = formatFecha.parse(fechaYHora);
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(date);
//                        citaDB.setCalendaHoraCita(cal);
//                        citaArrayList.add(citaDB);
//                        break;
//                    }
//                }
                citas.setValue(citaArrayList);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, e.toString());
            }

//
//            citaArrayListFilter = new ArrayList<>();
//            try {
//                for (Cita c : citaArrayList) {
//                    for (String id : c.getParticipants()) {
//                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                            Log.d(TAG, c.toString());
//                            //c.setChatID();
//                            citaArrayListFilter.add(c);
//                            citas.setValue(citaArrayListFilter);
//                            break;
//                        }
//                    }
//                }
//
//            } catch (Exception e) {
//
//            }

            //citas.setValue(citaArrayList);


//            for (Cita cita:citaArrayList) {
            //              Log.e(TAG, cita.toString());

            //        }


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


//                for (String id : citaDB.getParticipants()) {
//                if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
////                    citaArrayList.add(citaDB);
//                    chatArrayList.set(finalIndex, chat);
//                    int index = chatArrayList.indexOf(citaDB);
//                    Log.e(TAG,String.valueOf(index));
//                    chats.setValue(chatArrayList);
//                    break;
//                }
//            }

//            int index = 0;
//            for (Chat chat : chatArrayList) {
//                if (chat.getIdChat().equals(commentKey)) {
//                    chat.setTimestamp(ct.getTimestamp());
//                    chat.setIdLastMessage(ct.getIdLastMessage());
//                    int finalIndex = index;
//                    databaseReference.child("mensajes").child(chat.getIdChat()).child(chat.getIdLastMessage()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            try {
//                                Log.e(TAG, "*************************************************");
//                                Mensaje mensaje = snapshot.getValue(Mensaje.class);
//                                Log.e(TAG, mensaje.toString());
//                                Log.e(TAG, "*************************************************\n");
//
//                                chat.setContent(mensaje.getContent());
//                                chatArrayList.set(finalIndex, chat);
//                                chats.setValue(chatArrayList);
////
////                                        if (mensaje.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
////                                            MainNavigationActivity.mainNavigationActivity.sendNotification();
////                                        }
//                            } catch (Exception exception) {
//                                Log.e(TAG, exception.getLocalizedMessage());
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
////                            chatArrayList.set(index, chat);
////                            chats.setValue(chatArrayList);
//                    break;
//                }
//                index++;
//            }

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

    public MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
        }
        return currentName;
    }

    public MutableLiveData<ArrayList<Chat>> getArrayListCitas() {
        chatArrayList = new ArrayList<>();
        if (chats == null) {
            chats = new MutableLiveData<>();
            loadChats();
        }
        return chats;
    }

    private void loadChats() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("chats")
                .addChildEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.i(TAG, "onChildAdded: " + snapshot.getKey());

            try {
                Chat chat = snapshot.getValue(Chat.class);
                for (Participante parti : chat.getParticipantes()) {
                    if (parti.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        //Log.d(TAG, chat.toString());
                        chatArrayList.add(chat);
                    }
                }
                chats.setValue(chatArrayList);
            } catch (Exception e) {

            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            try {
                Log.i(TAG, "onChildChanged: " + snapshot.getKey());
                Chat chat = snapshot.getValue(Chat.class);
                for (Participante parti : chat.getParticipantes()) {
                    if (parti.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Log.d(TAG, chat.toString());
                    }
                }
//                Log.d(TAG, chat.toString());
            } catch (Exception e) {

            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            try {
                Log.i(TAG, "onChildRemoved: " + snapshot.getKey());
                Chat chat = snapshot.getValue(Chat.class);
                Log.d(TAG, chat.toString());
            } catch (Exception e) {

            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            try {
                Log.i(TAG, "onChildMoved: " + snapshot.getKey());
                Chat chat = snapshot.getValue(Chat.class);
                Log.d(TAG, chat.toString());
            } catch (Exception e) {

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "citas:onCancelled", error.toException());
            Log.d(TAG, "Failed to load citas.: ");

        }
    };

    public MutableLiveData<ArrayList<Item>> getItems(String idCita) {
        if (items == null) {
            itemArrayList = new ArrayList<>();
            items = new MutableLiveData<>();
            loadItems(idCita);
        }

        return items;
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

    private ChildEventListener childEventListenerItem = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.d(TAG, "onChildAdded: " + snapshot.getKey());
            Item item = snapshot.getValue(Item.class);
            itemArrayList.add(item);
            items.setValue(itemArrayList);
//            for (DataSnapshot data:snapshot.getChildren()) {
//                Item item = data.getValue(Item.class);
//                itemArrayList.add(item);
//                items.setValue(itemArrayList);
//            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}