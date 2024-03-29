package com.marlon.apolo.tfinal2022.ui.trabajadores;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioTrabajadorVistaListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorCRUDListAdapter extends RecyclerView.Adapter<TrabajadorCRUDListAdapter.TrabajadorViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Trabajador> trabajadors;
    private List<Oficio> oficioList;
    private String TAG;

    public TrabajadorCRUDListAdapter(Context contextVar) {
        context = contextVar;
        inflater = LayoutInflater.from(context);
    }

    public TrabajadorCRUDListAdapter(Context contextVar, ArrayList<Oficio> oficioArrayList) {
        context = contextVar;
        inflater = LayoutInflater.from(context);
        oficioList = oficioArrayList;
    }

    @NonNull
    @Override
    public TrabajadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_crud_trabajador, parent, false);
        return new TrabajadorCRUDListAdapter.TrabajadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrabajadorViewHolder holder, int position) {
        Trabajador current = trabajadors.get(position);
        holder.textViewNombre.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
        Log.d(TAG, current.toString());
        if (current.getFotoPerfil() != null) {
            Glide.with(context)
                    .load(current.getFotoPerfil())
                    .apply(new RequestOptions().override(150, 150))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .circleCrop()
                    .into(holder.imageViewTrabajador);
        } else {
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24)).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewTrabajador);
        }


        ArrayList<Oficio> oficiosFiltrados = new ArrayList<>();
        for (Oficio o : oficioList) {
            if (current.getIdOficios().contains(o.getIdOficio())) {
//                ArrayList<Habilidad> habilidadsFiltradas = new ArrayList<>();
//                ArrayList<Habilidad> habilidads = new ArrayList<>();
//                habilidads = o.getHabilidadArrayList();
//                o.setHabilidadArrayList(new ArrayList<>());
//                try {
//                    for (Habilidad h : habilidads) {
//                        if (current.getIdHabilidades().contains(h.getIdHabilidad())) {
//                            habilidadsFiltradas.add(h);
//                        }
//                    }
//                    o.setHabilidadArrayList(habilidadsFiltradas);
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
                oficiosFiltrados.add(o);
            }
        }


        OficioTrabajadorVistaListAdapter oficioTrabajadorVistaListAdapter = new OficioTrabajadorVistaListAdapter(context);
        holder.recyclerViewOficios.setAdapter(oficioTrabajadorVistaListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.recyclerViewOficios.setLayoutManager(layoutManager);
        oficioTrabajadorVistaListAdapter.setOficios(oficiosFiltrados);
//        oficioTrabajadorVistaListAdapter.setOficios(oficioList);
//        oficioViewModel.getAllOficios().observe((LifecycleOwner) context, oficioRegistroListAdapter::setOficios);

        holder.textViewCalif.setText(String.format("Calificación: %.1f " + "/ 5.0", current.getCalificacion()));
        holder.ratingBar.setRating((float) current.getCalificacion());

        if (current.getEmail() != null) {
            holder.textViewContacto.setText(current.getEmail());
        }
        if (current.getCelular() != null) {
            holder.textViewContacto.setText(current.getCelular());
        }

    }

    @Override
    public int getItemCount() {
        if (trabajadors != null)
            return trabajadors.size();
        else return 0;
    }

    public List<Trabajador> getTrabajadors() {
        return trabajadors;
    }

    public void setTrabajadores(List<Trabajador> trabajadorsVar) {
        trabajadors = trabajadorsVar;
        notifyDataSetChanged();
    }

    public class TrabajadorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewCalif;
        private final RecyclerView recyclerViewOficios;
        private final ImageView imageViewTrabajador;
        private RatingBar ratingBar;
        private final TextView textViewContacto;
        private ImageButton imageButtonEdit;
        private ImageButton imageButtonDelete;


        public TrabajadorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCalif = itemView.findViewById(R.id.textViewCalificacion);
            recyclerViewOficios = itemView.findViewById(R.id.recyclerViewOficios);
            imageViewTrabajador = itemView.findViewById(R.id.imageViewTrabajador);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);

            imageButtonEdit = itemView.findViewById(R.id.imageButtonEdit);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);

            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Editar", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, EditarDataActivity.class);
                    intent.putExtra("trabajador", trabajadors.get(getAdapterPosition()));
                    intent.putExtra("usuario", 2);
                    context.startActivity(intent);
                }
            });

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Eliminar", Toast.LENGTH_LONG).show();
                    alertDialogConfirmar(trabajadors.get(getAdapterPosition())).show();
                }
            });
        }
    }

    public android.app.AlertDialog alertDialogConfirmar(Trabajador trabajador) {

        return new android.app.AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Eliminar trabajador:")
                .setMessage("¿Está seguro que desea eliminar la información del trabajador?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        trabajador.eliminarInfo((Activity) context);

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }

    public void setOficioList(List<Oficio> oficioList) {
        this.oficioList = oficioList;
    }
}
