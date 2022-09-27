package com.marlon.apolo.tfinal2022.individualChat.adaptador;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SpecialMensajeNubeListAdapter extends RecyclerView.Adapter<SpecialMensajeNubeListAdapter.MyadapterViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_AUDIO_LEFT = 10;
    private static final int MSG_TYPE_AUDIO_RIGHT = 11;
    private static final int MSG_TYPE_IMAGE_LEFT = 20;
    private static final int MSG_TYPE_IMAGE_RIGHT = 21;
    private static final int MSG_TYPE_VIDEO_LEFT = 30;
    private static final int MSG_TYPE_VIDEO_RIGHT = 31;
    private static final int MSG_TYPE_MAP_LEFT = 50;
    private static final int MSG_TYPE_MAP_RIGHT = 51;
    private static final String TAG = SpecialMensajeNubeListAdapter.class.getSimpleName();


    private static MediaPlayer mediaPlayer;
    private Activity activity;


    private ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();//change it() to your items
    private int currentPlayingPosition;
    private final SeekBarUpdater seekBarUpdater;
    private MyadapterViewHolder playingHolder;

    public SpecialMensajeNubeListAdapter(Activity activity) {
        seekBarUpdater = new SeekBarUpdater();
        this.activity = activity;
        currentPlayingPosition = -1;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public MyadapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //put YourItemsLayout;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;


        if (viewType == MSG_TYPE_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_rigth, parent, false);
        }
        if (viewType == MSG_TYPE_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_text_left, parent, false);
        }

        if (viewType == MSG_TYPE_IMAGE_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_image_right, parent, false);

        }
        if (viewType == MSG_TYPE_IMAGE_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_image_left, parent, false);
        }


        if (viewType == MSG_TYPE_AUDIO_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_audio_right, parent, false);

        }
        if (viewType == MSG_TYPE_AUDIO_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_audio_left, parent, false);
        }


        if (viewType == MSG_TYPE_MAP_RIGHT) {
            itemView = inflater.inflate(R.layout.mensajito_item_location_rigth, parent, false);

        }
        if (viewType == MSG_TYPE_MAP_LEFT) {
            itemView = inflater.inflate(R.layout.mensajito_item_location_left, parent, false);
        }

        return new MyadapterViewHolder(itemView);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w(TAG, "My Current loction address" + strReturnedAddress.toString());
            } else {
                Log.w(TAG, "My Current loction address" + "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "My Current loction address" + "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onBindViewHolder(MyadapterViewHolder holder, int position) {
        MensajeNube current = mensajeNubeArrayList.get(position);

        try {
//            holder.textViewDB.setText(String.valueOf(current.getIdMessage()));
//            holder.imageViewContent.(String.valueOf(current.getIdMessageFirebase()));
            // CONTENIDO = IMAGEN
//            if (current.getType() == 4) {
//                //String path = "content://com.miui.gallery.open/raw/%2Fstorage%2Femulated%2F0%2FDCIM%2FCamera%2FIMG_20220130_213803.jpg";
//
//                String[] parts = current.getContenido().split(",");
//                String part1 = parts[0]; // 123
//                String part2 = parts[1]; // 654321
////
//                double latitude = Double.parseDouble(part1.substring(part1.indexOf(":") + 1));
//                double longitude = Double.parseDouble(part2.substring(part2.indexOf(":") + 1));
//
//                Log.d(TAG, String.valueOf(latitude));
//                Log.d(TAG, String.valueOf(longitude));
//
//                List<Address> addresses = null;
//                String resultMessage = "";
//                Geocoder geocoder = new Geocoder(activity,
//                        Locale.getDefault());
////
//
//                try {
//                    addresses = geocoder.getFromLocation(
//                            latitude,
//                            longitude,
//                            // In this sample, get just a single address
//                            1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Address address = addresses.get(0);
//                ArrayList<String> addressParts = new ArrayList<>();
//
//                // Fetch the address lines using getAddressLine,
//                // join them, and send them to the thread
//                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
//                    addressParts.add(address.getAddressLine(i));
//                }
//
//                resultMessage = TextUtils.join(
//                        "\n",
//                        addressParts);
////
//                Log.d(TAG, String.valueOf(resultMessage));
//
////                holder.wordItemView.setText(resultMessage);
//                holder.textViewContenido.setText(activity.getString(R.string.address_text, resultMessage));
//                holder.textViewContenido.setText("Ubicación: " + resultMessage);
//
////                holder.wordItemView.setText(String.format("%s", context.getString(R.string.address_text), resultMessage));
//
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        String longitude = "37.7749";
////                        String latitude = "-122.4194";
////                        String geo = "geo:" + String.valueOf(longitude) + "," + String.valueOf(latitude);
////                        String geo = "geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude);
//                        DataValidation dataValidation = new DataValidation();
//
//
////                        String longitude = dataValidation.splitterData(current.getContent(),":",",");
////                        String latitude = dataValidation.splitterData(current.getContent(),",","");
//                        //Toast.makeText(context, part1.substring(part1.indexOf(":") + 1), Toast.LENGTH_SHORT).show();
//                        //Toast.makeText(context, part2.substring(part2.indexOf(":") + 1), Toast.LENGTH_SHORT).show();
//
//                        //                        String geo = current.getContent();
////                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
//                        String uri = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", latitude, longitude, latitude, longitude);
//
////                        Uri gmmIntentUri = Uri.parse(current.getContent());
//                        Uri gmmIntentUri = Uri.parse(uri);
//                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                        mapIntent.setPackage("com.google.android.apps.maps");
//                        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
//                            activity.startActivity(mapIntent);
//                        }
//
////                        String uri = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", latitude,longitude,latitude,longitude);
//////                        String uri = String.format(Locale.US, "geo:%f,%f?q=%f,%f", 0.0,0.0,latitude,longitude);
//////                        String uri = "http://maps.google.com/maps?q=" +latitude +","+longitude;
////                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.getContent()));
////
////                        context.startActivity(intent);
//                    }
//                });
//            }


            if (current.getType() == 4) {
                //String path = "content://com.miui.gallery.open/raw/%2Fstorage%2Femulated%2F0%2FDCIM%2FCamera%2FIMG_20220130_213803.jpg";
//                holder.textViewContenido.setText(current.getContenido());

                holder.textViewContenido.setText(activity.getString(R.string.address_text,
                        current.getContenido()));


////                String[] parts = current.getContenido().split(",");
////                String part1 = parts[0]; // 123
////                String part2 = parts[1]; // 654321
//////
//////                double latitude = Double.parseDouble(part1.substring(part1.indexOf(":") + 1));
//                double longitude = 37.4219983;
//////                double longitude = Double.parseDouble(part2.substring(part2.indexOf(":") + 1));
//                double latitude = -122.084;
////
//                Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                Log.d(TAG, getCompleteAddressString(latitude, longitude));
//
////                List<Address> addresses = null;
////                String resultMessage = "";
////                Geocoder geocoder = new Geocoder(activity.getApplicationContext(),
////                        Locale.getDefault());
//////
////
////                try {
////                    addresses = geocoder.getFromLocation(
////                            latitude,
////                            longitude,
////                            // In this sample, get just a single address
////                            1);
////                } catch (IOException e) {
////                    Log.d(TAG, e.toString());
////                    e.printStackTrace();
////                }
////
////                try {
////
////                    Address address = addresses.get(0);
////                    ArrayList<String> addressParts = new ArrayList<>();
////
////                    // Fetch the address lines using getAddressLine,
////                    // join them, and send them to the thread
////                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
////                        addressParts.add(address.getAddressLine(i));
////                    }
////
////                    resultMessage = TextUtils.join(
////                            "\n",
////                            addressParts);
//////
//////                holder.wordItemView.setText(resultMessage);
////                    holder.textViewContenido.setText(activity.getString(R.string.address_text, resultMessage));
////                    Log.d(TAG, resultMessage);
////
//////                holder.wordItemView.setText(String.format("%s", context.getString(R.string.address_text), resultMessage));
////                } catch (Exception e) {
////                    Log.d(TAG, e.toString());
////
////                }
//
////                Geocoder geocoder = new Geocoder(activity,
////                        Locale.getDefault());
//
////                List<Address> addresses = null;
//                String resultMessage = "";
//                List<Address> addresses;
//
//
//                try {
//
//                    Geocoder geocoder;
////                    List<Address> addresses;
//                    geocoder = new Geocoder(activity, Locale.getDefault());
//
//                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName(); //
//                    Log.d(TAG, address);
//                    Log.d(TAG, city);
//                    Log.d(TAG, state);
//                    Log.d(TAG, country);
//                    Log.d(TAG, postalCode);
//
//
//                    addresses = geocoder.getFromLocation(
//                            latitude,
//                            longitude,
//                            // In this sample, get just a single address
//                            1);
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                    // Catch network or other I/O problems
//                    resultMessage = activity
//                            .getString(R.string.service_not_available);
//                    Log.e(TAG, resultMessage, e);
//                }
////
////                if (addresses == null || addresses.size() == 0) {
////                    if (resultMessage.isEmpty()) {
////                        resultMessage = activity
////                                .getString(R.string.no_address_found);
////                        Log.e(TAG, resultMessage);
////                    }
////                } else {
////                    // If an address is found, read it into resultMessage
////                    Address address = addresses.get(0);
////                    ArrayList<String> addressParts = new ArrayList<>();
////
////                    // Fetch the address lines using getAddressLine,
////                    // join them, and send them to the thread
////                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
////                        addressParts.add(address.getAddressLine(i));
////                    }
////
////                    resultMessage = TextUtils.join("\n", addressParts);
////                    holder.textViewContenido.setText(activity.getString(R.string.address_text,
////                            resultMessage));
//            }
//
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String longitude = "37.7749";
//                        String latitude = "-122.4194";
//                        String geo = "geo:" + String.valueOf(longitude) + "," + String.valueOf(latitude);
//                        String geo = "geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude);
                        DataValidation dataValidation = new DataValidation();


//                        String longitude = dataValidation.splitterData(current.getContent(),":",",");
//                        String latitude = dataValidation.splitterData(current.getContent(),",","");
                        //Toast.makeText(context, part1.substring(part1.indexOf(":") + 1), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, part2.substring(part2.indexOf(":") + 1), Toast.LENGTH_SHORT).show();

                        //                        String geo = current.getContent();
//                        String locationLoca = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", current.getLatitude(), current.getLongitude(), current.getLatitude(), current.getLongitude());
                        String locationLoca = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", current.getLongitude(), current.getLatitude(), current.getLongitude(), current.getLatitude());
                        Log.d(TAG, locationLoca);
//                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                        Uri gmmIntentUri = Uri.parse(locationLoca);
//                        String uri = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", current.getLatitude(), current.getLongitude(), current.getLatitude(), current.getLongitude());

//                        Uri gmmIntentUri = Uri.parse(current.getContent());
//                        Uri gmmIntentUri = Uri.parse(uri);
                        //Toast.makeText(activity, "Algo va mal", Toast.LENGTH_LONG).show();
                        /*Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(mapIntent);
                        }*/ // Error luego de actualizar app

//                        String uri = String.format(Locale.US, "geo:%f,%f?z=17&q=%f,%f", latitude,longitude,latitude,longitude);
////                        String uri = String.format(Locale.US, "geo:%f,%f?q=%f,%f", 0.0,0.0,latitude,longitude);
////                        String uri = "http://maps.google.com/maps?q=" +latitude +","+longitude;
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.getContent()));
//
//                        context.startActivity(intent);

                        // Create a Uri from an intent string. Use the result to create an Intent.
//                        Uri gmmIntentUri1 = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//                        Uri gmmIntentUri1 = Uri.parse("geo:37.7749,-122.4194");
                        Uri gmmIntentUri1 = Uri.parse(locationLoca);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent1 = new Intent(Intent.ACTION_VIEW, gmmIntentUri1);
// Make the Intent explicit by setting the Google Maps package
                        mapIntent1.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                        activity.startActivity(mapIntent1);
                        if (mapIntent1.resolveActivity(activity.getPackageManager()) != null) {
//                            startActivity(mapIntent);
                            activity.startActivity(mapIntent1);

                        }
                    }
                });


            }
