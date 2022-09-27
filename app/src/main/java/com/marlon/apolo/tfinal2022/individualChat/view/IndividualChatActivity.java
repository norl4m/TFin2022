package com.marlon.apolo.tfinal2022.individualChat.view;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.individualChat.adaptador.SpecialMensajeNubeListAdapter;
import com.marlon.apolo.tfinal2022.individualChat.view.location.LocationActivity;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
//import com.marlon.apolo.tfinal2022.videoLlamada.VideoChatViewActivity;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class IndividualChatActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = IndividualChatActivity.class.getSimpleName();
    private static final int SELECT_AUDIO = 1500;
    private static final int SELECT_IMAGE = 1501;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1502;
    private static final int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1503;
    private static final int PERMISSION_REQUEST_CAMERA = 1504;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 1505;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 1506;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1510;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1520;
    private static final int LOCATION_CODE = 1700;
    private TextView textViewNameContactTo;
    private ImageView imageViewNameContactTo;

    private FloatingActionButton fabCall;
    private FloatingActionButton fabVideoCall;
    private FloatingActionButton fabMessageMic;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabShareFiles;

    private TextInputEditText textInputEditTextMessage;

    private boolean micMode;
    private Usuario usuarioTo;
    private Usuario usuarioLocal;
    private Chat chat;

    private RecyclerView recyclerViewMensajes;
    //    private MensajeNubeListAdapter mensajeNubeListAdapter;
    private SpecialMensajeNubeListAdapter mensajeNubeListAdapter;
    private String fileName;
    private SeekBar seekBarProgress;
    private MediaRecorder recorder;
    FloatingActionButton fabPlay;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    private boolean playing;
    private TextView textViewCurrentTime;
    private TextView textViewDuration;
    private boolean stopAudio;
    private ChildEventListener childEventListenerMensaje;

    private FloatingActionButton fabChooseImageProfile;

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
    private Uri uriPhoto;
    private SharedPreferences myPreferences;
    private int usuario;
    private boolean flagUsuarioEliminadoActual;


    public Dialog alertDialogAttachFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Compartir: ");
        String[] elements = {"Audio", "Imagen", "Ubicación"};
        builder.setItems(elements, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        selectAudio();
                        break;
                    case 1:
//                        selectImage();
                        escogerDesdeGaleria();
                        break;
                    case 2:
                        shareLocation();
                        break;
                }
            }
        });
        return builder.create();
    }

    private void shareLocation() {
        Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
        intent.putExtra("usuarioTo", usuarioTo);
        intent.putExtra("usuarioLocal", usuarioLocal);
        intent.putExtra("chat", chat);
//        startActivity(intent);
        startActivityForResult(intent, LOCATION_CODE);

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SELECT_IMAGE);
    }

    private void selectAudio() {

//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
//        startActivityForResult(intent, SELECT_AUDIO);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo de audio desde: "),SELECT_AUDIO);
            startActivityForResult(intent, SELECT_AUDIO);
        }
    }

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
                mediaPlayer.setDataSource(IndividualChatActivity.this, uri);
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

    public void listeneMensajesNube(String idChat) {
        Log.d(TAG, "####################################");
        Log.d(TAG, "LISTENER MENSAJES");
        Log.d(TAG, "####################################");


        MensajeNubeRepository mensajeNubeRepository = new MensajeNubeRepository();
        mensajeNubeRepository.getAllMensajes(idChat).observe(IndividualChatActivity.this, mensajeNubes -> {
            mensajeNubeListAdapter.setMensajeNubeArrayList(mensajeNubes);
            recyclerViewMensajes.scrollToPosition(mensajeNubes.size() - 1);
            for (MensajeNube m : mensajeNubes) {
                if (m.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mensajeNubeRepository.updateReadState(m);
                }
            }
        });


//        MensajeNubeViewModel mensajeNubeViewModel = new ViewModelProvider(this).get(MensajeNubeViewModel.class);
//        mensajeNubeViewModel.getAllMensajesNube(idChat).observe(IndividualChatActivity.this, mensajeNubes -> {
//            mensajeNubeListAdapter.setMensajeNubeArrayList(mensajeNubes);
//
//        });


        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
//        childEventListenerMensaje = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d(TAG, "onChildAdded");
//                try {
//                    MensajeNube mensajeNube = snapshot.getValue(MensajeNube.class);
//                    Log.d(TAG, mensajeNube.toString());
//                    mensajeNubeArrayList.add(mensajeNube);
//                    if (mensajeNube.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        mensajeNube.setEstadoLectura(true);
//                        updateStadoMensaje(mensajeNube);
//                    }
////                    mensajeNubeListAdapter.setMensajeNubeList(mensajeNubeArrayList);
//                    mensajeNubeListAdapter.addMensajeNubeToList(mensajeNube);
//                    recyclerViewMensajes.scrollToPosition(mensajeNubeArrayList.size() - 1);
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                MensajeNube mensajeNube = snapshot.getValue(MensajeNube.class);
//                int index = 0;
//                for (MensajeNube m : mensajeNubeArrayList) {
//                    if (m.getIdMensaje().equals(mensajeNube.getIdMensaje())) {
//                        if (mensajeNube.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                            mensajeNubeListAdapter.updateMensaje(index, mensajeNube);
//                            break;
//                        }
//                    }
//                    index++;
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        FirebaseDatabase.getInstance().getReference()
//                .child("mensajes")
//                .child(idChat)
//                .addChildEventListener(childEventListenerMensaje);
    }


    private void removeChildMensajesListener() {
        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(chat.getIdChat())
                .addChildEventListener(childEventListenerMensaje);
    }

    private void updateStadoMensaje(MensajeNube mensajeNube) {
        FirebaseDatabase.getInstance().getReference()
                .child("mensajes")
                .child(mensajeNube.getIdChat())
                .child(mensajeNube.getIdMensaje())
                .child("estadoLectura")
                .setValue(true);
    }

    private void startRecording() {
        findViewById(R.id.playConstrols).setVisibility(View.GONE);
        Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        findViewById(R.id.textViewBlink).startAnimation(animBlink);
        findViewById(R.id.textViewBlink).setVisibility(View.VISIBLE);


//        Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_LONG).show();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
        findViewById(R.id.textViewBlink).setVisibility(View.GONE);


        try {
            findViewById(R.id.textViewBlink).clearAnimation();
        } catch (Exception e) {

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        usuario = myPreferences.getInt("usuario", -1);

//        Toast.makeText(getContext(), String.valueOf(usuario), Toast.LENGTH_LONG).show();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadLocalUser();

        fabPlay = findViewById(R.id.fabPlay);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        textViewCurrentTime = findViewById(R.id.textViewUpdateTime);
        textViewDuration = findViewById(R.id.textViewDuration);
        fabChooseImageProfile = findViewById(R.id.buttonCamera);
        fabChooseImageProfile.setOnClickListener(this);
        fabPlay.setOnClickListener(clickListener);
        playing = true;
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        stopAudio = true;

        try {

            fileName = getExternalCacheDir().getAbsolutePath();
            fileName += "/audiorecordtest.mp3";

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

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


        findViewById(R.id.buttonAttachFile).setOnClickListener(this);

        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
//                findViewById(R.id.record_button).setVisibility(View.GONE);
//                findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
//                startRecording();


                if (ContextCompat.checkSelfPermission(IndividualChatActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    findViewById(R.id.record_button).setVisibility(View.GONE);
                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                    startRecording();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(IndividualChatActivity.this,
                        Manifest.permission.RECORD_AUDIO)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.

                    Snackbar.make(fabMessageMic, "Permiso de audio necesario",
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            ActivityCompat.requestPermissions(IndividualChatActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    PERMISSION_REQUEST_RECORD_AUDIO);
                        }
                    }).show();
                } else {
                    // You can directly ask for the permission.
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_RECORD_AUDIO);
                }

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
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                try {
                    recorder.stop();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                File dir = getExternalCacheDir();
//                File file = new File(dir, "audiorecordtest.mp3");
                File file = new File(fileName);
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

                    findViewById(R.id.messageContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.linLayoutMicControls).setVisibility(View.GONE);

                } else {
                    Log.d(TAG, "Archivo de audio no eliminado");
                }

                try {
                    findViewById(R.id.textViewBlink).setVisibility(View.GONE);
                    findViewById(R.id.textViewBlink).clearAnimation();
                } catch (Exception e) {

                }
            }
        });


        findViewById(R.id.fabSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.playConstrols).setVisibility(View.GONE);
                try {
                    findViewById(R.id.textViewBlink).setVisibility(View.GONE);
                    findViewById(R.id.textViewBlink).clearAnimation();
                } catch (Exception e) {

                }
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {

                }
                try {
                    recorder.stop();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

//                recorder.release();
//                recorder = null;


//                File dir = getExternalCacheDir();
//                File file = new File(dir, "audiorecordtest.3gp");
//                boolean deleted = file.delete();
//                if (deleted) {
                seekBarProgress.setProgress(0);
                textViewCurrentTime.setText("0:00");
                handler.removeCallbacks(runnable);
                playing = true;
                //mediaPlayer.release();
                //mediaPlayer = new MediaPlayer();
                stopAudio = true;
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                findViewById(R.id.messageContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.linLayoutMicControls).setVisibility(View.GONE);


                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                mensajeNube.setContenido(fileName);
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioTo.getIdUsuario());
                mensajeNube.setEstadoLectura(false);

                Uri uri = Uri.parse(fileName);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(getApplicationContext(), uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                mensajeNube.setAudioDuration(durationStr);
                mensajeNube.setType(2);/*0 audio */


                if (chat == null) {
                    String idChat = FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .push().getKey();
                    chat = new Chat();
                    chat.setIdChat(idChat);
                    chat.setMensajeNube(mensajeNube);

                    Participante participante1 = new Participante();
                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

                    Participante participante2 = new Participante();
                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());

                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
                    participanteArrayList.add(participante1);
                    participanteArrayList.add(participante2);

                    chat.setParticipantes(participanteArrayList);
                    mensajeNube.setIdChat(chat.getIdChat());
                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);

                } else {
                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
                }


