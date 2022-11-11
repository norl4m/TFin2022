package com.marlon.apolo.tfinal2022.receivers;

import static android.content.Context.MODE_PRIVATE;
import static com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity.sPref;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;

public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkReceiver.class.getSimpleName();
    private NetworkTool networkTool;

//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
//
//
//        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
////        boolean networkFlag = mPreferences.getBoolean("networkFlag", false);
//        SharedPreferences.Editor editorPref = myPreferences.edit();
//        myPreferences.getBoolean("networkFlag", false);
//
//        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        sPref = defaultSharedPreferences.getBoolean("sync_network", true);
//
//
//        // Checks the user prefs and the network connection. Based on the result, decides whether
//        // to refresh the display or keep the current display.
//        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
//        if (sPref && networkInfo != null
//                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            // If device has its Wi-Fi connection, sets refreshDisplay
//            // to true. This causes the display to be refreshed when the user
//            // returns to the app.
//            editorPref.putBoolean("networkFlag", true);
//            editorPref.apply();
//            Toast.makeText(context, "wifi_connected", Toast.LENGTH_SHORT).show();
//
//            // If the setting is ANY network and there is a network connection
//            // (which by process of elimination would be mobile), sets refreshDisplay to true.
//        } else if (!sPref && networkInfo != null) {
//            editorPref.putBoolean("networkFlag", true);
//            editorPref.apply();
//
//            // Otherwise, the app can't download content--either because there is no network
//            // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
//            // is no Wi-Fi connection.
//            // Sets refreshDisplay to false.
//        } else {
//            editorPref.putBoolean("networkFlag", false);
//            editorPref.apply();
//            Toast.makeText(context, "lost_connection", Toast.LENGTH_SHORT).show();
//        }
//
//
////        // Checks the user prefs and the network connection. Based on the result, decides whether
////        // to refresh the display or keep the current display.
////        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
////        if (sPref && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
////            // If device has its Wi-Fi connection, sets refreshDisplay
////            // to true. This causes the display to be refreshed when the user
////            // returns to the app.
//////            RegWithEmailPasswordActivity.refreshDisplay = true;
////            editorPref.putBoolean("networkFlag", true);
////            editorPref.apply();
////
//////            Toast.makeText(context, "wifi_connected", Toast.LENGTH_SHORT).show();
//////            Toast.makeText(context, "Utilizando red Wifi", Toast.LENGTH_LONG).show();
////            // If the setting is ANY network and there is a network connection
////            // (which by process of elimination would be mobile), sets refreshDisplay to true.
////        } else if (!sPref && networkInfo != null &&(networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
////            // Otherwise, the app can't download content--either because there is no network
////            // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
////            // is no Wi-Fi connection.
////            // Sets refreshDisplay to false.
//////            RegWithEmailPasswordActivity.refreshDisplay = true;
////
////            editorPref.putBoolean("networkFlag", true);
////            editorPref.apply();
////
//////            Toast.makeText(context, "Se ha restablecido la conexión a Internet", Toast.LENGTH_LONG).show();
////            //            Toast.makeText(context, "Utilizando red Wifi", Toast.LENGTH_LONG).show();
////
////        } else {
//////            RegWithEmailPasswordActivity.refreshDisplay = false;
////
////            editorPref.putBoolean("networkFlag", false);
////            editorPref.apply();
////
//////            Toast.makeText(context, "lost_connection", Toast.LENGTH_SHORT).show();
////            Toast.makeText(context, "Se ha perdido la conexión, por favor revise que tenga una conexión a Internet", Toast.LENGTH_LONG).show();
////        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        networkTool = new NetworkTool(context);

        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sPref = defaultSharedPreferences.getBoolean("sync_network", true);


        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
//        boolean networkFlag = mPreferences.getBoolean("networkFlag", false);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        myPreferences.getBoolean("networkFlag", false);


        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        if (sPref && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            editorPref.putBoolean("networkFlag", true);
            editorPref.apply();

            //Toast.makeText(context, "wifi_connected", Toast.LENGTH_SHORT).show();

            // If the setting is ANY network and there is a network connection
            // (which by process of elimination would be mobile), sets refreshDisplay to true.
        } else {
            if (sPref && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//                editorPref.putBoolean("networkFlag", true);
//                editorPref.apply();
                editorPref.putBoolean("networkFlag", true);
                editorPref.apply();

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.
            } else if (!sPref && networkInfo != null) {
                editorPref.putBoolean("networkFlag", true);
                editorPref.apply();

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.


            } else {
                editorPref.putBoolean("networkFlag", false);
                editorPref.apply();
//                Toast.makeText(context, "lost_connection", Toast.LENGTH_SHORT).show();
                networkTool.alertDialogNoConectadoReceiver();
            }
        }

        boolean networkFlag = myPreferences.getBoolean("networkFlag", false);
        sPref = defaultSharedPreferences.getBoolean("sync_network", true);

//        Toast.makeText(context, String.format("Preferencia: %s", String.valueOf(sPref)), Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, String.format("Red: %s", String.valueOf(networkFlag)), Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, String.format("Online: %s", String.valueOf(networkInfo)), Toast.LENGTH_SHORT).show();
        Log.d(TAG, String.format("Prefencia: %s", String.valueOf(sPref)));
        Log.d(TAG, String.format("Red: %s", String.valueOf(networkFlag)));
        Log.d(TAG, String.format("Online: %s", String.valueOf(networkInfo)));
//        if (networkFlag) {
//            Toast.makeText(context, "lost_connection", Toast.LENGTH_SHORT).show();
//        }


    }


}