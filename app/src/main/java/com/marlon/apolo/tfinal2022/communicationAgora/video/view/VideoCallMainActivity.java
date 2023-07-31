package com.marlon.apolo.tfinal2022.communicationAgora.video.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Usuario;

import org.w3c.dom.Text;

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
import io.agora.rtc2.internal.EncryptionConfig;
import io.agora.rtc2.internal.LastmileProbeConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class VideoCallMainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };
    private static final String TAG = VideoCallMainActivity.class.getSimpleName();

    private String javaUrl = "https://authwitouthauth-a975f368522e.herokuapp.com";


    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "7c0b693ccee54bcdb935f23c984dc2aa";
    // Fill the channel name.
    private String channelName = "yoru";
    // Fill the temp token generated on Agora Console.
//    private String token = "007eJxTYJju31ux4Dnv8uLWK3sDEz3zT/jZTi5YcGryvQn+/NKeMroKDObJBklmlsbJyamppiZJySlJlsamaUbGyZYWJinJRomJt9cfSWkIZGQ4xxTCwAiFID4LQ2V+USkDAwBbDx/4";
    // An integer that identifies the local user.
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;
    private VideoCanvas mLocalVideo;
    private FrameLayout mLocalContainer;
    private VideoCanvas mRemoteVideo;

    private boolean highQuality = true; // Quality of the remote video stream being played
    private int remoteUid;
    private boolean activeCamera = true;
    private boolean activeMic = true;
    private boolean activeSpeaker = true;
    private boolean resolution = true;
    private IAudioEffectManager audioEffectManager;
    private VideoEncoderConfiguration videoConfigLocal;
    private TextView networkStatus; // For updating the network status
    private ImageView imageView360;
    private ImageView imageView720;
    private ImageView imageView1080;
    private int selectColorResolution;
    private ImageView imageViewWifi;
    private ImageView imageViewEncryption;
    private Usuario usuarioLocal;
    private Usuario usuarioRemoto;
    private String idVideoCall;
    private ChildEventListener childEventListenerLlamar;
    private ChildEventListener childEventListenerResponder;
    private LlamadaVideo llamadaVideoRemota;
    private String callStatus;
    private TextView textViewNameRemoteUser;
    private TextView textViewStateRemoteUser;
    private MediaPlayer mediaPlayerCallTone;
    private ImageButton imageButtonJoin, imageButtonLeave;

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

    private int counter1 = 0;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
//            showMessage("Remote user joined " + uid);

            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            //showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
//            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
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

            if (reason == 5) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        remoteSurfaceView.setVisibility(View.GONE);

                    }
                });
            }
            if (reason == 6) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        remoteSurfaceView.setVisibility(View.VISIBLE);
                    }
                });
            }
            Log.d(TAG, msg);
//            showMessage(msg);
        }

        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
//            counter2 += 1;

//            if (counter2 == 5) {
            String msg = "Remote Video Stats: "
                    + "\n User id =" + stats.uid
                    + "\n Received bitrate =" + stats.receivedBitrate
                    + "\n Total frozen time =" + stats.totalFrozenTime
                    + "\n Width =" + stats.width
                    + "\n Height =" + stats.height;
//                counter2 = 0;
            Log.d(TAG, msg);
//                showMessage(msg);
//            }
        }


        @Override
        public void onVideoSubscribeStateChanged(String channel, int uid, int oldState, int newState, int elapseSinceLastState) {
            super.onVideoSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState);
            String msg = "onVideoSubscribeStateChanged: "
                    + "\n channel =" + channel
                    + "\n uid =" + uid
                    + "\n oldState =" + oldState
                    + "\n newState =" + newState;
//            if (oldState == 1) {
//                runOnUiThread(() -> deleteRemoteVideo(uid));
//            }
            Log.d(TAG, msg);
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, String.valueOf(uid));
                    Log.d(TAG, String.valueOf(muted));

                    //remoteSurfaceView.setVisibility(View.GONE);
