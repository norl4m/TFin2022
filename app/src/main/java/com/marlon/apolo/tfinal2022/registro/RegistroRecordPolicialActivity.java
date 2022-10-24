package com.marlon.apolo.tfinal2022.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.text.Text;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.FirebaseMLKitVision;
import com.marlon.apolo.tfinal2022.interfaces.DataStatusMLKit;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.PoliceRecord;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.io.InputStream;
import java.util.ArrayList;

public class RegistroRecordPolicialActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroRecordPolicialActivity.class.getSimpleName();
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private Dialog dialogInfo;

    private Button buttonNext;
    private Button buttonRecord;

    private static final int PERMISSION_REQUEST_CAMERA = 2000;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 2001;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 2003;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1001;
    private int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1000;

    private Uri uriPhoto;

    private ImageButton imageButtonFotoRecord;
    private PoliceRecord policeRecord;
    private Dialog dialogRecordPolicial;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_record_policial);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        findViewById(R.id.buttonNext).setOnClickListener(this);


        imageButtonFotoRecord = findViewById(R.id.imageButtonRecordPolicial);
        buttonNext = findViewById(R.id.buttonNext);
        buttonRecord = findViewById(R.id.buttonRecordPolicial);

        findViewById(R.id.buttonInfo).setOnClickListener(this);
        buttonRecord.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        imageButtonFotoRecord.setOnClickListener(this);

        buttonNext.setEnabled(false);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                trabajador.setEstadoRrcordP(true);
                //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:
                Intent intent = new Intent(RegistroRecordPolicialActivity.this, RegistroOficioActivity.class);
//                Intent intent = new Intent(RegistroRecordPolicialActivity.this, RegistroOficioActivityPoc.class);
                switch (regUsuario) {
                    case 1:/*empleador*/

                        intent.putExtra("usuario", regUsuario);
                        intent.putExtra("empleador", empleador);
                        break;
                    case 2:/*trabajador*/

                        intent.putExtra("usuario", regUsuario);
                        intent.putExtra("trabajador", trabajador);
                        break;
                }
                startActivity(intent);
                break;
            case R.id.buttonInfo:
                alertDialogInfo();
                break;
            case R.id.buttonRecordPolicial:
//                Toast.makeText(getApplicationContext(), "Seleccioar record policial", Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.imageButtonRecordPolicial:
                try {
                    if (policeRecord != null) {
                        if (!policeRecord.isStatusCriminalRecord()) {
                            alertDialogRecordPolicial(policeRecord);
                        } else {
                            Toast.makeText(getApplicationContext(), "Record policial inválido", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No se ha registrado ningún record policial", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Por favor ingrese su record policial nuevamente", Toast.LENGTH_LONG).show();
                    }
                    /*if (!policeRecord.isStatusCriminalRecord()){
                        showPoliceRecordInfo();
                    }*/
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error en la lectura de datos", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Por favor ingrese su record policial nuevamente", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
//
//        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.record_policial_info));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialogInfo.dismiss();
                } catch (Exception e) {

                }
            }
        });

        dialogInfo = builder.create();
        dialogInfo.show();
    }


    private void openAlertDialogPhotoOptions() {
        // setup the alert builder
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Completar acción mediante:");

// add a list
        String[] animals = {"Galería de imágenes", "Tomar foto"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // horse
                        escogerDesdeGaleria();
                        break;
                    case 1: // cow
                        tomarfoto();
                        break;
                }
            }
        });

