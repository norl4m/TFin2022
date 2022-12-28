package com.marlon.apolo.tfinal2022.ui.trabajadores.eliminar;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Constants for the various components of the Books API request.
    //
    // Base endpoint URL for the Books API.
//    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    // Parameter for the search string.
//    private static final String QUERY_PARAM = "q";
    // Parameter that limits search results.
//    private static final String MAX_RESULTS = "maxResults";
    // Parameter to filter by print type.
//    private static final String PRINT_TYPE = "printType";
    private static final String API_KEY = "AIzaSyAo3o1D0sEvi8M9bVJs_avW_82WRaa33qI";

    private static final String FIREBASE_REST_API_URL = "https://identitytoolkit.googleapis.com/v1/accounts:delete?key=" + API_KEY;
    //    private static final String FIREBASE_REST_API_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=" + API_KEY;
    private static final String FIREBASE_ID_TOKEN = "idToken";


    /**
     * Static method to make the actual query to the Books API.
     *
     * @param queryString the query string.
     * @return the JSON response string from the query.
     */
    static String getBookInfo(String queryString) {

        // Set up variables for the try block that need to be closed in the
        // finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            // Build the full query URI, limiting results to 10 items and
            // printed books.
//            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
//                    .appendQueryParameter(QUERY_PARAM, queryString)
//                    .appendQueryParameter(MAX_RESULTS, "10")
//                    .appendQueryParameter(PRINT_TYPE, "books")
//                    .build();

            Uri builtURI = Uri.parse(FIREBASE_REST_API_URL).buildUpon()
//                    .appendQueryParameter(FIREBASE_ID_TOKEN, queryString)
                    .build();

            // Convert the URI to a URL,
            URL requestURL = new URL(builtURI.toString());

            // Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestProperty(
                    "Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


//            Uri.Builder builder = new Uri.Builder()
//                    .appendQueryParameter("idToken", queryString);
////                    .appendQueryParameter("secondParam", paramValue2)
////                    .appendQueryParameter("thirdParam", paramValue3);
//            String query = builder.build().getEncodedQuery();
//
//            OutputStream os = urlConnection.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(query);
//            writer.flush();
//            writer.close();
//            os.close();


            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response.
            StringBuilder builderResponse = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                // Add the current line to the string.
                builderResponse.append(line);

                // Since this is JSON, adding a newline isn't necessary (it won't
                // affect parsing) but it does make debugging a *lot* easier
                // if you print out the completed buffer for debugging.
                builderResponse.append("\n");
            }

            if (builderResponse.length() == 0) {
                // Stream was empty.  Exit without parsing.
                return null;
            }

            bookJSONString = builderResponse.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the connection and the buffered reader.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Write the final JSON response to the log
        Log.d(LOG_TAG, bookJSONString);

        return bookJSONString;
    }
}