/*
 * @(#)VideoChatViewActivity.java        1.0 2022/04/24
 *
 * Este software es información confidencial y propiedad de Marlon Paúl Apolo Quishpe.
 * Usted no puede utilizar este software de manera maliciosa o con intención de causar
 * daños a usuarios que utilicen servicios de terceros.
 *
 *  Copyright (C) 2021 Marlon Paúl Apolo Quishpe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marlon.apolo.tfinal2022.videoLlamada;

import static io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15;
import static io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30;
import static io.agora.rtc.video.VideoEncoderConfiguration.VD_320x240;
import static io.agora.rtc.video.VideoEncoderConfiguration.VD_640x360;
import static io.agora.rtc.video.VideoEncoderConfiguration.VD_640x480;
import static io.agora.rtc.video.VideoEncoderConfiguration.VD_840x480;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.TokenAsyncTask;
import com.marlon.apolo.tfinal2022.model.VideoLlamada;

import java.util.Locale;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


/**
 * Esta clase muestra una interfaz gráfica para una videollamada 1 a 1
 * entre dos usuarios.
 *
 * @author Marlon Apolo
 * @version 1.0 24 Mar 2022
 */
public class VideoChatViewActivity extends AppCompatActivity {

    private static final String TAG = VideoChatViewActivity.class.getSimpleName();

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

    // Customized logger view
//    private LoggerRecyclerView mLogView;

    private String channelNameShare;
    private String localToken;
    private TextView textViewCallIn;
    private TextView textViewCallOut;

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
                }
            });
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLogView.logI("new user joined: " + String.valueOf(uid));
//                    mLogView.logI("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    try {

                        stopPlayingTone();

                    } catch (Exception e) {

//                        mLogView.logE(e.toString());
                    }
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
    private String contactTo;
    private int localUid;
    private boolean destiny;
    private VideoLlamada videoLlamadaLocal;
    private boolean llamadaInStatus;
    private String textoLlamada;
    private String resolution;
    private Spinner spnResolution;
    private Spinner spnFps;
    private boolean flagAcceptCall;
    private MediaPlayer mediaPlayerTonoLlamada;

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
        textViewCallOut.setText("Llamada finalizada!");
        textViewCallOut.setVisibility(View.VISIBLE);
        mCallBtn.setVisibility(View.GONE);
        mSwitchCameraBtn.setVisibility(View.GONE);
        mMuteBtn.setVisibility(View.GONE);

        videoLlamadaLocal.colgar(channelNameShare);

        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {

            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);

    }


    private ImageView imageViewRejectCall;


    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        imageViewRejectCall = findViewById(R.id.btn_reject_call);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

//        mLogView = findViewById(R.id.log_recycler_view);

        textViewCallOut = findViewById(R.id.textViewCallOut);
        textViewCallIn = findViewById(R.id.textViewCallIn);

