package com.marlon.apolo.tfinal2022;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.admin.AdminViewModel;
import com.marlon.apolo.tfinal2022.buscador.BuscadorActivity;
import com.marlon.apolo.tfinal2022.config.ConfiguracionActivity;
import com.marlon.apolo.tfinal2022.databinding.ActivityMainNavigationBinding;
import com.marlon.apolo.tfinal2022.individualChat.receiver.EliminarNotificationReceiver;
import com.marlon.apolo.tfinal2022.individualChat.receiver.RespuestaDirectaReceiver;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.NotificacionCustom;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;

public class MainNavigationActivity extends AppCompatActivity {

    private static final String TAG = MainNavigationActivity.class.getSimpleName();
    private static final String ACTION_DELETE_NOTIFICATION = "DELETE_NOT";
    private static final String ACTION_REPLY_NOTIFICATION = "REPLY_NOT";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainNavigationBinding binding;


    private Menu menu;
    private MenuItem
            navLogin,
            navPersonalData,
            navChats,
            navCitas,
            navAuthentication,
            navTrabajadores,
            navEmpleadores,
            navOficios,
            navConfiguraciones,
            navCerrarSesion,
            navEliminarCuenta;
    private boolean mode;
    private Trabajador trabajadorLocal;
    private Usuario usuarioLocal;
    private MenuItem navIniciarSesion;
    private TextView textViewEmailOrPhone;
    private TextView textViewUser;
    private ImageView imageView;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;
    private ArrayList<MensajeNube> mensajeNubes;


    private String CHANNEL_ID = "MESSAGE_NOTIFICATION_CHANNEL";
    private NotificationManager notificationManager;
    private ArrayList<NotificationCompat.Builder> notificationArrayList;
    private ArrayList<Integer> notificationsIds;
    private ArrayList<Chat> chatArrayList;
    private EliminarNotificationReceiver mReceiver;
    private RespuestaDirectaReceiver respuestaDirectaReceiver;
    private ArrayList<NotificacionCustom> notificacionCustoms;
    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private Dialog alertD;
    private DrawerLayout drawer;

    private void listenerNotificacionesDeCitasTrabajo() {
        ArrayList<Cita> citaArrayList = new ArrayList<>();
        ChildEventListener childEventListenerCitasTrabajo = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Cita cita = snapshot.getValue(Cita.class);
                    if (cita != null) {

                        if (cita.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            if (cita.isStateReceive() && cita.isState() && cita.getCalificacion() == 0) {
                                Log.d(TAG, "#################################");
                                Log.d(TAG, "LOKURA");
                                alertD = califTrabajador(cita);
                                alertD.show();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .addChildEventListener(childEventListenerCitasTrabajo);

    }

    public AlertDialog califTrabajador(Cita citaLocal) {


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();


        View promptsView = inflater.inflate(R.layout.card_view_enviar_calificacion, null);


        // set prompts.xml to alertdialog builder
        alert.setView(promptsView);

        final TextView textViewTrabajador = promptsView.findViewById(R.id.textViewEmployee);
        final ImageView imageView = promptsView.findViewById(R.id.imageViewEmployee);
        final RatingBar ratingBar = promptsView.findViewById(R.id.ratingBar);

        textViewTrabajador.setText(citaLocal.getNombreTrabajador());
//        ratingBar.setRating();


        final ImageButton calif1 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton1);
        final ImageButton calif2 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton2);
//        final ImageButton imageButtonAudio = promptsView
//                .findViewById(R.id.imageButtonAudio);
        final ImageButton calif3 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton3);
        final ImageButton calif4 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton4);
        final ImageButton calif5 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton5);

        calif1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, citaLocal.toString());
//                citaLocal.actualizarCita();
                float calif = 1;
                calificarTrabajador(calif, citaLocal);
                alertD.dismiss();
            }
        });
        calif2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 2;
                calificarTrabajador(calif, citaLocal);
                alertD.dismiss();
            }
        });
        calif3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 3;
                calificarTrabajador(calif, citaLocal);
                alertD.dismiss();
            }
        });
        calif4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 4;
                calificarTrabajador(calif, citaLocal);
                alertD.dismiss();
            }
        });
        calif5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 5;
                calificarTrabajador(calif, citaLocal);
                alertD.dismiss();
            }
        });
