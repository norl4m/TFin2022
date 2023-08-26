package com.marlon.apolo.tfinal2022.individualChat.view;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.NuevaCitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.AgoraVoiceCallActivityPoc;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.VoiceCallMainActivity;
import com.marlon.apolo.tfinal2022.individualChat.adapters.SpecialMessageListAdapterPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.MensajeNube;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.individualChat.view.location.LocationActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CrazyIndividualChatActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = CrazyIndividualChatActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1520;
    private static final int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1503;
    private static final int PERMISSION_REQUEST_CAMERA = 1504;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 1505;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 1506;

    private static final int SELECT_AUDIO = 1500;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1502;
    private static final int LOCATION_CODE = 1700;


    private SharedPreferences myPreferences;
    private int usuario;
    private TextView textViewNameContactTo;
    private ImageView imageViewNameContactTo;
    private Usuario usuarioRemoto;
    private Usuario usuarioLocal;
    private RecyclerView recyclerViewMensajes;
    private TextInputEditText textInputEditTextMessage;
    private FloatingActionButton fabMessageMic;
    private boolean micMode;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1510;
    private MediaRecorder recorder;
    private String fileName;
    private MediaPlayer mediaPlayer;
    private boolean playing;
    private FloatingActionButton fabPlay;
    private boolean stopAudio;
    private SeekBar seekBarProgress;
    private TextView textViewCurrentTime;
    private Handler handler;
    private Runnable runnable;
    private TextView textViewDuration;
    private SpecialMessageListAdapterPoc specialMessageListAdapterPoc;
    private ValueEventListener valueEventListenerMessagesPoc;
    private FloatingActionButton fabChooseImageProfile;


    private Uri uriPhoto;
    private AlertDialog alertDialogVar;
    private TextView textViewMessage;
    private String stateRemoteUser;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (playing) {
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_pause_24));
                playSong();
            } else {
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));
                pauseSong();
                handler.removeCallbacks(runnable);
            }
        }
    };


    public void pauseSong() {
        playing = true;
        mediaPlayer.pause();
    }

    public void playSong() {

        playing = false;


        if (stopAudio) {
//            String fileName = getExternalCacheDir().getAbsolutePath();
//            fileName += "/audiorecordtest.3gp";

//            Uri uri = Uri.parse("https://www.bensound.com/bensound-music/bensound-summer.mp3");
            Uri uri = Uri.parse(fileName);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(CrazyIndividualChatActivity.this, uri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        seekBarProgress.setMax(mediaPlayer.getDuration());
                        textViewDuration.setText(String.format(Locale.US, "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) mediaPlayer.getDuration()))));
                        mediaPlayer.start();
                        stopAudio = false;
                        updateSeekbar();
                    }
                });

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        double ratio = i / 100.1;
                        int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
                        seekBarProgress.setSecondaryProgress(bufferingLevel);
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        seekBarProgress.setProgress(0);
                        textViewCurrentTime.setText("0:00");
                        handler.removeCallbacks(runnable);
                        playing = true;
                        //mediaPlayer.release();
                        //mediaPlayer = new MediaPlayer();
                        stopAudio = true;
                        fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            mediaPlayer.start();
            updateSeekbar();
            if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()) {
//                mediaPlayer.start();
//                updateSeekbar();
            } else {

            }

        }


    }

    private void updateSeekbar() {
        int currentPos = mediaPlayer.getCurrentPosition();
        seekBarProgress.setProgress(currentPos);
        Log.d(TAG, "UPDATING SEEKBAR");
        textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) currentPos),
                TimeUnit.MILLISECONDS.toSeconds((long) currentPos) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) currentPos))));

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable, 1000);


    }

    public void showAlertDialogEliminarMensajes() {
        AlertDialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseDatabase.getInstance().getReference().child("crazyMessages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(usuarioRemoto.getIdUsuario())
                        .setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Mensajes eliminados...", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Mensajes eliminados");
                                } else {

                                }
                            }
                        });

                // User clicked OK button
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();

            }
        });
// Set other dialog properties
        builder.setTitle("Confirmación:");
        builder.setMessage("¿Está seguro que desea eliminar todos los mensajes de este chat?");
        dialog = builder.create();


// Create the AlertDialog
        dialog.show();
    }

    public void startRecording() {
        findViewById(R.id.playConstrols).setVisibility(View.GONE);
        Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        findViewById(R.id.textViewBlink).startAnimation(animBlink);
        findViewById(R.id.textViewBlink).setVisibility(View.VISIBLE);


//        Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_LONG).show();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }

    public void stopRecording() {
        // Toast.makeText(getApplicationContext(), "Finalizando grabación...", Toast.LENGTH_LONG).show();

        recorder.stop();
        recorder.release();
        recorder = null;

        findViewById(R.id.playConstrols).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewBlink).setVisibility(View.GONE);


        try {
            findViewById(R.id.textViewBlink).clearAnimation();
        } catch (Exception e) {

        }

    }

    public void blockingUI(String idRemoto) {

        listenerMensajesLocales(idRemoto);
        findViewById(R.id.toolbar).setEnabled(false);
        findViewById(R.id.content_main).setEnabled(false);
        textViewNameContactTo.setText(String.format("%s %s", "Usuario", "no disponible"));

        findViewById(R.id.textInputEditTextMessage).setEnabled(false);
        findViewById(R.id.buttonAttachFile).setEnabled(false);
        findViewById(R.id.buttonCamera).setEnabled(false);
        findViewById(R.id.buttonMessageAndMic).setEnabled(false);
        findViewById(R.id.imageButtonVideoCall).setEnabled(false);
        findViewById(R.id.imageButtonVideoCall).setBackgroundResource(R.drawable.ic_baseline_videocam_24_focused);
        findViewById(R.id.imageButtonCall).setEnabled(false);
        findViewById(R.id.imageButtonCall).setBackgroundResource(R.drawable.ic_baseline_call_24_focused);
//        ((ImageButton) findViewById(R.id.imageButtonCall)).setColorFilter(Color.parseColor("#fc0101"));

        findViewById(R.id.messageContainer).setEnabled(false);
        //findViewById(R.id.messageContainer).setBackgroundColor(getResources().getColor(R.color.green_light));
        usuarioRemoto = new Trabajador();
        usuarioRemoto.setIdUsuario(idRemoto);
    }

    public void sendAudioMessageWithFilename(MessageCloudPoc messageCloudPoc) {
        String title = "Por favor espere";
        String message = "Cargando audio...";
        showCustomProgressDialog(title, message);
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


        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        Uri audioUri = Uri.fromFile(new File(fileName));

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(), audioUri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        messageCloudPoc.setAudioDuration(durationStr);

        String fileExtensionImage = MimeTypeMap.getFileExtensionFromUrl(audioUri.toString());
        String mimeTypeImage = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionImage);
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType(mimeTypeImage)
                .build();
//
//                Toast.makeText(activity, mimeType, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, fileExtension, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, mensajeNube.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, chat.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, selectedUri.toString(), Toast.LENGTH_LONG).show();


        String baseReference = "gs://tfinal2022-afc91.appspot.com";
//        String imagePath = baseReference + "/" + "mensajes" + "/" + chat.getIdChat() + "/" + mensajeNube.getIdMensaje() + "." + fileExtensionImage;
        String imagePath = baseReference + "/" + "mensajes" + "/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key + "." + fileExtensionImage;
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(imagePath);


//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()), storageMetadata);
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));

        Uri imageUriSend = Uri.fromFile(new File(fileName));
        UploadTask uploadTask = storageRef.putFile(imageUriSend, storageMetadata);

        //uploadTask = storageRef.putFile(imageUriSend, storageMetadata);


//        if (mensajeNube.getContenido().contains("content:")) {
//            uploadTask = storageRef.putFile(uriImage, storageMetadata);
//
//        } else {
//            Uri imageUriSend = Uri.fromFile(new File(mensajeNube.getContenido()));
//            uploadTask = storageRef.putFile(imageUriSend, storageMetadata);
//
//        }


//                uploadTask = storageRef.putFile(imageUri, storageMetadata);
        // Listen for state changes, errors, and completion of the upload.
        StorageReference finalStorageRef = storageRef;
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            updateAudioProgress(progress);
            Log.d(TAG, "Upload is " + progress + "% done");

        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "on failure Foto complete...");
                closeAlertDialogLoad();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
                Log.d(TAG, "Upload is complete...");
                //  registroActivity.limpiarUI();
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return finalStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
//                            Toast.makeText(activity, downloadUri.toString(), Toast.LENGTH_LONG).show();


                    //MessageCloudPoc post = new MessageCloudPoc();
                    messageCloudPoc.setContenido(downloadUri.toString());
                    messageCloudPoc.setType(2);
                    Map<String, Object> postValues = messageCloudPoc.toMap();
