package com.marlon.apolo.tfinal2022.individualChat.view.location;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Usuario;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LocationActivity extends AppCompatActivity implements
        FetchAddressTask.OnTaskCompleted {

    // Constants
    private String TAG = LocationActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    // Views
    private Button mLocationButton;
    private TextView mLocationTextView;
    private ImageView mAndroidImageView;

    // Location classes
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    // Animation
    private AnimatorSet mRotateAnim;
    private String latitude;
    private String longitude;
    private Button mshareLocationButton;
    private Usuario usuarioTo;

    //    String contactTo;
//    String chatID;
   // private String location;


    // [START write_fan_out]
    private void sendMessage(MessageCloudPoc messageCloudPoc) {
        Log.d(TAG, "###########################");
        Log.d(TAG, "sendMessage");
        Log.d(TAG, messageCloudPoc.toString());
        Log.d(TAG, "###########################");
        Timestamp timestamp = new Timestamp(new Date());
        messageCloudPoc.setTimeStamp(timestamp.toString());

        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = FirebaseDatabase.getInstance().getReference().child("crazyMessages").push().getKey();
        messageCloudPoc.setIdMensaje(key);
        //MessageCloudPoc post = new MessageCloudPoc();
        Map<String, Object> postValues = messageCloudPoc.toMap();

//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
//        childUpdates.put("/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
//        FirebaseDatabase.getInstance().getReference().child("crazyMessages").updateChildren(childUpdates);
//


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/crazyMessages/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
        childUpdates.put("/crazyMessages/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
        childUpdates.put("/notificaciones/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Mensaje enviado");
                } else {
                    Log.d(TAG, "Error al enviar mensaje");
                }
            }
        });


        ChatPoc chatPoc = new ChatPoc();
        chatPoc.setIdRemoteUser(messageCloudPoc.getTo());
        chatPoc.setLastMessageCloudPoc(messageCloudPoc);
        FirebaseDatabase.getInstance().getReference().child("crazyChats")
                .child(messageCloudPoc.getFrom())
                .child(chatPoc.getIdRemoteUser())
                .setValue(chatPoc);

        ChatPoc chatPocRemoto = new ChatPoc();
        chatPocRemoto.setIdRemoteUser(messageCloudPoc.getFrom());
        chatPocRemoto.setLastMessageCloudPoc(messageCloudPoc);
        FirebaseDatabase.getInstance().getReference().child("crazyChats")
                .child(messageCloudPoc.getTo())
                .child(chatPocRemoto.getIdRemoteUser())
                .setValue(chatPocRemoto);

        finish();
        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getFrom()).updateChildren(childUpdates);
        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getTo()).updateChildren(childUpdates);

    }
    // [END write_fan_out]

    /**
     * Stops tracking the device. Removes the location
     * updates, stops the animation, and resets the UI.
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mLocationButton.setText(R.string.start_tracking_location);
            mLocationTextView.setText(R.string.textview_hint);
            mRotateAnim.end();
        }
    }


    /**
     * Sets up the location request.
     *
     * @return The LocationRequest object containing the desired parameters.
     */
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        mLocationButton = (Button) findViewById(R.id.button_location);
        mshareLocationButton = (Button) findViewById(R.id.button_share_location);
        mLocationTextView = (TextView) findViewById(R.id.textview_location);
        mAndroidImageView = (ImageView) findViewById(R.id.imageview_android);

        mshareLocationButton.setVisibility(View.GONE);

//        contactTo = getIntent().getStringExtra("contactTo");
         // usuarioLocal = (Usuario) getIntent().getSerializableExtra("usuarioLocal");
        usuarioTo = (Usuario) getIntent().getSerializableExtra("usuarioTo");
         //chat = (Chat) getIntent().getSerializableExtra("chat");
