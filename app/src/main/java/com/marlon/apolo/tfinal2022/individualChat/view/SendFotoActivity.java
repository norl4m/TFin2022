package com.marlon.apolo.tfinal2022.individualChat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.individualChat.view.no.CamActivity;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendFotoActivity extends AppCompatActivity {

    private Usuario usuarioRemoto;
    private String TAG = SendFotoActivity.class.getSimpleName();
    private Uri uriImage;
    private AlertDialog alertDialogVar;
    private TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_foto);

        uriImage = getIntent().getData();
        usuarioRemoto = (Usuario) getIntent().getSerializableExtra("usuarioRemoto");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView imageView = findViewById(R.id.imageViewFoto);
        if (uriImage != null) {
            Glide.with(this).load(uriImage).into(imageView);
        }

        findViewById(R.id.fabSendFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
                //mensajeNube.setIdMensaje();
                //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
                messageCloudPoc.setContenido(uriImage.toString());
                messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                messageCloudPoc.setTo(usuarioRemoto.getIdUsuario());
                messageCloudPoc.setEstadoLectura(false);
                //messageCloudPoc.setType(0);/*0 texto */
                Log.d(TAG, messageCloudPoc.toString());
                sendMessage(messageCloudPoc);
            }
        });
    }

    // [START write_fan_out]
    private void sendMessage(MessageCloudPoc messageCloudPoc) {
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


        String fileExtensionImage = MimeTypeMap.getFileExtensionFromUrl(uriImage.toString());
        String mimeTypeImage = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionImage);
//        StorageMetadata storageMetadata = new StorageMetadata.Builder()
//                .setContentType(mimeTypeImage)
//                .build();

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uriImage));
        String mimetype = "image/" + type;

        Log.d(TAG, "MYMETYPE: " + mimeTypeImage);
        Log.d(TAG, "MYME: " + mime);
        Log.d(TAG, "TYPE: " + type);
        Log.d(TAG, "TYPE: " + mimetype);

        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType(mimetype)
                .build();
        messageCloudPoc.setMimeType(mimetype);
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

        UploadTask uploadTask = storageRef.putFile(uriImage, storageMetadata);

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
                    try {
//                        CamActivity.camActivity.finish();
                        CamActivity.camActivity.closeForce();
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                    finish();

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

    // [END write_fan_out]
}