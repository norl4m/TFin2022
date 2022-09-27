/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marlon.apolo.tfinal2022.individualChat.view;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Este Activity permite utilizar la(s) Cámara(s) que un dispositivo con SO Android(Nougat(API 24) - Android 10(API 29))
 * API utilizada = CameraX permite compatibilidad con el 90% de los dispositivos android en el mercado.
 * Trabaja desde Android 5.0(API 21)
 * This activity shows a button to trigger the date picker.
 */
public class CamActivity extends AppCompatActivity
        implements CameraXConfig.Provider,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {
    private String TAG = CamActivity.class.getSimpleName();
    ProcessCameraProvider cameraProvider;
    private boolean frontBack;
    private Usuario usuarioRemoto;
    public static CamActivity camActivity;

    private static final int SELECT_AUDIO = 1500;
    private static final int SELECT_IMAGE = 1501;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1502;
    private static final int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1503;
    private static final int PERMISSION_REQUEST_CAMERA = 1504;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 1505;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 1506;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1510;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1520;
    private CameraControl cameraControl;
    private CameraInfo cameraInfo;
    private Slider slider;
    private boolean inScale;
    private float scaleFactor;
    private Camera cameraX;


    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    ImageButton imageButton;
    private ImageButton imageButtonTake;

    /**
     * Blocking camera operations are performed using this executor
     */
    private ExecutorService cameraExecutor;
    //    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector scaleGestureDetector;

    public void closeForce() {
        Log.d(TAG, "closeForce");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);
        camActivity = this;
        frontBack = false;
        cameraExecutor = Executors.newSingleThreadExecutor();
        Log.e(TAG, String.format("Camera: %s", String.valueOf(checkCameraHardware(this))));

        usuarioRemoto = (Usuario) getIntent().getSerializableExtra("usuarioRemoto");

//
//        int REQUEST_CODE_PERMISSION_STORAGE = 100;
//        String[] permissions = {
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
//        };
//
//        for (String str : permissions) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
//                    return;
//                }
//            }
//        }

//        mDetector = new GestureDetectorCompat(this, this);
        scaleGestureDetector = new ScaleGestureDetector(this, this);
        // Set the gesture detector as the double tap
        // listener.
//        mDetector.setOnDoubleTapListener(this);
//        scaleGestureDetector.setOnDoubleTapListener(this);

//        imageButton = findViewById(R.id.camera_images_button);
        slider = findViewById(R.id.slider);
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                Log.d(TAG, "onStartTrackingTouch" + String.valueOf(slider.getValue()));
                cameraControl.setZoomRatio(slider.getValue());

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {

            }
        });
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
//                Log.d(TAG, String.valueOf(slider.getValue()));
                Log.d(TAG, "onValueChange" + String.valueOf(slider.getValue()));

                Log.d(TAG, String.valueOf(value));
                Log.d(TAG, String.valueOf(fromUser));
                if (fromUser) {
                    cameraControl.setZoomRatio(slider.getValue());
                }
            }
        });
        imageButtonTake = findViewById(R.id.camera_capture_button);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        previewView = findViewById(R.id.viewFinder);
        findViewById(R.id.camera_capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.camera_change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontBack = !frontBack;
                if (frontBack) {
                    bindPreview(cameraProvider, CameraSelector.LENS_FACING_FRONT);
                } else {
                    bindPreview(cameraProvider, CameraSelector.LENS_FACING_BACK);
                }
            }
        });

//        if (SDK_INT >= Build.VERSION_CODES.M) {
//            if (this.checkSelfPermission(READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_DENIED) {
//                //permission already no granted\
////                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                String[] permissions = {READ_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA};
//                requestPermissions(permissions, 4003);
//            } else {
//                //permission already granted
//                startCamera();
//            }
//        } else {
//            //system os is less then marshallow
//            startCamera();
//        }


        //system os is less then marshallow
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            openAlertDialogPhotoOptions();
            startCamera();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                // Permission is missing and must be requested.
                requestCameraAndWExtStPermission();
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already available
                startCamera();
            } else {
                // Permission is missing and must be requested.
                requestCameraPermission();
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already available
                startCamera();
            } else {
                // Permission is missing and must be requested.
                requestCameraAPILocaPermission();
            }
        }


