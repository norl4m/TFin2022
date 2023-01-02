package com.marlon.apolo.tfinal2022.ui.oficioArchi.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel.OficioArchiViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.adapters.OficioViewModelPoc;
import com.marlon.apolo.tfinal2022.ui.trabajadores.viewModel.TrabajadorViewModel;


import java.util.ArrayList;
import java.util.List;

public class OficioArchiEditDeleteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1000;
    private static final String TAG = NuevoOficioArchiActivity.class.getSimpleName();
    private ImageView imageViewIcono;
    private TextInputLayout textInputLayoutNombre;
    private Button buttonSave;
    private Uri uriPhoto;
    private OficioArchiViewModel oficioArchiViewModel;
    //    private ArrayList<OficioArchiModel> oficioArchiModelsDB;
    private ProgressDialog progressDialog;
    private Oficio oficioArchiModelSelected;
    private ArrayList<Oficio> oficioArrayListDB;
    private boolean editMenu;
    private ArrayList<Trabajador> trabajadorArrayList;
    private int colorNight;

    public Uri getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(Uri uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficio_archi_edit_delete);

        editMenu = true;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initStatesButtons();


        oficioArchiModelSelected = (Oficio) getIntent().getSerializableExtra("oficioModel");

        if (oficioArchiModelSelected.getUriPhoto() != null) {
            uriPhoto = Uri.parse(oficioArchiModelSelected.getUriPhoto());
            Glide.with(getApplicationContext())
                    .load(uriPhoto)
                    .into(imageViewIcono);
            imageViewIcono.setColorFilter(colorNight);

        } else {
            Glide.with(getApplicationContext())
                    .load(AppCompatResources.getDrawable(this, R.drawable.ic_oficios))
                    .into(imageViewIcono);
            imageViewIcono.setColorFilter(colorNight);
        }

        textInputLayoutNombre.getEditText().setText(oficioArchiModelSelected.getNombre());

        oficioArrayListDB = new ArrayList<>();

        oficioArchiViewModel = new ViewModelProvider(this).get(OficioArchiViewModel.class);
        OficioViewModelPoc oficioViewModelPoc = new ViewModelProvider(this).get(OficioViewModelPoc.class);
        oficioViewModelPoc.getOficios().observe(this, new Observer<ArrayList<Oficio>>() {
            @Override
            public void onChanged(ArrayList<Oficio> oficios) {
//                oficioArrayList = new ArrayList<>();
                oficioArrayListDB = oficios;
            }
        });

        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(this, new Observer<List<Trabajador>>() {
            @Override
            public void onChanged(List<Trabajador> trabajadors) {
                trabajadorArrayList = new ArrayList<>();
                trabajadorArrayList = (ArrayList<Trabajador>) trabajadors;
            }
        });

    }

    public void initStatesButtons() {
        editMenu = true;

        buttonSave.setEnabled(false);
        textInputLayoutNombre.setEnabled(false);
        imageViewIcono.setEnabled(false);
        invalidateOptionsMenu();


    }

    public void editStatesButtons() {
        editMenu = false;
        buttonSave.setEnabled(true);
        textInputLayoutNombre.setEnabled(true);
        imageViewIcono.setEnabled(true);

        invalidateOptionsMenu();
    }

    private void escogerDesdeGaleria() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }

    public void closeProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }


    public Dialog deleteDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(OficioArchiEditDeleteActivity.this);
        builder
                .setTitle("Eliminar oficio:")
                .setMessage("¿Está seguro que desea eliminar este oficio?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
//                        Toast.makeText(getApplicationContext(), "Oficio eliminado.", Toast.LENGTH_SHORT).show();
//                        finish();
                        String title = "Por favor espere";
                        String message = "Eliminando oficio...";
                        showProgress(title, message);


                        boolean flagDelete = true;
                        try {
                            for (Trabajador tr : trabajadorArrayList) {
                                for (String idOf : tr.getIdOficios()) {
                                    if (idOf.equals(oficioArchiModelSelected.getIdOficio())) {
//                                        Toast.makeText(context, "No se puede eliminar el oficio", Toast.LENGTH_LONG).show();
                                        flagDelete = false;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d("TAG", e.toString());
                        }

                        if (flagDelete) {
//                            Toast.makeText(context,"Oficio eliminado",Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("oficios")
                                    .child(oficioArchiModelSelected.getIdOficio())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(OficioArchiEditDeleteActivity.this, "Oficio eliminado", Toast.LENGTH_LONG).show();
                                                closeProgressDialog();
                                                finish();
                                            } else {
                                                Toast.makeText(OficioArchiEditDeleteActivity.this, R.string.delete_oficio, Toast.LENGTH_LONG).show();
                                                closeProgressDialog();
                                            }

                                        }
                                    });
                        } else {
                            Toast.makeText(OficioArchiEditDeleteActivity.this, R.string.delete_oficio, Toast.LENGTH_LONG).show();
                            closeProgressDialog();
                        }


                        // oficioArchiViewModel.delete(oficioArchiModelSelected, OficioArchiEditDeleteActivity.this, progressDialog);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Dialog updateDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(OficioArchiEditDeleteActivity.this);
        builder
                .setTitle("Actualizar oficio:")
                .setMessage("¿Está seguro que desea actualizar este oficio?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
                        //Toast.makeText(getApplicationContext(), "Oficio actualizado.", Toast.LENGTH_SHORT).show();
                        //finish();
                        //OficioArchiModel oficioArchiModel = new OficioArchiModel();
                        oficioArchiModelSelected.setNombre(textInputLayoutNombre.getEditText().getText().toString().trim());
//                oficioArchiModel.setUriPhoto(uriPhoto.toString());
                        try {
                            if (!uriPhoto.toString().isEmpty()) {
                                oficioArchiModelSelected.setUriPhoto(uriPhoto.toString());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }


                        if (!oficioArchiModelSelected.getNombre().isEmpty()) {
                            boolean flagExit = false;
                            for (Oficio ofDB : oficioArrayListDB) {


                                if (oficioArchiModelSelected.getNombre().toLowerCase().equals(ofDB.getNombre().toLowerCase())) {
                                    Log.d(TAG, "Local " + oficioArchiModelSelected.toString());
                                    Log.d(TAG, "DB " + ofDB.toString());
                                    if (oficioArchiModelSelected.getUriPhoto() != null) {
                                        if (!oficioArchiModelSelected.getUriPhoto().equals(ofDB.getUriPhoto())) {
                                            flagExit = false;
                                            break;
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), R.string.oficio_duplicado, Toast.LENGTH_LONG).show();
//                                    Toast.makeText(getApplicationContext(), "Por favor ingrese otro nombre.", Toast.LENGTH_LONG).show();
                                    flagExit = true;
                                    break;
                                }


                            }
                            if (!flagExit) {
                                String title = "Por favor espere";
                                String message = "Actualizando oficio...";
                                showProgress(title, message);

                                oficioArchiViewModel.update(oficioArchiModelSelected, OficioArchiEditDeleteActivity.this, progressDialog);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "El nombre ingresado es inválido", Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        initStatesButtons();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_oficio_archi_edit_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
                Dialog deleteDialog = deleteDialog();
                deleteDialog.show();
                return true;
            case R.id.action_edit:
                editStatesButtons();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem edit = menu.findItem(R.id.action_edit);
//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//        colorNight = typedValue.data;

        Drawable delete = menu.getItem(0).getIcon(); // change 0 with 1,2 ...
//        yourdrawable.mutate();
        delete.setColorFilter(colorNight, PorterDuff.Mode.SRC_IN);


        Drawable editIcon = menu.getItem(1).getIcon(); // change 0 with 1,2 ...
//        yourdrawable.mutate();
        editIcon.setColorFilter(colorNight, PorterDuff.Mode.SRC_IN);


        edit.setVisible(editMenu);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewIcono:
                escogerDesdeGaleria();
                break;
            case R.id.buttonSave:
                Dialog updateDialog = updateDialog();
                updateDialog.show();
                break;
        }
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

}