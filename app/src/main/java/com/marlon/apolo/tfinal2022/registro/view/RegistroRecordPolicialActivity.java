package com.marlon.apolo.tfinal2022.registro.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.text.Text;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.FirebaseMLKitVision;
import com.marlon.apolo.tfinal2022.interfaces.DataStatusMLKit;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.PoliceRecord;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class RegistroRecordPolicialActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistroRecordPolicialActivity.class.getSimpleName();
    private int regUsuario;
    private Empleador empleador;
    private Trabajador trabajador;
    private Dialog dialogInfo;

    private Button buttonNext;
//    private Button buttonRecord;

    private static final int PERMISSION_REQUEST_CAMERA = 2000;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 2001;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 2003;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1001;
    private int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1000;
    private static final int SELECCIONAR_PDF_REQ_ID = 1003;


    private Uri uriPhoto;

    private ImageButton imageButtonFotoRecord;
    private PoliceRecord policeRecord;
    private Dialog dialogRecordPolicial;
    private ProgressDialog progressDialog;

    private int selectMethod;
    private int colorTheme;
    private ImageView imageViewCam;
    private ImageView imageViewGallery;
    private ImageView imageViewPdf;

    private void extractPDF(String filename, InputStream inputStream) {
        try {
            // creating a string for
            // storing our extracted text.
            String extractedText = "";

            // creating a variable for pdf reader
            // and passing our PDF file in it.
//            filename = "res/raw/cert_ant_penales_1722162698.pdf";
//            File f = new File(filename);
//            Log.d(TAG, f.getPath());
//            Log.d(TAG, f.getAbsolutePath());
//            Log.d(TAG, f.getName());

//            PdfReader reader = new PdfReader(filename);
            PdfReader reader = new PdfReader(inputStream);

            // below line is for getting number
            // of pages of PDF file.
            int n = reader.getNumberOfPages();

            // running a for loop to get the data from PDF
            // we are storing that data inside our string.
            for (int i = 0; i < n; i++) {
                String line = PdfTextExtractor.getTextFromPage(reader, i + 1).trim();
                Log.d(TAG, String.valueOf(i) + " : " + line);
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                // to extract the PDF content from the different pages
            }

            // after extracting all the data we are
            // setting that string value to our text view.
//            extractedTV.setText(extractedText);
//            Toast.makeText(getApplicationContext(), extractedText, Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Validando PDF...", Toast.LENGTH_LONG).show();
            Log.d(TAG, extractedText);

            alertDialogExtractedText(extractedText);
            // below line is used for closing reader.
            reader.close();
        } catch (Exception e) {
            // for handling error while extracting the text file.
//            extractedTV.setText("Error found is : \n" + e);
            Log.e(TAG, e.toString());
        }
    }

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        WindowInsetsControllerCompat windowInsetsController =
//                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
//        if (windowInsetsController == null) {
//            return;
//        }
//        // Configure the behavior of the hidden system bars
//        windowInsetsController.setSystemBarsBehavior(
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        );
//        // Hide both the status bar and the navigation bar
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registro_record_policial);
        hideSystemBars();
        setContentView(R.layout.activity_registro_record_policial_poc);

        selectMethod = -1;

        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorTheme = typedValue.data;
        /*Esto es una maravilla*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        findViewById(R.id.buttonNext).setOnClickListener(this);

//
//        imageButtonFotoRecord = findViewById(R.id.imageButtonRecordPolicial);
        buttonNext = findViewById(R.id.buttonNext);
//        buttonRecord = findViewById(R.id.buttonRecordPolicial);
//
//        findViewById(R.id.buttonInfo).setOnClickListener(this);
        findViewById(R.id.cardViewCamera).setOnClickListener(this);
        imageViewCam = findViewById(R.id.imgViewCamera);
        imageViewCam.setColorFilter(colorTheme);

        findViewById(R.id.cardViewGallery).setOnClickListener(this);
        imageViewGallery = findViewById(R.id.imgViewGallery);
        imageViewGallery.setColorFilter(colorTheme);

        findViewById(R.id.cardViewPdf).setOnClickListener(this);
        imageViewPdf = findViewById(R.id.imgViewPdf);
        imageViewPdf.setColorFilter(colorTheme);
//        buttonRecord.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
//        imageButtonFotoRecord.setOnClickListener(this);
//
        buttonNext.setEnabled(false);

        regUsuario = getIntent().getIntExtra("usuario", -1);

        switch (regUsuario) {
            case 1:
                empleador = (Empleador) getIntent().getSerializableExtra("empleador");
                break;
            case 2:
                trabajador = (Trabajador) getIntent().getSerializableExtra("trabajador");
                trabajador.setEstadoRrcordP(true);/*Config inicial*/
                /*Asumo que el trabajador tiene antecedentes para solo pasar en el caso de que:
                El record policial ingresado sea valido
                Y la detección de los antecedentes en el mismo sean válidos y la respuesta detectada sea: NO
                */
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
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                    openAlertDialogPhotoOptions();
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
////                    selectPhoto();
//                    // BEGIN_INCLUDE(startCamera)
//                    // Check if the Camera permission has been granted
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                            == PackageManager.PERMISSION_GRANTED
//                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_GRANTED) {
//                        openAlertDialogPhotoOptions();
//                    } else {
//                        // Permission is missing and must be requested.
//                        requestCameraAndWExtStPermission();
//                    }
//                    // END_INCLUDE(startCamera)
//                }
//
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        // Permission is already available
//                        openAlertDialogPhotoOptions();
//                    } else {
//                        // Permission is missing and must be requested.
//                        requestCameraPermission();
//                    }
//                }
//
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        // Permission is already available
//                        openAlertDialogPhotoOptions();
//                    } else {
//                        // Permission is missing and must be requested.
//                        requestCameraAPILocaPermission();
//                    }
//                }
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

            case R.id.cardViewCamera:
                selectMethod = 1;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    tomarfoto();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    selectPhoto();
                    // BEGIN_INCLUDE(startCamera)
                    // Check if the Camera permission has been granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        tomarfoto();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraAndWExtStPermission();
                    }
                    // END_INCLUDE(startCamera)
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        tomarfoto();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraPermission();
                    }
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        tomarfoto();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraAPILocaPermission();
                    }
                }

                break;
            case R.id.cardViewGallery:
                selectMethod = 2;
                escogerDesdeGaleria();
                break;
            case R.id.cardViewPdf:
//                Toast.makeText(getApplicationContext(), "PDF", Toast.LENGTH_LONG).show();
                selectMethod = 3;
//                String filenamePdf = "storage/emulated/0/Download/cert_ant_penales_1719099127.pdf";
//                try {
//                    extractPDF(filenamePdf);
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
                /*Almacenamiento interno*/
                File internoFileDir = getFilesDir();
                File internoCacheDir = getCacheDir();
                /*Almacenamiento interno*/
                File externoFileDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File externoCacheDir = getExternalCacheDir();


                escogerPDF();
                break;
        }
    }

    public int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    private void blockingAllButtons() {
        imageViewCam.setEnabled(false);
        imageViewGallery.setEnabled(false);
        imageViewPdf.setEnabled(false);

        findViewById(R.id.cardViewCamera).setEnabled(false);
        findViewById(R.id.cardViewGallery).setEnabled(false);
        findViewById(R.id.cardViewPdf).setEnabled(false);

        switch (selectMethod) {
            case 1:
                imageViewCam.setEnabled(true);
                findViewById(R.id.cardViewCamera).setEnabled(true);
                break;
            case 2:
                imageViewGallery.setEnabled(true);
                findViewById(R.id.cardViewGallery).setEnabled(true);
                break;
            case 3:
                imageViewPdf.setEnabled(true);
                findViewById(R.id.cardViewPdf).setEnabled(true);
                break;
        }

    }

    private void removeBlockingAllButtons() {
        imageViewCam.setEnabled(true);
        imageViewGallery.setEnabled(true);
        imageViewPdf.setEnabled(true);

        findViewById(R.id.cardViewCamera).setEnabled(true);
        findViewById(R.id.cardViewGallery).setEnabled(true);
        findViewById(R.id.cardViewPdf).setEnabled(true);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void alertDialogExtractedText(String extractedText) {
        Log.d(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        Log.d(TAG, String.valueOf(countLines(extractedText)));
        String[] lines = extractedText.split("\\n");
        String line1 = lines[0];
        String line2 = lines[1];
        String line3 = lines[2];
        String line4 = lines[3];
        String line5 = lines[4];
        String line6 = lines[5];
//
//        String line1Veri = "Fecha de emisión: " + lines[0];
//        String line2Veri = "Número de certificado: " + lines[1];
//        String line3Veri = "Tipo de Documento: " + lines[2];
//        String line4Veri = "No. de Identificación: " + lines[3];
//        String line5Veri = "Apellidos y Nombres: " + lines[4];
//        String line6Veri = "Registra Antecedentes: " + lines[5];

        PoliceRecord policeRecordFromPdf = new PoliceRecord();
        policeRecordFromPdf.setDateCreation(line1);
        policeRecordFromPdf.setCertificateNumber(line2);
        policeRecordFromPdf.setTypeDocument(line3);
        policeRecordFromPdf.setCi(line4);
        policeRecordFromPdf.setNameAndLastName(line5);
        policeRecordFromPdf.setCriminalRecordStat(line6);

        if (line6.equals("NO")) {
//            Toast.makeText(RegistroRecordPolicialActivity.this, "Verificación existosa!", Toast.LENGTH_SHORT).show();
            trabajador.setEstadoRrcordP(false);
            policeRecordFromPdf.setStatusCriminalRecord(false);
            buttonNext.setEnabled(true);

            try {
                hideKeyboard(this);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            alertDialogRecordPolicial(policeRecordFromPdf);
//            blockingAllButtons();


        } else {
            Toast.makeText(RegistroRecordPolicialActivity.this, "El record policial ingresado es inválido!", Toast.LENGTH_SHORT).show();
            trabajador.setEstadoRrcordP(true);
            policeRecordFromPdf.setStatusCriminalRecord(true);
            buttonNext.setEnabled(false);

            try {
                hideKeyboard(this);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

//                            buttonRecord.setEnabled(false);
            //buttonRecord.setVisibility(View.GONE);


        }


//        RecordPolicialEc recordPolicialEc = new RecordPolicialEc();
//        recordPolicialEc.setFechaEmision(line1);
//        recordPolicialEc.setNumCert(line2);
//        recordPolicialEc.setTipoDocu(line3);
//        recordPolicialEc.setNumIdenti(line4);
//        recordPolicialEc.setApellidosNombres(line5);
//        recordPolicialEc.setRegAntecedentes(line6);

//        Log.d(TAG, line1);
//        final EditText input = new EditText(this);
//        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // Width , height
//        input.setLayoutParams(lparams);
//        input.setText(extractedText);
//        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        TextView textView = new TextView(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(10, 10, 10, 10);
//        textView.setLayoutParams(params);
//        textView.setPadding(4, 4, 4, 4);
//        textView.setText(recordPolicialEc.toString());


//        AlertDialog dialogNuevoOficio = new AlertDialog.Builder(this)
//                .setIcon(R.drawable.ic_police_record)
//                .setTitle("Record policial ingresado:")
//                .setMessage(recordPolicialEc.toString())
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
////                        try {
////                            dialogNuevoOficio.dismiss();
////                        } catch (Exception e) {
////
////                        }
//
//                        if (line6.equals("NO")) {
//                            Toast.makeText(RegistroRecordPolicialActivity.this, "Verificación existosa!", Toast.LENGTH_SHORT).show();
//                            trabajador.setEstadoRrcordP(false);
//                            policeRecord.setStatusCriminalRecord(false);
//                            buttonNext.setEnabled(true);
//                            if (selectMethod == 3) {
//                                Glide.with(getApplicationContext()).load(R.drawable.ic_pdf_checked).into(imageViewPdf);
//                                imageViewGallery.setColorFilter(colorTheme);
//                            }
//
//                            blockingAllButtons();
//
//
//                        } else {
//                            Toast.makeText(RegistroRecordPolicialActivity.this, "El record policial ingresado es inválido!", Toast.LENGTH_SHORT).show();
//                            trabajador.setEstadoRrcordP(true);
//                            policeRecord.setStatusCriminalRecord(true);
//                            buttonNext.setEnabled(false);
////                            buttonRecord.setEnabled(false);
//                            //buttonRecord.setVisibility(View.GONE);
//                        }
//                    }
//
//
//                }).create();
//
//        dialogNuevoOficio.show();
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


//    private void openAlertDialogPhotoOptions() {
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
//                        escogerDesdeGaleria();
//                        break;
//                    case 1: // cow
//                        tomarfoto();
//                        break;
//                }
//            }
//        });
//
//// create and show the alert dialog
//        android.app.AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    private void escogerDesdeGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }


    private void escogerPDF() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("pdf/*");
//        startActivityForResult(intent, SELECCIONAR_PDF_REQ_ID);


        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, SELECCIONAR_PDF_REQ_ID);

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

//    private void selectPhoto() {
//        // BEGIN_INCLUDE(startCamera)
//        // Check if the Camera permission has been granted
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
//            openAlertDialogPhotoOptions();
//        } else {
//            // Permission is missing and must be requested.
//            requestCameraAndWExtStPermission();
//        }
//        // END_INCLUDE(startCamera)
//    }


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
//                    openAlertDialogPhotoOptions();
                    if (selectMethod == 1) {
                        tomarfoto();
                    }
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
//                    openAlertDialogPhotoOptions();
                    if (selectMethod == 1) {
                        tomarfoto();
                    }
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
//                    openAlertDialogPhotoOptions();
                    if (selectMethod == 1) {
                        tomarfoto();
                    }
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
//                Glide.with(getApplicationContext()).load(uriPhoto).into(imageViewCam);
//                imageViewCam.setColorFilter(null);
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
                Glide.with(getApplicationContext()).load(uriPhoto).into(imageViewGallery);
                imageViewGallery.setColorFilter(null);
                InputStream imageStream = getContentResolver().openInputStream(uriPhoto);
                final Bitmap recordPolicialImg = BitmapFactory.decodeStream(imageStream);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();
                procesarRecordPolicial(recordPolicialImg);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == SELECCIONAR_PDF_REQ_ID && resultCode == RESULT_OK) {

            try {
                final Uri pdfUri = data.getData();
                uriPhoto = pdfUri;
//                Glide.with(getApplicationContext()).load(uriPhoto).into(imageViewPdf);
                InputStream imageStream = getContentResolver().openInputStream(uriPhoto);
                extractPDF(data.getData().getPath(), imageStream);

//                final Bitmap recordPolicialImg = BitmapFactory.decodeStream(imageStream);
//                Toast.makeText(getApplicationContext(), uriPhoto.getPath().toString(), Toast.LENGTH_SHORT).show();
//                procesarRecordPolicial(recordPolicialImg);

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
                                || policeRecord.getNameAndLastName().equals("No detectado") || policeRecord.getCriminalRecordStat().equals("No detectado"))
                            policeRecord.setStatusCriminalRecord(true);
                        {
                            alertDialogRecordPolicial(policeRecord);
                        }
                    }
                });
    }

    public void alertDialogRecordPolicial(PoliceRecord policeRecordVar) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
        dialogRecordPolicial = new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle(R.string.record_ingresado)
                .setMessage(policeRecordVar.toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (policeRecordVar.isStatusCriminalRecord()) {
                            Toast.makeText(RegistroRecordPolicialActivity.this, "El record policial ingresado es inválido!", Toast.LENGTH_SHORT).show();
                            trabajador.setEstadoRrcordP(true);
                            buttonNext.setEnabled(false);
                            removeBlockingAllButtons();

                        } else {
                            Toast.makeText(RegistroRecordPolicialActivity.this, "Verificación existosa!", Toast.LENGTH_SHORT).show();
                            trabajador.setEstadoRrcordP(false);
                            buttonNext.setEnabled(true);

                            if (selectMethod == 1) {
                                Glide.with(getApplicationContext()).load(uriPhoto).into(imageViewCam);
                                imageViewCam.setColorFilter(null);
                            }

                            if (selectMethod == 2) {
                                Glide.with(getApplicationContext()).load(uriPhoto).into(imageViewGallery);
                                imageViewGallery.setColorFilter(null);
                            }


                            if (selectMethod == 3) {
                                Glide.with(getApplicationContext()).load(R.drawable.ic_pdf_checked).into(imageViewPdf);
                                imageViewPdf.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.check_color));
                            }

                            blockingAllButtons();


//                            buttonRecord.setEnabled(false);
                            //buttonRecord.setVisibility(View.GONE);
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