//        spnResolution = (Spinner) findViewById(R.id.spnResolution);
//        spnResolution.setSelection(1);
//        spnResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//                resolution = item;
//                switch (resolution) {
//                    case "320x240":
//                        selectedResolution = VD_320x240;
//                        break;
//                    case "640x480":
//                        selectedResolution = VD_640x480;
//                        break;
//                    case "840x480":
//                        selectedResolution = VD_840x480;
//                        break;
//                }
//
//                //changeConfigVideoWithResolution(valueResolution);
//                try {
//                    changeVideoConfig(selectedResolution, selectedFPS);
//
//                } catch (Exception e) {
//
//                }
//
//
//                //tv.setText(item);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                //tv.setText("");
//
//            }
//        });
//        spnFps = (Spinner) findViewById(R.id.spnFPS);
//        spnFps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//                //Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
//
//                int selected = spnResolution.getSelectedItemPosition();
//                switch (selected) {
//                    case 0:
//                        selectedResolution = VD_320x240;
//                        break;
//                    case 1:
//                        selectedResolution = VD_640x480;
//                        break;
//                    case 2:
//                        selectedResolution = VD_840x480;
//                        break;
//
//                }
//                switch (position) {
//                    case 0:
//                        selectedFPS = FRAME_RATE_FPS_15;
//                        break;
//                    case 1:
//                        selectedFPS = FRAME_RATE_FPS_30;
//                        break;
//
//                }
//                try {
//                    changeVideoConfig(selectedResolution, selectedFPS);
//
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        if (destiny) {
//            textViewCallIn.setText(textoLlamada);
            mMuteBtn.setVisibility(View.GONE);
            mSwitchCameraBtn.setVisibility(View.GONE);
            mCallBtn.setImageDrawable(getDrawable(R.drawable.btn_startcall_normal));
            textViewCallIn.setVisibility(View.VISIBLE);
            textViewCallOut.setVisibility(View.GONE);
//            spnFps.setVisibility(View.GONE);
//            spnResolution.setVisibility(View.GONE);

            mCallEnd = true;
            imageViewRejectCall.setVisibility(View.VISIBLE);
            imageViewRejectCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "RECHAZANDO", Toast.LENGTH_SHORT).show();
                    try {
                        videoLlamadaLocal.setLlamadaRechazada(true);
                        videoLlamadaLocal.rechazar(channelNameShare, VideoChatViewActivity.this);
                    } catch (Exception e) {
//                        mLogView.logE(e.toString());
                    }
                }
            });
        } else {
//            textViewCallOut.setText("Llamando...");
            imageViewRejectCall.setVisibility(View.GONE);
            textViewCallIn.setVisibility(View.GONE);
            textViewCallOut.setVisibility(View.VISIBLE);
        }
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
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA);
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
//        TokenAsyncTask tokenAsyncTask = new TokenAsyncTask(channelNameShare, localToken);
//
//        tokenAsyncTask.execute();
//        tokenAsyncTask.setOnItemClickListener(new TokenAsyncTask.ClickListener() {
//            @Override
//            public void onTokenListener(String token, int uid) {
//                if (token.length() > 0) {
//                    localToken = token;
//                    localUid = uid;
//                    if (!destiny){
//                        joinChannel();
//                    }
//                }
//            }
//        });
        if (!flagAcceptCall) {
            if (!llamadaInStatus) {
                TokenAsyncTask tokenAsyncTask = new TokenAsyncTask(channelNameShare);
                tokenAsyncTask.execute();
                tokenAsyncTask.setOnItemClickListener(new TokenAsyncTask.ClickListener() {
                    @Override
                    public void onTokenListener(String token, int uid) {

                        if (token.length() > 0) {
                            localToken = token;
                            localUid = uid;
                            mCallEnd = false;
                            joinChannel();
//                    Toast.makeText(getApplicationContext(), localToken, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }


    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VD_640x480,
                FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
//        mLogView.logW("Configurando video...");
//        mLogView.logW(String.format(Locale.US, "Video height: %d pixels", VideoEncoderConfiguration.VD_1280x720.height));
//        mLogView.logW(String.format(Locale.US, "Video width: %d pixels", VideoEncoderConfiguration.VD_1280x720.width));
//        mLogView.logW(String.format(Locale.US, "Video FPS: %d Frames Per Second", VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30.getValue()));
//        mLogView.logW(String.format(Locale.US, "Video orientation: %d", VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT.getValue()));

    }


    private void setupLocalVideo() {
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
//        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_FIT, 0);
//        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_FILL, 0);
//        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_ADAPTIVE, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);

    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
//        String token = getString(R.string.agora_access_token);
        //Toast.makeText(getApplicationContext(), "JOIN CHANNEL", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), channelNameShare, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), String.valueOf(localUid), Toast.LENGTH_LONG).show();
//        mLogView.logI(String.format("Channel name: %s", channelNameShare));
//        mLogView.logI(String.format(Locale.US, "uid: %d", localUid));
//        mLogView.logI(String.format("Token: %s", localToken));


//        String token = "0064956a1efcd5c4b7b8ee12ffe31a950edIACaPQ4djPrdxUBMZ6n/VBX1SsiNhKKRwXz2ciuj6LcVOeIVUyEAAAAAEAAhqfJE3v49YgEAAQDe/j1i ";
//        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
//            token = null; // default, no token
//        }

        if (destiny) {
            VideoLlamada videoLlamada = new VideoLlamada();
            videoLlamada = videoLlamadaLocal;
            videoLlamada.setId(channelNameShare);
//            imageViewRejectCall.setVisibility(View.GONE);
            //llamada.setCaller(FirebaseAuth.getInstance().getCurrentUser().getUid());
            //llamada.setCallerToken(tokenLocal);
            //llamada.setCallerStatus(true);
            //llamada.setUidCaller(0);
            videoLlamada.setDestiny(FirebaseAuth.getInstance().getCurrentUser().getUid());
            videoLlamada.setDestinyToken(localToken);
            videoLlamada.setUidCaller(localUid);
            videoLlamada.setDestinyStatus(true);
            if (videoLlamada.isCallerStatus())
                videoLlamada.setChannelConnectedStatus(true);
            //textViewCall.setText(textoLlamada);
            if (!mCallEnd) {
//                Toast.makeText(getApplicationContext(), "CONTESTANDO", Toast.LENGTH_SHORT).show();
                videoLlamada.setNameDestiny(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                videoLlamada.contestar(mRtcEngine, localToken, channelNameShare, "Extra Optional Data", localUid);
                spnFps.setVisibility(View.VISIBLE);
                spnResolution.setVisibility(View.VISIBLE);
                flagAcceptCall = false;
            }


        } else {
            VideoLlamada videoLlamada = new VideoLlamada();
            videoLlamadaLocal = videoLlamada;
            videoLlamada.setId(channelNameShare);
            videoLlamada.setCaller(FirebaseAuth.getInstance().getCurrentUser().getUid());
            videoLlamada.setCallerToken(localToken);
            videoLlamada.setCallerStatus(true);
            videoLlamada.setUidCaller(localUid);

            videoLlamada.setDestiny(contactTo);
            videoLlamada.setDestinyToken(null);
            videoLlamada.setUidCaller(0);
            videoLlamada.setDestinyStatus(false);
            videoLlamada.setChannelConnectedStatus(false);
            videoLlamada.setNameCaller(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

            mediaPlayerTonoLlamada = new MediaPlayer();
//            mediaPlayerTonoLlamada = MediaPlayer.create(this, R.raw.tono_llamada);
            mediaPlayerTonoLlamada.setLooping(true);
            mediaPlayerTonoLlamada.start();

            videoLlamada.llamar(mRtcEngine, localToken, channelNameShare, "Extra Optional Data", localUid, this);

        }

//        llamada.llamar(mRtcEngine, token, channelNameShare, "Extra Optional Data", localUid);

        //mRtcEngine.joinChannel(token, "demoChannel1", "Extra Optional Data", 0);
    }

    public void startPlayingTone(MediaPlayer mPlayer2) {
//        mPlayer2 = MediaPlayer.create(this, R.raw.tono_llamada);
        mPlayer2.setLooping(true);
        try {
            //    mPlayer2.prepare();
            mPlayer2.start();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void stopPlayingTone() {
        mediaPlayerTonoLlamada.stop();
        mediaPlayerTonoLlamada.release();
        mediaPlayerTonoLlamada = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
        unregisterReceiver(mMessageReceiver);

        try {
            if (mediaPlayerTonoLlamada != null) {
                mediaPlayerTonoLlamada.stop();
                mediaPlayerTonoLlamada.release();
                mediaPlayerTonoLlamada = null;
            }

        } catch (Exception e) {
//            mLogView.logE(e.toString());
        }


    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
        try {
            videoLlamadaLocal.colgar(channelNameShare);
        } catch (Exception e) {
//            mLogView.logE(e.toString());
        }
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute_normal : R.drawable.btn_unmute_normal;
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
        imageViewRejectCall.setVisibility(View.GONE);

        mCallEnd = false;
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
        if (destiny) {
            spnFps.setVisibility(View.GONE);
            spnResolution.setVisibility(View.GONE);
        } else {
            spnFps.setVisibility(View.GONE);
            spnResolution.setVisibility(View.GONE);
            textViewCallOut.setText("Llamada finalizada!");
            try {
                stopPlayingTone();
            } catch (Exception e) {
//                mLogView.logE(e.toString());
            }
            try {
                videoLlamadaLocal.colgar(channelNameShare);
                finish();
            } catch (Exception e) {

            }

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (destiny) {
                try {
                    boolean callRemove = intent.getBooleanExtra("callRemove", false);
                    VideoLlamada videoLlamadaLocal = (VideoLlamada) intent.getSerializableExtra("localCall");

                } catch (Exception e) {

                }

                textViewCallIn.setText("Llamada finalizada!");
                textViewCallIn.setVisibility(View.VISIBLE);
                mCallBtn.setVisibility(View.GONE);
                mSwitchCameraBtn.setVisibility(View.GONE);
                mMuteBtn.setVisibility(View.GONE);
                imageViewRejectCall.setVisibility(View.GONE);
                leaveChannel();

                destiny = false;
                videoLlamadaLocal = null;
                textoLlamada = "";
                llamadaInStatus = false;
                spnFps.setVisibility(View.GONE);
                spnResolution.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //startActivity(new Intent(getApplicationContext(), MainNavigationActivity.class));

                        finish();

                    }
                }, 1000);

            } else {
                textViewCallOut.setText("LLAMADA RECHAZADA!");
                textViewCallOut.setVisibility(View.VISIBLE);
                mCallBtn.setVisibility(View.GONE);
                mSwitchCameraBtn.setVisibility(View.GONE);
                mMuteBtn.setVisibility(View.GONE);
                imageViewRejectCall.setVisibility(View.GONE);
                leaveChannel();

                spnFps.setVisibility(View.GONE);
                spnResolution.setVisibility(View.GONE);
                destiny = false;
                videoLlamadaLocal = null;
                textoLlamada = "";
                llamadaInStatus = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        //startActivity(new Intent(getApplicationContext(), MainNavigationActivity.class));
                    }
                }, 1000);
            }

            //new SendNotificationTask().execute("");
//            finish();
        }
    };


    /**
     * Este método no retorna ningún valor, pero permite iniciar los componentes de la interfaz gráfica.
     *
     * @param savedInstanceState objeto Bundle que almacena información en el caso de que se prodruzca un cambio
     *                           de configuración en el dispositivo como: cambiar de orientación la pantalla.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);
        mCallEnd = true;
        localToken = "";
        destiny = false;


        try {
            destiny = getIntent().getBooleanExtra("destiny", false);
            channelNameShare = getIntent().getStringExtra("chatID");
            contactTo = getIntent().getStringExtra("contactTo");
            flagAcceptCall = getIntent().getBooleanExtra("flagAcceptCall", false);

            if (flagAcceptCall) {
//                Toast.makeText(getApplicationContext(), String.valueOf(flagAcceptCall), Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), String.valueOf(channelNameShare), Toast.LENGTH_LONG).show();
                mCallEnd = false;

                localToken = "";
                TokenAsyncTask tokenAsyncTask = new TokenAsyncTask(channelNameShare);

                tokenAsyncTask.execute();
                tokenAsyncTask.setOnItemClickListener(new TokenAsyncTask.ClickListener() {
                    @Override
                    public void onTokenListener(String token, int uid) {
                        if (token.length() > 0) {
                            //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
                            localToken = token;
                            localUid = uid;
                            Intent intent = new Intent("DESTINY_ACCEPT_VIDEO_CALL");
                            sendBroadcast(intent);
                        }
                    }
                });


                Intent intent = new Intent("ACTION_ANSWER_CALL");
                intent.putExtra("idRandomNotification", getIntent().getIntExtra("idRandomNotification", -1));
                sendBroadcast(intent);
            } else {
                if (destiny) {
//                Toast.makeText(getApplicationContext(), String.valueOf(flagAcceptCall), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), String.valueOf(channelNameShare), Toast.LENGTH_LONG).show();
                    mCallEnd = false;

                    localToken = "";
                    TokenAsyncTask tokenAsyncTask = new TokenAsyncTask(channelNameShare);

                    tokenAsyncTask.execute();
                    tokenAsyncTask.setOnItemClickListener(new TokenAsyncTask.ClickListener() {
                        @Override
                        public void onTokenListener(String token, int uid) {
                            if (token.length() > 0) {
                                //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
                                localToken = token;
                                localUid = uid;
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {

        }

        try {
            destiny = getIntent().getBooleanExtra("destiny", false);
            channelNameShare = getIntent().getStringExtra("chatID");
            videoLlamadaLocal = (VideoLlamada) getIntent().getSerializableExtra("llamada");
            textoLlamada = getIntent().getStringExtra("mensaje");
            llamadaInStatus = getIntent().getBooleanExtra("llamadaEntrante", false);
            flagAcceptCall = getIntent().getBooleanExtra("flagAcceptCall", false);


        } catch (Exception e) {

        }

        //Recibidor para llamada
        registerReceiver(mMessageReceiver, new IntentFilter("callFinish"));
        registerReceiver(mAcceptCall, new IntentFilter("DESTINY_ACCEPT_VIDEO_CALL"));

        initUI();


        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }


    }


    private VideoEncoderConfiguration.FRAME_RATE selectedFPS = FRAME_RATE_FPS_15;
    private VideoEncoderConfiguration.VideoDimensions selectedResolution = VD_640x360;

    private BroadcastReceiver mAcceptCall = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (destiny) {

                textViewCallIn.setVisibility(View.GONE);
                mMuteBtn.setVisibility(View.VISIBLE);
                mSwitchCameraBtn.setVisibility(View.VISIBLE);
                mCallBtn.setImageDrawable(getDrawable(R.drawable.btn_endcall_normal));
                llamadaInStatus = false;
                mCallEnd = false;
//                setupLocalVideo();
//                joinChannel();
                startCall();
            }

            //new SendNotificationTask().execute("");
//            finish();
        }
    };

    private void changeVideoConfig(VideoEncoderConfiguration.VideoDimensions
                                           dimensions, VideoEncoderConfiguration.FRAME_RATE fps) {
        // Set the video encoding resolution, frame rate, bitrate and orientation mode according to the settings of the user
        VideoEncoderConfiguration.VideoDimensions valueResolution = dimensions;
        VideoEncoderConfiguration.FRAME_RATE valueFps = fps;
        selectedFPS = valueFps;
        selectedResolution = dimensions;

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                valueResolution,
                valueFps,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
//        mLogView.logW("Configurando video...");
//        mLogView.logW(String.format(Locale.US, "Video width: %d pixels", valueResolution.width));
//        mLogView.logW(String.format(Locale.US, "Video height: %d pixels", valueResolution.height));
//        mLogView.logW(String.format(Locale.US, "Video FPS: %d Frames Per Second", valueFps.getValue()));
//        mLogView.logW(String.format(Locale.US, "Video orientation: %d", VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT.getValue()));

    }


}