package com.marlon.apolo.tfinal2022.citasTrabajo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Item;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class DetalleServicioActivity extends AppCompatActivity {

    private String TAG = DetalleServicioActivity.class.getSimpleName();
    private Cita citaLocal;
    private TextView textViewNombreEmpleador;
    private TextView textViewNombreTrabajador;
    private TextView textViewFechaCita;
    private TextView textViewHoraCita;
    private float precioTotal;
    private AlertDialog alertD;

    public TextView getTextViewTotal() {
        return textViewTotal;
    }

    public void setTextViewTotal(TextView textViewTotal) {
        this.textViewTotal = textViewTotal;
    }

    private TextView textViewTotal;
    private ImageButton imageButtonAddDetail;
    private Button buttonFechaCita;
    private Button buttonHoraCita;
    private Button buttonGuardarCambios;
    private int opcion = 0;
    private String chatID;
    private CitaViewModel citaViewModel;
    private RecyclerView recyclerView;
    private int localHourDay;
    private int localMinute;
    private int localYear;
    private int localMonth;
    private int localDay;
    private String dateSelected;
    ItemAdapter itemAdapter;
    public static DetalleServicioActivity detalleServicioActivity;
    private Trabajador trabajadorLocal;
    private Empleador empleadorLocal;
    private Administrador administradorLocal;
    private int usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_servicio);
        citaViewModel = new ViewModelProvider(this).get(CitaViewModel.class);
        recyclerView = findViewById(R.id.recyclerViewItems);
        detalleServicioActivity = this;

        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        usuario = prefs.getInt("usuario", -1);
        Log.i(TAG, String.format("USUARIO: %d", usuario));
        //Toast.makeText(getApplicationContext(), String.valueOf(usuario), Toast.LENGTH_SHORT).show();

        switch (usuario) {
            case 0:
                administradorLocal = new Administrador();
                administradorLocal.setIdUsuario(FirebaseAuth.getInstance().getCurrentUser().getUid());
                break;
            case 1:
                empleadorLocal = new Empleador();
                empleadorLocal.setIdUsuario(FirebaseAuth.getInstance().getCurrentUser().getUid());

                break;
            case 2:
                trabajadorLocal = new Trabajador();
                trabajadorLocal.setIdUsuario(FirebaseAuth.getInstance().getCurrentUser().getUid());
                break;
        }