//
//                    Map<String, Object> childUpdates = new HashMap<>();
//                    childUpdates.put("/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
//                    childUpdates.put("/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
//                    FirebaseDatabase.getInstance().getReference().child("crazyMessages").updateChildren(childUpdates);


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

                    closeAlertDialogLoad();
                    //finish();

                } else {
                    // Handle failures

                }
            }
        });
    }

    public void loadRemoteUserInfoWithID(String idRemoteUser) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(idRemoteUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Administrador administrador = snapshot.getValue(Administrador.class);
                        if (administrador != null) {
                            usuarioRemoto = administrador;
                            loadRemoteUserInfo(usuarioRemoto);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(idRemoteUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trabajador trabajador = snapshot.getValue(Trabajador.class);
                        if (trabajador != null) {
                            usuarioRemoto = trabajador;
                            loadRemoteUserInfo(usuarioRemoto);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(idRemoteUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Empleador empleador = snapshot.getValue(Empleador.class);
                        if (empleador != null) {
                            usuarioRemoto = empleador;
                            loadRemoteUserInfo(usuarioRemoto);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void animationButtonMic(int i) {
        switch (i) {
            case 0:
                fabMessageMic.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_send_24));
//                rocketAnimation = (AnimationDrawable) fabMessageMic.getDrawable();
//                rocketAnimation.start();
                micMode = false;
                break;
            case 1:
                fabMessageMic.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_mic_24));
                micMode = true;
                break;
        }
    }

    public void loadUsuarioLocal() {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Administrador administrador = snapshot.getValue(Administrador.class);
                        if (administrador != null) {
                            usuarioLocal = administrador;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trabajador trabajador = snapshot.getValue(Trabajador.class);
                        if (trabajador != null) {
                            usuarioLocal = trabajador;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Empleador empleador = snapshot.getValue(Empleador.class);
                        if (empleador != null) {
                            usuarioLocal = empleador;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void loadRemoteUserInfo(Usuario usuario) {
        usuarioRemoto = usuario;
        if (!usuario.getNombre().isEmpty() && !usuario.getApellido().isEmpty()) {
            textViewNameContactTo.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido()));
        } else {
            textViewNameContactTo.setText(String.format("%s %s", "Usuario", "no encontrado"));
        }
        if (usuario.getFotoPerfil() != null) {
            Glide.with(getApplicationContext()).load(usuario.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageViewNameContactTo);
        }
        listenerMensajesLocales(usuario.getIdUsuario());
        deleteNotifications(usuarioRemoto.getIdUsuario());
        setUsuarioBloqueado(usuarioRemoto.getIdUsuario());
//        try {
//            setUsuarioBloqueado(usuarioTo.getIdUsuario());
//        } catch (Exception e) {
//
//        }
    }


    public void deleteNotifications(String idRemoteUser) {
        Log.d(TAG, "ELIMINANDO NOTIFICACIONES");
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idRemoteUser)
                .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Notificaciones eliminadas");

                        } else {
                            Log.d(TAG, "Error al eliminar notificaciones");

                        }
                    }
                });
    }

    public void listenerMensajesLocales(String idRemoto) {
        Log.d(TAG, "####################################");
        Log.d(TAG, "listenerMensajesLocales");
        Log.d(TAG, "####################################");
//        FirebaseDatabase.getInstance().getReference()
//                .child("crazyMessages")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//        x        .child(usuarioRemoto.getIdUsuario())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<MessageCloudPoc> messageCloudPocArrayList = new ArrayList<>();
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            MessageCloudPoc messageCloudPoc = data.getValue(MessageCloudPoc.class);
//                            Log.d(TAG, messageCloudPoc.toString());
//                            messageCloudPocArrayList.add(messageCloudPoc);
//                        }
////                ArrayList<MessageCloudPoc> messageCloudPocsAux = messageCloudPocArrayList;
////                updateMessagesPoc(messageCloudPocsAux);
////                updateMessagesPoc(messageCloudPocsAux);
//
//                        specialMessagePoc.setMensajeNubeArrayList(messageCloudPocArrayList);
//                        recyclerViewMensajes.scrollToPosition(messageCloudPocArrayList.size() - 1);
//                        //updateMessagesPoc(messageCloudPocArrayList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        valueEventListenerMessagesPoc = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "####################################");
                Log.d(TAG, "change messages");
                Log.d(TAG, "####################################");
                ArrayList<MessageCloudPoc> messageCloudPocArrayList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    MessageCloudPoc messageCloudPoc = data.getValue(MessageCloudPoc.class);
                    Log.d(TAG, messageCloudPoc.toString());
                    messageCloudPocArrayList.add(messageCloudPoc);
                }
//                ArrayList<MessageCloudPoc> messageCloudPocsAux = messageCloudPocArrayList;
//                updateMessagesPoc(messageCloudPocsAux);
//                updateMessagesPoc(messageCloudPocsAux);

                specialMessageListAdapterPoc.setMensajeNubeArrayList(messageCloudPocArrayList);
                recyclerViewMensajes.scrollToPosition(messageCloudPocArrayList.size() - 1);
                if (stateRemoteUser != null) {
                    if (stateRemoteUser.equals("eliminado")) {

                    }
                } else {
                    updateMessagesPoc(messageCloudPocArrayList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("crazyMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(usuarioRemoto.getIdUsuario())
                .child(idRemoto)
                .addValueEventListener(valueEventListenerMessagesPoc);
    }

    public void updateMessagesPoc(ArrayList<MessageCloudPoc> messageCloudPocArrayList) {
        Log.d(TAG, "####################################");
        Log.d(TAG, "updateMessagesPoc");
        Log.d(TAG, "####################################");


        for (MessageCloudPoc m : messageCloudPocArrayList) {
            if (m.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                if (!m.isEstadoLectura()) {

                    m.setEstadoLectura(true);

                    FirebaseDatabase.getInstance().getReference().child("crazyMessages")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(usuarioRemoto.getIdUsuario())
                            .child(m.getIdMensaje())
                            .setValue(m);

                    FirebaseDatabase.getInstance().getReference().child("crazyMessages")
                            .child(usuarioRemoto.getIdUsuario())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long numberMessages = snapshot.getChildrenCount();

                                    if (numberMessages > 0) {
                                        FirebaseDatabase.getInstance().getReference().child("crazyMessages")
                                                .child(usuarioRemoto.getIdUsuario())
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(m.getIdMensaje())
                                                .setValue(m);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                    //Map<String, Object> postValues = m.toMap();

                    //Map<String, Object> childUpdates = new HashMap<>();
                    //childUpdates.put("/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + m.getTo() + "/" + m.getIdMensaje(), postValues);
                    //childUpdates.put("/" + m.getFrom() + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + m.getIdMensaje(), postValues);
//                    childUpdates.put("/" + m.getTo() + "/" + m.getFrom() + "/" + m.getIdMensaje(), postValues);
                    //FirebaseDatabase.getInstance().getReference().child("crazyMessages").updateChildren(childUpdates);

//                    FirebaseDatabase.getInstance().getReference().child("crazyMessages")
//                            .child(m.getFrom())
//                            .child(m.getTo())
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    Log.d(TAG, "Number of messages: " + snapshot.getChildrenCount());
//                                    long numberMessages = snapshot.getChildrenCount();
//                                    for (DataSnapshot data : snapshot.getChildren()) {
//                                        MessageCloudPoc messageCloudPoc = data.getValue(MessageCloudPoc.class);
//                                        Log.d(TAG, messageCloudPoc.getContenido());
//                                    }
//                                    if (numberMessages > 0) {
//                                        childUpdates.put("/" + m.getTo() + "/" + m.getFrom() + "/" + m.getIdMensaje(), postValues);
//                                        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").updateChildren(childUpdates);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                    ChatPoc chatPoc = new ChatPoc();
//                    chatPoc.setIdRemoteUser(m.getTo());
//                    chatPoc.setLastMessageCloudPoc(m);
//                    FirebaseDatabase.getInstance().getReference().child("crazyChats")
//                            .child(m.getFrom())
//                            .child(chatPoc.getIdRemoteUser())
//                            .setValue(chatPoc);

//                    ChatPoc chatPocRemoto = new ChatPoc();
//                    chatPocRemoto.setIdRemoteUser(m.getFrom());
//                    chatPocRemoto.setLastMessageCloudPoc(m);
//                    FirebaseDatabase.getInstance().getReference().child("crazyChats")
//                            .child(m.getTo())
//                            .child(chatPocRemoto.getIdRemoteUser())
//                            .setValue(chatPocRemoto);
                }
            }
        }
        if (messageCloudPocArrayList.size() > 0) {
            if (messageCloudPocArrayList.get(messageCloudPocArrayList.size() - 1).getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                if (!messageCloudPocArrayList.get(messageCloudPocArrayList.size() - 1).isEstadoLectura()) {
                updateLastMessageOnChat(messageCloudPocArrayList.get(messageCloudPocArrayList.size() - 1));
//                }
            }

        }

    }

    public void updateLastMessageOnChat(MessageCloudPoc messageCloudPoc) {
        Log.d(TAG, "####################################");
        Log.d(TAG, "updateLastMessageOnChat");
        Log.d(TAG, "####################################");
        messageCloudPoc.setEstadoLectura(true);
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
    }


    public void sendMessage(MessageCloudPoc messageCloudPoc) {
        Log.d(TAG, "###########################");
        Log.d(TAG, "sendMessage");
        Log.d(TAG, messageCloudPoc.toString());
        Log.d(TAG, "###########################");
        Timestamp timestamp = new Timestamp(new Date());
        messageCloudPoc.setTimeStamp(timestamp.toString());

        String key = FirebaseDatabase.getInstance().getReference()
                .child("crazyMessages").push().getKey();
        messageCloudPoc.setIdMensaje(key);
        Map<String, Object> postValues = messageCloudPoc.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/crazyMessages/" + messageCloudPoc.getFrom()
                + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
        childUpdates.put("/crazyMessages/" + messageCloudPoc.getTo()
                + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
        childUpdates.put("/notificaciones/" + messageCloudPoc.getTo()
                + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Mensaje enviado");
            } else {
                Log.d(TAG, "Error al enviar mensaje");
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


        textInputEditTextMessage.setText("");
        micMode = true;
        animationButtonMic(1);

        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getFrom()).updateChildren(childUpdates);
        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getTo()).updateChildren(childUpdates);

    }
    // [END write_fan_out]

    public Dialog alertDialogAttachFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Compartir: ");
        String[] elements = {"Audio", "Imagen", "Ubicación"};
        builder.setItems(elements, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        selectAudio();
                        break;
                    case 1:
//                        selectImage();
                        escogerDesdeGaleria();
                        break;
                    case 2:
                        shareLocation();
                        break;
                }
            }
        });
        return builder.create();
    }

    public void shareLocation() {
        Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
        intent.putExtra("usuarioTo", usuarioRemoto);
        intent.putExtra("usuarioLocal", usuarioLocal);
//        intent.putExtra("chat", chat);
//        startActivity(intent);
        startActivityForResult(intent, LOCATION_CODE);

    }

    public void selectAudio() {

//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
//        startActivityForResult(intent, SELECT_AUDIO);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent_upload = new Intent();
            intent_upload.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, SELECT_AUDIO);
        } else {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo de audio desde: "),SELECT_AUDIO);
                startActivityForResult(intent, SELECT_AUDIO);
            }
        }

    }

    public void escogerDesdeGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }


    public void openAlertDialogPhotoOptions() {
        tomarfoto();
    }

    public void tomarfoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                ContentResolver resolver = getApplicationContext().getContentResolver();

                Uri audioCollection;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    audioCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                } else {
                    audioCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                String displayName = "image." + System.currentTimeMillis() + ".jpeg";

                ContentValues newSongDetails = new ContentValues();
                newSongDetails.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis());
                newSongDetails.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis());
                newSongDetails.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                newSongDetails.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);


                Uri picUri = resolver.insert(audioCollection, newSongDetails);
                uriPhoto = picUri;

                Log.d("FotoUdir asda", uriPhoto.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_PERMISSION_FOTO_PERFIL);

            }


        } catch (Exception e) {
            Log.e(TAG, "ERRORRRRRRR!");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getLocalizedMessage());
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
//            startActivity(new Intent(getApplicationContext(), CamActivity.class));
        }
    }


    public void selectPhoto() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
