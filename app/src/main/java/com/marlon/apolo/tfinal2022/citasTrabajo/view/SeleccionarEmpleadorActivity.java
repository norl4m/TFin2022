package com.marlon.apolo.tfinal2022.citasTrabajo.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.admin.viewModel.AdminViewModel;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.adapters.SeleccionarEmpleadorListAdapterPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPocData;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.empleadores.viewModel.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.viewModel.TrabajadorViewModel;

import java.util.ArrayList;

public class SeleccionarEmpleadorActivity extends AppCompatActivity {

    private AdminViewModel adminViewModel;
    private EmpleadorViewModel empleadorViewModel;
    private TrabajadorViewModel trabajadorViewModel;
    private Usuario usuarioFrom;
    private SeleccionarEmpleadorListAdapterPoc seleccionarEmpleadorListAdapterPoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_empleador);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewEmpleadores);


        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);

        seleccionarEmpleadorListAdapterPoc = new SeleccionarEmpleadorListAdapterPoc(this);
        recyclerView.setAdapter(seleccionarEmpleadorListAdapterPoc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            adminViewModel.getAdministradorLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(this, administrador -> {
                if (administrador != null) {
                    usuarioFrom = administrador;
                    seleccionarEmpleadorListAdapterPoc.setUsuarioFrom(usuarioFrom);
                }
            });

            empleadorViewModel.getOneEmpleador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(this, empleador -> {
                if (empleador != null) {
                    usuarioFrom = empleador;
                    seleccionarEmpleadorListAdapterPoc.setUsuarioFrom(usuarioFrom);

                }
            });

            empleadorViewModel.getAllEmpleadores().observe(this, empleadors -> {
                if (empleadors != null) {
                    seleccionarEmpleadorListAdapterPoc.setEmpleadors(empleadors);
                }
            });

            trabajadorViewModel.getOneTrabajador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(this, trabajador -> {
                if (trabajador != null) {
                    usuarioFrom = trabajador;
                    seleccionarEmpleadorListAdapterPoc.setUsuarioFrom(usuarioFrom);
                }
            });


            ArrayList<ChatPoc> chatPocsLocos = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference()
                    .child("crazyChats")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            //Log.d(TAG, "onChildAdded");
                            ChatPoc chatPoc = snapshot.getValue(ChatPoc.class);
                            //Log.d(TAG, chatPoc.toString());
                            chatPocsLocos.add(chatPoc);
                            filterByUsers(chatPocsLocos);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            //Log.d(TAG, "onChildChanged");
                            ChatPoc chatPocChanged = snapshot.getValue(ChatPoc.class);
                            for (ChatPoc c : chatPocsLocos) {
                                if (chatPocChanged.getIdRemoteUser().equals(c.getIdRemoteUser())) {
                                    //updateChat(chatPocChanged);
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            ChatPoc chatPocRemoved = snapshot.getValue(ChatPoc.class);
                            for (ChatPoc c : chatPocsLocos) {
                                if (chatPocRemoved.getIdRemoteUser().equals(c.getIdRemoteUser())) {
                                    //removeChat(chatPocRemoved);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }
    }

    private void filterByUsers(ArrayList<ChatPoc> chatPocArrayList) {
        seleccionarEmpleadorListAdapterPoc.setChats(new ArrayList<>());
        ArrayList<ChatPocData> chatPocs = new ArrayList<>();
        for (ChatPoc cp : chatPocArrayList) {

            if (cp.getStateRemoteUser() != null) {
                if (cp.getStateRemoteUser().equals("eliminado")) {

                    ChatPocData chatPocData = new ChatPocData();
                    chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                    chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                    chatPocData.setStateRemoteUser(cp.getStateRemoteUser());
                    chatPocData.setName("Usuario" + " no " + "disponible");
                    chatPocData.setFoto(null);

//                    chatListAdapterPoc.addChat(chatPocData);

                    chatPocs.add(chatPocData);
                    seleccionarEmpleadorListAdapterPoc.setChats(chatPocs);

                }
            } else {
                FirebaseDatabase.getInstance().getReference()
                        .child("administrador")
                        .child(cp.getIdRemoteUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Administrador administrador = snapshot.getValue(Administrador.class);
                                ChatPocData chatPocData = new ChatPocData();
                                chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                                chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                                chatPocData.setStateRemoteUser(cp.getStateRemoteUser());
                                if (administrador != null) {
                                    chatPocData.setName(administrador.getNombre() + " " + administrador.getApellido());
                                    chatPocData.setFoto(administrador.getFotoPerfil());
//                                    Log.d(TAG, chatPocData.toString());
//                                    chatListAdapterPoc.addChat(chatPocData);
                                    chatPocs.add(chatPocData);
                                    seleccionarEmpleadorListAdapterPoc.setChats(chatPocs);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                FirebaseDatabase.getInstance().getReference()
                        .child("trabajadores")
                        .child(cp.getIdRemoteUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Trabajador trabajador = snapshot.getValue(Trabajador.class);
                                ChatPocData chatPocData = new ChatPocData();
                                chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                                chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                                chatPocData.setStateRemoteUser(cp.getStateRemoteUser());
                                if (trabajador != null) {
                                    chatPocData.setName(trabajador.getNombre() + " " + trabajador.getApellido());
                                    chatPocData.setFoto(trabajador.getFotoPerfil());
//                                    Log.d(TAG, chatPocData.toString());
//                                    chatListAdapterPoc.addChat(chatPocData);
                                    chatPocs.add(chatPocData);
                                    seleccionarEmpleadorListAdapterPoc.setChats(chatPocs);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                FirebaseDatabase.getInstance().getReference()
                        .child("empleadores")
                        .child(cp.getIdRemoteUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Empleador empleador = snapshot.getValue(Empleador.class);
                                ChatPocData chatPocData = new ChatPocData();
                                chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                                chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                                chatPocData.setStateRemoteUser(cp.getStateRemoteUser());

                                if (empleador != null) {
                                    chatPocData.setName(empleador.getNombre() + " " + empleador.getApellido());
                                    chatPocData.setFoto(empleador.getFotoPerfil());
//                                    Log.d(TAG, chatPocData.toString());
//                                    chatListAdapterPoc.addChat(chatPocData);
                                    chatPocs.add(chatPocData);
                                    seleccionarEmpleadorListAdapterPoc.setChats(chatPocs);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


        }
    }

}