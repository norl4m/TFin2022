package com.marlon.apolo.tfinal2022.individualChat.adaptador;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MensajeNubeListAdapter extends RecyclerView.Adapter<MensajeNubeListAdapter.MensajeNubeViewHolder> {

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
    private static final String TAG = MensajeNubeListAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<MensajeNube> mensajeNubeList;
    private LayoutInflater inflater;
    private MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;


    private int currentPlayingPosition;
    private MensajeNubeViewHolder playingHolder;
    private MensajeNubeViewHolder viewHolderLocal;
    private ArrayList<MensajeNubeViewHolder> mensajeNubeViewHolders;
    private int antPosition;
    private int nextPostion;


    // creating a variable for exoplayer
    ExoPlayer exoPlayer;

    private SeekBarUpdater seekBarUpdater;


    public MensajeNubeListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        mensajeNubeList = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        mensajeNubeViewHolders = new ArrayList<>();
        currentPlayingPosition = -1;
        antPosition = -1;
        nextPostion = -1;

        exoPlayer = new ExoPlayer.Builder(context).build();

    }


    private void updatePlayingView(MensajeNubeViewHolder playingHolder) {
        Toast.makeText(context.getApplicationContext(), String.format(Locale.US, "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) mediaPlayer.getDuration()))), Toast.LENGTH_LONG).show();

        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
        playingHolder.sbProgress.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            playingHolder.sbProgress.postDelayed(seekBarUpdater, 1000);
            playingHolder.floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
            playingHolder.floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }


    private void updateSeekbar(MensajeNubeViewHolder playingHolder) {

        if (mediaPlayer.isPlaying()) {
//            playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
//            //playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
//            playingHolder.sbProgress.setEnabled(true);
//            playingHolder.sbProgress.postDelayed(seekBarUpdater, 1000);
//            playingHolder.floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
            runnable = new Runnable() {
                @Override
                public void run() {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    playingHolder.sbProgress.setProgress(currentPos);
                    Log.d(TAG, "UPDATING SEEKBAR");
                    Log.d(TAG, String.format(Locale.US, "%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) currentPos),
                            TimeUnit.MILLISECONDS.toSeconds((long) currentPos) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) currentPos))));
                    playingHolder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) currentPos),
                            TimeUnit.MILLISECONDS.toSeconds((long) currentPos) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) currentPos))));
//                handler.removeCallbacks(runnable);
                    updateSeekbar(playingHolder);
                }
            };
            handler.postDelayed(runnable, 1000);
        } else {
            handler.removeCallbacks(runnable);
            playingHolder.floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }






    }

    @NonNull
    @Override
    public MensajeNubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = inflater.inflate(R.layout.mensajito_item_text_left, parent, false);

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
        mensajeNubeViewHolders.add(new MensajeNubeViewHolder(itemView));

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
        try {
            if (position == currentPlayingPosition) {
                playingHolder = holder;
//                updatePlayingView(holder);
                updateSeekbar(playingHolder);
            } else {
                updateNonPlayingView(holder);
            }
        } catch (Exception e) {

        }


//        try {
//            holder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(context, mensajeNubeList.get(holder.getAdapterPosition()).getContenido(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, String.format(Locale.US, "%d", holder.sbProgress.getId()), Toast.LENGTH_LONG).show();
//                    Log.d(TAG, String.format(Locale.US, "%d", holder.sbProgress.getId()));
//                }
//            });
//        } catch (Exception e) {
//
//        }


//        try {
//            holder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    playSong(current.getContenido(),
//                            holder.textViewCurrentTime,
//                            holder.textViewDuration,
//                            holder.seekBarProgress);
//
//                    holder.seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                            if (b) {
//                                mediaPlayer.seekTo(i);
//                                holder.seekBarProgress.setProgress(i);
//
//                                holder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
//                                        TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
//                                        TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
//                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                        toMinutes((long) mediaPlayer.getCurrentPosition()))));
//
//                                /**/
//                            }
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//
//                        }
//                    });
//
//                }
//            });
//
//        } catch (Exception e) {
//
//        }
    }


    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            if (null != playingHolder && null != mediaPlayer) {
                playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                playingHolder.sbProgress.postDelayed(this, 1000);
            }
        }
    }


