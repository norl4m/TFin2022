package com.marlon.apolo.tfinal2022.comunnication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.EchoTestConfiguration;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.internal.LastmileProbeConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class VideoCallUIKitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = VideoCallUIKitActivity.class.getSimpleName();
    // Fill the App ID of your project generated on Agora Console.
    private String appId = "7c0b693ccee54bcdb935f23c984dc2aa";
    // Fill the channel name.
//    private String channelName = "testu";
    // Fill the temp token generated on Agora Console.
//    private String token = "007eJxTYFD8dVTmROPd3XZXhRh/vN9e5fDgXaX093dB7/5yXjyvav5MgcE82SDJzNI4OTk11dQkKTklydLYNM3IONnSwiQl2Sgx8X1Lf3JDICNDwfMyZkYGCATxWRlKUotLShkYAKyKJKM=";
    // An integer that identifies the local user.
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;


    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };
    private String channelName;
    private String token;
    private ProgressDialog progressDialog;
    private String title;
    private String message;
    private String idVideoCall;
    //    private boolean highQuality;
    private int localFps;
    private String server;
    private VideoEncoderConfiguration videoConfigLocal;

    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;


    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            // By default, the video module is disabled, call enableVideo to enable it.

//
//            VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
//// Set mirror mode
//            videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
//// Set framerate
////            videoConfig.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
//            videoConfig.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
//// Set bitrate
//            videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//// Set dimensions
////            videoConfig.dimensions = VideoEncoderConfiguration.VD_640x360;
//            videoConfig.dimensions = VideoEncoderConfiguration.VD_1280x720;
//// Set orientation mode
//            videoConfig.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
//// Set degradation preference
//            videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
//// Apply the configuration
//            agoraEngine.setVideoEncoderConfiguration(videoConfig);


            agoraEngine.enableVideo();


            // Enable the dual stream mode
            agoraEngine.enableDualStreamMode(true);
// Set audio profile and audio scenario.
            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_GAME_STREAMING);

// Set the video profile
            videoConfigLocal = new VideoEncoderConfiguration();
// Set mirror mode
            videoConfigLocal.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
//            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
            videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
//            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_320x240;
            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
// Set orientation mode
            videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
//            videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
            videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
            agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);

// Start the probe test
            startProbeTest();
        } catch (Exception e) {
            showMessage(e.toString());
        }
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            showMessage("Remote user joined " + uid);
            remoteUid = uid;

            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            uidLocal = uid;
            showMessage("Joined Channel " + channel);
            closeProgressDialog();
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            showMessage("Remote user offline " + uid + " " + reason);
//            onRemoteUserLeft(uid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //onRemoteUserLeft(uid);
                    remoteSurfaceView.setVisibility(View.GONE);
                }
            });
//            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
        }

        // Listen for the event that the token is about to expire
        @Override
        public void onTokenPrivilegeWillExpire(String token) {
            Log.i("i", "Token Will expire");
            // fetchToken(uid, channelName, tokenRole);
            super.onTokenPrivilegeWillExpire(token);
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            showMessage("Connection state changed"
                    + "\n New state: " + state
                    + "\n Reason: " + reason);
        }

        @Override
        public void onLastmileQuality(int quality) {
            runOnUiThread(() -> updateNetworkStatus(quality));
        }

        @Override
        public void onLastmileProbeResult(LastmileProbeResult result) {
            agoraEngine.stopLastmileProbeTest();
            // The result object contains the detailed test results that help you
            // manage call quality, for example, the downlink jitter.
            showMessage("Downlink jitter: " + result.downlinkReport.jitter);
        }

        @Override
        public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
            // Use downlink network quality to update the network status
            runOnUiThread(() -> updateNetworkStatus(rxQuality));
        }

        @Override
        public void onRtcStats(RtcStats rtcStats) {
            counter1 += 1;
            String msg = "";

            if (counter1 == 5)
                msg = rtcStats.users + " user(s)";
            else if (counter1 == 10) {
                msg = "Packet loss rate: " + rtcStats.rxPacketLossRate;
                counter1 = 0;
            }

            if (msg.length() > 0) showMessage(msg);
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            String msg = "Remote video state changed: \n Uid =" + uid
                    + " \n NewState =" + state
                    + " \n reason =" + reason
                    + " \n elapsed =" + elapsed
                    + " \n elapsed =" + elapsed;

            showMessage(msg);
        }

        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
            counter2 += 1;

            if (counter2 == 5) {
                String msg = "Remote Video Stats: "
                        + "\n User id =" + stats.uid
                        + "\n Received bitrate =" + stats.receivedBitrate
                        + "\n Total frozen time =" + stats.totalFrozenTime
                        + "\n Width =" + stats.width
                        + "\n Height =" + stats.height;
                counter2 = 0;
                showMessage(msg);
            }
        }


    };

    private void closeProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

