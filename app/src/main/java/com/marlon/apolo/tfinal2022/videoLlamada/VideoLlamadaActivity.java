package com.marlon.apolo.tfinal2022.videoLlamada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.TokenAsyncTask;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.model.VideoLlamada;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoLlamadaActivity extends AppCompatActivity {

    private static final String TAG = VideoLlamadaActivity.class.getSimpleName();
    private Usuario usuarioTo;


    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;


    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLogView.logI("Join channel success, uid: " + (uid & 0xFFFFFFFFL));
//                    Toast.makeText(getApplicationContext(), "Join channel success, uid: " + (uid & 0xFFFFFFFFL), Toast.LENGTH_LONG).show();

                }
            });
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLogView.logI("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
//                    Toast.makeText(getApplicationContext(), "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL), Toast.LENGTH_LONG).show();
                    setupRemoteVideo(uid);
                }
            });
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft(uid);
                }
            });
        }
    };
    private String localToken;
    private int uidLocal;
    private String channelNameShare;
    private Usuario usuarioFrom;
    private int callStatus;
    private TextView textViewParticipant;
    private TextView textViewLlamando;
    //    private LlamadaVoz llamadaVozActual;
    private Boolean contestar;
    private int contest;
    private LlamadaVideo llamadaVideo;
    private LlamadaVideo llamadaVideoEntrante;

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        if (mRemoteVideo != null) {
            return;
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
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
        setContentView(R.layout.activity_video_llamada);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        contestar = false;
        contest = -1;
        initUI();
//        loadLocalUser();

        usuarioTo = (Usuario) getIntent().getSerializableExtra("usuarioTo");
        usuarioFrom = (Usuario) getIntent().getSerializableExtra("usuarioFrom");
        callStatus = getIntent().getIntExtra("callStatus", -1);
        contest = getIntent().getIntExtra("contest", -1);
        llamadaVideoEntrante = (LlamadaVideo) getIntent().getSerializableExtra("llamadaVideo");
        channelNameShare = getIntent().getStringExtra("channelNameShare");
        contestar = getIntent().getBooleanExtra("contestar", false);


        textViewLlamando.setText("");
        textViewParticipant.setText("");
//        notifyIntent.putExtra("callStatus", 1);
//        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
//        notifyIntent.putExtra("usuarioTo", usuarioTo);
//        notifyIntent.putExtra("llamadaVideo", llamadaVoz1);
//
        try {
//            Toast.makeText(getApplicationContext(), String.valueOf(llamadaVideoEntrante), Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), String.valueOf(contest), Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), String.valueOf(callStatus), Toast.LENGTH_LONG).show();

        } catch (Exception e) {

        }

        if (usuarioTo != null) {
            switch (callStatus) {
                case 0:
                    channelNameShare = FirebaseDatabase.getInstance().getReference()
                            .child("llamadasDeVideo")
                            .push().getKey();
//                    Toast.makeText(getApplicationContext(), "Llamando", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), usuarioTo.toString(), Toast.LENGTH_LONG).show();
                    textViewLlamando.setText("Conectando...");
                    textViewParticipant.setText(String.format("%s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
                    break;
                case 1:
                    Log.d(TAG, "################################");
                    Log.d(TAG, "llamada entrante");
                    Log.d(TAG, "################################");
                    Log.d(TAG, "################################");

                    Log.d(TAG, "################################");
                    Log.d(TAG, String.valueOf(contest));
                    Log.d(TAG, "################################");
                    mCallEnd = true;

                    textViewLlamando.setText("Llamada entrante...");

                    textViewParticipant.setText(llamadaVideoEntrante.getParticipanteCaller().getNombreParticipante());

                    if (contest == 1) {/*contestar*/
                        textViewLlamando.setText("Llamada entrante...");
                        textViewParticipant.setText(llamadaVideoEntrante.getParticipanteCaller().getNombreParticipante());
                    }


                    break;
            }

        } else {
            if (callStatus == 1) {
                Log.d(TAG, "################################");
                Log.d(TAG, "llamada entrante");
                Log.d(TAG, "################################");
                Log.d(TAG, "################################");


                Log.d(TAG, "################################");
                Log.d(TAG, String.valueOf(contest));
                Log.d(TAG, "################################");

//                mCallEnd = true;

                switch (contest) {
                    case 1:/*contestar*/
                        textViewLlamando.setText("Llamada entrante...");
                        //Toast.makeText(getApplicationContext(), "AAAAAAAAAAAAAAAAAAAAAAAAAAAA", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), llamadaVideoEntrante.toString(), Toast.LENGTH_LONG).show();

                        textViewParticipant.setText(llamadaVideoEntrante.getParticipanteCaller().getNombreParticipante());
                        mCallEnd = true;

                        break;
                    case 0:
//                        Toast.makeText(getApplicationContext(), "Establciendo comunicación...", Toast.LENGTH_LONG).show();

                        textViewLlamando.setText("Establciendo comunicación...");
                        //Toast.makeText(getApplicationContext(), "AAAAAAAAAAAAAAAAAAAAAAAAAAAA", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), llamadaVideoEntrante.toString(), Toast.LENGTH_LONG).show();

                        textViewParticipant.setText(llamadaVideoEntrante.getParticipanteCaller().getNombreParticipante());
                        mCallEnd = false;
                        contestar = true;


                        break;
                }
            }

        }
        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }



//        if (ContextCompat.checkSelfPermission(
//                CONTEXT, Manifest.permission.REQUESTED_PERMISSION) ==
//                PackageManager.PERMISSION_GRANTED) {
//            // You can use the API that requires the permission.
//            performAction(...);
//        } else if (shouldShowRequestPermissionRationale(...)){
//            // In an educational UI, explain to the user why your app requires this
//            // permission for a specific feature to behave as expected. In this UI,
//            // include a "cancel" or "no thanks" button that allows the user to
//            // continue using your app without granting the permission.
//            showInContextUI(...);
//        } else{
//            // You can directly ask for the permission.
//            requestPermissions(CONTEXT,
//                    new String[]{Manifest.permission.REQUESTED_PERMISSION},
//                    REQUEST_CODE);
//        }


    }


    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);
        textViewParticipant = findViewById(R.id.textViewNameParticipant);
        textViewLlamando = findViewById(R.id.textViewLlamando);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