//        if (SDK_INT >= Build.VERSION_CODES.M && SDK_INT <= Build.VERSION_CODES.P) {
//            if (this.checkSelfPermission(WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_DENIED) {
//                //permission already no granted\
//                //                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                String[] permissions = {WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA};
//                requestPermissions(permissions, 4003);
//            } else {
//                //permission already granted
//                startCamera();
//
//            }
//
//        } else {
//            //system os is less then marshallow
//            if (SDK_INT < Build.VERSION_CODES.M) {
//                startCamera();
//            }
//            if (SDK_INT >= Build.VERSION_CODES.Q) {
//
//                if (this.checkSelfPermission(CAMERA)
//                        == PackageManager.PERMISSION_DENIED) {
//                    //permission already no granted\
//                    //                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                    String[] permissions = {
//                            Manifest.permission.CAMERA};
//                    requestPermissions(permissions, 4004);
//                } else {
//                    //permission already granted
//                    startCamera();
//
//
//                }
//            }
//        }
    }

    private void requestCameraAPILocaPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(imageButtonTake, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CamActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_LOCA);
                }
            }).show();

        } else {
            Snackbar.make(imageButtonTake, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
        }
    }


    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(imageButtonTake, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CamActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA_ONLY);
                }
            }).show();

        } else {
            Snackbar.make(imageButtonTake, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
        }
    }

    private void requestCameraAndWExtStPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(imageButtonTake, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CamActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(imageButtonTake, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }


    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, CameraSelector.LENS_FACING_BACK);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                Log.e("TAG", e.getLocalizedMessage());
            }
        }, getExecutor());
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider, int camera) {

//        cameraProvider.unbindAll();


        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .requireLensFacing(camera)
                .build();

//        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);


        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        // viewFinder is a PreviewView instance


//        override fun onTouchEvent(event: MotionEvent) : Boolean {
//            // Let the ScaleGestureDetector inspect all events
//            scaleDetector.onTouchEvent(event)
//            return true
//        }
//
//        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//            override fun onScale(detector: ScaleGestureDetector): Boolean {
//                val scale = cameraControl.getZoomRatio.getValue() * detector.getScaleFactor()
//            }
//        }

//        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetResolution(new Size(600, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageCapture =
                new ImageCapture.Builder()
//                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

        cameraProvider.unbindAll();

        cameraX = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

        // For performing operations that affect all outputs.
        cameraControl = cameraX.getCameraControl();
// For querying information and states.
        cameraInfo = cameraX.getCameraInfo();

        Log.d(TAG, String.valueOf(cameraInfo.getZoomState().getValue().getMinZoomRatio()));
        Log.d(TAG, String.valueOf(cameraInfo.getZoomState().getValue().getMaxZoomRatio()));
        slider.setValueFrom(cameraInfo.getZoomState().getValue().getMinZoomRatio());
        slider.setValue(cameraInfo.getZoomState().getValue().getMinZoomRatio());
        slider.setValueTo(cameraInfo.getZoomState().getValue().getMaxZoomRatio());


        // Use the CameraInfo instance to observe the zoom state
        cameraInfo.getZoomState().observe((LifecycleOwner) this, zoomState -> {
            Log.d(TAG, "########################################");
            Log.d(TAG, "current zoom: " + String.valueOf(zoomState.getZoomRatio()));
            Log.d(TAG, "########################################");
        });


//        ListenableFuture<Void> mm = cameraControl.enableTorch(true);
//        mm.addListener(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mm.get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, getExecutor());


        // Attach the pinch gesture listener to the viewfinder
        previewView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // ... Respond to touch events
//                mDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);
//                return super.onTouchEvent(event);
                return true;
            }
        });

    }


    public void takePhoto() throws IOException {
        long name = System.currentTimeMillis();

        // Add a specific media item.
//        ContentResolver resolver = getApplicationContext()
//                .getContentResolver();

// Find all audio files on the primary external storage device.
// On API <= 28, use VOLUME_EXTERNAL instead.
//        Uri audioCollection = MediaStore.Images.Media.getContentUri(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
//
//// Publish a new song.
//        ContentValues newSongDetails = new ContentValues();
//        newSongDetails.put(MediaStore.Images.Media.DISPLAY_NAME,
//                name + ".jpg");
//
//// Keeps a handle to the new song's URI in case we need to modify it
//// later.
//        Uri myFavoriteSongUri = resolver
//                .insert(audioCollection, newSongDetails);
//        File file = new File(myFavoriteSongUri.getPath());

        /*********************************ESTO FUNCIONA EN EL EMULADOR******/
        /******************************************************************/

        String resultPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) +
                String.valueOf(name) + ".jpg";
        Log.e("resultpath", resultPath);


