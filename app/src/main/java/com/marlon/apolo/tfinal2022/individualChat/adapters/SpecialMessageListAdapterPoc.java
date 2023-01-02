package com.marlon.apolo.tfinal2022.individualChat.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SpecialMessageListAdapterPoc extends RecyclerView.Adapter<SpecialMessageListAdapterPoc.MyadapterViewHolder> {

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
    private static final String TAG = SpecialMessageListAdapterPoc.class.getSimpleName();


    private static MediaPlayer mediaPlayer;
    private Activity activity;


    private ArrayList<MessageCloudPoc> mensajeNubeArrayList = new ArrayList<>();//change it() to your items
    private int currentPlayingPosition;
    private final SeekBarUpdater seekBarUpdater;
    private MyadapterViewHolder playingHolder;

    public SpecialMessageListAdapterPoc(Activity activity) {
        seekBarUpdater = new SeekBarUpdater();
        this.activity = activity;
        currentPlayingPosition = -1;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public MyadapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //put YourItemsLayout;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;


        if (viewType == MSG_TYPE_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_rigth, parent, false);
        }
        if (viewType == MSG_TYPE_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_left, parent, false);
        }

        if (viewType == MSG_TYPE_IMAGE_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_image_right, parent, false);

        }
        if (viewType == MSG_TYPE_IMAGE_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_image_left, parent, false);
        }


        if (viewType == MSG_TYPE_AUDIO_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_audio_right, parent, false);

        }
        if (viewType == MSG_TYPE_AUDIO_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_audio_left, parent, false);
        }


        if (viewType == MSG_TYPE_MAP_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_location_rigth, parent, false);

        }
        if (viewType == MSG_TYPE_MAP_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_location_left, parent, false);
        }

        return new MyadapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyadapterViewHolder holder, int position) {
        MessageCloudPoc current = mensajeNubeArrayList.get(position);

        try {


            if (current.getType() == 4) {

                holder.textViewContenido.setText(activity.getString(R.string.address_text,
                        current.getContenido()));


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String locationLoca = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", current.getLongitude(), current.getLatitude(), current.getLongitude(), current.getLatitude());
                        Log.d(TAG, locationLoca);
//                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
//                        Uri gmmIntentUri = Uri.parse(locationLoca);

                        Uri gmmIntentUri1 = Uri.parse(locationLoca);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent1 = new Intent(Intent.ACTION_VIEW, gmmIntentUri1);
// Make the Intent explicit by setting the Google Maps package
                        mapIntent1.setPackage("com.google.android.apps.maps");

                        try {
                            activity.startActivity(mapIntent1);

                        } catch (Exception e) {
                            Toast.makeText(activity, "La aplicación de Google Maps no se encuentra instalada en su dispositivo móvil", Toast.LENGTH_LONG).show();

                        }

                    }
                });


            }


//            holder.textViewTime.setText(String.valueOf(current.getCreateDate()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            if (current.getType() == 0) {
                holder.textViewContenido.setText(String.format("%s", current.getContenido()));

            }
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

        // Cargamos una referencia a la preferencia para cambiar el tema
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean mode = mPrefs.getBoolean("sync_theme", false);
        int someColorFrom = 0;
        int someColorTo = 0;


        switch (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
//                someColorFrom = ContextCompat.getColor(activity, R.color.black_minus);
                someColorFrom = ContextCompat.getColor(activity, R.color.black_minus);
                someColorTo = ContextCompat.getColor(activity, R.color.green_minus);

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                someColorFrom = ContextCompat.getColor(activity, R.color.purple_100);
                someColorTo = ContextCompat.getColor(activity, R.color.teal_100);

                break;
        }


//        int someColor = ContextCompat.getColor(activity, R.color.black_minus);
        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
//        activity.getTheme().resolveAttribute(R.attr.color6PrimaryVariant, typedValue, true);
//        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        activity.getTheme().resolveAttribute(R.attr.colorPrimaryInverse, typedValue, true);
//        int colorPrimary = typedValue.data;
        int colorPrimary = someColorFrom;

        TypedValue typedValue2 = new TypedValue();
//        activity.getTheme().resolveAttribute(R.attr.colorSecondaryVariant, typedValue2, true);
        activity.getTheme().resolveAttribute(R.attr.colorSecondary, typedValue2, true);
        //int colorSecondary = typedValue2.data;
        int colorSecondary = someColorTo;
