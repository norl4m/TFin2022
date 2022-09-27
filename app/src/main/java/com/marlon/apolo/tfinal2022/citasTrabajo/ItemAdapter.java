package com.marlon.apolo.tfinal2022.citasTrabajo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Item;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final String TAG = ItemAdapter.class.getSimpleName();

    private int viewMode;

    public ArrayList<Item> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    private ArrayList<Item> itemArrayList;
    private LayoutInflater layoutInflater;
    private Context context;

    public ItemAdapter(Context context, int viewMode) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewMode = viewMode;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView =
                layoutInflater.inflate(R.layout.card_view_item,
                        parent, false);
        return new ItemViewHolder(mItemView, this);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Retrieve the data for that position
        Item mCurrent = itemArrayList.get(position);
        // Add the data to the view
        holder.detailItem.setText(String.format("%s", mCurrent.getDetail()));
        double parteDecimal = mCurrent.getPrice() % 1; // Lo que sobra de dividir al número entre 1
        double parteEntera = mCurrent.getPrice() - parteDecimal; // Le quitamos la parte decimal usando una resta
        String parteDecima = String.format("%.2f", parteDecimal); // Lo que sobra de dividir al número entre 1

        Log.d(TAG, parteDecima);
        Log.d(TAG, parteDecima.substring(2));
        String parteFormateada = parteDecima.substring(2); // Lo que sobra de dividir al número entre 1
        Log.d(TAG, String.valueOf(parteFormateada.length()));

        holder.detailItemPrice.setText(String.valueOf((int) parteEntera));
        holder.detailItemPriceCents1.setText(parteDecima.substring(2));

        switch (viewMode) {
            case 0:
            case 1:
                holder.detailItem.setEnabled(false);
                holder.detailItemPrice.setEnabled(false);
                holder.detailItemPriceCents1.setEnabled(false);
                holder.imageButtonQuitarItem.setEnabled(false);
                holder.imageButtonQuitarItem.setVisibility(View.GONE);
                holder.detailItem.setTextColor(context.getResources().getColor(R.color.black));
                holder.detailItemPrice.setTextColor(context.getResources().getColor(R.color.black));
                holder.detailItemPriceCents1.setTextColor(context.getResources().getColor(R.color.black));

                break;
            case 2:
                holder.detailItem.setEnabled(true);
                holder.detailItemPrice.setEnabled(true);
                holder.detailItemPriceCents1.setEnabled(true);
                holder.imageButtonQuitarItem.setEnabled(true);
                break;
        }
        /*holder.detailItem.setEnabled(false);
        holder.detailItemPrice.setEnabled(false);
        holder.detailItemPriceCents1.setEnabled(false);
        holder.imageButtonQuitarItem.setEnabled(false);*/
    }

    @Override
    public int getItemCount() {
        if (itemArrayList != null)
            return itemArrayList.size();
        else return 0;
//        return itemArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextInputEditText detailItem;
        private final TextInputEditText detailItemPrice;
        private final TextInputEditText detailItemPriceCents1;
        private final ItemAdapter mAdapter;
        private final ImageButton imageButtonQuitarItem;

        String parteEntera = "0";
        String parteDecimal = "00";
        String completePrice = "0.00";

        @SuppressLint("ResourceAsColor")
        public ItemViewHolder(@NonNull View itemView, ItemAdapter jobAdapter) {
            super(itemView);
            detailItem = itemView.findViewById(R.id.cardViewItemTextDetail);
            detailItemPrice = itemView.findViewById(R.id.cardViewItemTextPrice);
            detailItemPriceCents1 = itemView.findViewById(R.id.cardViewItemTextPriceCents1);
            imageButtonQuitarItem = itemView.findViewById(R.id.imgButtonQuitarItem);

            this.mAdapter = jobAdapter;
            /*detailItem.setEnabled(false);
            detailItemPrice.setEnabled(false);
            detailItemPriceCents1.setEnabled(false);
            imageButtonQuitarItem.setEnabled(false);*/

//            itemView.setOnClickListener(this);
            imageButtonQuitarItem.setOnClickListener(this);
            detailItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String elNuevoTexto = s.toString();
                    Log.d(TAG, "Cambio a" + elNuevoTexto);
                    int mPosition = getLayoutPosition();
                    Item element = itemArrayList.get(mPosition);
                    element.setDetail(elNuevoTexto);
                    itemArrayList.set(mPosition, element);
                    Log.d(TAG, element.toString());
                    if (!itemArrayList.get(mPosition).getDetail().isEmpty() && !(itemArrayList.get(mPosition).getPrice() == 0)) {
                        Log.d(TAG, "Se puede crear cita con detalle");
                    }
                    // Notify the adapter, that the data has changed so it can
                    // update the RecyclerView to display the data.
                    //   mAdapter.notifyDataSetChanged();
                }
            });
            detailItemPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String elNuevoTexto = s.toString();
                    Log.d(TAG, "Cambio a" + elNuevoTexto);
                    int mPosition = getLayoutPosition();
                    Item element = itemArrayList.get(mPosition);
                    parteEntera = elNuevoTexto;

                    completePrice = parteEntera + "." + parteDecimal;

                    element.setPrice(Float.parseFloat(completePrice));
                    itemArrayList.set(mPosition, element);
                    Log.d(TAG, element.toString());
                    //snackBar(itemView);
                    CitaTrabajoActivity citaActivity = null;

                    try {
                        citaActivity = CitaTrabajoActivity.citaActivity;
                        float precio = 0;
                        for (Item item : itemArrayList) {
                            precio = precio + item.getPrice();
                            citaActivity.setPrecio(precio);
                        }
                        citaActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", citaActivity.getPrecio()));

                        if (!itemArrayList.get(mPosition).getDetail().isEmpty() && !(itemArrayList.get(mPosition).getPrice() == 0)) {
                            Log.d(TAG, "Se puede crear cita con detalle");
                            //snackBar(itemView);

                        }
                    } catch (Exception e) {

                    }

                    DetalleServicioActivity detalleServicioActivity = null;

                    try {
                        detalleServicioActivity = detalleServicioActivity.detalleServicioActivity;
                        float precio = 0;
                        for (Item item : itemArrayList) {
                            precio = precio + item.getPrice();
                        }
                        detalleServicioActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", precio));

                        if (!itemArrayList.get(mPosition).getDetail().isEmpty() && !(itemArrayList.get(mPosition).getPrice() == 0)) {
                            Log.d(TAG, "Se puede crear cita con detalle");
                            //snackBar(itemView);

                        }
                    } catch (Exception e) {

                    }

                    // Notify the adapter, that the data has changed so it can
                    // update the RecyclerView to display the data.
