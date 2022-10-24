package com.marlon.apolo.tfinal2022.herramientasAsíncronas;

import static com.google.common.net.HttpHeaders.USER_AGENT;

import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.load.model.GlideUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetAsyncTask extends AsyncTask<String, Integer, String> {

    private static String TAG = GetAsyncTask.class.getSimpleName();
    private String cipherText = "";

    // Constructor that provides a reference to the TextView from the MainActivity
    public GetAsyncTask(String message) {
        cipherText = message;
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

//        String urlBase = "https://authwitouthauth.herokuapp.com/damin/decrypt";
//        String params = "?id=aaaaaaaaaaaaaaaaaaaa";
        String params = "?id=" + cipherText;
        Log.d(TAG, "Encode message: " + cipherText);
//        String urlBase = "https://authwitouthauth.herokuapp.com/damin/aux" + params;
        String urlBase = "https://authwitouthauth.herokuapp.com/damin/decrypt" + params;

//        String getPublicKey = urlBase + "/damin/" + FirebaseAuth.getInstance().getCurrentUser();
//        String getToken = urlBase + "/rtc/" + "demonChannel" + "/publisher/uid/" + uid + "/";
        String response = "";
        Log.d(TAG, "GET REQUEST");

        try {
            // 1. Declare a URL Connection
            URL url = new URL(urlBase);
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
                Log.d(TAG, line);
            }
            //tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2, stringBuilder.length() - 2);
//            tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2);
            response = stringBuilder.toString();
            Log.d(TAG, response);



            /*conn.connect();

            InputStream in = conn.getInputStream();
            // 3. Download and decode the string response using builder
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                Log.d("TAG", line);
            }*/


        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        try {
            sendGET(cipherText);

        } catch (Exception e) {
            Log.d(TAG, e.toString());

        }


        // Return a String result.
//        return "Tu token es: " + tokenLocal;
        return response;
    }

    private static void sendGET(String cipherText) throws IOException {
        Log.d(TAG, "Send GET");
//        String urlBase = "https://authwitouthauth.herokuapp.com/damin/decrypt" + params;
//        String urlBase = "https://authwitouthauth.herokuapp.com/damin/decrypt";
        String urlBase = "https://authwitouthauth.herokuapp.com/damin/dpc";
        URL obj = new URL(urlBase);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);


        JSONObject cred = new JSONObject();
//        JSONObject auth=new JSONObject();
//        JSONObject parent=new JSONObject();
        try {
            cred.put("id", "asdasdasdad");
            cred.put("data", cipherText);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        auth.put("tenantName", "adm");
//        auth.put("passwordCredentials", cred);
//        parent.put("auth", auth);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(cred.toString());

        OutputStream os = con.getOutputStream();
        os.write(cred.toString().getBytes("UTF-8"));
        os.close();


        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            Log.d(TAG, response.toString());
        } else {
            Log.d(TAG, "GET request not worked");
        }

    }

    /**
     * Does something with the result on the UI thread; in this case
     * updates the TextView.
     */
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: " + result);
    }


    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }

    private void setProgressPercent(Integer progress) {
//        mTextView.get().setText(String.valueOf(progress));
    }

    /*public void setOnListenerAsyncTask(PublicKeyAsyncTask.ClickListener varClickListener) {
        clickListener = varClickListener;
    }*/

    public interface ClickListener {
        void onTokenListener(String publicKey);
    }
}
