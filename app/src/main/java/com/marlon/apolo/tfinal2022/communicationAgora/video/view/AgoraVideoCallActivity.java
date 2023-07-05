package com.marlon.apolo.tfinal2022.communicationAgora.video.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Random;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IAudioEffectManager;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.ScreenCaptureParameters;
import io.agora.rtc2.internal.EncryptionConfig;
import io.agora.rtc2.internal.LastmileProbeConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class AgoraVideoCallActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };
    private static final String TAG = AgoraVideoCallActivity.class.getSimpleName();
    // Fill the App ID of your project generated on Agora Console.
//    private final String appId = "7c0b693ccee54bcdb935f23c984dc2aa";
    // Fill the channel name.
    private String channelName = "";

    private String appId = "7c0b693ccee54bcdb935f23c984dc2aa";

    // Fill the temp token generated on Agora Console.
//    private String token = "007eJxTYEiR29IX+SLkKA+zHddVTbfTUicF9NcVTH6/d+vUlK0hLvkKDObJBklmlsbJyamppiZJySlJlsamaUbGyZYWJinJRomJwlGTkxsCGRnOZecxMEIhiM/MUJJfxMAAANRUHjk=";
    private String token = "";
    // An integer that identifies the local user.
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;
    private VideoEncoderConfiguration videoConfigLocal;

    private int flagexit;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onVideoStopped() {
            super.onVideoStopped();
            runOnUiThread(() -> Toast.makeText(AgoraVideoCallActivity.this, "onVideoStopped", Toast.LENGTH_SHORT).show());

        }

        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            //showMessage("Remote user joined " + uid);

            remoteUid = uid;
            // Set the remote video view
//            runOnUiThread(() -> setupRemoteVideo(uid));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                    stopPlaying();

                }
            });

        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;

//            showMessage("Joined Channel " + channel);

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    /*Pinreles*//*Re mal che*/
////                    linLytRemoteUser.setVisibility(View.GONE);
////                    textViewRemoteState.setVisibility(View.GONE);
////                    textViewRemoteUser.setVisibility(View.GONE);
////                    imageViewRemoteUser.setVisibility(View.GONE);
//                    /**/
//                }
//            });

        }

        @Override
        public void onUserOffline(int uid, int reason) {
//            showMessage("Remote user offline " + uid + " " + reason);
//            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linLytRemoteUser.setVisibility(View.GONE);
                    textViewRemoteState.setText("Llamada finalizada");
                    textViewRemoteState.setVisibility(View.VISIBLE);
                    textViewRemoteUser.setVisibility(View.GONE);
                    imageViewRemoteUser.setVisibility(View.GONE);
                    linLytRemoteUser.setVisibility(View.VISIBLE);

                    onRemoteUserLeft(uid);
                }
            });
        }

        // Listen for the event that the token is about to expire
        @Override
        public void onTokenPrivilegeWillExpire(String token) {
            Log.i("i", "Token Will expire");
//            fetchToken(uid, channelName, tokenRole);
            super.onTokenPrivilegeWillExpire(token);
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            String msg = "Connection state changed"
                    + "\n New state: " + state
                    + "\n Reason: " + reason;

//            showMessage(msg);
            Log.d(TAG, msg);
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
//            showMessage("Downlink jitter: " + result.downlinkReport.jitter);
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

            if (msg.length() > 0) {
                Log.d(TAG, msg);
//                showMessage(msg);
            }
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            String msg = "Remote video state changed: \n Uid =" + uid
                    + " \n NewState =" + state
                    + " \n reason =" + reason
                    + " \n elapsed =" + elapsed;

            Log.d(TAG, msg);
//            showMessage(msg);
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
                Log.d(TAG, msg);
//                showMessage(msg);
            }
        }


        @Override
        public void onVideoSubscribeStateChanged(String channel, int uid, int oldState, int newState, int elapseSinceLastState) {
            super.onVideoSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState);
            String msg = "onVideoSubscribeStateChanged: "
                    + "\n channel =" + channel
                    + "\n uid =" + uid
                    + "\n oldState =" + oldState
                    + "\n newState =" + newState;
            if (oldState == 1) {
                runOnUiThread(() -> deleteRemoteVideo(uid));
            }
            Log.d(TAG, msg);
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, String.valueOf(uid));
                    Log.d(TAG, String.valueOf(muted));

                    if (!muted && flagexit >= 1) {
//                        onRemoteUserVideoMuted(uid, muted);
                        surfaceViewRemote.setVisibility(View.VISIBLE);
                        linLytRemoteUser.setVisibility(View.GONE);
                        imageViewRemoteUser.setVisibility(View.GONE);
                        textViewRemoteUser.setVisibility(View.GONE);
                        textViewRemoteState.setVisibility(View.GONE);
                    }
                    if (muted && uid == remoteUid && flagexit >= 1) {
//                        onRemoteUserVideoMuted(uid, muted);
                        linLytRemoteUser.setVisibility(View.VISIBLE);
                        imageViewRemoteUser.setVisibility(View.VISIBLE);
                        textViewRemoteUser.setVisibility(View.VISIBLE);
                        textViewRemoteState.setVisibility(View.GONE);
                        onRemoteUserVideoMuted(uid, muted);

                    }

                    flagexit++;
//                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };
    private SurfaceView surfaceViewRemote;

    private void onRemoteUserVideoMuted(int uid, boolean muted) {

//        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceViewRemote.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceViewRemote.setVisibility(muted ? View.GONE : View.VISIBLE);
            //surfaceView.setVisibility(View.GONE);
        }
