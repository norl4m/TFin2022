package com.marlon.apolo.tfinal2022.individualChat.adaptador;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MensajeNubeListAdapter2 extends RecyclerView.Adapter<MensajeNubeListAdapter2.MensajeNubeViewHolder> {

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
    private static final String TAG = MensajeNubeListAdapter2.class.getSimpleName();
    private Context context;
    private ArrayList<MensajeNube> mensajeNubeList;
    private LayoutInflater inflater;
    private MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    private static ClickListener clickListener;


    public MensajeNubeListAdapter2(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        mensajeNubeList = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
    }


    @NonNull
    @Override
    public MensajeNubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = null;

        if (viewType == MSG_TYPE_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_rigth, parent, false);
        }
        if (viewType == MSG_TYPE_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_left, parent, false);
        }

        if (viewType == MSG_TYPE_AUDIO_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_audio_right, parent, false);

        }
        if (viewType == MSG_TYPE_AUDIO_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_audio_left, parent, false);
        }

        return new MensajeNubeViewHolder(itemView);


    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        int select = -1;
        switch (mensajeNubeList.get(position).getType()) {
            case 0:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_RIGHT;
                } else {
                    select = MSG_TYPE_LEFT;
                }
                break;
            case 2:
                if (mensajeNubeList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_AUDIO_RIGHT;
                } else {
                    select = MSG_TYPE_AUDIO_LEFT;
                }
                break;
            case 1:
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
        MensajeNube current = mensajeNubeList.get(position);
        try {
            holder.textViewContenido.setText(String.format("%s", current.getContenido()));
        } catch (Exception e) {

        }
        DataValidation dataValidation = new DataValidation();
        String sec = dataValidation.splitterData(current.getTimeStamp(), "(seconds=", ",");
        String nansec = dataValidation.splitterData(current.getTimeStamp(), ", nanoseconds=", ")");
        long seconds = Long.parseLong(sec);
        long nanoseconds = Integer.parseInt(nansec);
        Timestamp timestamp = new Timestamp(seconds, (int) nanoseconds);
        Date date = timestamp.toDate();
        holder.textViewFecha.setText(String.format("%s", date.toLocaleString()));
        if (current.isEstadoLectura()) {
            holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.teal_700));
        } else {
            holder.imageViewEstadoLectura.setColorFilter(context.getResources().getColor(R.color.purple_700));

        }


    }

    @Override
    public int getItemCount() {
        if (mensajeNubeList != null)
            return mensajeNubeList.size();
        else return 0;
    }

    public void setMensajeNubeList(ArrayList<MensajeNube> mensajeNubeList) {
        this.mensajeNubeList = mensajeNubeList;
        notifyDataSetChanged();
    }

    public void addMensajeNubeToList(MensajeNube mensajeNube) {
//        if (mensajeNubeList != null) {
//            mensajeNubeList = new ArrayList<>();
//        }
        mensajeNubeList.add(mensajeNube);
        notifyItemInserted(mensajeNubeList.size() - 1);
    }

    public void updateMensaje(int index, MensajeNube mensajeNube) {
//        if (mensajeNubeList != null) {
//            mensajeNubeList = new ArrayList<>();
//        }
        mensajeNubeList.set(index, mensajeNube);
        notifyItemChanged(index);
    }


    public class MensajeNubeViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewContenido;
        private TextView textViewFecha;
        private ImageView imageViewEstadoLectura;


        private TextView textViewCurrentTime;
        private TextView textViewDuration;

        private FloatingActionButton floatingActionButton;
        private FloatingActionButton floatingActionButtonPause;

        private SeekBar sbProgress;


        public MensajeNubeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            textViewFecha = itemView.findViewById(R.id.textViewDate);
            imageViewEstadoLectura = itemView.findViewById(R.id.imageViewEstadoLectura);


            try {
                textViewCurrentTime = itemView.findViewById(R.id.textViewInitTime);
                textViewDuration = itemView.findViewById(R.id.textViewFinishTime);
                floatingActionButton = itemView.findViewById(R.id.buttoPlay);
                floatingActionButtonPause = itemView.findViewById(R.id.buttonPause);
                sbProgress = itemView.findViewById(R.id.seekBar);
//                floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        MensajeNube mensajeNube = mensajeNubeList.get(getAbsoluteAdapterPosition());
//                        Toast.makeText(
//                                context.getApplicationContext(),
//                                mensajeNube.toString(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                    }
//                });

                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onItemClick(view, getAbsoluteAdapterPosition());
                    }
                });


            } catch (Exception e) {

            }

        }
    }



    public MensajeNube getMensajeNubeAtPosition(int position) {
        return mensajeNubeList.get(position);
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        MensajeNubeListAdapter2.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

}