//            openAlertDialogPhotoOptions();
            tomarfoto();
        } else {
            // Permission is missing and must be requested.
            requestCameraAndWExtStPermission();
        }
        // END_INCLUDE(startCamera)
    }


    public void requestCameraAndWExtStPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }

    public void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_ONLY);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA_ONLY);
        }
    }

    public void requestCameraAPILocaPermission() {
//        Toast.makeText(getApplicationContext(), "Request camera permision", Toast.LENGTH_LONG).show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_REQUEST_CAMERA_LOCA);
                    }
                }).show();
            } else {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("");
                    builder.setMessage(R.string.permiso_camera_text);
                    // Add the buttons
                    builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    // Set other dialog properties

                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    // You can directly ask for the permission.
                    Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);

                }


                // The registered ActivityResultCallback gets the result of this request.
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
//                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                try {
                    //requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        } else {

        }


//        // Permission has not been granted and must be requested.
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.CAMERA)) {
//            // Provide an additional rationale to the user if the permission was not granted
//            // and the user would benefit from additional context for the use of the permission.
//            // Display a SnackBar with cda button to request the missing permission.
//            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
//                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Request the permission
//                    ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
//                            new String[]{Manifest.permission.CAMERA},
//                            PERMISSION_REQUEST_CAMERA_LOCA);
//                }
//            }).show();
//
//        } else {
//            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
//            // Request the permission. The result will be received in onRequestPermissionResult().
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
//        }
    }

    public void cleanMessagesPocListener() {
        // Clean up value listener
        // [START clean_basic_listen]
        Log.d(TAG, "####################################");
        Log.d(TAG, "cleanMessagesPocListener");
        Log.d(TAG, "####################################");
        FirebaseDatabase.getInstance().getReference().child("crazyMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(usuarioRemoto.getIdUsuario())
                .removeEventListener(valueEventListenerMessagesPoc);
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void closeAlertDialogLoad() {
        try {
            alertDialogVar.dismiss();
        } catch (Exception e) {

        }
    }

    public void showCustomProgressDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.custom_progress_dialog, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
//        return builder.create();
        final TextView textViewTitle = promptsView.findViewById(R.id.textViewTitle);
        textViewMessage = promptsView.findViewById(R.id.textViewMessage);

        textViewTitle.setText(title);
        textViewMessage.setText(message);

        alertDialogVar = builder.create();
        alertDialogVar.show();
//        builder.show();
    }

    public void updateProgress(Double progress) {

        String message = String.format(Locale.getDefault(), "Cargando imagen: %.2f %s", progress, "%");
        textViewMessage.setText(message);
    }

    private void sendAudioMessage(MessageCloudPoc messageCloudPoc, Uri audioUri) {
        String title = "Por favor espere";
        String message = "Cargando audio...";
        showCustomProgressDialog(title, message);
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

        Uri uri = audioUri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        messageCloudPoc.setAudioDuration(durationStr);


        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


        String fileExtensionImage = MimeTypeMap.getFileExtensionFromUrl(audioUri.toString());
        String mimeTypeImage = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionImage);
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType(mimeTypeImage)
                .build();
//
//                Toast.makeText(activity, mimeType, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, fileExtension, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, mensajeNube.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, chat.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, selectedUri.toString(), Toast.LENGTH_LONG).show();


        String baseReference = "gs://tfinal2022-afc91.appspot.com";
//        String imagePath = baseReference + "/" + "mensajes" + "/" + chat.getIdChat() + "/" + mensajeNube.getIdMensaje() + "." + fileExtensionImage;
        String imagePath = baseReference + "/" + "mensajes" + "/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key + "." + fileExtensionImage;
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(imagePath);


//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()), storageMetadata);
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));

        UploadTask uploadTask = storageRef.putFile(audioUri, storageMetadata);

//        if (mensajeNube.getContenido().contains("content:")) {
//            uploadTask = storageRef.putFile(uriImage, storageMetadata);
//
//        } else {
//            Uri imageUriSend = Uri.fromFile(new File(mensajeNube.getContenido()));
//            uploadTask = storageRef.putFile(imageUriSend, storageMetadata);
//
//        }


//                uploadTask = storageRef.putFile(imageUri, storageMetadata);
        // Listen for state changes, errors, and completion of the upload.
        StorageReference finalStorageRef = storageRef;
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            updateAudioProgress(progress);
            Log.d(TAG, "Upload is " + progress + "% done");

        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "on failure Foto complete...");
                closeAlertDialogLoad();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
                Log.d(TAG, "Upload is complete...");
                //  registroActivity.limpiarUI();
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return finalStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
//                            Toast.makeText(activity, downloadUri.toString(), Toast.LENGTH_LONG).show();


                    //MessageCloudPoc post = new MessageCloudPoc();
                    messageCloudPoc.setContenido(downloadUri.toString());
                    messageCloudPoc.setType(2);
                    Map<String, Object> postValues = messageCloudPoc.toMap();

