package com.marlon.apolo.tfinal2022.citasTrabajo;

import static android.app.PendingIntent.FLAG_MUTABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Item;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class CitaTrabajoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CitaTrabajoActivity.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1700;
    private Usuario usuarioFrom;
    private Usuario usuarioTo;
    private TextView textViewEmpleador;
    private TextView textViewTrabajador;
    private int localHourDay;
    private int localMinute;
    private int localYear;
    private int localMonth;
    private int localDay;
    private TextView textViewHoraCita;
    private String dateSelected;
    private TextView textViewFechaCita;
    private TextView textViewTotal;
    private ItemAdapter itemAdapter;
    public static CitaTrabajoActivity citaActivity;
    private float precio;
    private RecyclerView recyclerView;
    private int usuario;
    private Calendar objCalendarPoCLoco;

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public TextView getTextViewTotal() {
        return textViewTotal;
    }

    public void setTextViewTotal(TextView textViewTotal) {
        this.textViewTotal = textViewTotal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita_trabajo);
        objCalendarPoCLoco = Calendar.getInstance();

        citaActivity = this;
        localYear = 0;
        localDay = 0;
        localMonth = 0;
        localMinute = 0;
        localHourDay = 0;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewEmpleador = findViewById(R.id.textViewEmpleador);
        textViewTrabajador = findViewById(R.id.textViewTrabajador);
        textViewHoraCita = findViewById(R.id.textViewHoraIni);
        textViewFechaCita = findViewById(R.id.textViewFechaIni);
        textViewTotal = findViewById(R.id.textViewTotal);

        findViewById(R.id.buttonCrearCita).setOnClickListener(this);
        findViewById(R.id.buttonHoraCita).setOnClickListener(this);
        findViewById(R.id.buttonFechaCita).setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerViewItems);

        usuarioFrom = (Usuario) getIntent().getSerializableExtra("usuarioFrom");
        usuarioTo = (Usuario) getIntent().getSerializableExtra("usuarioTo");
        Log.d(TAG, "usuarioFrom.toString()");
        Log.d(TAG, usuarioFrom.toString());
        Log.d(TAG, usuarioTo.toString());
        Log.d(TAG, "usuarioTo.toString()");
        if (usuarioFrom != null) {
            textViewTrabajador.setText(String.format("Trabajador: %s %s", usuarioFrom.getNombre(), usuarioFrom.getApellido()));
        }
        if (usuarioTo != null) {
            textViewEmpleador.setText(String.format("Empleador: %s %s", usuarioTo.getNombre(), usuarioTo.getApellido()));
        }

        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        usuario = prefs.getInt("usuario", -1);
        itemAdapter = new ItemAdapter(this, usuario);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Item> itemArrayList = new ArrayList<>();
        // Item item = new Item();
//        item.setDetail("Pastel familiar");
        // item.setDetail("");
//        item.setPrice(56.0f);
        //item.setPrice(0.0f);
        //itemArrayList.add(item);

        itemAdapter.setItems(itemArrayList);

        findViewById(R.id.fabNuevoItem)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item item = new Item();
//        item.setDetail("Pastel familiar");
                        item.setDetail("");
//        item.setPrice(56.0f);
                        item.setPrice(0.0f);
                        itemAdapter.addItem(item);
                    }
                });

        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        textViewFechaCita.setText(format.format(objCalendarPoCLoco.getTime()));

        SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm aa", new Locale("es", "ES"));
        textViewHoraCita.setText(formatHora.format(objCalendarPoCLoco.getTime()));
    }


    public Dialog crearCita(Cita cita) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.card_view_service_detail, null);
        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);
        final TextView textViewEmpleador = promptsView.findViewById(R.id.textViewEmpleadorDialog);
        final TextView textViewTrabajador = promptsView.findViewById(R.id.textViewTrabajadorDialog);
        final TextView textViewFechaCita = promptsView.findViewById(R.id.textViewFechaCitadialog);
        final TextView textViewTotal = promptsView.findViewById(R.id.textViewCostoTotalDialog);

        textViewEmpleador.setText(String.format("Empleador: %s", cita.getNombreEmpleador()));
        textViewTrabajador.setText(String.format("Trabajador: %s", cita.getNombreTrabajador()));
        textViewFechaCita.setText(String.format("Fecha: %s", cita.getFechaCita()));
