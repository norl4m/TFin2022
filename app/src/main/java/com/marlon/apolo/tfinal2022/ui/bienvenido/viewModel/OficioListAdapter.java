package com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.ArrayList;
import java.util.List;

public class OficioListAdapter extends RecyclerView.Adapter<OficioListAdapter.WordViewHolder> {

    private final LayoutInflater mInflater;
    private List<Oficio> mWords; // Cached copy of words
    private static ClickListener clickListener;

    public OficioListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.card_view_presentacion_oficio_vista, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (mWords != null) {
            Oficio current = mWords.get(position);
            holder.wordItemView.setText(current.getNombre());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText(R.string.no_resultados);
        }
    }

    /**
     * Associates a list of words with this adapter
     */
    public void setWords(List<Oficio> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    public void setNoResultados() {
        Oficio oficio = new Oficio();
        mWords = new ArrayList<>();
        oficio.setIdOficio(null);
        oficio.setNombre("No existen resultados!");
        mWords.add(oficio);
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
    public Oficio getWordAtPosition(int position) {
        return mWords.get(position);
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textViewOficioName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Oficio oficioSelected = mWords.get(getAdapterPosition());
                    if (oficioSelected.getIdOficio() != null) {
                        clickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        OficioListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

}
