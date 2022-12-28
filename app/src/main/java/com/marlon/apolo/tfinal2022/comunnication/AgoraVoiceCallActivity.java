package com.marlon.apolo.tfinal2022.comunnication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Usuario;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class AgoraVoiceCallActivity extends AppCompatActivity {

    private static final String TAG = AgoraVoiceCallActivity.class.getSimpleName();
    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "7c0b693ccee54bcdb935f23c984dc2aa";
    // Fill the channel name.
    private String channelName = "chainsawman";
    // Fill the temp token generated on Agora Console.
//    private String token = "007eJxTYHC7avQuMlMx09ZywqN1CyOFXprOuKNxzsX95Moax+IvWtkKDObJBklmlsbJyamppiZJySlJlsamaUbGyZYWJinJRomJ1S7TkxsCGRn0ItwYGKEQxOdmSM5IzMwrTizPTcxjYAAALUsh6w==";
    // An integer that identifies the local user.
    private int uid = 0;
    // Track the status of your connection
    private boolean isJoined = false;

    // Agora engine instance
    private RtcEngine agoraEngine;
    // UI elements
    private TextView infoText;
//    private ImageButton joinLeaveButton;

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO
            };

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> infoText.setText("Remote user joined: " + uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            // Successfully joined a channel
            isJoined = true;
            showMessage("Joined Channel " + channel);
            runOnUiThread(() -> infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline " + uid + " " + reason);
            if (isJoined)
                runOnUiThread(() -> infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            // Listen for the local user leaving the channel
            runOnUiThread(() -> infoText.setText("Press the button to join a channel"));
            isJoined = false;
        }

        // Listen for the event that the token is about to expire
        @Override
        public void onTokenPrivilegeWillExpire(String token) {
            Log.i("i", "Token Will expire");
            //fetchToken(uid, channelName, tokenRole);
            super.onTokenPrivilegeWillExpire(token);
        }

    };


    private int tokenRole; // The token role
    //private String serverUrl = "<Token Server URL>"; // The base URL to your token server, for example, "https://agora-token-service-production-92ff.up.railway.app".
    private int tokenExpireTime = 40; // Expire time in Seconds.
    private EditText editChannelName; // To read the channel name from the UI.
    private String serverUrl = "https://authwitouthauth.herokuapp.com";
    private ImageButton muteButton;
    private ImageButton unMuteButton;
    private Usuario usuarioRemoto;
    private Usuario usuarioLocal;
    private String callStatus;

    private LinearLayout linLytRemoteUser;
    private ImageView imageViewRemoteUser;
    private TextView textViewRemoteUser;
    private TextView textViewRemoteState;
    private String idVoiceCall;
    private ChildEventListener childEventListenerLlamar;
    private LlamadaVoz llamadaVozRemota;

    private ImageButton joinButton;
    private ImageButton leaveButton;

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        WindowInsetsControllerCompat windowInsetsController =
//                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
//        if (windowInsetsController == null) {
//            return;
//        }
//        // Configure the behavior of the hidden system bars
//        windowInsetsController.setSystemBarsBehavior(
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        );
//        // Hide both the status bar and the navigation bar
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }


    private void joinChannel() {
//        ChannelMediaOptions options = new ChannelMediaOptions();
//        options.autoSubscribeAudio = true;
//        // Set both clients as the BROADCASTER.
//        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//        // Set the channel profile as BROADCASTING.
//        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
//
//        // Join the channel with a temp token.
//        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
//        agoraEngine.joinChannel(token, channelName, uid, options);


        //channelName = editChannelName.getText().toString();
        if (channelName.length() == 0) {
            showMessage("Type a channel name");
            return;
        } else if (!serverUrl.contains("http")) {
            showMessage("Invalid token server URL");
            return;
        }

        if (checkSelfPermission()) {

            Toast.makeText(getApplicationContext(), "Join Channel...", Toast.LENGTH_SHORT).show();
//            ChannelMediaOptions options = new ChannelMediaOptions();
//            options.autoSubscribeAudio = true;
//            // Set both clients as the BROADCASTER.
//            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//            // Set the channel profile as BROADCASTING.
//            options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
//
//            // Join the channel with a temp token.
//            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
//            agoraEngine.joinChannel(token, channelName, uid, options);
            fetchToken(uid, channelName, tokenRole);
        } else {
            showMessage("Permissions was not granted");
        }
    }


    public void joinLeaveChannel(View view) {
        ImageButton imageButton = (ImageButton) view;

        if (isJoined) {
            imageButton.setBackground(AppCompatResources.getDrawable(AgoraVoiceCallActivity.this, R.drawable.start_call_icon_behavior));
            agoraEngine.leaveChannel();


//            joinLeaveButton.setText("Join");
        } else {
            imageButton.setBackground(AppCompatResources.getDrawable(AgoraVoiceCallActivity.this, R.drawable.end_call_icon_behavior));
            joinChannel();
//            joinLeaveButton.setText("Leave");
        }
    }


    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);

            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO);
            agoraEngine.setAudioScenario(Constants.AUDIO_SCENARIO_GAME_STREAMING);