//    private void setupLocalVideo() {
////        FrameLayout container = findViewById(R.id.local_video_view_container);
//        // Create a SurfaceView object and add it as a child to the FrameLayout.
//        localSurfaceView = new SurfaceView(getBaseContext());
//        localSurfaceView.setZOrderMediaOverlay(true);
//
//        mLocalContainer.addView(localSurfaceView);
//        mLocalVideo = new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
//        // Pass the SurfaceView object to Agora so that it renders the local video.
//        agoraEngine.setupLocalVideo(mLocalVideo);
//    }

    private void setupRemoteVideo(int uid) {
        RelativeLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        // Display RemoteSurfaceView.
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }


    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = new SurfaceView(getBaseContext());
        container.addView(localSurfaceView);
        // Pass the SurfaceView object to Agora so that it renders the local video.
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }


//    private void setupRemoteVideo(int uid) {
//        ViewGroup parent = mRemoteContainer;
//        if (parent.indexOfChild(mLocalVideo.view) > -1) {
//            parent = mLocalContainer;
//        }
//
//        // Only one remote video view is available for this
//        // tutorial. Here we check if there exists a surface
//        // view tagged as this uid.
//        if (mRemoteVideo != null) {
//            return;
//        }
//
//        /*
//          Creates the video renderer view.
//          CreateRendererView returns the SurfaceView type. The operation and layout of the view
//          are managed by the app, and the Agora SDK renders the view provided by the app.
//          The video display view must be created using this method instead of directly
//          calling SurfaceView.
//         */
//        remoteSurfaceView = RtcEngine.CreateRendererView(getBaseContext());
//        remoteSurfaceView.setZOrderMediaOverlay(parent == mLocalContainer);
//        parent.addView(remoteSurfaceView);
//        mRemoteVideo = new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid);
//        // Initializes the video view of a remote user.
//        agoraEngine.setupRemoteVideo(mRemoteVideo);
//    }


//    public void joinChannel(View view) {
//        if (checkSelfPermission()) {
//            ChannelMediaOptions options = new ChannelMediaOptions();
//
//            // For a Video call, set the channel profile as COMMUNICATION.
//            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
//            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
//            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//            // Display LocalSurfaceView.
//            setupLocalVideo();
//            localSurfaceView.setVisibility(View.VISIBLE);
//            // Start local preview.
//            agoraEngine.startPreview();
//            // Join the channel with a temp token.
//            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
//            agoraEngine.joinChannel(token, channelName, uid, options);
//        } else {
//            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
//        }
//    }


    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
//        dialog.setTitle("Por favor espere");
        progressDialog.setTitle(title);
//        dialog.setMessage("Trabix se encuentra verificando su nùmero celular...");
        progressDialog.setMessage(message);
        progressDialog.show();

    }


