package com.marlon.apolo.tfinal2022.registro.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.view.FotoActivity;

public class RegDatoPersonalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegDatoPersonalActivity.class.getSimpleName();
    private Button buttonNext;
    private Dialog dialogInfo;
    TextInputEditText textInputEditTextNombre;
    TextInputEditText textInputEditTextApellido;
    private String apellido;
    private String nombre;
    private int regUsuario;

    private static final int PERMISSION_REQUEST_CAMERA = 2000;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 2001;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 2003;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1001;
    private int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1000;
    private FloatingActionButton fabChooseImageProfile;
    private ImageView imageViewFoto;
    private Uri uriPhoto;
    private int colorPrimary;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;


    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.basic_bottom_sheet);

        LinearLayout copy = bottomSheetDialog.findViewById(R.id.takePhotoLinearLayout);
//        ((ImageView) findViewById(R.id.imvSelectCamera)).setColorFilter(colorPrimary);

        try {
            ImageView imageViewCamera = bottomSheetDialog.findViewById(R.id.imvSelectCamera);
            imageViewCamera.setColorFilter(colorPrimary);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        LinearLayout share = bottomSheetDialog.findViewById(R.id.galleryPhotoLinearLayout);
//        ((ImageView) findViewById(R.id.imvSelectGallery)).setColorFilter(colorPrimary);
        try {
            ImageView imageViewGallery = bottomSheetDialog.findViewById(R.id.imvSelectGallery);
            imageViewGallery.setColorFilter(colorPrimary);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        bottomSheetDialog.show();

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Copy is Clicked ", Toast.LENGTH_LONG).show();
                tomarfoto();

                bottomSheetDialog.dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Share is Clicked", Toast.LENGTH_LONG).show();
                escogerDesdeGaleria();

                bottomSheetDialog.dismiss();
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_reg_nombre_usuario);


        regUsuario = getIntent().getIntExtra("usuario", -1);

        nombre = "";
        apellido = "";

        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        findViewById(R.id.buttonInfo).setOnClickListener(this);
        fabChooseImageProfile = findViewById(R.id.fabSeleccionarFoto);
        findViewById(R.id.fabSeleccionarFoto).setOnClickListener(this);

        imageViewFoto = findViewById(R.id.imageViewProfile);
//        ImageView imageView = findViewById(R.id.imageViewProfile);
        findViewById(R.id.imageViewProfile).setOnClickListener(this);
//        Glide.with(this)
//                .load(R.drawable.ic_user)
////                .apply(new RequestOptions().override(150, 150))
//                .placeholder(R.drawable.ic_user)
////                .circleCrop()
//                .into(imageView);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mode = mPrefs.getBoolean("sync_theme", false);


        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorPrimary = typedValue.data;
        imageViewFoto.setColorFilter(colorPrimary);
        /*Esto es una maravilla*/


        buttonNext = findViewById(R.id.buttonNext);
        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linLytBack);

        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(this);


        getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int colorOnPrimary = typedValue.data;

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.card_background);
        drawable.setTint(colorOnPrimary);
        scrollView.setBackground(drawable);
//        linearLayout.setBackground(drawable);
        linearLayout.setBackgroundColor(colorOnPrimary);

        textInputEditTextNombre = findViewById(R.id.editTextNombre);
        textInputEditTextApellido = findViewById(R.id.editTextApellido);


        textInputEditTextNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nombre = s.toString();
                TextInputLayout tilNombre = (TextInputLayout) findViewById(R.id.textInputLayoutNombre);

                if (nombre.isEmpty()) {
                    tilNombre.setError("Error: su nombre debe contener al menos 4 letras");
                } else {
                    tilNombre.setError(null);
                }
                if (!nombre.isEmpty() && !apellido.isEmpty()) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputEditTextApellido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                apellido = s.toString();
                TextInputLayout tilApellido = (TextInputLayout) findViewById(R.id.textInputLayoutApellido);
                if (apellido.isEmpty()) {
                    tilApellido.setError("Error: su apellido debe contener al menos 4 letras");
                } else {
                    tilApellido.setError(null);
                }

                if (!nombre.isEmpty() && !apellido.isEmpty()) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