//            agoraEngine.disableVideo();
//            agoraEngine.setEnableSpeakerphone(true);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
    }

    // Fetch the <Vg k="VSDK" /> token
    private void fetchToken(int uid, String channelName, int tokenRole) {
//        // Prepare the Url
//        String URLString = serverUrl + "/rtc/" + channelName + "/" + tokenRole + "/"
//                + "uid" + "/" + uid + "/?expiry=" + tokenExpireTime;
//
//        OkHttpClient client = new OkHttpClient();
//
//        // Instantiate the RequestQueue.
//        Request request = new Request.Builder()
//                .url(URLString)
//                .header("Content-Type", "application/json; charset=UTF-8")
//                .get()
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("IOException", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Gson gson = new Gson();
//                    String result = response.body().string();
//                    Map map = gson.fromJson(result, Map.class);
//                    String _token = map.get("rtcToken").toString();
//                    if (isJoined) setToken(_token);
//                    Log.i("Token Received", token);
//                }
//            }
//        });

        String externalServer = "Java";
        // The base URL to your token server, for example, "https://agora-token-service-production-92ff.up.railway.app".
//        String serverUrl = "";

        String nodeJsUrl = "https://s3rv3rsid3.herokuapp.com";
        AgoraGetAsyncToken agoraTokenAsyncTaskWithJava = new AgoraGetAsyncToken(AgoraVoiceCallActivity.this,
                isJoined,
                agoraEngine,
                channelName,
                uid);

        agoraTokenAsyncTaskWithJava.execute(serverUrl);
//        switch (externalServer) {
//            case "Java":
//                AgoraGetAsyncToken agoraTokenAsyncTaskWithJava = new AgoraGetAsyncToken(AgoraVoiceCallActivity.this,
//                        isJoined,
//                        agoraEngine,
//                        channelName,
//                        uid);
//
//                agoraTokenAsyncTaskWithJava.execute(serverUrl);
//                break;
//            case "Node.js":
////                AgoraTokenAsyncTaskWithNodeJs agoraTokenAsyncTaskWithNodeJs = new AgoraTokenAsyncTaskWithNodeJs(AgoraVideoCallActivity.this,
////                        isJoined,
////                        agoraEngine,
////                        channelName,
////                        uid);
////
////                agoraTokenAsyncTaskWithNodeJs.execute(nodeJsUrl);
//                break;
//        }
    }


    void setToken(String newValue) {
//        token = newValue;
        if (!isJoined) { // Join a channel
//            ChannelMediaOptions options = new ChannelMediaOptions();
//
//            // For a Video call, set the channel profile as COMMUNICATION.
//            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
//            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
//            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//            // Start local preview.
//            //agoraEngine.startPreview();
//
//            // Join the channel with a token.
//            agoraEngine.joinChannel(newValue, channelName, uid, options);

            ChannelMediaOptions options = new ChannelMediaOptions();

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            // Start local preview.
            agoraEngine.startPreview();

            // Join the channel with a token.
            agoraEngine.joinChannel(newValue, channelName, uid, options);


            try {

                switch (callStatus) {
                    case "llamadaEntrante":
//                        isJoined = false;
//                        Toast.makeText(getApplicationContext(), "contesntaod" + channelName, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "contesntaod" + llamadaVozRemota.toString(), Toast.LENGTH_SHORT).show();
//                        constestarLlamada(llamadaVideoRemota);
                        break;
                    case "llamadaSaliente":
                        isJoined = true;
                        createVoiceCallOnFirebase(channelName, uid);
                        break;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());

            }
        } else { // Already joined, renew the token by calling renewToken
            agoraEngine.renewToken(newValue);
            //showMessage("Token renewed");
        }
    }

    private void createVoiceCallOnFirebase(String channelName, int uid) {
        LlamadaVoz llamadaVoz = new LlamadaVoz();
        Participante participanteCaller = new Participante();
        Participante participanteDestiny = new Participante();
        participanteCaller.setIdParticipante(FirebaseAuth.getInstance().getCurrentUser().getUid());
        participanteCaller.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
        participanteCaller.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

        participanteDestiny.setIdParticipante(usuarioRemoto.getIdUsuario());
        participanteDestiny.setNombreParticipante(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido());
        participanteDestiny.setUriFotoParticipante(usuarioRemoto.getFotoPerfil());

//        idVideoCall = FirebaseDatabase.getInstance().getReference().child("videoCalls").push().getKey();
        idVoiceCall = channelName;
        llamadaVoz.setId(idVoiceCall);
        llamadaVoz.setAccessToken(channelName);/*Reemplazar por channel Name*/
        llamadaVoz.setUidCaller(uid);
        llamadaVoz.setParticipanteCaller(participanteCaller);
        llamadaVoz.setParticipanteDestiny(participanteDestiny);

        llamadaVoz.setChannelConnectedStatus(false);
        llamadaVoz.setCallerStatus(true);
        llamadaVoz.setDestinyStatus(false);
        llamadaVoz.setRejectCallStatus(false);
        llamadaVoz.setFinishCall(false);

        FirebaseDatabase.getInstance().getReference()
                .child("voiceCalls")
                .child(idVoiceCall)
                .setValue(llamadaVoz)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Lllamando...");
                            textViewRemoteState.setText("Llamando...");
                        } else {
                            Log.d(TAG, "Error al realizar llamada.");
                        }
                    }
                });