//        textViewTotal.setText(cita.getTotal());
        textViewTotal.setText(String.format("Total: $ %.2f", cita.getTotal()));

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String fec = textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString();
                Log.d(TAG, cita.getFechaCita());
//                cita.setFechaCita("");
//                cita.setFechaCita(fec);

                if (cita.validarCita(CitaTrabajoActivity.this, 0)) {
                    Trabajador trabajador = (Trabajador) usuarioFrom;
                    trabajador.enviarCita(cita, CitaTrabajoActivity.this);
                    Log.d(TAG, cita.toString());
                }

//                if (validarCita(cita)) {
//                    trabajador.enviarCita(cita, CitaActivity.this);
//                    Log.d(TAG, cita.toString());
//                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    public Dialog erroresCita(String errores) {

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(CitaTrabajoActivity.this);
        builder.setTitle("Error")
                .setMessage(errores)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                })
        ;
        // Create the AlertDialog object and return it
        return builder.create();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cita, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItemO = menu.findItem(R.id.mnu_crear_cita);
        MenuItem menuItem1 = menu.findItem(R.id.mnu_editar_cita);
        MenuItem menuItem2 = menu.findItem(R.id.mnu_cancelar_cita);
        MenuItem menuItem3 = menu.findItem(R.id.mnu_eliminar_cita);
        MenuItem menuItem4 = menu.findItem(R.id.mnu_finalizar_trabajo);
        MenuItem menuItem5 = menu.findItem(R.id.mnu_calif);

        menuItem1.setVisible(false);
        menuItem2.setVisible(false);
        menuItem3.setVisible(false);
        menuItem4.setVisible(false);
        menuItem5.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }


//    @Override
//    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        MenuItem item = menu.findItem(R.id.mnu_edit_oficio);
//        item.setVisible(false);
//        if (user == 2) {
//            item.setVisible(true);
//        }
//
//    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_crear_cita:
//                Toast.makeText(getApplicationContext(), "Creando cita   ", Toast.LENGTH_SHORT).show();

                Cita cita = new Cita();

                cita.setNombreEmpleador(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
                cita.setNombreTrabajador(usuarioFrom.getNombre() + " " + usuarioFrom.getApellido());
                cita.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
                cita.setItems(itemAdapter.getItemArrayList());
                ArrayList<String> participants = new ArrayList<>();
//                participants.add(idTrabajador);
//                participants.add(idEmpleador);
                cita.setTo(usuarioTo.getIdUsuario());
                cita.setFrom(usuarioFrom.getIdUsuario());
//                Toast.makeText(getApplicationContext(), idTrabajador + "\n" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\n" + idEmpleador, Toast.LENGTH_SHORT).show();
//                cita.setParticipants(participants);
//                cita.setFrom(idTrabajador);
//                cita.setTo(idEmpleador);
                float precioTotal = 0;
                for (Item itemAux : itemAdapter.getItemArrayList()) {
                    precioTotal = precioTotal + itemAux.getPrice();
                }
                cita.setTotal(precioTotal);
                //trabajador.enviarCita(cita, chatID, CitaActivity.this);
                Log.d(TAG, cita.toString());
                crearCita(cita).show();


                break;
            case R.id.mnu_editar_cita:
                Toast.makeText(getApplicationContext(), "Editar cita   ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnu_cancelar_cita:
                Toast.makeText(getApplicationContext(), "Cancelar cita   ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnu_eliminar_cita:
                Toast.makeText(getApplicationContext(), "Eliminar cita   ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnu_finalizar_trabajo:
                Toast.makeText(getApplicationContext(), "Finalizar trabajo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnu_calif:
                Toast.makeText(getApplicationContext(), "Calificando", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCrearCita:
//                Toast.makeText(getApplicationContext(), "Creando cita   ", Toast.LENGTH_SHORT).show();
                Cita cita = new Cita();

                cita.setNombreEmpleador(usuarioTo.getNombre() + " " + usuarioTo.getApellido());
                cita.setNombreTrabajador(usuarioFrom.getNombre() + " " + usuarioFrom.getApellido());
                cita.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
                cita.setItems(itemAdapter.getItemArrayList());
                ArrayList<String> participants = new ArrayList<>();
//                participants.add(idTrabajador);
//                participants.add(idEmpleador);
                cita.setTo(usuarioTo.getIdUsuario());
                cita.setFrom(usuarioFrom.getIdUsuario());
//                Toast.makeText(getApplicationContext(), idTrabajador + "\n" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\n" + idEmpleador, Toast.LENGTH_SHORT).show();
//                cita.setParticipants(participants);
//                cita.setFrom(idTrabajador);
//                cita.setTo(idEmpleador);
                float precioTotal = 0;
                for (Item itemAux : itemAdapter.getItemArrayList()) {
                    precioTotal = precioTotal + itemAux.getPrice();
                }
                cita.setTotal(precioTotal);
                //trabajador.enviarCita(cita, chatID, CitaActivity.this);
                Log.d(TAG, cita.toString());
                crearCita(cita).show();

                break;
            case R.id.buttonFechaCita:
//                Toast.makeText(getApplicationContext(), "Fecha cita   ", Toast.LENGTH_SHORT).show();
                showDatePicker(v);
                break;
            case R.id.buttonHoraCita:
//                Toast.makeText(getApplicationContext(), "Hora cita   ", Toast.LENGTH_SHORT).show();
                showTimePicker(v);
                break;

        }
    }

    /**
     * Handles the button click to create a new time picker fragment and
     * show it.
     *
     * @param view View that was clicked
     */
    public void showTimePicker(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(),
                "Timepicker");
    }

    /**
     * Process the time picker result into strings that can be displayed in
     * a Toast.
     *
     * @param hourOfDay Chosen hour
     * @param minute    Chosen minute
     */
    public void processTimePickerResult(int hourOfDay, int minute) {
        // Convert time elements into strings.
        localHourDay = hourOfDay;
        localMinute = minute;

        String hour_string = Integer.toString(hourOfDay);
        String minute_string = Integer.toString(minute);
        // Assign the concatenated strings to timeMessage.
        String timeMessage = (hour_string + ":" + minute_string);

        // Toast.makeText(this, "Time" + timeMessage, Toast.LENGTH_SHORT).show();

        textViewHoraCita.setText(timeMessage);
        SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm aa", new Locale("es", "ES"));


        //DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(localYear, localMonth, localDay, hourOfDay, minute, 0);
        Log.d(TAG, formatHora.format(calendar.getTime()));
        textViewHoraCita.setText(formatHora.format(calendar.getTime()));


//        objCalendarPoCLoco.set(Calendar.YEAR, 2022);
        //objCalendarPoC.set(Calendar.YEAR, objCalendarPoC.get(Calendar.YEAR));
//        objCalendarPoCLoco.set(Calendar.MONTH, 7);/*0 - 11*/
//        objCalendarPoCLoco.set(Calendar.DAY_OF_MONTH, 15);
        objCalendarPoCLoco.set(Calendar.HOUR_OF_DAY, hourOfDay);
        objCalendarPoCLoco.set(Calendar.MINUTE, minute);
        objCalendarPoCLoco.set(Calendar.SECOND, 0);
        objCalendarPoCLoco.set(Calendar.MILLISECOND, 0);
//        objCalendarPoC.set(Calendar.AM_PM, Calendar.PM);

    }

    /**
     * Handles the button click to create a new date picker fragment and
     * show it.
     *
     * @param view View that was clicked
     */
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),
                "getString(R.string.datepicker)");
    }


    /**
     * Process the date picker result into strings that can be displayed in
     * a Toast.
     *
     * @param year  Chosen year
     * @param month Chosen month
     * @param day   Chosen day
     */
    public void processDatePickerResult(int year, int month, int day) {

        localYear = year;
        localMonth = month;
        localDay = day;
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        dateSelected = day_string + "/" + month_string + "/" + year_string;

        //   Toast.makeText(this, "Fecha seleccionada: " + dateSelected, Toast.LENGTH_LONG).show();


        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Log.d(TAG, format.format(calendar.getTime()));
        textViewFechaCita.setText(format.format(calendar.getTime()));


        objCalendarPoCLoco.set(Calendar.YEAR, year);
        objCalendarPoCLoco.set(Calendar.MONTH, month);/*0 - 11*/
        objCalendarPoCLoco.set(Calendar.DAY_OF_MONTH, day);
//        objCalendarPoCLoco.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        objCalendarPoCLoco.set(Calendar.MINUTE, minute);
//        objCalendarPoCLoco.set(Calendar.SECOND, 0);
//        objCalendarPoCLoco.set(Calendar.MILLISECOND, 0);
//        objCalendarPoC.set(Calendar.AM_PM, Calendar.PM);

    }

    public void programarAlarmaLocal(Cita cita) {

//        Toast.makeText(getApplicationContext(), "Programando alarma local", Toast.LENGTH_LONG).show();
        final int min = 7000;
        final int max = 7999;
        int random = new Random().nextInt((max - min) + 1) + min;


//        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent1 = new Intent(this, AlarmReceiver.class);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "CONFIGURANDO ALARMA LOCAL");
        Log.d(TAG, cita.toString());
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        alarmIntent1.putExtra("idCita", cita.getIdCita());
        //alarmIntent1.putExtra("cita", cita);
        alarmIntent1.putExtra("nT", cita.getNombreTrabajador());
        alarmIntent1.putExtra("nE", cita.getNombreEmpleador());
        alarmIntent1.putExtra("idFrom", cita.getFrom());
        alarmIntent1.putExtra("idTo", cita.getTo());
        alarmIntent1.putExtra("fec", cita.getFechaCita());

//        Cita cita1 = new Cita();
//        cita1.setIdCita(cita.getIdCita());
//        alarmIntent.putExtra("cita",cita1);

//        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent,
//                PendingIntent.FLAG_NO_CREATE) != null);
//        Log.d(TAG, String.format("Estado de alarma: %s", alarmUp));
//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (getApplicationContext(), NOTIFICATION_ID, alarmIntent1,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent notifyPendingIntent = null;
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, alarmIntent1,
                            FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, alarmIntent1,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
        }