//        mLogView = findViewById(R.id.log_recycler_view);

        // Sample logs are optional.
        showSampleLogs();
    }

    private void showSampleLogs() {
//        mLogView.logI("Welcome to Agora 1v1 video call");
//        mLogView.logW("You will see custom logs here");
//        mLogView.logE("You can also use this to show errors");
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {
//                showLongToast("Need X permissions " + Manifest.permission.RECORD_AUDIO +
//                        "/" + Manifest.permission.CAMERA);
                showLongToast("Los permisos de acceso a su cámara o micrófono no han sido concedidos.");
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();


        TokenAsyncTask tokenAsyncTask = new TokenAsyncTask(channelNameShare);
        tokenAsyncTask.execute();
        tokenAsyncTask.setOnItemClickListener(new TokenAsyncTask.ClickListener() {
            @Override
            public void onTokenListener(String token, int uid) {

                if (token.length() > 0) {
                    localToken = token;
                    uidLocal = uid;
//                    Toast.makeText(getApplicationContext(), localToken, Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), String.valueOf(uidLocal), Toast.LENGTH_LONG).show();
                    try {
                        joinChannel();
                    } catch (Exception e) {

                    }
//                    Toast.makeText(getApplicationContext(), localToken, Toast.LENGTH_LONG).show();
                }
            }
        });
//        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            Log.d(TAG, "initializeEngine");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        Log.d(TAG, "setupVideoConfig");

        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        Log.d(TAG, "setupVideoConfig");

        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
//        String token = getString(R.string.agora_access_token);
        String token = localToken;
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
//        Toast.makeText(getApplicationContext(), "join channel", Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), channelNameShare, Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), String.valueOf(uidLocal), Toast.LENGTH_LONG).show();
        switch (callStatus) {
            case 0:
                textViewLlamando.setText("Llamando...");
//                mRtcEngine.joinChannel(token, channelNameShare, "Extra Optional Data", uidLocal);
                setListenerLlamadaActual();
                reproducirSonidoDeLLamada();
//                usuarioFrom.realizarllamadaDeVoz(usuarioTo, uidLocal, mRtcEngine, accessToken);
                usuarioFrom.realizarllamadaDeVideo(usuarioTo, uidLocal, mRtcEngine, localToken, channelNameShare);
                break;
            case 1:
                Log.d(TAG, "####################################");
                Log.d(TAG, String.valueOf(contestar));
                Log.d(TAG, "####################################");
//                usuarioFrom.constestarLlamadaDeVideo(llamadaVideo.getId(), mRtcEngine, localToken, channelNameShare, uidLocal);
//                usuarioFrom.constestarLlamadaDeVideo(llamadaVideo.getId(), mRtcEngine, localToken, channelNameShare, uidLocal);

//                Toast.makeText(getApplicationContext(), localToken, Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), String.valueOf(uidLocal), Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "Contest: " + String.valueOf(contest), Toast.LENGTH_LONG).show();
//                usuarioFrom.constestarLlamadaDeVideo(channelNameShare, mRtcEngine, localToken, channelNameShare, uidLocal);
//                usuarioFrom.constestarLlamadaDeVideo(llamadaVideo.getId(), mRtcEngine, localToken, channelNameShare, uidLocal);

                switch (contest) {
                    case 0:/*contestar*/
//                        usuarioFrom.constestarLlamadaDeVideo(llamadaVideo.getId(), mRtcEngine, localToken, channelNameShare, uidLocal);
                        //joinChannel();
                        //Toast.makeText(getApplicationContext(), String.valueOf(usuarioFrom), Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), "Constestando", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), channelNameShare, Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), llamadaVideoEntrante.getId(), Toast.LENGTH_LONG).show();

                        setListenerLlamadaActual();
                        contestar = true;

                        usuarioFrom.constestarLlamadaDeVideo(llamadaVideoEntrante.getId(), mRtcEngine, localToken, channelNameShare, uidLocal);
//                        usuarioFrom.constestarLlamadaDeVideo(channelNameShare, mRtcEngine, localToken, channelNameShare, uidLocal);
                        break;
                    case 1:

                        mCallBtn.setImageResource(R.drawable.btn_startcall_normal);

//                        if (mCallEnd) {
//                            startCall();
//                            mCallEnd = false;
//                            mCallBtn.setImageResource(R.drawable.btn_endcall_normal);
//                        } else {
//                            endCall();
//                            mCallEnd = true;
//                            mCallBtn.setImageResource(R.drawable.btn_startcall_normal);
//                        }

//                        showButtons(!mCallEnd);

                        break;
                }
//                if (contestar) {
//                    usuarioFrom.constestarLlamadaDeVideo(channelNameShare, mRtcEngine, localToken, channelNameShare, uidLocal);
//                } else {
//
//                }
                break;
        }
//        mRtcEngine.joinChannel(token, channelNameShare, "Extra Optional Data", uidLocal);
//        mRtcEngine.joinChannel(token, "demoChannel1", "Extra Optional Data", 0);
    }

    private MediaPlayer mediaPlayerTonoLlamada;

    private void reproducirSonidoDeLLamada() {
        mediaPlayerTonoLlamada = new MediaPlayer();
        mediaPlayerTonoLlamada = MediaPlayer.create(VideoLlamadaActivity.this, R.raw.dialtone2);
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

    private void setListenerLlamadaActual() {
        llamadaVideo = new LlamadaVideo();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    LlamadaVideo llamadaVideoDB = snapshot.getValue(LlamadaVideo.class);
                    llamadaVideo = llamadaVideoDB;
                    /*usuario From*/
                    if (llamadaVideo.getParticipanteCaller().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        if (llamadaVideo.isDestinyStatus() && llamadaVideo.isChannelConnectedStatus()) {
                            switch (callStatus) {
                                case 0:
                                    /*Contestando*/
                                    textViewLlamando.setText("Comunicación establecida");
                                    stopMediaPlayer();
                                    break;
                            }
                        }

//
                        if (llamadaVideo.isRejectCallStatus()) {
                            switch (callStatus) {
                                case 0:
                                    textViewLlamando.setText("Llamada rechazada");

                                    stopMediaPlayer();
//                                    blockingControls();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 3000);
                                    break;
                            }
                        }
//
                        if (llamadaVideo.isFinishCall()) {
                            switch (callStatus) {
                                case 0:
                                    try {
                                        //timerAsc.cancel(true);
                                    } catch (Exception e) {

                                    }
                                    //usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                                    textViewLlamando.setText("Llamada finalizada");

//                                    liberarRecursos();
                                    stopMediaPlayer();
//                                    blockingControls();


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


                    if (llamadaVideo.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                        if (llamadaVideo.isFinishCall()) {
                            switch (callStatus) {
                                case 1:
                                    try {
                                        //timerAsc.cancel(true);
                                    } catch (Exception e) {

                                    }
                                    usuarioFrom.cancelarLlamadaDeVideo(channelNameShare);
                                    textViewLlamando.setText("Llamada finalizada");
                                    //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_LONG).show();
                                    //timerAsc = new TimerAsc(textViewLlamando);
                                    //timerAsc.execute();
//                                new TimerAsc(textViewLlamando).execute();
                                    stopMediaPlayer();
//                                    blockingControls();
//                                    liberarRecursos();


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


                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .child(channelNameShare)
                .addValueEventListener(valueEventListener);
    }


    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute_normal;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall_normal);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall_normal);
        }

        showButtons(!mCallEnd);
    }

    private void startCall() {
        switch (callStatus) {
            case 0:
                break;
            case 1:
                contest = 0;
                break;
        }
        setupLocalVideo();
        joinChannel();

    }

    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();

        switch (callStatus) {
            case 0:
                textViewLlamando.setText("Finalizando llamada...");
                try {
                    usuarioFrom.finalizarLlamadaDeVideo(channelNameShare);
//                    usuarioFrom.cancelarLlamadaDeVoz(channelNameShare);
                    try {
                        if (mediaPlayerTonoLlamada != null) {
                            if (mediaPlayerTonoLlamada.isPlaying()) {
                                mediaPlayerTonoLlamada.stop();
                            }
                            mediaPlayerTonoLlamada.release();
                        }
                    } catch (Exception e) {

                    }

                    finish();
                } catch (Exception e) {

                }
                break;
            case 1:
                textViewLlamando.setText("Finalizando llamada...");
                try {
                    usuarioFrom.finalizarLlamadaDeVideo(channelNameShare);
                    finish();
                } catch (Exception e) {

                }
                break;
        }
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }

    public void onLocalContainerClick(View view) {
        switchView(mLocalVideo);
        switchView(mRemoteVideo);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mediaPlayerTonoLlamada != null) {
                if (mediaPlayerTonoLlamada.isPlaying()) {
                    mediaPlayerTonoLlamada.stop();
                }
                mediaPlayerTonoLlamada.release();
            }
        } catch (Exception e) {

        }
//
        try {
            if (!contestar) {
                usuarioFrom.rechazarLlamadaDeVideo(llamadaVideo.getId());
            }
        } catch (Exception e) {

        }


        switch (callStatus) {
            case 0:
                usuarioFrom.finalizarLlamadaDeVideo(channelNameShare);

                break;
            case 1:
                if (channelNameShare != null) {
                    try {
                        if (contestar) {
                            usuarioFrom.finalizarLlamadaDeVideo(channelNameShare);
                        } else {
                            usuarioFrom.rechazarLlamadaDeVideo(channelNameShare);
                        }
                    } catch (Exception e) {

                    }
                    finish();
                }
                break;
        }


        textViewLlamando.setText("Finalizando llamada...");

    }


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
        if (!mCallEnd) {
            try {
                leaveChannel();
            } catch (Exception e) {

            }
        }


        RtcEngine.destroy();
        mRtcEngine = null;

        try {
            if (mediaPlayerTonoLlamada != null) {
                if (mediaPlayerTonoLlamada.isPlaying()) {
                    mediaPlayerTonoLlamada.stop();
                }
                mediaPlayerTonoLlamada.release();
            }
        } catch (Exception e) {

        }

        switch (callStatus) {
            case 0:
                try {
                    usuarioFrom.cancelarLlamadaDeVideo(channelNameShare);
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


        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
    }
}