//            holder.textViewTime.setText(String.valueOf(current.getCreateDate()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            if (current.getType() == 0) {
                holder.textViewContenido.setText(String.format("%s", current.getContenido()));

            }
        } catch (Exception e) {

        }
        DataValidation dataValidation = new DataValidation();
        String sec = dataValidation.splitterData(current.getTimeStamp(), "(seconds=", ",");
        String nansec = dataValidation.splitterData(current.getTimeStamp(), ", nanoseconds=", ")");
        long seconds = Long.parseLong(sec);
        long nanoseconds = Integer.parseInt(nansec);
        Timestamp timestamp = new Timestamp(seconds, (int) nanoseconds);
        Date date = timestamp.toDate();
        holder.textViewFecha.setText(String.format("%s", date.toLocaleString()));
        if (current.isEstadoLectura()) {
            holder.imageViewEstadoLectura.setColorFilter(activity.getResources().getColor(R.color.teal_700));
        } else {
            holder.imageViewEstadoLectura.setColorFilter(activity.getResources().getColor(R.color.purple_700));

        }

        try {
//            Glide.with(activity).load(current.getContenido()).into(holder.imageViewContent);
//
//            Glide.with(activity)
//                    .load(current.getContenido())
//                    .placeholder(R.drawable.placeholder)
//                    .fitCenter()
//                    .into(imageView);

//            RequestOptions cropOptions = new RequestOptions().centerCrop();
//            cropOptions.placeholder(R.drawable.placeholder);
//
//            DrawableCrossFadeFactory factory =
//                    new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
//
//            Glide.with(activity)
//                    .load(current.getContenido())
//                    .apply(cropOptions)
//                    .transition(withCrossFade(factory))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//
//                    .into(holder.imageViewContent)
//            ;


            Glide.with(activity).load(current.getContenido()).apply(new
                            RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error((R.drawable.error))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH))
                    .into(holder.imageViewContent);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        try {
            int duration = Integer.parseInt(current.getAudioDuration());
            holder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) duration),
                    TimeUnit.MILLISECONDS.toSeconds((long) duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) duration))));

            if (position == currentPlayingPosition) {
                playingHolder = holder;
                updatePlayingView();
            } else {
                updateNonPlayingView(holder);
            }
        } catch (Exception e) {

        }


    }

    private void updateNonPlayingView(MyadapterViewHolder holder) {
        holder.sbProgress.removeCallbacks(seekBarUpdater);
        holder.sbProgress.setEnabled(false);
        holder.sbProgress.setProgress(0);
        holder.ivPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        //playingHolder.textViewCurrentTime.setText("0:00");
    }

    private void updatePlayingView() {
        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
        playingHolder.sbProgress.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            playingHolder.sbProgress.postDelayed(seekBarUpdater, 100);
            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void setMensajeNubeArrayList(ArrayList<MensajeNube> mensajeNubeArrayList) {
        this.mensajeNubeArrayList = mensajeNubeArrayList;
        notifyDataSetChanged();
    }

    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            if (null != playingHolder && null != mediaPlayer) {

                playingHolder.textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) mediaPlayer.getCurrentPosition()))));

                playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                playingHolder.sbProgress.postDelayed(this, 100);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mensajeNubeArrayList.size();
    }

    public class MyadapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        SeekBar sbProgress;
        ImageView ivPlayPause;

        private TextView textViewContenido;
        private TextView textViewFecha;
        private ImageView imageViewEstadoLectura;
        private ImageView imageViewContent;


        private TextView textViewCurrentTime;

        MyadapterViewHolder(View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            textViewFecha = itemView.findViewById(R.id.textViewDate);
            imageViewEstadoLectura = itemView.findViewById(R.id.imageViewEstadoLectura);
            try {
                imageViewContent = itemView.findViewById(R.id.imageViewContent);
                imageViewContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(mensajeNubeArrayList.get(getAbsoluteAdapterPosition()).getContenido()), "image/*");
                        activity.startActivity(intent);
                    }
                });
            } catch (Exception e) {

            }

            try {
                textViewCurrentTime = itemView.findViewById(R.id.textViewInitTime);
                ivPlayPause = itemView.findViewById(R.id.buttoPlay);
                ivPlayPause.setOnClickListener(this);
                sbProgress = itemView.findViewById(R.id.seekBar);
                sbProgress.setOnSeekBarChangeListener(this);
            } catch (Exception e) {

            }

        }

        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.seekBar:
                    break;

                case R.id.buttoPlay: {
                    if (getAdapterPosition() == currentPlayingPosition) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            if (mediaPlayer != null)
                                mediaPlayer.start();
                        }
                    } else {
                        currentPlayingPosition = getAdapterPosition();
                        if (mediaPlayer != null) {
                            if (null != playingHolder) {
                                updateNonPlayingView(playingHolder);
                            }
                            mediaPlayer.release();
                        }
                        playingHolder = this;


                        PlaySound(mensajeNubeArrayList.get(getAdapterPosition()).getContenido());//put your audio file


                    }
                    if (mediaPlayer != null)
                        updatePlayingView();
                }
                break;
            }


        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private void PlaySound(String filesound) {

        mediaPlayer = MediaPlayer.create(activity, Uri.parse(filesound));

        try {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                }
            });

            mediaPlayer.start();


        } catch (Exception e) {

        }

    }

    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }

//        mediaPlayer.release();
//        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();/*Ups*/
        currentPlayingPosition = -1;
    }


    public void liberarRecursos() {
        try {
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void addMensajeNubeToList(MensajeNube mensajeNube) {
//        if (mensajeNubeList != null) {
//            mensajeNubeList = new ArrayList<>();
//        }
        mensajeNubeArrayList.add(mensajeNube);
        notifyItemInserted(mensajeNubeArrayList.size() - 1);
    }

    public void updateMensaje(int index, MensajeNube mensajeNube) {
//        if (mensajeNubeList != null) {
//            mensajeNubeList = new ArrayList<>();
//        }
        mensajeNubeArrayList.set(index, mensajeNube);
        notifyItemChanged(index);
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        int select = -1;
        switch (mensajeNubeArrayList.get(position).getType()) {
            case 0:/*texto*/
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_RIGHT;
                } else {
                    select = MSG_TYPE_LEFT;
                }
                break;
            case 2:/*audio*/
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_AUDIO_RIGHT;
                } else {
                    select = MSG_TYPE_AUDIO_LEFT;
                }
                break;
            case 1:/*imagen*/
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_IMAGE_RIGHT;
                } else {
                    select = MSG_TYPE_IMAGE_LEFT;
                }
                break;
            case 3:
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_VIDEO_RIGHT;
                } else {
                    select = MSG_TYPE_VIDEO_LEFT;
                }
                break;
            case 4:
                if (mensajeNubeArrayList
                        .get(position).getFrom()
                        .equals(firebaseUser.getUid())) {
                    select = MSG_TYPE_MAP_RIGHT;
                } else {
                    select = MSG_TYPE_MAP_LEFT;
                }
                break;

        }


        return select;

    }
}
