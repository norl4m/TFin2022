package com.marlon.apolo.tfinal2022.citasTrabajo.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.NuevaCitaTrabajoActivity;
import com.marlon.apolo.tfinal2022.model.Item;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class ItemAdapterPoCCreate extends RecyclerView.Adapter<ItemAdapterPoCCreate.ItemViewHolder> {

    private final String TAG = ItemAdapterPoCCreate.class.getSimpleName();

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

    public ItemAdapterPoCCreate(Context context, int viewMode) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewMode = viewMode;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView =
                layoutInflater.inflate(R.layout.card_view_item_poc,
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
//        holder.detailItemPrice.setText(mCurrent.getPriceFormat());

        switch (viewMode) {
            case 0:
            case 1:
                holder.detailItem.setEnabled(false);
                holder.detailItemPrice.setEnabled(false);
                holder.imageButtonQuitarItem.setEnabled(false);
                holder.imageButtonQuitarItem.setVisibility(View.GONE);
                holder.detailItem.setTextColor(context.getResources().getColor(R.color.black));
                holder.detailItemPrice.setTextColor(context.getResources().getColor(R.color.black));

                break;
            case 2:
                holder.detailItem.setEnabled(true);
                holder.detailItemPrice.setEnabled(true);
                holder.imageButtonQuitarItem.setEnabled(true);
                break;
        }

        int someColorFrom = 0;
        int someColorTo = 0;


        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
//                someColorFrom = ContextCompat.getColor(activity, R.color.black_minus);
                someColorFrom = ContextCompat.getColor(context, R.color.white);
                someColorTo = ContextCompat.getColor(context, R.color.green_minus);

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                someColorFrom = ContextCompat.getColor(context, R.color.black_minus);
                someColorTo = ContextCompat.getColor(context, R.color.teal_100);

                break;
        }

//        holder.linearLayout.setBackgroundColor(someColorTo);
        holder.detailItem.setTextColor(someColorFrom);
        holder.detailItemPrice.setTextColor(someColorFrom);


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
        private final ItemAdapterPoCCreate mAdapter;
        private final ImageButton imageButtonQuitarItem;
        private final LinearLayout linearLayout;


        @SuppressLint("ResourceAsColor")
        public ItemViewHolder(@NonNull View itemView, ItemAdapterPoCCreate jobAdapter) {
            super(itemView);
            detailItem = itemView.findViewById(R.id.cardViewItemTextDetail);
            detailItemPrice = itemView.findViewById(R.id.cardViewItemTextPrice);
            imageButtonQuitarItem = itemView.findViewById(R.id.imgButtonQuitarItem);
            linearLayout = itemView.findViewById(R.id.linLytBack);

            this.mAdapter = jobAdapter;

//            itemView.setOnClickListener(this);
            imageButtonQuitarItem.setOnClickListener(this);
            detailItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
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

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            detailItemPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String elNuevoTexto = s.toString();
                    Log.e(TAG, "onTextChanged" + elNuevoTexto);
                    int mPosition = getLayoutPosition();
                    Item element = itemArrayList.get(mPosition);

                    if (TextUtils.isEmpty(elNuevoTexto)) {
                        // No word was entered, set the result accordingly.
                        element.setPrice(0.0);
                        itemArrayList.set(mPosition, element);
                    } else {
                        try {

                            NumberFormat format = NumberFormat.getInstance(Locale.US);

                            Number number = format.parse(elNuevoTexto);
                            element.setPrice(number.doubleValue());
                            //Double d = Double.parseDouble(elNuevoTexto.replaceAll(",", ""));
                            element.setPriceFormat(String.valueOf(number.doubleValue()));
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());

                        }
                        itemArrayList.set(mPosition, element);
                        Log.d(TAG, element.toString());
                        //snackBar(itemView);

                    }
                    NuevaCitaTrabajoActivity citaActivity = null;

                    try {
                        citaActivity = NuevaCitaTrabajoActivity.citaActivity;
                        Double precio = 0.0;
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
                        Double precio = 0.0;
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
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged" + s.toString());

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
                    NuevaCitaTrabajoActivity citaActivity = NuevaCitaTrabajoActivity.citaActivity;
                    citaActivity.setPrecio(0.0);
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
                NuevaCitaTrabajoActivity citaActivity = null;

                try {
                    citaActivity = NuevaCitaTrabajoActivity.citaActivity;
                    Double precio = 0.0;
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
                    Double precio = 0.0;
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
        if (itemArrayList == null) {
            itemArrayList = new ArrayList<>();
        }
        itemArrayList.add(item);
        notifyItemInserted(getItemCount() - 1);
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

    public String formatNumber(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(number));
    }


}
