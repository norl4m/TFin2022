package com.marlon.apolo.tfinal2022.ui.oficios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;

public class OficioRegistroListAdapter extends RecyclerView.Adapter<OficioRegistroListAdapter.OficioViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Oficio> oficios;

    public OficioRegistroListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficios() {
        return oficios;
    }

    @NonNull
    @Override
    public OficioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_oficio_with_habilidad_list_item, parent, false);
        return new OficioRegistroListAdapter.OficioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OficioViewHolder holder, int position) {
        Oficio current = oficios.get(position);
        holder.textViewOficioName.setText(String.format("%s", current.getNombre()));
//        holder.textViewOficioName.setText(String.format("%s", current.getIdOficio()));

        //holder.switchOnOff.setText(context.getResources().getString(R.string.no_text));
        //holder.switchOnOff.setChecked(false);
        if (holder.switchOnOff.isChecked()) {
            current.setEstadoRegistro(true);
        } else {
            current.setEstadoRegistro(false);
        }

        oficios.set(position, current);

        final HabilidadListAdapter adapter = new HabilidadListAdapter(context);
        holder.recyclerView.setAdapter(adapter);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setHabilidades(current.getHabilidadArrayList());


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
        private final TextView textViewOficioName;
        private SwitchCompat switchOnOff;
        private RecyclerView recyclerView;

        private OficioViewHolder(View itemView) {
            super(itemView);
            textViewOficioName = itemView.findViewById(R.id.textViewOficioName);
            switchOnOff = itemView.findViewById(R.id.cardViewJobSwitchOnOff);
            recyclerView = itemView.findViewById(R.id.recyclerViewHabilidades);
            recyclerView.setVisibility(View.GONE);

            switchOnOff.setText(context.getResources().getString(R.string.no_text));
            switchOnOff.setChecked(false);

            switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) { // The switch is enabled
                        switchOnOff.setText(context.getResources().getString(R.string.yes_text));
                        oficios.get(getAdapterPosition()).setEstadoRegistro(true);
                        recyclerView.setVisibility(View.VISIBLE);

                    } else { // The switch is disabled
                        switchOnOff.setText(context.getResources().getString(R.string.no_text));
                        oficios.get(getAdapterPosition()).setEstadoRegistro(false);
                        recyclerView.setVisibility(View.GONE);

                    }
                }
            });
        }
    }
}