//    public void VideoEncoderConfiguration() {
//        this.dimensions = new VideoDimensions(640, 480);
//        this.frameRate = FRAME_RATE.FRAME_RATE_FPS_15.getValue();
//        this.minFrameRate = DEFAULT_MIN_FRAMERATE;
//        this.bitrate = STANDARD_BITRATE;
//        this.minBitrate = DEFAULT_MIN_BITRATE;
//        this.orientationMode = ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
//        this.degradationPrefer = DEGRADATION_PREFERENCE.MAINTAIN_QUALITY;
//        this.mirrorMode = MIRROR_MODE_TYPE.MIRROR_MODE_DISABLED;
//    }


    public void joinChannel(View view) {
        Log.d(TAG, "Join Channel");
        channelName = editChannelName.getText().toString();
        title = "Por favor espere";
        message = "Conectando...";
        showProgress(title, message);

//        channelName = FirebaseDatabase.getInstance().getReference().child("videoCalls").push().getKey();

        if (channelName.length() == 0) {
            showMessage("Type a channel name");
            return;
        } else if (!serverUrl.contains("http")) {
            showMessage("Invalid token server URL");
            return;
        }

        if (checkSelfPermission()) {
            tokenRole = Constants.CLIENT_ROLE_BROADCASTER;
            // Display LocalSurfaceView.
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
//            channelName = FirebaseDatabase.getInstance().getReference().child("videoCalls").push().getKey();

//            server = "";
            fetchToken(uid, channelName, tokenRole, server);
        } else {
            showMessage("Permissions was not granted");
        }
    }


    String urlBase = "https://s3rv3rsid3.herokuapp.com";
    final int min = 3000;
    final int max = 3999;
    int random = new Random().nextInt((max - min) + 1) + min;
    int uidLocal = 0;
    //        String channelName = channelNameShare;
    //String getToken = urlBase + "/rtc/" + channelNameShare + "/publisher/uid/" + uidLocal + "/";
    //        String getToken = urlBase + "/rtc/" + "demonChannel" + "/publisher/uid/" + uid + "/";
    String tokenLocal = "";


    // The base URL to your token server.
// For example, "https://agora-token-service-production-92ff.up.railway.app"
    private String serverUrl = "https://s3rv3rsid3.herokuapp.com";
    //    private String serverUrl = "https://authwitouthauth.herokuapp.com";
    private int tokenRole; // The token role: Broadcaster or Audience
    //    private int tokenExpireTime = 40; // Expire time in Seconds.
    private String tokenExpireTime = "40"; // Expire time in Seconds.
    private EditText editChannelName; // To read the channel name from the UI.
    private CheckBox checkBoxVideo;

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

    // Volume Control
    private SeekBar volumeSeekBar;
    private CheckBox muteCheckBox;
    private int volume = 50;


    private TextView networkStatus; // For updating the network status
    private int counter1 = 0; // Controls the frequency of messages
    private int counter2 = 0; // Controls the frequency of messages
    private int remoteUid; // Uid of the remote user
    private boolean highQuality = true; // Quality of the remote video stream being played
    private boolean isEchoTestRunning = false; // Keeps track of the echo test
    private Button echoTestButton;

    private void updateNetworkStatus(int quality) {
        if (quality > 0 && quality < 3) networkStatus.setBackgroundColor(Color.GREEN);
        else if (quality <= 4) networkStatus.setBackgroundColor(Color.YELLOW);
        else if (quality <= 6) networkStatus.setBackgroundColor(Color.RED);
        else networkStatus.setBackgroundColor(Color.WHITE);
    }

    public void startProbeTest() {
        // Configure a LastmileProbeConfig instance.
        LastmileProbeConfig config = new LastmileProbeConfig();
        // Probe the uplink network quality.
        config.probeUplink = true;
        // Probe the downlink network quality.
        config.probeDownlink = true;
        // The expected uplink bitrate (bps). The value range is [100000,5000000].
        config.expectedUplinkBitrate = 100000;
        // The expected downlink bitrate (bps). The value range is [100000,5000000].
        config.expectedDownlinkBitrate = 100000;
        agoraEngine.startLastmileProbeTest(config);
        showMessage("Running the last mile probe test ...");
    }

    public void echoTest(View view) {
        if (!isEchoTestRunning) {
            EchoTestConfiguration echoConfig = new EchoTestConfiguration();
            echoConfig.enableAudio = true;
            echoConfig.enableVideo = true;
            echoConfig.token = token;
            echoConfig.channelId = channelName;

            setupLocalVideo();
            echoConfig.view = localSurfaceView;
            localSurfaceView.setVisibility(View.VISIBLE);
            agoraEngine.startEchoTest(echoConfig);
            echoTestButton.setText("Stop Echo Test");
            isEchoTestRunning = true;
        } else {
            agoraEngine.stopEchoTest();
            echoTestButton.setText("Start Echo Test");
            isEchoTestRunning = false;
            setupLocalVideo();
            localSurfaceView.setVisibility(View.GONE);
        }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_video_call_uikit);
