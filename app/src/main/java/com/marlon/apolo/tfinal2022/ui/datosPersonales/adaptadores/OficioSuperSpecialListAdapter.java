package com.marlon.apolo.tfinal2022.ui.datosPersonales.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Oficio;

import java.util.ArrayList;

public class OficioSuperSpecialListAdapter extends
        RecyclerView.Adapter<OficioSuperSpecialListAdapter.OSuperListAdapteWordViewHolder> {

    private ArrayList<Oficio> oficioArrayList;
    private final LayoutInflater mInflater;
    private Context contextInstance;

    public class OSuperListAdapteWordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final CheckBox checkBox;
        public final RecyclerView recyclerView;
        final OficioSuperSpecialListAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter  The adapter that manages the the data and views
         *                 for the RecyclerView.
         */
        public OSuperListAdapteWordViewHolder(View itemView, OficioSuperSpecialListAdapter adapter) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxOficio);
            recyclerView = itemView.findViewById(R.id.recyclerViewHabilidades);
            this.mAdapter = adapter;
//            itemView.setOnClickListener(this);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) { // The switch is enabled
                        oficioArrayList.get(getAbsoluteAdapterPosition()).setEstadoRegistro(true);
                    } else { // The switch is disabled
                        checkBox.setChecked(false);
                        oficioArrayList.get(getAbsoluteAdapterPosition()).setEstadoRegistro(false);
                    }
                    checkBox.setChecked(oficioArrayList.get(getAbsoluteAdapterPosition()).isEstadoRegistro());
                }
            });
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();

            // Use that to access the affected item in mWordList.
            Oficio element = oficioArrayList.get(mPosition);
            // Change the word in the mWordList.

            oficioArrayList.set(mPosition, element);
            // Notify the adapter, that the data has changed so it can
            // update the RecyclerView to display the data.
            //mAdapter.notifyDataSetChanged();
        }
    }

    public OficioSuperSpecialListAdapter(Context context, ArrayList<Oficio> wordList) {
        mInflater = LayoutInflater.from(context);
        this.oficioArrayList = wordList;
    }

    public OficioSuperSpecialListAdapter(Context context) {
        contextInstance = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setOficioArrayList(ArrayList<Oficio> oficioArrayList) {
        this.oficioArrayList = oficioArrayList;
        notifyDataSetChanged();
    }

    public ArrayList<Oficio> getOficioArrayList() {
        return oficioArrayList;
    }

    public void updateViewWithOficio(int position, Oficio oficio) {
        oficioArrayList.set(position, oficio);
//        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after
     *                 it is bound to an adapter position.
     * @param viewType The view type of the new View. @return A new ViewHolder
     *                 that holds a View of the given view type.
     */
    @Override
    public OSuperListAdapteWordViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(R.layout.card_view_oficio_with_habilidad_list_item_check_box, parent, false);
        return new OSuperListAdapteWordViewHolder(mItemView, this);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent
     *                 the contents of the item at the given position in the
     *                 data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(OSuperListAdapteWordViewHolder holder,
                                 int position) {
        // Retrieve the data for that position.
        Oficio mCurrent = oficioArrayList.get(position);
        // Add the data to the view holder.
        holder.checkBox.setText(mCurrent.getNombre());
        holder.checkBox.setChecked(mCurrent.isEstadoRegistro());


        final HabilidadSuperSpecialListAdapter adapter = new HabilidadSuperSpecialListAdapter(contextInstance);
        holder.recyclerView.setAdapter(adapter);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(contextInstance));
        adapter.setHabilidadArrayList(mCurrent.getHabilidadArrayList());

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (oficioArrayList != null)
            return oficioArrayList.size();
        else return 0;
    }
}