//        FirebaseDatabase.getInstance().getReference().child("videoCalls")
//                .child(idVideoCall)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        LlamadaVideo llamadaVideo1 = snapshot.getValue(LlamadaVideo.class);
//                        Toast.makeText(getApplicationContext(), "State Changed", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        childEventListenerLlamar = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVoz llamadaVozChanged = snapshot.getValue(LlamadaVoz.class);
                if (llamadaVozChanged.getId().equals(idVoiceCall)) {
                    if (llamadaVozChanged.isChannelConnectedStatus() && llamadaVozChanged.isDestinyStatus()) {
                        linLytRemoteUser.setVisibility(View.GONE);
                    }

                    //*llamada rechazada*/
                    if (llamadaVozChanged.isRejectCallStatus()) {
                        linLytRemoteUser.setVisibility(View.GONE);
                        textViewRemoteState.setText("Llamada rechazada");
                        textViewRemoteState.setVisibility(View.VISIBLE);
                        textViewRemoteUser.setVisibility(View.GONE);
                        imageViewRemoteUser.setVisibility(View.GONE);
                        linLytRemoteUser.setVisibility(View.VISIBLE);

//                        if (llamadaVideoChanged.isRejectCallStatus()) {
//                            finishWithRejectLocalCall();
//                        } else {
//                            finishLocalCall();
//                        }
//                        finishVideoCall(idVideoCall);

                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                LlamadaVoz llamadaVozRemoved = snapshot.getValue(LlamadaVoz.class);
                if (llamadaVozRemoved.getId().equals(idVoiceCall)) {
//                    finishLocalCall();
//                    if (llamadaVideoRemoved.isRejectCallStatus()) {
//                        finishWithRejectLocalCall();
//                    } else {
//                        finishLocalCall();
//                    }
                    //finishVideoCall(idVideoCall);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("voiceCalls")
                .addChildEventListener(childEventListenerLlamar);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_agora_voice_call);

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

        usuarioRemoto = (Usuario) getIntent().getSerializableExtra("usuarioRemoto");
        usuarioLocal = (Usuario) getIntent().getSerializableExtra("usuarioLocal");
        llamadaVozRemota = (LlamadaVoz) getIntent().getSerializableExtra("llamadaVoz");
//        joinRemoteStatus = getIntent().getStringExtra("extraJoin");

        callStatus = getIntent().getStringExtra("callStatus");
//        channelName = getIntent().getStringExtra("channelName");

        setupVoiceSDKEngine();

        // Set up access to the UI elements
//        joinLeaveButton = findViewById(R.id.joinLeaveButton);
        infoText = findViewById(R.id.infoText);
        editChannelName = (EditText) findViewById(R.id.editChannelName);

        muteButton = findViewById(R.id.micMute);
        unMuteButton = findViewById(R.id.micUnmute);

        linLytRemoteUser = findViewById(R.id.linLytRemoteUser);
        imageViewRemoteUser = findViewById(R.id.imageViewRemote);
        textViewRemoteUser = findViewById(R.id.textViewRemoteUser);
        textViewRemoteState = findViewById(R.id.textViewRemoteState);

        joinButton = findViewById(R.id.joinButton);
        leaveButton = findViewById(R.id.leaveButton);


        try {

            switch (callStatus) {
                case "llamadaEntrante":
                    channelName = llamadaVozRemota.getId();
                    linLytRemoteUser.setVisibility(View.VISIBLE);
                    textViewRemoteState.setText("Llamada de voz entrante..." + channelName);
                    textViewRemoteUser.setVisibility(View.VISIBLE);
                    imageViewRemoteUser.setVisibility(View.VISIBLE);
                    editChannelName.setText(channelName);

                    break;
                case "llamadaSaliente":


                    linLytRemoteUser.setVisibility(View.VISIBLE);
                    textViewRemoteState.setText("Conectando...");
                    textViewRemoteUser.setVisibility(View.VISIBLE);
                    imageViewRemoteUser.setVisibility(View.VISIBLE);
                    editChannelName.setText(channelName);

                    joinChannel(findViewById(R.id.joinButton));
//                    joinLeaveChannel(findViewById(R.id.joinButton));
//                        createVideoCallOnFirebase(channelName, uid);
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());

        }

    }

    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }


    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            Toast.makeText(getApplicationContext(), "Desactivando altavoz", Toast.LENGTH_LONG).show();
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            Toast.makeText(getApplicationContext(), "Activando altavoz", Toast.LENGTH_LONG).show();
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        agoraEngine.setEnableSpeakerphone(view.isSelected());
    }

    public void muteMic(View view) {
//        agoraEngine.muteLocalAudioStream(true);
//        agoraEngine.muteAllRemoteAudioStreams(true);
        agoraEngine.muteLocalAudioStream(true);
        unMuteButton.setVisibility(View.VISIBLE);
        muteButton.setVisibility(View.GONE);

    }

    public void unmuteMic(View view) {
//        agoraEngine.muteLocalAudioStream(false);

//        agoraEngine.muteAllRemoteAudioStreams(false);
        agoraEngine.muteLocalAudioStream(false);
        unMuteButton.setVisibility(View.GONE);
        muteButton.setVisibility(View.VISIBLE);
    }

    public void joinChannel(View view) {
//        isJoined = true;

        joinButton.setVisibility(View.GONE);
        leaveButton.setVisibility(View.VISIBLE);
        joinChannel();
//        joinLeaveChannel(view);
    }

    public void leaveChannel(View view) {
        if (isJoined) {

            joinButton.setVisibility(View.VISIBLE);
            leaveButton.setVisibility(View.GONE);
            agoraEngine.leaveChannel();
        }
    }
}