//                    if (!muted && flagexit >= 1) {
////                        onRemoteUserVideoMuted(uid, muted);
//                        surfaceViewRemote.setVisibility(View.VISIBLE);
//                        linLytRemoteUser.setVisibility(View.GONE);
//                        imageViewRemoteUser.setVisibility(View.GONE);
//                        textViewRemoteUser.setVisibility(View.GONE);
//                        textViewRemoteState.setVisibility(View.GONE);
//                    }
//                    if (muted && uid == remoteUid && flagexit >= 1) {
////                        onRemoteUserVideoMuted(uid, muted);
//                        linLytRemoteUser.setVisibility(View.VISIBLE);
//                        imageViewRemoteUser.setVisibility(View.VISIBLE);
//                        textViewRemoteUser.setVisibility(View.VISIBLE);
//                        textViewRemoteState.setVisibility(View.GONE);
//                        onRemoteUserVideoMuted(uid, muted);
//
//                    }
//
//                    flagexit++;
//                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }

    };


    private void setupRemoteVideo(int uid) {
        try {
            stopPlaying();
        } catch (Exception e) {

        }
        remoteUid = uid;
        FrameLayout parent = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
//        remoteSurfaceView.setZOrderMediaOverlay(true);

        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
//        if (mRemoteVideo != null) {
//            return;
//        }

        remoteSurfaceView.setZOrderMediaOverlay(parent == mLocalContainer);

        parent.addView(remoteSurfaceView);
        mRemoteVideo = new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid);

//        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        agoraEngine.setupRemoteVideo(mRemoteVideo);
        // Display RemoteSurfaceView.
        remoteSurfaceView.setVisibility(View.VISIBLE);

    }

    public void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = new SurfaceView(getBaseContext());
        localSurfaceView.setZOrderMediaOverlay(true);
        container.addView(localSurfaceView);
        mLocalVideo = new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0);

        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
//        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        agoraEngine.setupLocalVideo(mLocalVideo);

    }

    public void joinChannelButton(View view) {
//        joinChannelCaller();

        joinChannelDestiny();
    }

    public void joinChannelCaller() {
        if (checkSelfPermission()) {
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
//            isJoined = true;
            fetchCallerToken(uid, channelName);


        } else {
            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinChannelDestiny() {
        if (checkSelfPermission()) {
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
//            isJoined = true;
            imageButtonJoin.setVisibility(View.GONE);
            fetchDestinyToken(uid, channelName);


        } else {
            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLocalSurface() {
        localSurfaceView.setVisibility(View.VISIBLE);
    }


    public void leaveChannel(View view) {
        if (callStatus.equals("llamadaSaliente")) {
            if (!isJoined) {
//                showMessage("Join a channel first");
            } else {

                finishVideoLocalCall(idVideoCall);
            }
            try {
                stopPlaying();
            } catch (Exception e) {

            }
        } else {
            if (!isJoined) {
//                showMessage("Join a channel first");
                textViewStateRemoteUser.setText("Llamada rechazada");
                rejectVideoCall(llamadaVideoRemota);
            } else {
                agoraEngine.leaveChannel();
//                showMessage("You left the channel");
                // Stop remote video rendering.
                if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
                // Stop local video rendering.
                if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
                isJoined = false;
                finishVideoCall(idVideoCall);
            }

        }
    }

    private void rejectVideoCall(LlamadaVideo llamadaVideo) {
        llamadaVideo.setRejectCallStatus(true);
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .child(llamadaVideo.getId())
                .setValue(llamadaVideo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 2000);
                        }
                    }
                });
    }

    public void switchCamera(View view) {
        agoraEngine.switchCamera();
    }


    public void controlMic(View view) {
        if (activeMic) {
            //Toast.makeText(getApplicationContext(), "Desactivando micrófono", Toast.LENGTH_LONG).show();
            ((ImageButton) view).setBackgroundResource(R.drawable.mic_unmute_icon_behavior);
            agoraEngine.muteLocalAudioStream(true);

        } else {
            //Toast.makeText(getApplicationContext(), "Activando micrófono", Toast.LENGTH_LONG).show();
            ((ImageButton) view).setBackgroundResource(R.drawable.mic_mute_icon_behavior);
            agoraEngine.muteLocalAudioStream(false);


        }
        activeMic = !activeMic;
    }

    public void set360pResolution(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
//            Toast.makeText(getApplicationContext(), "Cambiando resolución a: 360p", Toast.LENGTH_LONG).show();

            iv.clearColorFilter();//
            iv.setSelected(false);

            imageView720.setSelected(true);
            imageView720.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);

            imageView1080.setSelected(true);
            imageView1080.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);


            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
            videoConfigLocal.bitrate = 400;
