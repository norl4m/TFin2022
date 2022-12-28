package com.marlon.apolo.tfinal2022.citasTrabajoArchi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marlon.apolo.tfinal2022.R;

import java.util.ArrayList;
import java.util.List;

public class CitaTrabajoArchiListAdapter extends RecyclerView.Adapter<CitaTrabajoArchiListAdapter.WordViewHolder> {

    private final LayoutInflater mInflater;
    private List<CitaTrabajoArchi> mWords; // Cached copy of words
    private static ClickListener clickListener;
    private static ClickListenerDelete clickListenerDelete;

    public CitaTrabajoArchiListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.cita_trabajo_archi_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (mWords != null) {
            CitaTrabajoArchi current = mWords.get(position);
            holder.wordItemView.setText(current.getObservaciones());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("R.string.no_word");
        }
    }

    /**
     * Associates a list of words with this adapter
     */
    public void setWords(List<CitaTrabajoArchi> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    public void setWordsByWord(CitaTrabajoArchi citaTrabajoArchi) {
        if (mWords == null) {
            mWords = new ArrayList<>();
        }
        mWords.add(citaTrabajoArchi);
//        notifyDataSetChanged();
        notifyItemInserted(mWords.size() - 1);
    }

    public void setUpdateWord(int index, CitaTrabajoArchi citaTrabajoArchi) {
//        mWords = words;
        mWords.set(index, citaTrabajoArchi);
//        notifyDataSetChanged();
        notifyItemChanged(index);
    }

    public void setRemoveWord(int index) {
//        mWords = words;
        mWords.remove(index);
//        notifyDataSetChanged();
        notifyItemRemoved(index);
    }

    public List<CitaTrabajoArchi> getmWords() {
        return mWords;
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
    public CitaTrabajoArchi getWordAtPosition(int position) {
        return mWords.get(position);
    }

    public int getWorPosition(String id) {
        int position = 0;
        for (CitaTrabajoArchi ct : mWords) {
            if (ct.getId().equals(id)) {
                break;
            }
            position++;
        }
        return position;
    }

    public void removeItemFromRecyclerView(int position) {
//        mWords.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItemFromRecyclerViewWithSwiped(int position) {
        mWords.remove(position);
        notifyItemRemoved(position);
    }

    public void updateItemFromRecyclerView(int position) {
//        mWords.set(position, );
        notifyItemChanged(position);
    }


    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;
        private FloatingActionButton delete;

        private WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
            delete = itemView.findViewById(R.id.deleteCita);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListenerDelete.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        CitaTrabajoArchiListAdapter.clickListener = clickListener;
    }

    public void setOnItemClickListenerDelete(ClickListenerDelete clickListenerDelete) {
        CitaTrabajoArchiListAdapter.clickListenerDelete = clickListenerDelete;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

    public interface ClickListenerDelete {
        void onItemClick(View v, int position);
    }
}