//        setContentView(R.layout.activity_video_call_uikit_poc);
        //appId = getString(R.string.agora_app_id);
        editChannelName = (EditText) findViewById(R.id.editChannelName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVideoSDKEngine();

        networkStatus = findViewById(R.id.networkStatus);
        echoTestButton = findViewById(R.id.echoTestButton);


        checkBoxVideo = findViewById(R.id.videoCheckBox);
//        checkBoxVideo.setOnCheckedChangeListener(
//                (buttonView, isChecked) -> {
//                    if (!isChecked) {
//                        checkBoxVideo.setText("Disable video");
//                        enableVideo();
//                    } else {
//                        checkBoxVideo.setText("Enable video");
//                        disableVideo();
//                    }
//                }
//        );


        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        ((ImageView) findViewById(R.id.switchCamera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });


//        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
//        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                volume = progress;
//                agoraEngine.adjustRecordingSignalVolume(volume);
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                //Required to implement OnSeekBarChangeListener
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                //Required to implement OnSeekBarChangeListener
//            }
//        });

        muteCheckBox = (CheckBox) findViewById(R.id.muteCheckBox);
//        muteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                agoraEngine.muteRemoteAudioStream(uidLocal, isChecked);
//            }
//        });


//        Spinner spinner = (Spinner) findViewById(R.id.resolution_spinner);
//        spinner.setOnItemSelectedListener(this);
//
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = null;
//
//        adapter = ArrayAdapter.createFromResource(this, R.array.resolution, android.R.layout.simple_spinner_item);
//
//
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);


    }

    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }


//    private void setupRemoteVideo(int uid) {
//        RelativeLayout container = findViewById(R.id.remote_video_view_container);
//        remoteSurfaceView = new SurfaceView(getBaseContext());
//        remoteSurfaceView.setZOrderMediaOverlay(true);
//        container.addView(remoteSurfaceView);
//        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
//        // Display RemoteSurfaceView.
//        remoteSurfaceView.setVisibility(View.VISIBLE);
//    }


    // Fetch the <Vg k="VSDK" /> token
    private void fetchToken(int uid, String channelName, int tokenRole, String server) {
        // Prepare the Url
//        String URLString = serverUrl + "/rtc/" + channelName + "/" + tokenRole + "/" + "uid" + "/" + uid + "/?expiry=" + tokenExpireTime;
//        String URLString = serverUrl + "/rtc/" + channelName + "/" + "publisher" + "/" + "uid" + "/" + uid + "/?expiry=" + tokenExpireTime;
//        String URLString = serverUrl + "/rtc/" + channelName + "/" + "publisher" + "/" + uid;
        String URLString = "";
        switch (server) {
            case "java":
                closeProgressDialog();
                title = "Por favor espere";
                message = "Conectando al servidor JAVA...";
                showProgress(title, message);
                serverUrl = "https://authwitouthauth.herokuapp.com";
                URLString = serverUrl + "/rtc/" + channelName + "/" + "publisher" + "/" + uid;
                OkHttpClient client = new OkHttpClient();

                // Instantiate the RequestQueue.
                Request request = new Request.Builder()
                        .url(URLString)
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .get()
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("IOException", e.toString());

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String result = response.body().string();
                            Map map = gson.fromJson(result, Map.class);
                            String _token = map.get("rtcToken").toString();
                            setToken(_token);
                            Log.i("Token Received", token);
                        }
                    }


                });
                break;
            case "node":
                closeProgressDialog();
                title = "Por favor espere";
                message = "Conectando al servidor NodeJs...";
                showProgress(title, message);

                serverUrl = "https://s3rv3rsid3.herokuapp.com";
