package com.marlon.apolo.tfinal2022.llamadaVoz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.TokenAsyncTask;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.lang.ref.WeakReference;
import java.util.Locale;


import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;


public class LlamadaVozActivity extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final String LOG_TAG = LlamadaVozActivity.class.getSimpleName();
    private static final String TAG = LlamadaVozActivity.class.getSimpleName();
    private RtcEngine mRtcEngine; // Tutorial Step 1
    private String localToken;
    private int uidLocal;
    private String channelNameShare;
    private TextView textViewLlamando;
    private MediaPlayer mediaPlayerTonoLlamada;
    private Usuario usuarioFrom;
    private Usuario usuarioTo;
    private int callStatus;

    private ChildEventListener childEventListenerLlamada;
    private LlamadaVoz llamadaVozActual;
    //    private TimerAsc timerAsc;
    private ImageView imageViewMic;
    private ImageView imageViewSpeaker;
    private ImageView imageViewCall;
    private ImageView imageViewStarCall;
    private boolean contestar;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //onRemoteUserLeft(uid, reason);
                }
            });
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         *     true: Muted.
         *     false: Unmuted.
         */
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };
    private Usuario usuarioLocal;


    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
//        joinChannel();               // Tutorial Step 2


//        channelNameShare = FirebaseDatabase.getInstance().getReference()
//                .child("LlamdasDeVoz")
//                .push().getKey();
//        channelNameShare = "channelTesting";
        switch (callStatus) {
            case 0:
                //emisor
                channelNameShare = FirebaseDatabase.getInstance().getReference()
                        .child("llamadasDeVoz")
                        .push().getKey();
                textViewLlamando.setText("Conectando...");
                //reproducirSonidoDeLLamada();
                break;
            case 1:
                textViewLlamando.setText("Conectando...");
                break;

        }
        TokenAsyncTask tokenAsyncTask = new TokenAsyncTask(channelNameShare);
        tokenAsyncTask.execute();
        tokenAsyncTask.setOnItemClickListener(new TokenAsyncTask.ClickListener() {
            @Override
            public void onTokenListener(String token, int uid) {

                if (token.length() > 0) {
                    localToken = token;
                    uidLocal = uid;
                    try {
                        joinChannel();
                    } catch (Exception e) {

                    }
//                    Toast.makeText(getApplicationContext(), localToken, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
//        String accessToken = getString(R.string.agora_access_token);
        String accessToken = localToken;
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        // Sets the channel profile of the Agora RtcEngine.
        // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
        // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        // Allows a user to join a channel.
        //
        // Toast.makeText(getApplicationContext(), "Join channel", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), accessToken, Toast.LENGTH_LONG).show();
        //mRtcEngine.joinChannel(accessToken, "channelTest", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you


        switch (callStatus) {
            case 0:
                //emisor
                textViewLlamando.setText("Llamando...");
                setListenerLlamadaActual();
                reproducirSonidoDeLLamada();
//                usuarioFrom.realizarllamadaDeVoz(usuarioTo, uidLocal, mRtcEngine, accessToken);
                usuarioFrom.realizarllamadaDeVoz(usuarioTo, uidLocal, mRtcEngine, localToken, channelNameShare);
                break;
            case 1:
                if (contestar) {
                    stopMediaPlayer();
                    //setListenerLlamadaActual();
                    Usuario usuarioDestiny = new Trabajador();
                    textViewLlamando.setText("Comunicación establecida");
//                    textViewLlamando.setText("00:00");
                    //timerAsc = new TimerAsc(textViewLlamando);
                    //timerAsc.execute();
                    imageViewStarCall.setVisibility(View.GONE);
                    usuarioDestiny.constestarLlamadaDeVoz(channelNameShare, mRtcEngine, accessToken, channelNameShare, uidLocal);

//                    usuarioDestiny.contestar(usuarioTo, uidLocal, mRtcEngine, localToken, channelNameShare);

                }
                break;

        }

//        mRtcEngine.joinChannel(accessToken, channelNameShare, "Extra Optional Data", uidLocal); // if you do not specify the uid, we will generate the uid for you
    }

    private void setListenerLlamadaActual() {

        llamadaVozActual = new LlamadaVoz();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    LlamadaVoz llamadaVoz = snapshot.getValue(LlamadaVoz.class);
                    llamadaVozActual = llamadaVoz;
                    /*usuario From*/
                    if (llamadaVoz.getParticipanteCaller().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        if (llamadaVozActual.isDestinyStatus() && llamadaVozActual.isChannelConnectedStatus()) {
                            switch (callStatus) {
                                case 0:
                                    /*Contestando*/
                                    textViewLlamando.setText("Comunicación establecida");
                                    //customAsyncTask = new CustomAsyncTask(getApplicationContext());
                                    //customAsyncTask.execute();
                                    //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
                                    //timerAsc = new TimerAsc(textViewLlamando);
                                    //timerAsc.execute();
//                                new TimerAsc(textViewLlamando).execute();
                                    stopMediaPlayer();
                                    break;
                            }
                        }


                        if (llamadaVozActual.isRejectCallStatus()) {
                            switch (callStatus) {
                                case 0:
                                    textViewLlamando.setText("Llamada rechazada");
                                    //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
                                    //timerAsc = new TimerAsc(textViewLlamando);
                                    //timerAsc.execute();
//                                new TimerAsc(textViewLlamando).execute();
                                    stopMediaPlayer();
                                    blockingControls();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 3000);
                                    break;
                            }
                        }

                        if (llamadaVozActual.isFinishCall()) {
                            switch (callStatus) {
                                case 0:
                                    try {
                                        //timerAsc.cancel(true);
                                    } catch (Exception e) {

                                    }
                                    //usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                                    textViewLlamando.setText("Llamada finalizada");
                                    //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
                                    //timerAsc = new TimerAsc(textViewLlamando);
                                    //timerAsc.execute();
//                                new TimerAsc(textViewLlamando).execute();
                                    liberarRecursos();
                                    stopMediaPlayer();
                                    blockingControls();


                                    //customAsyncTask.cancel(true);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                    break;
                            }
                        }
                    }


                    if (llamadaVoz.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        if (!llamadaVozActual.isCallerStatus()) {
//                            switch (callStatus) {
//                                case 1:
//                                    /*Contestando*/
//                                    textViewLlamando.setText("00:00");
//                                    //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
//                                    timerAsc = new TimerAsc(textViewLlamando);
//                                    timerAsc.execute();
////                                new TimerAsc(textViewLlamando).execute();
//                                    stopMediaPlayer();
//                                    break;
//                            }
//                        }

                        if (llamadaVozActual.isFinishCall()) {
                            switch (callStatus) {
                                case 1:
                                    try {
                                        //timerAsc.cancel(true);
                                    } catch (Exception e) {

                                    }
                                    usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                                    textViewLlamando.setText("Llamada finalizada");
                                    //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
                                    //timerAsc = new TimerAsc(textViewLlamando);
                                    //timerAsc.execute();
//                                new TimerAsc(textViewLlamando).execute();
                                    stopMediaPlayer();
                                    blockingControls();
                                    liberarRecursos();


                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                    break;
                            }
                        }
                    }


//                    if (llamadaVozActual.isChannelConnectedStatus()) {
//                        switch (callStatus) {
//                            case 0:
//                                textViewLlamando.setText("00:00");
//                                //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
//                                //timerAsc = new TimerAsc(textViewLlamando);
//                                //timerAsc.execute();
////                                new TimerAsc(textViewLlamando).execute();
//                                stopMediaPlayer();
//                                break;
//                        }
//                    }
                    /*usuario To*/
                    /*if (llamadaVozActual.isChannelConnectedStatus()) {
                        switch (callStatus) {
                            case 0:
                                textViewLlamando.setText("00:00");
                                //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
                                //timerAsc = new TimerAsc(textViewLlamando);
                                //timerAsc.execute();
//                                new TimerAsc(textViewLlamando).execute();
                                stopMediaPlayer();
                                break;
                        }
                    }*/

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .child(channelNameShare)
                .addValueEventListener(valueEventListener);
    }

    private void liberarRecursos() {

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;

        try {
            if (mediaPlayerTonoLlamada != null) {
                mediaPlayerTonoLlamada.release();
            }
        } catch (Exception e) {

        }


        switch (callStatus) {
            case 0:
                try {
                    usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                } catch (Exception e) {

                }
                break;
            case 1:
                break;
        }
        textViewLlamando.setText("Finalizando llamada...");


        try {
            //timerAsc.cancel(true);
        } catch (Exception e) {

        }
    }

    private void blockingControls() {
        imageViewMic.setEnabled(false);
        imageViewSpeaker.setEnabled(false);
        imageViewCall.setEnabled(false);
    }

    private void reproducirSonidoDeLLamada() {
//        mediaPlayerTonoLlamada = new MediaPlayer();
        mediaPlayerTonoLlamada = MediaPlayer.create(LlamadaVozActivity.this, R.raw.dialtone2);
        mediaPlayerTonoLlamada.setLooping(true);
        mediaPlayerTonoLlamada.start();
    }

    public void stopMediaPlayer() {
        try {
            if (mediaPlayerTonoLlamada != null) {
                mediaPlayerTonoLlamada.stop();
                mediaPlayerTonoLlamada.release();
            }
        } catch (Exception e) {

        }

    }


    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.teal_700), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Tutorial Step 3
    public void onEndCallClicked(View view) {

//        Toast.makeText(getApplicationContext(), "Finalizando", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "Finalizando" + llamadaVozActual, Toast.LENGTH_LONG).show();
        switch (callStatus) {
            case 0:
                textViewLlamando.setText("Finalizando llamada...");
                try {
                    if (mediaPlayerTonoLlamada != null) {
                        mediaPlayerTonoLlamada.stop();
                        mediaPlayerTonoLlamada.release();
                    }
                } catch (Exception e) {

                }

                try {
//                    usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                    usuarioFrom.finalizarLlamadaDeVoz(channelNameShare);

                } catch (Exception e) {

                }
                finish();
                break;
            case 1:
                if (channelNameShare != null) {
                    try {
                        if (contestar) {
                            usuarioFrom.finalizarLlamadaDeVoz(channelNameShare);
                        } else {
                            usuarioFrom.rechazarLlamadaDeVoz(channelNameShare);
                        }
                    } catch (Exception e) {

                    }
                    finish();
                }
                break;
        }

    }

    // Tutorial Step 3
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    private void loadLocalUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ValueEventListener valueEventListenerUserLocal = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Administrador administrador = snapshot.getValue(Administrador.class);
                    if (administrador != null) {
                        usuarioFrom = administrador;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    if (empleador != null) {
                        usuarioFrom = empleador;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    if (trabajador != null) {
                        usuarioFrom = trabajador;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamada_voz);
        TextView textView = findViewById(R.id.textViewReceptor);
        textViewLlamando = findViewById(R.id.textViewLlamando);
        ImageView imageView = findViewById(R.id.imageViewReceptor);
        contestar = false;

        imageViewMic = findViewById(R.id.imageViewMic);
        imageViewSpeaker = findViewById(R.id.imageViewSpeaker);
        imageViewCall = findViewById(R.id.imageViewCall);
        imageViewStarCall = findViewById(R.id.imageViewStartCall);
        imageViewStarCall.setVisibility(View.GONE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        loadLocalUser();


        usuarioTo = (Usuario) getIntent().getSerializableExtra("usuarioTo");
        usuarioFrom = (Usuario) getIntent().getSerializableExtra("usuarioFrom");
        callStatus = getIntent().getIntExtra("callStatus", -1);


        switch (callStatus) {
            case 0:
                /*llamada saliente*/
                textViewLlamando.setText("Conectando...");
                textView.setText(String.format("%s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
                if (usuarioTo.getFotoPerfil() != null) {
                    Glide.with(this).load(usuarioTo.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageView);
                }

                break;
            case 1:
                /*llamada entrante*/
                imageViewCall.setVisibility(View.VISIBLE);
                imageViewMic.setVisibility(View.GONE);
                imageViewSpeaker.setVisibility(View.GONE);
                imageViewStarCall.setVisibility(View.VISIBLE);
                imageViewStarCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageViewCall.setVisibility(View.VISIBLE);
                        imageViewMic.setVisibility(View.VISIBLE);
                        imageViewSpeaker.setVisibility(View.VISIBLE);
                        contestar = true;
                        joinChannel();
                    }
                });
                textViewLlamando.setText("Llamada entrante...");
                channelNameShare = getIntent().getStringExtra("channelNameShare");
                llamadaVozActual = (LlamadaVoz) getIntent().getSerializableExtra("llamadaVoz");
                textView.setText(String.format("%s", llamadaVozActual.getParticipanteCaller().getNombreParticipante()));
                if (llamadaVozActual.getParticipanteCaller().getUriFotoParticipante() != null) {
                    Glide.with(this).load(llamadaVozActual.getParticipanteCaller().getUriFotoParticipante()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageView);
                }
                contestar = getIntent().getBooleanExtra("contestar", false);
                if (contestar) {
                    imageViewStarCall.setVisibility(View.GONE);
                    imageViewCall.setVisibility(View.VISIBLE);
                    imageViewMic.setVisibility(View.VISIBLE);
                    imageViewSpeaker.setVisibility(View.VISIBLE);
                    contestar = true;
                }
                setListenerLlamadaActual();

//                configControls();
                //if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                //  initAgoraEngineAndJoinChannel();
                //}
                /*textView.setText(String.format("%s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
                if (usuarioTo.getFotoPerfil() != null) {
                    Glide.with(this).load(usuarioTo.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageView);
                }*/
                break;
            case 2:
//                textViewLlamando.setText("Conectando");
//                channelNameShare = getIntent().getStringExtra("channelNameShare");
//                contestar = getIntent().getBooleanExtra("contestar", false);
//                if (contestar) {
//                    imageViewStarCall.setVisibility(View.GONE);
//                    imageViewCall.setVisibility(View.VISIBLE);
//                    imageViewMic.setVisibility(View.VISIBLE);
//                    imageViewSpeaker.setVisibility(View.VISIBLE);
//                    contestar = true;
//                }
//                setListenerLlamadaActual();

                //if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                //  initAgoraEngineAndJoinChannel();
                //}
                /*textView.setText(String.format("%s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
                if (usuarioTo.getFotoPerfil() != null) {
                    Glide.with(this).load(usuarioTo.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageView);
                }*/
                break;
        }


        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }


        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    5000);
        } else {
            initAgoraEngineAndJoinChannel();
        }*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mediaPlayerTonoLlamada != null) {
                mediaPlayerTonoLlamada.stop();
                mediaPlayerTonoLlamada.release();
            }
        } catch (Exception e) {

        }

        try {
            if (!contestar) {
                usuarioFrom.rechazarLlamadaDeVoz(llamadaVozActual.getId());
            }
        } catch (Exception e) {

        }


        switch (callStatus) {
            case 0:

                break;
            case 1:
                if (channelNameShare != null) {
                    try {
                        if (contestar) {
                            usuarioFrom.finalizarLlamadaDeVoz(channelNameShare);
                        } else {
                            usuarioFrom.rechazarLlamadaDeVoz(channelNameShare);
                        }
                    } catch (Exception e) {

                    }
                    finish();
                }
                break;
        }


        textViewLlamando.setText("Finalizando llamada...");

    }