// Set dimensions
            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
// Set orientation mode
//        videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//        videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
            changeQualityVideoQuality(videoConfigLocal);
        }

    }

    public void set720pResolution(View view) {

        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
//            Toast.makeText(getApplicationContext(), "Cambiando resolución a: 720p", Toast.LENGTH_LONG).show();
            iv.clearColorFilter();//
            iv.setSelected(false);

            imageView360.setSelected(true);
            imageView360.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);

            imageView1080.setSelected(true);
            imageView1080.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);

            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
            videoConfigLocal.bitrate = 1130;
// Set dimensions
            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1280x720;
// Set orientation mode
//        videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//        videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
            changeQualityVideoQuality(videoConfigLocal);
        }

    }


    public void set1080pResolution(View view) {

        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
//            Toast.makeText(getApplicationContext(), "Cambiando resolución a: 1080p", Toast.LENGTH_LONG).show();
            iv.clearColorFilter();//
            iv.setSelected(false);

            imageView360.setSelected(true);
            imageView360.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);
            imageView720.setSelected(true);
            imageView720.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);

            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
// Set bitrate
            videoConfigLocal.bitrate = 2080;
// Set dimensions
            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_1920x1080;
// Set orientation mode
//        videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
// Set degradation preference
//        videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
// Apply the configuration
            changeQualityVideoQuality(videoConfigLocal);
        }

    }

    private void changeQualityVideoQuality(VideoEncoderConfiguration videoConfig) {
//        Toast.makeText(getApplicationContext(), "Cambiando calidad de video...", Toast.LENGTH_SHORT).show();
        agoraEngine.setVideoEncoderConfiguration(videoConfig);
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
//        showMessage("Running the last mile probe test ...");
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
//            audioEffectManager = agoraEngine.getAudioEffectManager();
// Pre-load sound effects to improve performance
//            audioEffectManager.preloadEffect(soundEffectId, soundEffectFilePath);


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
//            startProbeTest();


        } catch (Exception e) {
            showMessage(e.toString());
        }
    }

    // A 32-byte string for encryption.
    private String encryptionKey = "bba451a33aa46dbcf3e37e4ca8638655870c73e4ac2be89b9165f09d87784b96";
    // A 32-byte string in Base64 format for encryption.
    private String encryptionSaltBase64 = "OmWvZqKPHAbDdJE7AdznDfGbSKFlH3y2L6fYnG6lRIY=";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enableEncryption() {
//        showMessage("Enable encryption");
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
//            Log.d(TAG, "Media encryption enabled");
//            showMessage(String.valueOf(config.encryptionMode));
            imageViewEncryption.setImageResource(R.drawable.icon_encryption);
            imageViewEncryption.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        }
    }

    private void updateNetworkStatus(int quality) {
        //wifiAnimation.start();
//        if (quality > 0 && quality < 3) networkStatus.setBackgroundColor(Color.GREEN);
//        else if (quality <= 4) networkStatus.setBackgroundColor(Color.YELLOW);
//        else if (quality <= 6) networkStatus.setBackgroundColor(Color.RED);
//        else networkStatus.setBackgroundColor(Color.WHITE);

        if (quality > 0 && quality < 3)
            animationWifi(R.drawable.ic_baseline_signal_wifi_4_bar_24, Color.GREEN);
        else if (quality <= 4)
            animationWifi(R.drawable.ic_baseline_network_wifi_3_bar_24, Color.YELLOW);
        else if (quality <= 6)
            animationWifi(R.drawable.ic_baseline_network_wifi_2_bar_24, Color.RED);
        else animationWifi(R.drawable.ic_baseline_signal_wifi_0_bar_24, Color.WHITE);

    }

    public void animationWifi(int drawable, int color) {
        imageViewWifi.setImageResource(drawable);
        imageViewWifi.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void enableCamera(View view) {
//        agoraEngine.enableVideo();
        agoraEngine.muteLocalVideoStream(false);
        localSurfaceView.setVisibility(View.VISIBLE);

    }

    public void controlCamera(View view) {
//        localSurfaceView.setVisibility(View.GONE);
//        agoraEngine.disableVideo();/*deshabilita a los dos lados*/
        //agoraEngine.muteLocalVideoStream(true);
        if (activeCamera) {
            //Toast.makeText(getApplicationContext(), "Desactivando cámara", Toast.LENGTH_LONG).show();
            ((ImageButton) view).setBackgroundResource(R.drawable.enable_camera_icon_behavior);
            localSurfaceView.setVisibility(View.GONE);
            agoraEngine.muteLocalVideoStream(true);

        } else {
            //Toast.makeText(getApplicationContext(), "Activando cámara", Toast.LENGTH_LONG).show();
            ((ImageButton) view).setBackgroundResource(R.drawable.disable_camera_icon_behavior);
            agoraEngine.muteLocalVideoStream(false);
            localSurfaceView.setVisibility(View.VISIBLE);

        }
        activeCamera = !activeCamera;

    }

    public void onSwitchSpeakerphoneClicked(View view) {
        if (activeSpeaker) {
            //Toast.makeText(getApplicationContext(), "Desactivando altavoz", Toast.LENGTH_LONG).show();
            ((ImageButton) view).setBackgroundResource(R.drawable.speaker_high_icon_behavior);
            agoraEngine.setEnableSpeakerphone(false);


        } else {
            //Toast.makeText(getApplicationContext(), "Activando altavoz", Toast.LENGTH_LONG).show();
            ((ImageButton) view).setBackgroundResource(R.drawable.speaker_normal_icon_behavior);
            agoraEngine.setEnableSpeakerphone(true);
        }
        activeSpeaker = !activeSpeaker;


//        ImageView iv = (ImageView) view;
//        if (iv.isSelected()) {
//            Toast.makeText(getApplicationContext(), "Desactivando altavoz", Toast.LENGTH_LONG).show();
//            iv.setSelected(false);
//            iv.clearColorFilter();
//        } else {
//            Toast.makeText(getApplicationContext(), "Activando altavoz", Toast.LENGTH_LONG).show();
//            iv.setSelected(true);
//            iv.setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.MULTIPLY);
//        }
//
//        // Enables/Disables the audio playback route to the speakerphone.
//        //
//        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
//        agoraEngine.setEnableSpeakerphone(view.isSelected());
    }

//    public void setStreamQuality(View view) {
////        highQuality = !highQuality;
////        showMessage("Cambiando calidad de video: alta-baja");
//
//        if (highQuality) {
//            agoraEngine.setRemoteVideoStreamType(remoteUid, Constants.VIDEO_STREAM_LOW);
////            showMessage("Switching to high-quality video");
//            showMessage("Cambiando calidad de video: alta-baja");
//        } else {
//            agoraEngine.setRemoteVideoStreamType(remoteUid, Constants.VIDEO_STREAM_HIGH);
////            showMessage("Switching to low-quality video");
//            showMessage("Cambiando calidad de video: baja-alta");
//        }
//        highQuality = !highQuality;
//    }

    private void hideSystemBars() {


        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Configure the behavior of the hidden system bars.
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());

        }

