package com.marlon.apolo.tfinal2022.citasTrabajo.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.NuevaCitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPocData;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SeleccionarEmpleadorListAdapterPoc extends RecyclerView.Adapter<SeleccionarEmpleadorListAdapterPoc.ChatListAdapterViewHolder> {

    private Context context;
    private Activity activityInstance;
    private LayoutInflater inflater;
    private List<ChatPocData> chats;
    private List<Empleador> empleadors;
    private Usuario usuarioFrom;
    private RecyclerView recyclerView;
    private SparseBooleanArray seleccionados;
    private boolean modoSeleccion;
    private SeleccionarEmpleadorListAdapterPoc.ChatListAdapterViewHolder chatListAdapterViewHolderSelected;


    private String TAG;
    private Dialog dialogVar;
    private int selectedItemPosition;
    private int crazyPosition;

    public SeleccionarEmpleadorListAdapterPoc(Context context) {
        selectedItemPosition = -1;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        seleccionados = new SparseBooleanArray();
        modoSeleccion = false;
    }

//    public Activity getActivityInstance() {
//        return activityInstance;
//    }


    public List<Empleador> getEmpleadors() {
        return empleadors;
    }

    public void setEmpleadors(List<Empleador> empleadors) {
        this.empleadors = empleadors;
    }

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
        View itemView = inflater.inflate(R.layout.card_view_presentacion_empleadort_cita, parent, false);
        return new SeleccionarEmpleadorListAdapterPoc.ChatListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapterViewHolder holder, int position) {
        ChatPocData current = chats.get(position);
        //Log.d(TAG,current.toString());

        if (current.getStateRemoteUser() != null)
            if (current.getStateRemoteUser().equals("eliminado"))
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.green_light));


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


//        if (!modoSeleccion) {
//            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (modoSeleccion) {
//                    selectedItemPosition = position;
//                    notifyDataSetChanged();
//                    chatListAdapterViewHolderSelected = new SeleccionarTrabajdorListAdapterPoc.ChatListAdapterViewHolder(holder.itemView);
//                } else {
//                    crazyPosition = position;
//                    ChatPocData chat = chats.get(crazyPosition);
//                    //Toast.makeText(context, chat.toString(), Toast.LENGTH_LONG).show();
////                    Intent intent = new Intent(context, IndividualChatActivity.class);
//                    Intent intent = new Intent(context, CrazyIndividualChatActivity.class);
//                    //intent.putExtra("chat", chat);
//                    intent.putExtra("idRemoteUser", chat.getIdRemoteUser());
//                    intent.putExtra("stateRemoteUser", chat.getStateRemoteUser());
//                    context.startActivity(intent);
//                }
//            }
//        });
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
        private final ImageView imageViewContacto;


        public ChatListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContacto = itemView.findViewById(R.id.textViewNombreContact);
            imageViewContacto = itemView.findViewById(R.id.imageViewContact);


            //Selecciona el objeto si estaba seleccionado
//            if (seleccionados.get(getAbsoluteAdapterPosition())) {
//                itemView.setSelected(true);
//                itemView.setBackgroundColor(Color.parseColor("#00FF00"));
//            } else
//                itemView.setSelected(false);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ChatPocData chat = chats.get(getAdapterPosition());
//                    //Toast.makeText(context, chat.toString(), Toast.LENGTH_LONG).show();
////                    Intent intent = new Intent(context, IndividualChatActivity.class);
//                    Intent intent = new Intent(context, CrazyIndividualChatActivity.class);
//                    //intent.putExtra("chat", chat);
//                    intent.putExtra("idRemoteUser", chat.getIdRemoteUser());
//                    context.startActivity(intent);

                    Empleador empleadorSelect = new Empleador();
                    for (Empleador e : empleadors) {
                        if (e.getIdUsuario().equals(chat.getIdRemoteUser())) {
                            empleadorSelect = e;
                            break;
                        }
                    }


//                    Toast.makeText(context, empleadorSelect.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, NuevaCitaTrabajoActivity.class);
                    intent.putExtra("usuarioFrom", usuarioFrom);
                    intent.putExtra("usuarioTo", empleadorSelect);
                    context.startActivity(intent);


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
