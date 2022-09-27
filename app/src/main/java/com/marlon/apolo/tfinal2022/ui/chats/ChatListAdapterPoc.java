package com.marlon.apolo.tfinal2022.ui.chats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPocData;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ChatListAdapterPoc extends RecyclerView.Adapter<ChatListAdapterPoc.ChatListAdapterViewHolder> {

    private Context context;
    private Activity activityInstance;
    private LayoutInflater inflater;
    private List<ChatPocData> chats;
    private Usuario usuarioFrom;
    private RecyclerView recyclerView;
    private SparseBooleanArray seleccionados;
    private boolean modoSeleccion;
    private ChatListAdapterPoc.ChatListAdapterViewHolder chatListAdapterViewHolderSelected;


    private String TAG;
    private Dialog dialogVar;
    private int selectedItemPosition;
    private int crazyPosition;

    public ChatListAdapterPoc(Context context) {
        selectedItemPosition = -1;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        seleccionados = new SparseBooleanArray();
        modoSeleccion = false;
    }

//    public Activity getActivityInstance() {
//        return activityInstance;
//    }

    public void setActivityInstance(Activity activityInstance) {
        this.activityInstance = activityInstance;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ChatListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_chat, parent, false);
        return new ChatListAdapterPoc.ChatListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapterViewHolder holder, int position) {
        ChatPocData current = chats.get(position);
        //Log.d(TAG,current.toString());

        if (current.getStateRemoteUser() != null)
            if (current.getStateRemoteUser().equals("eliminado"))
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.green_light));


        String contenido = current.getLastMessageCloudPoc().getContenido();
        holder.textViewContenido.setText(contenido);

        if (current.getLastMessageCloudPoc().getType() == 1) {
            contenido = new String(Character.toChars(0x1F4F7)) + " Foto";
            holder.textViewContenido.setText(contenido);
        }


        if (current.getLastMessageCloudPoc().getType() == 2) {
            contenido = new String(Character.toChars(0x1F3A7)) + " Audio";
            holder.textViewContenido.setText(contenido);
        }


        holder.textViewContacto.setText(String.format("%s", current.getName()));
        if (current.getFoto() != null) {
            Glide
                    .with(context)
                    .load(current.getFoto())
                    .circleCrop()
                    .apply(new RequestOptions().override(300, 400))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageViewContacto);
        } else {
            Glide
                    .with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24))
                    .circleCrop()
                    .apply(new RequestOptions().override(300, 400))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageViewContacto);
        }


        try {


            DataValidation dataValidation = new DataValidation();
            String sec = dataValidation.splitterData(current.getLastMessageCloudPoc().getTimeStamp(), "(seconds=", ",");
            String nansec = dataValidation.splitterData(current.getLastMessageCloudPoc().getTimeStamp(), ", nanoseconds=", ")");
            long seconds = Long.parseLong(sec);
            long nanoseconds = Integer.parseInt(nansec);
            Timestamp timestamp = new Timestamp(seconds, (int) nanoseconds);
            Date date = timestamp.toDate();
            holder.textViewDate.setText(String.format("%s", date.toLocaleString()));


        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }

        if (current.getLastMessageCloudPoc().isEstadoLectura()) {
            holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.teal_700));
        } else {
            holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.purple_700));
        }


//        if (!modoSeleccion) {
//            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoSeleccion) {
                    selectedItemPosition = position;
                    notifyDataSetChanged();
                    chatListAdapterViewHolderSelected = new ChatListAdapterPoc.ChatListAdapterViewHolder(holder.itemView);
                } else {
                    crazyPosition = position;
                    ChatPocData chat = chats.get(crazyPosition);
                    //Toast.makeText(context, chat.toString(), Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, IndividualChatActivity.class);
                    Intent intent = new Intent(context, CrazyIndividualChatActivity.class);
                    //intent.putExtra("chat", chat);
                    intent.putExtra("idRemoteUser", chat.getIdRemoteUser());
                    intent.putExtra("stateRemoteUser", chat.getStateRemoteUser());
                    context.startActivity(intent);
                }
            }
        });
