package com.marlon.apolo.tfinal2022.ui.bienvenido.view;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.admin.AdminViewModel;
import com.marlon.apolo.tfinal2022.citasTrabajo.adapters.CitaListAdapter;
import com.marlon.apolo.tfinal2022.citasTrabajo.viewModel.CitaViewModel;
import com.marlon.apolo.tfinal2022.communicationAgora.video.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.AgoraOnlyVoiceCallActivity;
import com.marlon.apolo.tfinal2022.databinding.FragmentBienvenidoBinding;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.bienvenido.adaptadores.OficioArchiVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.chats.ChatViewModel;
import com.marlon.apolo.tfinal2022.ui.citaTrabajo.CitaTrabajoViewActivity;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel.OficioArchiViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.adaptadores.OficioRegistroCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.PerfilTrabajadorActivity;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.adaptadores.TrabajadorVistaEmpleadorListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BienvenidoFragment extends Fragment {

    private static final String TAG = BienvenidoFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_ONLY_MIC = 8000;
    private BienvenidoViewModel bienvenidoViewModel;
    private EmpleadorViewModel empleadorViewModel;
    private FragmentBienvenidoBinding binding;
    private List<Oficio> oficiosLocales;
    private TrabajadorViewModel trabajadorViewModel;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;
    private Usuario usuarioFrom;
    private OficioViewModel oficioViewModel;
    private AdminViewModel adminViewModel;
    private Usuario usuarioLocal;
    private TrabajadorListAdapter trabajadorListAdapter;
    private CitaViewModel mViewModel;
    private CitaListAdapter citaListAdapter;
    private ArrayList<Trabajador> trabajadorArrayList;
    private Dialog alertD;
    private AlertDialog dialogVar;
    private List<Chat> chatList;
    private View root;
    private Trabajador trabajadorAtPosition;
    private TrabajadorVistaEmpleadorListAdapter trabajadorVistaEmpleadorListAdapter;
    private OficioArchiViewModel oficioArchiViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        mViewModel = new ViewModelProvider(this).get(CitaViewModel.class);

        binding = FragmentBienvenidoBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        final TextView textView = binding.textViewApp;
//        bienvenidoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        myPreferences = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);

        editorPref = myPreferences.edit();
        int u = myPreferences.getInt("usuario", -1);
        //Toast.makeText(getContext(),String.valueOf(u),Toast.LENGTH_LONG).show();

        if (u != 0) {
            setListenerTextViews(root);
        }
        root.findViewById(R.id.relativeLayout1).setVisibility(View.GONE);/*Trabajadores*/
        root.findViewById(R.id.relativeLayout2).setVisibility(View.GONE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout3).setVisibility(View.GONE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.GONE);/*Citas de trabajo*/


        loadOficios(root);
//        loadOficiosSpecial(root);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        trabajadorListAdapter = new TrabajadorListAdapter(requireActivity());


//        setInvitadoUI(root);
        if (firebaseUser != null) {
            root.findViewById(R.id.relativeLayout3).setVisibility(View.GONE);/*Trabajadores*/

            ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
            chatViewModel.getAllChats().observe(getViewLifecycleOwner(), chats -> {
                trabajadorListAdapter.setChatList(chats);
                chatList = chats;
            });

            loadLocalUser(root, firebaseUser);
//            loadTrabajadoresYOficios(root);
        } else {
            setInvitadoUI(root);
        }


//        /*Carga de datos trabajadores*/
//        RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
//        ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
//        TrabajadorListAdapter trabajadorListAdapter = new TrabajadorListAdapter(requireActivity(), (ArrayList<Oficio>) oficiosLocales);
//        recyclerView1.setAdapter(trabajadorListAdapter);
//        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
//        trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
//            if (trabajadors != null) {
//                trabajadorListAdapter.setTrabajadores(trabajadors);
//                progressBar1.setVisibility(View.GONE);
//            }
//        });
//        /*Carga de datos trabajadores*/


        return root;
    }

    private void loadOficiosWithArchi(View root) {
        RecyclerView recyclerView3 = root.findViewById(R.id.fragHomeRecyclerView3);
        recyclerView3.setVisibility(View.GONE);
        ProgressBar progressBar3 = root.findViewById(R.id.fragHomeProgressBar3);

        OficioArchiVistaListAdapter oficioArchiVistaListAdapter = new OficioArchiVistaListAdapter(requireActivity());
        recyclerView3.setAdapter(oficioArchiVistaListAdapter);
//        recyclerView3.setLayoutManager(new LinearLayoutManager(requireActivity()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(layoutManager);

//        int numberOfElementsToShow = 4;
//        int oneElementHeight = 200;
//
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//        recyclerView3.setLayoutParams(lp);


//        int numberOfElementsToShow = 5;
//        int oneElementHeight = 175;
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//        recyclerView3.setLayoutParams(lp);

        bienvenidoViewModel.getAllOficios().observe(requireActivity(), oficios -> {
            if (oficios != null) {
                Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));


                if (oficios.size() > 2) {

                    int numberOfElementsToShow = 5;
                    int oneElementHeight = 175;

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);

                    } else {
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                    }
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    recyclerView3.setLayoutParams(lp);

                } else {
                    recyclerView3.setLayoutManager(new LinearLayoutManager(requireActivity()));
                }

                oficioArchiVistaListAdapter.setOficios(oficios);
                progressBar3.setVisibility(View.GONE);
                recyclerView3.setVisibility(View.VISIBLE);

            }
        });

    }

    private void setListenerTextViews(View root) {

        root.findViewById(R.id.fragHomeTextView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOficios = new Intent(requireActivity(), OficiosActivityVista.class);
                startActivity(intentOficios);
            }
        });

        root.findViewById(R.id.fragHomeTextView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTrabajadores = new Intent(requireActivity(), TrabajadoresActivityVista.class);
                startActivity(intentTrabajadores);
            }
        });

        root.findViewById(R.id.fragHomeTextView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCitasTrabajo = new Intent(requireActivity(), CitaTrabajoViewActivity.class);
                startActivity(intentCitasTrabajo);
            }
        });

    }

    private void loadOficios(View root) {
        root.findViewById(R.id.relativeLayout3).setVisibility(View.VISIBLE);/*Empleadores*/

        RecyclerView recyclerView3 = root.findViewById(R.id.fragHomeRecyclerView3);
        recyclerView3.setVisibility(View.GONE);
        ProgressBar progressBar3 = root.findViewById(R.id.fragHomeProgressBar3);

        OficioVistaListAdapter oficioVistaListAdapter = new OficioVistaListAdapter(requireActivity());
        recyclerView3.setAdapter(oficioVistaListAdapter);
//        recyclerView3.setLayoutManager(new LinearLayoutManager(requireActivity()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(layoutManager);

//        int numberOfElementsToShow = 4;
//        int oneElementHeight = 200;
//
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//        recyclerView3.setLayoutParams(lp);


//        int numberOfElementsToShow = 5;
//        int oneElementHeight = 175;
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//        recyclerView3.setLayoutParams(lp);

        bienvenidoViewModel.getAllOficios().observe(requireActivity(), oficios -> {
            if (oficios != null) {
                Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));


                if (oficios.size() > 2) {

                    int numberOfElementsToShow = 5;
                    int oneElementHeight = 175;

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);

                    } else {
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                    }
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    recyclerView3.setLayoutParams(lp);

                } else {
                    recyclerView3.setLayoutManager(new LinearLayoutManager(requireActivity()));
                }

                oficioVistaListAdapter.setOficios(oficios);
                progressBar3.setVisibility(View.GONE);
                recyclerView3.setVisibility(View.VISIBLE);

            }
        });

    }


    public void loadCitas(View root) {
//        Toast.makeText(getContext(), "Load Citas", Toast.LENGTH_LONG).show();
        RecyclerView recyclerView4 = root.findViewById(R.id.fragHomeRecyclerView4);
        ProgressBar progressBar4 = root.findViewById(R.id.fragHomeProgressBar4);
//        recyclerView4.setVisibility(View.GONE);

        citaListAdapter = new CitaListAdapter(requireActivity());
        recyclerView4.setAdapter(citaListAdapter);

        recyclerView4.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        int numberOfElementsToShow = 4;
//        int oneElementHeight = 294;
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
//        recyclerView4.setLayoutParams(lp);


//        FirebaseDatabase.getInstance().getReference()
//                .child("citas")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Log.d(TAG, "Citas de trabajo totales: " + String.valueOf(snapshot.getChildrenCount()));
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Cita cita = data.getValue(Cita.class);
//                            Log.d(TAG, cita.toString());
//                        }
//                        if (snapshot.getChildrenCount() > 0) {
//
//                        } else {
//                            Toast.makeText(getContext(), "No existen resultados", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        mViewModel
                .getCitas()
                .observe(this, new Observer<ArrayList<Cita>>() {
                    @Override
                    public void onChanged(ArrayList<Cita> citas) {
                        if (citas != null) {
                            if (citas.size() > 2) {
                                progressBar4.setVisibility(View.GONE);
//                                citaListAdapter.setCitas(citas);
                                recyclerView4.setVisibility(View.VISIBLE);
                                int numberOfElementsToShow = 4;
                                int oneElementHeight = 294;
//                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                recyclerView4.setLayoutParams(lp);

                            } else {
//                                Log.d("TAG", "no existen resultados");
//                                progressBar4.setVisibility(View.GONE);
//                                recyclerView4.setVisibility(View.GONE);

                            }
                            citaListAdapter.setCitas(citas);

                        }
//                        else {
//                            Log.d("TAG", "no existen resultados");
//                            Toast.makeText(getContext(), "No existen resultados", Toast.LENGTH_LONG).show();
//                            progressBar4.setVisibility(View.GONE);
//                            recyclerView4.setVisibility(View.GONE);
//                        }
                    }

                });
    }


    public void loadEmpleadoresAdmin(View root) {
        /*Carga de datos empleadores*/
        RecyclerView recyclerView = root.findViewById(R.id.fragHomeRecyclerView2);
        ProgressBar progressBar2 = root.findViewById(R.id.fragHomeProgressBar2);
        final EmpleadorCRUDListAdapter adapter = new EmpleadorCRUDListAdapter(requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));


        empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getAllEmpleadores().observe(getViewLifecycleOwner(), empleadors -> {
            if (empleadors != null) {
                //recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

                if (empleadors.size() > 2) {

                    int numberOfElementsToShow = 2;
                    int oneElementHeight = 480;

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
                    recyclerView.setLayoutParams(lp);

                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                }

                adapter.setEmpleadores(empleadors);
                progressBar2.setVisibility(View.GONE);
            }
        });
        /*Carga de datos empleadores*/
    }

    private void loadTrabajadoresYOficios(View root) {
        /*Carga de datos oficios*/
        RecyclerView recyclerView3 = root.findViewById(R.id.fragHomeRecyclerView3);
        ProgressBar progressBar3 = root.findViewById(R.id.fragHomeProgressBar3);
        OficioVistaListAdapter oficioVistaListAdapter = new OficioVistaListAdapter(requireActivity());
        recyclerView3.setAdapter(oficioVistaListAdapter);
        recyclerView3.setLayoutManager(new LinearLayoutManager(requireActivity()));
        oficioViewModel.getAllOficios().observe(requireActivity(), oficios -> {
            if (oficios != null) {
                oficioVistaListAdapter.setOficios(oficios);
                progressBar3.setVisibility(View.GONE);
                oficiosLocales = oficios;
            }
            /*Carga de datos trabajadores*/
            RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
            ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);

            TrabajadorListAdapter trabajadorListAdapter = new TrabajadorListAdapter(requireActivity(), (ArrayList<Oficio>) oficiosLocales);
            recyclerView1.setAdapter(trabajadorListAdapter);
            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
            try {
                trabajadorViewModel.getAllTrabajadores().observe(requireActivity(), trabajadors -> {
                    if (trabajadors != null) {

                        trabajadorListAdapter.setTrabajadores(trabajadors);
                        progressBar1.setVisibility(View.GONE);

                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            adminViewModel.getAdministradorLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), administrador -> {
                                if (administrador != null) {
                                    usuarioFrom = administrador;
                                    trabajadorListAdapter.setUsuarioFrom(usuarioFrom);
                                    trabajadorVistaEmpleadorListAdapter.setUsuarioLocal(usuarioFrom);
                                    //Toast.makeText(requireActivity(),administrador.toString(),Toast.LENGTH_LONG).show();
                                }
                            });

                            empleadorViewModel.getOneEmpleador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), empleador -> {
                                if (empleador != null) {
                                    usuarioFrom = empleador;
                                    trabajadorListAdapter.setUsuarioFrom(usuarioFrom);
                                    trabajadorVistaEmpleadorListAdapter.setUsuarioLocal(usuarioFrom);

                                    // Toast.makeText(requireActivity(),empleador.toString(),Toast.LENGTH_LONG).show();

                                }
                            });

                            trabajadorViewModel.getOneTrabajador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), trabajador -> {
                                if (trabajador != null) {
                                    usuarioFrom = trabajador;
                                    trabajadorListAdapter.setUsuarioFrom(usuarioFrom);
                                    trabajadorVistaEmpleadorListAdapter.setUsuarioLocal(usuarioFrom);

                                    // Toast.makeText(requireActivity(),trabajador.toString(),Toast.LENGTH_LONG).show();

                                }
                            });
                        }


                        ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
                        chatViewModel.getAllChats().observe(getViewLifecycleOwner(), chats -> {
                            if (chats != null) {
                                trabajadorListAdapter.setChatList(chats);
                                //trabajadorListAdapter.setUsuarioFrom(usuarioFrom);

                            }
                        });
                    }
                });

            } catch (Exception e) {

            }
            /*Carga de datos trabajadores*/


        });
        /*Carga de datos oficios*/
    }

    private void setInvitadoUI(View root) {
//        loadTrabajadoresYOficios(root);
//        loadTrabajadores(root);
        root.findViewById(R.id.relativeLayout1).setVisibility(View.GONE);/*Trabajadores*/
//        loadTrabajadoresVista(root);
        root.findViewById(R.id.relativeLayout2).setVisibility(View.GONE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.GONE);/*Citas de trabajo*/
    }

    private void loadLocalUser(View root, FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                usuarioLocal = administrador;
                                trabajadorListAdapter.setUsuarioFrom(usuarioLocal);
                                setAdminUI(root);
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
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                                trabajadorListAdapter.setUsuarioFrom(usuarioLocal);
                                setEmpleadorUI(root);

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
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                                trabajadorListAdapter.setUsuarioFrom(usuarioLocal);
                                setTrabajadorUI(root);

                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setTrabajadorUI(View root) {
        root.findViewById(R.id.relativeLayout1).setVisibility(View.GONE);/*Trabajadores*/
        root.findViewById(R.id.relativeLayout2).setVisibility(View.GONE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout3).setVisibility(View.GONE);/*Oficios*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.VISIBLE);/*Citas de trabajo*/

        loadCitas(root);
    }

    private void setEmpleadorUI(View root) {
        root.findViewById(R.id.relativeLayout3).setVisibility(View.VISIBLE);/*Trabajadores*/

        loadOficiosWithArchi(root);
        listenerNotificacionesDeCitasTrabajo();
        root.findViewById(R.id.relativeLayout1).setVisibility(View.VISIBLE);/*Trabajadores*/
        root.findViewById(R.id.relativeLayout2).setVisibility(View.GONE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.GONE);/*Citas de trabajo*/
//        loadTrabajadores(root);
        loadTrabajadoresVista(root);
//        loadTrabajadoresVistaDefinitiva(root);

    }

    private void loadTrabajadoresVistaDefinitiva(View root) {
        Toast.makeText(requireActivity(), "vISTA DEFINITIVA", Toast.LENGTH_LONG).show();
        trabajadorVistaEmpleadorListAdapter = new TrabajadorVistaEmpleadorListAdapter(requireActivity());
        /*Carga de datos oficios*/
        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(getViewLifecycleOwner(), oficios -> {

            /*Carga de datos trabajadores*/
            RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
            ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
//            TrabajadorCRUDListAdapter trabajadorListAdapter = new TrabajadorCRUDListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);

//             trabajadorListAdapter = new TrabajadorListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);

//            trabajadorListAdapter.setOficioList(oficios);
            trabajadorVistaEmpleadorListAdapter.setOficioList(oficios);
//            recyclerView1.setAdapter(trabajadorListAdapter);
            recyclerView1.setAdapter(trabajadorVistaEmpleadorListAdapter);
//            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));


            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
            trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
                if (trabajadors != null) {

                    Collections.sort(trabajadors, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

                    if (trabajadors.size() > 2) {
                        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        int numberOfElementsToShow = 3;
                        int oneElementHeight = 380;

                        RelativeLayout.LayoutParams lp =
                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
                        recyclerView1.setLayoutParams(lp);

                    } else {
                        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    }


//                    trabajadorListAdapter.setTrabajadores(trabajadors);
                    trabajadorVistaEmpleadorListAdapter.setTrabajadores(trabajadors);

                    trabajadorVistaEmpleadorListAdapter.setOnItemClickListener(new TrabajadorVistaEmpleadorListAdapter.ClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            trabajadorAtPosition = trabajadorVistaEmpleadorListAdapter.getTrabajadorAtPosition(position);
                            opcionesTrabajadorDialog(trabajadorAtPosition);
//                            launchTrabajadorActivity(trabajadorAtPosition);
                        }
                    });

                    progressBar1.setVisibility(View.GONE);
//                    cleanInvitadoUI(root);
                }
            });

            /*Carga de datos trabajadores*/
        });
        /*Carga de datos oficios*/
    }

    private void launchTrabajadorActivity(Trabajador trabajadorAtPosition) {

    }


    public void opcionesTrabajadorDialog(Trabajador trabajador) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(requireActivity());


        View promptsView = inflater.inflate(R.layout.alert_dialog_opciones_trabajadores, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);


        final ImageView imageButtonMessage = promptsView.findViewById(R.id.imageViewMessage);
        final ImageView imageButtonCall = promptsView.findViewById(R.id.imageViewCall);
        final ImageView imageButtonVideoCall = promptsView.findViewById(R.id.imageViewVideoCall);
        final ImageView imageButtonInfo = promptsView.findViewById(R.id.imageViewInfo);


        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        imageButtonMessage.setColorFilter(colorPrimary);
        imageButtonCall.setColorFilter(colorPrimary);
        imageButtonVideoCall.setColorFilter(colorPrimary);
        imageButtonInfo.setColorFilter(colorPrimary);
        /*Esto es una maravilla*/


        final ImageView imageView = promptsView.findViewById(R.id.imageViewTrabajador);
        final TextView textView = promptsView.findViewById(R.id.textViewNombreUsuario);

        textView.setText(String.format("%s %s", trabajador.getNombre(), trabajador.getApellido()));
        if (trabajador.getFotoPerfil() != null) {


            Glide
                    .with(requireActivity())
                    .load(trabajador.getFotoPerfil())
//                    .circleCrop() /*mala idea*/
                    .apply(new RequestOptions().override(300, 400))
                    .placeholder(R.drawable.ic_usuario)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);


        } else {
            imageView.setImageResource(R.drawable.ic_usuario);
            imageView.setColorFilter(colorPrimary);

        }


        imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(requireActivity(), CrazyIndividualChatActivity.class);
                intent.putExtra("trabajador", trabajador);

                requireActivity().startActivity(intent);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }

            }
        });

        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
