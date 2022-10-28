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

public class DeleteAsyncTask extends AsyncTask<String, Integer, String> {

    private static String TAG = DeleteAsyncTask.class.getSimpleName();
    private String uid;
    private Context mContext;
    private String response;
    private static DeleteAsyncTask.ClickListener clickListener;


    // Constructor that provides a reference to the TextView from the MainActivity
    public DeleteAsyncTask(String var, Context context) {
        uid = var;
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
        Log.d(TAG, "DELETE REQUEST");

        response = "";
        try {
//            String urlBase = "https://authwitouthauth.herokuapp.com/usuarios";
//            String urlBase = "https://authwitouthauth.herokuapp.com/usuarios/damin/delete/usuarios/{uid}";
            String urlBase = "https://authwitouthauth.herokuapp.com/damin/delete/usuarios/" + uid;

            URL url = new URL(urlBase);
//        HttpURLConnection httpURLConnection = new url.openConnection();
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

//            httpURLConnection.setRequestMethod("DELETE");
//            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // The format of the content we're sending to the server
//            httpURLConnection.setRequestProperty("Accept", "application/json"); // The format of response we want to get from the server
//            httpURLConnection.setDoInput(true);
//            httpURLConnection.setDoOutput(true);

            // Send the JSON we created
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
//            outputStreamWriter.write(jsonObjectString);
//            outputStreamWriter.flush();

//            URL url = new URL("http://www.example.com/resource");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();


            // Check if the connection is successful
            int responseCode = httpCon.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = httpCon.getInputStream();
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
//            Toast.makeText(mContext, "Eliminando registros...", Toast.LENGTH_LONG).show();
        }
        if (result.equals("0")) {
//            Toast.makeText(mContext, mContext.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
        }
        clickListener.onTokenListener(result);
    }


    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }

    private void setProgressPercent(Integer progress) {
//        mTextView.get().setText(String.valueOf(progress));
    }

    public void setOnListenerAsyncTask(DeleteAsyncTask.ClickListener varClickListener) {
        clickListener = varClickListener;
    }

    public interface ClickListener {
        void onTokenListener(String publicKey);
    }
}
