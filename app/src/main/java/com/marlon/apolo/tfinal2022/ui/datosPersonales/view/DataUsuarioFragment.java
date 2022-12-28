package com.marlon.apolo.tfinal2022.ui.datosPersonales.view;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.admin.AdminViewModel;
import com.marlon.apolo.tfinal2022.citasTrabajo.HerramientaCalendar;
import com.marlon.apolo.tfinal2022.databinding.FragmentDataUsuarioBinding;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.datosPersonales.viewModel.DataUsuarioViewModel;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataUsuarioFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = DataUsuarioViewModel.class.getSimpleName();
    private DataUsuarioViewModel dataUsuarioViewModel;
    private FragmentDataUsuarioBinding binding;
    private TextInputLayout textInputLayoutNombre;
    private TextInputLayout textInputLayoutApellido;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutCelular;
    private TextInputLayout textInputLayoutPassword;
    private FloatingActionButton floatingActionButtonImagenPerfil;
    private Button buttonActualizar;
    private Usuario usuarioLocal;

    private ImageView imageViewProfile;
    private int metodoRegistro;
    private AdminViewModel adminViewModel;
    private static final int SELECCIONAR_FOTO_GALERIA_REQ_ID = 1001;
    private static final int PERMISSION_REQUEST_CAMERA = 2000;
    private static final int REQUEST_CAMERA_PERMISSION_FOTO_PERFIL = 1000;
    private static final int PERMISSION_REQUEST_CAMERA_ONLY = 2001;
    private static final int PERMISSION_REQUEST_CAMERA_LOCA = 2003;

    private Uri uriPhoto;
    private Uri uriPhotoHttp;

    private ProgressDialog progressDialog;
    private int user;
    private RelativeLayout relativeLayoutCalif;
    private LinearLayout linearLayout;
    private ArrayList<Cita> citaArrayList;
    private ValueEventListener valueEventListenerCitasCrazys;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editar_oficio, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnu_edit_oficio) {
//            Intent intent = new Intent(requireActivity(), EditarOficioHabilidadActivity.class);
            Intent intent = new Intent(requireActivity(), EditarOficioHabilidad2Activity.class);
            intent.putExtra("trabajador", (Trabajador) usuarioLocal);
//            ArrayList<String> idOficios = this.trabajador.getIdOficios();
//            ArrayList<String> idHabilidades = this.trabajador.getIdHabilidades();
//            intent.putStringArrayListExtra("idOficios", idOficios);
//            intent.putStringArrayListExtra("idHabilidades", idHabilidades);
            requireActivity().startActivity(intent);
            return true;

        }


