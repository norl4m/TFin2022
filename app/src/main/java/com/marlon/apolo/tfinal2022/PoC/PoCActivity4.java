package com.marlon.apolo.tfinal2022.PoC;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marlon.apolo.tfinal2022.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PoCActivity4 extends AppCompatActivity {
    private static final String TAG = PoCActivity4.class.getSimpleName();

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private RecordButton recordButton = null;
    private MediaRecorder recorder = null;

//    private PlayButton playButton = null;
//    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


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

    private void pauseSong() {
        playing = true;
        mediaPlayer.pause();
    }

    public void playSong() {

        playing = false;
        mediaPlayer = new MediaPlayer();


        if (stopAudio) {
            String fileName = getExternalCacheDir().getAbsolutePath();
            fileName += "/audiorecordtest.3gp";

//            Uri uri = Uri.parse("https://www.bensound.com/bensound-music/bensound-summer.mp3");
            Uri uri = Uri.parse(fileName);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(PoCActivity4.this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

//    private void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }

//    private void startPlaying() {
//        player = new MediaPlayer();
//        try {
//            player.setDataSource(fileName);
//            player.prepare();
//            player.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }

//    private void stopPlaying() {
//        player.release();
//        player = null;
//    }

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
            Log.e(LOG_TAG, "prepare() failed");
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording() {
        //Toast.makeText(getApplicationContext(), "Pausando grabación...", Toast.LENGTH_LONG).show();

        recorder.pause();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording() {
        //Toast.makeText(getApplicationContext(), "Reanudando grabación...", Toast.LENGTH_LONG).show();

        recorder.resume();
    }


    class RecordButton extends androidx.appcompat.widget.AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

//    class PlayButton extends androidx.appcompat.widget.AppCompatButton {
//        boolean mStartPlaying = true;
//
//        OnClickListener clicker = new OnClickListener() {
//            public void onClick(View v) {
//                onPlay(mStartPlaying);
//                if (mStartPlaying) {
//                    setText("Stop playing");
//                } else {
//                    setText("Start playing");
//                }
//                mStartPlaying = !mStartPlaying;
//            }
//        };
//
//        public PlayButton(Context ctx) {
//            super(ctx);
//            setText("Start playing");
//            setOnClickListener(clicker);
//        }
//    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_po_cactivity4);

        findViewById(R.id.resume_button).setVisibility(View.GONE);
        findViewById(R.id.pause_button).setEnabled(false);
        findViewById(R.id.stop_button).setEnabled(false);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
                findViewById(R.id.record_button).setEnabled(false);
                findViewById(R.id.record_button).setVisibility(View.GONE);
                findViewById(R.id.resume_button).setVisibility(View.VISIBLE);
                findViewById(R.id.pause_button).setEnabled(true);
                findViewById(R.id.stop_button).setEnabled(true);
                findViewById(R.id.resume_button).setEnabled(false);
            }
        });

        findViewById(R.id.pause_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pauseRecording();
//                    findViewById(R.id.record_button).setEnabled(false);
                    findViewById(R.id.pause_button).setEnabled(false);
                    findViewById(R.id.resume_button).setEnabled(true);
                }
            }
        });

        findViewById(R.id.resume_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resumeRecording();
                    //findViewById(R.id.record_button).setVisibility(View.VISIBLE);
//                    findViewById(R.id.resume_button).setVisibility(View.GONE);
//                    findViewById(R.id.record_button).setEnabled(false);
                    findViewById(R.id.pause_button).setEnabled(true);
                    findViewById(R.id.resume_button).setEnabled(false);
                }
            }
        });

        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopRecording();
                    findViewById(R.id.record_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.record_button).setEnabled(true);

                    findViewById(R.id.resume_button).setVisibility(View.GONE);
                    findViewById(R.id.resume_button).setEnabled(false);
                    findViewById(R.id.stop_button).setEnabled(false);
                    findViewById(R.id.pause_button).setEnabled(false);
                }
            }
        });


//        LinearLayout ll = new LinearLayout(this);
//        recordButton = new RecordButton(this);
//        ll.addView(recordButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//        playButton = new PlayButton(this);
//        ll.addView(playButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//        setContentView(ll);


        fabPlay = findViewById(R.id.fabPlay);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        textViewCurrentTime = findViewById(R.id.textViewUpdateTime);
        textViewDuration = findViewById(R.id.textViewDuration);
        fabPlay.setOnClickListener(clickListener);
        playing = true;
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        stopAudio = true;

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

    }


    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
            findViewById(R.id.record_button).setVisibility(View.VISIBLE);
            findViewById(R.id.record_button).setEnabled(true);

            findViewById(R.id.resume_button).setVisibility(View.GONE);
            findViewById(R.id.resume_button).setEnabled(false);
            findViewById(R.id.stop_button).setEnabled(false);
            findViewById(R.id.pause_button).setEnabled(false);


        }

//        if (player != null) {
//            player.release();
//            player = null;
//        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            try {
                seekBarProgress.setProgress(0);
                textViewCurrentTime.setText("0:00");
                handler.removeCallbacks(runnable);
                playing = true;
                //mediaPlayer.release();
                //mediaPlayer = new MediaPlayer();
                stopAudio = true;
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

            } catch (Exception e) {

            }

        }
    }

}