//                URLString = serverUrl + "/rtc/" + channelName + "/" + "publisher" + "/" + "uid" + "/" + uid + "/?expiry=" + tokenExpireTime;
                AgoraTokenAsyncTaskWithNodeJs agoraTokenAsyncTaskWithNodeJs = new AgoraTokenAsyncTaskWithNodeJs(VideoCallUIKitActivity.this,
                        isJoined,
                        agoraEngine,
                        channelName,
                        uid);

                agoraTokenAsyncTaskWithNodeJs.execute(serverUrl);


                break;
        }
//

    }

    void setToken(String newValue) {
        token = newValue;
        if (!isJoined) { // Join a channel
            ChannelMediaOptions options = new ChannelMediaOptions();

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            // Start local preview.
            agoraEngine.startPreview();

            // Join the channel with a token.
            agoraEngine.joinChannel(token, channelName, uid, options);
            createVideoCallOnFirebase(channelName, uid);


        } else { // Already joined, renew the token by calling renewToken
            agoraEngine.renewToken(token);
            showMessage("Token renewed");
        }
    }


    private String id;
    private String accessToken;
    private int uidCaller;
    private int uidDestiny;
    private boolean callerStatus;
    private boolean destinyStatus;
    private String callerToken;
    private String destinyToken;
    Participante participanteCaller;
    Participante participanteDestiny;
    private boolean channelConnectedStatus;
    private boolean rejectCallStatus;
    private boolean finishCall;


    private void createVideoCallOnFirebase(String channelName, int uid) {
        LlamadaVideo llamadaVideo = new LlamadaVideo();
        Participante participanteCaller = new Participante();
        Participante participanteDestiny = new Participante();
        participanteCaller.setIdParticipante(FirebaseAuth.getInstance().getCurrentUser().getUid());
        participanteCaller.setNombreParticipante("Alvita Castro");
        participanteCaller.setUriFotoParticipante("https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/empleadores%2FwKaAwCZgA6hP4mPHWBcvsYhUW6e2%2FfotoPerfil.jpg?alt=media&token=9588d764-f9d5-42c0-a5a4-70984dc3eef6");

        participanteDestiny.setIdParticipante("73BKCV96EEPS5wAb8SuscAdDBAX2");
        participanteDestiny.setNombreParticipante("Gonzalo Plata");
        participanteDestiny.setUriFotoParticipante("https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/trabajadores%2F73BKCV96EEPS5wAb8SuscAdDBAX2%2FfotoPerfil.jpg?alt=media&token=887e6b41-d2c3-4fb5-b28c-367c7d95bebc");

        idVideoCall = FirebaseDatabase.getInstance().getReference().child("videoCalls").push().getKey();
        llamadaVideo.setId(idVideoCall);
        llamadaVideo.setAccessToken(channelName);/*Reemplazar por channel Name*/
        llamadaVideo.setUidCaller(uid);
        llamadaVideo.setParticipanteCaller(participanteCaller);
        llamadaVideo.setParticipanteDestiny(participanteDestiny);

        llamadaVideo.setChannelConnectedStatus(false);
        llamadaVideo.setCallerStatus(true);
        llamadaVideo.setDestinyStatus(false);
        llamadaVideo.setRejectCallStatus(false);
        llamadaVideo.setFinishCall(false);

        FirebaseDatabase.getInstance().getReference()
                .child("videoCalls")
                .child(idVideoCall)
                .setValue(llamadaVideo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Lllamando...");
                        } else {
                            Log.d(TAG, "Error al realizar llamada.");
                        }
                    }
                });
    }


    public void leaveChannel(View view) {
        if (!isJoined) {
            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
            showMessage("You left the channel");
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;

            finishVideoCall(idVideoCall);
//            finish();
        }
    }

    public void changeQualityVideo10() {
        Toast.makeText(getApplicationContext(), "changeQualityVideo10", Toast.LENGTH_SHORT).show();
        // Set the video profile
        VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
        videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
        videoConfig.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
// Set bitrate
        videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
        videoConfig.dimensions = VideoEncoderConfiguration.VD_320x240;
// Set orientation mode
        videoConfig.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
        videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
        agoraEngine.setVideoEncoderConfiguration(videoConfig);
        createSpinnerResolution(10);


//        // Enable the dual stream mode
//        agoraEngine.enableDualStreamMode(true);
//// Set audio profile and audio scenario.
//        agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_GAME_STREAMING);
//
//// Set the video profile
//        videoConfigLocal = new VideoEncoderConfiguration();
//// Set mirror mode
//        videoConfigLocal.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
//// Set framerate
//        videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
//// Set bitrate
//        videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//// Set dimensions
////            videoConfig.dimensions = VideoEncoderConfiguration.VD_640x360;
//        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_240x180;
//// Set orientation mode
//        videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
//// Set degradation preference
//        videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
//// Apply the configuration
//        agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
//
////        setupLocalVideo();
    }


    public void changeQualityVideo15() {
        Toast.makeText(getApplicationContext(), "changeQualityVideo15", Toast.LENGTH_SHORT).show();
        // Set the video profile
        VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
        videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
        videoConfig.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
        videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//        videoConfig.bitrate = 400;
// Set dimensions
//        videoConfig.dimensions = VideoEncoderConfiguration.VD_1280x720;
        videoConfig.dimensions = VideoEncoderConfiguration.VD_640x360;
// Set orientation mode
        videoConfig.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
        videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
        agoraEngine.setVideoEncoderConfiguration(videoConfig);

//        setupLocalVideo();

        createSpinnerResolution(15);
    }

    public void changeQualityVideo30() {
        Toast.makeText(getApplicationContext(), "changeQualityVideo30", Toast.LENGTH_SHORT).show();
        // Set the video profile
        VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
        videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
        videoConfig.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30.getValue();
// Set bitrate
        videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//        videoConfig.bitrate = 1710;
// Set dimensions
        videoConfig.dimensions = VideoEncoderConfiguration.VD_1920x1080;
// Set orientation mode
        videoConfig.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
        videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
        agoraEngine.setVideoEncoderConfiguration(videoConfig);
        createSpinnerResolution(30);

//        setupLocalVideo();
    }

    public void createSpinnerResolution(int fps) {

        localFps = fps;
        Spinner spinner = (Spinner) findViewById(R.id.resolution_spinner);
        spinner.setOnItemSelectedListener(this);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = null;
        switch (fps) {
            case 10:
                adapter =
                        ArrayAdapter.createFromResource(this, R.array.fps_10_array, android.R.layout.simple_spinner_item);

                break;
            case 15:
                adapter =
                        ArrayAdapter.createFromResource(this, R.array.fps_15_array, android.R.layout.simple_spinner_item);

                break;
            case 30:
                adapter =
                        ArrayAdapter.createFromResource(this, R.array.fps_30_array, android.R.layout.simple_spinner_item);

                break;
        }

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    public void setStreamQuality(View view) {
        Toast.makeText(getApplicationContext(), "Cambiando calidad de video.", Toast.LENGTH_SHORT).show();
        highQuality = !highQuality;


        if (highQuality) {
            agoraEngine.setRemoteVideoStreamType(remoteUid, Constants.VIDEO_STREAM_HIGH);
            showMessage("Switching to high-quality video");
        } else {
            agoraEngine.setRemoteVideoStreamType(remoteUid, Constants.VIDEO_STREAM_LOW);
            showMessage("Switching to low-quality video");
        }
    }


    private void finishVideoCall(String idVideoCall) {
        FirebaseDatabase.getInstance().getReference()
                .child("videoCalls")
                .child(idVideoCall)
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Finalizando llamada...");
                        } else {
                            Log.d(TAG, "Error al finalizar llamada.");
                        }
                    }
                });
        // finish();
    }


    public void shareScreen(View view) {
//        changeQualityVideo();
//        setStreamQuality();
    }

    public void disableVideo() {
        agoraEngine.disableVideo();
//        int color = ContextCompat.getColor(VideoCallUIKitActivity.this, R.color.white_smoke);
//        localSurfaceView.setBackgroundColor(color);
        localSurfaceView.setVisibility(View.GONE);
    }

    public void enableVideo() {
        localSurfaceView.setVisibility(View.VISIBLE);
        agoraEngine.enableVideo();
    }

    public void disableMic() {
        agoraEngine.muteLocalAudioStream(true);

    }

    public void enableMic() {
        agoraEngine.muteLocalAudioStream(false);

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_10:
                if (checked) {
                    // Pirates are the best
                    agoraEngine.enableVideo();
                    changeQualityVideo10();
                }
                break;
            case R.id.radio_15:
                if (checked) {
                    // Pirates are the best
                    agoraEngine.enableVideo();
                    changeQualityVideo15();
                }
                break;
            case R.id.radio_30:
                if (checked) {
                    // Pirates are the best
                    agoraEngine.enableVideo();
                    changeQualityVideo30();
                }
                break;

        }
    }

    public void switchCamera() {
        agoraEngine.switchCamera();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String element = parent.getItemAtPosition(position).toString();
//        Toast.makeText(getApplicationContext(), element, Toast.LENGTH_SHORT).show();
        //VideoEncoderConfiguration.VideoDimensions videoDimensions = VideoEncoderConfiguration.VD_640x360;

        switch (element) {
            case "360p":
                videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_320x240;
                break;
            case "720p":
                videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
                break;
        }
        agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);