//
        if (modoSeleccion) {
            if (selectedItemPosition == position) {
                holder.itemView.setBackgroundColor(Color.parseColor("#00FF00"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }

//        else
//            holder.itemView.setBackgroundColor(Color.parseColor("#E49B83"));


//        try {
////            holder.textViewDB.setText(String.valueOf(current.getIdMessage()));6
////            holder.imageViewContent.(String.valueOf(current.getIdMessageFirebase()));
//            // CONTENIDO = IMAGEN
//            if (current.getMensajeNube().getType() == 4) {
//                holder.textViewContenido.setText(context.getString(R.string.address_text, current.getMensajeNube().getContenido()));
//
//            }
//
////            holder.textViewTime.setText(String.valueOf(current.getCreateDate()));
//        } catch (Exception e) {
////            Log.e(TAG, e.toString());
//        }

    }

    @Override
    public int getItemCount() {
        if (chats != null)
            return chats.size();
        else return 0;
    }

    public List<ChatPocData> getChats() {
        return chats;
    }

    public void setChats(List<ChatPocData> chatsVar) {
        chats = chatsVar;
        notifyDataSetChanged();
    }


    public void addChat(ChatPocData chatPocVar) {
        if (chats == null) {
            chats = new ArrayList<>();
        }
        chats.add(chatPocVar);
        notifyDataSetChanged();
    }

    public void updateChat(int position, ChatPocData chatPocVar) {
        chats.set(position, chatPocVar);
        notifyDataSetChanged();
    }

    public void removeChat(int position) {
        chats.remove(position);
        notifyDataSetChanged();
    }

    public void setUsuarioFrom(Usuario usuarioFrom) {
        this.usuarioFrom = usuarioFrom;
    }


    public class ChatListAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewContacto;
        private final TextView textViewContenido;
        private final ImageView imageViewContacto;
        private final ImageView imageViewEstadoLectura;
        private final TextView textViewDate;


        public ChatListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContacto = itemView.findViewById(R.id.textViewNombreContact);
            textViewContenido = itemView.findViewById(R.id.textViewContent);
            imageViewContacto = itemView.findViewById(R.id.imageViewContact);
            imageViewEstadoLectura = itemView.findViewById(R.id.imageViewReadStatus);
            textViewDate = itemView.findViewById(R.id.textViewDate);


            //Selecciona el objeto si estaba seleccionado
//            if (seleccionados.get(getAbsoluteAdapterPosition())) {
//                itemView.setSelected(true);
//                itemView.setBackgroundColor(Color.parseColor("#00FF00"));
//            } else
//                itemView.setSelected(false);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (modoSeleccion) {
                        if (!v.isSelected()) {
                            v.setSelected(true);
                            v.setBackgroundColor(Color.parseColor("#00FF00"));
                            seleccionados.put(getAdapterPosition(), true);
                            chatListAdapterViewHolderSelected = new ChatListAdapterPoc.ChatListAdapterViewHolder(itemView);


                        } else {
                            v.setSelected(false);
                            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            seleccionados.put(getAdapterPosition(), false);
//                            if (!haySeleccionados())
//                                modoSeleccion = false;
                            chatListAdapterViewHolderSelected = new ChatListAdapterPoc.ChatListAdapterViewHolder(itemView);

                        }


                    } else {
                        //Log.d(TAG, String.valueOf(chats.size()));
                        //Log.d(TAG, String.valueOf(seleccionados.size()));
                        //Log.d(TAG, String.valueOf(seleccionados.size()));
                        //Log.d(TAG, String.valueOf(getAdapterPosition()));
//                        ChatPocData chat = chats.get(getAdapterPosition());
                        ChatPocData chat = chats.get(crazyPosition);
                        //Toast.makeText(context, chat.toString(), Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, IndividualChatActivity.class);
                        Intent intent = new Intent(context, CrazyIndividualChatActivity.class);
                        //intent.putExtra("chat", chat);
                        intent.putExtra("idRemoteUser", chat.getIdRemoteUser());
                        context.startActivity(intent);

                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (mActionMode != null) return false;
                    // Start the contextual action bar
                    // using the ActionMode.Callback.
                    mActionMode = activityInstance.startActionMode(mActionModeCallback);
                    modoSeleccion = true;
                    v.setSelected(true);
                    v.setBackgroundColor(Color.parseColor("#00FF00"));
                    chatListAdapterViewHolderSelected = new ChatListAdapterPoc.ChatListAdapterViewHolder(v);

                    seleccionados.put(getAbsoluteAdapterPosition(), true);
                    return true;
//                }
//                    return false;
                }
            });

        }
    }

    public boolean haySeleccionados() {
        for (int i = 0; i <= chats.size(); i++) {
            if (seleccionados.get(i))
                return true;
        }
        return false;
    }

    /**
     * Devuelve aquellos objetos marcados.
     */
    public LinkedList<ChatPocData> obtenerSeleccionados() {
        LinkedList<ChatPocData> marcados = new LinkedList<>();
        for (int i = 0; i < chats.size(); i++) {
            if (seleccionados.get(i)) {
                marcados.add(chats.get(i));
            }
        }
        return marcados;
    }

    private ActionMode mActionMode;


    public ActionMode.Callback mActionModeCallback = new
            ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate a menu resource providing context menu items
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.mnu_eliminar_chat:
                            Log.d(TAG, "Eliminando chat...");
                            LinkedList<ChatPocData> chatPocs = obtenerSeleccionados();
                            modoSeleccion = false;

                            if (chatPocs != null) {
                                Log.d(TAG, String.valueOf(chatPocs.size()));
                                for (ChatPocData cpd : chatPocs) {
                                    Log.d(TAG, cpd.toString());
                                    deleteChat(cpd.getIdRemoteUser());
                                }
                                if (chatPocs.size() == 0) {
                                    Log.d(TAG, "No existen elementos para eliminar");
                                    Toast.makeText(context, "No se ha seleccionado ningún elemento", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d(TAG, "No existen elementos para eliminar");
                            }
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }

//                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mActionMode = null;
                    modoSeleccion = false;
                    try {
                        chatListAdapterViewHolderSelected.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                    Log.d(TAG, "DESTROY CONTEX MENU");
//                    recyclerView.
                }

                // Add code to create action mode here.

            };

    private void deleteChat(String idRemoteUser) {

        FirebaseDatabase.getInstance().getReference()
                .child("crazyChats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idRemoteUser)
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Chat eliminado...", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("crazyMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idRemoteUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<MessageCloudPoc> messageCloudPocsMultimedia = new ArrayList<>();
                        for (DataSnapshot me : snapshot.getChildren()) {
                            MessageCloudPoc messageCloudPoc = me.getValue(MessageCloudPoc.class);
                            if (messageCloudPoc.getType() > 0) {
                                messageCloudPocsMultimedia.add(messageCloudPoc);
                            }
                        }

                        FirebaseDatabase.getInstance().getReference()
                                .child("crazyMessages")
                                .child(idRemoteUser)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Log.d(TAG, "Message remote: " + String.valueOf(snapshot.getChildrenCount()));
                                        if (snapshot.getChildrenCount() == 0) {
                                            cleanMultimediaMessageForward(idRemoteUser);
                                            cleanMultimediaMessageBackward(idRemoteUser);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("crazyMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idRemoteUser)
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Mensajes eliminados");
//                            FirebaseDatabase.getInstance().getReference()
//                                    .child("crazyMessages")
//                                    .child(idRemoteUser)
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            Log.d(TAG, "Message remote: " + String.valueOf(snapshot.getChildrenCount()));
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
                        } else {

                        }
                    }
                });
    }

    private void cleanMultimediaMessageForward(String idRemoteUser) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

