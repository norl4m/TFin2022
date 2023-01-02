package com.marlon.apolo.tfinal2022.ui.bienvenido.adaptaders;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.buscador.view.BuscadorActivity;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.List;
import java.util.Random;

public class OficioArchiVistaListAdapter extends RecyclerView.Adapter<OficioArchiVistaListAdapter.OficioArchiViewHolder> {

    private final LayoutInflater mInflater;
    private List<Oficio> mWords; // Cached copy of words
    private Context contextInstance; // Cached copy of words

    public OficioArchiVistaListAdapter(Context context) {
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
            Oficio current = mWords.get(position);
            holder.wordItemView.setText(current.getNombre());
            int itemsColor = ContextCompat.getColor(contextInstance, R.color.white);
            holder.wordItemView.setTextColor(itemsColor);
            Drawable[] itrems = new Drawable[]{
//                    AppCompatResources.getDrawable(contextInstance, R.drawable.bg9),
                    AppCompatResources.getDrawable(contextInstance, R.drawable.bg10),
                    AppCompatResources.getDrawable(contextInstance, R.drawable.bg11),
                    AppCompatResources.getDrawable(contextInstance, R.drawable.bg12)
            };
            final int min = 0;
            final int max = itrems.length - 1;
            int random = new Random().nextInt((max - min) + 1) + min;
            holder.itemView.setBackground(itrems[random]);
            holder.imageView.setColorFilter(itemsColor);
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

    public void setOficios(List<Oficio> words) {
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
            Oficio element = mWords.get(mPosition);

            Intent intent = new Intent(contextInstance, BuscadorActivity.class);
            intent.setAction("android.intent.action.SEARCH");
            intent.putExtra(SearchManager.QUERY, element.getNombre());
            intent.putExtra("offset", 1);
            intent.putExtra("searchMode", 0);
            contextInstance.startActivity(intent);

        }
    }
}