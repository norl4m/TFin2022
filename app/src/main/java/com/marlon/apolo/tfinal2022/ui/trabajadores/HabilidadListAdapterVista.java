package com.marlon.apolo.tfinal2022.ui.trabajadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;

import java.util.List;

public class HabilidadListAdapterVista extends RecyclerView.Adapter<HabilidadListAdapterVista.HabilidadViewHolder> {
    private List<Habilidad> habilidadList; // Cached copy of words
    private Context context;
    private int optionView;
    private LayoutInflater mInflater;

    public HabilidadListAdapterVista(Context contextVar) {
        mInflater = LayoutInflater.from(contextVar);
        context = contextVar;
    }

    @NonNull
    @Override
    public HabilidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_view_item_skill_vista, parent, false);
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

        private HabilidadViewHolder(View itemView) {
            super(itemView);
            textViewHabilidadName = itemView.findViewById(R.id.textViewHabilidad);

        }
    }
}
