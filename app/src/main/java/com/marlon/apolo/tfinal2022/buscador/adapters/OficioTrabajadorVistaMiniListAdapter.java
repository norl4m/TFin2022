package com.marlon.apolo.tfinal2022.buscador.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;
import java.util.Random;

public class OficioTrabajadorVistaMiniListAdapter extends RecyclerView.Adapter<OficioTrabajadorVistaMiniListAdapter.OficioViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Oficio> oficios;

    public OficioTrabajadorVistaMiniListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficios() {
        return oficios;
    }

    @NonNull
    @Override
    public OficioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_oficio_list_mini_item, parent, false);
        return new OficioTrabajadorVistaMiniListAdapter.OficioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OficioViewHolder holder, int position) {
        Oficio current = oficios.get(position);
        holder.textViewOficioName.setText(String.format("%s", current.getNombre()));
        Drawable[] itrems = new Drawable[]{
//                    AppCompatResources.getDrawable(contextInstance, R.drawable.bg9),
                AppCompatResources.getDrawable(context, R.drawable.bg10),
                AppCompatResources.getDrawable(context, R.drawable.bg11),
                AppCompatResources.getDrawable(context, R.drawable.bg12)
        };
        final int min = 0;
        final int max = itrems.length - 1;
        int random = new Random().nextInt((max - min) + 1) + min;
        holder.itemView.setBackground(itrems[random]);

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
        }
    }
}
