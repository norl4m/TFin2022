package com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;

import java.util.List;

public class SpecialHabilidad2ListAdapter extends RecyclerView.Adapter<SpecialHabilidad2ListAdapter.HabilidadViewHolder> {
    private static final String TAG = SpecialHabilidad2ListAdapter.class.getSimpleName();
    private List<Habilidad> habilidadList; // Cached copy of words
    private Context context;
    private int optionView;
    private LayoutInflater mInflater;

    public SpecialHabilidad2ListAdapter(Context contextVar) {
        mInflater = LayoutInflater.from(contextVar);
        context = contextVar;
    }

    @NonNull
    @Override
    public HabilidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_view_item_skill_check_box, parent, false);
        return new HabilidadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HabilidadViewHolder holder, int position) {
        if (habilidadList != null) {
            Habilidad current = habilidadList.get(position);
            try {
                holder.checkBoxHabilidad.setText(current.getNombreHabilidad());
                holder.checkBoxHabilidad.setChecked(current.isHabilidadSeleccionada());
            } catch (Exception e) {
                Log.d(TAG, e.toString());
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
        private CheckBox checkBoxHabilidad;

        private HabilidadViewHolder(View itemView) {
            super(itemView);
            checkBoxHabilidad = itemView.findViewById(R.id.checkBoxHabilidad);


            checkBoxHabilidad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) { // The switch is enabled
                        checkBoxHabilidad.setChecked(true);
                        habilidadList.get(getAdapterPosition()).setHabilidadSeleccionada(true);
                    } else { // The switch is disabled
                        checkBoxHabilidad.setChecked(false);
                        habilidadList.get(getAdapterPosition()).setHabilidadSeleccionada(false);
                    }
                }
            });

        }
    }
}