//    private void updateNonPlayingView(MensajeNubeViewHolder holder) {
//        holder.sbProgress.removeCallbacks(seekBarUpdater);
//        holder.sbProgress.setEnabled(false);
//        holder.sbProgress.setProgress(0);
//        holder.floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//    }
//
//    private void updatePlayingView() {
////        Toast.makeText(context, "updatePlayingView", Toast.LENGTH_LONG).show();
////        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
//        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
//        playingHolder.sbProgress.setEnabled(true);
//        if (mediaPlayer.isPlaying()) {
//            Toast.makeText(context, "play updatePlayingView", Toast.LENGTH_LONG).show();
//
//            playingHolder.sbProgress.postDelayed(seekBarUpdater, 1000);
//            playingHolder.floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
//        } else {
//            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
//            playingHolder.floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//        }
//    }
//
//    private class SeekBarUpdater implements Runnable {
//        @Override
//        public void run() {
//            //if (null != playingHolder && null != mediaPlayer) {
//            playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
//            playingHolder.sbProgress.postDelayed(this, 1000);
//            Toast.makeText(context, "raro", Toast.LENGTH_LONG).show();
//            Log.d(TAG, "raro");
//            //}
//        }
//    }


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

    private void updateNonPlayingView(MensajeNubeViewHolder holder) {
//            holder.sbProgress.removeCallbacks(seekBarUpdater);
        holder.sbProgress.setEnabled(false);
        holder.sbProgress.setProgress(0);
        holder.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24));
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
                MensajeNubeViewHolder holderAux = this;
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MensajeNube mensajeNube = mensajeNubeList.get(getAbsoluteAdapterPosition());
//                        Uri uri = Uri.parse(mensajeNube.getContenido());
//                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                        mediaPlayer.reset();


                        if (getAdapterPosition() == currentPlayingPosition) {
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                playingHolder.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24));
                            } else {
                                if (mediaPlayer != null) {
                                    playingHolder.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_24));
                                    mediaPlayer.start();
                                }
                            }
                        } else {
                            currentPlayingPosition = getAdapterPosition();
                            if (mediaPlayer != null) {
                                if (null != playingHolder) {
                                    updateNonPlayingView(playingHolder);
                                }
                                mediaPlayer.release();
                            }
                            playingHolder = holderAux;

                            playSound(mensajeNube.getContenido());//put your audio file


                        }

//                        if (mediaPlayer != null)
//                            updatePlayingView(playingHolder);

//                        try {
//                            mediaPlayer.setDataSource(mensajeNube.getContenido());
//                            mediaPlayer.prepareAsync();
//                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mediaPlayer) {
////                                    seekBarProgress.setMax(mediaPlayer.getDuration());
//                                    textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
//                                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
//                                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
//                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                            toMinutes((long) mediaPlayer.getDuration()))));
//                                    mediaPlayer.start();
//
//                                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_24));
//
//                                    //floatingActionButton.setVisibility(View.GONE);
//                                    //floatingActionButtonPause.setVisibility(View.VISIBLE);
////                                    stopAudio = false;
////                                    updateSeekbar();
//
//
//                                    if (getAbsoluteAdapterPosition() == currentPlayingPosition) {
//                                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                            mediaPlayer.pause();
//                                        } else {
//                                            if (mediaPlayer != null)
//                                                mediaPlayer.start();
//                                        }
//                                    } else {
//                                        currentPlayingPosition = getAbsoluteAdapterPosition();
//                                        if (mediaPlayer != null) {
//                                            if (null != playingHolder) {
//                                                updateNonPlayingView(playingHolder);
//                                            }
////                                            mediaPlayer.release();
//                                        }
//                                    }
//                                    playingHolder = holderAux;
//
//                                }
//
//
//                            });
//
//                            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                                @Override
//                                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//                                    double ratio = i / 100.1;
//                                    int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
////                                    seekBarProgress.setSecondaryProgress(bufferingLevel);
//                                }
//                            });
//                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mediaPlayer) {
//                                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24));
//
//                                    //floatingActionButton.setVisibility(View.VISIBLE);
//                                    //floatingActionButtonPause.setVisibility(View.GONE);
////                                    seekBarProgress.setProgress(0);
////                                    textViewCurrentTime.setText("0:00");
////                                    handler.removeCallbacks(runnable);
////                                    playing = true;
////                                    //mediaPlayer.release();
////                                    //mediaPlayer = new MediaPlayer();
////                                    stopAudio = true;
////                                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
//
//                                }
//                            });
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                });
//                seekBarProgress.setClickable(true);
//                sbProgress.setProgress(0);
//                textViewDuration.setText("0:00");
//                textViewCurrentTime.setText("0:00");

