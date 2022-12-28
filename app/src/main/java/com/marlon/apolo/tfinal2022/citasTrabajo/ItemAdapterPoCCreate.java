package com.marlon.apolo.tfinal2022.citasTrabajo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.InputFilter;
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
import com.marlon.apolo.tfinal2022.model.Item;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
        /*holder.detailItem.setEnabled(false);
        holder.detailItemPrice.setEnabled(false);
        holder.detailItemPriceCents1.setEnabled(false);
        holder.imageButtonQuitarItem.setEnabled(false);*/

        int someColorFrom = 0;
        int someColorTo = 0;
//        if (mode) {
//////            holder.textViewContenido.settint.setColorFilter(activity.getResources().getColor(R.color.teal_700));
////
////            DrawableCompat.setTint(holder.textViewContenido.getBackground(), activity.getResources().getColor(R.color.black_minus));
////            holder.textViewContenido.setBackground(holder.textViewContenido.getBackground());
//            someColor = ContextCompat.getColor(activity, R.color.black_minus);
//
//        } else {
//            someColor = ContextCompat.getColor(activity, R.color.happy_color);
//
//        }

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

    /**
     * Get decimal formated string to include comma seperator to decimal number
     *
     * @param value
     * @return
     */
    public static String getDecimalFormattedString(String value) {
        if (value != null && !value.equalsIgnoreCase("")) {
            StringTokenizer lst = new StringTokenizer(value, ".");
            String str1 = value;
            String str2 = "";
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken();
                str2 = lst.nextToken();
            }
            String str3 = "";
            int i = 0;
            int j = -1 + str1.length();
            if (str1.charAt(-1 + str1.length()) == '.') {
                j--;
                str3 = ".";
            }
            for (int k = j; ; k--) {
                if (k < 0) {
                    if (str2.length() > 0)
                        str3 = str3 + "." + str2;
                    return str3;
                }
                if (i == 3) {
                    str3 = "," + str3;
                    i = 0;
                }
                str3 = str1.charAt(k) + str3;
                i++;
            }
        }
        return "";
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
//
//
//                    int cursorPosition = detailItemPrice.getSelectionEnd();
//                    String originalStr = detailItemPrice.getText().toString();
//
//                    //To restrict only two digits after decimal place
//                    detailItemPrice.setFilters(new InputFilter[]{new MoneyValueFilter(Integer.parseInt("2"))});
//
//                    try {
//                        detailItemPrice.removeTextChangedListener(this);
//                        String value = detailItemPrice.getText().toString();
//
//                        if (value != null && !value.equals("")) {
//                            if (value.startsWith(".")) {
//                                detailItemPrice.setText("0.");
//                            }
//                            if (value.startsWith("0") && !value.startsWith("0.")) {
//                                detailItemPrice.setText("");
//                            }
//                            String str = detailItemPrice.getText().toString().replaceAll(",", "");
//                            if (!value.equals(""))
//                                detailItemPrice.setText(getDecimalFormattedString(str));
//
//                            int diff = detailItemPrice.getText().toString().length() - originalStr.length();
//                            detailItemPrice.setSelection(cursorPosition + diff);
//                            Log.d(TAG, detailItemPrice.getText().toString());
//
//                            String str1 = detailItemPrice.getText().toString() + "d";
//                            try {
//                                double l = DecimalFormat.getNumberInstance().parse(str1).doubleValue();
//                                Log.w(TAG, String.valueOf(l));
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, e.toString());
//
//                            }
//
//                        }
//                        detailItemPrice.addTextChangedListener(this);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        detailItemPrice.addTextChangedListener(this);
//                    }
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

    /**
     * Restrict digits after decimal point value as per currency
     */
    class MoneyValueFilter extends DigitsKeyListener {
        private int digits;

        public MoneyValueFilter(int i) {
            super(false, true);
            digits = i;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            // if changed, replace the source
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            int len = end - start;

            // if deleting, source is empty
            // and deleting can't break anything
            if (len == 0) {
                return source;
            }

            int dlen = dest.length();

            // Find the position of the decimal .
            for (int i = 0; i < dstart; i++) {
                if (dest.charAt(i) == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString((dlen - (i + 1) + len > digits) ? "" : String.valueOf(new SpannableStringBuilder(source, start, end)));
                }
            }

            for (int i = start; i < end; ++i) {
                if (source.charAt(i) == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    if ((dlen - dend) + (end - (i + 1)) > digits)
                        return "";
                    else
                        break; // return new SpannableStringBuilder(source,
                    // start, end);
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(String.valueOf(new SpannableStringBuilder(source, start, end)));
        }
    }
}
