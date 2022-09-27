package com.marlon.apolo.tfinal2022.registro.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;

public class OficioListAdapterPoc extends RecyclerView.Adapter<OficioListAdapterPoc.OficioListAdapterPocHolder> {

    private final LayoutInflater mInflater;
    private List<Oficio> oficioList; // Cached copy of words
    private static ClickListener clickListener;
    private Context contextInstance;

    public OficioListAdapterPoc(Context context) {
        contextInstance = context;
        mInflater = LayoutInflater.from(context);
    }

    public List<Oficio> getOficioList() {
        return oficioList;
    }

    @Override
    public OficioListAdapterPocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.card_view_oficio_with_habilidad_list_item_check_box, parent, false);
        return new OficioListAdapterPocHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OficioListAdapterPocHolder holder, int position) {
        if (oficioList != null) {
            Oficio current = oficioList.get(position);
            holder.wordItemView.setText(current.getNombre());


            HabilidadListAdapterPoc habilidadListAdapterPoc = new HabilidadListAdapterPoc(contextInstance);/*paso 1*/

            holder.recyclerViewHabilidades.setAdapter(habilidadListAdapterPoc);
            holder.recyclerViewHabilidades.setLayoutManager(new LinearLayoutManager(contextInstance));

            if (current.getHabilidadArrayList() != null) {
                if (current.getHabilidadArrayList().size() > 5) {
                    habilidadListAdapterPoc.setHabillidades(current.getHabilidadArrayList());

                }
            }


        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No word");
        }
    }

    /**
     * Associates a list of words with this adapter
     */
    public void setOficios(List<Oficio> words) {
        oficioList = words;
        notifyDataSetChanged();
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * mWords has not been updated (means initially, it's null, and we can't return null).
     */
    @Override
    public int getItemCount() {
        if (oficioList != null)
            return oficioList.size();
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
    public Oficio getOficioAtPosition(int position) {
        return oficioList.get(position);
    }

    class OficioListAdapterPocHolder extends RecyclerView.ViewHolder {
        private final String TAG = OficioListAdapterPocHolder.class.getSimpleName();
        private final CheckBox wordItemView;
        private RecyclerView recyclerViewHabilidades;

        private OficioListAdapterPocHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.checkBoxOficio);
            recyclerViewHabilidades = itemView.findViewById(R.id.recyclerViewHabilidades);
            wordItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.d(TAG, "Checked: " + getOficioAtPosition(getAbsoluteAdapterPosition()).getNombre());
                        oficioList.get(getAbsoluteAdapterPosition()).setEstadoRegistro(true);
                    } else {
                        Log.d(TAG, "No checked: " + getOficioAtPosition(getAbsoluteAdapterPosition()).getNombre());
                        oficioList.get(getAbsoluteAdapterPosition()).setEstadoRegistro(false);
                    }
                }
            });

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        clickListener.onItemClick(view, getAbsoluteAdapterPosition());
//                    }catch (Exception e){
//                        Log.e(TAG,e.toString());
//                    }
//                }
//            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        OficioListAdapterPoc.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

}
