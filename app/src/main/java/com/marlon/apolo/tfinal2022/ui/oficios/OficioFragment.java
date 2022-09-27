package com.marlon.apolo.tfinal2022.ui.oficios;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.registro.RegistroOficioActivity;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import java.util.ArrayList;
import java.util.Collections;

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

    public android.app.AlertDialog alertDialogNuevaHabilidad() {

        Log.d("TAG", "Registrando nueva habilidad....");
        final EditText input = new EditText(requireActivity());
        input.setHint("Nombre de habilidad");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        return new android.app.AlertDialog.Builder(requireActivity())
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Habilidad habilidad = new Habilidad();
                        habilidad.setNombreHabilidad(input.getText().toString());
                        if (!input.getText().toString().equals("")) {
//                            mViewModel.registerJobOnFirebase(job, jobAdapter);

//                            LinearLayout layout = new LinearLayout(this);

                            if (oficioRegistroListAdapter.getOficios().size() > 0) {
                                spinnerSelectOficio(habilidad);
                            } else {
                                Toast.makeText(requireActivity(), "No existen oficios registrados!.", Toast.LENGTH_LONG).show();
//                                oficioViewModel.addHabilidadToOficioTofirebase(requireActivity(),oficio);
                                //habilidadViewModel.guardarHabilidadEnFirebase(allOficios.get(0).getIdOficio(), habilidad, getApplicationContext());
                            }
                        }
                        input.setText("");
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        input.setText("");
                    }
                }).create();
    }

    private void spinnerSelectOficio(Habilidad habilidad) {
        android.app.AlertDialog.Builder builder;
        android.app.AlertDialog alertDialog;

        Context mContext = requireActivity();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.spinner, null);

        String array_spinner[];
        array_spinner = new String[oficioRegistroListAdapter.getOficios().size()];

        int index = 0;
        for (Oficio o : oficioRegistroListAdapter.getOficios()) {
            array_spinner[index] = o.getNombre();
            index++;
        }
//        array_spinner[0] = "US";
//        array_spinner[1] = "Japan";
//        array_spinner[2] = "China";
//        array_spinner[3] = "India";
//        array_spinner[4] = "Vietnam";

        Spinner s = (Spinner) layout.findViewById(R.id.Spinner01);

//        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, array_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(requireActivity(), android.R.layout.simple_spinner_item, array_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oficioSelected = array_spinner[position];
                positionSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder = new android.app.AlertDialog.Builder(requireActivity());
        builder.setMessage("Por favor seleccione el oficio donde desea registrar su habilidad");
        builder.setView(layout);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialogConfirmar(oficioRegistroListAdapter.getOficios().get(positionSelected).getIdOficio(), habilidad).show();
//                habilidadViewModel.guardarHabilidadEnFirebase(allOficios.get(positionSelected).getIdOficio(), habilidad, getApplicationContext());
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        click = false;
        c = 0;


    }

    public android.app.AlertDialog alertDialogConfirmar(String idOficio, Habilidad habilidad) {

        return new android.app.AlertDialog.Builder(requireActivity())
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Nueva habilidad:")
                .setMessage("¿Está seguro que desea guardar su habilidad?")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //habilidadViewModel.guardarHabilidadEnFirebase(idOficio, habilidad, requireActivity());
                        Oficio oficioUpdate = new Oficio();
                        for (Oficio o : oficioRegistroListAdapter.getOficios()) {
                            if (o.getIdOficio().equals(idOficio)) {
                                oficioUpdate = o;
                            }
                        }
                        String idHabilidad = FirebaseDatabase.getInstance().getReference().child("oficios").child(idOficio).child("habilidades").push().getKey();
                        habilidad.setIdHabilidad(idHabilidad);

                        int exitFlag = 0;
                        try {
                            for (Habilidad h : oficioUpdate.getHabilidadArrayList()) {
                                if (h.getNombreHabilidad().toUpperCase().equals(habilidad.getNombreHabilidad().toUpperCase())) {
                                    exitFlag = 1;
                                    break;
                                }
                            }
                        } catch (Exception e) {

                        }

                        if (exitFlag == 0) {
                            if (oficioUpdate.getHabilidadArrayList() != null) {
                                oficioUpdate.getHabilidadArrayList().add(habilidad);
                            } else {
                                oficioUpdate.setHabilidadArrayList(new ArrayList<>());
                                oficioUpdate.getHabilidadArrayList().add(habilidad);
                            }
                            oficioViewModel.addHabilidadToOficioTofirebase(requireActivity(), oficioUpdate, habilidad);
                        } else {
                            Toast.makeText(requireActivity(), "No se ha podido registrar la habilidad", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.oficio_fragment, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewOficios);

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


        return root;
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
                alertDialogNuevoOficio();
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