//        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
//
//        trabajadorViewModel
//                .getTrabajador(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .observe(this, trabajador -> {
//                    trabajadorLocal = trabajador;
//                });
        citaLocal = new Cita();
        textViewNombreEmpleador = findViewById(R.id.textViewEmpleador);
        textViewNombreTrabajador = findViewById(R.id.textViewTrabajador);
        textViewFechaCita = findViewById(R.id.textViewFechaIni);
        textViewHoraCita = findViewById(R.id.textViewHoraIni);
        textViewTotal = findViewById(R.id.textViewTotal);
        imageButtonAddDetail = findViewById(R.id.imageButtonAddDetail);
        imageButtonAddDetail.setVisibility(View.GONE);
        buttonFechaCita = findViewById(R.id.buttonFechaCita);
        buttonHoraCita = findViewById(R.id.buttonHoraCita);
        buttonGuardarCambios = findViewById(R.id.buttonGuardar);
        buttonGuardarCambios.setVisibility(View.GONE);
        buttonFechaCita.setEnabled(false);
        buttonHoraCita.setEnabled(false);

        try {
            Cita cita = (Cita) getIntent().getSerializableExtra("cita");
            citaLocal = cita;
            chatID = cita.getChatID();
//             Toast.makeText(this, cita.toString() + chatID, Toast.LENGTH_LONG).show();
            textViewFechaCita.setText(String.format("%s", cita.getFechaCita()));
            textViewHoraCita.setText(String.format("%s", cita.getFechaCita()));
            textViewTotal.setText(String.format("Total: $ %.2f", cita.getTotal()));

            textViewNombreEmpleador.setText(String.format("Empleador: %s", cita.getNombreEmpleador()));
            textViewNombreTrabajador.setText(String.format("Trabajador: %s", cita.getNombreTrabajador()));


            //cita.setIdCita(getIntent().getStringExtra("idCita"));
            //cita.setNombreTrabajador(getIntent().getStringExtra("nombreEmpleador"));
            // cita.setNombreEmpleador(getIntent().getStringExtra("nombreTrabajador"));
            // cita.setFechaCita(getIntent().getStringExtra("fecha"));
            // cita.setTotal(getIntent().getFloatExtra("total", 0));
            // Toast.makeText(this, cita.toString(), Toast.LENGTH_LONG).show();
            Log.e(TAG, cita.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar
        );

        try {
            //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatFecha = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
            //SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm a", new Locale("es", "ES"));
            Date date = formatFecha.parse(citaLocal.getFechaCita());
            if (citaLocal.getIdCita().contains("a. m.")) {
                Log.e(TAG, "AM");
            } else {
                Log.e(TAG, "PM");
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Log.d(TAG, cal.toString());
            Log.d(TAG, cal.getTime().toString());

            DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));

            SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm a", new Locale("es", "ES"));
            //DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
            //Calendar calendar = Calendar.getInstance();
            //cal.setTime(date);
            //calendar.set(year, month, day);
            Log.d(TAG, formatFec.format(cal.getTime()));
            /**********************************************/
//            String fechaYHora = textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString();
            String fechaYHora = citaLocal.getFechaCita();
            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
            Locale locale = new Locale("es", "ES");
            String patronFecha = "dd MMMM yyyy";
            String patronHora = "HH:mm aa";


            HerramientaCalendar herramientaCalendar = new HerramientaCalendar();
            String[] result = herramientaCalendar.separarFechaYHora(fechaYHora, patronFechaYHora, locale, patronFecha, patronHora);
            textViewFechaCita.setText(result[0]);
            textViewHoraCita.setText(result[1]);

            /**********************************************/

            //textViewFechaCita.setText(formatFec.format(cal.getTime()));
            //textViewHoraCita.setText(formatHora.format(cal.getTime()));
            /********************************************/

            //cal.setTime(date);
            //Log.d(TAG, formatHora.format(cal.getTime()));
            //Log.d(TAG, String.valueOf(cal.getTime().getHours()));
            //Log.d(TAG, String.valueOf(cal.getTime().getMinutes()));
            //Log.d(TAG, String.valueOf(cal.get));

//            Toast.makeText(this, "Date" +
//                    cal.toString() + chatID, Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Date" + cal.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemAdapter = new ItemAdapter(this, 0);

        citaViewModel.getItems(citaLocal.getIdCita()).observe(this, items -> {

//            itemAdapter = new ItemAdapter(this, usuario);
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            for (Item item : items) {
//                Log.e(TAG, item.toString());
//            }
            // create an arraylist from the array
            // using asList() method of the Arrays class
            ArrayList<Item> numbers = items;
            // System.out.println("ArrayList with duplicate elements: " + numbers);

            // convert the arraylist into a set
            Set<Item> set = new LinkedHashSet<>();
            set.addAll(numbers);

            // delete al elements of arraylist
            numbers.clear();

            // add element from set to arraylist
            numbers.addAll(set);
            //System.out.println("ArrayList without duplicate elements: " + numbers);
            for (Item item : numbers) {
                Log.e(TAG, item.toString());
            }

//            itemAdapter.setItems(items);
            itemAdapter.setItems(numbers);
            citaLocal.setItems(numbers);
//                            trabajadorListAdapter = new TrabajadorListAdapter(requireActivity());
//                            recyclerViewEmployees.setAdapter(trabajadorListAdapter);
//                            recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(requireActivity()));

//            if (itemAdapter.getItemArrayList() == null) {
//                itemAdapter.setItems(items);
//            }
            //else {
            //     itemAdapter.setItemArrayList(itemAdapter.getItemArrayList());
            // }
        });

        buttonHoraCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(v);
            }
        });

        buttonFechaCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });
        buttonGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Actualizando cita de trabajo");
                //citaLocal.finalizarTrabajo(chatID);
                citaLocal.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
                citaLocal.setItems(itemAdapter.getItemArrayList());
                precioTotal = 0;
                for (Item i : itemAdapter.getItemArrayList()) {
                    precioTotal = precioTotal + i.getPrice();
                }
                citaLocal.setTotal(precioTotal);
                Log.d(TAG, citaLocal.toString());
                //citaLocal.actualizarCita();
