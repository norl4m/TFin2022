package com.marlon.apolo.tfinal2022.communicationAgora.video.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.marlon.apolo.tfinal2022.R;

import java.util.Base64;

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

    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "7c0b693ccee54bcdb935f23c984dc2aa";
    // Fill the channel name.
    private String channelName = "yoru";
    // Fill the temp token generated on Agora Console.
    private String token = "007eJxTYJju31ux4Dnv8uLWK3sDEz3zT/jZTi5YcGryvQn+/NKeMroKDObJBklmlsbJyamppiZJySlJlsamaUbGyZYWJinJRomJt9cfSWkIZGQ4xxTCwAiFID4LQ2V+USkDAwBbDx/4";
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

    private int counter2 = 0;
    private int counter1 = 0;
    AnimationDrawable wifiAnimation;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            showMessage("Remote user joined " + uid);

            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            showMessage("Remote user offline " + uid + " " + reason);
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
            counter2 += 1;

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

    private void setupLocalVideo() {
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

    public void joinChannel(View view) {
        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            // Display LocalSurfaceView.
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            // Start local preview.
            agoraEngine.startPreview();
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine.joinChannel(token, channelName, uid, options);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
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
        }
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


//    private void setupVideoSDKEngine() {
//        try {
////            RtcEngineConfig config = new RtcEngineConfig();
////            config.mContext = getBaseContext();
////            config.mAppId = appId;
////            config.mEventHandler = mRtcEventHandler;
////            agoraEngine = RtcEngine.create(config);
////            // By default, the video module is disabled, call enableVideo to enable it.
////            agoraEngine.enableVideo();
//
//
//            RtcEngineConfig config = new RtcEngineConfig();
//            config.mContext = getBaseContext();
//            config.mAppId = appId;
//            config.mEventHandler = mRtcEventHandler;
//            agoraEngine = RtcEngine.create(config);
//
////            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                enableEncryption();
////            }
//
//            // By default, the video module is disabled, call enableVideo to enable it.
//            agoraEngine.enableVideo();
//
//            // Enable the dual stream mode
//            agoraEngine.enableDualStreamMode(true);
//
//
//// Set audio profile and audio scenario.
////            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_GAME_STREAMING);
//
//            // Specify the audio scenario and audio profile
//            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO);
//            agoraEngine.setAudioScenario(Constants.AUDIO_SCENARIO_GAME_STREAMING);
//
//
//            // Set up the audio effects manager
//            //audioEffectManager = agoraEngine.getAudioEffectManager();
//
//
//// Set the video profile
//            videoConfigLocal = new VideoEncoderConfiguration();
//// Set mirror mode
//            videoConfigLocal.mirrorMode = VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
//// Set framerate
//            videoConfigLocal.frameRate = VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15.getValue();
//// Set bitrate
//            videoConfigLocal.bitrate = 400;
//// Set dimensions
//            videoConfigLocal.dimensions = VideoEncoderConfiguration.VD_640x360;
//// Set orientation mode
////            videoConfigLocal.orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
//// Set degradation preference
////            videoConfigLocal.degradationPrefer = VideoEncoderConfiguration.DEGRADATION_PREFERENCE.MAINTAIN_BALANCED;
//// Apply the configuration
//            agoraEngine.setVideoEncoderConfiguration(videoConfigLocal);
//
////            startProbeTest();
//
//
//        } catch (Exception e) {
//            showMessage(e.toString());
//        }
//    }


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
            startProbeTest();


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_video_call_main);
        mLocalContainer = findViewById(R.id.local_video_view_container);
        networkStatus = findViewById(R.id.networkStatus);

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

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }


        setupVideoSDKEngine();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enableEncryption();
        } else {
            imageViewEncryption.setImageResource(R.drawable.icon_encryption);
            imageViewEncryption.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        wifiAnimation.stop();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }

}