//        Calendar objCalendar = Calendar.getInstance();
//
//        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
//        Calendar calendar = Calendar.getInstance();
//
//        try {
//            objCalendar.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if (alarmManager != null) {
//            Log.d(TAG, "Configurando alarma");
//            alarmManager.set(AlarmManager.RTC,
//                    objCalendar.getTimeInMillis(),
//                    notifyPendingIntent);
//
//            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
//            Locale locale = new Locale("es", "ES");
//            DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm aa", new Locale("es", "ES"));
//
//
//            try {
//                SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
//                Date date = formatFecha.parse(cita.getFechaCita());
//                //Log.d(TAG, "INPUT: " + horaYFecha);
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//
////            Date date1 = format.parse(format.format(calendar.getTime()));
//                Date date1 = format.parse(format.format(cal.getTime()));
//                Date date2 = new Date();
//                Log.d(TAG, "Date 1 selected(alarm date): " + format.format(date1));
//                Log.d(TAG, "Date 2 compare(now): " + format.format(date2));
//                if (date1.compareTo(date2) > 0) {
//                    //Log.d(TAG, "La fecha seleccionada es correcta");
//
//                } else if (date1.compareTo(date2) < 0) {
//                    Log.d(TAG, "La alarma ha expirado!");
//                    alarmManager.cancel(notifyPendingIntent);
//                    Log.d(TAG, "La alarma ha sido descativada!");
//
////                                            arrayListErrores.add("La fecha seleccionada es incorrecta");
////
////                                            validacion = false;
//                } else if (date1.compareTo(date2) == 0) {
//
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//
////            objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), notifyPendingIntent);
//
//        }

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        //boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (this, NOTIFICATION_ID, notifyIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService
                (ALARM_SERVICE);


