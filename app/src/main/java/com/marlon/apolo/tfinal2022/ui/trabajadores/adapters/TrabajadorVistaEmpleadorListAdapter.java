package com.marlon.apolo.tfinal2022.ui.trabajadores.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.oficios.adapters.OficioTrabajadorVistaListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorVistaEmpleadorListAdapter extends RecyclerView.Adapter<TrabajadorVistaEmpleadorListAdapter.TrabajadorVistaEmpleadorHolder> {

    private final LayoutInflater mInflater;
    private List<Trabajador> trabajadorList; // Cached copy of words
    private static ClickListener clickListener;
    private Context context;
    private List<Oficio> oficioList;
    private Usuario usuarioLocal;


    public TrabajadorVistaEmpleadorListAdapter(Context contextVar) {
        mInflater = LayoutInflater.from(contextVar);
        context = contextVar;
    }

    public void setOficioList(List<Oficio> oficioList) {
        this.oficioList = oficioList;
    }

    public Usuario getUsuarioLocal() {
        return usuarioLocal;
    }

    public void setUsuarioLocal(Usuario usuarioLocal) {
        this.usuarioLocal = usuarioLocal;
    }

    @Override
    public TrabajadorVistaEmpleadorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.card_view_presentacion_trabajador, parent, false);
        return new TrabajadorVistaEmpleadorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrabajadorVistaEmpleadorHolder holder, int position) {
        if (trabajadorList != null) {
            Trabajador current = trabajadorList.get(position);
            holder.textViewNombre.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
            if (current.getFotoPerfil() != null) {
                holder.imageViewTrabajador.setColorFilter(null);
                Glide.with(context).load(current.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewTrabajador);
            } else {
                TypedValue typedValue = new TypedValue();
                context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int colorPrimary = typedValue.data;
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_usuario)).placeholder(R.drawable.ic_usuario).into(holder.imageViewTrabajador);
                holder.imageViewTrabajador.setColorFilter(colorPrimary);
            }


//        Log.d(TAG, current.toString());


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

//        holder.textViewCalif.setText(String.format("Calificación: %.2f " + "/ 5.00", current.getCalificacion()));
            if (current.getCalificacion() > 0.0) {
                holder.textViewCalif.setText(String.format("Calificación: %.1f " + "/ 5.0", current.getCalificacion()));
                holder.ratingBar.setRating((float) current.getCalificacion());
                holder.ratingBar.setVisibility(View.VISIBLE);


            } else {
                holder.textViewCalif.setText(String.format("%s %s %s", current.getNombre(), current.getApellido(), context.getString(R.string.text_no_trabajo)));

//                holder.textViewCalif.setText(context.getString(R.string.text_no_trabajo));
//            holder.ratingBar.setRating((float) current.getCalificacion());
                holder.ratingBar.setVisibility(View.GONE);
            }

            if (current.getEmail() != null) {
                holder.textViewContacto.setText(current.getEmail());
            }
            if (current.getCelular() != null) {
                holder.textViewContacto.setText(current.getCelular());

            }






        } else {
            // Covers the case of data not being ready yet.
//            holder.wordItemView.setText(R.string.no_word);
        }
    }

    /**
     * Associates a list of words with this adapter
     */
    public void setTrabajadores(List<Trabajador> words) {
        trabajadorList = words;
        notifyDataSetChanged();
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * mWords has not been updated (means initially, it's null, and we can't return null).
     */
    @Override
    public int getItemCount() {
        if (trabajadorList != null)
            return trabajadorList.size();
        else return 0;
    }

    /**
     * Gets the word at a given position.
     * This method is useful for identifying which word
     * was clicked or swiped in methods that handle user events.
     *
     * @param position The position of the word in the RecyclerView
     * @return The word at the given position
     */
    public Trabajador getTrabajadorAtPosition(int position) {
        return trabajadorList.get(position);
    }

    class TrabajadorVistaEmpleadorHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewCalif;
        private final RecyclerView recyclerViewOficios;
        private final ImageView imageViewTrabajador;
        private RatingBar ratingBar;
        private final TextView textViewContacto;

        private TrabajadorVistaEmpleadorHolder(View itemView) {
            super(itemView);

            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCalif = itemView.findViewById(R.id.textViewCalificacion);
            recyclerViewOficios = itemView.findViewById(R.id.recyclerViewOficios);
            imageViewTrabajador = itemView.findViewById(R.id.imageViewTrabajador);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        TrabajadorVistaEmpleadorListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }
}
