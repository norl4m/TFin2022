package com.marlon.apolo.tfinal2022.herramientasAsíncronas;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PublicKeyAsyncTask extends AsyncTask<String, Integer, String> {

    private static String TAG = PublicKeyAsyncTask.class.getSimpleName();
    private static PublicKeyAsyncTask.ClickListener clickListener;
    private String publicKeyLocal;

    // Constructor that provides a reference to the TextView from the MainActivity
    public PublicKeyAsyncTask() {

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

        String urlBase = "https://authwitouthauth.herokuapp.com";

        String getPublicKey = urlBase + "/damin/" + FirebaseAuth.getInstance().getCurrentUser();
//        String getToken = urlBase + "/rtc/" + "demonChannel" + "/publisher/uid/" + uid + "/";
        String tokenLocal = "";

        try {
            // 1. Declare a URL Connection
            URL url = new URL(getPublicKey);
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
            //tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2, stringBuilder.length() - 2);
//            tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2);
            tokenLocal = stringBuilder.toString();
            //Log.d(TAG, tokenLocal);

        } catch (Exception e) {
            Log.d(TAG, e.toString());

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
        clickListener.onTokenListener(result);
        publicKeyLocal = result;
//        Log.d(TAG, "onPostExecute: " + publicKeyLocal);
    }


    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }

    private void setProgressPercent(Integer progress) {
//        mTextView.get().setText(String.valueOf(progress));
    }

    public void setOnListenerAsyncTask(PublicKeyAsyncTask.ClickListener varClickListener) {
        clickListener = varClickListener;
    }

    public interface ClickListener {
        void onTokenListener(String publicKey);
    }
}