//        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + idRemoteUser;
//        StorageReference listRef = storage.getReference().child("files/uid");
        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, prefix.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, item.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            String baseReference = item.toString();
                            Log.d(TAG, "Path reference on fireStorage");
                            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
// Delete the file
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d(TAG, "File delete");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "Error delete");
                                    Log.d(TAG, exception.toString());
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


//
//
//
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/7Om2riDJ5YQtB0sW8P5pFiBSQXs1";
////        StorageReference listRef = storage.getReference().child("files/uid");
//        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                            Log.d(TAG, prefix.toString());
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.
//                            Log.d(TAG, item.toString());
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//
//
//        Log.d(TAG, "cleanMultimediaMessage");
//        for (MessageCloudPoc me : messageCloudPocsMultimedia) {
//            Log.d(TAG, me.toString());
//            //6String baseReference = "gs://tfinal2022-afc91.appspot.com";
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/7Om2riDJ5YQtB0sW8P5pFiBSQXs1/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/-NBKEUq0Tit_XihDpPAY.mp3";
////            String baseReference = "https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/mensajes%2F7Om2riDJ5YQtB0sW8P5pFiBSQXs1%2F9JeHodaM0kOVyEuDWvAVyOiB2Qb2%2F-NBKJNguUWtJWJsNpu6Z.mp3?alt=media&token=e78d17d6-f362-4b33-8bc1-afc9d488f7c7";
////            String imagePath = baseReference + "/" + "mensajes" + "/" + me.getFrom() + "/" + me.getTo() + "/" + me.getIdMensaje();
////            String imagePath = me.getContenido();
//            String baseReference = me.getContenido();
//            Log.d(TAG, "Path reference on fireStorage");
//            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
//// Delete the file
//            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    // File deleted successfully
//                    Log.d(TAG, "File delete");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Uh-oh, an error occurred!
//                    Log.d(TAG, "Error delete");
//                    Log.d(TAG, exception.toString());
//                }
//            });
//        }

    }

    private void cleanMultimediaMessageBackward(String idRemoteUser) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

