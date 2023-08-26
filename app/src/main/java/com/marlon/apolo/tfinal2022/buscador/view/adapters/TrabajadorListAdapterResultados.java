package com.marlon.apolo.tfinal2022.buscador.view.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.AgoraVoiceCallActivityPoc;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.VoiceCallMainActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.oficios.adapters.OficioTrabajadorVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.view.PerfilTrabajadorActivity;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorListAdapterResultados extends RecyclerView.Adapter<TrabajadorListAdapterResultados.TrabajadorViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Trabajador> trabajadors;
    private List<Oficio> oficioList;
    private List<Chat> chatList;
    private List<Cita> citaList;
    private Dialog dialogVar;
    private Usuario usuarioFrom;

    public TrabajadorListAdapterResultados(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
//        trabajadorsAux = new ArrayList<>();

    }

    public List<Trabajador> getTrabajadors() {
        return trabajadors;
    }

    public void setTrabajadores(List<Trabajador> trabajadorsVar) {
        trabajadors = trabajadorsVar;
        notifyDataSetChanged();
    }

    public void setUsuarioFrom(Usuario usuarioFrom) {
        this.usuarioFrom = usuarioFrom;
    }

    public void setCitaList(List<Cita> citaList) {
        this.citaList = citaList;
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
    }

    public void opcionesTrabajadorDialog(Trabajador trabajador) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);


        View promptsView = inflater.inflate(R.layout.alert_dialog_opciones_trabajadores, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);

        final ImageView imageButtonMessage = promptsView.findViewById(R.id.imageViewMessage);
        final ImageView imageButtonCall = promptsView.findViewById(R.id.imageViewCall);
        final ImageView imageButtonVideoCall = promptsView.findViewById(R.id.imageViewVideoCall);
        final ImageView imageButtonInfo = promptsView.findViewById(R.id.imageViewInfo);

        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        imageButtonMessage.setColorFilter(colorPrimary);
        imageButtonCall.setColorFilter(colorPrimary);
        imageButtonVideoCall.setColorFilter(colorPrimary);
        imageButtonInfo.setColorFilter(colorPrimary);
        /*Esto es una maravilla*/

        final ImageView imageView = promptsView.findViewById(R.id.imageViewTrabajador);
        final TextView textView = promptsView.findViewById(R.id.textViewNombreUsuario);
//

        textView.setText(String.format("%s %s", trabajador.getNombre(), trabajador.getApellido()));


        if (trabajador.getFotoPerfil() != null) {

            imageView.setColorFilter(null);
            Glide
                    .with(context)
                    .load(trabajador.getFotoPerfil())
//                    .circleCrop() /*mala idea*/
                    .apply(new RequestOptions().override(300, 400))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);


        } else {
            imageView.setImageResource(R.drawable.ic_user_tra_emp);
            imageView.setColorFilter(colorPrimary);

        }


        imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, IndividualChatActivity.class);
                Intent intent = new Intent(context, CrazyIndividualChatActivity.class);
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


//                Intent intentllamadaVoz = new Intent(context, AgoraOnlyVoiceCallActivity.class);
                Intent intentllamadaVoz = new Intent(context, VoiceCallMainActivity.class);
                intentllamadaVoz.putExtra("usuarioRemoto", (Usuario) trabajador);
                intentllamadaVoz.putExtra("usuarioLocal", usuarioFrom);
                String channelName = FirebaseDatabase.getInstance().getReference().child("voiceCalls").push().getKey();
                intentllamadaVoz.putExtra("channelName", channelName);
                intentllamadaVoz.putExtra("callStatus", "llamadaSaliente");

                context.startActivity(intentllamadaVoz);


                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Intent intentVideollamada = new Intent(context, AgoraVideoCallActivity.class);
                Intent intentVideollamada = new Intent(context, VideoCallMainActivity.class);
                intentVideollamada.putExtra("usuarioRemoto", (Usuario) trabajador);
                intentVideollamada.putExtra("usuarioLocal", (Usuario) usuarioFrom);
                String channelName = FirebaseDatabase.getInstance().getReference().child("videoCalls")
                        .push().getKey();
                intentVideollamada.putExtra("channelName", channelName);
                intentVideollamada.putExtra("callStatus", "llamadaSaliente");

