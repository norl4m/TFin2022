package com.marlon.apolo.tfinal2022.herramientasAsíncronas;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class TokenAsyncTask extends AsyncTask<String, Integer, String> {

    private String TAG = TokenAsyncTask.class.getSimpleName();
    // The TextView where we will show results
    private WeakReference<TextView> mTextView;
    private String channelNameShare;
    private String localToken;
    private static ClickListener clickListener;
    private int uidLocal;

    // Constructor that provides a reference to the TextView from the MainActivity
    public TokenAsyncTask(String channelName) {
        channelNameShare = channelName;
    }

    /**
     * Runs on the background thread.
     *
     * @param strings parameters in this use case.
     * @return Returns the string including the amount of time that
     * the background thread slept.
     */

    @Override
    protected String doInBackground(String... strings) {

        String urlBase = "https://s3rv3rsid3.herokuapp.com";
        final int min = 3000;
        final int max = 3999;
        int random = new Random().nextInt((max - min) + 1) + min;
        uidLocal = random;


        String uid = String.valueOf(random);
//        String channelName = channelNameShare;
        String getToken = urlBase + "/rtc/" + channelNameShare + "/publisher/uid/" + uid + "/";
//        String getToken = urlBase + "/rtc/" + "demonChannel" + "/publisher/uid/" + uid + "/";
        String tokenLocal = "";

        try {
            // 1. Declare a URL Connection
            URL url = new URL(getToken);
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
            Log.d(TAG, uid);
//            this.channelNameShare =tokenLocal;
            //joinChannel();

        } catch (Exception e) {

        }


        // Return a String result.
//        return "Tu token es: " + tokenLocal;
        return tokenLocal;
    }

    /**
     * Does something with the result on the UI thread; in this case
     * updates the TextView.
     */
    protected void onPostExecute(String result) {
        //mTextView.get().setText(result);
        clickListener.onTokenListener(result, uidLocal);
        localToken = result;
    }


    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }

    private void setProgressPercent(Integer progress) {
        mTextView.get().setText(String.valueOf(progress));
    }

    public void setOnItemClickListener(ClickListener varClickListener) {
        clickListener = varClickListener;
    }

    public interface ClickListener {
        void onTokenListener(String token, int uid);
    }
}