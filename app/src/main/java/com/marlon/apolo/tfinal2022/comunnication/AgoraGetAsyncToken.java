package com.marlon.apolo.tfinal2022.comunnication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngine;

public class AgoraGetAsyncToken extends AsyncTask<String, Void, String> {

    private AgoraVoiceCallActivity contextInstance;
    private boolean isJoined;
    private RtcEngine agoraEngine;
    private String channelName;
    private int uid;
    String tokenLocal;
    private String TAG = AgoraGetAsyncToken.class.getSimpleName();

    public AgoraGetAsyncToken(AgoraVoiceCallActivity contextVar, boolean isJoinedVar, RtcEngine agoraEngineVar, String channelNameVar, int uidVar) {
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

        Log.d(TAG, "Conectado al servidor Java...");


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
        //closeProgressDialog();
        contextInstance.setToken(result);
    }

//    public void setAgoraToken(String newValue) {
//        String token = newValue;
//        if (!isJoined) { // Join a channel
//            ChannelMediaOptions options = new ChannelMediaOptions();
//
//            // For a Video call, set the channel profile as COMMUNICATION.
//            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
//            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
//            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
//            // Start local preview.
////            agoraEngine.startPreview();
////            agoraEngine.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_GAME_STREAMING);
//
//            // Join the channel with a token.
//            agoraEngine.joinChannel(token, channelName, uid, options);
//            contextInstance.
//            createVideoCallOnFirebase(channelName, uid);
//
//
//        } else { // Already joined, renew the token by calling renewToken
//            agoraEngine.renewToken(token);
////            showMessage("Token renewed");
//        }
//    }


}