//                    Map<String, Object> childUpdates = new HashMap<>();
//                    childUpdates.put("/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
//                    childUpdates.put("/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
//                    FirebaseDatabase.getInstance().getReference().child("crazyMessages").updateChildren(childUpdates);

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

                    closeAlertDialogLoad();
                    //finish();

                } else {
                    // Handle failures

                }
            }
        });





        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getFrom()).updateChildren(childUpdates);
        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getTo()).updateChildren(childUpdates);

    }

    private void updateAudioProgress(double progress) {
        String message = String.format(Locale.getDefault(), "Cargando audio: %.2f %s", progress, "%");
        textViewMessage.setText(message);
    }

    public void setUsuarioBloqueado(String idTo) {
        SharedPreferences myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        editorPref = myPreferences.edit();
        editorPref.putString("idUserBlocking", idTo);
        editorPref.apply();
//        Toast.makeText(getApplicationContext(), "Usuario bloqueado: \n" + myPreferences.getString("idUserBlocking", ""), Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_individual_chat);
        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        micMode = true;


        try {

            fileName = getExternalCacheDir().getAbsolutePath();
            fileName += "/audiorecordtest.mp3";

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        loadUsuarioLocal();
        usuario = myPreferences.getInt("usuario", -1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textViewNameContactTo = findViewById(R.id.textViewNameParticipant);
        imageViewNameContactTo = findViewById(R.id.imageViewParticipant);

        findViewById(R.id.imageButtonCall).setOnClickListener(this);
        findViewById(R.id.imageButtonVideoCall).setOnClickListener(this);
        findViewById(R.id.buttonAttachFile).setOnClickListener(this);

        ImageView imageViewCall = findViewById(R.id.imageButtonCall);
        ImageView imageViewVideoCall = findViewById(R.id.imageButtonVideoCall);

        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        imageViewCall.setColorFilter(colorNight);
        imageViewVideoCall.setColorFilter(colorNight);
        /*Esto es una maravilla*/

        Trabajador trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
        String idRemoteUser = getIntent().getStringExtra("idRemoteUser");
        stateRemoteUser = getIntent().getStringExtra("stateRemoteUser");
//        stateRemoteUser = "eliminado";
//        if (trabajador != null) {
//            Log.d(TAG, trabajador.toString());
//            loadRemoteUserInfo(trabajador);
//        }
//        if (idRemoteUser != null) {
//            loadRemoteUserInfoWithID(idRemoteUser);
//        }
        //blockingUI();
        if (stateRemoteUser != null) {
            //Toast.makeText(getApplicationContext(), stateRemoteUser, Toast.LENGTH_LONG).show();
            if (stateRemoteUser.equals("eliminado")) {
                blockingUI(idRemoteUser);
            }
        } else {
            if (trabajador != null) {
                Log.d(TAG, trabajador.toString());
                loadRemoteUserInfo(trabajador);
            }
            if (idRemoteUser != null) {
                loadRemoteUserInfoWithID(idRemoteUser);
            }
        }

        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes);

        specialMessageListAdapterPoc = new SpecialMessageListAdapterPoc(this);
        recyclerViewMensajes.setAdapter(specialMessageListAdapterPoc);
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager) recyclerViewMensajes.getLayoutManager()).setStackFromEnd(true);

        textInputEditTextMessage = findViewById(R.id.textInputEditTextMessage);

        textInputEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nowMessage = charSequence.toString();
                Log.d(TAG, String.format("Mensaje actual: %s", nowMessage));
                Log.d(TAG, String.format("Mensaje actual i: %d", i));
                Log.d(TAG, String.format("Mensaje actual i1: %d", i1));
                Log.d(TAG, String.format("Mensaje actual i2: %d", i2));

                if (i == 0 && i1 == 1 && i2 == 0) {
                    animationButtonMic(1);
                } else {
                    animationButtonMic(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fabMessageMic = findViewById(R.id.buttonMessageAndMic);
        fabMessageMic.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        playing = true;
        fabPlay = findViewById(R.id.fabPlay);
        stopAudio = true;
        seekBarProgress = findViewById(R.id.seekBarProgress);
        textViewCurrentTime = findViewById(R.id.textViewUpdateTime);
        handler = new Handler();
        fabPlay.setOnClickListener(clickListener);
        textViewDuration = findViewById(R.id.textViewDuration);

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);

                    textViewCurrentTime.setText(String.format(Locale.US, "%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                            TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) mediaPlayer.getCurrentPosition()))));

                    /**/
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
//                findViewById(R.id.record_button).setVisibility(View.GONE);
//                findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
//                startRecording();


                if (ContextCompat.checkSelfPermission(CrazyIndividualChatActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    findViewById(R.id.record_button).setVisibility(View.GONE);
                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                    startRecording();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(CrazyIndividualChatActivity.this,
                        Manifest.permission.RECORD_AUDIO)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.

                    Snackbar.make(fabMessageMic, "Permiso de audio necesario",
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    PERMISSION_REQUEST_RECORD_AUDIO);
                        }
                    }).show();
                } else {
                    // You can directly ask for the permission.
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_RECORD_AUDIO);
                }

            }
        });
        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.record_button).setVisibility(View.VISIBLE);
                findViewById(R.id.stop_button).setVisibility(View.GONE);
                stopRecording();


            }
        });

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.playConstrols).setVisibility(View.GONE);
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                try {
                    recorder.stop();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                File dir = getExternalCacheDir();
//                File file = new File(dir, "audiorecordtest.mp3");
                File file = new File(fileName);
                boolean deleted = file.delete();
                if (deleted) {
                    Log.d(TAG, "Archivo de audio eliminado");
                    seekBarProgress.setProgress(0);
                    textViewCurrentTime.setText("0:00");
                    handler.removeCallbacks(runnable);
                    playing = true;
                    //mediaPlayer.release();
                    //mediaPlayer = new MediaPlayer();
                    stopAudio = true;
                    fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                    findViewById(R.id.messageContainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.linLayoutMicControls).setVisibility(View.GONE);

                } else {
                    Log.d(TAG, "Archivo de audio no eliminado");
                }

                try {
                    findViewById(R.id.textViewBlink).setVisibility(View.GONE);
                    findViewById(R.id.textViewBlink).clearAnimation();
                } catch (Exception e) {

                }
            }
        });


        findViewById(R.id.fabSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.playConstrols).setVisibility(View.GONE);
                try {
                    findViewById(R.id.textViewBlink).setVisibility(View.GONE);
                    findViewById(R.id.textViewBlink).clearAnimation();
                } catch (Exception e) {

                }
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {

                }
                try {
                    recorder.stop();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

//                recorder.release();
//                recorder = null;


//                File dir = getExternalCacheDir();
//                File file = new File(dir, "audiorecordtest.3gp");
//                boolean deleted = file.delete();
//                if (deleted) {
                seekBarProgress.setProgress(0);
                textViewCurrentTime.setText("0:00");
                handler.removeCallbacks(runnable);
                playing = true;
                //mediaPlayer.release();
                //mediaPlayer = new MediaPlayer();
                stopAudio = true;
                fabPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_play_arrow_24));

                findViewById(R.id.messageContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.linLayoutMicControls).setVisibility(View.GONE);


                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                mensajeNube.setContenido(fileName);
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioRemoto.getIdUsuario());
                mensajeNube.setEstadoLectura(false);

                Uri uri = Uri.parse(fileName);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(getApplicationContext(), uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                mensajeNube.setAudioDuration(durationStr);
                mensajeNube.setType(2);/*0 audio */


                MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                messageCloudPoc.setContenido(fileName);
                messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                messageCloudPoc.setTo(usuarioRemoto.getIdUsuario());
                messageCloudPoc.setEstadoLectura(false);
                messageCloudPoc.setType(2);/*0 texto */
                Log.d(TAG, messageCloudPoc.toString());
//                sendAudioMessage();
                sendAudioMessageWithFilename(messageCloudPoc);


//                if (chat == null) {
//                    String idChat = FirebaseDatabase.getInstance().getReference()
//                            .child("chats")
//                            .push().getKey();
//                    chat = new Chat();
//                    chat.setIdChat(idChat);
//                    chat.setMensajeNube(mensajeNube);
//
//                    Participante participante1 = new Participante();
//                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
//                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                    Participante participante2 = new Participante();
//                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
//                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                    participanteArrayList.add(participante1);
//                    participanteArrayList.add(participante2);
//
//                    chat.setParticipantes(participanteArrayList);
//                    mensajeNube.setIdChat(chat.getIdChat());
//                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);
//
//                } else {
//                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
//                }


//                } else {
//                    Log.d(TAG, "Archivo de audio no eliminado");
//                }


            }
        });


        fabChooseImageProfile = findViewById(R.id.buttonCamera);
        fabChooseImageProfile.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_crazy_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnu_agendar_cita:
