package com.marlon.apolo.tfinal2022.ui.oficios;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class OficioRegistroCRUDListAdapter extends RecyclerView.Adapter<OficioRegistroCRUDListAdapter.OficioViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Oficio> oficios;
    private AlertDialog dialogNuevoOficio;
    private ArrayList<Trabajador> trabajadorArrayList;

//    private static ClickListener clickListener;


    public OficioRegistroCRUDListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficios() {
        return oficios;
    }

    @NonNull
    @Override
    public OficioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_crud_oficio_with_habilidad_list_item, parent, false);
        return new OficioRegistroCRUDListAdapter.OficioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OficioViewHolder holder, int position) {
        Oficio current = oficios.get(position);
        holder.textViewOficioName.setText(String.format("%s", current.getNombre()));


    }

    public void setOficios(List<Oficio> oficiosVar) {
        oficios = oficiosVar;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (oficios != null)
            return oficios.size();
        else return 0;
    }

    public void setTrabajadorArrayList(ArrayList<Trabajador> trabajadorArrayList) {
        this.trabajadorArrayList = trabajadorArrayList;
    }

    public class OficioViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewOficioName;
        private final ImageButton imageButtonEdit;
        private final ImageButton imageButtonDelete;

//        private RecyclerView recyclerView;

        private OficioViewHolder(View itemView) {
            super(itemView);
            textViewOficioName = itemView.findViewById(R.id.textViewOficioName);
//            recyclerView = itemView.findViewById(R.id.recyclerViewHabilidades);
            imageButtonEdit = itemView.findViewById(R.id.imageButtonEdit);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HabilidadActivity.class);
                    intent.putExtra("oficio", oficios.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Editar", Toast.LENGTH_LONG).show();
//                    alertDialogEditarOficio(oficios.get(getAdapterPosition()));

//                    clickListener.onItemClickEdit(imageButtonEdit, getAdapterPosition());


                    Oficio oficio = getOficioAtPosition(getAdapterPosition());
                    launchUpdateOficioActivity(oficio);


                }
            });

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Oficio oficio = getOficioAtPosition(getAdapterPosition());
                    launchDeleteOficioActivity(oficio);
                    //Toast.makeText(context, "Eliminar", Toast.LENGTH_LONG).show();
                    boolean flagEliminar = true;
                    try {
//                        for (Trabajador tr : trabajadors) {
//                            if (tr.getIdOficios() != null) {
//                                for (String id : tr.getIdOficios()) {
//                                    if (oficios.get(getAdapterPosition()).getIdOficio().equals(id)) {
//                                        flagEliminar = false;
//                                        break;
//                                    }
//                                }
//                            }
//                        }
                    } catch (Exception e) {
                        Log.d("TAG", e.toString());
                    }