//        chatID = getIntent().getStringExtra("idChat");

        // Initialize the FusedLocationClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                this);

        // Set up the animation.
        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator
                (this, R.animator.rotate);
        mRotateAnim.setTarget(mAndroidImageView);

        // Restore the state if the activity is recreated.
        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY);
        }

        // Set the listener for the location button.
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Toggle the tracking state.
             * @param v The track location button.
             */
            @Override
            public void onClick(View v) {
                if (!mTrackingLocation) {
                    startTrackingLocation();
                    mshareLocationButton.setVisibility(View.VISIBLE);
                } else {
                    stopTrackingLocation();
                    mshareLocationButton.setVisibility(View.GONE);
                }
            }
        });

        mshareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), latitude + "\n" + longitude, Toast.LENGTH_SHORT).show();

//                // Create a Uri from an intent string. Use the result to create an Intent.
//                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
//                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                // Make the Intent explicit by setting the Google Maps package
//                mapIntent.setPackage("com.google.android.apps.maps");
//
//                // Attempt to start an activity that can handle the Intent
//                startActivity(mapIntent);
//

//                String geo = "geo:" + longitude + "," + latitude + "?" + "z=17&q=" + longitude + "," + latitude;
                String geo = "longitude:" + longitude + ",latitude:" + latitude;
