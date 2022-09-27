package com.marlon.apolo.tfinal2022.ui.chats;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListAdapterViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Chat> chats;
    private Usuario usuarioFrom;

    private String TAG;
    private Dialog dialogVar;

    public ChatListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ChatListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_chat, parent, false);
        return new ChatListAdapter.ChatListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapterViewHolder holder, int position) {
        Chat current = chats.get(position);
        holder.textViewContenido.setText(current.getMensajeNube().getContenido());
//        holder.textViewContacto.setText(current.getMensajeNube().getContenido());
//        for (Participante part : current.getParticipantes()) {
//            if (!part.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                holder.textViewContacto.setText(String.format("%s",part.getNombreParticipante()));
//
//                break;
//            }
//        }
        for (Participante p : current.getParticipantes()) {
            try {
                if (!p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.textViewContacto.setText(String.format("%s", p.getNombreParticipante()));
                    if (p.getUriFotoParticipante() != null) {
                        Glide
                                .with(context)
                                .load(p.getUriFotoParticipante())
                                .circleCrop()
                                .apply(new RequestOptions().override(300, 400))
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.imageViewContacto);
                    }
                    break;
                }
            } catch (Exception e) {

            }

        }
        try {

//            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
//            Locale locale = new Locale("es", "ES");
//
//
//            SimpleDateFormat formatFecha = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
//                String date = formatFecha.format(current.getMensajeNube().getTimeStamp());
//                holder.textViewDate.setText(date);
//            }


            DataValidation dataValidation = new DataValidation();
            String sec = dataValidation.splitterData(current.getMensajeNube().getTimeStamp(), "(seconds=", ",");
            String nansec = dataValidation.splitterData(current.getMensajeNube().getTimeStamp(), ", nanoseconds=", ")");
            long seconds = Long.parseLong(sec);
            long nanoseconds = Integer.parseInt(nansec);
            Timestamp timestamp = new Timestamp(seconds, (int) nanoseconds);
            Date date = timestamp.toDate();
            holder.textViewDate.setText(String.format("%s", date.toLocaleString()));


        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }

        if (current.getMensajeNube().isEstadoLectura()) {
            holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.teal_700));
        } else {
            holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.purple_700));
        }


        try {
//            holder.textViewDB.setText(String.valueOf(current.getIdMessage()));6
//            holder.imageViewContent.(String.valueOf(current.getIdMessageFirebase()));
            // CONTENIDO = IMAGEN
            if (current.getMensajeNube().getType() == 4) {
                holder.textViewContenido.setText(context.getString(R.string.address_text, current.getMensajeNube().getContenido()));

                //String path = "content://com.miui.gallery.open/raw/%2Fstorage%2Femulated%2F0%2FDCIM%2FCamera%2FIMG_20220130_213803.jpg";

//                String[] parts = current.getMensajeNube().getContenido().split(",");
//                String part1 = parts[0]; // 123
//                String part2 = parts[1]; // 654321
////
//                double latitude = Double.parseDouble(part1.substring(part1.indexOf(":") + 1));
//                double longitude = Double.parseDouble(part2.substring(part2.indexOf(":") + 1));
//
//                Log.d(TAG, String.valueOf(latitude));
//                Log.d(TAG, String.valueOf(longitude));
//
//                List<Address> addresses = null;
//                String resultMessage = "";
//                Geocoder geocoder = new Geocoder(context,
//                        Locale.getDefault());
////
//
//                try {
//                    addresses = geocoder.getFromLocation(
//                            latitude,
//                            longitude,
//                            // In this sample, get just a single address
//                            1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Address address = addresses.get(0);
//                ArrayList<String> addressParts = new ArrayList<>();
//
//                // Fetch the address lines using getAddressLine,
//                // join them, and send them to the thread
//                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
//                    addressParts.add(address.getAddressLine(i));
//                }
//
//                resultMessage = TextUtils.join(
//                        "\n",
//                        addressParts);
////
////                holder.wordItemView.setText(resultMessage);
//                holder.textViewContenido.setText(context.getString(R.string.address_text, resultMessage));

//                holder.wordItemView.setText(String.format("%s", context.getString(R.string.address_text), resultMessage));


            }

//            holder.textViewTime.setText(String.valueOf(current.getCreateDate()));
        } catch (Exception e) {
//            Log.e(TAG, e.toString());
        }

    }

    @Override
    public int getItemCount() {
        if (chats != null)
            return chats.size();
        else return 0;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chatsVar) {
        chats = chatsVar;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Chat chat = chats.get(getAdapterPosition());
                    //Toast.makeText(context, chat.toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, IndividualChatActivity.class);
                    intent.putExtra("chat", chat);
                    //intent.putExtra("usuarioFrom", usuarioFrom);
                    context.startActivity(intent);
                }
            });

        }
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