//        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + idRemoteUser;
        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/" + idRemoteUser + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
//        StorageReference listRef = storage.getReference().child("files/uid");
        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, prefix.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            Log.d(TAG, item.toString());
                            Log.w(TAG, "@@@@@@@@@@@@@@ ITEM @@@@@@@@@@@@@@@@@@@@@@");
                            String baseReference = item.toString();
                            Log.d(TAG, "Path reference on fireStorage");
                            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
// Delete the file
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d(TAG, "File delete");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "Error delete");
                                    Log.d(TAG, exception.toString());
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


//
//
//
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/";
//        String baseReferenceX = "gs://tfinal2022-afc91.appspot.com/mensajes/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/7Om2riDJ5YQtB0sW8P5pFiBSQXs1";
////        StorageReference listRef = storage.getReference().child("files/uid");
//        StorageReference listRef = firebaseStorage.getReferenceFromUrl(baseReferenceX);
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                            Log.d(TAG, prefix.toString());
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.
//                            Log.d(TAG, item.toString());
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//
//
//        Log.d(TAG, "cleanMultimediaMessage");
//        for (MessageCloudPoc me : messageCloudPocsMultimedia) {
//            Log.d(TAG, me.toString());
//            //6String baseReference = "gs://tfinal2022-afc91.appspot.com";
////        String baseReference = "gs://tfinal2022-afc91.appspot.com/mensajes/7Om2riDJ5YQtB0sW8P5pFiBSQXs1/9JeHodaM0kOVyEuDWvAVyOiB2Qb2/-NBKEUq0Tit_XihDpPAY.mp3";
////            String baseReference = "https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/mensajes%2F7Om2riDJ5YQtB0sW8P5pFiBSQXs1%2F9JeHodaM0kOVyEuDWvAVyOiB2Qb2%2F-NBKJNguUWtJWJsNpu6Z.mp3?alt=media&token=e78d17d6-f362-4b33-8bc1-afc9d488f7c7";
////            String imagePath = baseReference + "/" + "mensajes" + "/" + me.getFrom() + "/" + me.getTo() + "/" + me.getIdMensaje();
////            String imagePath = me.getContenido();
//            String baseReference = me.getContenido();
//            Log.d(TAG, "Path reference on fireStorage");
//            StorageReference storageRef = firebaseStorage.getReferenceFromUrl(baseReference);
//// Delete the file
//            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    // File deleted successfully
//                    Log.d(TAG, "File delete");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Uh-oh, an error occurred!
//                    Log.d(TAG, "Error delete");
//                    Log.d(TAG, exception.toString());
//                }
//            });
//        }

    }


    public void opcionesTrabajadorDialog(Trabajador trabajador) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);


        View promptsView = inflater.inflate(R.layout.alert_dialog_opciones_trabajadores, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);

        final ImageView imageButtonMessage = promptsView.findViewById(R.id.imageViewMessage);
        final ImageView imageButtonCall = promptsView.findViewById(R.id.imageViewCall);
        final ImageView imageButtonVideoCall = promptsView.findViewById(R.id.imageViewVideoCall);
        final ImageView imageButtonInfo = promptsView.findViewById(R.id.imageViewInfo);
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean mode = mPrefs.getBoolean("sync_theme", false);
        if (mode) {
            imageButtonMessage.setColorFilter(context.getResources().getColor(R.color.white));
            imageButtonCall.setColorFilter(context.getResources().getColor(R.color.white));
            imageButtonVideoCall.setColorFilter(context.getResources().getColor(R.color.white));
            imageButtonInfo.setColorFilter(context.getResources().getColor(R.color.white));
        } else {

        }

        final ImageView imageView = promptsView.findViewById(R.id.imageViewTrabajador);
        final TextView textView = promptsView.findViewById(R.id.textViewNombreUsuario);