//                    alertDialogConfirmar(oficios.get(getAdapterPosition()).getIdOficio(), flagEliminar);

                }
            });

        }
    }


    public void launchUpdateOficioActivity(Oficio oficio) {
        alertDialogEditarOficio(oficio);
    }

    public void launchDeleteOficioActivity(Oficio oficio) {
        alertDialogConfirmar(oficio.getIdOficio());
    }

    public void alertDialogConfirmar(String idOficio) {

        dialogNuevoOficio = new AlertDialog.Builder(context)
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
                            Log.d("TAG", e.toString());
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
                                                Toast.makeText(context, "Oficio eliminado", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, "No se puede eliminar el oficio", Toast.LENGTH_LONG).show();
                        }

                        try {
                            dialogNuevoOficio.dismiss();
                        } catch (Exception e) {

                            Log.e("TAG", e.toString());
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


    public void alertDialogEditarOficio(Oficio oficio) {
        final EditText input = new EditText(context);
        input.setHint("Editando oficio");
        input.setText(oficio.getNombre());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog dialogNuevoOficio = new AlertDialog.Builder(context)
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
                                                Toast.makeText(context, "Oficio actualizado", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
//                            oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
                            Toast.makeText(context, "No se ha ingresado ningún nombre." +
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


//    public void alertDialogEditarOficio(Oficio oficio) {
//        final EditText input = new EditText(context);
//        input.setHint("Editando oficio");
//        input.setText(oficio.getNombre());
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        dialogNuevoOficio = new AlertDialog.Builder(context)
//                .setIcon(R.drawable.ic_oficios)
//                .setTitle("Nuevo oficio:")
//                .setView(input)
//                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Oficio oficioAux = new Oficio();
//                        if (!input.getText().toString().equals("")) {
//                            oficio.setNombre(input.getText().toString());
//
//                            FirebaseDatabase.getInstance().getReference()
//                                    .child("oficios")
//                                    .child(oficio.getIdOficio())
//                                    .child("nombre")
//                                    .setValue(oficio.getNombre())
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(context, "Oficio actualizado", Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    });
//                        } else {
////                            oficioViewModel.addOficioToFirebase(requireActivity(), oficio);
//                            Toast.makeText(context, "No se ha ingresado ningún nombre." +
//                                    "\nPor favor ingrese un nombre válido.", Toast.LENGTH_LONG).show();
//                        }
//                        try {
//                            dialogNuevoOficio.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                        input.setText("");
//                    }
//
//
//                })
//                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        input.setText("");
//                        try {
//                            dialogNuevoOficio.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }).create();
//        dialogNuevoOficio.show();
//    }
//
//    public void alertDialogConfirmar(String idOficio, boolean flagEliminar) {
//
//        dialogNuevoOficio = new AlertDialog.Builder(context)
//                .setIcon(R.drawable.ic_oficios)
//                .setTitle("Eliminar oficio:")
//                .setMessage("¿Está seguro que desea eliminar este oficio?")
//                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
////                        boolean flagDelete = true;
////                        try {
////                            for (Trabajador tr : trabajadors) {
////                                for (String idOf : tr.getIdOficios()) {
////                                    if (idOf.equals(idOficio)) {
//////                                        Toast.makeText(context, "No se puede eliminar el oficio", Toast.LENGTH_LONG).show();
////                                        flagDelete = false;
////                                        break;
////                                    }
////                                }
////                            }
////                        } catch (Exception e) {
////
////                        }
//
//                        if (flagEliminar) {
////                            Toast.makeText(context,"Oficio eliminado",Toast.LENGTH_LONG).show();
//                            FirebaseDatabase.getInstance().getReference()
//                                    .child("oficios")
//                                    .child(idOficio)
//                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(context, "Oficio eliminado", Toast.LENGTH_LONG).show();
//                                                FirebaseDatabase.getInstance().getReference()
//                                                        .child("habilidades")
//                                                        .child(idOficio)
//                                                        .setValue(null)
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()) {
//                                                                } else {
//                                                                }
//                                                            }
//                                                        }).addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                            }
//                                                        });
//
//                                            }
//
//                                        }
//                                    });
//                        } else {
//                            Toast.makeText(context, "No se puede eliminar el oficio", Toast.LENGTH_LONG).show();
//                        }
//
//
//                        try {
//                            dialogNuevoOficio.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                })
//                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        try {
//                            dialogNuevoOficio.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }).create();
//        dialogNuevoOficio.show();
//    }


    /**
     * Gets the word at a given position.
     * This method is useful for identifying which word
     * was clicked or swiped in methods that handle user events.
     *
     * @param position The position of the word in the RecyclerView
     * @return The word at the given position
     */
    public Oficio getOficioAtPosition(int position) {
        return oficios.get(position);
    }


//    public void setOnItemClickListener(ClickListener clickListener) {
//        OficioRegistroCRUDListAdapter.clickListener = clickListener;
//    }

    public interface ClickListener {
        void onItemClickEdit(View v, int position);

        void onItemClickDelete(View v, int position);
    }


}