//        if (id == R.id.habilidades_reg) {
//            Intent intent = new Intent(requireActivity(), UpdateHabilidadActivity.class);
//            ArrayList<String> idOficios = this.trabajador.getIdOficios();
//            ArrayList<String> idHabilidades = this.trabajador.getIdHabilidades();
//            intent.putStringArrayListExtra("idOficios", idOficios);
//            intent.putStringArrayListExtra("idHabilidades", idHabilidades);
//            requireActivity().startActivity(intent);
//            return true;
//
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.mnu_edit_oficio);
        item.setVisible(false);
        if (user == 2) {
            item.setVisible(true);
        }

    }

    public void updateOptionsMenu() {
        //isEditing = !isEditing;
        requireActivity().invalidateOptionsMenu();
    }

    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dataUsuarioViewModel = new ViewModelProvider(this).get(DataUsuarioViewModel.class);

        binding = FragmentDataUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textInputLayoutNombre = root.findViewById(R.id.textInputLayoutName);
        textInputLayoutApellido = root.findViewById(R.id.textInputLayoutLastName);
        textInputLayoutEmail = root.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutCelular = root.findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = root.findViewById(R.id.textInputLayoutPassword);
        imageViewProfile = root.findViewById(R.id.imageViewProfile);
        buttonActualizar = root.findViewById(R.id.buttonUpdate);
        buttonActualizar.setOnClickListener(this);
        floatingActionButtonImagenPerfil = root.findViewById(R.id.fabSeleccionarFoto);
        floatingActionButtonImagenPerfil.setOnClickListener(this);

        textInputLayoutCelular.setVisibility(View.GONE);
        textInputLayoutCelular.setEnabled(false);
        textInputLayoutEmail.setVisibility(View.GONE);
        textInputLayoutEmail.setEnabled(false);
        textInputLayoutPassword.setVisibility(View.GONE);
        textInputLayoutPassword.setEnabled(true);
        imageViewProfile.setOnClickListener(this);

        relativeLayoutCalif = root.findViewById(R.id.relLayoutCalif);
        linearLayout = root.findViewById(R.id.linLayoutCalif);
        linearLayout.setVisibility(View.GONE);
        relativeLayoutCalif.setVisibility(View.GONE);


        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        imageViewProfile.setColorFilter(colorNight);
        /*Esto es una maravilla*/
        /*final TextView textView = binding.textDataUsuario;
        dataUsuarioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            filterByUser(FirebaseAuth.getInstance().getCurrentUser());
        }

        SharedPreferences mPreferences = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        user = mPreferences.getInt("usuario", -1);


        return root;
    }

    private void filterByUser(FirebaseUser firebaseUser) {

        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getOneTrabajador(firebaseUser.getUid()).observe(requireActivity(), trabajador -> {
            if (trabajador != null) {
                usuarioLocal = trabajador;
                user = 2;
                loadLocalInfo(usuarioLocal);
                relativeLayoutCalif.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                TextView textViewCalif = relativeLayoutCalif.findViewById(R.id.textViewCalif);
                TextView textViewTrabComple = linearLayout.findViewById(R.id.textViewTrabComple);
                TextView textViewTrabIncom = linearLayout.findViewById(R.id.textViewTrabIncom);
                TextView textViewTrabNoAsist = linearLayout.findViewById(R.id.textViewNoAsist);
                TextView textViewNoCalif = linearLayout.findViewById(R.id.textViewNoCalif);
                RatingBar ratingBar = relativeLayoutCalif.findViewById(R.id.ratingBar);
                textViewCalif.setText(String.format(Locale.getDefault(), "Calificación: %.1f ", trabajador.getCalificacion()));
                ratingBar.setRating((float) trabajador.getCalificacion());
                valueEventListenerCitasCrazys = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Number of messages: " + snapshot.getChildrenCount());
                        //textViewCalif.setText("Trabajos completados: " + String.valueOf(snapshot.getChildrenCount()));

                        citaArrayList = new ArrayList<>();
                        ArrayList<Cita> citaArrayListNoAsist = new ArrayList<>();
                        ArrayList<Cita> citaArrayListIncomple = new ArrayList<>();
                        ArrayList<Cita> citaArrayListCalif = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Cita citaDB = data.getValue(Cita.class);
                            Log.d(TAG, citaDB.toString());
                            if (citaDB.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if (citaDB.getCalificacion() == 0) {
//                                            citaArrayList.add(citaDB);
                                    citaArrayListCalif.add(citaDB);
                                }

                                if (citaDB.isState()) {
                                    citaArrayList.add(citaDB);
                                }

                                try {

                                    switch (citaDB.getObservaciones()) {
                                        case "Trabajador no asistió":
                                            citaArrayListNoAsist.add(citaDB);
                                            break;
                                        case "Trabajador incumplido":
                                            citaArrayListIncomple.add(citaDB);
                                            break;
                                        case "Ninguna":
                                        default:
//                            mnuFin.setVisible(true);
//                            mnuFin.setVisible(!citaLocal.isState());

                                            //mnuEliminarCita.setVisible(true);
                                            //editCita.setVisible(true);
                                            break;
                                    }
                                } catch (Exception e) {
                                    //mnuFin.setVisible(true);
                                    Log.d(TAG, e.toString());
                                }
                            }
                        }
                        Log.d(TAG, "Number of messages: " + String.valueOf(citaArrayList.size()));

                        textViewTrabComple.setText("Trabajos completados: " + String.valueOf(citaArrayList.size()));
                        textViewTrabIncom.setText("Trabajos incompletos: " + String.valueOf(citaArrayListIncomple.size()));
                        textViewTrabNoAsist.setText("Trabajos no asistidos: " + String.valueOf(citaArrayListNoAsist.size()));
                        textViewNoCalif.setText("Trabajos no calificados: " + String.valueOf(citaArrayListCalif.size()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                FirebaseDatabase.getInstance().getReference().child("citas")
                        .addValueEventListener(valueEventListenerCitasCrazys);

            }
        });
        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getOneEmpleador(firebaseUser.getUid()).observe(requireActivity(), empleador -> {
            if (empleador != null) {
                usuarioLocal = empleador;
                user = 1;
                loadLocalInfo(usuarioLocal);
                relativeLayoutCalif.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

            }
        });

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.getAdministradorLiveData(firebaseUser.getUid()).observe(requireActivity(), administrador -> {
            if (administrador != null) {
                usuarioLocal = administrador;
                user = 0;
                loadLocalInfo(usuarioLocal);
                relativeLayoutCalif.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
            }
        });


        Log.d(TAG, "Provider: " + firebaseUser.getProviderData().get(firebaseUser.getProviderData().size() - 1).getProviderId());

        String provider = firebaseUser.getProviderData().get(firebaseUser.getProviderData().size() - 1).getProviderId();
        if (provider.contains("google.com")) {
            textInputLayoutPassword.setVisibility(View.GONE);
            textInputLayoutEmail.setVisibility(View.VISIBLE);
            textInputLayoutCelular.setVisibility(View.GONE);
            metodoRegistro = 2;
        } else {
            if (provider.contains("phone")) {
                metodoRegistro = 1;
                textInputLayoutCelular.setVisibility(View.VISIBLE);
                textInputLayoutPassword.setVisibility(View.GONE);
                textInputLayoutEmail.setVisibility(View.GONE);
            } else {
                metodoRegistro = 0;
                textInputLayoutCelular.setVisibility(View.GONE);
                textInputLayoutPassword.setVisibility(View.VISIBLE);
                textInputLayoutEmail.setVisibility(View.VISIBLE);
            }
        }


    }

    private void loadLocalInfo(Usuario usuario) {
        if (usuario.getFotoPerfil() != null) {
            try {
                Glide.with(requireActivity()).load(usuario.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageViewProfile);
                imageViewProfile.setColorFilter(null);
            } catch (Exception e) {

            }
//            uriPhoto = Uri.parse(usuario.getFotoPerfil());
            uriPhotoHttp = Uri.parse(usuario.getFotoPerfil());
        }
        if (usuario.getNombre() != null && usuario.getApellido() != null) {
            textInputLayoutNombre.getEditText().setText(String.format("%s", usuario.getNombre()));
            textInputLayoutApellido.getEditText().setText(String.format("%s", usuario.getApellido()));
        }
        if (usuario.getEmail() != null) {
            textInputLayoutEmail.getEditText().setText(usuario.getEmail());
        }

        if (usuario.getCelular() != null) {
            textInputLayoutCelular.getEditText().setText(usuario.getCelular());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdate:
//                Toast.makeText(requireActivity(), "Actualizando", Toast.LENGTH_LONG).show();
//                Toast.makeText(requireActivity(), "Actualizando" + usuarioLocal.toString(), Toast.LENGTH_LONG).show();


                showProgress("Actualizando información", "Por favor espere...");
                String name = textInputLayoutNombre.getEditText().getText().toString();
                String apellido = textInputLayoutApellido.getEditText().getText().toString();
                String password = textInputLayoutPassword.getEditText().getText().toString();

                boolean flagReg = false;
                if (!name.isEmpty() && !apellido.isEmpty()) {
                    usuarioLocal.setNombre(name);
                    usuarioLocal.setApellido(apellido);
                    flagReg = true;
                }


                String locationToFirebase = "";
                if (usuarioLocal instanceof Administrador) {
                    Log.d(TAG, "Administrador");
                    locationToFirebase = "administrador";
                }
                if (usuarioLocal instanceof Empleador) {
                    Log.d(TAG, "Empleador");
                    locationToFirebase = "empleadores";

                }
                if (usuarioLocal instanceof Trabajador) {
                    Log.d(TAG, "Trabajador");
                    locationToFirebase = "trabajadores";

                }


                boolean photoFlag = false;
                if (uriPhoto != null) {
//                    Uri returnUri = uriPhoto;
//                    Cursor returnCursor = requireActivity().getContentResolver().query(returnUri, null, null, null, null);
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
//                    if (returnCursor.getLong(sizeIndex) > 0) {
//                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
//                        photoFlag = true;
//                    } else {
//                        photoFlag = false;
//                    }


                    if (uriPhoto.toString().contains("https:")) {
                        photoFlag = true;
                    } else {
                        //                            Uri returnUri = Uri.parse(empleador.getFotoPerfil().toString());
                        Uri returnUri = uriPhoto;
                        Cursor returnCursor = requireActivity().getContentResolver().query(returnUri, null, null, null, null);
                        /*
                         * Get the column indexes of the data in the Cursor,
                         * move to the first row in the Cursor, get the data,
                         * and display it.
                         */
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        Log.d(TAG, returnCursor.getString(nameIndex));
                        Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                        if (returnCursor.getLong(sizeIndex) > 0) {
                            //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                            //photoFlag = true;
                        } else {
                            //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                            //photoFlag = false;
                        }
                        photoFlag = true;

                    }


                } else {
                    photoFlag = false;
                }

                if (uriPhotoHttp != null) {
                    if (uriPhotoHttp.toString().contains("https:")) {
                        usuarioLocal.setFotoPerfil(uriPhotoHttp.toString());
                        photoFlag = false;
                    } else {
                        usuarioLocal.setFotoPerfil(uriPhotoHttp.toString());
                        photoFlag = true;
                    }
                }

