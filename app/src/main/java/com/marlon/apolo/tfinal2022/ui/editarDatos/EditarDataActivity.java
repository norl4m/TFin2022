package com.marlon.apolo.tfinal2022.ui.editarDatos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.view.EditarOficioHabilidad2Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EditarDataActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditarDataActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CAMERA = 2000;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 2001;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 2003;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1001;
    private int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1000;

    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutLastName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutCelular;
    private TextInputLayout textInputLayoutPassword;
    private FloatingActionButton fabChooseImageProfile;
    private Button buttonUpdate;
    private ImageView imageViewFoto;
    private Usuario usuarioEdt;
    private Uri uriPhoto;
    private String mediaPath;
    private File cameraImage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uriPhoto = null;

        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutLastName = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutCelular = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        textInputLayoutEmail.setVisibility(View.GONE);
        textInputLayoutCelular.setVisibility(View.GONE);
        textInputLayoutPassword.setVisibility(View.GONE);

        textInputLayoutEmail.setEnabled(false);
        textInputLayoutCelular.setEnabled(false);
        textInputLayoutPassword.setEnabled(false);

        imageViewFoto = findViewById(R.id.imageViewProfile);
        fabChooseImageProfile = findViewById(R.id.fabSeleccionarFoto);

        buttonUpdate.setOnClickListener(this);
        fabChooseImageProfile.setOnClickListener(this);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                usuarioEdt = new Empleador();
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                usuarioEdt = empleador;
                //Toast.makeText(getApplicationContext(), empleador.toString(), Toast.LENGTH_LONG).show();
                break;
            case 2:
                usuarioEdt = new Trabajador();
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                usuarioEdt = trabajador;
                //Toast.makeText(getApplicationContext(), trabajador.toString(), Toast.LENGTH_LONG).show();
                break;
        }

        loadInfo(usuarioEdt);


    }

    private void loadInfo(Usuario usuarioEdt) {
        textInputLayoutName.getEditText().setText(usuarioEdt.getNombre());
        textInputLayoutLastName.getEditText().setText(usuarioEdt.getApellido());
        if (usuarioEdt.getEmail() != null) {
            textInputLayoutEmail.getEditText().setText(usuarioEdt.getEmail());
            textInputLayoutEmail.setVisibility(View.VISIBLE);

        }
        if (usuarioEdt.getCelular() != null) {
            textInputLayoutCelular.getEditText().setText(usuarioEdt.getCelular());
            textInputLayoutCelular.setVisibility(View.VISIBLE);
        }
        if (usuarioEdt.getFotoPerfil() != null) {
            Glide.with(EditarDataActivity.this)
                    .load(usuarioEdt.getFotoPerfil())
                    .circleCrop().placeholder(R.drawable.ic_baseline_person_24)
                    .into(imageViewFoto);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdate:
                //Toast.makeText(this, "Update", Toast.LENGTH_LONG).show();
                String name = textInputLayoutName.getEditText().getText().toString();
                String lastName = textInputLayoutLastName.getEditText().getText().toString();
                //String email = textInputLayoutName.getEditText().getText().toString();
                //String celular = textInputLayoutName.getEditText().getText().toString();

                boolean photoFlag = false;
//                if (uriPhoto != null) {
//                    Uri returnUri = uriPhoto;
////                    Cursor returnCursor = EditarDataActivity.this.getContentResolver().query(returnUri, null, null, null, null);
//                    Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
//                    ;
//                    /*
//                     * Get the column indexes of the data in the Cursor,
//                     * move to the first row in the Cursor, get the data,
//                     * and display it.
//                     */
//                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                    returnCursor.moveToFirst();
//                    Log.d(TAG, returnCursor.getString(nameIndex));
//                    Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//
//                    if (returnCursor.getLong(sizeIndex) > 0) {
//                        //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//                        photoFlag = true;
//                    } else {
//                        //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
//                        photoFlag = false;
//                    }
//
//                    Log.d(TAG, String.format("Version de SDK del dispositivo: %d", Build.VERSION.SDK_INT));
//                    String mimeType = getContentResolver().getType(returnUri);
//                    Log.d(TAG, String.format("Mime type archivo: %s", mimeType));
//
//
//
//                    /*
//                     * Get the file's content URI from the incoming Intent,
//                     * then query the server app to get the file's display name
//                     * and size.
//                     */
////                    Uri returnUri = returnIntent.getData();
////                    Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
//                    /*
//                     * Get the column indexes of the data in the Cursor,
//                     * move to the first row in the Cursor, get the data,
//                     * and display it.
//                     */
//
//                    Log.d(TAG, returnCursor.getString(nameIndex));
//                    Log.d(TAG, Long.toString(returnCursor.getLong(sizeIndex)));
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                    } else {
//
//                    }
//
//                } else {
//                    photoFlag = false;
//                }
//
//                if (photoFlag) {
//                    usuarioEdt.setFotoPerfil(uriPhoto.toString());
//                }


                InputStream iStream = null;

                if (!name.isEmpty() && !lastName.isEmpty()) {
                    switch (regUsuario) {
                        case 1:
                            empleador.setNombre(name);
                            empleador.setApellido(lastName);
                            empleador.setFotoPerfil(usuarioEdt.getFotoPerfil());
//                            Uri returnUri = uriPhoto;
//                                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                            /*
                             * Get the column indexes of the data in the Cursor,
                             * move to the first row in the Cursor, get the data,
                             * and display it.
                             */
                            iStream = null;
                            try {
                                iStream = getContentResolver().openInputStream(uriPhoto);
                                byte[] inputData = getBytes(iStream);
                                Log.d(TAG, String.format("Bytes: %d", inputData.length));
                                if (inputData.length > 0) {
                                    photoFlag = true;
                                } else {
                                    photoFlag = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                photoFlag = false;
                            }
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                returnCursor.moveToFirst();
//
//                                Log.d(TAG, returnCursor.getString(nameIndex));
//                                Log.d(TAG, Long.toString(returnCursor.getLong(sizeIndex)));
                            String title = "Actualizando información";
                            String messsage = "Por favor espere...";
                            showProgress(title, messsage);
                            if (photoFlag) {
                                empleador.actualizarInfoConFoto(this, uriPhoto);
                            } else {
                                empleador.actualizarInfo(this);

                            }

                            break;
                        case 2:
                            String title1 = "Actualizando información";
                            String messsage1 = "Por favor espere...";
                            showProgress(title1, messsage1);
                            trabajador.setNombre(name);
                            trabajador.setApellido(lastName);
                            trabajador.setFotoPerfil(usuarioEdt.getFotoPerfil());

                            iStream = null;
                            try {
                                iStream = getContentResolver().openInputStream(uriPhoto);
                                byte[] inputData = getBytes(iStream);
                                Log.d(TAG, String.format("Bytes: %d", inputData.length));
                                if (inputData.length > 0) {
                                    photoFlag = true;
                                } else {
                                    photoFlag = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                photoFlag = false;
                            }

                            if (photoFlag) {
                                trabajador.actualizarInfoConFoto(this, uriPhoto);
                            } else {
                                trabajador.actualizarInfo(this);

                            }


                            break;
                    }
                }
                break;
            case R.id.fabSeleccionarFoto:
                //  Toast.makeText(this, "Seleccionar Foto", Toast.LENGTH_LONG).show();


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
        }
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


                // Add a specific media item.
                ContentResolver resolverImage = getApplicationContext()
                        .getContentResolver();

// Find all audio files on the primary external storage device.
                Uri imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imageUri = MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                } else {
                    imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

// Publish a new song.
                ContentValues newImageDetails = new ContentValues();
                newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + "image.jpeg");
                newImageDetails.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis());
                newImageDetails.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());