//        DrawableCompat.setTint(holder.textViewContenido.getBackground(), activity.getResources().getColor(R.color.black_minus));
        try {
            switch (current.getType()) {
                case 0:
                case 4:
                    if (current.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        DrawableCompat.setTint(holder.textViewContenido.getBackground(), colorPrimary);
                    } else {
                        DrawableCompat.setTint(holder.textViewContenido.getBackground(), colorSecondary);
                    }
//                    holder.textViewContenido.setTextColor(ContextCompat.getColor(activity, R.color.white_smoke));
                    break;
                case 1:
                    if (current.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        DrawableCompat.setTint(holder.imageViewContent.getBackground(), colorPrimary);
                    } else {
                        DrawableCompat.setTint(holder.imageViewContent.getBackground(), colorSecondary);
                    }
//                    holder.textViewContenido.setTextColor(ContextCompat.getColor(activity, R.color.white_smoke));
                    break;
                case 2:
                    if (current.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        holder.relativeLayoutAudioE.setBackgroundColor(colorPrimary);
//                        holder.relativeLayoutAudioE.getBackground().setColorFilter(colorPrimary, PorterDuff.Mode.SRC_ATOP);

                        DrawableCompat.setTint(holder.relativeLayoutAudioE.getBackground(), colorPrimary);
                    } else {
//                        holder.relativeLayoutAudioE.setBackgroundColor(colorSecondary);
//                        holder.relativeLayoutAudioE.getBackground().setColorFilter(colorSecondary, PorterDuff.Mode.SRC_ATOP);

                        DrawableCompat.setTint(holder.relativeLayoutAudioE.getBackground(), colorSecondary);
                    }
                    break;

            }


        } catch (Exception e) {

        }

        /*Esto es una maravilla*/

        if (current.isEstadoLectura()) {
            holder.imageViewEstadoLectura.setColorFilter(activity.getResources().getColor(R.color.teal_700));
        } else {
            holder.imageViewEstadoLectura.setColorFilter(activity.getResources().getColor(R.color.purple_700));
        }

        try {


            Glide.with(activity).load(current.getContenido()).apply(new
                            RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error((R.drawable.error))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH))
                    .into(holder.imageViewContent);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        try {
            if (current.getType() == 2) {
                int duration = Integer.parseInt(current.getAudioDuration());
                holder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) duration),
                        TimeUnit.MILLISECONDS.toSeconds((long) duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) duration))));

                if (position == currentPlayingPosition) {
                    playingHolder = holder;
                    updatePlayingView();
                } else {
                    updateNonPlayingView(holder);
                }
            }

        } catch (Exception e) {

        }


    }

    private void updateNonPlayingView(MyadapterViewHolder holder) {
        holder.sbProgress.removeCallbacks(seekBarUpdater);
        holder.sbProgress.setEnabled(false);
        holder.sbProgress.setProgress(0);
        holder.ivPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        //playingHolder.textViewCurrentTime.setText("0:00");
    }

    private void updatePlayingView() {
        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
        playingHolder.sbProgress.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            playingHolder.sbProgress.postDelayed(seekBarUpdater, 100);
            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void setMensajeNubeArrayList(ArrayList<MessageCloudPoc> mensajeNubeArrayList) {
        this.mensajeNubeArrayList = mensajeNubeArrayList;
        notifyDataSetChanged();
    }

    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            if (null != playingHolder && null != mediaPlayer) {

                playingHolder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) mediaPlayer.getCurrentPosition()))));

                playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                playingHolder.sbProgress.postDelayed(this, 100);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mensajeNubeArrayList.size();
    }

    public class MyadapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        SeekBar sbProgress;
        ImageView ivPlayPause;

        private TextView textViewContenido;
        private TextView textViewFecha;
        private ImageView imageViewEstadoLectura;
        private ImageView imageViewContent;


        private TextView textViewCurrentTime;
        private RelativeLayout relativeLayoutAudioE;

        MyadapterViewHolder(View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            textViewFecha = itemView.findViewById(R.id.textViewDate);
            imageViewEstadoLectura = itemView.findViewById(R.id.imageViewEstadoLectura);
            try {
                imageViewContent = itemView.findViewById(R.id.imageViewContent);
                imageViewContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(mensajeNubeArrayList.get(getAdapterPosition()).getContenido()), "image/*");
                        activity.startActivity(intent);
                    }
                });
            } catch (Exception e) {

            }

            try {
                relativeLayoutAudioE = itemView.findViewById(R.id.audioElements);
                textViewCurrentTime = itemView.findViewById(R.id.textViewInitTime);
                ivPlayPause = itemView.findViewById(R.id.buttoPlay);
                ivPlayPause.setOnClickListener(this);
                sbProgress = itemView.findViewById(R.id.seekBar);
                sbProgress.setOnSeekBarChangeListener(this);
            } catch (Exception e) {

            }

        }

        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.seekBar:
                    break;

                case R.id.buttoPlay: {
                    if (getAdapterPosition() == currentPlayingPosition) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            if (mediaPlayer != null)
                                mediaPlayer.start();
                        }
                    } else {
                        currentPlayingPosition = getAdapterPosition();
                        if (mediaPlayer != null) {
                            if (null != playingHolder) {
                                updateNonPlayingView(playingHolder);
                            }
                            mediaPlayer.release();
                        }
                        playingHolder = this;


                        PlaySound(mensajeNubeArrayList.get(getAdapterPosition()).getContenido());//put your audio file


                    }
                    if (mediaPlayer != null)
                        updatePlayingView();
                }
                break;
            }


        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private void PlaySound(String filesound) {

        mediaPlayer = MediaPlayer.create(activity, Uri.parse(filesound));

        try {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                }
            });

            mediaPlayer.start();


        } catch (Exception e) {

        }

    }

    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }

//        mediaPlayer.release();
//        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();/*Ups*/
        currentPlayingPosition = -1;
    }


    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        int select = -1;
        switch (mensajeNubeArrayList.get(position).getType()) {
            case 0:/*texto*/
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_RIGHT;
                } else {
                    select = MSG_TYPE_LEFT;
                }
                break;
            case 2:/*audio*/
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_AUDIO_RIGHT;
                } else {
                    select = MSG_TYPE_AUDIO_LEFT;
                }
                break;
            case 1:/*imagen*/
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_IMAGE_RIGHT;
                } else {
                    select = MSG_TYPE_IMAGE_LEFT;
                }
                break;
            case 3:
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_VIDEO_RIGHT;
                } else {
                    select = MSG_TYPE_VIDEO_LEFT;
                }
                break;
            case 4:
                if (mensajeNubeArrayList
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
}