//        Toast.makeText(AgoraVideoCallActivity.this, "MUTEANDO VIDEO", Toast.LENGTH_SHORT).show();

        //parent.removeView(mRemoteVideo.view);
    }

    private String externalServer;
    // The base URL to your token server, for example, "https://agora-token-service-production-92ff.up.railway.app".
    private String serverUrl = "";

    private String nodeJsUrl = "https://s3rv3rsid3.herokuapp.com";
    private String javaUrl = "https://authwitouthauth.herokuapp.com";

    private int tokenRole; // The token role: Broadcaster or Audience
    private int tokenExpireTime = 3600; // Expire time in Seconds.
    private EditText editChannelName;
    private FrameLayout mLocalContainer;
    private FrameLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    private TextView networkStatus; // For updating the network status
    private int counter1 = 0; // Controls the frequency of messages
    private int counter2 = 0; // Controls the frequency of messages
    private int remoteUid; // Uid of the remote user
    private boolean highQuality = true; // Quality of the remote video stream being played
    private boolean isEchoTestRunning = false; // Keeps track of the echo test
    private Button echoTestButton;

    // Volume Control
    private SeekBar volumeSeekBar;
    private CheckBox muteCheckBox;
    private int volume = 50;
//    private int remoteUid = 0; // Stores the uid of the remote user

    // Screen sharing
    private final int DEFAULT_SHARE_FRAME_RATE = 10;
    private boolean isSharingScreen = false;
    private Intent fgServiceIntent;

    private IAudioEffectManager audioEffectManager;
    private final int soundEffectId = 1; // Unique identifier for the sound effect file
    private String soundEffectFilePath = "https://www.soundjay.com/human/applause-01.mp3"; // URL or path to the sound effect
    private int soundEffectStatus = 0;
    private int voiceEffectIndex = 0;
    private boolean audioPlaying = false; // Manage the audio mixing state
    private String audioFilePath = "https://www.kozco.com/tech/organfinale.mp3"; // URL or path to the audio mixing file

    private Button playEffectButton, voiceEffectButton;
    private SwitchCompat speakerphoneSwitch;
    private ImageButton joinButton;
    private ImageButton leaveButton;
    private ImageButton muteButton;
    private ImageButton unMuteButton;
    private String idVideoCall;
    private Usuario usuarioRemoto;
    private Usuario usuarioLocal;
    private String callStatus = "";
    private String joinRemoteStatus = "";
    private LlamadaVideo llamadaVideoRemota;
    private ChildEventListener childEventListenerLlamar;
    private ChildEventListener childEventListenerResponder;
    private LinearLayout linLytRemoteUser;
    private ImageView imageViewRemoteUser;
    private TextView textViewRemoteUser;
    private TextView textViewRemoteState;

    private ImageView imageButtonEnableVideo;
    private ImageView imageButtonDisableVideo;
    private boolean value;
    private MediaPlayer mediaPlayerCallTone;
    private AlertDialog dialogSDKLoco;
    private ViewGroup parent;

    private void updateNetworkStatus(int quality) {
        if (quality > 0 && quality < 3) networkStatus.setBackgroundColor(Color.GREEN);
        else if (quality <= 4) networkStatus.setBackgroundColor(Color.YELLOW);
        else if (quality <= 6) networkStatus.setBackgroundColor(Color.RED);
        else networkStatus.setBackgroundColor(Color.WHITE);
    }


    private void deleteRemoteVideo(int uid) {

    }

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


    private boolean checkSelfPermission() {
//        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        }
//        return true;


        value = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
                value = true;
//                } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            } else if (shouldShowRequestPermissionRationale(REQUESTED_PERMISSIONS[0]) && shouldShowRequestPermissionRationale(REQUESTED_PERMISSIONS[1])) {
                Snackbar.make(joinButton, "Permiso de micrófono necesario",
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
//                        Toast.makeText(getApplicationContext(), "GG permisos", Toast.LENGTH_LONG).show();
//                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                        ActivityCompat.requestPermissions(AgoraVideoCallActivity.this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

                    }
                }).show();
            } else {
//                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                ActivityCompat.requestPermissions(AgoraVideoCallActivity.this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else if (shouldShowRequestPermissionRationale(REQUESTED_PERMISSIONS[0]) && shouldShowRequestPermissionRationale(REQUESTED_PERMISSIONS[1])) {
                Snackbar.make(joinButton, "Permiso de micrófono necesario",
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
//                        Toast.makeText(getApplicationContext(), "GG permisos", Toast.LENGTH_LONG).show();
//                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                        if (ContextCompat.checkSelfPermission(AgoraVideoCallActivity.this, REQUESTED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                            value = true;
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgoraVideoCallActivity.this);
//        builder.setTitle("");
                            builder.setMessage(R.string.permiso_call_text);
                            // Add the buttons
                            builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    Toast.makeText(AgoraVideoCallActivity.this, "Permiso de audio NO concedido!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                            // Set other dialog properties

                            // Create the AlertDialog
                            dialogSDKLoco = builder.create();
                            dialogSDKLoco.show();
                        }


                    }
                }).show();
            } else {
//                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                AlertDialog.Builder builder = new AlertDialog.Builder(AgoraVideoCallActivity.this);
//        builder.setTitle("");
                builder.setMessage(R.string.permiso_call_text);
                // Add the buttons
                builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties

                // Create the AlertDialog
                dialogSDKLoco = builder.create();
                dialogSDKLoco.show();
            }
        }