//                if (citaLocal.validarCita(DetalleServicioActivity.this, 0)) {
                if (citaLocal.validarCita(DetalleServicioActivity.this, 1)) {
                    trabajadorLocal.actualizarCita(citaLocal, DetalleServicioActivity.this);
                }


                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(DetalleServicioActivity.this));

                itemAdapter.setItems(citaLocal.getItems());
            }
        });

        findViewById(R.id.imageButtonAddDetail)
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
        SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm a", new Locale("es", "ES"));


        //DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(localYear, localMonth, localDay, hourOfDay, minute, 0);
        Log.d(TAG, formatHora.format(calendar.getTime()));
        textViewHoraCita.setText(formatHora.format(calendar.getTime()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_detail, menu);
        return true;
    }

    public android.app.AlertDialog alertDialogConfirmar() {

        return new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Confirmación:")
                .setMessage("¿Está seguro que desea eliminar esta cita de trabajo?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.e(TAG, "Eliminando cita");
                        citaLocal.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
                        citaLocal.setItems(itemAdapter.getItemArrayList());
                        precioTotal = 0;
                        for (Item i : itemAdapter.getItemArrayList()) {
                            precioTotal = precioTotal + i.getPrice();
                        }
                        citaLocal.setTotal(precioTotal);
                        Log.d(TAG, citaLocal.toString());
                        trabajadorLocal.eliminarCita(citaLocal, DetalleServicioActivity.this);
                        finish();
                    }

                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_finalizar_trabajo:
                Log.e(TAG, "Finalizar trabajo");
                //citaLocal.finalizarTrabajo(chatID);
                citaLocal.setState(true);
                citaLocal.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
                citaLocal.setItems(itemAdapter.getItemArrayList());
                precioTotal = 0;
                for (Item i : itemAdapter.getItemArrayList()) {
                    precioTotal = precioTotal + i.getPrice();
                }
                citaLocal.setTotal(precioTotal);
                Log.d(TAG, citaLocal.toString());
                //citaLocal.actualizarCita();
                if (citaLocal.validarCita(DetalleServicioActivity.this, 1)) {
                    trabajadorLocal.actualizarCita(citaLocal, this);
                }
                buttonGuardarCambios.setVisibility(View.GONE);
                return true;
            case R.id.mnu_eliminar_cita:
//                Log.e(TAG, "Eliminando cita");
//                citaLocal.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
//                citaLocal.setItems(itemAdapter.getItemArrayList());
//                precioTotal = 0;
//                for (Item i : itemAdapter.getItemArrayList()) {
//                    precioTotal = precioTotal + i.getPrice();
//                }
//                citaLocal.setTotal(precioTotal);
//                Log.d(TAG, citaLocal.toString());
//                trabajadorLocal.eliminarCita(citaLocal, DetalleServicioActivity.this);
//                finish();
                buttonGuardarCambios.setVisibility(View.GONE);
                alertDialogConfirmar().show();

                return true;
//            case R.id.mnu_guardar:
//                Log.e(TAG, "Actualizando cita de trabajo");
//                //citaLocal.finalizarTrabajo(chatID);
//                citaLocal.setFechaCita(textViewFechaCita.getText().toString() + " " + textViewHoraCita.getText().toString());
//                citaLocal.setItems(itemAdapter.getItemArrayList());
//                precioTotal = 0;
//                for (Item i : itemAdapter.getItemArrayList()) {
//                    precioTotal = precioTotal + i.getPrice();
//                }
//                citaLocal.setTotal(precioTotal);
//                Log.d(TAG, citaLocal.toString());
//                //citaLocal.actualizarCita();
////                if (citaLocal.validarCita(DetalleServicioActivity.this, 0)) {
//                if (citaLocal.validarCita(DetalleServicioActivity.this, 1)) {
//                    trabajadorLocal.actualizarCita(citaLocal, this);
//                }
//                return true;
            case R.id.mnu_editar_cita:
                Log.d(TAG, String.format("estado cita: %s", citaLocal.toString()));
                if (!citaLocal.isState()) {
                    Log.e(TAG, "Editar cita de trabajo");
                    //buttonFechaCita.setEnabled(true);
                    //buttonHoraCita.setEnabled(true);
                    imageButtonAddDetail.setVisibility(View.VISIBLE);
                    itemAdapter = new ItemAdapter(this, 2);

//            itemAdapter = new ItemAdapter(this, usuario);
                    recyclerView.setAdapter(itemAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    itemAdapter.setItems(citaLocal.getItems());
                    buttonGuardarCambios.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.mnu_calif_trab:
                Log.e(TAG, "calificando a trabajador");

                if (citaLocal.isState()) {
                    alertD = califTrabajador();
                    alertD.show();
                }
                return true;
            default:
                // Do nothing
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.mnu_detail, menu);
//        return true;
//    }


    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem editCita = menu.findItem(R.id.mnu_editar_cita);
        MenuItem mnuEliminarCita = menu.findItem(R.id.mnu_eliminar_cita);
        MenuItem mnuFin = menu.findItem(R.id.mnu_finalizar_trabajo);
//        MenuItem mnuGuardarCita = menu.findItem(R.id.mnu_guardar);
        MenuItem mnuCalifTrab = menu.findItem(R.id.mnu_calif_trab);


        switch (usuario) {
            case 0:
                Log.d(TAG, "Administrador");

                mnuFin.setVisible(false);
//                mnuEliminarCita.setVisible(false);
//                mnuGuardarCita.setVisible(false);
                editCita.setVisible(false);
                mnuCalifTrab.setVisible(true);
                break;
            case 1:
                Log.d(TAG, "Empleador");

                mnuFin.setVisible(false);
//                mnuEliminarCita.setVisible(false);
//                mnuGuardarCita.setVisible(false);
                editCita.setVisible(false);
                Log.d(TAG, String.valueOf(citaLocal.isState()));
                mnuCalifTrab.setVisible(citaLocal.isState());
                break;
            case 2:
                Log.d(TAG, "Trabajador");

                mnuFin.setVisible(!citaLocal.isState());
                mnuEliminarCita.setVisible(true);
                mnuCalifTrab.setVisible(false);
                editCita.setVisible(!citaLocal.isState());
//                mnuGuardarCita.setVisible(!citaLocal.isState());
                break;
        }


        return true;
    }


    public AlertDialog califTrabajador() {


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();


        View promptsView = inflater.inflate(R.layout.card_view_enviar_calificacion, null);


        // set prompts.xml to alertdialog builder
        alert.setView(promptsView);

        final TextView textViewTrabajador = promptsView.findViewById(R.id.textViewEmployee);
        final ImageView imageView = promptsView.findViewById(R.id.imageViewEmployee);
        final RatingBar ratingBar = promptsView.findViewById(R.id.ratingBar);

        textViewTrabajador.setText(citaLocal.getNombreTrabajador());
//        ratingBar.setRating();


        final ImageButton calif1 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton1);
        final ImageButton calif2 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton2);
