package com.marlon.apolo.tfinal2022.herramientas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.config.ConfiguracionActivity;

public class NetworkTool {
    private boolean syncNetwork;
    private Context context;
    private boolean isWifiConn;
    private boolean isMobileConn;
    private AlertDialog dialogInfo;

    public NetworkTool(Context contextVar) {
        context = contextVar;

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        syncNetwork = sharedPrefs.getBoolean("sync_network", true);


    }

    public boolean isOnlineWithWifi() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean online = false;


//        if (sPref && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            online = true;
        }


//        return (networkInfo != null && networkInfo.isConnected());
        return online;
    }

    public boolean isOnlineWithData() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean online = false;


        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            online = true;
        }


//        return (networkInfo != null && networkInfo.isConnected());
        return online;
    }

    private void checkInternetConnection() {
        String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        isWifiConn = networkInfo.isConnected();
        networkInfo =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
        //Toast.makeText(getApplicationContext(), String.format("Wifi connected %b", isWifiConn), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), String.format("Mobile connected %s", String.valueOf(isMobileConn)), Toast.LENGTH_SHORT).show();
    }


    public void alertDialogNoConectadoInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(context.getResources().getString(R.string.text_error_no_connectado));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

            }
        });


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

        dialogInfo = builder.create();
        dialogInfo.show();

    }

    public void alertDialogSoloConectadoConWifi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(context.getResources().getString(R.string.text_error_no_connectado));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

            }
        });


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

        dialogInfo = builder.create();
        dialogInfo.show();

    }


    public void alertDialogNoConectadoWifiInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(context.getResources().getString(R.string.text_error_no_connectado_con_wifi));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

            }
        }).setNegativeButton("Cambiar configuración", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(context, ConfiguracionActivity.class));
            }
        });


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

        dialogInfo = builder.create();
        dialogInfo.show();
    }

    public boolean isSyncNetwork() {
        return syncNetwork;
    }

    public void setSyncNetwork(boolean syncNetwork) {
        this.syncNetwork = syncNetwork;
    }

    public void cambiadaSoloWifi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(context.getResources().getString(R.string.text_config_cambiada_solo_wifi));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

            }
        });
//
        dialogInfo = builder.create();
        dialogInfo.show();
    }

    public void cambiadaDatosMovyWifi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(context.getResources().getString(R.string.text_config_cambiada_datos_moviles_y_wifi));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

            }
        });

        dialogInfo = builder.create();
        dialogInfo.show();
    }

    public void alertDialogNoConectadoReceiver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
        builder.setCancelable(false);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);

        textViewInfo.setText(context.getResources().getString(R.string.text_error_no_connectado_receiver));
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }

            }
        });


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons

        dialogInfo = builder.create();
        dialogInfo.show();
    }
}
