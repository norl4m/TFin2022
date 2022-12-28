package com.marlon.apolo.tfinal2022.buscador.adaptadores;

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
import com.marlon.apolo.tfinal2022.comunnication.video.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.comunnication.voice.AgoraOnlyVoiceCallActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioTrabajadorVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.PerfilTrabajadorActivity;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorListAdapterResultados extends RecyclerView.Adapter<TrabajadorListAdapterResultados.TrabajadorViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Trabajador> trabajadors;
    //    private List<Trabajador> trabajadorsAux;
    private List<Oficio> oficioList;
    private List<Chat> chatList;
    private List<Cita> citaList;

    private String TAG;
    private Dialog dialogVar;

    public void setUsuarioFrom(Usuario usuarioFrom) {
        this.usuarioFrom = usuarioFrom;
    }

    private Usuario usuarioFrom;

    public TrabajadorListAdapterResultados(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
//        trabajadorsAux = new ArrayList<>();

    }

    public TrabajadorListAdapterResultados(Context context, ArrayList<Oficio> oficioArrayList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        oficioList = oficioArrayList;
    }


    public List<Cita> getCitaList() {
        return citaList;
    }

    public void setCitaList(List<Cita> citaList) {
        this.citaList = citaList;
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

            Glide.with(context).load(current.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewTrabajador);
        } else {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_usuario)).placeholder(R.drawable.ic_usuario).into(holder.imageViewTrabajador);
            holder.imageViewTrabajador.setColorFilter(colorPrimary);
        }
        Log.d(TAG, current.toString());


        ArrayList<Oficio> oficiosFiltrados = new ArrayList<>();
        for (Oficio o : oficioList) {
            if (current.getIdOficios().contains(o.getIdOficio())) {
//                ArrayList<Habilidad> habilidadsFiltradas = new ArrayList<>();
//                ArrayList<Habilidad> habilidads = new ArrayList<>();
//                habilidads = o.getHabilidadArrayList();
//                o.setHabilidadArrayList(new ArrayList<>());
//                try {
//                    for (Habilidad h : habilidads) {
//                        if (current.getIdHabilidades().contains(h.getIdHabilidad())) {
//                            habilidadsFiltradas.add(h);
//                        }
//                    }
//                    o.setHabilidadArrayList(habilidadsFiltradas);
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
                oficiosFiltrados.add(o);
            }
        }


        OficioTrabajadorVistaListAdapter oficioTrabajadorVistaListAdapter = new OficioTrabajadorVistaListAdapter(context);
        holder.recyclerViewOficios.setAdapter(oficioTrabajadorVistaListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.recyclerViewOficios.setLayoutManager(layoutManager);
        oficioTrabajadorVistaListAdapter.setOficios(oficiosFiltrados);
//        oficioTrabajadorVistaListAdapter.setOficios(oficioList);
//        oficioViewModel.getAllOficios().observe((LifecycleOwner) context, oficioRegistroListAdapter::setOficios);

//        holder.textViewCalif.setText(String.format("Calificación: %.2f " + "/ 5.00", current.getCalificacion()));
//        holder.textViewCalif.setText(String.format("Calificación: %.1f " + "/ 5.0", current.getCalificacion()));
//        holder.ratingBar.setRating((float) current.getCalificacion());

        if (current.getCalificacion() > 0.0) {
            holder.textViewCalif.setText(String.format("Calificación: %.1f " + "/ 5.0", current.getCalificacion()));
            holder.ratingBar.setRating((float) current.getCalificacion());
            holder.ratingBar.setVisibility(View.VISIBLE);


        } else {
            holder.textViewCalif.setText(context.getString(R.string.text_no_trabajo));
//            holder.ratingBar.setRating((float) current.getCalificacion());
            holder.ratingBar.setVisibility(View.GONE);
        }

        if (current.getEmail() != null) {
            holder.textViewContacto.setText(current.getEmail());
        }
        if (current.getCelular() != null) {
            holder.textViewContacto.setText(current.getCelular());
        }


        try {

            ArrayList<Cita> citaArrayListNoAsist = new ArrayList<>();
            ArrayList<Cita> citaArrayListIncomple = new ArrayList<>();
            ArrayList<Cita> citaArrayList = new ArrayList<>();
            for (Cita data : citaList) {
                Cita citaDB = data;
                Log.d(TAG, citaDB.toString());
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
                        Log.d(TAG, e.toString());
                    }
                }
            }

            holder.textViewComplete.setText("Trabajos completados: " + String.valueOf(citaArrayList.size()));
            holder.textViewInComplete.setText("Trabajos incompletos: " + String.valueOf(citaArrayListIncomple.size()));
            holder.textViewNoAssit.setText("Trabajos no asistidos: " + String.valueOf(citaArrayListNoAsist.size()));

        } catch (Exception e) {
            Log.d(TAG, e.toString());
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

    public List<Trabajador> getTrabajadors() {
        return trabajadors;
    }

    public void setTrabajadores(List<Trabajador> trabajadorsVar) {
        trabajadors = trabajadorsVar;
        notifyDataSetChanged();
    }

//    public void filtrado(final String txtBuscar) {
////        trabajadorsAux = new ArrayList<>();
//
//        int longitud = txtBuscar.length();
//        if (longitud == 0) {
//            trabajadorsAux.clear();
//            trabajadorsAux.addAll(trabajadors);
//        } else {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                List<Trabajador> collecion = trabajadors.stream()
//                        .filter(i -> (i.getNombre() + " " + i.getApellido()).toLowerCase().contains(txtBuscar.toLowerCase()))
//                        .collect(Collectors.toList());
//                trabajadorsAux.clear();
//                trabajadorsAux.addAll(collecion);
//            } else {
//                for (Trabajador c : trabajadors) {
//                    if ((c.getNombre() + " " + c.getApellido()).toLowerCase().contains(txtBuscar.toLowerCase())) {
//                        trabajadorsAux.add(c);
//                    }
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    public void filtradoByOficio(final String txtBuscar) {
////        trabajadorsAux = new ArrayList<>();
//
//        String idFound = "";
//        ArrayList<String> ofids = new ArrayList<>();
//        for (Oficio o : oficioList) {
//            if (o.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
////                idFound = o.getIdOficio();
//                ofids.add(o.getIdOficio());
//            }
//        }
//        int longitud = txtBuscar.length();
//        if (longitud == 0) {
//            trabajadorsAux.clear();
//            trabajadorsAux.addAll(trabajadors);
//        } else {
//            trabajadorsAux.clear();
//            for (Trabajador tr : trabajadors) {
//                for (String idof : tr.getIdOficios()) {
//                    for (String ofiIdLocal : ofids) {
//                        if (idof.equals(ofiIdLocal)) {
//                            trabajadorsAux.add(tr);
//                            break;
//                        }
//                    }
//
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }


    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
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
                                opcionesTrabajadorDialog(trabajadors.get(getAbsoluteAdapterPosition()));
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
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean mode = mPrefs.getBoolean("sync_theme", false);
//        if (mode) {
//            imageButtonMessage.setColorFilter(context.getResources().getColor(R.color.white));
//            imageButtonCall.setColorFilter(context.getResources().getColor(R.color.white));
//            imageButtonVideoCall.setColorFilter(context.getResources().getColor(R.color.white));
//            imageButtonInfo.setColorFilter(context.getResources().getColor(R.color.white));
//        } else {
//
//        }


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
                    .placeholder(R.drawable.ic_usuario)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);


        } else {
            imageView.setImageResource(R.drawable.ic_usuario);
            imageView.setColorFilter(colorPrimary);

        }
        //imageButtonImages.setOnClickListener(clickListenerDialogCustom);
        //imageButtonCall.setOnClickListener(clickListenerDialogCustom);