//        try {
//            if (v.getId() == R.id.buttonInfo) {
//                //Toast.makeText(getApplicationContext(), "Info", Toast.LENGTH_LONG).show();
//                dialogInfo = alertDialogInfo();
//                dialogInfo.show();
//            }
//        } catch (Exception e) {
//
//        }
        try {
            if (v.getId() == R.id.imageViewProfile) {
                if (uriPhoto != null) {
                    Intent intentFoto = new Intent(this, FotoActivity.class);
                    intentFoto.setData(uriPhoto);
                    this.startActivity(intentFoto);
                }
            }
        } catch (Exception e) {

        }
        try {
            if (v.getId() == R.id.fabSeleccionarFoto) {

//                showBottomSheetDialog();
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
            }
        } catch (Exception e) {

        }
        try {
            if (v.getId() == R.id.buttonNext) {
//                Toast.makeText(getApplicationContext(), "Siguiente", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(RegNombreUsuarioActivity.this, MetodoRegActivity.class);
//                Intent intent = new Intent(RegNombreUsuarioActivity.this, RegFotoPerfilActivity.class);
//                Intent intent = new Intent(RegNombreUsuarioActivity.this, MetodoRegActivity.class);
                Intent intent = new Intent(RegDatoPersonalActivity.this, RegWithEmailPasswordActivity.class);

                switch (regUsuario) {
                    case 1:
                        editorPref = myPreferences.edit();
                        int checkAdmin = myPreferences.getInt("usuario", -1);

                        if (checkAdmin == 0) {
                            intent = new Intent(RegDatoPersonalActivity.this, RegWithEmailPasswordActivityAdmin.class);
                        }


                        Empleador empleador = new Empleador();
                        empleador.setNombre(nombre);
                        empleador.setApellido(apellido);
                        try {
                            if (!uriPhoto.toString().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();
                                empleador.setFotoPerfil(uriPhoto.toString());
                            }
                        } catch (Exception e) {

                        }
                        intent.putExtra("usuario", regUsuario);
                        intent.putExtra("empleador", empleador);
                        break;
                    case 2:
                        intent = new Intent(RegDatoPersonalActivity.this, RegistroRecordPolicialActivity.class);
                        Trabajador trabajador = new Trabajador();
                        trabajador.setNombre(nombre);
                        trabajador.setApellido(apellido);
                        try {
                            if (!uriPhoto.toString().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();
                                trabajador.setFotoPerfil(uriPhoto.toString());
                            }
                        } catch (Exception e) {

                        }

                        intent.putExtra("usuario", regUsuario);
                        intent.putExtra("trabajador", trabajador);
                        break;
                }
                startActivity(intent);

            }
        } catch (Exception e) {

        }
    }

    public Dialog alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);

        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(getResources().getString(R.string.text_info_nombre_usuario));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_info, null))
        // Add action buttons
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
        return builder.create();
    }


    private void openAlertDialogPhotoOptions() {
//        // setup the alert builder
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//        builder.setTitle("Completar acción mediante:");
//
//// add a list
//        String[] animals = {"Galería de imágenes", "Tomar foto"};
//        builder.setItems(animals, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0: // horse
////                        escogerDesdeGaleria();
//                        break;
//                    case 1: // cow
////                        tomarfoto();
//                        break;
//                }
//            }
//        });
//
//// create and show the alert dialog
//        android.app.AlertDialog dialog = builder.create();
//        dialog.show();

        showBottomSheetDialog();
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
                    ActivityCompat.requestPermissions(RegDatoPersonalActivity.this,
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
                    ActivityCompat.requestPermissions(RegDatoPersonalActivity.this,
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
                    ActivityCompat.requestPermissions(RegDatoPersonalActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_LOCA);
                }
            }).show();

        } else {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("");
                builder.setMessage(R.string.permiso_camera_text_data_foto);
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
                Snackbar.make(fabChooseImageProfile, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
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

                imageViewFoto.setColorFilter(null);

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
                imageViewFoto.setColorFilter(null);

                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
            }
        }
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

}