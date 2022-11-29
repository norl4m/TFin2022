package com.marlon.apolo.tfinal2022.ui.trabajadores;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.buscador.view.BuscadorActivity;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;

public class OficioHabilidadVistaListAdapter extends RecyclerView.Adapter<OficioHabilidadVistaListAdapter.OficioViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Oficio> oficios;
    private Context contextInstance;

    public OficioHabilidadVistaListAdapter(Context context) {
        this.context = context;
        contextInstance = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficios() {
        return oficios;
    }

    @NonNull
    @Override
    public OficioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_oficio_with_habilidad_list_item_vista, parent, false);
        return new OficioHabilidadVistaListAdapter.OficioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OficioViewHolder holder, int position) {
        Oficio current = oficios.get(position);
        holder.textViewOficioName.setText(String.format("%s", current.getNombre()));


        final HabilidadListAdapterVista adapter = new HabilidadListAdapterVista(contextInstance);
        holder.recyclerView.setAdapter(adapter);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(contextInstance));
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
        private final RecyclerView recyclerView;

        private OficioViewHolder(View itemView) {
            super(itemView);
            textViewOficioName = itemView.findViewById(R.id.textViewOficio);
            recyclerView = itemView.findViewById(R.id.recyclerViewHabilidades);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BuscadorActivity.class);
                    intent.setAction("android.intent.action.SEARCH");
                    intent.putExtra(SearchManager.QUERY, oficios.get(getAbsoluteAdapterPosition()).getNombre());
                    intent.putExtra("offset", 1);
                    context.startActivity(intent);
                }
            });
        }
    }
}