//            case R.id.mnu_crear_cita:
                Intent intent = new Intent(CrazyIndividualChatActivity.this, NuevaCitaTrabajoActivity.class);
                intent.putExtra("usuarioFrom", usuarioLocal);
                intent.putExtra("usuarioTo", usuarioRemoto);
                startActivity(intent);

                return true;
            case R.id.mnu_eliminar_mensajes:
                showAlertDialogEliminarMensajes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItemO = menu.findItem(R.id.mnu_agendar_cita);
        menuItemO.setVisible(false);
        switch (usuario) {
            case 0:
            case 1:
                menuItemO.setVisible(false);
                break;
            case 2:
                menuItemO.setVisible(true);
                break;
        }

        if (stateRemoteUser != null) {
            //Toast.makeText(getApplicationContext(), stateRemoteUser, Toast.LENGTH_LONG).show();
            if (stateRemoteUser.equals("eliminado")) {
                menuItemO.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonVideoCall:
//                Intent intentVideollamada = new Intent(CrazyIndividualChatActivity.this, AgoraVideoCallActivity.class);
                Intent intentVideollamada = new Intent(CrazyIndividualChatActivity.this, VideoCallMainActivity.class);
                intentVideollamada.putExtra("usuarioRemoto", usuarioRemoto);
                intentVideollamada.putExtra("usuarioLocal", usuarioLocal);
                String channelNamea = FirebaseDatabase.getInstance().getReference().child("videoCalls")
                        .push().getKey();
                intentVideollamada.putExtra("channelName", channelNamea);
                intentVideollamada.putExtra("callStatus", "llamadaSaliente");
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
////                        Intent intentVideollamada = new Intent(CrazyIndividualChatActivity.this, VideoLlamadaActivity.class);
//                        Intent intentVideollamada = new Intent(CrazyIndividualChatActivity.this, AgoraVideoCallActivity.class);
//                        intentVideollamada.putExtra("usuarioRemoto", usuarioRemoto);
//                        intentVideollamada.putExtra("usuarioLocal", usuarioLocal);
//                        String channelName = FirebaseDatabase.getInstance().getReference().child("videoCalls")
//                                .push().getKey();
//                        intentVideollamada.putExtra("channelName", channelName);
//                        intentVideollamada.putExtra("callStatus", "llamadaSaliente");
//
////                        intentVideollamada.putExtra("callStatus", 0);
                startActivity(intentVideollamada);
//                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//
//                        Snackbar.make(fabMessageMic, "Permiso de cámara y micrófono necesario",
//                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Request the permission
//                                Toast.makeText(getApplicationContext(), "GG permisos", Toast.LENGTH_LONG).show();
//                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 8000);
//                            }
//                        }).show();
//                    } else {
//
//
//                        /* 10 - 11 - 12 */
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        builder.setTitle("");
//                            builder.setMessage(R.string.permiso_video_call_text);
//                            // Add the buttons
//                            builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // User clicked OK button
//
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    intent.setData(Uri.parse("package:" + getPackageName()));
//                                    startActivity(intent);
//                                }
//                            });
//                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // User cancelled the dialog
//                                }
//                            });
//                            // Set other dialog properties
//
//                            // Create the AlertDialog
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//
//                        } else {
//                            // You can directly ask for the permission.
//                            Snackbar.make(fabMessageMic, "Permiso de cámara y micrófono no concedido", Snackbar.LENGTH_LONG).show();
//
//                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 8000);
//                        }
//
//
//                    }
//
//                } else {
//
//                }
                /*************************/

//                if (ContextCompat.checkSelfPermission(
//                        this, Manifest.permission.CAMERA) ==
//                        PackageManager.PERMISSION_GRANTED) {
//                    // You can use the API that requires the permission.
//
//
//                    Intent intentVideollamada = new Intent(CrazyIndividualChatActivity.this, VideoLlamadaActivity.class);
//                    intentVideollamada.putExtra("usuarioTo", usuarioRemoto);
//                    intentVideollamada.putExtra("usuarioFrom", usuarioLocal);
//                    intentVideollamada.putExtra("callStatus", 0);
//                    startActivity(intentVideollamada);
//
//                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//                    // In an educational UI, explain to the user why your app requires this
//                    // permission for a specific feature to behave as expected. In this UI,
//                    // include a "cancel" or "no thanks" button that allows the user to
//                    // continue using your app without granting the permission.
//                    showInContextUI(...);
//
//                } else {
//                    // You can directly ask for the permission.
//                    // The registered ActivityResultCallback gets the result of this request.
//                    requestPermissionLauncher.launch(
//                            Manifest.permission.REQUESTED_PERMISSION);
//                }


                break;
            case R.id.imageButtonCall:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {


//                        Intent intent = new Intent(CrazyIndividualChatActivity.this, LlamadaVozActivity.class);
//                        Intent intent = new Intent(CrazyIndividualChatActivity.this, AgoraOnlyVoiceCallActivity.class);
                        Intent intent = new Intent(CrazyIndividualChatActivity.this, VoiceCallMainActivity.class);
//                        intent.putExtra("usuarioTo", usuarioRemoto);
//                        intent.putExtra("usuarioFrom", usuarioLocal);
//                        intent.putExtra("callStatus", 0);
                        intent.putExtra("usuarioRemoto", usuarioRemoto);
                        intent.putExtra("usuarioLocal", usuarioLocal);
                        String channelName = FirebaseDatabase.getInstance().getReference().child("voiceCalls")
                                .push().getKey();
                        intent.putExtra("channelName", channelName);
                        intent.putExtra("callStatus", "llamadaSaliente");

                        startActivity(intent);


                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {

                        Snackbar.make(fabMessageMic, "Permiso de micrófono necesario",
                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request the permission
                                Toast.makeText(getApplicationContext(), "GG permisos", Toast.LENGTH_LONG).show();
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                            }
                        }).show();
                    } else {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("");
                            builder.setMessage(R.string.permiso_call_text);
                            // Add the buttons
                            builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            // Set other dialog properties

                            // Create the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            // You can directly ask for the permission.
                            Snackbar.make(fabMessageMic, "Permiso de micrófono no concedido", Snackbar.LENGTH_LONG).show();
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                        }


                    }

                } else {

                    Log.d(TAG, "SDK menos que 6.0 Marshmallow");

                }

                break;

            case R.id.buttonMessageAndMic:
                if (micMode) {

                    if (ContextCompat.checkSelfPermission(CrazyIndividualChatActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {


                        findViewById(R.id.messageContainer).setVisibility(View.GONE);
                        findViewById(R.id.linLayoutMicControls).setVisibility(View.VISIBLE);

                        findViewById(R.id.record_button).setVisibility(View.GONE);
                        findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                        startRecording();

                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(CrazyIndividualChatActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected. In this UI,
                        // include a "cancel" or "no thanks" button that allows the user to
                        // continue using your app without granting the permission.

                        Snackbar.make(fabMessageMic, "Permiso de audio necesario",
                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request the permission
                                ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        PERMISSION_REQUEST_RECORD_AUDIO);
                            }
                        }).show();
                    } else {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("");
                            builder.setMessage(R.string.permiso_audio_text);
                            // Add the buttons
                            builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            // Set other dialog properties

                            // Create the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            // You can directly ask for the permission.
                            Snackbar.make(fabMessageMic, "Permiso de micrófono no concedido", Snackbar.LENGTH_LONG).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                        PERMISSION_REQUEST_RECORD_AUDIO);
                            }
                        }

                    }


//                    startActivity(new Intent(this, PoCActivity4.class));
                    //Toast.makeText(getApplicationContext(), "Mic mode", Toast.LENGTH_LONG).show();
                } else {
                    String message = textInputEditTextMessage.getText().toString();
//                    Toast.makeText(getApplicationContext(), "Message mode", Toast.LENGTH_LONG).show();
                    if (message.length() > 0) {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), usuarioLocal.toString(), Toast.LENGTH_LONG).show();
                        MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
                        //mensajeNube.setIdMensaje();
                        //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                        messageCloudPoc.setContenido(message.trim());
                        messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        messageCloudPoc.setTo(usuarioRemoto.getIdUsuario());
                        messageCloudPoc.setEstadoLectura(false);
                        messageCloudPoc.setType(0);/*0 texto */
                        Log.e(TAG, "@######################################");
                        Log.e(TAG, messageCloudPoc.toString());
                        sendMessage(messageCloudPoc);
