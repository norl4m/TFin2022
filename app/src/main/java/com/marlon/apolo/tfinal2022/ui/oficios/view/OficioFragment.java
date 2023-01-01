package com.marlon.apolo.tfinal2022.ui.oficios.view;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.adapters.OficioArchiCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.model.OficioArchiModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.NuevoOficioArchiActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.viewModel.OficioArchiViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.adaptadores.OficioRegistroCRUDListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OficioFragment extends Fragment {

    private static final String TAG = OficioFragment.class.getSimpleName();
    private OficioViewModel oficioViewModel;
    private View root;
    private Dialog dialogNuevoOficio;
    private OficioRegistroCRUDListAdapter oficioRegistroListAdapter;

    private String oficioSelected;
    private int positionSelected;
    private boolean click;
    private int c;
    private TrabajadorViewModel trabajadorViewModel;
    private ArrayList<Trabajador> trabajadorArrayList;
    private ChildEventListener oficioChildEventListener;

//    public static OficioFragment newInstance() {
//        return new OficioFragment();
//    }


    public void alertDialogNuevoOficio() {
        Log.d("TAG", "Registrando nuevo oficio....");
        final EditText input = new EditText(requireActivity());
        input.setHint("Nombre de oficio");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dialogNuevoOficio = new AlertDialog.Builder(requireActivity())
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nuevo oficio:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Oficio oficio = new Oficio();
                        oficio.setNombre(input.getText().toString());
                        if (!input.getText().toString().equals("")) {
                            int exit = 0;
                            if (oficioRegistroListAdapter.getOficios() != null) {
                                if (oficioRegistroListAdapter.getOficios().size() > 0) {
                                    for (Oficio o : oficioRegistroListAdapter.getOficios()) {
                                        if (o.getNombre().toUpperCase().equals(oficio.getNombre().toUpperCase())) {
                                            exit++;
                                            break;
                                        }
                                    }
                                    if (exit == 1) {
                                        Toast.makeText(requireActivity(), "Registro fallido!", Toast.LENGTH_LONG).show();
                                        exit = 0;
                                    } else {
                                        oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                                    }
                                } else {
                                    /*No ninguno registrado uno*/
                                    oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                                }
                            } else {
                                oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                            }
                        } else {
//                            oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                            Toast.makeText(requireActivity(), "No se ha ingresado ningún nombre." +
                                    "\nPor favor ingrese un nombre válido.", Toast.LENGTH_LONG).show();
                        }
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                        input.setText("");
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        input.setText("");
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                    }
                }).create();
        dialogNuevoOficio.show();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.oficio_fragment, container, false);


        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewOficios);

        FloatingActionButton floatingActionButton = root.findViewById(R.id.fabAddOficio);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), NuevoOficioArchiActivity.class);
                startActivity(intent);
            }
        });
        trabajadorArrayList = new ArrayList<>();

        ArrayList<Oficio> oficioArrayList = new ArrayList<>();
//        oficioChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                try {
//                    Oficio oficio = snapshot.getValue(Oficio.class);
//                    oficios.add(oficio);
//                    allOficios.setValue(oficios);
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if (oficios.size() > 0) {
//                    try {
//                        Oficio oficioChanged = snapshot.getValue(Oficio.class);
//                        for (Oficio oficioDB : oficios) {
//                            try {
//                                if (oficioDB.getIdOficio().equals(oficioChanged.getIdOficio())) {
//                                    oficios.set(oficios.indexOf(oficioDB), oficioChanged);
//                                }
//                            } catch (Exception e) {
//
//                            }
//
//                        }
//                        allOficios.setValue(oficios);
//                    } catch (Exception e) {
//                        Log.d(TAG, "onChildChanged ERROR");
//                        Log.d(TAG, e.toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Log.d("TAG", "onChildRemoved");
//                if (oficios.size() > 0) {
//                    try {
//                        Oficio oficioRemoved = snapshot.getValue(Oficio.class);
//
//                        int inde = 0;
//
//                        for (Oficio oficioDB : oficios) {
//                            try {
//                                if (oficioDB.getIdOficio().equals(oficioRemoved.getIdOficio())) {
////                                            oficios.set(oficios.indexOf(oficioDB), null);
//                                    //oficios.remove(oficios.indexOf(oficioDB));
//                                    oficios.remove(inde);
//                                    break;
//                                }
//                            } catch (Exception e) {
//
//                            }
//                            inde++;
//                        }
//                        allOficios.setValue(oficios);
//                    } catch (Exception e) {
//                        Log.d("TAG", "onChildRemoved ERROR");
//                        Log.d("TAG", e.toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };

//        FirebaseDatabase.getInstance().getReference().child("oficios")
//                .addChildEventListener(oficioChildEventListener);

        /*******************************/

        oficioRegistroListAdapter = new OficioRegistroCRUDListAdapter(requireActivity());
        recyclerView.setAdapter(oficioRegistroListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        oficioViewModel = new ViewModelProvider(this).get(OficioViewModel.class);
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

            }

        });
        /*******************************/