//        imageButtonAudio.setOnClickListener(clickListenerDialogCustom);


        imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, IndividualChatActivity.class);
                Intent intent = new Intent(context, CrazyIndividualChatActivity.class);
                intent.putExtra("trabajador", trabajador);
                //intent.putExtra("usuarioFrom", usuarioFrom);
                //Toast.makeText(context, usuarioFrom.toString(), Toast.LENGTH_SHORT).show();
//
//                int exitFlag = 0;
//                if (chatList != null) {
//                    for (Chat chat : chatList) {
//                        exitFlag = 0;
//                        for (Participante p : chat.getParticipantes()) {
//                            if (p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                                exitFlag++;
//                            }
//                            if (p.getIdParticipante().equals(trabajador.getIdUsuario())) {
//                                exitFlag++;
//                            }
//                            if (exitFlag == 2) {
//                                break;
//                            }
//                        }
//                        if (exitFlag == 2) {
//                            //Toast.makeText(context, chat.toString(), Toast.LENGTH_SHORT).show();
//                            intent.putExtra("chat", chat);
//                            break;
//                        }
//                    }
//                }

                //intent.putExtra("trabajador", trabajador);

                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

//                Intent intentllamadaVoz = new Intent(context, LlamadaVozActivity.class);
//                intentllamadaVoz.putExtra("usuarioTo", (Usuario) trabajador);
//                intentllamadaVoz.putExtra("usuarioFrom", (Usuario) usuarioFrom);
//                intentllamadaVoz.putExtra("callStatus", 0);
//                context.startActivity(intentllamadaVoz);
//
//


                Intent intentllamadaVoz = new Intent(context, AgoraOnlyVoiceCallActivity.class);
                intentllamadaVoz.putExtra("usuarioRemoto", (Usuario) trabajador);
                intentllamadaVoz.putExtra("usuarioLocal", usuarioFrom);
                String channelName = FirebaseDatabase.getInstance().getReference().child("voiceCalls").push().getKey();
                intentllamadaVoz.putExtra("channelName", channelName);
                intentllamadaVoz.putExtra("callStatus", "llamadaSaliente");

                context.startActivity(intentllamadaVoz);


//
//                Intent intent = new Intent(context, LlamadaVozActivity.class);
//                intent.putExtra("usuarioTo", (Usuario) trabajador);
////                intent.putExtra("usuarioFrom", usuarioLocal);
//                intent.putExtra("callStatus", 0
//                );
//                context.startActivity(intent);
//
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
//                Intent intentVideollamada = new Intent(context, VideoLlamadaActivity.class);
//                intentVideollamada.putExtra("usuarioTo", (Usuario) trabajador);
//                intentVideollamada.putExtra("usuarioFrom", (Usuario) usuarioFrom);
//                intentVideollamada.putExtra("callStatus", 0);
//                context.startActivity(intentVideollamada);


                Intent intentVideollamada = new Intent(context, AgoraVideoCallActivity.class);
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

//    public void setTrabajadors(List<Trabajador> trabajadors) {
////        this.trabajadors = trabajadors;
//        this.trabajadorsAux = trabajadors;
//    }
}