//        // Configure the behavior of the hidden system bars
//        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        // Hide both the status bar and the navigation bar
//        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
//        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
    }

    public void setTextRemoteState(String mssg) {
        textViewStateRemoteUser.setText(mssg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_video_call_main);
        mLocalContainer = findViewById(R.id.local_video_view_container);
        networkStatus = findViewById(R.id.networkStatus);

        textViewNameRemoteUser = findViewById(R.id.textViewRemoteU);
        textViewStateRemoteUser = findViewById(R.id.textViewRemoteStateU);

        imageButtonJoin = findViewById(R.id.joinButton);
        imageButtonLeave = findViewById(R.id.leaveButton);

        imageView360 = findViewById(R.id.resolution_360p);
        imageView720 = findViewById(R.id.resolution_720p);
        imageView1080 = findViewById(R.id.resolution_1080p);

        imageViewEncryption = findViewById(R.id.imageViewEncryption);

        imageViewWifi = findViewById(R.id.imageViewWifiAnimation);
//        imageViewWifi.setBackgroundResource(R.drawable.wifi_animation);
//        wifiAnimation = (AnimationDrawable) imageViewWifi.getBackground();

        selectColorResolution = R.color.purple_200;

        imageView360.setSelected(false);
        imageView360.clearColorFilter();

        imageView1080.setSelected(true);
        imageView1080.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);

        imageView720.setSelected(true);
        imageView720.setColorFilter(getResources().getColor(selectColorResolution), PorterDuff.Mode.MULTIPLY);


        callStatus = getIntent().getExtras().getString("callStatus");


        setupVideoSDKEngine();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enableEncryption();
        } else {
            imageViewEncryption.setImageResource(R.drawable.icon_encryption);
            imageViewEncryption.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }

        if (callStatus.equals("llamadaSaliente")) {
//            showMessage("Llamada saliente");

            imageButtonJoin.setVisibility(View.GONE);
            usuarioRemoto = (Usuario) getIntent().getSerializableExtra("usuarioRemoto");
            usuarioLocal = (Usuario) getIntent().getSerializableExtra("usuarioLocal");
            channelName = getIntent().getExtras().getString("channelName");
            idVideoCall = channelName;

            textViewNameRemoteUser.setText(String.format("%s %s", usuarioRemoto.getNombre(), usuarioRemoto.getApellido()));
            textViewStateRemoteUser.setText(String.format("%s", "Conectando..."));

            //showMessage(channelName);
            // If all the permissions are granted, initialize the RtcEngine object and join a channel.
            if (!checkSelfPermission()) {
                ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
            }
            // Llamada se realiza cuando se abre el view
            joinChannelCaller();


        } else {
//            showMessage("Llamada entrante");

            llamadaVideoRemota = (LlamadaVideo) getIntent().getSerializableExtra("llamadaVideo");
            channelName = llamadaVideoRemota.getId();
            idVideoCall = llamadaVideoRemota.getId();
            //showMessage(llamadaVideoRemota.toString());

            if (llamadaVideoRemota.isDestinyStatus() && llamadaVideoRemota.isChannelConnectedStatus()) {
                imageButtonJoin.setVisibility(View.GONE);
                joinChannelDestiny();
            } else {
                textViewNameRemoteUser.setText(String.format("%s", llamadaVideoRemota.getParticipanteCaller().getNombreParticipante()));
                textViewStateRemoteUser.setText(String.format("%s", "Llamada entrante..."));
            }

        }