//

        textView.setText(String.format("%s %s", trabajador.getNombre(), trabajador.getApellido()));
        if (trabajador.getFotoPerfil() != null) {

//            Bitmap bitmap = procesamientoDeImagen.stringToBitMap(trabajador.getFotoPerfil());
//            imageView.setImageBitmap(bitmap);


//            Glide
//                    .with(requireActivity())
//                    .load(trabajador.getFotoPerfil())
//                    .placeholder(R.drawable.ic_baseline_person_24)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .into(imageView);

            Glide
                    .with(context)
                    .load(trabajador.getFotoPerfil())
//                    .circleCrop() /*mala idea*/
                    .apply(new RequestOptions().override(300, 400))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);


        } else {
            imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
        //imageButtonImages.setOnClickListener(clickListenerDialogCustom);
        //imageButtonCall.setOnClickListener(clickListenerDialogCustom);
//        imageButtonAudio.setOnClickListener(clickListenerDialogCustom);


        imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, IndividualChatActivity.class);
                intent.putExtra("trabajador", trabajador);
                context.startActivity(intent);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });
        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Call", Toast.LENGTH_SHORT).show();
                Intent intentLlamadaVoz = new Intent(context, LlamadaVozActivity.class);
                intentLlamadaVoz.putExtra("trabajador", trabajador);
                context.startActivity(intentLlamadaVoz);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Video Call", Toast.LENGTH_SHORT).show();
                Intent intentVideoLlamada = new Intent(context, VideoLlamadaActivity.class);
                intentVideoLlamada.putExtra("trabajador", trabajador);
                context.startActivity(intentVideoLlamada);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

        imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Info", Toast.LENGTH_SHORT).show();
//                Intent intentVideoLlamada = new Intent(context, VideoLlamadaActivity.class);
//                context.startActivity(intentVideoLlamada);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });


//        return builder.create();
        dialogVar = builder.create();
        dialogVar.show();
    }


    public void alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
//
//        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(context.getResources().getString(R.string.text_select_trabjador_no_login));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialog.dismiss();
                } catch (Exception e) {

                }
            }
        });
        dialogVar = builder.create();
        dialogVar.show();
    }

}