//                        if (chat == null) {
//                            String idChat = FirebaseDatabase.getInstance().getReference()
//                                    .child("chats")
//                                    .push().getKey();
//                            chat = new Chat();
//                            chat.setIdChat(idChat);
//                            chat.setMensajeNube(mensajeNube);
//
//                            Participante participante1 = new Participante();
//                            participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//
//                            participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
//
//                            participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                            Participante participante2 = new Participante();
//                            participante2.setIdParticipante(usuarioTo.getIdUsuario());
//
//                            participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//
//                            participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                            ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                            participanteArrayList.add(participante1);
//                            participanteArrayList.add(participante2);
//
//                            chat.setParticipantes(participanteArrayList);
//                            mensajeNube.setIdChat(chat.getIdChat());
//                            usuarioLocal.crearChat(chat, mensajeNube, this);
//
//                        } else {
//                            usuarioLocal.enviarMensaje(chat, mensajeNube, this);
//                        }
                        //mensajeNube.setTimeStamp();
                    }


                }
                break;

            case R.id.buttonAttachFile:
//                Toast.makeText(getApplicationContext(), "Attach file", Toast.LENGTH_LONG).show();
//                alertDialogAttachFile().show();

                if (ContextCompat.checkSelfPermission(CrazyIndividualChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.

                    alertDialogAttachFile().show();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(CrazyIndividualChatActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.

                    Snackbar.make(fabMessageMic, "Permiso de almacenamiento necesario",
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            ActivityCompat.requestPermissions(CrazyIndividualChatActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    // You can directly ask for the permission.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("");
                        builder.setMessage(R.string.permiso_cam_audio_text);
                        // Add the buttons
                        builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        // Set other dialog properties

                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Snackbar.make(fabMessageMic, "Permiso de almacenamiento no concedido", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                        }

                    }


                }
                break;
            case R.id.buttonCamera:
                Log.d(TAG, "##############################");
                Log.d(TAG, "Opening camera...");
                Log.d(TAG, "##############################");
                Log.d(TAG, "SDK VERSION: %d" + Build.VERSION.SDK_INT);

//                Intent intentCamx = new Intent(this, CamXActivity.class);
                //Intent intentCamx = new Intent(this, CamActivity.class);
                //intentCamx.putExtra("usuarioRemoto", usuarioRemoto);
                //startActivity(intentCamx);

                //                Toast.makeText(getApplicationContext(), "Attach file", Toast.LENGTH_LONG).show();
                //system os is less then marshallow
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    openAlertDialogPhotoOptions();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    selectPhoto();
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        openAlertDialogPhotoOptions();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraPermission();
                    }
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        openAlertDialogPhotoOptions();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraAPILocaPermission();
                    }
                }


//                Toast.makeText(getApplicationContext(), "Attach file", Toast.LENGTH_LONG).show();
                //system os is less then marshallow
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
////                    openAlertDialogPhotoOptions();
//                    Log.d(TAG, "TOMAR FOTO DIRECTAMENTE");
//                    tomarfoto();
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    Log.d(TAG, "SOLICITANDO PERMISOS PARA TOMAR LA FOTO");
//                    selectPhoto();
//                }
//
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        // Permission is already available
////                        openAlertDialogPhotoOptions();
//                        tomarfoto();
//                    } else {
//                        // Permission is missing and must be requested.
//                        requestCameraPermission();
//                    }
//                }
//
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        // Permission is already available
////                        openAlertDialogPhotoOptions();
//                        tomarfoto();
//                    } else {
//                        // Permission is missing and must be requested.
//                        requestCameraAPILocaPermission();
//                    }
//                }
//
                break;

        }

    }


    @Override
    public void onStop() {
        super.onStop();
        //cleanMessagesPocListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                // Request for camera permission.
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
//                    openAlertDialogPhotoOptions();
                    tomarfoto();
                } else {
                    // Permission request was denied.
                    Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case PERMISSION_REQUEST_CAMERA_ONLY:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
//                    openAlertDialogPhotoOptions();
                    tomarfoto();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                }
                break;

            case PERMISSION_REQUEST_CAMERA_LOCA:
                // Request for camera permission.
                if (grantResults.length >= 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission has been granted. Start camera preview Activity.
//                    openAlertDialogPhotoOptions();
                    tomarfoto();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                }
                break;
            case PERMISSION_REQUEST_RECORD_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
//                    findViewById(R.id.record_button).setVisibility(View.GONE);
//                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
//                    startRecording();

                    findViewById(R.id.messageContainer).setVisibility(View.GONE);
                    findViewById(R.id.linLayoutMicControls).setVisibility(View.VISIBLE);

                    findViewById(R.id.record_button).setVisibility(View.GONE);
                    findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                    startRecording();

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                break;
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    alertDialogAttachFile().show();

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                break;
        }


        // END_INCLUDE(onRequestPermissionsResult)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_CODE && resultCode == RESULT_OK) {
//            Toast.makeText(getContext(), "aaaaaaaaaa", Toast.LENGTH_LONG).show();
            String resultMessage = data.getStringExtra("EXTRA_REPLY");
            String latitude = data.getStringExtra("EXTRA_REPLY_LATITUDE");
            String longitude = data.getStringExtra("EXTRA_REPLY_LONGITUDE");


            MensajeNube mensajeNube = new MensajeNube();
            //mensajeNube.setIdMensaje();
            //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                    mensajeNube.setContenido(geo);
            mensajeNube.setContenido(resultMessage);
            mensajeNube.setLatitude(Double.parseDouble(latitude));
            mensajeNube.setLongitude(Double.parseDouble(longitude));
            mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mensajeNube.setTo(usuarioRemoto.getIdUsuario());
            mensajeNube.setEstadoLectura(false);

            mensajeNube.setType(4);/*4 location */

            Log.d(TAG, mensajeNube.toString());