//                } else {
//                    Log.d(TAG, "Archivo de audio no eliminado");
//                }


            }
        });


        micMode = true;
        flagUsuarioEliminadoActual = true;
//        Toast.makeText(getApplicationContext(),trabajador.toString(),Toast.LENGTH_LONG).show();
        textViewNameContactTo = findViewById(R.id.textViewNameParticipant);
        imageViewNameContactTo = findViewById(R.id.imageViewParticipant);
        textInputEditTextMessage = findViewById(R.id.textInputEditTextMessage);
        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes);

//        mensajeNubeListAdapter = new MensajeNubeListAdapter(this);
        mensajeNubeListAdapter = new SpecialMensajeNubeListAdapter(this);
        recyclerViewMensajes.setAdapter(mensajeNubeListAdapter);
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager) recyclerViewMensajes.getLayoutManager()).setStackFromEnd(true);


        textInputEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nowMessage = charSequence.toString();
                Log.d(TAG, String.format("Mensaje actual: %s", nowMessage));
                Log.d(TAG, String.format("Mensaje actual i: %d", i));
                Log.d(TAG, String.format("Mensaje actual i1: %d", i1));
                Log.d(TAG, String.format("Mensaje actual i2: %d", i2));

                if (i == 0 && i1 == 1 && i2 == 0) {
                    animationButtonMic(1);
                } else {
                    animationButtonMic(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Trabajador trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
        chat = (Chat) getIntent().getSerializableExtra("chat");

        if (chat != null) {
            //Toast.makeText(getApplicationContext(), chat.toString(), Toast.LENGTH_LONG).show();
            for (Participante p : chat.getParticipantes()) {
                if (!p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    try {
                        switch (p.getEstadoEnApp()) {
                            case "eliminado":
                                setUsuarioEliminado();
                                break;
                        }
                    } catch (Exception e) {

                    }

                    loadContactInfoFromChat(p.getIdParticipante());
                    break;
                }
            }
            listenerNotifications(chat);
            listeneMensajesNube(chat.getIdChat());
        }

        if (trabajador != null) {
            loadContactInfo((Usuario) trabajador);
        }

//        ImageView rocketImage = (ImageView) findViewById(R.id.rocket_image);
        fabMessageMic = findViewById(R.id.buttonMessageAndMic);
        fabMessageMic.setOnClickListener(this);

//        ImageView rocketImage = (ImageView) findViewById(R.id.buttonPoc);
//        rocketImage.setBackgroundResource(R.drawable.animation_poc);
//        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
//
//        rocketImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rocketAnimation.start();
//            }
//        });

        findViewById(R.id.imageViewCall).setOnClickListener(this);
        findViewById(R.id.imageViewVideoCall).setOnClickListener(this);


    }

    private void setUsuarioEliminado() {
        flagUsuarioEliminadoActual = true;
        textViewNameContactTo.setText("Usuario no registrado");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Información");
        builder.setMessage("Lo sentimos, el usuario al que intentas contactar ha eliminado su cuenta de nuestra aplicación.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
                        blockingUI();
                    }
                });
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void blockingUI() {
        findViewById(R.id.imageViewVideoCall).setEnabled(false);
        findViewById(R.id.imageViewCall).setEnabled(false);
        findViewById(R.id.buttonCamera).setEnabled(false);
        findViewById(R.id.buttonMessageAndMic).setEnabled(false);
        findViewById(R.id.buttonAttachFile).setEnabled(false);

        textInputEditTextMessage.setEnabled(false);
    }


    private void listenerNotifications(Chat chat) {
        Log.d(TAG, "####################################");
        Log.d(TAG, "LISTENER NOTIFICATIONS");
        Log.d(TAG, "####################################");
        String idFrom = "";

        for (Participante p : chat.getParticipantes()) {
            if (!p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                idFrom = p.getIdParticipante();
                break;
            }
        }
        Log.d(TAG, idFrom);
        try {
            FirebaseDatabase.getInstance().getReference()
                    .child("notificaciones")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(idFrom)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Notificaciones eliminadas");
                                //Toast.makeText(getApplicationContext(), "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
                            } else {

                            }
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
//        ChildEventListener childEventListenerNotifications = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        FirebaseDatabase.getInstance().getReference()
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(idFrom)
//                .setValue(null);
//                .removeValue(new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                        Log.e(TAG, "Notifications eliminadas");
//                    }
//                });
//                .addChildEventListener(childEventListenerNotifications);


    }

    private void playAudio(MensajeNube mensajeNube) {
        Toast.makeText(getApplicationContext(), mensajeNube.getContenido(), Toast.LENGTH_LONG).show();

        MediaPlayer mediaPlayer1 = new MediaPlayer();


        Uri uri = Uri.parse(mensajeNube.getContenido());
        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer1.reset();

        try {
            mediaPlayer1.setDataSource(IndividualChatActivity.this, uri);
            mediaPlayer1.prepareAsync();
            mediaPlayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
//                    seekBarProgress.setMax(mediaPlayer.getDuration());
//                    textViewDuration.setText(String.format(Locale.US, "%d:%02d",
//                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
//                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                            toMinutes((long) mediaPlayer.getDuration()))));
                    mediaPlayer.start();
//                    stopAudio = false;
//                    updateSeekbar();
                }
            });

            mediaPlayer1.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    double ratio = i / 100.1;
                    int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