//        final ImageButton imageButtonAudio = promptsView
//                .findViewById(R.id.imageButtonAudio);
        final ImageButton calif3 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton3);
        final ImageButton calif4 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton4);
        final ImageButton calif5 = promptsView
                .findViewById(R.id.cardViewSendQualificationImageButton5);

        calif1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, citaLocal.toString());
//                citaLocal.actualizarCita();
                float calif = 1;
                calificarTrabajador(calif);
                alertD.dismiss();
            }
        });
        calif2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 2;
                calificarTrabajador(calif);
                alertD.dismiss();
            }
        });
        calif3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 3;
                calificarTrabajador(calif);
                alertD.dismiss();
            }
        });
        calif4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 4;
                calificarTrabajador(calif);
                alertD.dismiss();
            }
        });
        calif5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float calif = 5;
                calificarTrabajador(calif);
                alertD.dismiss();
            }
        });
//        imageButtonAudio.setOnClickListener(clickListenerDialogCustom);

        return alert.create();
    }

    private void calificarTrabajador(Float calif) {
        if (empleadorLocal != null) {
            citaLocal.setCalificacion(calif);
            empleadorLocal.calificarTrabajador(citaLocal, this);
        }
        if (administradorLocal != null) {
            citaLocal.setCalificacion(calif);
            administradorLocal.calificarTrabajador(citaLocal, this);
        }
        Toast.makeText(getApplicationContext(), "Gracias por utilizar nuestros servicios.", Toast.LENGTH_SHORT).show();

    }


}