//                if (photoFlag) {
//                    if (uriPhoto.toString().contains("https:")) {
//
//                    } else {
//                        usuarioLocal.setFotoPerfil(uriPhoto.toString());
//                    }
//                } else {
//                    usuarioLocal.setFotoPerfil(null);
//                }


                if (flagReg) {
                    usuarioLocal.updateCompleteInfo(locationToFirebase, requireActivity(), metodoRegistro, password, progressDialog);
                } else {
                    closeProgressDialog();
                    Toast.makeText(requireActivity(), "La información ingresada es incorrecta.", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.fabSeleccionarFoto:
//                Toast.makeText(requireActivity(), "Seleccionar foto", Toast.LENGTH_LONG).show();

                //system os is less then marshallow
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.d(TAG, "System OS <: " + String.valueOf(Build.VERSION_CODES.M));
                    openAlertDialogPhotoOptions();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    Log.d(TAG, "System OS >= : " + String.valueOf(Build.VERSION_CODES.M) + " YY <=" + String.valueOf(Build.VERSION_CODES.P));
                    selectPhoto();
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    Log.d(TAG, "System OS > : " + String.valueOf(Build.VERSION_CODES.P) + " XX <=" + String.valueOf(Build.VERSION_CODES.Q));

                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        openAlertDialogPhotoOptions();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraPermission();
                    }
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    Log.d(TAG, "System OS > : " + String.valueOf(Build.VERSION_CODES.Q));
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already available
                        openAlertDialogPhotoOptions();
                    } else {
                        // Permission is missing and must be requested.
                        requestCameraAPILocaPermission();
                    }
                }


                break;
            case R.id.imageViewProfile:
                boolean photoFlagI = false;

                if (uriPhotoHttp != null) {
                    Intent intentFoto = new Intent(requireActivity(), FotoActivity.class);
                    intentFoto.setData(uriPhotoHttp);
                    requireActivity().startActivity(intentFoto);
                } else {
                    try {
                        if (uriPhoto != null) {
                            Log.d(TAG, uriPhoto.toString());
                            if (uriPhoto.toString().contains("https:")) {
                                photoFlagI = true;
                            } else {
                                //                            Uri returnUri = Uri.parse(empleador.getFotoPerfil().toString());
                                Uri returnUri = uriPhoto;
                                Cursor returnCursor = requireActivity().getContentResolver().query(returnUri, null, null, null, null);
                                /*
                                 * Get the column indexes of the data in the Cursor,
                                 * move to the first row in the Cursor, get the data,
                                 * and display it.
                                 */
                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                returnCursor.moveToFirst();
                                Log.d(TAG, returnCursor.getString(nameIndex));
                                Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                                if (returnCursor.getLong(sizeIndex) > 0) {
                                    //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                                    photoFlagI = true;
                                } else {
                                    //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                                    photoFlagI = false;
                                }
                            }

                        } else {
                            photoFlagI = false;
                        }
                        if (photoFlagI) {
                            Intent intentFoto = new Intent(requireActivity(), FotoActivity.class);
                            intentFoto.setData(uriPhoto);
                            requireActivity().startActivity(intentFoto);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }


                break;
        }
    }

    private void closeProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