//        imageButtonAudio.setOnClickListener(clickListenerDialogCustom);

        return alert.create();
    }


    private void calificarTrabajador(Float calif, Cita citaLocal) {
        if (usuarioLocal != null) {
            citaLocal.setCalificacion(calif);
            ((Empleador) usuarioLocal).calificarTrabajador(citaLocal, this);
        }
        Toast.makeText(getApplicationContext(), "Gracias por utilizar nuestros servicios.", Toast.LENGTH_SHORT).show();

    }


    private void removeBlockingNotificationsFromOneUser() {
        editorPref = myPreferences.edit();
        editorPref.putString("idUserBlocking", "");
        editorPref.apply();
        //Toast.makeText(getApplicationContext(), "Usuario bloqueado: \n" + myPreferences.getString("usurioBloqueado", ""), Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

//        startActivity(new Intent(this, PoCActivity.class));
        editorPref = myPreferences.edit();
        myPreferences.getInt("usuario", -1);

        removeBlockingNotificationsFromOneUser();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        binding = ActivityMainNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // getHeaderView permite obtener una referencia a todos los elemento de navegación que se encuentra en el encabezado
        View headerView = navigationView.getHeaderView(0);

//        textViewBienvenido = headerView.findViewById(R.id.textViewBienvenido);
        textViewEmailOrPhone = headerView.findViewById(R.id.textViewEmailCelular);
        imageView = headerView.findViewById(R.id.imageView);
        textViewUser = headerView.findViewById(R.id.textViewNombre);
//        imageViewStatusOnline = headerView.findViewById(R.id.imageViewOnlineStatus);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_datos_personales,
                R.id.nav_chats,
                R.id.nav_citas,
                R.id.nav_auth,
                R.id.nav_trabajadores,
                R.id.nav_empleadores,
                R.id.nav_oficios,
                R.id.nav_cerrar_sesion,
                R.id.nav_eliminar_cuenta,
                R.id.nav_politica_priv,
                R.id.nav_sobre_nos)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        menu = navigationView.getMenu();
        navLogin = menu.findItem(R.id.nav_iniciar_sesion);
        navPersonalData = menu.findItem(R.id.nav_datos_personales);
        navChats = menu.findItem(R.id.nav_chats);
        navCitas = menu.findItem(R.id.nav_citas);
        navAuthentication = menu.findItem(R.id.nav_auth);
        navTrabajadores = menu.findItem(R.id.nav_trabajadores);
        navEmpleadores = menu.findItem(R.id.nav_empleadores);
        navOficios = menu.findItem(R.id.nav_oficios);