//
//                sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                        if (b) {
//                            mediaPlayer.seekTo(i);
//                        }
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//
//                    }
//                });
//
//                floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        if (getAdapterPosition() == currentPlayingPosition) {
////                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
////                                mediaPlayer.pause();
////                            } else {
////                                if (mediaPlayer != null)
////                                    mediaPlayer.start();
////                            }
////                        } else {
////                            currentPlayingPosition = getAdapterPosition();
////                            if (mediaPlayer != null) {
////                                if (null != playingHolder) {
////                                    updateNonPlayingView(playingHolder);
////                                }
////                                mediaPlayer.release();
////                            }
////                            playingHolder = mensajeNubeViewHolders.get(getAdapterPosition());
//
//
//                        Toast.makeText(context, mensajeNubeList.get(getAdapterPosition()).getContenido(), Toast.LENGTH_LONG).show();
////                        Toast.makeText(context,String.format("%d",sbProgress.toString()), Toast.LENGTH_LONG).show();
//                        Toast.makeText(context, String.format(Locale.US, "%d", sbProgress.getId()), Toast.LENGTH_LONG).show();
////                            try {
////                                PlaySound(mensajeNubeList.get(getAdapterPosition()).getContenido());//put your audio file
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
//
//
////                        }
////                        if (mediaPlayer != null) {
////                            updatePlayingView();
////                        }
//                    }
//                });

//                floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        playSong(mensajeNubeList.get(getAdapterPosition()).getContenido(),
//                                textViewCurrentTime,
//                                textViewDuration,
//                                seekBarProgress);
//
//                        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                            @Override
//                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                                if (b) {
//                                    mediaPlayer.seekTo(i);
//                                    seekBarProgress.setProgress(i);
//
//                                    textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
//                                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
//                                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
//                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                            toMinutes((long) mediaPlayer.getCurrentPosition()))));
//
//                                    /**/
//                                }
//                            }
//
//                            @Override
//                            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                            }
//
//                            @Override
//                            public void onStopTrackingTouch(SeekBar seekBar) {
//
//                            }
//                        });
//
//                    }
//                });
            } catch (Exception e) {

            }

        }


        private void playSound(String filesound) {

//            mediaPlayer = MediaPlayer.create(activity, Uri.parse(String.valueOf(filesound)));
//
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    releaseMediaPlayer();
//                }
//            });
//            mediaPlayer.start();


            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(filesound);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
//                                    seekBarProgress.setMax(mediaPlayer.getDuration());
                        playingHolder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) mediaPlayer.getDuration()))));
                        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
                        playingHolder.sbProgress.setEnabled(true);
                        playingHolder.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_24));

                        mediaPlayer.start();

//                        updatePlayingView(playingHolder);

                        updateSeekbar(playingHolder);


                        //playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());


//
//                        Toast.makeText(context.getApplicationContext(), String.format(Locale.US, "%d:%02d",
//                                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
//                                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
//                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                toMinutes((long) mediaPlayer.getDuration()))), Toast.LENGTH_LONG).show();
//

                    }


                });

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        double ratio = i / 100.1;
                        int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