//        File file = null;


//        if (Build.VERSION.SDK_INT < 29){

//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, "Photo");
//            values.put(MediaStore.Images.Media.DESCRIPTION, "Edited");
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
//            values.put("_data", resultPath);
//
//            ContentResolver cr = getContentResolver();
//            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


//        }else {

//            file = new File(resultPath);

//            final String relativeLocation = Environment.DIRECTORY_PICTURES;
//            final ContentValues  contentValues = new ContentValues();
//
//            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation+"/"+dirName);
//            contentValues.put(MediaStore.MediaColumns.TITLE, "Photo");
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//            contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis ());
//            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
//            contentValues.put(MediaStore.MediaColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
//            contentValues.put(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
//
//            final ContentResolver resolver = getContentResolver();
//            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            Uri uri = resolver.insert(contentUri, contentValues);


//        }


        String target = getApplicationContext().getExternalCacheDir().getAbsolutePath();
        target += "/tempLocal.jpeg";
//        File file = new File(target);
        File file = getOutputMediaFile(1);
//        file = new File(target);


//        ImageCapture.OutputFileOptions outputFileOptions =
//                new ImageCapture.OutputFileOptions.Builder(file).build();
//
//
//        imageCapture.takePicture(outputFileOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
//            //        imageCapture.takePicture(outputFileOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                Uri uri = outputFileResults.getSavedUri();
//
////                Toast.makeText(CamActivity.this, "Imagen guardada en: " + uri.getPath(), Toast.LENGTH_LONG).show();
//                Toast.makeText(CamActivity.this, "Foto capturada", Toast.LENGTH_LONG).show();
//                Glide.with(CamActivity.this).load(uri).circleCrop().into(imageButton);
//                Intent replyIntent = new Intent();
//                replyIntent.setData(uri);
//                setResult(RESULT_OK, replyIntent);
//                //finish();
//                //saveImageOnFireStorage(uri);
//
////                imageButton.setImageURI(uri);
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException error) {
//                error.printStackTrace();
//            }
//        });


        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Photo_cam_x_" + String.valueOf(System.currentTimeMillis()));
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        //values.put(MediaStore.Images.Media.DESCRIPTION, "Edited");
        //values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        //values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        //values.put("_data", resultPath);

        // Create output options object which contains file + metadata


        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values).build();


        // Set up image capture listener, which is triggered after photo has
        // been taken

        imageCapture.
                takePicture(
                        outputFileOptions,
                        ContextCompat.getMainExecutor(this),
//                        cameraExecutor,
                        new ImageCapture.OnImageSavedCallback() {
                            //        imageCapture.takePicture(outputFileOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri uri = outputFileResults.getSavedUri();

//                Toast.makeText(CamActivity.this, "Imagen guardada en: " + uri.getPath(), Toast.LENGTH_LONG).show();
//                                Toast.makeText(getApplicationContext(), "Foto capturada", Toast.LENGTH_LONG).show();
//                Glide.with(CamActivity.this).load(uri).circleCrop().into(imageButton);
//                Intent replyIntent = new Intent();
//                replyIntent.setData(uri);
//                setResult(RESULT_OK, replyIntent);

                                Intent intent = new Intent(getApplicationContext(), SendFotoActivity.class);
                                intent.setData(uri);
                                intent.putExtra("usuarioRemoto", usuarioRemoto);
                                startActivity(intent);
//                                finish();

//                                String msg = "Photo capture succeeded: ${outputFileResults.getSavedUri()}";
//                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, msg);

                                //finish();
                                //saveImageOnFireStorage(uri);

//                imageButton.setImageURI(uri);
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException error) {
                                error.printStackTrace();
                                Log.e(TAG, "###############################################");
                                Log.e(TAG, "Photo capture failed: ${exc.message}", error);
                                Log.e(TAG, "###############################################");
                            }
                        });