//        long repeatInterval = 800000L;
//                            long repeatInterval = 0;

//        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        // If the Toggle is turned on, set the repeating alarm with
        // a 15 minute interval.
//                            Calendar objCalendar = Calendar.getInstance();
        // Set the toast message for the "on" case.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


        Date d = null;
        try {
            d = sdf.parse("2022-08-06 13:45:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
        cal.setTime(new Date());
        if (alarmManager != null) {
//                                objCalendar = Calendar.getInstance();
//                                alarmManager.setInexactRepeating
//                                        (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                                                triggerTime, repeatInterval,
//                                                notifyPendingIntent);
//            alarmManager.set(AlarmManager.RTC,
//                    cal.getTimeInMillis(),
//                    notifyPendingIntent);
        }

        String dateStr1 = sdf.format(cal.getTime());

//                            toastMessage = getString(R.string.alarm_on_toast) + objCalendar.getTime().toLocaleString();
        String toastMessage = getString(R.string.alarm_on_toast) + dateStr1;
        Log.d(TAG, toastMessage);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, 0);


//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 14);
        // Set the alarm to start at 8:30 a.m.

        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendarX = Calendar.getInstance();
        try {
            calendarX.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
        } catch (Exception e) {
            Log.d(TAG, e.toString());

        }
        Log.d(TAG, "calendarX:" + String.valueOf(formatFec.format(calendarX.getTime())) + ":");
        Log.d(TAG, "FecÑ:" + cita.getFechaCita() + ":");

//        Calendar calendarm = Calendar.getInstance();
//        calendarm.setTimeInMillis(SystemClock.elapsedRealtime());
//        calendarm.set(Calendar.HOUR_OF_DAY, 2);
//        calendarm.set(Calendar.HOUR_OF_DAY, calendarX.getTime().getHours());
//        calendarm.set(Calendar.MINUTE, 39);
//        calendarm.setTimeInMillis(System.currentTimeMillis());
//        calendarm.set(Calendar.HOUR_OF_DAY, 2);
        calendarX.set(Calendar.SECOND, 0);
        calendarX.set(Calendar.MILLISECOND, 0);
//        calendarm.set(Calendar.MONTH, calendarX.getTime().getMonth() + 1);
//        calendarm.set(Calendar.DAY_OF_MONTH, calendarX.getTime().getDay());
//        calendarm.set(Calendar.HOUR_OF_DAY, calendarX.getTime().getHours());
//        calendarm.set(Calendar.MINUTE, 51);
//        calendarm.set(Calendar.MINUTE, calendarX.getTime().getMinutes());
//        calendarm.set(Calendar.MINUTE, calendarX.getTime().getMinutes());

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 60 * 1000,
//                alarmIntent);

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, notifyPendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarm.getTimeInMillis(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarm.getTimeInMillis(), notifyPendingIntent);

        Date dateConfig = calendarX.getTime();

//        scheduleNotification(cita, dateConfig.getTime());

//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, dateConfig.getTime(), notifyPendingIntent);

//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarX.getTimeInMillis(), notifyPendingIntent);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, dateConfig.getTime(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, dateConfig.getTime(), notifyPendingIntent);


//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                calendarm.getTimeInMillis(),
//                alarmIntent);


//        Log.d(TAG, String.valueOf(SystemClock.elapsedRealtime() + 60 * 1000));
//
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.US);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(SystemClock.elapsedRealtime() + (60 * 1000));
//
//        Log.d(TAG, String.valueOf(calendar.getTime()));
//        Log.d(TAG, String.valueOf(formatter.format(calendar.getTime())));


