package com.marlon.apolo.tfinal2022.ui.oficios.view;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.NuevoOficioArchiActivity;
import com.marlon.apolo.tfinal2022.ui.oficios.viewModel.OficioViewModel;
import com.marlon.apolo.tfinal2022.ui.oficios.adapters.OficioRegistroCRUDListAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class OficioFragment extends Fragment {

    private static final String TAG = OficioFragment.class.getSimpleName();
    private OficioViewModel oficioViewModel;
    private View root;
    private Dialog dialogNuevoOficio;
    private OficioRegistroCRUDListAdapter oficioRegistroListAdapter;


    private ArrayList<Trabajador> trabajadorArrayList;



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