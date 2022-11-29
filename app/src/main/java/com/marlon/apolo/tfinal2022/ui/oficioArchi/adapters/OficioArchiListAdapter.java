package com.marlon.apolo.tfinal2022.ui.oficioArchi.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.model.OficioArchiModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.OficioArchiEditDeleteActivity;

import java.util.ArrayList;
import java.util.List;

public class OficioArchiListAdapter extends RecyclerView.Adapter<OficioArchiListAdapter.OficioArchiViewHolder> {

    private final LayoutInflater mInflater;
    private List<OficioArchiModel> mWords; // Cached copy of words
    private Context contextInstance; // Cached copy of words

    public OficioArchiListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        contextInstance = context;
    }

    @Override
    public OficioArchiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.oficio_archi_item, parent, false);
        return new OficioArchiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OficioArchiViewHolder holder, int position) {
//        Log.d("TAG", String.valueOf(mWords.size()));

        if (mWords != null) {
            OficioArchiModel current = mWords.get(position);
            holder.wordItemView.setText(current.getNombre());
            if (current.getUriPhoto() != null) {
                Glide.with(contextInstance)
                        .load(current.getUriPhoto())
                        .placeholder(R.drawable.ic_oficios)
                        .into(holder.imageView);
            } else {
                Glide.with(contextInstance)
                        .load(R.drawable.ic_oficios)
                        .placeholder(R.drawable.ic_oficios)
                        .into(holder.imageView);
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Word");
        }
    }

    public void setOficios(List<OficioArchiModel> words) {
//        Log.d("TAG", String.valueOf(words.size()));
        mWords = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
        else return 0;
    }

    class OficioArchiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView wordItemView;
        private final ImageView imageView;

        private OficioArchiViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageViewIcono);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();

            // Use that to access the affected item in mWordList.
            OficioArchiModel element = mWords.get(mPosition);

            //Toast.makeText(contextInstance, element.toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(contextInstance, OficioArchiEditDeleteActivity.class);
            intent.putExtra("oficioModel",element);
            contextInstance.startActivity(intent);

        }
    }
}