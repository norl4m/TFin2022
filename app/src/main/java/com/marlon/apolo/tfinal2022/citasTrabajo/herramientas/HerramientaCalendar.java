package com.marlon.apolo.tfinal2022.citasTrabajo.herramientas;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HerramientaCalendar extends Calendar {
    private String TAG = HerramientaCalendar.class.getSimpleName();

    @Override
    protected void computeTime() {

    }

    @Override
    protected void computeFields() {

    }

    @Override
    public void add(int field, int amount) {

    }

    @Override
    public void roll(int field, boolean up) {

    }

    @Override
    public int getMinimum(int field) {
        return 0;
    }

    @Override
    public int getMaximum(int field) {
        return 0;
    }

    @Override
    public int getGreatestMinimum(int field) {
        return 0;
    }

    @Override
    public int getLeastMaximum(int field) {
        return 0;
    }

    public HerramientaCalendar() {

    }

    public String[] separarFechaYHora(String horaYFecha, String patronHoraYFecha, Locale locale,String patronFecha, String patronHora) {
        String[] result = new String[2];

        try {
//            SimpleDateFormat formatFecha = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
            SimpleDateFormat formatFecha = new SimpleDateFormat(patronHoraYFecha,locale);
            Date date = formatFecha.parse(horaYFecha);
            Log.d(TAG, "INPUT: " + horaYFecha);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            //Log.d(TAG, cal.toString());
            //Log.d(TAG, cal.getTime().toString());

//            DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
            DateFormat formatFec = new SimpleDateFormat(patronFecha, locale);

//            SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm a", new Locale("es", "ES"));
            SimpleDateFormat formatHora = new SimpleDateFormat(patronHora, locale);

            Log.d(TAG, formatFec.format(cal.getTime()));
            Log.d(TAG, formatHora.format(cal.getTime()));
            result[0] = formatFec.format(cal.getTime());
            result[1] = formatHora.format(cal.getTime());
            Log.d(TAG, "OUTPUT FECHA: " + result[0]);
            Log.d(TAG, "OUTPUT HORA: " + result[0]);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return result;
    }
}