//        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
//        if (!checkSelfPermission()) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
//        }
//
//
//        setupVideoSDKEngine();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            enableEncryption();
//        } else {
//            imageViewEncryption.setImageResource(R.drawable.icon_encryption);
//            imageViewEncryption.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
//        }
    }

    protected void onDestroy() {
        super.onDestroy();

        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        if (callStatus.equals("llamadaSaliente")) {
            removeChildEventListenerLlamar();

        } else {
            try {
                removeChildEventListenerContestar();

            } catch (Exception e) {

            }

        }

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }

    private void fetchCallerToken(int uid, String channelName) {

//        joinButton.setVisibility(View.GONE);
//        leaveButton.setVisibility(View.VISIBLE);
        AgoraTokenAsyncTaskWithJavaCaller agoraTokenAsyncTaskWithJava = new AgoraTokenAsyncTaskWithJavaCaller(
                isJoined,
                agoraEngine,
                channelName,
                uid,
                VideoCallMainActivity.this);

        agoraTokenAsyncTaskWithJava.execute(javaUrl);

    }

    public static class AgoraTokenAsyncTaskWithJavaCaller extends AsyncTask<String, Void, String> {

        private VideoCallMainActivity videoCallMainActivity;
        private boolean isJoined;
        private RtcEngine agoraEngine;
        private String channelName;
        private int uid;
        private String tokenLocal;
        private String TAG = AgoraTokenAsyncTaskWithJavaCaller.class.getSimpleName();
        private String token;

        public AgoraTokenAsyncTaskWithJavaCaller(boolean isJoinedVar, RtcEngine agoraEngineVar, String channelNameVar, int uidVar, VideoCallMainActivity videoCallMainActivityVar) {
            videoCallMainActivity = videoCallMainActivityVar;
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
//            ChannelMediaOptions options = new ChannelMediaOptions();
//
//            // For a Video call, set the channel profile as COMMUNICATION.
//            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
//            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
//            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//            // Start local preview.
//            agoraEngine.startPreview();
//
//            // Join the channel with a token.
//            agoraEngine.joinChannel(token, channelName, uid, options);


                ChannelMediaOptions options = new ChannelMediaOptions();

                // For a Video call, set the channel profile as COMMUNICATION.
                options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
                // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
                options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                // Display LocalSurfaceView.
                videoCallMainActivity.setupLocalVideo();
                videoCallMainActivity.setLocalSurface();
                // Start local preview.
                agoraEngine.startPreview();
                // Join the channel with a temp token.
                // You need to specify the user ID yourself, and ensure that it is unique in the channel.
                agoraEngine.joinChannel(token, channelName, uid, options);
                videoCallMainActivity.createVideoCallOnFirebase(channelName, uid);


//            try {
//
//                switch (callStatus) {
//                    case "llamadaEntrante":
//                        constestarLlamada(llamadaVideoRemota);
//                        break;
//                    case "llamadaSaliente":
//                        createVideoCallOnFirebase(channelName, uid);
//                        break;
//                }
//            } catch (Exception e) {
//                Log.d(TAG, e.toString());
//
//            }


//                joinButton.setVisibility(View.GONE);
//                leaveButton.setVisibility(View.VISIBLE);

            } else { // Already joined, renew the token by calling renewToken
                agoraEngine.renewToken(token);
                videoCallMainActivity.showMessage("Token renewed");
            }
        }


    }


    private void fetchDestinyToken(int uid, String channelName) {

//        joinButton.setVisibility(View.GONE);
//        leaveButton.setVisibility(View.VISIBLE);
        AgoraTokenAsyncTaskWithJavaDestiny agoraTokenAsyncTaskWithJavaDestiny = new AgoraTokenAsyncTaskWithJavaDestiny(
                isJoined,
                agoraEngine,
                channelName,
                uid,
                VideoCallMainActivity.this,
                llamadaVideoRemota);

        agoraTokenAsyncTaskWithJavaDestiny.execute(javaUrl);

    }

    public static class AgoraTokenAsyncTaskWithJavaDestiny extends AsyncTask<String, Void, String> {

        private VideoCallMainActivity videoCallMainActivity;
        private boolean isJoined;
        private RtcEngine agoraEngine;
        private String channelName;
        private int uid;
        private String tokenLocal;
        private String TAG = AgoraTokenAsyncTaskWithJavaDestiny.class.getSimpleName();
        private String token;
        private LlamadaVideo llamadaVideo;

        public AgoraTokenAsyncTaskWithJavaDestiny(boolean isJoinedVar, RtcEngine agoraEngineVar, String channelNameVar, int uidVar, VideoCallMainActivity videoCallMainActivityVar, LlamadaVideo llamadaVideoVar) {
            videoCallMainActivity = videoCallMainActivityVar;
            isJoined = isJoinedVar;
            agoraEngine = agoraEngineVar;
            channelName = channelNameVar;
            uid = uidVar;
            llamadaVideo = llamadaVideoVar;
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
//            ChannelMediaOptions options = new ChannelMediaOptions();
//
//            // For a Video call, set the channel profile as COMMUNICATION.
//            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
//            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
//            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//            // Start local preview.
//            agoraEngine.startPreview();
//
//            // Join the channel with a token.
//            agoraEngine.joinChannel(token, channelName, uid, options);


                ChannelMediaOptions options = new ChannelMediaOptions();

                // For a Video call, set the channel profile as COMMUNICATION.
                options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
                // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
                options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                // Display LocalSurfaceView.
                videoCallMainActivity.setupLocalVideo();
                videoCallMainActivity.setLocalSurface();
                // Start local preview.
                agoraEngine.startPreview();
                // Join the channel with a temp token.
                // You need to specify the user ID yourself, and ensure that it is unique in the channel.
                agoraEngine.joinChannel(token, channelName, uid, options);
                videoCallMainActivity.constestarLlamada(llamadaVideo);


//            try {
//
//                switch (callStatus) {
//                    case "llamadaEntrante":
//                        constestarLlamada(llamadaVideoRemota);
//                        break;
//                    case "llamadaSaliente":
//                        createVideoCallOnFirebase(channelName, uid);
//                        break;
//                }
//            } catch (Exception e) {
//                Log.d(TAG, e.toString());
//
//            }


//                joinButton.setVisibility(View.GONE);
//                leaveButton.setVisibility(View.VISIBLE);

            } else { // Already joined, renew the token by calling renewToken
                agoraEngine.renewToken(token);
                videoCallMainActivity.showMessage("Token renewed");
            }
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
//                            showMessage("Llamando...");
                            textViewStateRemoteUser.setText("Llamando...");
                            playingVideoCallAudioDialTone();
                            setListenerLlamadaLocalCaller();

                        } else {
                            Log.d(TAG, "Error al realizar llamada.");
                        }
                    }
                });


        //setListenerLlamadaLocalCaller();
    }

    public void setListenerLlamadaLocalCaller() {
//        showMessage("Escuchando eventos de llamada en Firebase");
        childEventListenerLlamar = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVideo llamadaVideoChanged = snapshot.getValue(LlamadaVideo.class);
                if (llamadaVideoChanged.getId().equals(idVideoCall)) {
                    if (llamadaVideoChanged.isChannelConnectedStatus() && llamadaVideoChanged.isDestinyStatus()) {
                        //linLytRemoteUser.setVisibility(View.GONE);
                        //Los usuarios se conectan
                        textViewStateRemoteUser.setText("");
                        stopPlaying();
                    }

                    //*llamada rechazada*/
                    if (llamadaVideoChanged.isRejectCallStatus()) {
//                        linLytRemoteUser.setVisibility(View.GONE);
                        textViewStateRemoteUser.setText("Llamada rechazada");
//                        textViewRemoteState.setVisibility(View.VISIBLE);
//                        textViewRemoteUser.setVisibility(View.GONE);
//                        imageViewRemoteUser.setVisibility(View.GONE);
//                        linLytRemoteUser.setVisibility(View.VISIBLE);
//                        finishWithRejectLocalCall();
//                        if (llamadaVideoChanged.isRejectCallStatus()) {
//                            finishWithRejectLocalCall();
//                        } else {
                        finishVideoLocalCall(idVideoCall);
//                            stopPlaying();


                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                LlamadaVideo llamadaVideoRemoved = snapshot.getValue(LlamadaVideo.class);
                if (llamadaVideoRemoved.getId().equals(idVideoCall)) {
                    if (llamadaVideoRemoved.isRejectCallStatus()) {
                        finishWithRejectLocalCall();
                    } else {
                        finishLocalCall();
                    }
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

//    private void finishLocalCall() {
//        linLytRemoteUser.setVisibility(View.GONE);
//        textViewRemoteState.setText("Llamada finalizada");
//        textViewRemoteState.setVisibility(View.VISIBLE);
//        textViewRemoteUser.setVisibility(View.GONE);
//        imageViewRemoteUser.setVisibility(View.GONE);
//        linLytRemoteUser.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 2000);
//    }


    public void finishVideoLocalCall(String idVideoCall) {
        agoraEngine.leaveChannel();
//                showMessage("You left the channel");
        // Stop remote video rendering.
        if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
        // Stop local video rendering.
        if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
        isJoined = false;

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
//                                finish();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            } else {
                                Log.d(TAG, "Error al finalizar llamada.");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void finishVideoCall(String idVideoCall) {
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
//                                finish();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            } else {
                                Log.d(TAG, "Error al finalizar llamada.");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, e.toString());
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
                            //BUTTON
                            textViewStateRemoteUser.setText("");
                            setListenerLlamadaRemota(llamadaVideo);
//                            linLytRemoteUser.setVisibility(View.GONE);
//                            textViewRemoteState.setVisibility(View.GONE);
//                            textViewRemoteUser.setVisibility(View.GONE);
//                            imageViewRemoteUser.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error al realizar llamada.");
                        }
                    }
                });


    }


    private void setListenerLlamadaRemota(LlamadaVideo llamadaVideoRemota) {
        idVideoCall = llamadaVideoRemota.getId();

//        Toast.makeText(getApplicationContext(), "Escuchando eventos llamada remota", Toast.LENGTH_SHORT).show();
        childEventListenerResponder = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

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

    public void removeChildEventListenerLlamar() {
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .removeEventListener(childEventListenerLlamar);
    }

    public void removeChildEventListenerContestar() {
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .removeEventListener(childEventListenerResponder);
    }


    private void finishRemoteCall() {
        textViewStateRemoteUser.setText("Llamada finalizada");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void finishLocalCall() {
        textViewStateRemoteUser.setText("Llamada finalizada");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void finishWithRejectLocalCall() {
        try {
            stopPlaying();
        } catch (Exception e) {

        }
        textViewStateRemoteUser.setText("Llamada rechazada");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}