//                    performAction(...);
//                    realizarLlamadaVoz(trabajadorAtPosition);


                    Intent intentllamadaVoz = new Intent(requireActivity(), AgoraOnlyVoiceCallActivity.class);
                    intentllamadaVoz.putExtra("usuarioRemoto", (Usuario) trabajadorAtPosition);
                    intentllamadaVoz.putExtra("usuarioLocal", usuarioFrom);
                    String channelName = FirebaseDatabase.getInstance().getReference().child("voiceCalls").push().getKey();
                    intentllamadaVoz.putExtra("channelName", channelName);
                    intentllamadaVoz.putExtra("callStatus", "llamadaSaliente");

                    requireActivity().startActivity(intentllamadaVoz);

                } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                    Snackbar.make(root, "Permiso de micrófono necesario",
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            Toast.makeText(requireActivity(), "GG permisos", Toast.LENGTH_LONG).show();
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                        }
                    }).show();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setTitle("");
                        builder.setMessage(R.string.permiso_call_text);
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
                        // You can directly ask for the permission.
                        Snackbar.make(root, "Permiso de micrófono no concedido", Snackbar.LENGTH_LONG).show();
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 8000);
                    }
                }


                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentVideollamada = new Intent(requireActivity(), AgoraVideoCallActivity.class);
                intentVideollamada.putExtra("usuarioRemoto", (Usuario) trabajador);
                intentVideollamada.putExtra("usuarioLocal", (Usuario) usuarioFrom);
                String channelName = FirebaseDatabase.getInstance().getReference().child("videoCalls")
                        .push().getKey();
                intentVideollamada.putExtra("channelName", channelName);
                intentVideollamada.putExtra("callStatus", "llamadaSaliente");

                requireActivity().startActivity(intentVideollamada);


                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPerfilData = new Intent(requireActivity(), PerfilTrabajadorActivity.class);
                intentPerfilData.putExtra("idTrabajador", trabajador.getIdUsuario());

                int exitFlag = 0;
                if (chatList != null) {
                    for (Chat chat : chatList) {
                        exitFlag = 0;
                        for (Participante p : chat.getParticipantes()) {
                            if (p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                exitFlag++;
                            }
                            if (p.getIdParticipante().equals(trabajador.getIdUsuario())) {
                                exitFlag++;
                            }
                            if (exitFlag == 2) {
                                break;
                            }
                        }
                        if (exitFlag == 2) {
                            intentPerfilData.putExtra("chat", chat);
                            break;
                        }
                    }
                }

                requireActivity().startActivity(intentPerfilData);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });


//        return builder.create();
        dialogVar = builder.create();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 180);
        dialogVar.getWindow().setBackgroundDrawable(inset);
        dialogVar.show();
    }

    public void realizarLlamadaVoz(Trabajador trabajador) {
        Intent intentllamadaVoz = new Intent(requireActivity(), AgoraOnlyVoiceCallActivity.class);
        intentllamadaVoz.putExtra("usuarioRemoto", (Usuario) trabajadorAtPosition);
        intentllamadaVoz.putExtra("usuarioLocal", usuarioFrom);
        String channelName = FirebaseDatabase.getInstance().getReference().child("voiceCalls").push().getKey();
        intentllamadaVoz.putExtra("channelName", channelName);
        intentllamadaVoz.putExtra("callStatus", "llamadaSaliente");

        requireActivity().startActivity(intentllamadaVoz);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_ONLY_MIC:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    realizarLlamadaVoz(trabajadorAtPosition);
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Snackbar.make(root, "Permiso de micrófono no concedido",
                            Snackbar.LENGTH_LONG).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    private void setAdminUI(View root) {
        root.findViewById(R.id.relativeLayout3).setVisibility(View.VISIBLE);/*Trabajadores*/

        root.findViewById(R.id.relativeLayout1).setVisibility(View.VISIBLE);/*Trabajadores*/
        root.findViewById(R.id.relativeLayout2).setVisibility(View.VISIBLE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.GONE);/*Citas de trabajo*/
        loadOficiosAdmin(root);
        loadTrabajadoresAdmin(root);
        loadEmpleadoresAdmin(root);
//        loadTrabajadores(root);
        //cleanInvitadoUI(root);
//        loadHabiliades(root);

    }

    private void loadOficiosAdmin(View root) {
        OficioRegistroCRUDListAdapter oficioRegistroListAdapter;
        oficioRegistroListAdapter = new OficioRegistroCRUDListAdapter(requireActivity());
        RecyclerView recyclerView3 = root.findViewById(R.id.fragHomeRecyclerView3);
        ProgressBar progressBar3 = root.findViewById(R.id.fragHomeProgressBar3);
        recyclerView3.setAdapter(oficioRegistroListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(layoutManager);

        BienvenidoViewModel bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);

        bienvenidoViewModel.getAllTrabajadores().observe(requireActivity(), trabajadors -> {
            if (trabajadors != null) {
                trabajadorArrayList = trabajadors;
                oficioRegistroListAdapter.setTrabajadorArrayList(trabajadorArrayList);
            }
        });

        bienvenidoViewModel.getAllOficios().observe(requireActivity(), oficios -> {
            if (oficios != null) {
                Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));

                oficioRegistroListAdapter.setOficios(oficios);
                progressBar3.setVisibility(View.GONE);


//                oficioRegistroListAdapter.setOnItemClickListener(new OficioRegistroCRUDListAdapter.ClickListener() {
//                    @Override
//                    public void onItemClickEdit(View v, int position) {
//                        Oficio oficio = oficioRegistroListAdapter.getOficioAtPosition(position);
//                        launchUpdateOficioActivity(oficio);
//                    }
//
//                    @Override
//                    public void onItemClickDelete(View v, int position) {
//                        Oficio oficio = oficioRegistroListAdapter.getOficioAtPosition(position);
//                        launchDeleteOficioActivity(oficio);
//                    }
//                });
            }

        });


    }

    public void launchUpdateOficioActivity(Oficio oficio) {
        alertDialogEditarOficio(oficio);
    }

    public void launchDeleteOficioActivity(Oficio oficio) {
        alertDialogConfirmar(oficio.getIdOficio());
    }

    public void alertDialogEditarOficio(Oficio oficio) {
        final EditText input = new EditText(requireActivity());
        input.setHint("Editando oficio");
        input.setText(oficio.getNombre());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog dialogNuevoOficio = new AlertDialog.Builder(requireActivity())
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Editar oficio:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Oficio oficioAux = new Oficio();
                        if (!input.getText().toString().equals("")) {
                            oficio.setNombre(input.getText().toString());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("oficios")
                                    .child(oficio.getIdOficio())
                                    .child("nombre")
                                    .setValue(oficio.getNombre())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(requireActivity(), "Oficio actualizado", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
//                            oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                            Toast.makeText(requireActivity(), "No se ha ingresado ningún nombre." +
                                    "\nPor favor ingrese un nombre válido.", Toast.LENGTH_LONG).show();
                        }
                        /*try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }*/
                        input.setText("");
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        input.setText("");
                        /*try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }*/
                    }
                }).create();
        dialogNuevoOficio.show();
    }

    public void alertDialogConfirmar(String idOficio) {

        AlertDialog dialogEliminarOficio = new AlertDialog.Builder(requireActivity())
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Eliminar oficio:")
                .setMessage("¿Está seguro que desea eliminar este oficio?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        boolean flagDelete = true;
                        try {
                            for (Trabajador tr : trabajadorArrayList) {
                                for (String idOf : tr.getIdOficios()) {
                                    if (idOf.equals(idOficio)) {
//                                        Toast.makeText(context, "No se puede eliminar el oficio", Toast.LENGTH_LONG).show();
                                        flagDelete = false;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                        if (flagDelete) {
//                            Toast.makeText(context,"Oficio eliminado",Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("oficios")
                                    .child(idOficio)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(requireActivity(), "Oficio eliminado", Toast.LENGTH_LONG).show();
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("habilidades")
                                                        .child(idOficio)
                                                        .setValue(null)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                } else {
                                                                }
                                                            }
                                                        });

                                            }

                                        }
                                    });
                        } else {
                            Toast.makeText(requireActivity(), "No se puede eliminar el oficio", Toast.LENGTH_LONG).show();
                        }

                        /*try {
                            dialogEliminarOficio.dismiss();
                        } catch (Exception e) {

                            Log.e(TAG, e.toString());
                        }*/
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*try {
                            dialogEliminarOficio.dismiss();
                        } catch (Exception e) {

                        }*/
                    }
                }).create();
        dialogEliminarOficio.show();
    }


    private void cleanInvitadoUI(View root) {
        RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);

        recyclerView1.setAdapter(trabajadorListAdapter);
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
        recyclerView1.setLayoutParams(lp);
        trabajadorListAdapter.setTrabajadores(new ArrayList<>());
        trabajadorListAdapter.getTrabajadors().clear();
    }

    private void loadTrabajadoresAdmin(View root) {

//        TrabajadorCRUDListAdapter trabajadorCRUDListAdapter = new TrabajadorCRUDListAdapter(requireActivity());
//        RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
//        ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
//        recyclerView1.setAdapter(trabajadorCRUDListAdapter);
//        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
//
//        FirebaseDatabase.getInstance().getReference().child("oficios")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<Oficio> oficioArrayList = new ArrayList<>();
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Oficio oficio = data.getValue(Oficio.class);
//                            oficioArrayList.add(oficio);
//                        }
//                        trabajadorCRUDListAdapter.setOficioList(oficioArrayList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//        FirebaseDatabase.getInstance().getReference().child("trabajadores")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Trabajador trabajador = data.getValue(Trabajador.class);
//                            trabajadorArrayList.add(trabajador);
//                        }
//                        trabajadorCRUDListAdapter.setTrabajadores(trabajadorArrayList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        /*Carga de datos oficios*/
        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(this, oficios -> {

            /*Carga de datos trabajadores*/
            RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
            ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
            TrabajadorCRUDListAdapter trabajadorCRUDListAdapter = new TrabajadorCRUDListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);
//             trabajadorCRUDListAdapterO = new TrabajadorCRUDListAdapter(requireActivity());
            recyclerView1.setAdapter(trabajadorCRUDListAdapter);
            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));


            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
            trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
                if (trabajadors != null) {

                    if (trabajadors.size() > 2) {

                        int numberOfElementsToShow = 2;
                        int oneElementHeight = 670;

                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
                        recyclerView1.setLayoutParams(lp);

                    } else {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        recyclerView1.setLayoutParams(lp);
                    }
                    trabajadorCRUDListAdapter.setTrabajadores(trabajadors);
                    progressBar1.setVisibility(View.GONE);
                }
            });

            /*Carga de datos trabajadores*/
        });
        /*Carga de datos oficios*/


    }

    private void loadTrabajadoresVista(View root) {


        FirebaseDatabase.getInstance().getReference().child("citas")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Cita> citaArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Cita cita = data.getValue(Cita.class);
                            citaArrayList.add(cita);
                        }
                        trabajadorListAdapter.setCitaList(citaArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        /*Carga de datos oficios*/
        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(getViewLifecycleOwner(), oficios -> {

            /*Carga de datos trabajadores*/
            RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
            ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
//            TrabajadorCRUDListAdapter trabajadorListAdapter = new TrabajadorCRUDListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);

//             trabajadorListAdapter = new TrabajadorListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);

            trabajadorListAdapter.setOficioList(oficios);
            recyclerView1.setAdapter(trabajadorListAdapter);
//            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));


            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
            trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
                if (trabajadors != null) {

                    Collections.sort(trabajadors, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

                    if (trabajadors.size() > 2) {
                        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        int numberOfElementsToShow = 3;
                        int oneElementHeight = 380;

                        RelativeLayout.LayoutParams lp =
                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
                        recyclerView1.setLayoutParams(lp);

                    } else {
                        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    }


                    trabajadorListAdapter.setTrabajadores(trabajadors);
                    progressBar1.setVisibility(View.GONE);
//                    cleanInvitadoUI(root);
                }
            });

            /*Carga de datos trabajadores*/
        });
        /*Carga de datos oficios*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            trabajadorViewModel.removeChildListener();
        } catch (Exception e) {

        }
    }

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
                                try {
                                    alertD.show();

                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
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

    public android.app.AlertDialog califTrabajador(Cita citaLocal) {


        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(requireActivity());
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
            ((Empleador) usuarioLocal).calificarTrabajador(citaLocal, requireActivity());
        }
        Toast.makeText(requireActivity(), "Gracias por utilizar nuestros servicios.", Toast.LENGTH_SHORT).show();

    }
}