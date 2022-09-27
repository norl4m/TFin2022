package com.marlon.apolo.tfinal2022.PoC;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marlon.apolo.tfinal2022.R;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PocActivity3 extends AppCompatActivity {
    private static final String TAG = PocActivity3.class.getSimpleName();

    FloatingActionButton fabPlay;
    SeekBar seekBarProgress;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    private boolean playing;
    private TextView textViewCurrentTime;
    private TextView textViewDuration;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (playing) {
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_pause_24));
                playSong();
            } else {
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
                pauseSong();
                handler.removeCallbacks(runnable);
            }
        }
    };
    private boolean stopAudio;
    private MediaRecorder recorder = null;
    private static String fileName = null;

    private void pauseSong() {
        playing = true;
        mediaPlayer.pause();
    }

    public void playSong() {

        playing = false;


        if (stopAudio) {
//            String fileName = getExternalCacheDir().getAbsolutePath();
//            fileName += "/audiorecordtest.3gp";

//            Uri uri = Uri.parse("https://www.bensound.com/bensound-music/bensound-summer.mp3");
            Uri uri = Uri.parse(fileName);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(PocActivity3.this, uri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                            seekBarProgress.setMax(mediaPlayer.getDuration());
                            textViewDuration.setText(String.format(Locale.US, "%d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                                    TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                    toMinutes((long) mediaPlayer.getDuration()))));
                        mediaPlayer.start();
                        stopAudio = false;
                        updateSeekbar();
                    }
                });

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        double ratio = i / 100.1;
                        int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
                        seekBarProgress.setSecondaryProgress(bufferingLevel);
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        seekBarProgress.setProgress(0);
                        textViewCurrentTime.setText("0:00");
                        handler.removeCallbacks(runnable);
                        playing = true;
                        //mediaPlayer.release();
                        //mediaPlayer = new MediaPlayer();
                        stopAudio = true;
                        fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    seekBarProgress.setMax(mediaPlayer.getDuration());
//                    textViewDuration.setText(String.format(Locale.US, "%d:%02d",
//                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
//                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                            toMinutes((long) mediaPlayer.getDuration()))));
//                    mediaPlayer.start();
//                    stopAudio = false;
//                    updateSeekbar();
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
//                    textViewCurrentTime.setText("0:00");
//                    handler.removeCallbacks(runnable);
//                    playing = true;
//                    //mediaPlayer.release();
//                    //mediaPlayer = new MediaPlayer();
//                    stopAudio = true;
//                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
//
//                }
//            });
        } else {
            mediaPlayer.start();
            updateSeekbar();
            if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()) {
//                mediaPlayer.start();
//                updateSeekbar();
            } else {
//                Uri uri = Uri.parse("https://www.bensound.com/bensound-music/bensound-summer.mp3");
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.reset();
//
//                try {
//                    mediaPlayer.setDataSource(PocActivity3.this, uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mediaPlayer) {
//                        seekBarProgress.setMax(mediaPlayer.getDuration());
//                        textViewDuration.setText(String.format(Locale.US, "%d:%02d",
//                                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
//                                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
//                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                toMinutes((long) mediaPlayer.getDuration()))));
//                        mediaPlayer.start();
//                        updateSeekbar();
//                    }
//                });
//
//                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                    @Override
//                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//                        double ratio = i / 100.1;
//                        int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
//                        seekBarProgress.setSecondaryProgress(bufferingLevel);
//                    }
//                });
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mediaPlayer) {
//                        seekBarProgress.setProgress(0);
//                        textViewCurrentTime.setText("0:00");
//                        handler.removeCallbacks(runnable);
//                        playing = true;
//                        mediaPlayer.release();
//                        stopAudio = true;
//                    }
//                });
            }

        }


    }

    private void updateSeekbar() {
        int currentPos = mediaPlayer.getCurrentPosition();
        seekBarProgress.setProgress(currentPos);
        Log.d(TAG, "UPDATING SEEKBAR");
        textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) currentPos),
                TimeUnit.MILLISECONDS.toSeconds((long) currentPos) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) currentPos))));

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable, 1000);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poc3);
        fabPlay = findViewById(R.id.fabPlay);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        textViewCurrentTime = findViewById(R.id.textViewUpdateTime);
        textViewDuration = findViewById(R.id.textViewDuration);
        fabPlay.setOnClickListener(clickListener);
        playing = true;
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        stopAudio = true;


        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);

                    textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) mediaPlayer.getCurrentPosition()))));

                    /**/
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.record_button).setVisibility(View.GONE);
                findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                startRecording();
            }
        });
        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.record_button).setVisibility(View.VISIBLE);
                findViewById(R.id.stop_button).setVisibility(View.GONE);
                stopRecording();
            }
        });

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.playConstrols).setVisibility(View.GONE);
                mediaPlayer.stop();
                File dir = getExternalCacheDir();
                File file = new File(dir, "audiorecordtest.3gp");
                boolean deleted = file.delete();
                if (deleted) {
                    Log.d(TAG, "Archivo de audio eliminado");
                    seekBarProgress.setProgress(0);
                    textViewCurrentTime.setText("0:00");
                    handler.removeCallbacks(runnable);
                    playing = true;
                    //mediaPlayer.release();
                    //mediaPlayer = new MediaPlayer();
                    stopAudio = true;
                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
                } else {
                    Log.d(TAG, "Archivo de audio no eliminado");
                }


            }
        });
    }

    private void startRecording() {
        findViewById(R.id.playConstrols).setVisibility(View.GONE);

//        Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_LONG).show();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        // Toast.makeText(getApplicationContext(), "Finalizando grabación...", Toast.LENGTH_LONG).show();

        recorder.stop();
        recorder.release();
        recorder = null;

        findViewById(R.id.playConstrols).setVisibility(View.VISIBLE);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
            findViewById(R.id.record_button).setVisibility(View.VISIBLE);
            findViewById(R.id.record_button).setEnabled(true);

//            findViewById(R.id.resume_button).setVisibility(View.GONE);
//            findViewById(R.id.resume_button).setEnabled(false);
            findViewById(R.id.stop_button).setEnabled(false);
//            findViewById(R.id.pause_button).setEnabled(false);


        }

//        if (player != null) {
//            player.release();
//            player = null;
//        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            seekBarProgress.setProgress(0);
            textViewCurrentTime.setText("0:00");
            handler.removeCallbacks(runnable);
            playing = true;
            //mediaPlayer.release();
            //mediaPlayer = new MediaPlayer();
            stopAudio = true;
            fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

//            try {
//                seekBarProgress.setProgress(0);
//                textViewCurrentTime.setText("0:00");
//                handler.removeCallbacks(runnable);
//                playing = true;
//                //mediaPlayer.release();
//                //mediaPlayer = new MediaPlayer();
//                stopAudio = true;
//                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
//
//            } catch (Exception e) {
//
//            }

        }
    }
}