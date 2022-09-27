package com.marlon.apolo.tfinal2022.individualChat.view.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;


import com.marlon.apolo.tfinal2022.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String[]> {

    private Context mContext;
    private OnTaskCompleted mListener;


    public FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    private final String TAG = FetchAddressTask.class.getSimpleName();

    @Override
    protected String[] doInBackground(Location... params) {
        // Set up the geocoder
        String[] results = new String[3];
        Geocoder geocoder = new Geocoder(mContext,
                Locale.getDefault());

        // Get the passed in location
        Location location = params[0];
        List<Address> addresses = null;
        String resultMessage = "";


        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);
//            Log.d(TAG, "Latitude: " + String.valueOf(location.getLatitude()));
//            Log.d(TAG, "Longitude: " + String.valueOf(location.getLongitude()));
        } catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage = mContext.getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage = mContext.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // If no addresses found, print an error message.
        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        } else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join(
                    "\n",
                    addressParts);

        }
        results[0] = resultMessage;
        results[1] = String.valueOf(location.getLongitude());
        results[2] = String.valueOf(location.getLatitude());

//        return resultMessage;
        return results;
    }

    /**
     * Called once the background thread is finished and updates the
     * UI with the result.
     *
     * @param address The resulting reverse geocoded address, or error
     *                message if the task failed.
     */
    @Override
    protected void onPostExecute(String[] address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String[] result);
    }
}