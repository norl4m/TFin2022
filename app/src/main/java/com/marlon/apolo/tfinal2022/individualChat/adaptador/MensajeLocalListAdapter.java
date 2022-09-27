package com.marlon.apolo.tfinal2022.individualChat.adaptador;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.model.Mensajito;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MensajeLocalListAdapter extends RecyclerView.Adapter<MensajeLocalListAdapter.MensajeNubeViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_AUDIO_LEFT = 10;
    private static final int MSG_TYPE_AUDIO_RIGHT = 11;
    private static final int MSG_TYPE_IMAGE_LEFT = 20;
    private static final int MSG_TYPE_IMAGE_RIGHT = 21;
    private static final int MSG_TYPE_VIDEO_LEFT = 30;
    private static final int MSG_TYPE_VIDEO_RIGHT = 31;
    private static final int MSG_TYPE_MAP_LEFT = 50;
    private static final int MSG_TYPE_MAP_RIGHT = 51;

    private Context context;
    private List<Mensajito> mensajeNubeList;
    private LayoutInflater inflater;

    public List<Mensajito> getMensajeNubeList() {
        return mensajeNubeList;
    }

    public MensajeLocalListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MensajeNubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View itemView = inflater.inflate(R.layout.mensajito_item_text_rigth, parent, false);

        View itemView = null;

        if (viewType == MSG_TYPE_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_rigth, parent, false);
        }
        if (viewType == MSG_TYPE_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_left, parent, false);
        }

        return new MensajeLocalListAdapter.MensajeNubeViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        int select = -1;
        switch (mensajeNubeList.get(position).getTypeContent()) {
            case 0:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_RIGHT;
                } else {
                    select = MSG_TYPE_LEFT;
                }
                break;
            case 1:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_AUDIO_RIGHT;
                } else {
                    select = MSG_TYPE_AUDIO_LEFT;
                }
                break;
            case 2:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_IMAGE_RIGHT;
                } else {
                    select = MSG_TYPE_IMAGE_LEFT;
                }
                break;
            case 3:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_VIDEO_RIGHT;
                } else {
                    select = MSG_TYPE_VIDEO_LEFT;
                }
                break;
            case 4:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_MAP_RIGHT;
                } else {
                    select = MSG_TYPE_MAP_LEFT;
                }
                break;

        }


        return select;

    }

    @Override
    public void onBindViewHolder(@NonNull MensajeNubeViewHolder holder, int position) {
        Mensajito current = mensajeNubeList.get(position);
        Log.d("TAG",current.toString());
        holder.textViewContenido.setText(String.format("%s", current.getContent()));
        //holder.textViewFecha.setText(String.format("%s", String.valueOf(current.getCreateDate())));
        try {

            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
            Locale locale = new Locale("es", "ES");

            Calendar calendar = Calendar.getInstance(); // Returns instance with current date and time set


            //String [] result = herramientaCalendar.separarFechaYHora(fechaYHora,patronFechaYHora,locale,patronFecha, patronHora);

            //cita.setFechaCita();

            SimpleDateFormat formatFecha = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
                String date = formatFecha.format(current.getCreateDate());
                holder.textViewFecha.setText(date);

            }


        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }


        try {
            if (current.isReadStatus()){
                holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.teal_700));
            }else {
                holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.purple_700));

            }
        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        if (mensajeNubeList != null)
            return mensajeNubeList.size();
        else return 0;    }

    public void setMensajeNubeList(List<Mensajito> mensajeNubeListVar) {
        mensajeNubeList = mensajeNubeListVar;
        notifyDataSetChanged();
    }

    public class MensajeNubeViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewContenido;
        private TextView textViewFecha;
        private ImageView imageViewEstadoLectura;

        public MensajeNubeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            textViewFecha = itemView.findViewById(R.id.textViewDate);
            imageViewEstadoLectura = itemView.findViewById(R.id.imageViewEstadoLectura);
        }
    }
}