//        loadOficiosSpecial(root);

        return root;
    }

    private void loadOficiosSpecial(View root) {

        RecyclerView recyclerView3 = root.findViewById(R.id.recyclerViewOficios);
//        ProgressBar progressBar3 = root.findViewById(R.id.fragHomeProgressBar3);
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        OficioArchiCRUDListAdapter oficioArchiCRUDListAdapter = new OficioArchiCRUDListAdapter(requireActivity());
        recyclerView3.setAdapter(oficioArchiCRUDListAdapter);
        recyclerView3.setLayoutManager(new GridLayoutManager(requireActivity(), gridColumnCount));


        OficioArchiViewModel oficioArchiViewModel = new ViewModelProvider(this).get(OficioArchiViewModel.class);


        oficioArchiViewModel.getNumberOficios().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    ArrayList<OficioArchiModel> oficioArchiModels = new ArrayList<>();
                    OficioArchiModel oficioArchiModel = new OficioArchiModel();
                    oficioArchiModel.setIdOficio("noResultados");
                    oficioArchiModel.setNombre("Lo sentimos no se encontraron resultados.");
                    oficioArchiModels.add(oficioArchiModel);
                    oficioArchiCRUDListAdapter.setOficios(oficioArchiModels);
//                    progressBar3.setVisibility(View.GONE);

                } else {
                    oficioArchiViewModel.getAllOficios().observe(getViewLifecycleOwner(), new Observer<List<OficioArchiModel>>() {
                        @Override
                        public void onChanged(List<OficioArchiModel> oficioArchiModels) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(oficioArchiModels, Comparator.comparing(OficioArchiModel::getNombre));
                            } else {
                                Collections.sort(oficioArchiModels, (t1, t2) -> (t1.getNombre()).compareTo(t2.getNombre()));
                            }

                            oficioArchiCRUDListAdapter.setOficios(oficioArchiModels);
//                            progressBar3.setVisibility(View.GONE);

                        }
                    });
                }
            }
        });


    }

    public void alertDialogEditarOficio(Oficio oficio) {
        final EditText input = new EditText(requireActivity());
        input.setHint("Editando oficio");
        input.setText(oficio.getNombre());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dialogNuevoOficio = new AlertDialog.Builder(requireActivity())
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
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                        input.setText("");
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        input.setText("");
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                    }
                }).create();
        dialogNuevoOficio.show();
    }

    public void alertDialogConfirmar(String idOficio) {

        dialogNuevoOficio = new AlertDialog.Builder(requireActivity())
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

                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                            Log.e(TAG, e.toString());
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                        }
                    }
                }).create();
        dialogNuevoOficio.show();
    }


    public void launchUpdateOficioActivity(Oficio oficio) {
        alertDialogEditarOficio(oficio);
    }

    public void launchDeleteOficioActivity(Oficio oficio) {
        alertDialogConfirmar(oficio.getIdOficio());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_oficio, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.mnu_add_oficio:
//                Toast.makeText(requireActivity(), "Add oficio", Toast.LENGTH_SHORT).show();
//                alertDialogNuevoOficio();
                Intent intent = new Intent(requireActivity(), NuevoOficioArchiActivity.class);
                startActivity(intent);
                break;
//            case R.id.mnu_add_habilidad:
////                Toast.makeText(requireActivity(), "Add habilidad", Toast.LENGTH_SHORT).show();
//                alertDialogNuevaHabilidad().show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        MenuItem menuItem = menu.findItem(R.id.mnu_add_habilidad);
//        menuItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


}