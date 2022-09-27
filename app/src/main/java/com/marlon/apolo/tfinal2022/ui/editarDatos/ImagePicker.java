package com.marlon.apolo.tfinal2022.ui.editarDatos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.marlon.apolo.tfinal2022.BuildConfig;
import com.marlon.apolo.tfinal2022.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ImagePicker extends BottomSheetDialogFragment {

    ImagePicker.GetImage getImage;

    public ImagePicker(ImagePicker.GetImage getImage, boolean allowMultiple) {
        this.getImage = getImage;
    }

    File cameraImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_imagepicker, container, false);
        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 2000);
                } else {
                    captureFromCamera();
                }
            }
        });
        view.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 2000);
                } else {
                    startGallery();
                }
            }
        });
        return view;
    }

    public interface GetImage {
        void setGalleryImage(Uri imageUri);

        void setCameraImage(String filePath);

        void setImageFile(File file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                getImage.setGalleryImage(returnUri);
                Bitmap bitmapImage = null;
            }
            if (requestCode == 1002) {
                if (cameraImage != null) {
                    getImage.setImageFile(cameraImage);
                }
                getImage.setCameraImage(cameraFilePath);
            }
        }
    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    private String cameraFilePath;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName, /* prefix */ ".jpg", /* suffix */ storageDir /* directory */);
        cameraFilePath = "file://" + image.getAbsolutePath();
        cameraImage = image;
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, 1002);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}