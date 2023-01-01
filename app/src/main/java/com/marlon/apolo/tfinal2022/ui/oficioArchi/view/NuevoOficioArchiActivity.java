package com.marlon.apolo.tfinal2022.ui.oficioArchi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.model.OficioArchiModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel.OficioArchiViewModel;

import java.util.ArrayList;
import java.util.List;


public class NuevoOficioArchiActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1000;
    private static final String TAG = NuevoOficioArchiActivity.class.getSimpleName();
    private ImageView imageViewIcono;
    private TextInputLayout textInputLayoutNombre;
    private Button buttonSave;
    private OficioArchiViewModel oficioArchiViewModel;
    private Uri uriPhoto;

    private ArrayList<OficioArchiModel> oficioArchiModelsDB;
    private ProgressDialog progressDialog;
    private int colorNight;


    public TextInputLayout getTextInputLayoutNombre() {
        return textInputLayoutNombre;
    }

    public void setUriPhoto(Uri uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    public ImageView getImageViewIcono() {
        return imageViewIcono;
    }

    private void escogerDesdeGaleria() {
        // create an instance of the
        // intent of the type image
//        Intent i = new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECCIONAR_FOTO_GALERIA_REQ_ID);

//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_oficio_archi);

        oficioArchiModelsDB = new ArrayList<OficioArchiModel>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorNight = typedValue.data;
//        holder.imageView.setColorFilter(colorNight);
        /*Esto es una maravilla*/

        imageViewIcono = findViewById(R.id.imageViewIcono);
        imageViewIcono.setColorFilter(colorNight);

        textInputLayoutNombre = findViewById(R.id.textInputLayoutNombre);
        buttonSave = findViewById(R.id.buttonSave);

        imageViewIcono.setOnClickListener(this);
        buttonSave.setOnClickListener(this);


        oficioArchiViewModel = new ViewModelProvider(this).get(OficioArchiViewModel.class);


        oficioArchiViewModel.getAllOficios().observe(NuevoOficioArchiActivity.this, new Observer<List<OficioArchiModel>>() {
            @Override
            public void onChanged(List<OficioArchiModel> oficioArchiModels) {
                oficioArchiModelsDB = (ArrayList<OficioArchiModel>) oficioArchiModels;
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewIcono:
                escogerDesdeGaleria();
                break;
            case R.id.buttonSave:
                Oficio oficioArchiModel = new Oficio();
                oficioArchiModel.setNombre(textInputLayoutNombre.getEditText().getText().toString().trim());
//                oficioArchiModel.setUriPhoto(uriPhoto.toString());
                try {
                    if (!uriPhoto.toString().isEmpty()) {
                        oficioArchiModel.setUriPhoto(uriPhoto.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());


                }


                if (!oficioArchiModel.getNombre().isEmpty()) {
                    boolean flagExit = false;
                    for (OficioArchiModel ofDB : oficioArchiModelsDB) {
//                        if (oficioArchiModel.getNombre().toLowerCase().equals(ofDB.getNombre().toLowerCase())) {
//                        if (ofDB.getNombre().toLowerCase().equals(oficioArchiModel.getNombre().toLowerCase())) {
//                            Toast.makeText(getApplicationContext(), "El oficio ingresado ya se encuentra registrado!(equals)", Toast.LENGTH_LONG).show();
//                            flagExit = true;
//                            break;
//                        }

                        if (oficioArchiModel.getNombre().toLowerCase().equals(ofDB.getNombre().toLowerCase())) {
                            Toast.makeText(getApplicationContext(), R.string.oficio_duplicado, Toast.LENGTH_LONG).show();
//                            Toast.makeText(getApplicationContext(), "El oficio ingresado ya se encuentra registrado.", Toast.LENGTH_LONG).show();
                            flagExit = true;
                            break;
                        }


                    }
                    if (!flagExit) {
                        String title = "Por favor espere";
                        String message = "Registrando nuevo oficio...";
                        showProgress(title, message);
                        oficioArchiViewModel.insert(oficioArchiModel, NuevoOficioArchiActivity.this, progressDialog);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "El nombre ingresado es inválido", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECCIONAR_FOTO_GALERIA_REQ_ID && resultCode == RESULT_OK) {

            try {
                //Log.e(TAG, "Seleccionando imagen...");
                final Uri imageUri = data.getData();
                uriPhoto = imageUri;
                Glide.with(getApplicationContext())
                        .load(imageUri)
                        .into(imageViewIcono);
                imageViewIcono.setColorFilter(colorNight);

                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ha ocurrido un error inesperado!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_oficio_archi_nuevo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_nuevo:
                Oficio oficioArchiModel = new Oficio();
                oficioArchiModel.setNombre(textInputLayoutNombre.getEditText().getText().toString().trim());
//                oficioArchiModel.setUriPhoto(uriPhoto.toString());
                try {
                    if (!uriPhoto.toString().isEmpty()) {
                        oficioArchiModel.setUriPhoto(uriPhoto.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());


                }


                if (!oficioArchiModel.getNombre().isEmpty()) {
                    boolean flagExit = false;
                    for (OficioArchiModel ofDB : oficioArchiModelsDB) {
//                        if (oficioArchiModel.getNombre().toLowerCase().equals(ofDB.getNombre().toLowerCase())) {
//                        if (ofDB.getNombre().toLowerCase().equals(oficioArchiModel.getNombre().toLowerCase())) {
//                            Toast.makeText(getApplicationContext(), "El oficio ingresado ya se encuentra registrado!(equals)", Toast.LENGTH_LONG).show();
//                            flagExit = true;
//                            break;
//                        }

                        if (oficioArchiModel.getNombre().toLowerCase().equals(ofDB.getNombre().toLowerCase())) {
                            Toast.makeText(getApplicationContext(), "El oficio ingresado ya se encuentra registrado.", Toast.LENGTH_LONG).show();
                            flagExit = true;
                            break;
                        }


                    }
                    if (!flagExit) {
                        String title = "Por favor espere";
                        String message = "Registrando nuevo oficio...";
                        showProgress(title, message);
                        oficioArchiViewModel.insert(oficioArchiModel, NuevoOficioArchiActivity.this, progressDialog);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "El nombre ingresado es inválido", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}