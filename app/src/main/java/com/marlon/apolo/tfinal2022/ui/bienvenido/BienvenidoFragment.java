package com.marlon.apolo.tfinal2022.ui.bienvenido;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.admin.AdminViewModel;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaListAdapter;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaViewModel;
import com.marlon.apolo.tfinal2022.citasTrabajo.TimePickerFragment;
import com.marlon.apolo.tfinal2022.databinding.FragmentBienvenidoBinding;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.bienvenido.view.OficiosActivityVista;
import com.marlon.apolo.tfinal2022.ui.bienvenido.view.TrabajadoresActivityVista;
import com.marlon.apolo.tfinal2022.ui.chats.ChatViewModel;
import com.marlon.apolo.tfinal2022.ui.citaTrabajo.CitaTrabajoViewActivity;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorFragment;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioRegistroCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BienvenidoFragment extends Fragment {

    private static final String TAG = BienvenidoFragment.class.getSimpleName();
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);
        empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        mViewModel = new ViewModelProvider(this).get(CitaViewModel.class);

        binding = FragmentBienvenidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        loadOficios(root);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        trabajadorListAdapter = new TrabajadorListAdapter(requireActivity());

        setInvitadoUI(root);
        if (firebaseUser != null) {

            ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
            chatViewModel.getAllChats().observe(getViewLifecycleOwner(), chats -> {
                trabajadorListAdapter.setChatList(chats);
            });

            loadLocalUser(root, firebaseUser);
//            loadTrabajadoresYOficios(root);
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


        int numberOfElementsToShow = 5;
        int oneElementHeight = 175;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
        recyclerView3.setLayoutParams(lp);

        bienvenidoViewModel.getAllOficios().observe(requireActivity(), oficios -> {
            if (oficios != null) {
                Collections.sort(oficios, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));

                oficioVistaListAdapter.setOficios(oficios);
                progressBar3.setVisibility(View.GONE);
                recyclerView3.setVisibility(View.VISIBLE);

            }
        });

    }

    private void loadTrabajadores(View root) {
        RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
        ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
        BienvenidoTrabajadorListAdapter bienvenidoTrabajadorListAdapter = new BienvenidoTrabajadorListAdapter(requireActivity());
        recyclerView1.setAdapter(bienvenidoTrabajadorListAdapter);
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
            if (trabajadors != null) {
                bienvenidoTrabajadorListAdapter.setTrabajadores(trabajadors);
                progressBar1.setVisibility(View.GONE);
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


        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Citas de trabajo totales: " + String.valueOf(snapshot.getChildrenCount()));
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Cita cita = data.getValue(Cita.class);
                            Log.d(TAG, cita.toString());
                        }
                        if (snapshot.getChildrenCount() > 0) {

                        } else {
                            Toast.makeText(getContext(), "No existen resultados", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        mViewModel
                .getCitas()
                .observe(this, new Observer<ArrayList<Cita>>() {
                    @Override
                    public void onChanged(ArrayList<Cita> citas) {
                        if (citas != null) {
                            if (citas.size() > 0) {
                                progressBar4.setVisibility(View.GONE);
                                citaListAdapter.setCitas(citas);
                                recyclerView4.setVisibility(View.VISIBLE);
                                int numberOfElementsToShow = 4;
                                int oneElementHeight = 294;
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
                                recyclerView4.setLayoutParams(lp);

                            } else {
                                Log.d("TAG", "no existen resultados");
                                progressBar4.setVisibility(View.GONE);
                                recyclerView4.setVisibility(View.GONE);

                            }
                        } else {
                            Log.d("TAG", "no existen resultados");
                            Toast.makeText(getContext(), "No existen resultados", Toast.LENGTH_LONG).show();
                            progressBar4.setVisibility(View.GONE);
                            recyclerView4.setVisibility(View.GONE);
                        }
                    }

                });
    }

    private void loadHabiliades(View root) {
        RecyclerView recyclerView4 = root.findViewById(R.id.fragHomeRecyclerView4);
        ProgressBar progressBar4 = root.findViewById(R.id.fragHomeProgressBar4);
        HabilidadListAdapter trabajadorListAdapter = new HabilidadListAdapter(requireActivity());
        recyclerView4.setAdapter(trabajadorListAdapter);
        recyclerView4.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        bienvenidoViewModel.getAllHabilidades().observe(getViewLifecycleOwner(), habilidads -> {
//            if (habilidads != null) {
//                trabajadorListAdapter.setHabilidades(habilidads);
////                progressBar4.setVisibility(View.GONE);
//            }
//        });
    }

    public void loadEmpleadoresAdmin(View root) {
        /*Carga de datos empleadores*/
        RecyclerView recyclerView = root.findViewById(R.id.fragHomeRecyclerView2);
        ProgressBar progressBar2 = root.findViewById(R.id.fragHomeProgressBar2);
        final EmpleadorCRUDListAdapter adapter = new EmpleadorCRUDListAdapter(requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        int numberOfElementsToShow = 2;
        int oneElementHeight = 480;

        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
        recyclerView.setLayoutParams(lp);


        empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        empleadorViewModel.getAllEmpleadores().observe(getViewLifecycleOwner(), empleadors -> {
            if (empleadors != null) {
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
                                    //Toast.makeText(requireActivity(),administrador.toString(),Toast.LENGTH_LONG).show();
                                }
                            });

                            empleadorViewModel.getOneEmpleador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), empleador -> {
                                if (empleador != null) {
                                    usuarioFrom = empleador;
                                    trabajadorListAdapter.setUsuarioFrom(usuarioFrom);
                                    // Toast.makeText(requireActivity(),empleador.toString(),Toast.LENGTH_LONG).show();

                                }
                            });

                            trabajadorViewModel.getOneTrabajador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), trabajador -> {
                                if (trabajador != null) {
                                    usuarioFrom = trabajador;
                                    trabajadorListAdapter.setUsuarioFrom(usuarioFrom);
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
        loadTrabajadoresVista(root);
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
        root.findViewById(R.id.relativeLayout4).setVisibility(View.VISIBLE);/*Citas de trabajo*/

        loadCitas(root);
    }

    private void setEmpleadorUI(View root) {
        root.findViewById(R.id.relativeLayout2).setVisibility(View.GONE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.GONE);/*Citas de trabajo*/
//        loadTrabajadores(root);
        loadTrabajadoresVista(root);

    }

    private void setAdminUI(View root) {
        root.findViewById(R.id.relativeLayout2).setVisibility(View.VISIBLE);/*Empleadores*/
        root.findViewById(R.id.relativeLayout4).setVisibility(View.GONE);/*Citas de trabajo*/
        loadEmpleadoresAdmin(root);
        loadOficiosAdmin(root);
//        loadTrabajadores(root);
        cleanInvitadoUI(root);
        loadTrabajadoresAdmin(root);
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

        /*Carga de datos oficios*/
        OficioViewModel oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
        oficioViewModel.getAllOficios().observe(this, oficios -> {

            /*Carga de datos trabajadores*/
            RecyclerView recyclerView1 = root.findViewById(R.id.fragHomeRecyclerView1);
            ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
            TrabajadorCRUDListAdapter trabajadorListAdapter = new TrabajadorCRUDListAdapter(requireActivity(), (ArrayList<Oficio>) oficios);
            recyclerView1.setAdapter(trabajadorListAdapter);
            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));

            int numberOfElementsToShow = 2;
            int oneElementHeight = 670;

            RelativeLayout.LayoutParams lp =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
            recyclerView1.setLayoutParams(lp);


            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
            trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
                if (trabajadors != null) {
                    trabajadorListAdapter.setTrabajadores(trabajadors);
                    progressBar1.setVisibility(View.GONE);
                }
            });

            /*Carga de datos trabajadores*/
        });
        /*Carga de datos oficios*/
    }

    private void loadTrabajadoresVista(View root) {

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
            recyclerView1.setLayoutManager(new LinearLayoutManager(requireActivity()));
            int numberOfElementsToShow = 3;
            int oneElementHeight = 380;

            RelativeLayout.LayoutParams lp =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, oneElementHeight * numberOfElementsToShow);
            recyclerView1.setLayoutParams(lp);

            trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
            trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
                if (trabajadors != null) {
                    Collections.sort(trabajadors, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

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
}