////                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
//                Uri gmmIntentUri = Uri.parse(geo);
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                if (mapIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(mapIntent);
//                }
                if (longitude != null && latitude != null) {
//                    MensajeNube mensajeTexto = new MensajeNube();
//                    mensajeTexto.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    mensajeTexto.setTo(contactTo);
//                    Timestamp timestamp = new Timestamp(new Date());
//                    mensajeTexto.setTimeStamp(timestamp.toString());
                    List<Address> addresses = null;
                    String resultMessage = "";
                    Geocoder geocoder = new Geocoder(getApplicationContext(),
                            Locale.getDefault());
//
                    try {
                        addresses = geocoder.getFromLocation(
                                Double.parseDouble(longitude),
                                Double.parseDouble(latitude),
                                // In this sample, get just a single address
                                1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//


                    /*ERROR*/
                    /*ERROR*/
                    /*ERROR*/
                    /*ERROR*/
                    Address address = addresses.get(0);
                    ArrayList<String> addressParts = new ArrayList<>();
//
//                // Fetch the address lines using getAddressLine,
//                // join them, and send them to the thread
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressParts.add(address.getAddressLine(i));
                    }

                    resultMessage = TextUtils.join(
                            "\n",
                            addressParts);

                    MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
                    //mensajeNube.setIdMensaje();
                    //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                    messageCloudPoc.setContenido(getString(R.string.address_text, resultMessage));
                    messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    messageCloudPoc.setTo(usuarioTo.getIdUsuario());
                    messageCloudPoc.setEstadoLectura(false);
                    messageCloudPoc.setType(4);/*0 texto */
                    Log.d(TAG, messageCloudPoc.toString());

//
////                mensajeTexto.setContent(getString(R.string.address_text, resultMessage, System.currentTimeMillis()));
////                mensajeTexto.setContent(getString(R.string.address_text, resultMessage));
//                    mensajeTexto.setContenido(geo);
//                mensajeTexto.setContent(geo);
//                    mensajeTexto.setType(4);

//                    Trabajador trabajadorFrom = new Trabajador();
//                    trabajadorFrom.sendLocationMessage(chatID, mensajeTexto, LocationActivity.this, mWordViewModel);


//                    MensajeNube mensajeNube = new MensajeNube();
//                    //mensajeNube.setIdMensaje();
//                    //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
////                    mensajeNube.setContenido(geo);
//                    mensajeNube.setContenido(resultMessage);
//                    mensajeNube.setLatitude(Double.parseDouble(latitude));
//                    mensajeNube.setLongitude(Double.parseDouble(longitude));
//                    mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    mensajeNube.setTo(usuarioTo.getIdUsuario());
//                    mensajeNube.setEstadoLectura(false);
//
//                    mensajeNube.setType(4);/*4 location */
//
//
//                    if (chat == null) {
//                        String idChat = FirebaseDatabase.getInstance().getReference()
//                                .child("chats")
//                                .push().getKey();
//                        chat = new Chat();
//                        chat.setIdChat(idChat);
//                        chat.setMensajeNube(mensajeNube);
//
//                        Participante participante1 = new Participante();
//                        participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//                        participante1.setNombreParticipante(usuarioLocal.getFullName());
//                        participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                        Participante participante2 = new Participante();
//                        participante2.setIdParticipante(usuarioTo.getIdUsuario());
//                        participante2.setNombreParticipante(usuarioTo.getFullName());
//                        participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                        ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                        participanteArrayList.add(participante1);
//                        participanteArrayList.add(participante2);
//
//                        chat.setParticipantes(participanteArrayList);
//                        mensajeNube.setIdChat(chat.getIdChat());
//                        usuarioLocal.crearChat(chat, mensajeNube, LocationActivity.this);
//
//                    } else {
//                        usuarioLocal.enviarMensaje(chat, mensajeNube, LocationActivity.this);
//                    }

                    Intent replyIntent = new Intent();
                    String word = resultMessage;
                    // Put the new word in the extras for the reply Intent.
                    replyIntent.putExtra("EXTRA_REPLY", word);
                    replyIntent.putExtra("EXTRA_REPLY_LATITUDE", latitude);
                    replyIntent.putExtra("EXTRA_REPLY_LONGITUDE", longitude);
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                    messageCloudPoc.setLatitude(Double.parseDouble(latitude));
                    messageCloudPoc.setLongitude(Double.parseDouble(longitude));
                    sendMessage(messageCloudPoc);


                } else {
                    Toast.makeText(getApplicationContext(), "Su ubicación no ha podido ser compartida!"
                            , Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Por favor revise que la opción de ubicación se encuentre activada en su dispositivo móvil!"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        // Initialize the location callbacks.
        mLocationCallback = new LocationCallback() {
            /**
             * This is the callback that is triggered when the
             * FusedLocationClient updates your location.
             * @param locationResult The result containing the device location.
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // If tracking is turned on, reverse geocode into an address
                if (mTrackingLocation) {
                    new FetchAddressTask(LocationActivity.this, LocationActivity.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };
    }

    /**
     * Starts tracking the device. Checks for
     * permissions, and requests them if they aren't present. If they are,
     * requests periodic location updates, sets a loading text and starts the
     * animation.
     */
    @SuppressLint("StringFormatMatches")
    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);

            // Set a loading text while you wait for the address to be
            // returned
            mLocationTextView.setText(String.format("%s", getString(R.string.address_text,
                    getString(R.string.loading),
                    System.currentTimeMillis())));
            mLocationButton.setText(R.string.stop_tracking_location);
            mRotateAnim.start();
        }
    }

    /**
     * Saves the last location on configuration change
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }

    /**
     * Callback that is invoked when the user responds to the permissions
     * dialog.
     *
     * @param requestCode  Request code representing the permission request
     *                     issued by the app.
     * @param permissions  An array that contains the permissions that were
     *                     requested.
     * @param grantResults An array with the results of the request for each
     *                     permission requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:

                // If the permission is granted, get the location, otherwise,
                // show a Toast
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onTaskCompleted(String[] result) {
        if (mTrackingLocation) {
            // Update the UI
//            Log.d(TAG,result[1]);
//            Log.d(TAG,result[2]);
            latitude = result[1];
            longitude = result[2];
           // location = result[0];
            Log.d(TAG, "Latitude: " + result[1]);
            Log.d(TAG, "Longitude: " + result[2]);
//            Log.d(TAG,result[0]);
            mLocationTextView.setText(getString(R.string.address_text,
                    result[0], System.currentTimeMillis()));
        }
    }

    @Override
    protected void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mTrackingLocation) {
            startTrackingLocation();
        }
        super.onResume();
    }


}