//                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
                }
            }).show();

        } else {
            Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_ONLY);
        }
    }

    private void requestCameraAPILocaPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
//                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
                }
            }).show();

        } else {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setTitle("");
                builder.setMessage(R.string.permiso_camera_text_data);
                // Add the buttons
                builder.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
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
                Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
                // Request the permission. The result will be received in onRequestPermissionResult().
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA_LOCA);


            }
        }
    }


    private void openAlertDialogPhotoOptions() {
        // setup the alert builder
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireActivity());
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


    private void tomarfoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {


                ContentResolver resolver = requireActivity()
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


    private void escogerDesdeGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO_GALERIA_REQ_ID);

    }


    private void selectPhoto() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
//                    ActivityCompat.requestPermissions(requireActivity(),
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CAMERA_PERMISSION_FOTO_PERFIL && resultCode == RESULT_OK) {

            try {
//                final Uri imageUri = data.getData();
                Uri uriImageToSend = uriPhoto;
                uriPhotoHttp = uriImageToSend;
//                uriPhoto = uriImageToSend;

                Glide.with(requireActivity()).load(uriPhoto).circleCrop().into(imageViewProfile);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();
                imageViewProfile.setColorFilter(null);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CAMERA_PERMISSION_FOTO_PERFIL && resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Seleccionando imagen desde galería." + "RESULT_CANCELED");
            uriPhoto = null;
            try {
//                final Uri imageUri = data.getData();
                Uri uriImageToSend = uriPhoto;
//                uriPhotoHttp = uriPhoto;

//                Glide.with(requireActivity()).load(uriPhoto).circleCrop().into(imageViewProfile);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == SELECCIONAR_FOTO_GALERIA_REQ_ID && resultCode == RESULT_OK) {

            try {
                Log.d(TAG, "Seleccionando imagen desde galería.");
                final Uri imageUri = data.getData();
                uriPhoto = imageUri;
                uriPhotoHttp = imageUri;

                Glide.with(requireActivity()).load(uriPhoto).circleCrop().into(imageViewProfile);
                // Toast.makeText(getApplicationContext(), uriPhoto.toString(), Toast.LENGTH_SHORT).show();

                imageViewProfile.setColorFilter(null);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                Log.d(TAG, "PERMISSION_REQUEST_CAMERA");
                // Request for camera permission.
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();
                }
                break;

            case PERMISSION_REQUEST_CAMERA_ONLY:
                Log.d(TAG, "PERMISSION_REQUEST_CAMERA_ONLY");

                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                    Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();

                }
                break;

            case PERMISSION_REQUEST_CAMERA_LOCA:
                Log.d(TAG, "PERMISSION_REQUEST_CAMERA_LOCA");

                // Request for camera permission.
                if (grantResults.length >= 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission has been granted. Start camera preview Activity.
                    openAlertDialogPhotoOptions();
                } else {
                    // Permission request was denied.
                    /*Snackbar.make(fabChooseImageProfile, R.string.camera_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();*/
                    Snackbar.make(floatingActionButtonImagenPerfil, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();

                }
                break;
        }

        // END_INCLUDE(onRequestPermissionsResult)
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            adminViewModel.removeValueEventListener();
        } catch (Exception e) {

        }
        try {
            FirebaseDatabase.getInstance().getReference().child("citas").removeEventListener(valueEventListenerCitasCrazys);
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}