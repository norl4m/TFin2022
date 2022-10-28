package com.marlon.apolo.tfinal2022.ui.oficios.adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class HabilidadCRUDListAdapterAaaaaaaa extends RecyclerView.Adapter<HabilidadCRUDListAdapterAaaaaaaa.HabilidadViewHolder> {
    private List<Habilidad> habilidadList; // Cached copy of words
    private List<Trabajador> trabajadorList; // Cached copy of words
    private Context context;
    private int optionView;
    private LayoutInflater mInflater;
    private AlertDialog dialogNuevoOficio;
    private Oficio oficio;

    public HabilidadCRUDListAdapterAaaaaaaa(Context contextVar, Oficio oficio) {
        mInflater = LayoutInflater.from(contextVar);
        context = contextVar;
        this.oficio = oficio;
    }

    @NonNull
    @Override
    public HabilidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_view_item_crud_skill, parent, false);
        return new HabilidadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HabilidadViewHolder holder, int position) {
        if (habilidadList != null) {
            Habilidad current = habilidadList.get(position);
            try {
                holder.textViewHabilidadName.setText(current.getNombreHabilidad());
            } catch (Exception e) {

            }
        } else {
            // Covers the case of data not being ready yet.
        }
    }

    @Override
    public int getItemCount() {
        if (habilidadList != null)
            return habilidadList.size();
        else return 0;
    }

    public void setHabilidades(List<Habilidad> habilidadsVar) {
        habilidadList = habilidadsVar;
        notifyDataSetChanged();
    }

    public class HabilidadViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewHabilidadName;
        private final ImageButton imageButtonEdit;
        private final ImageButton imageButtonDelete;


        private HabilidadViewHolder(View itemView) {
            super(itemView);
            textViewHabilidadName = itemView.findViewById(R.id.textViewHabilidad);

            imageButtonEdit = itemView.findViewById(R.id.imageButtonEdit);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);

            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    // Toast.makeText(context, "Editar habilidad", Toast.LENGTH_LONG).show();
                    alertDialogEditarHabilidad(habilidadList.get(getAdapterPosition()), oficio);
                }
            });

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Eliminar habilidad", Toast.LENGTH_LONG).show();
                    int escape = 0;
                    int exitFlag = 0;
                    for (Trabajador tr : trabajadorList) {
                        try {

                            for (String idH : tr.getIdHabilidades()) {
                                Log.d("TAG", idH);
                                if (idH.equals(habilidadList.get(getAdapterPosition()).getIdHabilidad())) {
                                    escape = 1;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Log.d("TAG", e.toString());
                        }
                        if (escape == 1) {
                            escape = 0;
                            exitFlag = 1;
                            break;
                        }
                    }
                    if (exitFlag == 1) {
                        Toast.makeText(context, R.string.delete_habilidad, Toast.LENGTH_LONG).show();
//                        alertDialogEliminarHabilidad(habilidadList.get(getAdapterPosition()), oficio);
                        exitFlag = 0;
                    } else {
                        alertDialogEliminarHabilidad(habilidadList.get(getAdapterPosition()), oficio);
//                        Toast.makeText(context, R.string.delete_habilidad, Toast.LENGTH_LONG).show();
                    }
//                    alertDialogEliminarHabilidad(habilidadList.get(getAdapterPosition()), oficio);
                }
            });
        }
    }

    public void alertDialogEditarHabilidad(Habilidad habilidad, Oficio oficio) {
        final EditText input = new EditText(context);
        input.setHint("Nombre de habilidad:");
        input.setText(habilidad.getNombreHabilidad());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dialogNuevoOficio = new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Editando habilidad:")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!input.getText().toString().equals("")) {
                            habilidad.setNombreHabilidad(input.getText().toString());
                            int index = 0;
                            for (Habilidad h : habilidadList) {
                                if (h.getIdHabilidad().equals(habilidad.getIdHabilidad())) {
                                    habilidadList.set(index, habilidad);
                                    break;
                                }
                                index++;
                            }

                            oficio.setHabilidadArrayList((ArrayList<Habilidad>) habilidadList);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("habilidades")
                                    .child(oficio.getIdOficio())
                                    .child(habilidad.getIdHabilidad())
                                    .setValue(habilidad)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Habilidad actualizada", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
//                            oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                            Toast.makeText(context, "No se ha ingresado ningún nombre." +
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

    public void alertDialogEliminarHabilidad(Habilidad habilidad, Oficio oficio) {
        final EditText input = new EditText(context);
        input.setHint("Nombre de habilidad:");
        input.setText(habilidad.getNombreHabilidad());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dialogNuevoOficio = new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_oficios)

                .setTitle("Eliminar habilidad:")
                .setMessage("¿Está seguro que desea eliminar esta habilidad?")


                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!input.getText().toString().equals("")) {
                            habilidad.setNombreHabilidad(input.getText().toString());
                            boolean flagNoEliminar = false;

                            Log.e("TAG", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                            Log.e("TAG", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                            Log.e("TAG", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

                            boolean flagDelete = true;
                            try {
                                for (Trabajador tr : trabajadorList) {
                                    Log.e("TAG", tr.toString());
                                    try {
                                        for (String idHa : tr.getIdHabilidades()) {
                                            if (idHa.equals(habilidad.getIdHabilidad())) {
//                                                Toast.makeText(context, "No se ha podido eliminar esta habilidad", Toast.LENGTH_LONG).show();
                                                flagDelete = false;
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {

                                    }

                                }
                            } catch (Exception e) {

                            }
                            int index = 0;

                            if (flagDelete) {
                                ArrayList<Habilidad> habilidadArrayListFilter = new ArrayList<>();
                                for (Habilidad haux : habilidadList) {
                                    if (haux.getIdHabilidad().equals(habilidad.getIdHabilidad())) {

                                    } else {
                                        habilidadArrayListFilter.add(haux);
                                    }
                                    index++;
                                }


                                oficio.setHabilidadArrayList(habilidadArrayListFilter);


                                FirebaseDatabase.getInstance().getReference()
                                        .child("habilidades")
                                        .child(oficio.getIdOficio())
                                        .child(habilidad.getIdHabilidad())
                                        .setValue(null)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Habilidad eliminada", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(context, "No se ha podido eliminar esta habilidad", Toast.LENGTH_LONG).show();
                            }


//                            for (Habilidad h : habilidadList) {
//                                for (Trabajador tr : trabajadorList) {
//                                    for (String idHabilidad : tr.getIdHabilidades()) {
//                                        if (idHabilidad.equals(habilidad.getIdHabilidad())) {
//                                            Toast.makeText(context, "No se puede eliminar este oficio", Toast.LENGTH_LONG).show();
//                                            flagNoEliminar = true;
//                                            break;
//                                        }
//                                    }
//                                }
//                                if (!flagNoEliminar) {
//                                    if (h.getIdHabilidad().equals(habilidad.getIdHabilidad())) {
////                                    habilidadList.set(index, habilidad);
//                                        habilidadList.remove(index);
//                                        break;
//                                    }
//                                }
//
//                                index++;
//                            }


                        } else {
//                            oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                            Toast.makeText(context, "No se ha ingresado ningún nombre." +
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

    public void setTrabajadorList(List<Trabajador> trabajadorList) {
        this.trabajadorList = trabajadorList;
    }


}