//                    seekBarProgress.setSecondaryProgress(bufferingLevel);
                }
            });
            mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
//                    seekBarProgress.setProgress(0);
//                    textViewCurrentTime.setText("0:00");
//                    handler.removeCallbacks(runnable);
//                    playing = true;
//                    //mediaPlayer.release();
//                    //mediaPlayer = new MediaPlayer();
//                    stopAudio = true;
//                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContactInfoFromChat(String idTo) {

        ValueEventListener valueEventListenerUserTo = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Administrador administrador = snapshot.getValue(Administrador.class);
                    if (administrador != null) {
                        usuarioTo = administrador;
                        loadContactInfo(usuarioTo);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    if (empleador != null) {
                        usuarioTo = empleador;
                        loadContactInfo(usuarioTo);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    if (trabajador != null) {
                        usuarioTo = trabajador;
                        loadContactInfo(usuarioTo);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(idTo)
                .addListenerForSingleValueEvent(valueEventListenerUserTo);

        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(idTo)
                .addListenerForSingleValueEvent(valueEventListenerUserTo);

        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(idTo)
                .addListenerForSingleValueEvent(valueEventListenerUserTo);

    }

    private void loadLocalUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ValueEventListener valueEventListenerUserLocal = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Administrador administrador = snapshot.getValue(Administrador.class);
                    if (administrador != null) {
                        usuarioLocal = administrador;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    if (empleador != null) {
                        usuarioLocal = empleador;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    if (trabajador != null) {
                        usuarioLocal = trabajador;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(user.getUid())
                .addListenerForSingleValueEvent(valueEventListenerUserLocal);

        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(user.getUid())
                .addListenerForSingleValueEvent(valueEventListenerUserLocal);

        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(user.getUid())
                .addListenerForSingleValueEvent(valueEventListenerUserLocal);

    }


    private void animationButtonMic(int i) {
        switch (i) {
            case 0:
                fabMessageMic.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_send_24));
//                rocketAnimation = (AnimationDrawable) fabMessageMic.getDrawable();
//                rocketAnimation.start();
                micMode = false;
                break;
            case 1:
                fabMessageMic.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_mic_24));
                micMode = true;
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private Context getContext() {
        return this;
    }

    private void loadContactInfo(Usuario usuario) {
        usuarioTo = usuario;
        if (!usuario.getNombre().isEmpty() && !usuario.getApellido().isEmpty()) {
            textViewNameContactTo.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido()));
        } else {
            textViewNameContactTo.setText(String.format("%s %s", "Usuario", "no encontrado"));

        }
        if (usuario.getFotoPerfil() != null) {
            Glide.with(getApplicationContext()).load(usuario.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageViewNameContactTo);
        }
        try {
            setUsuarioBloqueado(usuarioTo.getIdUsuario());
        } catch (Exception e) {

        }
    }

    public void setUsuarioBloqueado(String idTo) {
        SharedPreferences myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        editorPref = myPreferences.edit();
        editorPref.putString("idUserBlocking", idTo);
        editorPref.apply();
//        Toast.makeText(getApplicationContext(), "Usuario bloqueado: \n" + myPreferences.getString("idUserBlocking", ""), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMessageAndMic:
                if (micMode) {
                    //startActivity(new Intent(this, PocActivity3.class));
//                    findViewById(R.id.messageContainer).setVisibility(View.GONE);
//                    findViewById(R.id.linLayoutMicControls).setVisibility(View.VISIBLE);
//
//                    findViewById(R.id.record_button).setVisibility(View.GONE);
//                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
//                    startRecording();
//
//


                    if (ContextCompat.checkSelfPermission(IndividualChatActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        // You can use the API that requires the permission.
//                        findViewById(R.id.record_button).setVisibility(View.GONE);
//                        findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
//                        startRecording();


                        findViewById(R.id.messageContainer).setVisibility(View.GONE);
                        findViewById(R.id.linLayoutMicControls).setVisibility(View.VISIBLE);

                        findViewById(R.id.record_button).setVisibility(View.GONE);
                        findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                        startRecording();

                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(IndividualChatActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected. In this UI,
                        // include a "cancel" or "no thanks" button that allows the user to
                        // continue using your app without granting the permission.

                        Snackbar.make(fabMessageMic, "Permiso de audio necesario",
                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request the permission
                                ActivityCompat.requestPermissions(IndividualChatActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        PERMISSION_REQUEST_RECORD_AUDIO);
                            }
                        }).show();
                    } else {
                        // You can directly ask for the permission.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                    PERMISSION_REQUEST_RECORD_AUDIO);
                        }
                    }


//                    startActivity(new Intent(this, PoCActivity4.class));
                    //Toast.makeText(getApplicationContext(), "Mic mode", Toast.LENGTH_LONG).show();
                } else {
                    String message = textInputEditTextMessage.getText().toString();
//                    Toast.makeText(getApplicationContext(), "Message mode", Toast.LENGTH_LONG).show();
                    if (message.length() > 0) {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), usuarioLocal.toString(), Toast.LENGTH_LONG).show();
                        MensajeNube mensajeNube = new MensajeNube();
                        //mensajeNube.setIdMensaje();
                        //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                        mensajeNube.setContenido(message);
                        mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mensajeNube.setTo(usuarioTo.getIdUsuario());
                        mensajeNube.setEstadoLectura(false);
                        mensajeNube.setType(0);/*0 texto */
                        if (chat == null) {
                            String idChat = FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .push().getKey();
                            chat = new Chat();
                            chat.setIdChat(idChat);
                            chat.setMensajeNube(mensajeNube);

                            Participante participante1 = new Participante();
                            participante1.setIdParticipante(usuarioLocal.getIdUsuario());

                            participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());

                            participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

                            Participante participante2 = new Participante();
                            participante2.setIdParticipante(usuarioTo.getIdUsuario());

                            participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());

                            participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());

                            ArrayList<Participante> participanteArrayList = new ArrayList<>();
                            participanteArrayList.add(participante1);
                            participanteArrayList.add(participante2);

                            chat.setParticipantes(participanteArrayList);
                            mensajeNube.setIdChat(chat.getIdChat());
                            usuarioLocal.crearChat(chat, mensajeNube, this);

                        } else {
                            usuarioLocal.enviarMensaje(chat, mensajeNube, this);
                        }
                        //mensajeNube.setTimeStamp();
                    }


                }
                break;

            case R.id.buttonAttachFile:
//                Toast.makeText(getApplicationContext(), "Attach file", Toast.LENGTH_LONG).show();
//                alertDialogAttachFile().show();

                if (ContextCompat.checkSelfPermission(IndividualChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.

                    alertDialogAttachFile().show();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(IndividualChatActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.

                    Snackbar.make(fabMessageMic, "Permiso de almacenamiento necesario",
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            ActivityCompat.requestPermissions(IndividualChatActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    // You can directly ask for the permission.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }
                break;
            case R.id.buttonCamera:
//                Toast.makeText(getApplicationContext(), "Attach file", Toast.LENGTH_LONG).show();
                //system os is less then marshallow
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    openAlertDialogPhotoOptions();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    selectPhoto();
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        openAlertDialogPhotoOptions();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraPermission();
                    }
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        openAlertDialogPhotoOptions();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraAPILocaPermission();
                    }
                }
                break;
            case R.id.imageViewCall:
                Intent intent = new Intent(IndividualChatActivity.this, LlamadaVozActivity.class);
                intent.putExtra("usuarioTo", usuarioTo);
                intent.putExtra("usuarioFrom", usuarioLocal);
                intent.putExtra("callStatus", 0);
                startActivity(intent);
                break;
            case R.id.imageViewVideoCall:
//                Intent intentVideollamada = new Intent(IndividualChatActivity.this, VideoChatViewActivity.class);
                Intent intentVideollamada = new Intent(IndividualChatActivity.this, VideoLlamadaActivity.class);
                intentVideollamada.putExtra("usuarioTo", usuarioTo);
                intentVideollamada.putExtra("usuarioFrom", usuarioLocal);
                intentVideollamada.putExtra("callStatus", 0);
                startActivity(intentVideollamada);
                break;
        }
    }


    private void openAlertDialogPhotoOptions() {
//        // setup the alert builder
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//        builder.setTitle("Completar acción mediante:");
//
//// add a list
//        String[] animals = {"Galería de imágenes", "Tomar foto"};
//        builder.setItems(animals, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0: // horse
//                        escogerDesdeGaleria();
//                        break;
//                    case 1: // cow
//                        tomarfoto();
//                        break;
//                }
//            }
//        });
//
//// create and show the alert dialog
//        android.app.AlertDialog dialog = builder.create();
//        dialog.show();

        tomarfoto();
    }

    private void escogerDesdeGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }

    private void tomarfoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


                ContentResolver resolver = getApplicationContext()
                        .getContentResolver();

                Uri audioCollection;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    audioCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                } else {
                    audioCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                String displayName = "image." + System.currentTimeMillis() + ".jpeg";

                ContentValues newSongDetails = new ContentValues();
                newSongDetails.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis());
                newSongDetails.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis());
                newSongDetails.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                newSongDetails.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);


                Uri picUri = resolver.insert(audioCollection, newSongDetails);
                uriPhoto = picUri;

                Log.d("FotoUdir asda", uriPhoto.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_PERMISSION_FOTO_PERFIL);

            }


        } catch (Exception e) {
            Log.e(TAG, "ERRORRRRRRR!");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getLocalizedMessage());
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
//            startActivity(new Intent(getApplicationContext(), CamActivity.class));
        }
    }

    private void selectPhoto() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openAlertDialogPhotoOptions();
        } else {
            // Permission is missing and must be requested.
            requestCameraAndWExtStPermission();
        }
        // END_INCLUDE(startCamera)
    }

    private void requestCameraAndWExtStPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(IndividualChatActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(IndividualChatActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_ONLY);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
        }
    }

    private void requestCameraAPILocaPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(IndividualChatActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_LOCA);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                // Request for camera permission.
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case PERMISSION_REQUEST_CAMERA_ONLY:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                }
                break;

            case PERMISSION_REQUEST_CAMERA_LOCA:
                // Request for camera permission.
                if (grantResults.length >= 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                }
                break;
            case PERMISSION_REQUEST_RECORD_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