//
        /******************************************************************/
        /******************************************************************/

        /*
         * REDMI NOTE 9S UTILIZA ANDROID 10 QKQ1.191215.002
         * */
        /**ESTO FUNCIONA EN CELULAR REDMI TOCA VER API*/

        /******************************************************************/

//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        ImageCapture.OutputFileOptions outputFileOptions =
//                new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        contentValues).build();

//
//        imageCapture.takePicture(outputFileOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
//            //        imageCapture.takePicture(outputFileOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                Uri uri = outputFileResults.getSavedUri();
//
////                Toast.makeText(CamActivity.this, "Imagen guardada en: " + uri.getPath(), Toast.LENGTH_LONG).show();
//                Toast.makeText(CamActivity.this, "Foto capturada", Toast.LENGTH_LONG).show();
//                Glide.with(CamActivity.this).load(uri).circleCrop().into(imageButton);
//                Intent replyIntent = new Intent();
//                replyIntent.setData(uri);
//                setResult(RESULT_OK, replyIntent);
//                //finish();
//                //saveImageOnFireStorage(uri);
//
////                imageButton.setImageURI(uri);
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException error) {
//                error.printStackTrace();
//            }
//        });

        /******************************************************************/
        /******************************************************************/

    }

    String currentPhotoPath;

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void saveImageOnFireStorage(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        String baseReference = "gs://tesis2021-aeb94.appspot.com";
        String imagePath = baseReference + "/trabajadores/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/" + "fotoPerfil.jpg";
        Log.d(TAG, "Path reference on fireStorage");
        StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

        Uri file = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        UploadTask uploadTask = storageRef.putFile(uri, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
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
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
                Log.d(TAG, "Upload is complete...");


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        releaseCamera();
//        getExecutor()
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraExecutor.shutdown();
        releaseCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 4003) {
//            // Request for camera permission.
//            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                // Permission has been granted. Start camera preview Activity.
//                Snackbar.make(imageButton, R.string.camera_permission_granted,
//                                Snackbar.LENGTH_SHORT)
//                        .show();
//                startCamera();
//            } else {
//                // Permission request was denied.
//                Snackbar.make(imageButton, R.string.camera_permission_denied,
//                                Snackbar.LENGTH_SHORT)
//                        .show();
//            }
//        }


        /******************/


//        if (requestCode == 4004) {
//            // Request for camera permission.
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission has been granted. Start camera preview Activity.
//                Snackbar.make(imageButton, R.string.camera_permission_granted,
//                                Snackbar.LENGTH_SHORT)
//                        .show();
//                startCamera();
//            } else {
//                // Permission request was denied.
//                Snackbar.make(imageButton, R.string.camera_permission_denied,
//                                Snackbar.LENGTH_SHORT)
//                        .show();
//            }
//        }
        // END_INCLUDE(onRequestPermissionsResult)


        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                // Request for camera permission.
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    startCamera();
                } else {
                    // Permission request was denied.
                    Snackbar.make(imageButtonTake, R.string.camera_permission_denied,
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case PERMISSION_REQUEST_CAMERA_ONLY:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    startCamera();
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
                    startCamera();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                }
                break;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_foto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fitStart:
                previewView.setScaleType(PreviewView.ScaleType.FIT_START);
                return true;
            case R.id.menuLin:
                ListenableFuture<Void> mm = cameraControl.enableTorch(true);
                mm.addListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mm.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, getExecutor());


                return true;
            case R.id.menuEn:

                return true;
            case R.id.fitCenter:
                previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
                return true;
            case R.id.fitEnd:
                previewView.setScaleType(PreviewView.ScaleType.FIT_END);
                return true;
            case R.id.zoomX1:
                Toast.makeText(getApplicationContext(), "zoom", Toast.LENGTH_LONG).show();
                cameraControl.setZoomRatio(1);
                return true;
            case R.id.zoomX2:
                Toast.makeText(getApplicationContext(), "zoom: " + String.valueOf(cameraInfo.getZoomState().getValue().getMinZoomRatio()), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "zoom: " + String.valueOf(cameraInfo.getZoomState().getValue().getMaxZoomRatio()), Toast.LENGTH_LONG).show();

                Log.d(TAG, String.valueOf(cameraInfo.getZoomState().getValue().getMinZoomRatio()) + "/" + String.valueOf(cameraInfo.getZoomState().getValue().getMaxZoomRatio()));
                cameraControl.setZoomRatio(2);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (this.mDetector.onTouchEvent(event)) {
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }


    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(TAG, "onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        // Log.d(TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Log.d(TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        //Log.d(TAG, "onScroll: " + event1.toString() + event2.toString());
        try {
            //addZoomToslider();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        float newX = previewView.getX();
        float newY = previewView.getY();
        if (!inScale) {
            newX -= distanceX;
            newY -= distanceY;
        }
        WindowManager wm = (WindowManager) previewView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        if (newX > (previewView.getWidth() * scaleFactor - p.x) / 2) {
            newX = (previewView.getWidth() * scaleFactor - p.x) / 2;
        } else if (newX < -((previewView.getWidth() * scaleFactor - p.x) / 2)) {
            newX = -((previewView.getWidth() * scaleFactor - p.x) / 2);
        }

        if (newY > (previewView.getHeight() * scaleFactor - p.y) / 2) {
            newY = (previewView.getHeight() * scaleFactor - p.y) / 2;
        } else if (newY < -((previewView.getHeight() * scaleFactor - p.y) / 2)) {
            newY = -((previewView.getHeight() * scaleFactor - p.y) / 2);
        }

        previewView.setX(newX);
        previewView.setY(newY);

//        return true;


        return true;
    }

    private void addZoomToslider() {
        float currentValue = slider.getValue();

        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "addZoomToslider");
        Log.d(TAG, "addZoomToslider" + String.valueOf(currentValue));
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        if (currentValue >= slider.getValueFrom() && currentValue <= slider.getValueTo()) {
            slider.setValue(currentValue + 1.0f);
        }
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //Log.d(TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //Log.d(TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        //Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        // Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.d(TAG, "onScale: ");

        float currentZoomRatio = cameraInfo.getZoomState().getValue().getZoomRatio();

        // Get the pinch gesture's scaling factor
        scaleFactor = detector.getScaleFactor();
        float realValue = currentZoomRatio * scaleFactor;

        try {
            if (realValue > cameraInfo.getZoomState().getValue().getMinZoomRatio() && realValue < cameraInfo.getZoomState().getValue().getMaxZoomRatio())
                slider.setValue(currentZoomRatio * scaleFactor);
        } catch (Exception e) {

        }

        // Update the camera's zoom ratio. This is an asynchronous operation that returns
        // a ListenableFuture, allowing you to listen to when the operation completes.
        cameraControl.setZoomRatio(currentZoomRatio * scaleFactor);
        Log.d(TAG, String.valueOf(cameraInfo.getZoomState().getValue().getZoomRatio()));

        // onScroll(null, null, 0, 0); // call scroll to make sure our bounds are still ok //


        // Return true, as the event was handled
        return true;
//        return false;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        inScale = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        inScale = false;
//        onScroll(null, null, 0, 0); // call scroll to make sure our bounds are still ok //

    }

    //    @Override
//    public boolean onTouch(View v, MotionEvent event) {
////        previewView = v.findViewById(R.id.viewFinder);
//        scaleGestureDetector.onTouchEvent(event);
//        return true;
//    }
    private void releaseCamera() {
        try {
            if (cameraX != null) {
                //cameraX.release();        // release the camera for other applications
                //cameraX.;        // release the camera for other applications
                cameraX = null;
            }
        } catch (Exception e) {

        }

    }
}