//            if (chat == null) {
//                String idChat = FirebaseDatabase.getInstance().getReference()
//                        .child("chats")
//                        .push().getKey();
//                chat = new Chat();
//                chat.setIdChat(idChat);
//                chat.setMensajeNube(mensajeNube);
//
//                Participante participante1 = new Participante();
//                participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//
//                participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
//
//                participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                Participante participante2 = new Participante();
//                participante2.setIdParticipante(usuarioTo.getIdUsuario());
//                participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//
//                participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                participanteArrayList.add(participante1);
//                participanteArrayList.add(participante2);
//
//                chat.setParticipantes(participanteArrayList);
//                mensajeNube.setIdChat(chat.getIdChat());
//                usuarioLocal.crearChat(chat, mensajeNube, this);
//
//            } else {
//                usuarioLocal.enviarMensaje(chat, mensajeNube, this);
//            }

        }
        if (requestCode == SELECT_AUDIO && resultCode == RESULT_OK) {
            //Toast.makeText(getApplicationContext(), "Enviando audio desde archivo", Toast.LENGTH_LONG).show();

            try {
                final Uri audioUri = data.getData();

                //                        Toast.makeText(getApplicationContext(), usuarioLocal.toString(), Toast.LENGTH_LONG).show();
                MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                messageCloudPoc.setContenido(audioUri.toString());
                messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                messageCloudPoc.setTo(usuarioRemoto.getIdUsuario());
                messageCloudPoc.setEstadoLectura(false);
                messageCloudPoc.setType(2);/*0 texto */
                Log.d(TAG, messageCloudPoc.toString());
                sendAudioMessage(messageCloudPoc, audioUri);
                if (audioUri != null) {

//                    final File audio = new File(String.valueOf(audioUri));
//
//// Get uri related document id.
//                    String documentId = DocumentsContract.getDocumentId(audioUri);
//
//                    // Get uri authority.
//                    String uriAuthority = audioUri.getAuthority();
//
//
//                    String idArr[] = documentId.split(":");
//                    if (idArr.length == 2) {
//                        // First item is document type.
//                        String docType = idArr[0];
//
//                        // Second item is document real id.
//                        String realDocId = idArr[1];
//
//                        // Get content uri by document type.
//                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        if ("image".equals(docType)) {
//                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("video".equals(docType)) {
//                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("audio".equals(docType)) {
//                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                        }
//
//                        // Get where clause with real document id.
//                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;
//
//
//                        String ret = "";
//
//                        // Query the uri with condition.
//                        Cursor cursor = IndividualChatActivity.this.getContentResolver().query(audioUri, null, whereClause, null, null);

//                        if (cursor != null) {
//                            boolean moveToFirst = cursor.moveToFirst();
//                            if (moveToFirst) {
//
//                                // Get columns name by uri type.
//                                String columnName = MediaStore.Images.Media.DATA;
//
//                                if (audioUri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
//                                    columnName = MediaStore.Images.Media.DATA;
//                                } else if (audioUri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
//                                    columnName = MediaStore.Audio.Media.DATA;
//                                } else if (audioUri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
//                                    columnName = MediaStore.Video.Media.DATA;
//                                }
//
//                                // Get column index.
//                                int columnIndex = cursor.getColumnIndex(columnName);
//
//                                // Get column value which is the uri related file local path.
//                                ret = cursor.getString(columnIndex);
//                                Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                                Log.d(TAG, ret);
//
//                            }
//                        }
                }


//                    Log.d(TAG, audioUri.toString());
//                    Log.d(TAG, audio.getName());
//                    Log.d(TAG, audio.getPath());
//                    Log.d(TAG, audio.getAbsolutePath());
//                    String displayName = "";
//                    Cursor cursord = IndividualChatActivity.this.getContentResolver()
//                            .query(audioUri, null, null, null, null, null);

//                    try {
//                        // moveToFirst() returns false if the cursor has 0 rows. Very handy for
//                        // "if there's anything to look at, look at it" conditionals.
//                        if (cursord != null && cursord.moveToFirst()) {
//
//                            // Note it's called "Display Name". This is
//                            // provider-specific, and might not necessarily be the file name.
//                            displayName = cursord.getString(
//                                    cursord.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                            Log.i(TAG, "Display Name: " + displayName);
//
//                            int sizeIndex = cursord.getColumnIndex(OpenableColumns.SIZE);
//                            // If the size is unknown, the value stored is null. But because an
//                            // int can't be null, the behavior is implementation-specific,
//                            // and unpredictable. So as
//                            // a rule, check if it's null before assigning to an int. This will
//                            // happen often: The storage API allows for remote files, whose
//                            // size might not be locally known.
//                            String size = null;
//                            if (!cursord.isNull(sizeIndex)) {
//                                // Technically the column stores an int, but cursor.getString()
//                                // will do the conversion automatically.
//                                size = cursord.getString(sizeIndex);
//                            } else {
//                                size = "Unknown";
//                            }
//                            Log.i(TAG, "Size: " + size);
//                        }
//                    } finally {
//                        cursord.close();
//                    }
//
//                    File appSpecificExternalDir = new File(IndividualChatActivity.this.getExternalFilesDir(null), audio.getName());
//
//
//                    try {
//                        Log.d(TAG, "appSpecificExternalDir");
//                        Log.d(TAG, appSpecificExternalDir.getPath());
//                        Log.d(TAG, appSpecificExternalDir.getAbsolutePath());
//                        Log.d(TAG, appSpecificExternalDir.getParent() + "/" + displayName + ".mp3");
//                        Log.d(TAG, appSpecificExternalDir.getCanonicalPath());
//                        Log.d(TAG, appSpecificExternalDir.getName());
//
//                    } catch (Exception e) {
//                        Log.d(TAG, e.toString());
//                    }
//                    Log.d(TAG, fileName);
//            Log.d(TAG, audio.);
//                    Log.d(TAG, audio.getPath());
                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                    mensajeNube.setContenido(audioUri.toString());
//                    mensajeNube.setContenido(appSpecificExternalDir.getParent() + "/" + displayName + ".mp3");

                //Uri imageUriSend = Uri.fromFile(new File(mensajeNube.getContenido()));
                // Log.d(TAG, imageUriSend.toString());


                /*Filtro maldito*/
                if (isExternalStorageDocument(audioUri)) {
                    Log.d(TAG, "SIIIIIIIIIIIII");
                    final String docId = DocumentsContract.getDocumentId(audioUri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        String audioPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        mensajeNube.setContenido(audioPath);
                        Log.d(TAG, audioPath);
                    }
                } else {
                    if (isMediaDocument(audioUri)) {
                        Log.d(TAG, "isMediaDocument");
                        Log.d(TAG, audioUri.toString());
                        // Log.d(TAG, imageUriSend.toString());
                        mensajeNube.setContenido(audioUri.toString());


//                            try {
//                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                                Context context = getApplicationContext();
//
//                                String[] filePathColumn = {MediaStore.Audio.Media.DATA};
//                                Log.d(TAG, "locationMetadata: ");
//
//// Find the videos that are stored on a device by querying the video collection.
//                                try (Cursor cursor = context.getContentResolver().query(
//                                        audioUri,
//                                        filePathColumn,
//                                        null,
//                                        null,
//                                        null
//                                )) {
//                                    int idColumn = cursor.getColumnIndexOrThrow(filePathColumn[0]);
//                                    while (cursor.moveToNext()) {
//                                        long id = cursor.getLong(idColumn);
//                                        Uri videoUri = ContentUris.withAppendedId(
//                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
//                                        Log.e(TAG, "Video uri: " + videoUri.toString());
//
//                                        mensajeNube.setContenido(videoUri.toString());
////                                        extractVideoLocationInfo(videoUri);
//                                        try {
//                                            retriever.setDataSource(context, videoUri);
//                                        } catch (RuntimeException e) {
//                                            Log.e(TAG, "Cannot retrieve video file", e);
//                                        }
//                                        // Metadata should use a standardized format.
//                                        String locationMetadata = retriever.extractMetadata(
//                                                MediaMetadataRetriever.METADATA_KEY_LOCATION);
//                                        Log.d(TAG, "locationMetadata: " + locationMetadata);
//
//                                    }
//                                }
//
//
//                            } catch (Exception e) {
//                                Log.d(TAG, e.toString());
//                            }


//                            try {
//                                Log.d(TAG, "isMediaDocument");
//                                //mensajeNube.setContenido(audioUri.toString());
////                                Log.d(TAG, "isMediaDocument: " + getRealPath(IndividualChatActivity.this, audioUri));
//
//                                String path = null;
//                                String[] proj = {MediaStore.MediaColumns.DATA};
//
//                                Cursor cursor = getContentResolver().query(audioUri, proj, null, null, null);
//                                if (cursor.moveToFirst()) {
//                                    for (int i = 0; i < cursor.getColumnNames().length; i++) {
//                                        Log.d(TAG, "isMediaDocument: " + cursor.getColumnNames()[i]);
//
//                                    }
//                                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                                    path = cursor.getString(column_index);
//                                    Log.d(TAG, "isMediaDocument: " + path);
//
//                                }
//                                cursor.close();
//
//
//
//
//                                /*
//                                 * Get the file's content URI from the incoming Intent,
//                                 * then query the server app to get the file's display name
//                                 * and size.
//                                 */
//                                Uri returnUri = audioUri;
//                                Cursor returnCursor =
//                                        getContentResolver().query(returnUri, null, null, null, null);
//                                /*
//                                 * Get the column indexes of the data in the Cursor,
//                                 * move to the first row in the Cursor, get the data,
//                                 * and display it.
//                                 */
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                String[] filePathColumn = {MediaStore.Audio.Media.DATA};
//                                int dataIndex = returnCursor.getColumnIndex(filePathColumn[0]);
//                                returnCursor.moveToFirst();
//
//                                Log.d(TAG, "isMediaDocument: " + returnCursor.getString(nameIndex));
//                                Log.d(TAG, "isMediaDocument: " + Long.toString(returnCursor.getLong(sizeIndex)));
//                                for (int i = 0; i < returnCursor.getColumnNames().length; i++) {
//                                    Log.d(TAG, "isMediaDocument: " + returnCursor.getColumnNames()[i]);
//
//                                }
//
////                                String[] filePathColumn = {contentUri.toString()};
////                                Cursor cursor = IndividualChatActivity.this.getContentResolver().query(audioUri, filePathColumn, null, null, null);
////                                cursor.moveToFirst();
////                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                                String filePath = cursor.getString(columnIndex);
////                                cursor.close();
////                                Log.d(TAG, filePath);
////                                mensajeNube.setContenido(filePath);
////                                String audioPath = getDataColumn(IndividualChatActivity.this, contentUri, selection, selectionArgs);
////                                mensajeNube.setContenido(audioPath);
////                                Log.d(TAG, audioPath);
//                            } catch (Exception e) {
//                                Log.d(TAG, e.toString());
//                            }


                    } else {
                        try {
                            Log.d(TAG, "NOOOOOO isMediaDocument");
                            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                            Cursor cursor = CrazyIndividualChatActivity.this.getContentResolver().query(audioUri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            cursor.close();
                            Log.d(TAG, filePath);
                            mensajeNube.setContenido(filePath);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }


                }

//                    String[] filePathColumn = {MediaStore.Audio.Media.DATA};


//            mensajeNube.setContenido("/storage/emulated/0/Download/Billy-Ocean-Caribbean-Queen-No-More-Love-On-The-Run.mp3");
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioRemoto.getIdUsuario());
                mensajeNube.setEstadoLectura(false);

                Uri uri = audioUri;
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(getApplicationContext(), uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                mensajeNube.setAudioDuration(durationStr);
                mensajeNube.setType(2);/*0 audio */

                Log.d(TAG, mensajeNube.toString());

//                if (chat == null) {
//                    String idChat = FirebaseDatabase.getInstance().getReference()
//                            .child("chats")
//                            .push().getKey();
//                    chat = new Chat();
//                    chat.setIdChat(idChat);
//                    chat.setMensajeNube(mensajeNube);
//
//                    Participante participante1 = new Participante();
//                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//
//                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
//
//                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                    Participante participante2 = new Participante();
//                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
//                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//
//                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                    participanteArrayList.add(participante1);
//                    participanteArrayList.add(participante2);
//
//                    chat.setParticipantes(participanteArrayList);
//                    mensajeNube.setIdChat(chat.getIdChat());
//                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);
//
//                } else {
//                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
//                }


            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }


        }
        if (requestCode == SELECCIONAR_FOTO_GALERIA_REQ_ID && resultCode == RESULT_OK) {

            //Toast.makeText(getApplicationContext(), "Enviando imagen dedse galeria", Toast.LENGTH_LONG).show();
            try {
                final Uri imageUri = data.getData();
                Log.d(TAG, imageUri.toString());

                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                mensajeNube.setContenido(getRealPath(IndividualChatActivity.this, imageUri));
                mensajeNube.setContenido(imageUri.toString());
//            mensajeNube.setContenido("/storage/emulated/0/Download/Billy-Ocean-Caribbean-Queen-No-More-Love-On-The-Run.mp3");
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioRemoto.getIdUsuario());
                mensajeNube.setEstadoLectura(false);
                mensajeNube.setType(1);/*1 imagen */


                //                        Toast.makeText(getApplicationContext(), usuarioLocal.toString(), Toast.LENGTH_LONG).show();
                MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                messageCloudPoc.setContenido(imageUri.toString());
                messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                messageCloudPoc.setTo(usuarioRemoto.getIdUsuario());
                messageCloudPoc.setEstadoLectura(false);
                messageCloudPoc.setType(1);/*0 texto */
                Log.d(TAG, messageCloudPoc.toString());

                String title = "Por favor espere";
                String message = "Cargando imagen...";
                showCustomProgressDialog(title, message);
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


                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


                String fileExtensionImage = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString());
                String mimeTypeImage = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionImage);
                StorageMetadata storageMetadata = new StorageMetadata.Builder()
                        .setContentType(mimeTypeImage)
                        .build();
//
//                Toast.makeText(activity, mimeType, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, fileExtension, Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, mensajeNube.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, chat.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(activity, selectedUri.toString(), Toast.LENGTH_LONG).show();


                String baseReference = "gs://tfinal2022-afc91.appspot.com";
//        String imagePath = baseReference + "/" + "mensajes" + "/" + chat.getIdChat() + "/" + mensajeNube.getIdMensaje() + "." + fileExtensionImage;
                String imagePath = baseReference + "/" + "mensajes" + "/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key + "." + fileExtensionImage;
                Log.d(TAG, "Path reference on fireStorage");
                StorageReference storageRef = firebaseStorage.getReferenceFromUrl(imagePath);


//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()), storageMetadata);
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));
//                UploadTask uploadTask = storageRef.putFile(Uri.parse(mensajeNube.getContenido()));

                UploadTask uploadTask = storageRef.putFile(imageUri, storageMetadata);

