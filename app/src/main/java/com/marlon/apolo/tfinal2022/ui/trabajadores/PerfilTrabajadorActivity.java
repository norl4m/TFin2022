package com.marlon.apolo.tfinal2022.ui.trabajadores;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioVistaListAdapter;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import java.util.ArrayList;

public class PerfilTrabajadorActivity extends AppCompatActivity {

    private static final String TAG = PerfilTrabajadorActivity.class.getSimpleName();
    private TextView textViewNombre, textViewEmail, textViewPhone, textViewCalif;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private RatingBar ratingBar;
    private LinearLayout linearLayoutEmail, linearLayoutPhone;
    private Trabajador trabajadorSelected;
    private static final int PERMISSIONS_REQUEST_CAMERA_AND_AUDIO = 1000;
    private static final int PERMISSION_REQUEST_AUDIO = 1001;
    private Chat chat;
    private Usuario usuarioLocal;


    private void loadLocalUser() {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                usuarioLocal = administrador;
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void startIndividualChatActivity(Trabajador trabajador) {

//        Intent mensajitoIntent = new Intent(PerfilTrabajadorActivity.this, IndividualChatActivity.class);
        Intent mensajitoIntent = new Intent(PerfilTrabajadorActivity.this, CrazyIndividualChatActivity.class);
        mensajitoIntent.putExtra("trabajador", trabajador);
        mensajitoIntent.putExtra("usuarioFrom", usuarioLocal);
//        mensajitoIntent.putExtra("chat", chat);

//        mensajitoIntent.putExtra("idUsuario", trabajador.getIdUsuario());
//        mensajitoIntent.putExtra("nombre", trabajador.getNombre() + " " + trabajador.getApellido());

        startActivity(mensajitoIntent);


    }

    private void showVideoCamActivity() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            startVideoCall(trabajadorSelected);
        } else {
            // Permission is missing and must be requested.
            requestCameraAndAudioPermissions();
        }
        // END_INCLUDE(startCamera)
    }

    private void startVideoCall(Trabajador trabajador) {
        //                Intent videoCall = new Intent(this, CallActivity.class);
//        Intent videoCall = new Intent(this, VideoLlamadaActivity.class);
//        String videoCallId = FirebaseDatabase.getInstance().getReference().child("videoLlamadas").push().getKey();
//        videoCall.putExtra("chatID", videoCallId);
//        videoCall.putExtra("contactTo", trabajador.getIdUsuario());
//        videoCall.putExtra("nameTo", trabajador.getNombre() + " " + trabajador.getApellido());
//
//
//        startActivity(videoCall);

        Intent intentVideollamada = new Intent(this, VideoLlamadaActivity.class);
        intentVideollamada.putExtra("usuarioTo", (Usuario) trabajadorSelected);
        intentVideollamada.putExtra("usuarioFrom", usuarioLocal);
        intentVideollamada.putExtra("callStatus", 0);
        startActivity(intentVideollamada);
    }

    private void requestCameraAndAudioPermissions() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(findViewById(R.id.textViewNombreUsuario), "Los permiss de cámara y audio son necesarios!",
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(PerfilTrabajadorActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},

                            PERMISSIONS_REQUEST_CAMERA_AND_AUDIO);
                }
            }).show();

        } else {
            Snackbar.make(findViewById(R.id.textViewNombreUsuario), "Cámara y audio no disponibles", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_CAMERA_AND_AUDIO);
        }
    }

    private void startVoiceCall(Trabajador trabajador) {
//        Intent call = new Intent(this, LlamadaVozActivity.class);
//        String callId = FirebaseDatabase.getInstance().getReference().child("videoLlamadas").push().getKey();
//        call.putExtra("chatID", callId);
//        call.putExtra("contactTo", trabajador.getIdUsuario());
//        call.putExtra("nameTo", trabajador.getNombre() + " " + trabajador.getApellido());
//        startActivity(call);
//


        Intent intent = new Intent(this, LlamadaVozActivity.class);
        intent.putExtra("usuarioTo", (Usuario) trabajadorSelected);
        intent.putExtra("usuarioFrom", usuarioLocal);
        intent.putExtra("callStatus", 0
        );
        startActivity(intent);
//
//        try {
//            dialogVar.dismiss();
//        } catch (Exception e) {
//
//        }
    }


    private void showVoiceCallActivity() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            startVoiceCall(trabajadorSelected);
        } else {
            // Permission is missing and must be requested.
            requestAudioToVoiceCallPermission();
        }
    }


    private void requestAudioToVoiceCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(findViewById(R.id.textViewNombreUsuario), "Permiso de micrófono es necesario!",
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(PerfilTrabajadorActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_AUDIO);
                }
            }).show();

        } else {
            Snackbar.make(findViewById(R.id.textViewNombreUsuario), "Micrófono no disponible", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA_AND_AUDIO:
                // Request for camera permission.
                if (grantResults.length == 2 &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED
                                && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    startVideoCall(trabajadorSelected);
                } else {
                    // Permission request was denied.
                    Snackbar.make(findViewById(R.id.textViewNombreUsuario), "Permisos de cámara y audio denegados",
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;

            case PERMISSION_REQUEST_AUDIO:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startVoiceCall(trabajadorSelected);
                } else {
                    Snackbar.make(findViewById(R.id.textViewNombreUsuario), "Permiso de micrófono denegado",
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
        }
//        if (!permissionToRecordAccepted ) finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_trabajador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewNombre = findViewById(R.id.textViewNombreUsuario);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewCalif = findViewById(R.id.textViewCalif);
        imageView = findViewById(R.id.imageViewUsuario);
        recyclerView = findViewById(R.id.recyclerViewOficiosReg);
        ratingBar = findViewById(R.id.ratingBar);

        linearLayoutEmail = findViewById(R.id.linLytEmail);
        linearLayoutEmail.setVisibility(View.GONE);
        linearLayoutPhone = findViewById(R.id.linLytPhone);
        linearLayoutPhone.setVisibility(View.GONE);
        textViewEmail.setVisibility(View.GONE);
        textViewPhone.setVisibility(View.GONE);

        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        String id = getIntent().getStringExtra("idTrabajador");
        chat = (Chat) getIntent().getSerializableExtra("chat");
//        Toast.makeText(getApplicationContext(), chat.toString(), Toast.LENGTH_SHORT).show();

        loadLocalUser();

        trabajadorViewModel.getOneTrabajador(id).observe(this, new Observer<Trabajador>() {
            @Override
            public void onChanged(Trabajador trabajador) {
                if (trabajador != null) {
//                    Toast.makeText(getApplicationContext(), "TRABAJADOR", Toast.LENGTH_SHORT).show();
                    trabajadorSelected = trabajador;
                    textViewNombre.setText(trabajador.getNombre() + " " + trabajador.getApellido());
                    try {
                        if (!trabajador.getFotoPerfil().isEmpty()) {
                            //byte[] encodeByte = Base64.decode(trabajador.getFotoPerfil(), Base64.DEFAULT);
                            //Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);


                            //Glide.with(PerfilTrabajadorActivity.this)
                            //      .load(trabajador.getFotoPerfil())
                            //    .into(imageView);

                            Glide
                                    .with(PerfilTrabajadorActivity.this)
                                    .load(trabajador.getFotoPerfil())
                                    .apply(new RequestOptions().override(300, 400))
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imageView);


                        }


                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    if (trabajador.getEmail() != null) {
                        textViewEmail.setText(String.format("%s", trabajador.getEmail()));
                        textViewEmail.setVisibility(View.VISIBLE);
                        textViewPhone.setVisibility(View.GONE);
                        linearLayoutEmail.setVisibility(View.VISIBLE);
                        linearLayoutPhone.setVisibility(View.GONE);
                    }
                    if (trabajador.getCelular() != null) {

                        textViewPhone.setText(String.format("%s", trabajador.getCelular()));
                        textViewEmail.setVisibility(View.GONE);
                        textViewPhone.setVisibility(View.VISIBLE);

                        linearLayoutEmail.setVisibility(View.GONE);
                        linearLayoutPhone.setVisibility(View.VISIBLE);
                    }

                    ratingBar.setRating((float) trabajador.getCalificacion());

                    textViewCalif.setText(String.format("Calificación: %.2f / 5.00", trabajador.getCalificacion()));
                    oficioViewModel.getAllOficios().observe(PerfilTrabajadorActivity.this, oficios -> {
                        ArrayList<Oficio> oficioArrayList = new ArrayList<>();
                        OficioHabilidadVistaListAdapter oficioListAdapter = new OficioHabilidadVistaListAdapter(PerfilTrabajadorActivity.this);
                        recyclerView.setAdapter(oficioListAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PerfilTrabajadorActivity.this));
//                            oficioListAdapter.setOficios(oficioArrayList);

                        for (Oficio of : oficios) {
                            for (String idOf : trabajador.getIdOficios()) {
                                if (idOf.equals(of.getIdOficio())) {
                                    of.setEstadoRegistro(true);
                                    oficioArrayList.add(of);
                                }
                            }


                        }

                        for (Oficio of : oficioArrayList) {
                            FirebaseDatabase.getInstance().getReference().child("habilidades")
                                    .child(of.getIdOficio())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();

                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                try {
                                                    Habilidad h = data.getValue(Habilidad.class);
                                                    if (trabajador.getIdHabilidades() != null) {
                                                        try {
                                                            for (String idH : trabajador.getIdHabilidades()) {
                                                                if (idH.equals(h.getIdHabilidad())) {
                                                                    h.setHabilidadSeleccionada(true);
                                                                    habilidadArrayList.add(h);

                                                                }
                                                            }
//                                                            if (trabajador.getIdHabilidades().contains(h.getIdHabilidad())) {
//                                                                h.setHabilidadSeleccionada(true);
//                                                            }
                                                        } catch (Exception e) {

                                                        }

                                                    }

//                                                    habilidadArrayList.add(h);
                                                } catch (Exception e) {

                                                }
                                            }
                                            of.setHabilidadArrayList(habilidadArrayList);
                                            oficioListAdapter.setOficios(oficioArrayList);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }


                    });

//                    oficioViewModel.getAllOficios().observe(PerfilTrabajadorActivity.this, new Observer<ArrayList<Oficio>>() {
//                        @Override
//                        public void onChanged(ArrayList<Oficio> oficios) {
//                            ArrayList<Oficio> oficioArrayList = new ArrayList<>();
//                            OficioVistaListAdapter oficioListAdapter = new OficioVistaListAdapter(PerfilTrabajadorActivity.this);
//                            recyclerView.setAdapter(oficioListAdapter);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(PerfilTrabajadorActivity.this));
////                            oficioListAdapter.setOficios(oficioArrayList);
//
//                            for (Oficio of : oficios) {
//                                for (String idOf : trabajador.getIdOficios()) {
//                                    if (idOf.equals(of.getIdOficio())) {
//                                        of.setEstadoRegistro(true);
//                                        oficioArrayList.add(of);
//                                    }
//                                }
//
//                                FirebaseDatabase.getInstance().getReference().child("habilidades")
//                                        .child(of.getIdOficio())
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                ArrayList<Habilidad> habilidadArrayList = new ArrayList<>();
//
//                                                for (DataSnapshot data : snapshot.getChildren()) {
//                                                    try {
//                                                        Habilidad h = data.getValue(Habilidad.class);
//                                                        if (trabajador.getIdHabilidades() != null) {
//                                                            if (trabajador.getIdHabilidades().contains(h.getIdHabilidad())) {
//                                                                h.setHabilidadSeleccionada(true);
//                                                            }
//                                                        }
//
//                                                        habilidadArrayList.add(h);
//                                                    } catch (Exception e) {
//
//                                                    }
//                                                }
//                                                of.setHabilidadArrayList(habilidadArrayList);
//                                                oficioListAdapter.setOficios(oficioArrayList);
//
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                            }
//
//                            for (Oficio of : oficioArrayList) {
//                                Log.e(TAG, of.toString());
//                            }
////
////
//                            oficioListAdapter.setOnItemClickListener(new OficioListAdapter.ClickListener() {
//                                @Override
//                                public void onItemClick(View v, int position) {
//                                    Oficio oficio = oficioListAdapter.getJobAtPosition(position);
//                                    Log.e("TAG", oficio.toString());
//                                    Intent intent = new Intent(PerfilTrabajadorActivity.this, SearcherActivity.class);
//                                    intent.setAction("android.intent.action.SEARCH");
//                                    intent.putExtra(SearchManager.QUERY, oficio.getNombre());
//                                    startActivity(intent);
//                                }
//                            });
//                        }
//                    });

                    //ratingBar.setRating((float) trabajador.getCalificacion());
//                    ratingBar.setRating((float) trabajador.getCalificacion());
                }
            }
        });

        empleadorViewModel.getOneEmpleador(id).observe(this, empleador -> {
            if (empleador != null) {
                textViewCalif.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                textViewNombre.setText(empleador.getNombre() + " " + empleador.getApellido());

                if (!empleador.getFotoPerfil().isEmpty()) {

                    Glide
                            .with(PerfilTrabajadorActivity.this)
                            .load(empleador.getFotoPerfil())
                            .apply(new RequestOptions().override(300, 400))
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView);
                }

                if (empleador.getEmail().contains(".com") || empleador.getEmail().contains(".ec")) {
                    textViewEmail.setText(String.format("%s", empleador.getEmail()));
                    textViewEmail.setVisibility(View.VISIBLE);
                    textViewPhone.setVisibility(View.GONE);
                    linearLayoutEmail.setVisibility(View.VISIBLE);
                    linearLayoutPhone.setVisibility(View.GONE);
                } else {
                    textViewPhone.setText(String.format("%s", empleador.getEmail()));
                    textViewEmail.setVisibility(View.GONE);
                    textViewPhone.setVisibility(View.VISIBLE);

                    linearLayoutEmail.setVisibility(View.GONE);
                    linearLayoutPhone.setVisibility(View.VISIBLE);
                }

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options_trabajador, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_chat:
                //Toast.makeText(getApplicationContext(), trabajadorSelected.toString(), Toast.LENGTH_LONG).show();
                startIndividualChatActivity(trabajadorSelected);
                break;
            case R.id.mnu_llamada:
                showVoiceCallActivity();
                break;
            case R.id.mnu_videollamada:
                showVideoCamActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}