//                        intentVideollamada.putExtra("callStatus", 0);
                context.startActivity(intentVideollamada);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Info", Toast.LENGTH_SHORT).show();
                Intent intentPerfilData = new Intent(context, PerfilTrabajadorActivity.class);
                intentPerfilData.putExtra("idTrabajador", trabajador.getIdUsuario());
                int exitFlag = 0;
                if (chatList != null) {
                    for (Chat chat : chatList) {
                        exitFlag = 0;
                        for (Participante p : chat.getParticipantes()) {
                            if (p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                exitFlag++;
                            }
                            if (p.getIdParticipante().equals(trabajador.getIdUsuario())) {
                                exitFlag++;
                            }
                            if (exitFlag == 2) {
                                break;
                            }
                        }
                        if (exitFlag == 2) {
                            //Toast.makeText(context, chat.toString(), Toast.LENGTH_SHORT).show();
                            intentPerfilData.putExtra("chat", chat);
                            break;
                        }
                    }
                }
                context.startActivity(intentPerfilData);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });


//        return builder.create();
        dialogVar = builder.create();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 180);
        dialogVar.getWindow().setBackgroundDrawable(inset);
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

    public void setOficioList(List<Oficio> oficioList) {
        this.oficioList = oficioList;
    }

    @NonNull
    @Override
    public TrabajadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_trabajador, parent, false);
        return new TrabajadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrabajadorViewHolder holder, int position) {
        Trabajador current = trabajadors.get(position);

        holder.textViewComplete.setVisibility(View.GONE);
        holder.textViewInComplete.setVisibility(View.GONE);
        holder.textViewNoAssit.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
            int usuario = myPreferences.getInt("usuario", -1);

            switch (usuario) {
                case 0:/*admin*/
                case 1:/*empleador*/
                    holder.textViewComplete.setVisibility(View.VISIBLE);
                    holder.textViewInComplete.setVisibility(View.VISIBLE);
                    holder.textViewNoAssit.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    break;
            }
        } else {
            holder.textViewComplete.setVisibility(View.GONE);
            holder.textViewInComplete.setVisibility(View.GONE);
            holder.textViewNoAssit.setVisibility(View.GONE);

        }

        holder.textViewNombre.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
        if (current.getFotoPerfil() != null) {
            holder.imageViewTrabajador.setColorFilter(null);

            Glide.with(context).load(current.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24_color_app).circleCrop().into(holder.imageViewTrabajador);
        } else {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_user_tra_emp)).placeholder(R.drawable.ic_user_tra_emp).into(holder.imageViewTrabajador);
            holder.imageViewTrabajador.setColorFilter(colorPrimary);
        }