//                    findViewById(R.id.record_button).setVisibility(View.GONE);
//                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
//                    startRecording();

                    findViewById(R.id.messageContainer).setVisibility(View.GONE);
                    findViewById(R.id.linLayoutMicControls).setVisibility(View.VISIBLE);

                    findViewById(R.id.record_button).setVisibility(View.GONE);
                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                    startRecording();

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                break;
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    alertDialogAttachFile().show();

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                break;
        }


        // END_INCLUDE(onRequestPermissionsResult)
    }


    public void clearText() {
        textInputEditTextMessage.setText("");
        textInputEditTextMessage.clearComposingText();
        animationButtonMic(1);
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

            try {
                setUsuarioBloqueado("");
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

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
        try {
            removeChildMensajesListener();
        } catch (Exception e) {
            Log.e(TAG, e.toString());

        }
        try {
            mensajeNubeListAdapter.liberarRecursos();
        } catch (Exception e) {
            Log.e(TAG, e.toString());

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            removeChildMensajesListener();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        try {
            mensajeNubeListAdapter.liberarRecursos();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        try {
            setUsuarioBloqueado("");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }


    @Override
    public void onBackPressed() {
        try {
            setUsuarioBloqueado("");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_CODE && resultCode == RESULT_OK) {
//            Toast.makeText(getContext(), "aaaaaaaaaa", Toast.LENGTH_LONG).show();
            String resultMessage = data.getStringExtra("EXTRA_REPLY");
            String latitude = data.getStringExtra("EXTRA_REPLY_LATITUDE");
            String longitude = data.getStringExtra("EXTRA_REPLY_LONGITUDE");


            MensajeNube mensajeNube = new MensajeNube();
            //mensajeNube.setIdMensaje();
            //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                    mensajeNube.setContenido(geo);
            mensajeNube.setContenido(resultMessage);
            mensajeNube.setLatitude(Double.parseDouble(latitude));
            mensajeNube.setLongitude(Double.parseDouble(longitude));
            mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mensajeNube.setTo(usuarioTo.getIdUsuario());
            mensajeNube.setEstadoLectura(false);

            mensajeNube.setType(4);/*4 location */
            if (chat == null) {
                String idChat = FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .push().getKey();
                chat = new Chat();
                chat.setIdChat(idChat);
                chat.setMensajeNube(mensajeNube);

                Participante participante1 = new Participante();
                participante1.setIdParticipante(usuarioLocal.getIdUsuario());

                participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());

                participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

                Participante participante2 = new Participante();
                participante2.setIdParticipante(usuarioTo.getIdUsuario());
                participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());

                participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());

                ArrayList<Participante> participanteArrayList = new ArrayList<>();
                participanteArrayList.add(participante1);
                participanteArrayList.add(participante2);

                chat.setParticipantes(participanteArrayList);
                mensajeNube.setIdChat(chat.getIdChat());
                usuarioLocal.crearChat(chat, mensajeNube, this);

            } else {
                usuarioLocal.enviarMensaje(chat, mensajeNube, this);
            }

        }
        if (requestCode == SELECT_AUDIO && resultCode == RESULT_OK) {

            try {
                final Uri audioUri = data.getData();
                if (audioUri != null) {

//                    final File audio = new File(String.valueOf(audioUri));
//
//// Get uri related document id.
//                    String documentId = DocumentsContract.getDocumentId(audioUri);
//
//                    // Get uri authority.
//                    String uriAuthority = audioUri.getAuthority();
//
//
//                    String idArr[] = documentId.split(":");
//                    if (idArr.length == 2) {
//                        // First item is document type.
//                        String docType = idArr[0];
//
//                        // Second item is document real id.
//                        String realDocId = idArr[1];
//
//                        // Get content uri by document type.
//                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        if ("image".equals(docType)) {
//                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("video".equals(docType)) {
//                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("audio".equals(docType)) {
//                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                        }
//
//                        // Get where clause with real document id.
//                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;
//
//
//                        String ret = "";
//
//                        // Query the uri with condition.
//                        Cursor cursor = IndividualChatActivity.this.getContentResolver().query(audioUri, null, whereClause, null, null);

//                        if (cursor != null) {
//                            boolean moveToFirst = cursor.moveToFirst();
//                            if (moveToFirst) {
//
//                                // Get columns name by uri type.
//                                String columnName = MediaStore.Images.Media.DATA;
//
//                                if (audioUri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
//                                    columnName = MediaStore.Images.Media.DATA;
//                                } else if (audioUri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
//                                    columnName = MediaStore.Audio.Media.DATA;
//                                } else if (audioUri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
//                                    columnName = MediaStore.Video.Media.DATA;
//                                }
//
//                                // Get column index.
//                                int columnIndex = cursor.getColumnIndex(columnName);
//
//                                // Get column value which is the uri related file local path.
//                                ret = cursor.getString(columnIndex);
//                                Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                                Log.d(TAG, ret);
//
//                            }
//                        }
                }


//                    Log.d(TAG, audioUri.toString());
//                    Log.d(TAG, audio.getName());
//                    Log.d(TAG, audio.getPath());
//                    Log.d(TAG, audio.getAbsolutePath());
//                    String displayName = "";
//                    Cursor cursord = IndividualChatActivity.this.getContentResolver()
//                            .query(audioUri, null, null, null, null, null);

//                    try {
//                        // moveToFirst() returns false if the cursor has 0 rows. Very handy for
//                        // "if there's anything to look at, look at it" conditionals.
//                        if (cursord != null && cursord.moveToFirst()) {
//
//                            // Note it's called "Display Name". This is
//                            // provider-specific, and might not necessarily be the file name.
//                            displayName = cursord.getString(
//                                    cursord.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                            Log.i(TAG, "Display Name: " + displayName);
//
//                            int sizeIndex = cursord.getColumnIndex(OpenableColumns.SIZE);
//                            // If the size is unknown, the value stored is null. But because an
//                            // int can't be null, the behavior is implementation-specific,
//                            // and unpredictable. So as
//                            // a rule, check if it's null before assigning to an int. This will
//                            // happen often: The storage API allows for remote files, whose
//                            // size might not be locally known.
//                            String size = null;
//                            if (!cursord.isNull(sizeIndex)) {
//                                // Technically the column stores an int, but cursor.getString()
//                                // will do the conversion automatically.
//                                size = cursord.getString(sizeIndex);
//                            } else {
//                                size = "Unknown";
//                            }
//                            Log.i(TAG, "Size: " + size);
//                        }
//                    } finally {
//                        cursord.close();
//                    }
//
//                    File appSpecificExternalDir = new File(IndividualChatActivity.this.getExternalFilesDir(null), audio.getName());
//
//
//                    try {
//                        Log.d(TAG, "appSpecificExternalDir");
//                        Log.d(TAG, appSpecificExternalDir.getPath());
//                        Log.d(TAG, appSpecificExternalDir.getAbsolutePath());
//                        Log.d(TAG, appSpecificExternalDir.getParent() + "/" + displayName + ".mp3");
//                        Log.d(TAG, appSpecificExternalDir.getCanonicalPath());
//                        Log.d(TAG, appSpecificExternalDir.getName());
//
//                    } catch (Exception e) {
//                        Log.d(TAG, e.toString());
//                    }
//                    Log.d(TAG, fileName);
//            Log.d(TAG, audio.);
//                    Log.d(TAG, audio.getPath());
                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                    mensajeNube.setContenido(audioUri.toString());
//                    mensajeNube.setContenido(appSpecificExternalDir.getParent() + "/" + displayName + ".mp3");

                //Uri imageUriSend = Uri.fromFile(new File(mensajeNube.getContenido()));
                // Log.d(TAG, imageUriSend.toString());


                /*Filtro maldito*/
                if (isExternalStorageDocument(audioUri)) {
                    Log.d(TAG, "SIIIIIIIIIIIII");
                    final String docId = DocumentsContract.getDocumentId(audioUri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        String audioPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        mensajeNube.setContenido(audioPath);
                        Log.d(TAG, audioPath);
                    }
                } else {
                    if (isMediaDocument(audioUri)) {
                        Log.d(TAG, "isMediaDocument");
                        Log.d(TAG, audioUri.toString());
                        // Log.d(TAG, imageUriSend.toString());
                        mensajeNube.setContenido(audioUri.toString());


//                            try {
//                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                                Context context = getApplicationContext();
//
//                                String[] filePathColumn = {MediaStore.Audio.Media.DATA};
//                                Log.d(TAG, "locationMetadata: ");
//
//// Find the videos that are stored on a device by querying the video collection.
//                                try (Cursor cursor = context.getContentResolver().query(
//                                        audioUri,
//                                        filePathColumn,
//                                        null,
//                                        null,
//                                        null
//                                )) {
//                                    int idColumn = cursor.getColumnIndexOrThrow(filePathColumn[0]);
//                                    while (cursor.moveToNext()) {
//                                        long id = cursor.getLong(idColumn);
//                                        Uri videoUri = ContentUris.withAppendedId(
//                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
//                                        Log.e(TAG, "Video uri: " + videoUri.toString());
//
//                                        mensajeNube.setContenido(videoUri.toString());
////                                        extractVideoLocationInfo(videoUri);
//                                        try {
//                                            retriever.setDataSource(context, videoUri);
//                                        } catch (RuntimeException e) {
//                                            Log.e(TAG, "Cannot retrieve video file", e);
//                                        }
//                                        // Metadata should use a standardized format.
//                                        String locationMetadata = retriever.extractMetadata(
//                                                MediaMetadataRetriever.METADATA_KEY_LOCATION);
//                                        Log.d(TAG, "locationMetadata: " + locationMetadata);
//
//                                    }
//                                }
//
//
//                            } catch (Exception e) {
//                                Log.d(TAG, e.toString());
//                            }


//                            try {
//                                Log.d(TAG, "isMediaDocument");
//                                //mensajeNube.setContenido(audioUri.toString());
////                                Log.d(TAG, "isMediaDocument: " + getRealPath(IndividualChatActivity.this, audioUri));
//
//                                String path = null;
//                                String[] proj = {MediaStore.MediaColumns.DATA};
//
//                                Cursor cursor = getContentResolver().query(audioUri, proj, null, null, null);
//                                if (cursor.moveToFirst()) {
//                                    for (int i = 0; i < cursor.getColumnNames().length; i++) {
//                                        Log.d(TAG, "isMediaDocument: " + cursor.getColumnNames()[i]);
//
//                                    }
//                                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                                    path = cursor.getString(column_index);
//                                    Log.d(TAG, "isMediaDocument: " + path);
//
//                                }
//                                cursor.close();
//
//
//
//
//                                /*
//                                 * Get the file's content URI from the incoming Intent,
//                                 * then query the server app to get the file's display name
//                                 * and size.
//                                 */
//                                Uri returnUri = audioUri;
//                                Cursor returnCursor =
//                                        getContentResolver().query(returnUri, null, null, null, null);
//                                /*
//                                 * Get the column indexes of the data in the Cursor,
//                                 * move to the first row in the Cursor, get the data,
//                                 * and display it.
//                                 */
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                String[] filePathColumn = {MediaStore.Audio.Media.DATA};
//                                int dataIndex = returnCursor.getColumnIndex(filePathColumn[0]);
//                                returnCursor.moveToFirst();
//
//                                Log.d(TAG, "isMediaDocument: " + returnCursor.getString(nameIndex));
//                                Log.d(TAG, "isMediaDocument: " + Long.toString(returnCursor.getLong(sizeIndex)));
//                                for (int i = 0; i < returnCursor.getColumnNames().length; i++) {
//                                    Log.d(TAG, "isMediaDocument: " + returnCursor.getColumnNames()[i]);
//
//                                }
//
////                                String[] filePathColumn = {contentUri.toString()};
////                                Cursor cursor = IndividualChatActivity.this.getContentResolver().query(audioUri, filePathColumn, null, null, null);
////                                cursor.moveToFirst();
////                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                                String filePath = cursor.getString(columnIndex);
////                                cursor.close();
////                                Log.d(TAG, filePath);
////                                mensajeNube.setContenido(filePath);
////                                String audioPath = getDataColumn(IndividualChatActivity.this, contentUri, selection, selectionArgs);
////                                mensajeNube.setContenido(audioPath);
////                                Log.d(TAG, audioPath);
//                            } catch (Exception e) {
//                                Log.d(TAG, e.toString());
//                            }


                    } else {
                        try {
                            Log.d(TAG, "NOOOOOO isMediaDocument");
                            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                            Cursor cursor = IndividualChatActivity.this.getContentResolver().query(audioUri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            cursor.close();
                            Log.d(TAG, filePath);
                            mensajeNube.setContenido(filePath);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }


                }

//                    String[] filePathColumn = {MediaStore.Audio.Media.DATA};


//            mensajeNube.setContenido("/storage/emulated/0/Download/Billy-Ocean-Caribbean-Queen-No-More-Love-On-The-Run.mp3");
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioTo.getIdUsuario());
                mensajeNube.setEstadoLectura(false);

                Uri uri = audioUri;
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(getApplicationContext(), uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                mensajeNube.setAudioDuration(durationStr);
                mensajeNube.setType(2);/*0 audio */


                if (chat == null) {
                    String idChat = FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .push().getKey();
                    chat = new Chat();
                    chat.setIdChat(idChat);
                    chat.setMensajeNube(mensajeNube);

                    Participante participante1 = new Participante();
                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());

                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());

                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

                    Participante participante2 = new Participante();
                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());

                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());

                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
                    participanteArrayList.add(participante1);
                    participanteArrayList.add(participante2);

                    chat.setParticipantes(participanteArrayList);
                    mensajeNube.setIdChat(chat.getIdChat());
                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);

                } else {
                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
                }


            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }


        }
        if (requestCode == SELECCIONAR_FOTO_GALERIA_REQ_ID && resultCode == RESULT_OK) {

            //Toast.makeText(getApplicationContext(), "Enviando imagen dedse galeria", Toast.LENGTH_LONG).show();
            try {
                final Uri imageUri = data.getData();
                Log.d(TAG, imageUri.toString());

                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                mensajeNube.setContenido(getRealPath(IndividualChatActivity.this, imageUri));
                mensajeNube.setContenido(imageUri.toString());
//            mensajeNube.setContenido("/storage/emulated/0/Download/Billy-Ocean-Caribbean-Queen-No-More-Love-On-The-Run.mp3");
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioTo.getIdUsuario());
                mensajeNube.setEstadoLectura(false);
                mensajeNube.setType(1);/*1 imagen */


                if (chat == null) {
                    String idChat = FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .push().getKey();
                    chat = new Chat();
                    chat.setIdChat(idChat);
                    chat.setMensajeNube(mensajeNube);

                    Participante participante1 = new Participante();
                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());

                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

                    Participante participante2 = new Participante();
                    participante2.setIdParticipante(usuarioTo.getIdUsuario());

                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());

                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());

                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
                    participanteArrayList.add(participante1);
                    participanteArrayList.add(participante2);

                    chat.setParticipantes(participanteArrayList);
                    mensajeNube.setIdChat(chat.getIdChat());
                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);

                } else {
                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }


        }


        if (requestCode == REQUEST_CAMERA_PERMISSION_FOTO_PERFIL && resultCode == RESULT_OK) {
            //Toast.makeText(getApplicationContext(), "Enviando imagen", Toast.LENGTH_LONG).show();

            try {
//                final Uri imageUri = data.getData();
                Uri uriImageToSend = uriPhoto;
//                Glide.with(getApplicationContext()).load(uriPhoto).circleCrop().into(imageViewFoto);
//                Toast.makeText(getApplicationContext(),uriImageToSend.getPath(), Toast.LENGTH_SHORT).show();

                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                mensajeNube.setContenido(getRealPath(IndividualChatActivity.this, uriImageToSend));
                mensajeNube.setContenido(uriImageToSend.toString());
//            mensajeNube.setContenido("/storage/emulated/0/Download/Billy-Ocean-Caribbean-Queen-No-More-Love-On-The-Run.mp3");
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioTo.getIdUsuario());
                mensajeNube.setEstadoLectura(false);
                mensajeNube.setType(1);/*1 imagen */


                if (chat == null) {
                    String idChat = FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .push().getKey();
                    chat = new Chat();
                    chat.setIdChat(idChat);
                    chat.setMensajeNube(mensajeNube);

                    Participante participante1 = new Participante();
                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

                    Participante participante2 = new Participante();
                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());

                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());

                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
                    participanteArrayList.add(participante1);
                    participanteArrayList.add(participante2);

                    chat.setParticipantes(participanteArrayList);
                    mensajeNube.setIdChat(chat.getIdChat());
                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);

                } else {
                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        }

    }


    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crear_cita, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_crear_cita:
                Intent intent = new Intent(IndividualChatActivity.this, CitaTrabajoActivity.class);
                intent.putExtra("usuarioFrom", usuarioLocal);
                intent.putExtra("usuarioTo", usuarioTo);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItemO = menu.findItem(R.id.mnu_crear_cita);
        menuItemO.setVisible(false);
        switch (usuario) {
            case 0:
            case 1:
                menuItemO.setVisible(false);
                break;
            case 2:
                menuItemO.setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

}