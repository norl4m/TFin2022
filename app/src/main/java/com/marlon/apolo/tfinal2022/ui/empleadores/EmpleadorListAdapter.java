package com.marlon.apolo.tfinal2022.ui.empleadores;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;

import java.util.List;

public class EmpleadorListAdapter extends RecyclerView.Adapter<EmpleadorListAdapter.EmpleadorViewHolder> {
    private final Context contextInstance;
    private final LayoutInflater mInflater;
    private List<Empleador> mEmpleadores; // Cached copy of words

    public EmpleadorListAdapter(Context context) {
        contextInstance = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public EmpleadorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_empleador, parent, false);
        return new EmpleadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmpleadorViewHolder holder, int position) {
        Empleador current = mEmpleadores.get(position);
        holder.textViewNombreUsuario.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
        if (current.getEmail() != null) {
            holder.textViewContacto.setText(current.getEmail());
        }
        if (current.getCelular() != null) {
            holder.textViewContacto.setText(current.getCelular());
        }
        if (current.getFotoPerfil() != null) {
            Glide.with(contextInstance).load(current.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewUsuario);
        }

    }

    public void setEmpleadores(List<Empleador> empleadores) {
        mEmpleadores = empleadores;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mEmpleadores != null)
            return mEmpleadores.size();
        else return 0;
    }

    public class EmpleadorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombreUsuario;
        private final TextView textViewContacto;
        private final ImageView imageViewUsuario;

        private EmpleadorViewHolder(View itemView) {
            super(itemView);
            textViewNombreUsuario = itemView.findViewById(R.id.textViewNombreUsuario);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);
            imageViewUsuario = itemView.findViewById(R.id.imageViewUser);
        }
    }

}