//                    mAdapter.notifyDataSetChanged();
                }
            });

            detailItemPriceCents1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String elNuevoTexto = s.toString();
                    Log.d(TAG, "Cambio a" + elNuevoTexto);
                    int mPosition = getLayoutPosition();
                    parteDecimal = elNuevoTexto;

                    completePrice = parteEntera + "." + parteDecimal;
                    Log.d(TAG, parteEntera);
                    Log.d(TAG, parteDecimal);
                    Log.d(TAG, completePrice);


                    Item element = itemArrayList.get(mPosition);
//                    try {
//                        element.setPrice(Float.parseFloat(completePrice));
//                    } catch (Exception e) {
//
//                    }
                    if (completePrice.length() == 1 && completePrice.contains(".")) {
                        Log.d(TAG, "ENCERar");
                        element.setPrice(0);
                    } else {
                        element.setPrice(Float.parseFloat(completePrice));

                    }

                    itemArrayList.set(mPosition, element);

//                    try {
//                        CitaActivity citaActivity = CitaActivity.citaActivity;
//                        citaActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", citaActivity.getPrecio()));
//                    } catch (Exception e) {
//
//                    }
                    CitaTrabajoActivity citaActivity = null;

                    try {
                        citaActivity = CitaTrabajoActivity.citaActivity;
                        float precio = 0;
                        for (Item item : itemArrayList) {
                            precio = precio + item.getPrice();
                            citaActivity.setPrecio(precio);
                        }
                        citaActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", citaActivity.getPrecio()));
                    } catch (Exception e) {

                    }

                    Log.d(TAG, element.toString());
                    if (!itemArrayList.get(mPosition).getDetail().isEmpty() && !(itemArrayList.get(mPosition).getPrice() == 0)) {
                        Log.d(TAG, "Se puede crear cita con detalle");
                        //snackBar(itemView);

//                        CitaActivity citaActivity = null;
//
//                        try {
//                            citaActivity = CitaActivity.citaActivity;
//
//                        } catch (Exception e) {
//
//                        }
//                        float precio = 0;
//                        for (Item item : itemArrayList) {
//                            precio = precio + item.getPrice();
//                            citaActivity.setPrecio(precio);
//                        }
//                        citaActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", citaActivity.getPrecio()));
//
//
                    }

                    DetalleServicioActivity detalleServicioActivity = null;

                    try {
                        detalleServicioActivity = detalleServicioActivity.detalleServicioActivity;
                        float precio = 0;
                        for (Item item : itemArrayList) {
                            precio = precio + item.getPrice();
                        }
                        detalleServicioActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", precio));

                    } catch (Exception e) {

                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            Item element = itemArrayList.get(mPosition);
            Log.d(TAG, element.toString());
            itemArrayList.remove(element);
            if (itemArrayList.size() == 0) {
                try {
                    CitaTrabajoActivity citaActivity = CitaTrabajoActivity.citaActivity;
                    citaActivity.setPrecio(0);
                    citaActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", citaActivity.getPrecio()));
                } catch (Exception e) {

                }
                DetalleServicioActivity detalleServicioActivity = null;

                try {
                    detalleServicioActivity = detalleServicioActivity.detalleServicioActivity;
                    float precio = 0;

                    detalleServicioActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", precio));

                } catch (Exception e) {

                }
            } else {
                //snackBar(itemView);
                CitaTrabajoActivity citaActivity = null;

                try {
                    citaActivity = CitaTrabajoActivity.citaActivity;
                    float precio = 0;
                    for (Item item : itemArrayList) {
                        precio = precio + item.getPrice();
                        citaActivity.setPrecio(precio);
                    }
                    citaActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", citaActivity.getPrecio()));
                } catch (Exception e) {

                }
                DetalleServicioActivity detalleServicioActivity = null;

                try {
                    detalleServicioActivity = detalleServicioActivity.detalleServicioActivity;
                    float precio = 0;
                    for (Item item : itemArrayList) {
                        precio = precio + item.getPrice();
                    }
                    detalleServicioActivity.getTextViewTotal().setText(String.format("Total: $ %.2f", precio));

                } catch (Exception e) {

                }


            }
            notifyDataSetChanged();
            // Notify the adapter, that the data has changed so it can
            // update the RecyclerView to display the data.

        }
    }


    public void setItems(ArrayList<Item> items) {
        itemArrayList = new ArrayList<>();
        itemArrayList = items;
        notifyDataSetChanged();
    }

    public void addItem(Item item) {
        itemArrayList.add(item);
        notifyDataSetChanged();
    }

    public void snackBar(View view) {
        Snackbar mySnackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        mySnackbar.setAction("Crear cita", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mySnackbar.show();

    }
}
