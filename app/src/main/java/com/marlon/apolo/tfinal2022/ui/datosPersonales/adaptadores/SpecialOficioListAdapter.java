package com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;

public class SpecialOficioListAdapter extends RecyclerView.Adapter<SpecialOficioListAdapter.OficioViewHolder> {

    private static final String TAG = SpecialOficioListAdapter.class.getSimpleName();
    private final Context context;
    private final LayoutInflater inflater;
    private List<Oficio> oficios;

    public SpecialOficioListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficios() {
        return oficios;
    }

    @NonNull
    @Override
    public OficioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_oficio_with_habilidad_list_item_check_box, parent, false);
        return new SpecialOficioListAdapter.OficioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OficioViewHolder holder, int position) {
        Oficio current = oficios.get(position);
        try {
//            for (Habilidad hB : current.getHabilidadArrayList()) {
            Log.d(TAG, current.toString());
//            }
        } catch (Exception e) {

        }
        holder.checkBoxOficio.setText(String.format("%s", current.getNombre()));
        holder.checkBoxOficio.setChecked(current.isEstadoRegistro());

        //oficios.set(position, current);

        final SpecialHabilidadListAdapter adapter = new SpecialHabilidadListAdapter(context);
        holder.recyclerView.setAdapter(adapter);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setHabilidades(current.getHabilidadArrayList());

//        if (current.isEstadoRegistro()) { // The switch is enabled
//            holder.recyclerView.setVisibility(View.VISIBLE);
//        } else { // The switch is disabled
//            holder.recyclerView.setVisibility(View.GONE);
//        }
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

    public class OficioViewHolder extends RecyclerView.ViewHolder {
        //        private final TextView textViewOficioName;
        private final CheckBox checkBoxOficio;
        private RecyclerView recyclerView;

        private OficioViewHolder(View itemView) {
            super(itemView);
            checkBoxOficio = itemView.findViewById(R.id.checkBoxOficio);
            recyclerView = itemView.findViewById(R.id.recyclerViewHabilidades);
            checkBoxOficio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) { // The switch is enabled
                        checkBoxOficio.setChecked(true);
                        oficios.get(getAdapterPosition()).setEstadoRegistro(true);
//                        recyclerView.setVisibility(View.VISIBLE);

                    } else { // The switch is disabled
                        checkBoxOficio.setChecked(false);
                        oficios.get(getAdapterPosition()).setEstadoRegistro(false);
//                        recyclerView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