//        alarmMgr.set(AlarmManager.RTC, calendarX.getTimeInMillis(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarX.getTimeInMillis(), alarmIntent);

        Log.d(TAG, "###################################");
//        Log.d(TAG, String.valueOf(calendarX.getTime()));
//        Log.d(TAG, String.valueOf(formatFec.format(calendar.getTime())));
        Log.d(TAG, String.valueOf(formatFec.format(calendarX.getTime())));
//        Log.d(TAG, String.valueOf(formatFec.format(calendarX.getTime())));
//        Log.d(TAG, String.valueOf(formatFec.format(cal.getTime())));
    }

    public void programarAlarmaLocalCustomLoco(Cita cita) {

        final int min = 7000;
        final int max = 7999;
        int random = new Random().nextInt((max - min) + 1) + min;
//        int random = 7000;


        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(CitaTrabajoActivity.this, AlarmReceiver.class);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "CONFIGURANDO ALARMA LOCAL LOCOx");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        alarmIntent.putExtra("idCita", cita.getIdCita());
        alarmIntent.putExtra("nT", cita.getNombreTrabajador());
        alarmIntent.putExtra("nE", cita.getNombreEmpleador());
        alarmIntent.putExtra("idFrom", cita.getFrom());
        alarmIntent.putExtra("idTo", cita.getTo());
        alarmIntent.putExtra("fec", cita.getFechaCita());
        cita.setItems(null);
//        alarmIntent.putExtra("cita", cita);
        alarmIntent.putExtra("random", random);

//        Cita cita1 = new Cita();
//        cita1.setIdCita(cita.getIdCita());
//        alarmIntent.putExtra("cita",cita1);

//        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

//        Log.d(TAG, String.format("Estado de alarma: %s", alarmUp));
        PendingIntent notifyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, alarmIntent,
                            FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, alarmIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            /*Bandera de mrda: PendingIntent.FLAG_IMMUTABLE*/
//
        }


        /*
         *  La fecha y hora que he mencionado aquí es: 2012- 28 de junio, 11:20:00 AM. Y lo más importante es que el mes se especifica de 0 a 11 solamente. Medios de junio debe ser especificado por 5.
         * */

        AlarmManager objAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar objCalendarPoC = Calendar.getInstance();
        objCalendarPoC.set(Calendar.YEAR, 2022);
        //objCalendarPoC.set(Calendar.YEAR, objCalendarPoC.get(Calendar.YEAR));
        objCalendarPoC.set(Calendar.MONTH, 7);/*0 - 11*/
        objCalendarPoC.set(Calendar.DAY_OF_MONTH, 15);
        objCalendarPoC.set(Calendar.HOUR_OF_DAY, 13);
        objCalendarPoC.set(Calendar.MINUTE, 20);
        objCalendarPoC.set(Calendar.SECOND, 0);
        objCalendarPoC.set(Calendar.MILLISECOND, 0);
        objCalendarPoC.set(Calendar.AM_PM, Calendar.PM);
        //Intent alamShowIntent = new Intent(this, AlarmReceiver.class);
        //PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0, alamShowIntent, 0);
        //objAlarmManager.set(AlarmManager.RTC_WAKEUP, objCalendarPoC.getTimeInMillis(), alarmPendingIntent);
        DateFormat formatFecPoc = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