//
//        //Toast.makeText(getApplicationContext(), videoDimensions.toString(), Toast.LENGTH_SHORT).show();
//        switch (localFps) {
//            case 10:
//                videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
//// Set bitrate
//                videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//
//                switch (element) {
//                    case "240x180":
//// Set dimensions
////            videoConfig.dimensions = VideoEncoderConfiguration.VD_640x360;
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_240x180;
//                        break;
//                    case "320x240":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_320x240;
//                        break;
//                    case "640x360":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
//                        break;
//                }
//
//
//                break;
//            case 15:
//                videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
//// Set bitrate
//                videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//
//                switch (element) {
//
//                    case "320x240":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_320x240;
//                        break;
//                    case "640x360":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
//                        break;
//                    case "1280x720":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
//                        break;
//                }
////                agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
//                break;
//            case 30:
//                videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30.getValue();
//// Set bitrate
//                videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
//
//                switch (element) {
//
//                    case "960x720":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_960x720;
//                        break;
//                    case "1280x720":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
//                        break;
//                    case "1920x1080":
//                        videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1920x1080;
//                        break;
//                }
////                agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
//                break;
//        }
//        videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
//
//        agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onRadioButtonServer(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_java:
                if (checked) {
                    server = "java";
                }
                break;
            case R.id.radio_node:
                if (checked) {
                    server = "node";
                }
                break;
        }
    }

    public void controlMicClick(View view) {
        if (((CheckBox) view).isChecked()) {
            disableMic();
        } else {
            enableMic();
        }
    }

