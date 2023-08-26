package com.marlon.apolo.tfinal2022.herramientasAsíncronas;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.marlon.apolo.tfinal2022.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostAsyncTask extends AsyncTask<String, Integer, String> {

    private static String TAG = PostAsyncTask.class.getSimpleName();
    private String jsonObjectString;
    private Context mContext;
    private String response;
    private static PostAsyncTask.ClickListener clickListener;


    // Constructor that provides a reference to the TextView from the MainActivity
    public PostAsyncTask(String var, Context context) {
        jsonObjectString = var;
        mContext = context;
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
        Log.d(TAG, "POST REQUEST");
        Log.d(TAG, jsonObjectString);
        response = "";
        try {
            String javaUrl = "https://authwitouthauth-a975f368522e.herokuapp.com";

//            String urlBase = "https://authwitouthauth.herokuapp.com/usuarios";
            String urlBase = javaUrl + "/usuarios";

            URL url = new URL(urlBase);
//        HttpURLConnection httpURLConnection = new url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // The format of the content we're sending to the server
            httpURLConnection.setRequestProperty("Accept", "application/json"); // The format of response we want to get from the server
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            // Send the JSON we created
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(jsonObjectString);
            outputStreamWriter.flush();

            // Check if the connection is successful
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = httpURLConnection.getInputStream();
                // 3. Download and decode the string response using builder
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    Log.d(TAG, line);
                }
                response = stringBuilder.toString();


//                val response = httpURLConnection.inputStream.bufferedReader()
//                        .use {
//                    it.readText()
//                }  // defaults to UTF-8
//                withContext(Dispatchers.Main) {
//
//                    // Convert raw JSON to pretty JSON using GSON library
//                    val gson = GsonBuilder().setPrettyPrinting().create()
//                    val prettyJson = gson.toJson(JsonParser.parseString(response))
                Log.d("Pretty Printed JSON :", "prettyJson");
                Log.d(TAG, response);

//                }
            } else {
                Log.e(TAG, String.valueOf(responseCode));
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        return response;
    }


    /**
     * Does something with the result on the UI thread; in this case
     * updates the TextView.
     */
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: " + result);
        if (result.equals("1")) {
            Toast.makeText(mContext, "Usuario registrado existosamente!", Toast.LENGTH_LONG).show();
        }
        if (result.equals("0")) {
            Toast.makeText(mContext, mContext.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
        }
        clickListener.onTokenListener(result);

//        if (result != null) {
//            if (result.length() > 10) {
//                Toast.makeText(mContext, "Usuario registrado existosamente!", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(mContext, mContext.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(mContext, mContext.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
//        }
    }


    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }

    private void setProgressPercent(Integer progress) {
//        mTextView.get().setText(String.valueOf(progress));
    }

    public void setOnListenerAsyncTask(PostAsyncTask.ClickListener varClickListener) {
        clickListener = varClickListener;
    }

    public interface ClickListener {
        void onTokenListener(String publicKey);
    }
}