//        return false;
        return value;


    }

    public void closeDialog() {
        try {
            dialogSDKLoco.dismiss();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
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
            agoraEngine.enableVideo();

            // Enable the dual stream mode
            agoraEngine.enableDualStreamMode(true);
// Set audio profile and audio scenario.
//            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_GAME_STREAMING);

            // Specify the audio scenario and audio profile
            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO);
            agoraEngine.setAudioScenario(Constants.AUDIO_SCENARIO_GAME_STREAMING);


            // Set up the audio effects manager
            audioEffectManager = agoraEngine.getAudioEffectManager();
// Pre-load sound effects to improve performance
            audioEffectManager.preloadEffect(soundEffectId, soundEffectFilePath);


// Set the video profile
            videoConfigLocal = new VideoEncoderConfiguration();
// Set mirror mode
            videoConfigLocal.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
// Set bitrate
            videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
// Set orientation mode
            videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
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


//    private void setupRemoteVideo(int uid) {
//        FrameLayout container = findViewById(R.id.remote_video_view_container);
//        remoteSurfaceView = new SurfaceView(getBaseContext());
//        remoteSurfaceView.setZOrderMediaOverlay(true);
//        container.addView(remoteSurfaceView);
//        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
//        // Display RemoteSurfaceView.
//        remoteSurfaceView.setVisibility(View.VISIBLE);
//    }


//    private void setupLocalVideo() {
//        FrameLayout container = findViewById(R.id.local_video_view_container);
//        // Create a SurfaceView object and add it as a child to the FrameLayout.
//        localSurfaceView = new SurfaceView(getBaseContext());
//        container.addView(localSurfaceView);
//        // Pass the SurfaceView object to Agora so that it renders the local video.
//        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
//    }


    private void setupRemoteVideo(int uid) {

//        Toast.makeText(this, "Setup remote video", Toast.LENGTH_SHORT).show();

//        /*Pinreles*/
//        linLytRemoteUser.setVisibility(View.GONE);
//        textViewRemoteState.setVisibility(View.GONE);
//        textViewRemoteUser.setVisibility(View.GONE);
//        imageViewRemoteUser.setVisibility(View.GONE);
//        /**/
//        ViewGroup parent = mRemoteContainer;
        parent = mRemoteContainer;
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
        surfaceViewRemote = RtcEngine.CreateRendererView(getBaseContext());
        surfaceViewRemote.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(surfaceViewRemote);
        mRemoteVideo = new VideoCanvas(surfaceViewRemote, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        agoraEngine.setupRemoteVideo(mRemoteVideo);
        surfaceViewRemote.setTag(uid); // for mark purpose

    }

//    private void setupRemoteVideo(int uid) {
//        FrameLayout container = findViewById(R.id.remote_video_view_container);
//        remoteSurfaceView = new SurfaceView(getBaseContext());
//        remoteSurfaceView.setZOrderMediaOverlay(true);
//        container.addView(remoteSurfaceView);
//        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
//        // Display RemoteSurfaceView.
//        remoteSurfaceView.setVisibility(View.VISIBLE);
//    }


    private void setupLocalVideo() {
//        Log.d(TAG, "setupVideoConfig");

        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        localSurfaceView = view;
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        agoraEngine.setupLocalVideo(mLocalVideo);
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

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
    }

    public void playingVideoCallAudioDialTone() {
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.beat_it_gameboy);
        mediaPlayerCallTone = MediaPlayer.create(this, R.raw.dialtone2);
        mediaPlayerCallTone.setLooping(true);
        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you
    }

    public void stopPlaying() {
        try {
            mediaPlayerCallTone.stop();
            if (mediaPlayerCallTone != null) mediaPlayerCallTone.release();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

    // Fetch the <Vg k="VSDK" /> token
    private void fetchToken(int uid, String channelName, int tokenRole) {


        joinButton.setVisibility(View.GONE);
        leaveButton.setVisibility(View.VISIBLE);
        switch (externalServer) {
            case "Java":
                AgoraTokenAsyncTaskWithJava agoraTokenAsyncTaskWithJava = new AgoraTokenAsyncTaskWithJava(AgoraVideoCallActivity.this,
                        isJoined,
                        agoraEngine,
                        channelName,
                        uid);

                agoraTokenAsyncTaskWithJava.execute(javaUrl);
                break;
            case "Node.js":
                AgoraTokenAsyncTaskWithNodeJs agoraTokenAsyncTaskWithNodeJs = new AgoraTokenAsyncTaskWithNodeJs(AgoraVideoCallActivity.this,
                        isJoined,
                        agoraEngine,
                        channelName,
                        uid);

                agoraTokenAsyncTaskWithNodeJs.execute(nodeJsUrl);
                break;
        }
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
        //showMessage("Running the last mile probe test ...");
    }

    // In a production environment, you retrieve the key and salt from
// an authentication server. For this code example you generate them locally.

    // A 32-byte string for encryption.
    private String encryptionKey = "bba451a33aa46dbcf3e37e4ca8638655870c73e4ac2be89b9165f09d87784b96";
    // A 32-byte string in Base64 format for encryption.
    private String encryptionSaltBase64 = "OmWvZqKPHAbDdJE7AdznDfGbSKFlH3y2L6fYnG6lRIY=";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enableEncryption() {
        Log.d(TAG, "Enable video encryption...");
        if (encryptionSaltBase64 == null || encryptionKey == null)
            return;
        // Convert the salt string into bytes
        byte[] encryptionSalt = Base64.getDecoder().decode(encryptionSaltBase64);
        // An object to specify encryption configuration.
        EncryptionConfig config = new EncryptionConfig();
        // Specify an encryption mode.
        config.encryptionMode = EncryptionConfig.EncryptionMode.AES_128_GCM2;
        // Set secret key and salt.
        config.encryptionKey = encryptionKey;
        System.arraycopy(encryptionSalt, 0, config.encryptionKdfSalt, 0, config.encryptionKdfSalt.length);
        // Call the method to enable media encryption.
        if (agoraEngine.enableEncryption(true, config) == 0) {
//            Toast.makeText(getApplicationContext(), "Media encryption enabled", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Media encryption enabled");

        }
    }


//    public void joinChannel(View view) {
//        //view.setVisibility(View.GONE);
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

    public void setStreamQuality(View view) {
        highQuality = !highQuality;

        if (highQuality) {
            agoraEngine.setRemoteVideoStreamType(remoteUid, Constants.VIDEO_STREAM_HIGH);
//            showMessage("Switching to high-quality video");
            showMessage("Cambiando calidad de video: alta-baja");
        } else {
            agoraEngine.setRemoteVideoStreamType(remoteUid, Constants.VIDEO_STREAM_LOW);
//            showMessage("Switching to low-quality video");
            showMessage("Cambiando calidad de video: baja-alta");
        }
    }


    public void joinChannel(View view) {
//        channelName = editChannelName.getText().toString();
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
//            localSurfaceView.setVisibility(View.VISIBLE);
            fetchToken(uid, channelName, tokenRole);
        } else {
//            showMessage("Permissions was not granted");
            showMessage("Permiso de cámara y micrófono no concedido");
        }
    }

    public void joinChannelLocal() {
//        channelName = editChannelName.getText().toString();
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
//            localSurfaceView.setVisibility(View.VISIBLE);
            fetchToken(uid, channelName, tokenRole);
        } else {
//            showMessage("Permissions was not granted");
            showMessage("Permiso de cámara y micrófono no concedido");

        }
    }


    public void leaveChannel(View view) {


        if (!isJoined) {
            showMessage("Join a channel first");
        } else {
            leaveButton.setVisibility(View.GONE);
            //joinButton.setVisibility(View.VISIBLE);

            try {

                switch (callStatus) {
                    case "llamadaEntrante":
                        linLytRemoteUser.setVisibility(View.GONE);
                        textViewRemoteState.setText("Llamada finalizada");
                        textViewRemoteState.setVisibility(View.VISIBLE);
                        textViewRemoteUser.setVisibility(View.GONE);
                        imageViewRemoteUser.setVisibility(View.GONE);
                        linLytRemoteUser.setVisibility(View.VISIBLE);
                        // Toast.makeText(getApplicationContext(), "Llamada remota finalizada", Toast.LENGTH_LONG).show();
                        removeChildEventListenerContestar();
                        break;
                    case "llamadaSaliente":
                        stopPlaying();
                        linLytRemoteUser.setVisibility(View.GONE);
                        textViewRemoteState.setText("Llamada finalizada");
                        textViewRemoteState.setVisibility(View.VISIBLE);
                        textViewRemoteUser.setVisibility(View.GONE);
                        imageViewRemoteUser.setVisibility(View.GONE);
                        linLytRemoteUser.setVisibility(View.VISIBLE);
                        //   Toast.makeText(getApplicationContext(), "Llamada local finalizada", Toast.LENGTH_LONG).show();
                        removeChildEventListenerLlamar();

//                        createVideoCallOnFirebase(channelName, uid);
                        break;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());

            }


            finishVideoCall(idVideoCall);


            removeFromParent(mLocalVideo);
            mLocalVideo = null;
            removeFromParent(mRemoteVideo);
            mRemoteVideo = null;

            agoraEngine.leaveChannel();
            //showMessage("You left the channel");
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);

        }
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

    public void switchCamera(View view) {
        agoraEngine.switchCamera();
    }

    public void changeSettings(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu_video, popup.getMenu());
        popup.show();
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_java:
                if (checked) {
                    // Pirates are the best
                    Toast.makeText(AgoraVideoCallActivity.this,
                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    externalServer = "Java";
                }
                break;
            case R.id.radio_nodejs:
                if (checked) {
                    // Ninjas rule
                    Toast.makeText(AgoraVideoCallActivity.this,
                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    externalServer = "Node.js";
                }
                break;
            case R.id.radio_360p:
                if (checked) {
//                    Toast.makeText(AgoraVideoCallActivity.this,
//                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    // Set the video profile
//                    VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
//                    videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
// Set bitrate
//                    videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
// Set orientation mode
                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//                    videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
                    changeQualityVideoQuality(videoConfigLocal);
                }
                break;

            case R.id.radio_480p:
                if (checked) {
                    Toast.makeText(AgoraVideoCallActivity.this,
                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    // Set the video profile
//                    VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
//                    videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
//                    videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x480;
// Set orientation mode
                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//                    videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
                    changeQualityVideoQuality(videoConfigLocal);
                }
                break;
            case R.id.radio_720p:
                if (checked) {
//                    Toast.makeText(AgoraVideoCallActivity.this,
//                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    // Set the video profile
//                    VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
//                    videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
//                    videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
// Set orientation mode
                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//                    videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
                    changeQualityVideoQuality(videoConfigLocal);
                }
                break;
            case R.id.radio_1080p:
                if (checked) {
                    Toast.makeText(AgoraVideoCallActivity.this,
                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    // Set the video profile
//                    VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
//                    videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
//                    videoConfigLocal.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1920x1080;
// Set orientation mode
                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//                    videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
                    changeQualityVideoQuality(videoConfigLocal);
                }
                break;
        }
    }

    private void changeQualityVideoQuality(VideoEncoderConfiguration videoConfig) {
        Toast.makeText(getApplicationContext(), "Cambiando calidad de video...", Toast.LENGTH_SHORT).show();
        agoraEngine.setVideoEncoderConfiguration(videoConfig);
    }

    public void shareScreen(View view) {
        try {
            Button sharingButton = (Button) view;

            if (!isSharingScreen) { // Start sharing
                // Ensure that your Android version is Lollipop or higher.
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        fgServiceIntent = new Intent(this, MainActivity.class);
                        startForegroundService(fgServiceIntent);
                    }
                    // Get the screen metrics
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    // Set screen capture parameters
                    ScreenCaptureParameters screenCaptureParameters = new ScreenCaptureParameters();
                    screenCaptureParameters.captureVideo = true;
                    screenCaptureParameters.videoCaptureParameters.width = metrics.widthPixels;
                    screenCaptureParameters.videoCaptureParameters.height = metrics.heightPixels;
                    screenCaptureParameters.videoCaptureParameters.framerate = DEFAULT_SHARE_FRAME_RATE;
                    screenCaptureParameters.captureAudio = true;
                    screenCaptureParameters.audioCaptureParameters.captureSignalVolume = 50;

                    // Start screen sharing
                    agoraEngine.startScreenCapture(screenCaptureParameters);
                    isSharingScreen = true;
                    startScreenSharePreview();
                    // Update channel media options to publish the screen sharing video stream
                    updateMediaPublishOptions(true);
                    sharingButton.setText("Stop Screen Sharing");
                }
            } else { // Stop sharing
                agoraEngine.stopScreenCapture();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (fgServiceIntent != null) stopService(fgServiceIntent);
                }
                isSharingScreen = false;
                sharingButton.setText("Start Screen Sharing");

                // Restore camera and microphone publishing
                updateMediaPublishOptions(false);
                setupLocalVideo();
            }
        } catch (Exception e) {
            Log.e(TAG, "########################################");
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }


    }


    private void startScreenSharePreview() {
        // Create render view by RtcEngine
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = new SurfaceView(getBaseContext());
        if (container.getChildCount() > 0) {
            container.removeAllViews();
        }
        // Add to the local container
        container.addView(localSurfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // Setup local video to render your local camera preview
        agoraEngine.setupLocalVideo(new VideoCanvas(surfaceView, Constants.RENDER_MODE_FIT,
                Constants.VIDEO_MIRROR_MODE_DISABLED,
                Constants.VIDEO_SOURCE_SCREEN_PRIMARY,
                0));

        agoraEngine.startPreview(Constants.VideoSourceType.VIDEO_SOURCE_SCREEN_PRIMARY);
    }

    void updateMediaPublishOptions(boolean publishScreen) {
        ChannelMediaOptions mediaOptions = new ChannelMediaOptions();
        mediaOptions.publishCameraTrack = !publishScreen;
        mediaOptions.publishMicrophoneTrack = !publishScreen;
        mediaOptions.publishScreenCaptureVideo = publishScreen;
        mediaOptions.publishScreenCaptureAudio = publishScreen;
        agoraEngine.updateChannelMediaOptions(mediaOptions);
    }

    public void audioMixing(View view) {
        Button startStopButton = (Button) findViewById(R.id.AudioMixingButton);
        audioPlaying = !audioPlaying;

        if (audioPlaying) {
            startStopButton.setText("Stop playing audio");
            try {
                agoraEngine.startAudioMixing(audioFilePath, false, 1, 0);
                showMessage("Audio playing");
            } catch (Exception e) {
                showMessage("Exception playing audio" + "\n" + e.toString());
            }
        } else {
            agoraEngine.stopAudioMixing();
            startStopButton.setText("Play Audio");
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChanged() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                agoraEngine.setDefaultAudioRoutetoSpeakerphone(false); // Disables the default audio route.
                agoraEngine.setEnableSpeakerphone(isChecked); // Enables or disables the speakerphone temporarily.

                // Enables/Disables the audio playback route to the speakerphone.
                //
                // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
                agoraEngine.setEnableSpeakerphone(isChecked);
            }
        };
    }


    public void playSoundEffect(View view) {
        if (playEffectButton == null) playEffectButton = (Button) view;
        if (soundEffectStatus == 0) { // Stopped
            audioEffectManager.playEffect(
                    soundEffectId,   // The ID of the sound effect file.
                    soundEffectFilePath,   // The path of the sound effect file.
                    0,  // The number of sound effect loops. -1 means an infinite loop. 0 means once.
                    1,   // The pitch of the audio effect. 1 represents the original pitch.
                    0.0, // The spatial position of the audio effect. 0.0 represents that the audio effect plays in the front.
                    100, // The volume of the audio effect. 100 represents the original volume.
                    true,// Whether to publish the audio effect to remote users.
                    0    // The playback starting position of the audio effect file in ms.
            );
            playEffectButton.setText("Pause audio effect");
            soundEffectStatus = 1;
        } else if (soundEffectStatus == 1) { // Playing
            audioEffectManager.pauseEffect(soundEffectId);
            soundEffectStatus = 2;
            playEffectButton.setText("Resume audio effect");
        } else if (soundEffectStatus == 2) { // Paused
            audioEffectManager.resumeEffect(soundEffectId);
            soundEffectStatus = 1;
            playEffectButton.setText("Pause audio effect");
        }
    }


    public void applyVoiceEffect(View view) {
        if (voiceEffectButton == null) voiceEffectButton = (Button) view;
        voiceEffectIndex++;
        // Turn off all previous effects
        agoraEngine.setVoiceBeautifierPreset(Constants.VOICE_BEAUTIFIER_OFF);
        agoraEngine.setAudioEffectPreset(Constants.AUDIO_EFFECT_OFF);
        agoraEngine.setVoiceConversionPreset(Constants.VOICE_CONVERSION_OFF);

        if (voiceEffectIndex == 1) {
            agoraEngine.setVoiceBeautifierPreset(Constants.CHAT_BEAUTIFIER_MAGNETIC);
            voiceEffectButton.setText("Voice effect: Chat Beautifier");
        } else if (voiceEffectIndex == 2) {
            agoraEngine.setVoiceBeautifierPreset(Constants.SINGING_BEAUTIFIER);
            voiceEffectButton.setText("Voice effect: Singing Beautifier");
        } else if (voiceEffectIndex == 3) {
            agoraEngine.setAudioEffectPreset(Constants.VOICE_CHANGER_EFFECT_HULK);
            voiceEffectButton.setText("Voice effect: Hulk");
        } else if (voiceEffectIndex == 4) {
            agoraEngine.setVoiceConversionPreset(Constants.VOICE_CHANGER_BASS);
            voiceEffectButton.setText("Voice effect: Voice Changer");
        } else if (voiceEffectIndex == 5) {
            // Sets the local voice equalization.
            // The first parameter sets the band frequency. The value ranges between 0 and 9.
            // Each value represents the center frequency of the band:
            // 31, 62, 125, 250, 500, 1k, 2k, 4k, 8k, and 16k Hz.
            // The second parameter sets the gain of each band between -15 and 15 dB.
            // The default value is 0.
            agoraEngine.setLocalVoiceEqualization(Constants.AUDIO_EQUALIZATION_BAND_FREQUENCY.fromInt(4), 3);
            agoraEngine.setLocalVoicePitch(0.5);
            voiceEffectButton.setText("Voice effect: Voice Equalization");
        } else if (voiceEffectIndex > 5) { // Remove all effects
            voiceEffectIndex = 0;
            agoraEngine.setLocalVoicePitch(1.0);
            agoraEngine.setLocalVoiceEqualization(Constants.AUDIO_EQUALIZATION_BAND_FREQUENCY.fromInt(4), 0);
            voiceEffectButton.setText("Apply voice effect");
        }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_agora_video_call);


        flagexit = 0;
        joinButton = findViewById(R.id.joinButton);
        leaveButton = findViewById(R.id.leaveButton);
        muteButton = findViewById(R.id.micMute);
        unMuteButton = findViewById(R.id.micUnmute);

        editChannelName = findViewById(R.id.editChannelName);
        networkStatus = findViewById(R.id.networkStatus);
        echoTestButton = findViewById(R.id.echoTestButton);

        linLytRemoteUser = findViewById(R.id.linLytRemoteUser);
        imageViewRemoteUser = findViewById(R.id.imageViewRemote);
        textViewRemoteUser = findViewById(R.id.textViewRemoteUser);
        textViewRemoteState = findViewById(R.id.textViewRemoteState);

        imageButtonEnableVideo = findViewById(R.id.enableCamera);
        imageButtonDisableVideo = findViewById(R.id.disableCamera);

        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        textViewRemoteUser.setTextColor(colorNight);
        textViewRemoteState.setTextColor(colorNight);
        imageButtonEnableVideo.setColorFilter(colorNight);
        imageButtonDisableVideo.setColorFilter(colorNight);
        /*Esto es una maravilla*/


        SwitchCompat speakerphoneSwitch = findViewById(R.id.SwitchSpeakerphone);
        speakerphoneSwitch.setOnCheckedChangeListener(onCheckedChanged());

        usuarioRemoto = (Usuario) getIntent().getSerializableExtra("usuarioRemoto");
        usuarioLocal = (Usuario) getIntent().getSerializableExtra("usuarioLocal");
        llamadaVideoRemota = (LlamadaVideo) getIntent().getSerializableExtra("llamadaVideo");
        joinRemoteStatus = getIntent().getStringExtra("extraJoin");

        callStatus = getIntent().getStringExtra("callStatus");
        channelName = getIntent().getStringExtra("channelName");


        linLytRemoteUser.setVisibility(View.GONE);


        ImageView iv = findViewById(R.id.activarAltavoz);
        iv.setSelected(true);
        iv.setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.MULTIPLY);

        try {
            Log.d(TAG, usuarioRemoto.toString());
            Log.d(TAG, usuarioLocal.toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = progress;
                agoraEngine.adjustRecordingSignalVolume(volume);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //Required to implement OnSeekBarChangeListener
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //Required to implement OnSeekBarChangeListener
            }
        });

        muteCheckBox = (CheckBox) findViewById(R.id.muteCheckBox);
        muteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                agoraEngine.muteRemoteAudioStream(remoteUid, isChecked);
            }
        });


        presetServer();

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        setupVideoSDKEngine();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            enableEncryption();
        }


        try {

            switch (callStatus) {
                case "llamadaEntrante":
//                    Toast.makeText(getApplicationContext(), callStatus + " : " + joinRemoteStatus, Toast.LENGTH_SHORT).show();
                    channelName = llamadaVideoRemota.getId();
                    textViewRemoteUser.setText(llamadaVideoRemota.getParticipanteCaller().getNombreParticipante());
                    textViewRemoteState.setText("Videollamada entrante...");
                    linLytRemoteUser.setVisibility(View.VISIBLE);

                    setListenerLlamadaRemota(llamadaVideoRemota);


//                    switch ()
//
                    if (joinRemoteStatus.equals("conectar")) {
                        joinChannel(findViewById(R.id.joinButton));
                    } else {

                    }


                    break;
                case "llamadaSaliente":
//                    Toast.makeText(getApplicationContext(), "Llamando", Toast.LENGTH_SHORT).show();
                    textViewRemoteUser.setText(String.format("%s %s", usuarioRemoto.getNombre(), usuarioRemoto.getApellido()));
                    textViewRemoteState.setText("Conectando...");
                    linLytRemoteUser.setVisibility(View.VISIBLE);
                    joinChannelLocal();
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_ID:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    try {
                        switch (callStatus) {
                            case "llamadaEntrante":
//                    Toast.makeText(getApplicationContext(), callStatus + " : " + joinRemoteStatus, Toast.LENGTH_SHORT).show();
                                channelName = llamadaVideoRemota.getId();
                                textViewRemoteUser.setText(llamadaVideoRemota.getParticipanteCaller().getNombreParticipante());
                                textViewRemoteState.setText("Llamada entrante...");
                                linLytRemoteUser.setVisibility(View.VISIBLE);

                                setListenerLlamadaRemota(llamadaVideoRemota);


//                    switch ()
//
                                if (joinRemoteStatus.equals("conectar")) {
                                    joinChannel(findViewById(R.id.joinButton));
                                } else {

                                }


                                break;
                            case "llamadaSaliente":
//                    Toast.makeText(getApplicationContext(), "Llamando", Toast.LENGTH_SHORT).show();
                                textViewRemoteUser.setText(String.format("%s %s", usuarioRemoto.getNombre(), usuarioRemoto.getApellido()));
                                textViewRemoteState.setText("Conectando...");
                                linLytRemoteUser.setVisibility(View.VISIBLE);
                                joinChannelLocal();
                                break;
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());

                    }
                    closeDialog();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(getApplicationContext(), "Permiso de micrófono NO concecido!", Toast.LENGTH_LONG).show();

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    private void setListenerLlamadaRemota(LlamadaVideo llamadaVideoRemota) {
        idVideoCall = llamadaVideoRemota.getId();

        //Toast.makeText(getApplicationContext(), "Listener llamnad local", Toast.LENGTH_SHORT).show();
        childEventListenerResponder = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        Toast.makeText(getApplicationContext(), "Remove call", Toast.LENGTH_LONG).show();
//                        finishRemoteCall();
                LlamadaVideo llamadaVideoRemoved = snapshot.getValue(LlamadaVideo.class);
                if (llamadaVideoRemoved.getId().equals(llamadaVideoRemota.getId())) {
                    finishRemoteCall();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .addChildEventListener(childEventListenerResponder);
    }

    private void presetServer() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupServer);

        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedId);

//        Toast.makeText(AgoraVideoCallActivity.this,
//                radioButton.getText(), Toast.LENGTH_SHORT).show();
        externalServer = radioButton.getText().toString();

        switch (externalServer) {
            case "Java":
                serverUrl = javaUrl;
                break;
            case "Node.js":
                serverUrl = nodeJsUrl;
                break;
        }
    }


    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        stopPlaying();

        finishVideoCall(idVideoCall);

        try {
            switch (callStatus) {
                case "llamadaEntrante":
//                    Toast.makeText(getApplicationContext(), "Llamada remota finalizada", Toast.LENGTH_LONG).show();
                    break;
                case "llamadaSaliente":
//                    Toast.makeText(getApplicationContext(), "Llamada local finalizada", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sd:
                if (item.isChecked()) {
                    item.setChecked(false);

                } else {
                    item.setChecked(true);

//                    Toast.makeText(AgoraVideoCallActivity.this,
//                            ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    // Set the video profile
//                    VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration();
// Set mirror mode
//                    videoConfig.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
// Set framerate
                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10.getValue();
// Set bitrate
//                    videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
// Set orientation mode
                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//                    videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
                    changeQualityVideoQuality(videoConfigLocal);
                }
                return true;

            case R.id.action_hd:
                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);

                    videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
//                    videoConfig.bitrate = VideoEncoderConfiguration.STANDARD_BITRATE;
// Set dimensions
                    videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
// Set orientation mode
                    videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//                    videoConfig.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
                    changeQualityVideoQuality(videoConfigLocal);
                }
                return true;
            default:
//                return super.onOptionsItemSelected(item);
                return false;
        }
//        return false;
    }

    public void enableCamera(View view) {
        imageButtonEnableVideo.setVisibility(View.GONE);
        imageButtonDisableVideo.setVisibility(View.VISIBLE);
//        agoraEngine.enableVideo();
        agoraEngine.muteLocalVideoStream(false);
        localSurfaceView.setVisibility(View.VISIBLE);

    }

    public void disableCamera(View view) {
        imageButtonEnableVideo.setVisibility(View.VISIBLE);
        imageButtonDisableVideo.setVisibility(View.GONE);
        localSurfaceView.setVisibility(View.GONE);
//        agoraEngine.disableVideo();/*deshabilita a los dos lados*/
        agoraEngine.muteLocalVideoStream(true);
    }


    private class AgoraTokenAsyncTaskWithNodeJs extends AsyncTask<String, Void, String> {

        private Context contextInstance;
        private boolean isJoined;
        private RtcEngine agoraEngine;
        private String channelName;
        private int uid;
        String tokenLocal;
        private String TAG = AgoraTokenAsyncTaskWithNodeJs.class.getSimpleName();

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
                Log.d(TAG, String.valueOf(uid));
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
            //closeProgressDialog();
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
                //createVideoCallOnFirebase(channelName, uid);


            } else { // Already joined, renew the token by calling renewToken
                agoraEngine.renewToken(token);
                showMessage("Token renewed");
            }
        }


    }


    private class AgoraTokenAsyncTaskWithJava extends AsyncTask<String, Void, String> {

        private Context contextInstance;
        private boolean isJoined;
        private RtcEngine agoraEngine;
        private String channelName;
        private int uid;
        private String tokenLocal;
        private String TAG = AgoraTokenAsyncTaskWithJava.class.getSimpleName();

        public AgoraTokenAsyncTaskWithJava(Context contextVar, boolean isJoinedVar, RtcEngine agoraEngineVar, String channelNameVar, int uidVar) {
            contextInstance = contextVar;
            isJoined = isJoinedVar;
            agoraEngine = agoraEngineVar;
            channelName = channelNameVar;
            uid = uidVar;
        }

        protected String doInBackground(String... urls) {

            Log.d(TAG, "Conectado al servidor Java...");


            // Generate a random number between 0 and 10
            Random r = new Random();
            uid = r.nextInt(11);

            try {
                String urlAgora = urls[0] + "/rtc/" + channelName + "/" + "publisher" + "/" + uid;

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
                Log.d(TAG, String.valueOf(uid));
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
//            closeProgressDialog();
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
//                try {

                try {

                    switch (callStatus) {
                        case "llamadaEntrante":
                            constestarLlamada(llamadaVideoRemota);
                            break;
                        case "llamadaSaliente":
                            createVideoCallOnFirebase(channelName, uid);
                            break;
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());

                }

//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }


                joinButton.setVisibility(View.GONE);
                leaveButton.setVisibility(View.VISIBLE);

            } else { // Already joined, renew the token by calling renewToken
                agoraEngine.renewToken(token);
                showMessage("Token renewed");
            }
        }


    }

    private void constestarLlamada(LlamadaVideo llamadaVideo) {


        idVideoCall = llamadaVideo.getId();

        llamadaVideo.setChannelConnectedStatus(true);
        llamadaVideo.setDestinyStatus(true);

//        Toast.makeText(getApplicationContext(), "Contestando", Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), llamadaVideo.toString(), Toast.LENGTH_LONG).show();

        FirebaseDatabase.getInstance().getReference()
                .child("videoCalls")
                .child(llamadaVideo.getId())
                .setValue(llamadaVideo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Contestando...");
                            linLytRemoteUser.setVisibility(View.GONE);
                            textViewRemoteState.setVisibility(View.GONE);
                            textViewRemoteUser.setVisibility(View.GONE);
                            imageViewRemoteUser.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error al realizar llamada.");
                        }
                    }
                });