//                                    seekBarProgress.setSecondaryProgress(bufferingLevel);
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_arrow_24));

                        playingHolder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) mediaPlayer.getDuration()))));
                        mediaPlayer.stop();
                        releaseMediaPlayer();
                        //floatingActionButton.setVisibility(View.VISIBLE);
                        //floatingActionButtonPause.setVisibility(View.GONE);
//                                    seekBarProgress.setProgress(0);
//                                    textViewCurrentTime.setText("0:00");
//                                    handler.removeCallbacks(runnable);
//                                    playing = true;
//                                    //mediaPlayer.release();
//                                    //mediaPlayer = new MediaPlayer();
//                                    stopAudio = true;
//                                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        private void releaseMediaPlayer() {
            if (null != playingHolder) {
                updateNonPlayingView(playingHolder);
            }

//            mediaPlayer.release();
//            mediaPlayer = null;
            currentPlayingPosition = -1;

            handler.removeCallbacks(runnable);
        }
//        private void PlaySound(String filesound) throws IOException {
//
////            mediaPlayer = MediaPlayer.create(context, Uri.parse(String.valueOf(filesound)));
//
//            mediaPlayer.setDataSource(filesound);
//            mediaPlayer.prepareAsync();
//
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    releaseMediaPlayer();
//                }
//            });
////            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                @Override
////                public void onPrepared(MediaPlayer mediaPlayer) {
////                    mediaPlayer.start();
////                }
////            });
//            mediaPlayer.start();
//        }
//
//        private void releaseMediaPlayer() {
//            if (null != playingHolder) {
//                updateNonPlayingView(playingHolder);
//            }
////            if (outputFile.exists())
////                outputFile.delete();
//
//            mediaPlayer.release();
//            mediaPlayer = null;
//            currentPlayingPosition = -1;
//        }


//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (fromUser) {
//                mediaPlayer.seekTo(progress);
//            }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//        }
    }


//    private void updateSeekbar(SeekBar seekBarProgress, TextView textViewCurrentTime) {
//        int currentPos = mediaPlayer.getCurrentPosition();
//        seekBarProgress.setProgress(currentPos);
////        Log.d(TAG, "UPDATING SEEKBAR");
//        textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes((long) currentPos),
//                TimeUnit.MILLISECONDS.toSeconds((long) currentPos) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                toMinutes((long) currentPos))));
//
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                updateSeekbar(seekBarProgress, textViewCurrentTime);
//            }
//        };
//        handler.postDelayed(runnable, 500);
//
//
//    }

//    public void playSong(String fileName, TextView textViewCurrenTime, TextView textViewDuration, SeekBar seekBarProgress) {
//
//        String url = fileName;
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.reset();
//
//        try {
//            handler.removeCallbacks(runnable);
//        } catch (Exception e) {
////            Log.d(TAG,e.toString());
//        }
//
//        try {
//
//
//            mediaPlayer.setDataSource(url);
//            //mediaPlayer.prepare(); // might take long! (for buffering, etc)
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    seekBarProgress.setMax(mediaPlayer.getDuration());
////                    textViewDuration.setText(String.format(Locale.US, "%d:%02d",
////                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
////                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
////                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
////                                            toMinutes((long) mediaPlayer.getDuration()))));
//                    mediaPlayer.start();
////                        stopAudio = false;
//                    updateSeekbar(seekBarProgress, textViewCurrenTime);
//                }
//            });
//
//            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                @Override
//                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//                    double ratio = i / 100.1;
//                    int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
//                    seekBarProgress.setSecondaryProgress(bufferingLevel);
//                }
//            });
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    seekBarProgress.setProgress(0);
////                    textViewCurrenTime.setText("0:00");
//                    textViewCurrenTime.setText(String.format(Locale.US, "%d:%02d",
//                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
//                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                            toMinutes((long) mediaPlayer.getCurrentPosition()))));
//                    handler.removeCallbacks(runnable);
////                    playing = true;
//                    //mediaPlayer.release();
//                    //mediaPlayer = new MediaPlayer();
////                    stopAudio = true;
////                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
//
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public void liberarRecursos() {


//        if (player != null) {
//            player.release();
//            player = null;
//        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            handler.removeCallbacks(runnable);
        }
    }

}