//
//    public void configSome(){
//        // Set the video encoding resolution, frame rate, bitrate and orientation mode according to the settings of the user
//        VideoEncoderConfiguration.VideoDimensions value = VideoEncoderConfiguration.VD_640x360;
//
//
//            agoraEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
//                    value,
//                    VideoEncoderConfiguration.FRAME_RATE.valueOf(framerate.getSelectedItem().toString()),
//                    Integer.valueOf(et_bitrate.getText().toString()),
//                    VideoEncoderConfiguration.ORIENTATION_MODE.valueOf(orientation.getSelectedItem().toStri())
//            ));
//    }


    private class AgoraTokenAsyncTaskWithNodeJs extends AsyncTask<String, Void, String> {

        private Context contextInstance;
        private boolean isJoined;
        private RtcEngine agoraEngine;
        private String channelName;
        private int uid;

        public AgoraTokenAsyncTaskWithNodeJs(Context contextVar, boolean isJoinedVar, RtcEngine agoraEngineVar, String channelNameVar, int uidVar) {
            contextInstance = contextVar;
            isJoined = isJoinedVar;
            agoraEngine = agoraEngineVar;
            channelName = channelNameVar;
            uid = uidVar;
        }

        protected String doInBackground(String... urls) {


            // Generate a random number between 0 and 10
            Random r = new Random();
            uid = r.nextInt(11);

            try {
                String urlAgora = urls[0] + "/rtc/" + channelName + "/" + "publisher" + "/" + "uid" + "/" + uid + "/?expiry=" + tokenExpireTime;

                // 1. Declare a URL Connection
                URL url = new URL(urlAgora);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 2. Open InputStream to connection
                conn.connect();
                InputStream in = conn.getInputStream();
                // 3. Download and decode the string response using builder
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    //Log.d(TAG, line);
                }
                tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2, stringBuilder.length() - 2);
//            tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2);
//            tokenLocal = stringBuilder.toString();
                Log.d(TAG, tokenLocal);
                Log.d(TAG, String.valueOf(uidLocal));
//            this.channelNameShare =tokenLocal;
                //joinChannel();

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }


            // Return a String result.