//        navConfiguraciones = menu.findItem(R.id.nav_settings);
        navCerrarSesion = menu.findItem(R.id.nav_cerrar_sesion);
        navIniciarSesion = menu.findItem(R.id.nav_iniciar_sesion);
        navEliminarCuenta = menu.findItem(R.id.nav_eliminar_cuenta);

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                if (id == R.id.nav_citas) {
//                    Toast.makeText(getApplicationContext(), "Clickianedo diferentes", Toast.LENGTH_LONG).show();
//                    Intent newIntent = new Intent(MainNavigationActivity.this, CitaTrabajoViewActivity.class);
//                    startActivity(newIntent);
//                    drawer.closeDrawers();
//                }
//                return true;
//            }
//        });

        if (firebaseUser != null) {
            Log.d(TAG, "FIREBASE LOGIN-" + firebaseUser.getUid());
            //Toast.makeText(getApplicationContext(), "AAAAAAAA", Toast.LENGTH_LONG).show();
            setInvitadoUI();
//            setAdminUI();
            setInvitadoHeader();
            filterByFirebaseUserUID(firebaseUser);

            /*Login*/
//            setUserUI();
//            setAdminUI();
        } else {
            setInvitadoUI();
//            setAdminUI();
            setInvitadoHeader();
        }
//        setAdminUI();


//        setInvitadoUI();

        try {
            //notificationListener();
        } catch (Exception e) {

        }


        if (firebaseUser != null) {
//            Intent intent = new Intent(this, ServicioNotificacionCustom.class);
//            Intent intent = new Intent(this, OffLaneService.class);
//            Intent intent = new Intent(this, ForegroundCustomService.class);
            Context context = getApplicationContext();
            //Toast.makeText(getApplicationContext(), "GG SERVICIO", Toast.LENGTH_LONG).show();
            Intent intentPoc = new Intent(this, CrazyService.class); // Build the intent for the service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent);
                startForegroundService(intentPoc);


            } else {
                startService(intentPoc);
//                startService(intent);
            }
        }
    }


    private void setInvitadoHeader() {
        textViewUser.setText(String.format("%s", "Bienvenido"));
        textViewUser.setVisibility(View.VISIBLE);
        textViewEmailOrPhone.setText("");
        textViewEmailOrPhone.setVisibility(View.GONE);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            //deleteLocalUserInEspecialCase();

        } else {

            Glide.with(MainNavigationActivity.this).load(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_oficios)).placeholder(R.drawable.ic_baseline_person_24).into(imageView);
            imageView.setColorFilter(getResources().getColor(R.color.white));

        }


    }

    private void deleteLocalUserInEspecialCase() {

        Log.d(TAG, "Parece q funciona");

    }

    private void filterByFirebaseUserUID(FirebaseUser firebaseUser) {
        Log.d(TAG, "########################");
        Log.d(TAG, "FILTRANDO USUARIO POR UID");
        Log.d(TAG, "filter by user: " + firebaseUser.getUid());
        Log.d(TAG, "########################");

//        FirebaseDatabase.getInstance().getReference()
//                .child("trabajadores")
//                .child(firebaseUser.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                            if (trabajador != null) {
//                                Log.d(TAG, "########################");
//                                Log.d(TAG, "TRABAJADOR");
//                                Log.d(TAG, trabajador.toString());
//                                Log.d(TAG, "########################");
//                                setTrabajadorUI();
//                            }
//
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("empleadores")
//                .child(firebaseUser.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Empleador empleador = snapshot.getValue(Empleador.class);
//                            if (empleador != null) {
//                                Log.d(TAG, "########################");
//                                Log.d(TAG, "EMPLEADOR");
//                                Log.d(TAG, empleador.toString());
//                                Log.d(TAG, "########################");
//                                setEmpleadorUI();
//                            }
//
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("administrador")
//                .child(firebaseUser.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Administrador administrador = snapshot.getValue(Administrador.class);
//                            if (administrador != null) {
//                                Log.d(TAG, "########################");
//                                Log.d(TAG, "ADMINISTRADOR");
//                                Log.d(TAG, administrador.toString());
//                                Log.d(TAG, "########################");
//                                setAdminUI();
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getOneTrabajador(firebaseUser.getUid()).observe(this, trabajador -> {
            if (trabajador != null) {
                Log.d(TAG, "########################");
                Log.d(TAG, "TRABAJADOR");
                Log.d(TAG, trabajador.toString());
                Log.d(TAG, "########################");
                usuarioLocal = trabajador;
                loadLocalInfoHeader(usuarioLocal);
                setTrabajadorUI();
                editorPref.putInt("usuario", 2);
                editorPref.apply();
            }
        });
        EmpleadorViewModel empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getOneEmpleador(firebaseUser.getUid()).observe(this, empleador -> {
            if (empleador != null) {
                Log.d(TAG, "########################");
                Log.d(TAG, "EMPLEADOR");
                Log.d(TAG, empleador.toString());
                Log.d(TAG, "########################");
                usuarioLocal = empleador;
                loadLocalInfoHeader(usuarioLocal);
                setEmpleadorUI();
                editorPref.putInt("usuario", 1);
                editorPref.apply();
                listenerNotificacionesDeCitasTrabajo();
            }
        });

        AdminViewModel adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.getAdministradorLiveData(firebaseUser.getUid()).observe(this, administrador -> {
            if (administrador != null) {
                Log.d(TAG, "########################");
                Log.d(TAG, "ADMINISTRADOR");
                Log.d(TAG, administrador.toString());
                Log.d(TAG, "########################");
                usuarioLocal = administrador;
                loadLocalInfoHeader(usuarioLocal);
                setAdminUI();
                editorPref.putInt("usuario", 0);


                editorPref.apply();
            }
        });
    }

    private void loadLocalInfoHeader(Usuario usuario) {
        if (usuario.getFotoPerfil() != null) {
            Glide.with(MainNavigationActivity.this).load(usuario.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageView);
        } else {
            Glide.with(MainNavigationActivity.this).load(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_person_24)).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(imageView);

        }
        if (usuario.getNombre() != null && usuario.getApellido() != null) {
            textViewUser.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido()));
        }
        if (usuario.getEmail() != null) {
            textViewEmailOrPhone.setText(usuario.getEmail());
            textViewEmailOrPhone.setVisibility(View.VISIBLE);
        }
        if (usuario.getCelular() != null) {
            textViewEmailOrPhone.setText(usuario.getCelular());
            textViewEmailOrPhone.setVisibility(View.VISIBLE);
        }
    }

    private void setAdminUI() {
        navPersonalData.setVisible(true);
        navChats.setVisible(true);
        navCitas.setVisible(false);
        navAuthentication.setVisible(true);
        navTrabajadores.setVisible(true);
        navEmpleadores.setVisible(true);
        navOficios.setVisible(true);
        navCerrarSesion.setVisible(true);
        navIniciarSesion.setVisible(true);
        navIniciarSesion.setVisible(false);
        navEliminarCuenta.setVisible(true);
    }

    public void setTrabajadorUI() {
        navPersonalData.setVisible(true);
        navChats.setVisible(true);
        navCitas.setVisible(true);
        navAuthentication.setVisible(false);
        navTrabajadores.setVisible(false);
        navEmpleadores.setVisible(false);
        navOficios.setVisible(false);
        navCerrarSesion.setVisible(true);
        navIniciarSesion.setVisible(false);
        navEliminarCuenta.setVisible(true);
    }

    public void setEmpleadorUI() {
        navPersonalData.setVisible(true);
        navChats.setVisible(true);
        navCitas.setVisible(true);
        navAuthentication.setVisible(false);
        navTrabajadores.setVisible(false);
        navEmpleadores.setVisible(false);
        navOficios.setVisible(false);
        navCerrarSesion.setVisible(true);
        navIniciarSesion.setVisible(false);
        navEliminarCuenta.setVisible(true);
    }


    public void setInvitadoUI() {
        navPersonalData.setVisible(false);
        navChats.setVisible(false);
        navCitas.setVisible(false);
        navAuthentication.setVisible(false);
        navTrabajadores.setVisible(false);
        navEmpleadores.setVisible(false);
        navOficios.setVisible(false);
        navCerrarSesion.setVisible(false);
        navEliminarCuenta.setVisible(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainNavigationActivity.this, ConfiguracionActivity.class));
//                startActivity(new Intent(MainNavigationActivity.this, SettingsActivity.class));
                break;
            case R.id.mnu_nav_search:
                startActivity(new Intent(MainNavigationActivity.this, BuscadorActivity.class));
                break;
//            case R.id.action_app_config:
//                Intent i = new Intent(ACTION_WIRELESS_SETTINGS);
////                startActivity(i);
//
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivity(intent);
//                break;
//            case R.id.nav_cita:
//                Toast.makeText(getApplicationContext(), "Clickianedo diferentes", Toast.LENGTH_LONG).show();
//                Intent newIntent = new Intent(MainNavigationActivity.this, CitaTrabajoViewActivity.class);
//                startActivity(newIntent);
//                drawer.closeDrawers();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            drawer.closeDrawers();
        } catch (Exception e) {

        }
    }
}