//        try {
////            objCalendarPoC.setTime(Objects.requireNonNull(formatFecPoc.parse(cita.getFechaCita())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Log.d(TAG, "FECHA NUEVA SUPER EXACTA: " + formatFecPoc.format(objCalendarPoCLoco.getTime()));


        Calendar objCalendar = Calendar.getInstance();

        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();

        try {
            objCalendar.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (alarmManager != null) {
//            Log.d(TAG, "Configurando alarma RTC");
            Log.d(TAG, "Configurando alarma RTC-WAKE-UP  objCalendarPoCLoco "); /* es el mas exacto*/
            /*Las alarmas y notificaciones son un infierno en android*/
//            Log.d(TAG, "Configurando alarma ELAPSED_REALTIME_WAKsEUP  objCalendarPoCLoco ");
//            alarmManager.set(AlarmManager.RTC,
            alarmManager.set(AlarmManager.RTC_WAKEUP,
//                    objCalendar.getTimeInMillis(),
//                    objCalendarPoC.getTimeInMillis(),
                    objCalendarPoCLoco.getTimeInMillis(),
                    notifyPendingIntent);
//            boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

//            boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
//            if (alarmUp) {
//                Log.d("myTag", "Alarm is already active");
//            }
//
//
//            Log.d(TAG, String.format("EstadO ALARMA LOCO: %s", alarmUp));
//            Log.d(TAG, String.format("EstadO ALARMA LOCO: %s", cita.toString()));
//            Log.d(TAG, String.format("EstadO ALARMA LOCO: %s", String.valueOf(random)));


            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
            Locale locale = new Locale("es", "ES");
            DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm aa", new Locale("es", "ES"));


            try {
                SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
                Date date = formatFecha.parse(cita.getFechaCita());
                //Log.d(TAG, "INPUT: " + horaYFecha);

                Calendar cal = Calendar.getInstance();
                if (date != null) {
                    cal.setTime(date);
                }

//            Date date1 = format.parse(format.format(calendar.getTime()));
//                Date date1 = format.parse(format.format(cal.getTime()));
                Date date1 = format.parse(format.format(objCalendarPoCLoco.getTime()));
                Date date2 = new Date();
                Log.d(TAG, "Date 1 selected(alarm date) objCalendarPoCLoco: " + format.format(date1));
                Log.d(TAG, "Date 2 compare(now): " + format.format(date2));
                if (date1.compareTo(date2) > 0) {
                    Log.d(TAG, "alarma configurada");

                } else if (date1.compareTo(date2) < 0) {
                    Log.d(TAG, "La alarma ha expirado!");
                    alarmManager.cancel(notifyPendingIntent);
                    Log.d(TAG, "La alarma ha sido descativada!");

//                                            arrayListErrores.add("La fecha seleccionada es incorrecta");
//
//                                            validacion = false;
                } else if (date1.compareTo(date2) == 0) {

                }
//                if (alarmUp) {
//                    Log.d(TAG, "Alarm is active");
//                    Log.d(TAG, cita.toString());
//                    Log.d(TAG, String.valueOf(random));
//                } else {
//                    Log.d(TAG, "Alarm is no active");
//                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


//            objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), notifyPendingIntent);

        }
    }


    private void scheduleNotification(Cita cita, long delay) {
        Log.d(TAG, "###############################");
        Log.d(TAG, "Schedule notification");
        Log.d(TAG, "###############################");
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra("idCita", cita.getIdCita());
        notificationIntent.putExtra("nT", cita.getNombreTrabajador());
        notificationIntent.putExtra("nE", cita.getNombreEmpleador());
        notificationIntent.putExtra("idFrom", cita.getFrom());
        notificationIntent.putExtra("idTo", cita.getTo());
        notificationIntent.putExtra("fec", cita.getFechaCita());
//        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
//        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, NOTIFICATION_ID, notificationIntent, FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, NOTIFICATION_ID, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, pendingIntent);
    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_X");
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
//        builder.setChannelId("NOTIFICATION_CHANNEL_X");
        return builder.build();
    }

//    private void updateLabel() {
//        String myFormat = "dd/MM/yy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
//        Calendar myCalendar = Calendar.getInstance();
//        Date date = myCalendar.getTime();
//        scheduleNotification(getNotification(sdf.format(date)), date.getTime());
//    }
}