//        return "Tu token es: " + tokenLocal;
            return tokenLocal;


        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
//            showDialog("Downloaded " + result + " bytes");
            closeProgressDialog();
            setAgoraToken(result);
        }

        public void setAgoraToken(String newValue) {
            token = newValue;
            if (!isJoined) { // Join a channel
                ChannelMediaOptions options = new ChannelMediaOptions();

                // For a Video call, set the channel profile as COMMUNICATION.
                options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
                // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
                options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                // Start local preview.
                agoraEngine.startPreview();

                // Join the channel with a token.
                agoraEngine.joinChannel(token, channelName, uid, options);
                createVideoCallOnFirebase(channelName, uid);


            } else { // Already joined, renew the token by calling renewToken
                agoraEngine.renewToken(token);
                showMessage("Token renewed");
            }
        }


    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video, menu);
        return true;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.resolution_360p:
//                if (item.isChecked()) {
//                    item.setChecked(false);
//                } else {
//                    item.setChecked(true);
//                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
////                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_840x480;
//                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x480;
//                    // videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
//
//                    agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
//
//                }
//                return true;
//            case R.id.resolution_720p:
//                if (item.isChecked()) {
//                    item.setChecked(false);
//                } else {
//                    item.setChecked(true);
//                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
//                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
////                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
//                    agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
//
//                }
//                return true;
//            case R.id.java:
//                if (item.isChecked()) {
//                    item.setChecked(false);
//                } else {
//                    item.setChecked(true);
//                    server = "java";
//                }
//                return true;
//            case R.id.nodejs:
//                if (item.isChecked()) {
//                    item.setChecked(false);
//                } else {
//                    item.setChecked(true);
//                    server = "node";
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//        int itemId = item.getItemId();
//
//        if (item.isChecked()) {
//            if (R.id.action_320 == itemId)     //Individual checkbox logic
//            {   /*TODO unchecked Action*/}
//            item.setChecked(false);                   //Toggles checkbox state.
//        } else {
//            if (R.id.action_320 == itemId)    //Individual checkbox logic
//            {/*TODO checked Action*/}
//            item.setChecked(true);                   //Toggles checkbox state.
//        }
////        return true;
//        return super.onContextItemSelected(item);
//
//
////        switch (item.getItemId()) {
////            case R.id.edit:
////                return true;
////
////            default:
////                return super.onContextItemSelected(item);
////        }
//    }

}