//
//        FirebaseDatabase.getInstance().getReference().child("videoCalls")
//                .child(llamadaVideo.getId())
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


    }

    public void removeChildEventListenerLlamar() {
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .removeEventListener(childEventListenerLlamar);
    }

    public void removeChildEventListenerContestar() {
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .removeEventListener(childEventListenerLlamar);
    }


    public void createVideoCallOnFirebase(String channelName, int uid) {
        LlamadaVideo llamadaVideo = new LlamadaVideo();
        Participante participanteCaller = new Participante();
        Participante participanteDestiny = new Participante();
        participanteCaller.setIdParticipante(FirebaseAuth.getInstance().getCurrentUser().getUid());
        participanteCaller.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
        participanteCaller.setUriFotoParticipante(usuarioLocal.getFotoPerfil());

        participanteDestiny.setIdParticipante(usuarioRemoto.getIdUsuario());
        participanteDestiny.setNombreParticipante(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido());
        participanteDestiny.setUriFotoParticipante(usuarioRemoto.getFotoPerfil());

//        idVideoCall = FirebaseDatabase.getInstance().getReference().child("videoCalls").push().getKey();
        idVideoCall = channelName;
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
                            textViewRemoteState.setText("Llamando...");
                            playingVideoCallAudioDialTone();

                        } else {
                            Log.d(TAG, "Error al realizar llamada.");
                        }
                    }
                });

        childEventListenerLlamar = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVideo llamadaVideoChanged = snapshot.getValue(LlamadaVideo.class);
                if (llamadaVideoChanged.getId().equals(idVideoCall)) {
                    if (llamadaVideoChanged.isChannelConnectedStatus() && llamadaVideoChanged.isDestinyStatus()) {
                        linLytRemoteUser.setVisibility(View.GONE);
//                        stopPlaying();

                    }

                    //*llamada rechazada*/
                    if (llamadaVideoChanged.isRejectCallStatus()) {
                        linLytRemoteUser.setVisibility(View.GONE);
                        textViewRemoteState.setText("Llamada rechazada");
                        textViewRemoteState.setVisibility(View.VISIBLE);
                        textViewRemoteUser.setVisibility(View.GONE);
                        imageViewRemoteUser.setVisibility(View.GONE);
                        linLytRemoteUser.setVisibility(View.VISIBLE);

                        if (llamadaVideoChanged.isRejectCallStatus()) {
                            finishWithRejectLocalCall();
                        } else {
                            finishLocalCall();
                        }
                        finishVideoCall(idVideoCall);
                        stopPlaying();

                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                LlamadaVideo llamadaVideoRemoved = snapshot.getValue(LlamadaVideo.class);
                if (llamadaVideoRemoved.getId().equals(idVideoCall)) {
//                    finishLocalCall();
                    if (llamadaVideoRemoved.isRejectCallStatus()) {
                        finishWithRejectLocalCall();
                    } else {
                        finishLocalCall();
                    }
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
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .addChildEventListener(childEventListenerLlamar);
    }


    private void finishVideoCall(String idVideoCall) {
        try {
            FirebaseDatabase.getInstance().getReference()
                    .child("videoCalls")
                    .child(idVideoCall)
                    .setValue(null)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "ELiminando videollamada...");
                            } else {
                                Log.d(TAG, "Error al finalizar llamada.");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
//        FirebaseDatabase.getInstance().getReference()
//                .child("videoCalls")
//                .child(idVideoCall)
//                .setValue(null)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "ELiminando videollamada...");
//                        } else {
//                            Log.d(TAG, "Error al finalizar llamada.");
//                        }
//                    }
//                });
        // finish();
    }

    private void finishRemoteCall() {
        linLytRemoteUser.setVisibility(View.GONE);
        textViewRemoteState.setText("Llamada finalizada");
        textViewRemoteState.setVisibility(View.VISIBLE);
        textViewRemoteUser.setVisibility(View.GONE);
        imageViewRemoteUser.setVisibility(View.GONE);
        linLytRemoteUser.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void finishWithRejectLocalCall() {
//        linLytRemoteUser.setVisibility(View.GONE);
//        textViewRemoteState.setText("Llamada finalizada");
//        textViewRemoteState.setVisibility(View.VISIBLE);
//        textViewRemoteUser.setVisibility(View.GONE);
//        imageViewRemoteUser.setVisibility(View.GONE);
//        linLytRemoteUser.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void finishLocalCall() {
        linLytRemoteUser.setVisibility(View.GONE);
        textViewRemoteState.setText("Llamada finalizada");
        textViewRemoteState.setVisibility(View.VISIBLE);
        textViewRemoteUser.setVisibility(View.GONE);
        imageViewRemoteUser.setVisibility(View.GONE);
        linLytRemoteUser.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}