//    private class TimerAsc extends AsyncTask<String, Integer, String> {
//
//
//        private WeakReference<TextView> mTextView;
//        private int minutes;
//
//        public TimerAsc(TextView tv) {
//            mTextView = new WeakReference<>(tv);
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
////            for (int i = 0; i <= 59; i++) {
////                if (isCancelled()) {
////                    break;
////                }
//
//            int seconds = 0;
//            minutes = 0;
//            while (true) {
//                seconds = 0;
//                while (seconds <= 60) {
////                mTextView.get().setText(String.format("%s s", pStatus));
////                    if (!isCancelled())
////                        publishProgress(seconds, minutes);
//                    try {
//                        Thread.sleep(1000);
//                        if (!isCancelled())
//                            publishProgress(seconds, minutes);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    seconds++;
//                }
//                if (isCancelled()) {
//                    textViewLlamando.setText("Llamada finalizada");
//                    break;
//                }
//                minutes++;
//            }
//
////                publishProgress(i);
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//
//            return "Llamada finalizada";
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
////            mTextView.get().setText(String.format("%d", values));
//            if (!isCancelled()) {
//                mTextView.get().setText(String.format(Locale.US, "%d:%02d", values[1], values[0]));
//            } else {
//                textViewLlamando.setText("Llamada finalizada");
//            }
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String aLong) {
//            super.onPostExecute(aLong);
//            mTextView.get().setText(String.format("%s", aLong));
//        }
//
//        @Override
//        protected void onCancelled() {
//            textViewLlamando.setText("Llamada finalizada");
//        }
//
//        //        protected void onProgressUpdate(Integer... progress) {
////            setProgressPercent(progress[0]);
////        }
////
////        protected void onPostExecute(Long result) {
////            showDialog("Downloaded " + result + " bytes");
////        }
//    }


//    private static class CustomAsyncTask extends AsyncTask<Boolean, Integer, String> {
//
//        private WeakReference<TextView> mTextView;
//
//        public CustomAsyncTask(TextView textView) {
//            mTextView = new WeakReference<>(textView);
//        }
//
//        @Override
//        protected void onCancelled() {
//            mTextView.get().setText("Llamada finalizada");
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            if (!isCancelled()) {
//                mTextView.get().setText(String.format(Locale.US, "%d:%02d", values[0], values[0]));
//            }
//
//        }
//
//        @Override
//        protected String doInBackground(Boolean... params) {
//            int count = 0;
//            boolean exit = true;
//
//            while (count <= 10) {
//                if (isCancelled()) {
//                    count = 10;
//                    break;
//                } else {
//                    publishProgress(count);
//                }
//
//                try {
//                    Thread.sleep(1000);
//                    Log.d(CustomAsyncTask.class.getCanonicalName(), "aaaaaadoInBackground");
//                } catch (Exception exc) {
//                    Log.e(CustomAsyncTask.class.getCanonicalName(), "exception");
//                    count = 0;
//                }
//                count++;
//            }
//
////            while (exit) {
////                if (!isCancelled()) {
////                    publishProgress(count);
////                    count++;
////                } else {
////                    //Log.d(CustomAsyncTask.class.getCanonicalName(), "doInBackground is cancelled.");
////                    //cancel(true);
////                    exit = false;
////                    break;
////                }
////                try {
////                    Thread.sleep(1000);
////                    Log.d(CustomAsyncTask.class.getCanonicalName(), "doInBackground");
//////                    if (!isCancelled()) {
//////                        publishProgress(count);
//////                        count++;
//////                    } else {
////////                        Log.d(CustomAsyncTask.class.getCanonicalName(), "doInBackground is cancelled.");
////////                        cancel(true);
////////                        break;
//////                    }
////
////                } catch (Exception exc) {
////                    Log.e(CustomAsyncTask.class.getCanonicalName(), "exception");
////                    count = 0;
////                }
////            }
//
//            return "Llamada finalizada";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (!isCancelled()) {
//                mTextView.get().setText("Llamada finalizada");
//            } else {
//                Log.d(CustomAsyncTask.class.getCanonicalName(), "onPostExecute is cancelled.");
//            }
//        }
//    }


    //pressing the home button while the task is running will trigger the onStop being called.
    @Override
    protected void onStop() {
        try {
            //if (customAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            //customAsyncTask.cancel(true);
            //}
        } catch (Exception e) {

        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            leaveChannel();

        } catch (Exception e) {

        }
        RtcEngine.destroy();
        mRtcEngine = null;

        if (mediaPlayerTonoLlamada != null) {
            mediaPlayerTonoLlamada.release();
        }

        switch (callStatus) {
            case 0:
                try {
                    usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                } catch (Exception e) {

                }
                break;
            case 1:
                break;
        }
        textViewLlamando.setText("Finalizando llamada...");


        try {
            //timerAsc.cancel(true);
        } catch (Exception e) {

        }
    }


}