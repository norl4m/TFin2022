package com.marlon.apolo.tfinal2022.individualChat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.marlon.apolo.tfinal2022.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CamXActivity extends AppCompatActivity implements CameraXConfig.Provider {


    private static final int REQUEST_CAMX_CODE = 1000;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1001;
    private static final String TAG = CamXActivity.class.getSimpleName();
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_xactivity);


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                startCamera();


            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.

                showCamXMessage();
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                startCamera();

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.

                showCamXMessage();
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMX_CODE);
            }
        }

        // Set up the listeners for take photo and video capture buttons
        findViewById(R.id.image_capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        findViewById(R.id.video_capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePhoto();
            }
        });


        cameraExecutor = Executors.newSingleThreadExecutor();

        previewView = findViewById(R.id.viewFinder);


    }

    private void showCamXMessage() {

    }

    private void takePhoto() {

        // Get a stable reference of the modifiable image capture use case
//        val imageCapture = imageCapture ?: return
//
//                // Create time stamped name and MediaStore entry.
//                val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//                .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }


        imageCapture =
                new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();


        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Photo");
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
                        new ImageCapture.OnImageSavedCallback() {
                            //        imageCapture.takePicture(outputFileOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri uri = outputFileResults.getSavedUri();

//                Toast.makeText(CamActivity.this, "Imagen guardada en: " + uri.getPath(), Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Foto capturada", Toast.LENGTH_LONG).show();
//                Glide.with(CamActivity.this).load(uri).circleCrop().into(imageButton);
//                Intent replyIntent = new Intent();
//                replyIntent.setData(uri);
//                setResult(RESULT_OK, replyIntent);

                                String msg = "Photo capture succeeded: ${output.savedUri}";
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, msg);

                                //finish();
                                //saveImageOnFireStorage(uri);

//                imageButton.setImageURI(uri);
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException error) {
                                error.printStackTrace();
                                Log.e(TAG, "Photo capture failed: ${exc.message}", error);
                            }
                        });

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CamXActivity.this);

        cameraProviderFuture.addListener(() -> {
            ProcessCameraProvider cameraProvider;

            try {

                cameraProvider = cameraProviderFuture.get();


                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());


                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

//                imageCapture = new ImageCapture.Builder().build();

                imageCapture =
                        new ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build();
                // Unbind use cases before rebinding

                // Bind use cases to camera

                cameraProvider.bindToLifecycle(CamXActivity.this, cameraSelector, preview, imageCapture);
//                cameraProvider.bindToLifecycle(CamXActivity.this, cameraSelector, preview);


            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                Log.e("TAG", e.getLocalizedMessage());
            }


        }, ContextCompat.getMainExecutor(this));


    }

    private void captureVideo() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMX_CODE) {

            // Request for camera permission.
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startCamera();
            } else {
                // Permission request was denied.
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {

            // Request for camera permission.
            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }
}