// create and show the alert dialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void escogerDesdeGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }

    private void tomarfoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


                ContentResolver resolver = getApplicationContext()
                        .getContentResolver();

                Uri audioCollection;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    audioCollection = MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
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

    private void selectPhoto() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openAlertDialogPhotoOptions();
        } else {
            // Permission is missing and must be requested.
            requestCameraAndWExtStPermission();
        }
        // END_INCLUDE(startCamera)
    }


    private void requestCameraAndWExtStPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(buttonNext, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(RegistroRecordPolicialActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(buttonNext, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(buttonNext, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(RegistroRecordPolicialActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_ONLY);
                }
            }).show();

        } else {
            Snackbar.make(buttonNext, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
        }
    }

    private void requestCameraAPILocaPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(buttonNext, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(RegistroRecordPolicialActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_LOCA);
                }
            }).show();

        } else {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("");
                builder.setMessage(R.string.permiso_camera_text_data_record);
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
                Snackbar.make(buttonNext, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
                // Request the permission. The result will be received in onRequestPermissionResult().
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);


            }
        }
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
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    Snackbar.make(buttonNext, R.string.camera_permission_denied,
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case PERMISSION_REQUEST_CAMERA_ONLY:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/

                    Snackbar.make(buttonNext, R.string.camera_permission_denied,
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case PERMISSION_REQUEST_CAMERA_LOCA:
                // Request for camera permission.
                if (grantResults.length >= 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                    Snackbar.make(buttonNext, R.string.camera_permission_denied,
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
        }

        // END_INCLUDE(onRequestPermissionsResult)
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CAMERA_PERMISSION_FOTO_PERFIL && resultCode == RESULT_OK) {

            try {
//                final Uri imageUri = data.getData();
                Uri uriImageToSend = uriPhoto;
                Glide.with(getApplicationContext()).load(uriPhoto).into(imageButtonFotoRecord);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();
                InputStream imageStream = getContentResolver().openInputStream(uriPhoto);
                final Bitmap recordPolicialImg = BitmapFactory.decodeStream(imageStream);
                procesarRecordPolicial(recordPolicialImg);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == SELECCIONAR_FOTO_GALERIA_REQ_ID && resultCode == RESULT_OK) {

            try {
                Log.e(TAG, "Seleccionando imagen...");
                final Uri imageUri = data.getData();
                uriPhoto = imageUri;
                Glide.with(getApplicationContext()).load(uriPhoto).into(imageButtonFotoRecord);
                InputStream imageStream = getContentResolver().openInputStream(uriPhoto);
                final Bitmap recordPolicialImg = BitmapFactory.decodeStream(imageStream);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();
                procesarRecordPolicial(recordPolicialImg);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void procesarRecordPolicial(Bitmap recordPolicialBitmap) {
//        progressDialog = getDialogProgressBar().create();
//        progressDialog.show();
        String title = getString(R.string.proc_record_policial);
        String message = "Por favor espere...";
        showProgress(title, message);

        FirebaseMLKitVision firebaseMLKitVision = new FirebaseMLKitVision();
        firebaseMLKitVision.recognizeText(
                firebaseMLKitVision.imageFromBitmap(recordPolicialBitmap),
                new DataStatusMLKit() {
                    @Override
                    public void readTextRecognized(Text textRecognized) {
                        ArrayList<String> data = firebaseMLKitVision.processTextRecognized(textRecognized);
                        policeRecord = firebaseMLKitVision.convertTextRecognizedToPoliceReport(data);
                        if (policeRecord.getCi().equals("No detectado") || policeRecord.getCertificateNumber().equals("No detectado")
                                || policeRecord.getDateCreation().equals("No detectado") || policeRecord.getTypeDocument().equals("No detectado")
                                || policeRecord.getNameAndLastName().equals("No detectado"))
                            policeRecord.setStatusCriminalRecord(true);
                        {
                            alertDialogRecordPolicial(policeRecord);
                        }
                    }
                });
    }

    public void alertDialogRecordPolicial(PoliceRecord policeRecord) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
        dialogRecordPolicial = new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle(R.string.record_ingresado)
                .setMessage(policeRecord.toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (policeRecord.isStatusCriminalRecord()) {
                            Toast.makeText(RegistroRecordPolicialActivity.this, "Récord policial inválido!", Toast.LENGTH_SHORT).show();
                            trabajador.setEstadoRrcordP(true);
                            buttonNext.setEnabled(false);
                        } else {
                            Toast.makeText(RegistroRecordPolicialActivity.this, "Verificación existosa!", Toast.LENGTH_SHORT).show();
                            trabajador.setEstadoRrcordP(false);
                            buttonNext.setEnabled(true);
                            buttonRecord.setEnabled(false);
                            buttonRecord.setVisibility(View.GONE);
                        }
                        try {
                            dialogRecordPolicial.dismiss();
                        } catch (Exception e) {

                        }
                    }
                })
                .setCancelable(false)
                .create();
        dialogRecordPolicial.show();
    }

    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
//        dialog.setTitle("Por favor espere");
        progressDialog.setTitle(title);
//        dialog.setMessage("Trabix se encuentra verificando su nùmero celular...");
        progressDialog.setMessage(message);
        progressDialog.show();

    }


}