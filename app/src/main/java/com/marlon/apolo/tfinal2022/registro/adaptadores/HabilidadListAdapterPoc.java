package com.marlon.apolo.tfinal2022.registro.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Habilidad;

import java.util.List;

public class HabilidadListAdapterPoc extends RecyclerView.Adapter<HabilidadListAdapterPoc.HabilidadListAdapterPocHolder> {

    private static final String TAG = HabilidadListAdapterPoc.class.getSimpleName();
    private final LayoutInflater mInflater;
    private List<Habilidad> mWords; // Cached copy of words
    private static ClickListener clickListener;

    public HabilidadListAdapterPoc(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public HabilidadListAdapterPocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_view_item_skill_check_box, parent, false);
        return new HabilidadListAdapterPocHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HabilidadListAdapterPocHolder holder, int position) {
        if (mWords != null) {
            Habilidad current = mWords.get(position);
            holder.wordItemView.setText(current.getNombreHabilidad());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No word");
        }
    }

    /**
     * Associates a list of words with this adapter
     */
    public void setHabillidades(List<Habilidad> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * mWords has not been updated (means initially, it's null, and we can't return null).
     */
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
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
    public Habilidad getOficioAtPosition(int position) {
        return mWords.get(position);
    }

    class HabilidadListAdapterPocHolder extends RecyclerView.ViewHolder {
        private final CheckBox wordItemView;

        private HabilidadListAdapterPocHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.checkBoxHabilidad);
            wordItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.d(TAG, "Checked: " + getOficioAtPosition(getAbsoluteAdapterPosition()).getNombreHabilidad());
                        mWords.get(getAbsoluteAdapterPosition()).setHabilidadSeleccionada(true);
                    } else {
                        Log.d(TAG, "No checked: " + getOficioAtPosition(getAbsoluteAdapterPosition()).getNombreHabilidad());
                        mWords.get(getAbsoluteAdapterPosition()).setHabilidadSeleccionada(false);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        HabilidadListAdapterPoc.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

}