//        Log.d(TAG, current.toString());


        ArrayList<Oficio> oficiosFiltrados = new ArrayList<>();
        for (Oficio o : oficioList) {
            if (current.getIdOficios().contains(o.getIdOficio())) {

                oficiosFiltrados.add(o);
            }
        }


        OficioTrabajadorVistaListAdapter oficioTrabajadorVistaListAdapter = new OficioTrabajadorVistaListAdapter(context);
        holder.recyclerViewOficios.setAdapter(oficioTrabajadorVistaListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.recyclerViewOficios.setLayoutManager(layoutManager);
        oficioTrabajadorVistaListAdapter.setOficios(oficiosFiltrados);


        if (current.getCalificacion() > 0.0) {
            holder.textViewCalif.setText(String.format("Calificación: %.1f " + "/ 5.0", current.getCalificacion()));
            holder.ratingBar.setRating((float) current.getCalificacion());
            holder.ratingBar.setVisibility(View.VISIBLE);


        } else {
            holder.textViewCalif.setText(String.format("%s %s %s", current.getNombre(), current.getApellido(), context.getString(R.string.text_no_trabajo)));
//            holder.ratingBar.setRating((float) current.getCalificacion());
            holder.ratingBar.setVisibility(View.GONE);
        }

        if (current.getEmail() != null) {
            holder.textViewContacto.setText(current.getEmail());
        }


        try {

            ArrayList<Cita> citaArrayListNoAsist = new ArrayList<>();
            ArrayList<Cita> citaArrayListIncomple = new ArrayList<>();
            ArrayList<Cita> citaArrayList = new ArrayList<>();
            for (Cita data : citaList) {
                Cita citaDB = data;
//                Log.d(TAG, citaDB.toString());
                if (citaDB.getFrom().equals(current.getIdUsuario())) {


                    if (citaDB.isState()) {
                        citaArrayList.add(citaDB);
                    }

                    try {

                        switch (citaDB.getObservaciones()) {
                            case "Trabajador no asistió":
                                citaArrayListNoAsist.add(citaDB);
                                break;
                            case "Trabajador incumplido":
                                citaArrayListIncomple.add(citaDB);
                                break;
                            case "Ninguna":
                            default:
//                            mnuFin.setVisible(true);
//                            mnuFin.setVisible(!citaLocal.isState());

                                //mnuEliminarCita.setVisible(true);
                                //editCita.setVisible(true);
                                break;
                        }
                    } catch (Exception e) {
                        //mnuFin.setVisible(true);
//                        Log.d(TAG, e.toString());
                    }
                }
            }

            holder.textViewComplete.setText("Trabajos completados: " + String.valueOf(citaArrayList.size()));
            holder.textViewInComplete.setText("Trabajos incompletos: " + String.valueOf(citaArrayListIncomple.size()));
            holder.textViewNoAssit.setText("Trabajos no asistidos: " + String.valueOf(citaArrayListNoAsist.size()));

        } catch (Exception e) {
//            Log.d(TAG, e.toString());
            holder.textViewComplete.setText("Trabajos completados: " + "0");
            holder.textViewInComplete.setText("Trabajos incompletos: " + "0");
            holder.textViewNoAssit.setText("Trabajos no asistidos: " + "0");

        }

    }

    @Override
    public int getItemCount() {
        if (trabajadors != null)
            return trabajadors.size();
        else return 0;
    }


    public class TrabajadorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewCalif;
        private final RecyclerView recyclerViewOficios;
        private final ImageView imageViewTrabajador;
        private RatingBar ratingBar;
        private final TextView textViewContacto;
        private final TextView textViewComplete;
        private final TextView textViewInComplete;
        private final TextView textViewNoAssit;

        public TrabajadorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCalif = itemView.findViewById(R.id.textViewCalificacion);
            recyclerViewOficios = itemView.findViewById(R.id.recyclerViewOficios);
            imageViewTrabajador = itemView.findViewById(R.id.imageViewTrabajador);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);

            textViewComplete = itemView.findViewById(R.id.textViewTrabComple);
            textViewInComplete = itemView.findViewById(R.id.textViewTrabIncom);
            textViewNoAssit = itemView.findViewById(R.id.textViewNoAsist);
//            imageViewTrabajador.setOnClickListener(new View.OnClickListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
                        int usuario = myPreferences.getInt("usuario", -1);

                        switch (usuario) {
                            case 0:/*admin*/
                            case 1:/*empleador*/
                                opcionesTrabajadorDialog(trabajadors.get(getAdapterPosition()));
                                break;
                            case 2:
                                break;
                        }
                    } else {
                        alertDialogInfo();
                    }
                    //opcionesTrabajadorDialog(trabajadors.get(getAdapterPosition()));

                }
            });


        }
    }


}