//        if (mensajeNube.getContenido().contains("content:")) {
//            uploadTask = storageRef.putFile(uriImage, storageMetadata);
//
//        } else {
//            Uri imageUriSend = Uri.fromFile(new File(mensajeNube.getContenido()));
//            uploadTask = storageRef.putFile(imageUriSend, storageMetadata);
//
//        }


//                uploadTask = storageRef.putFile(imageUri, storageMetadata);
                // Listen for state changes, errors, and completion of the upload.
                StorageReference finalStorageRef = storageRef;
                uploadTask.addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    updateProgress(progress);
                    Log.d(TAG, "Upload is " + progress + "% done");

                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(TAG, "on failure Foto complete...");
                        closeAlertDialogLoad();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        // ...
                        Log.d(TAG, "Upload is complete...");
                        //  registroActivity.limpiarUI();
                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return finalStorageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
//                            Toast.makeText(activity, downloadUri.toString(), Toast.LENGTH_LONG).show();


                            //MessageCloudPoc post = new MessageCloudPoc();
                            messageCloudPoc.setContenido(downloadUri.toString());
                            messageCloudPoc.setType(1);
                            Map<String, Object> postValues = messageCloudPoc.toMap();

//                            Map<String, Object> childUpdates = new HashMap<>();
//                            childUpdates.put("/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
//                            childUpdates.put("/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
//                            FirebaseDatabase.getInstance().getReference().child("crazyMessages").updateChildren(childUpdates);


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

                            closeAlertDialogLoad();

                        } else {
                            // Handle failures

                        }
                    }
                });

                Log.d(TAG, mensajeNube.toString());

//
//                if (chat == null) {
//                    String idChat = FirebaseDatabase.getInstance().getReference()
//                            .child("chats")
//                            .push().getKey();
//                    chat = new Chat();
//                    chat.setIdChat(idChat);
//                    chat.setMensajeNube(mensajeNube);
//
//                    Participante participante1 = new Participante();
//                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
//
//                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                    Participante participante2 = new Participante();
//                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
//
//                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//
//                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                    participanteArrayList.add(participante1);
//                    participanteArrayList.add(participante2);
//
//                    chat.setParticipantes(participanteArrayList);
//                    mensajeNube.setIdChat(chat.getIdChat());
//                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);
//
//                } else {
//                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
//                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }


        }


        if (requestCode == REQUEST_CAMERA_PERMISSION_FOTO_PERFIL && resultCode == RESULT_OK) {
            //Toast.makeText(getApplicationContext(), "Enviando imagen capturada con CAM", Toast.LENGTH_LONG).show();


            try {
//                final Uri imageUri = data.getData();
                Uri uriImageToSend = uriPhoto;
                Intent intent = new Intent(getApplicationContext(), SendFotoActivity.class);
                intent.setData(uriImageToSend);
                intent.putExtra("usuarioRemoto", usuarioRemoto);
                startActivity(intent);
//                Glide.with(getApplicationContext()).load(uriPhoto).circleCrop().into(imageViewFoto);
//                Toast.makeText(getApplicationContext(),uriImageToSend.getPath(), Toast.LENGTH_SHORT).show();

                MensajeNube mensajeNube = new MensajeNube();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
//                mensajeNube.setContenido(getRealPath(IndividualChatActivity.this, uriImageToSend));
                mensajeNube.setContenido(uriImageToSend.toString());
//            mensajeNube.setContenido("/storage/emulated/0/Download/Billy-Ocean-Caribbean-Queen-No-More-Love-On-The-Run.mp3");
                mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mensajeNube.setTo(usuarioRemoto.getIdUsuario());
                mensajeNube.setEstadoLectura(false);
                mensajeNube.setType(1);/*1 imagen */


                Log.d(TAG, mensajeNube.toString());

//                if (chat == null) {
//                    String idChat = FirebaseDatabase.getInstance().getReference()
//                            .child("chats")
//                            .push().getKey();
//                    chat = new Chat();
//                    chat.setIdChat(idChat);
//                    chat.setMensajeNube(mensajeNube);
//
//                    Participante participante1 = new Participante();
//                    participante1.setIdParticipante(usuarioLocal.getIdUsuario());
//                    participante1.setNombreParticipante(usuarioLocal.getNombre() + " " + usuarioLocal.getApellido());
//                    participante1.setUriFotoParticipante(usuarioLocal.getFotoPerfil());
//
//                    Participante participante2 = new Participante();
//                    participante2.setIdParticipante(usuarioTo.getIdUsuario());
//                    participante2.setNombreParticipante(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//
//                    participante2.setUriFotoParticipante(usuarioTo.getFotoPerfil());
//
//                    ArrayList<Participante> participanteArrayList = new ArrayList<>();
//                    participanteArrayList.add(participante1);
//                    participanteArrayList.add(participante2);
//
//                    chat.setParticipantes(participanteArrayList);
//                    mensajeNube.setIdChat(chat.getIdChat());
//                    usuarioLocal.crearChat(chat, mensajeNube, IndividualChatActivity.this);
//
//                } else {
//                    usuarioLocal.enviarMensaje(chat, mensajeNube, IndividualChatActivity.this);
//                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            cleanMessagesPocListener();
        } catch (Exception e) {

        }

        try {
            deleteNotifications(usuarioRemoto.getIdUsuario());
        } catch (Exception e) {

        }

        setUsuarioBloqueado("");
    }
}