// Keeps a handle to the new song's URI in case we need to modify it
// later.
                Uri myImageUri = resolverImage
                        .insert(imageUri, newImageDetails);

                uriPhoto = myImageUri;

                Log.d("FotoUdir asda", uriPhoto.toString());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myImageUri);
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
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(EditarDataActivity.this,
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

    private void requestCameraPermission() {
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
                    ActivityCompat.requestPermissions(EditarDataActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_ONLY);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
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
            Snackbar.make(fabChooseImageProfile, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(EditarDataActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_LOCA);
                }
            }).show();

        } else {
            Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
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
                    Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
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
                Glide.with(getApplicationContext()).load(uriPhoto).circleCrop().into(imageViewFoto);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();

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
                Glide.with(getApplicationContext()).load(uriPhoto).circleCrop().into(imageViewFoto);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editar_oficio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_edit_oficio:
                //Toast.makeText(this, "Edit oficios", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(EditarDataActivity.this, EditarOficioActivity.class);
//                Intent intent = new Intent(EditarDataActivity.this, EditarOficioHabilidadActivity.class);
                Intent intent = new Intent(EditarDataActivity.this, EditarOficioHabilidad2Activity.class);
                intent.putExtra("trabajador", trabajador);
                startActivity(intent);
                break;
            case R.id.mnu_edit_habilidad:
//                Intent intent = new Intent(EditarDataActivity.this,EditarOficioActivity.class);
//                intent.putExtra("trabajador",trabajador);
//                startActivity(intent);
                Toast.makeText(this, "Edit habilidades", Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItemO = menu.findItem(R.id.mnu_edit_oficio);
        // MenuItem menuItemH = menu.findItem(R.id.mnu_edit_habilidad);
        switch (regUsuario) {
            case 1:
                menuItemO.setVisible(false);
                //  menuItemH.setVisible(false);
                break;
            case 2:
                menuItemO.setVisible(true);
                // menuItemH.setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }


    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(EditarDataActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void closeProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

}