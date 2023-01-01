package com.marlon.apolo.tfinal2022.ui.oficios;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.buscador.view.BuscadorActivity;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;

public class OficioVistaListAdapter extends RecyclerView.Adapter<OficioVistaListAdapter.OficioViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Oficio> oficios;

    public OficioVistaListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficios() {
        return oficios;
    }

    @NonNull
    @Override
    public OficioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_oficio, parent, false);
        return new OficioVistaListAdapter.OficioViewHolder(itemView);
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

    public class OficioViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewOficioName;

        private OficioViewHolder(View itemView) {
            super(itemView);
            textViewOficioName = itemView.findViewById(R.id.textViewOficioName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BuscadorActivity.class);
                    intent.setAction("android.intent.action.SEARCH");
                    intent.putExtra(SearchManager.QUERY, oficios.get(getAdapterPosition()).getNombre());
                    intent.putExtra("offset", 1);
                    intent.putExtra("searchMode", 0);
                    context.startActivity(intent);
                }
            });
        }
    }
}
