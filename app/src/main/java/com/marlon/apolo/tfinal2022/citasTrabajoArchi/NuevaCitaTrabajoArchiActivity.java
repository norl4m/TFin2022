package com.marlon.apolo.tfinal2022.citasTrabajoArchi;

import static com.marlon.apolo.tfinal2022.citasTrabajoArchi.CitaTrabajoArchiActivity.EXTRA_DATA_ID;
import static com.marlon.apolo.tfinal2022.citasTrabajoArchi.CitaTrabajoArchiActivity.EXTRA_DATA_UPDATE_WORD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.util.Log;
import com.marlon.apolo.tfinal2022.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class NuevaCitaTrabajoArchiActivity extends AppCompatActivity {


    public static final String EXTRA_REPLY = "com.example.android.roomwordssample.REPLY";
    public static final String EXTRA_REPLY_ID = "com.android.example.roomwordssample.REPLY_ID";

    private EditText mEditWordView;
    private boolean changeMenuEdit;
    private boolean changeMenuUpdate;
    private boolean changeMenuDelete;
    private Intent replyIntent;
    private Bundle extras;

    private Button buttonDate;
    private Button buttonHour;

    private TextView textViewFecha;
    private TextView textViewHora;
    private SimpleDateFormat formatterDate;
    private SimpleDateFormat formatterHour;
    private String strDate;
    private String strHour;
    private int yearLocal;
    private int monthLocal;
    private int dayLocal;
    private int hourLocal;
    private int minLocal;
    private Calendar calendarDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_cita_trabajo_archi);

        changeMenuEdit = true;
        changeMenuUpdate = true;
        changeMenuDelete = true;

//        Date currentTime = Calendar.getInstance().getTime();
        Date date = Calendar.getInstance().getTime();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        strDate = "";
        strHour = "";

        yearLocal = date.getYear();
        monthLocal = date.getMonth();
        dayLocal = date.getDay();
        hourLocal = date.getHours();
        minLocal = date.getMinutes();


        buttonDate = findViewById(R.id.buttonDate);
        buttonHour = findViewById(R.id.buttonHour);

        textViewFecha = findViewById(R.id.textViewFecha);
        textViewFecha.setText(date.toString());
        textViewHora = findViewById(R.id.textViewHora);
//        textViewFecha.setText(strDate);


//        formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//        strDate = formatter.format(date);
        System.out.println("Date Format with dd-M-yyyy hh:mm:ss : " + strDate);
//        textViewFecha.setText(strDate);

        formatterDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        strDate = formatterDate.format(date);
        System.out.println("Date Format with dd MMMM yyyy : " + strDate);
        textViewFecha.setText(String.format("Fecha: %s", strDate));

//        formatter = new SimpleDateFormat("dd MMMM yyyy zzzz");
//        strDate = formatter.format(date);
        System.out.println("Date Format with dd MMMM yyyy zzzz : " + strDate);


        formatterHour = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault());
        strHour = formatterHour.format(date);
//        System.out.println("Date Format HH:mm:ss z : " + strDate);
        textViewHora.setText(String.format("Hora: %s", strHour));

//        DateFormat dateFormat = new DateFormat( "yyyy-mm-dd hh:mm:ss" );
//        String strDate = dateFormat.format(currentTime);


        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "adasda", Toast.LENGTH_SHORT).show();
                DialogFragment newFragment = new SelectorDateFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        buttonHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectorTimeFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(getSupportFragmentManager(),"datePicker");
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditWordView = findViewById(R.id.edit_word);
        String id = "-1";

        extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            String word = extras.getString(EXTRA_DATA_UPDATE_WORD, "");
            if (!word.isEmpty()) {
                mEditWordView.setText(word);
                mEditWordView.setSelection(word.length());
                mEditWordView.requestFocus();
            }
        } else {
            changeMenuEdit = false;
            changeMenuUpdate = false;
            changeMenuDelete = false;
            invalidateOptionsMenu();
        } // Otherwise, start with empty fields.


        final Button button = findViewById(R.id.button_save);
        replyIntent = new Intent();

        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Create a new Intent for the reply.
//                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWordView.getText())) {
                    // No word was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // Get the new word that the user entered.
                    String word = mEditWordView.getText().toString();
                    CitaTrabajoArchi citaTrabajoArchi = new CitaTrabajoArchi();
                    citaTrabajoArchi.setObservaciones(word);
                    citaTrabajoArchi.setFechaCita(calendarDate.getTime());
                    // Put the new word in the extras for the reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY, citaTrabajoArchi);
                    if (extras != null && extras.containsKey(EXTRA_DATA_ID)) {
                        String id = extras.getString(EXTRA_DATA_ID);
                        if (!id.equals("-1")) {
                            replyIntent.putExtra(EXTRA_REPLY_ID, id);
                        }
                    }
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cita_edit_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnu_edit_cita:
                Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
                changeMenuEdit = false;
                invalidateOptionsMenu();

                return true;
            case R.id.mnu_update_cita:
                Toast.makeText(getApplicationContext(), "Update", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.mnu_delete_cita:
                Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT).show();
                // Get the new word that the user entered.
                String word = mEditWordView.getText().toString();
                // Put the new word in the extras for the reply Intent.
                replyIntent.putExtra(EXTRA_REPLY, word);
                if (extras != null && extras.containsKey(EXTRA_DATA_ID)) {
                    String id = extras.getString(EXTRA_DATA_ID);
                    if (!id.equals("-1")) {
                        replyIntent.putExtra(EXTRA_REPLY_ID, id);
                    }
                }
                // Set the result status to indicate success.
                setResult(RESULT_OK, replyIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItemO = menu.findItem(R.id.mnu_edit_cita);
        MenuItem menuItem1 = menu.findItem(R.id.mnu_update_cita);
        MenuItem menuItem2 = menu.findItem(R.id.mnu_delete_cita);


        menuItemO.setVisible(changeMenuEdit);
        menuItem1.setVisible(changeMenuUpdate);
        menuItem2.setVisible(changeMenuDelete);


        return super.onPrepareOptionsMenu(menu);
    }

    public void processDatePickerResult(int year, int month, int day) {
        yearLocal = year;
        monthLocal = month;
        dayLocal = day;

        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (month_string +
                "/" + day_string + "/" + year_string);

        Toast.makeText(this, "Date: " + dateMessage,
                Toast.LENGTH_SHORT).show();

        try {

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
            calendarDate = new GregorianCalendar(year, month, day, hourLocal, minLocal);
//            System.out.println(sdf.format(calendar.getTime()));
            strDate = formatterDate.format(calendarDate.getTime());
//            Toast.makeText(this, "Fecha: " + formatterDate.format(calendar.getTime()), Toast.LENGTH_SHORT).show();

            textViewFecha.setText(String.format("Fecha: %s", strDate));

//
//            //        System.out.println(year);
//            SimpleDateFormat formatterDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
////
//            Calendar c = Calendar.getInstance();
//            c.set(year, month, day, 0, 0);
////
////        System.out.println(formatterDate.format(c));
//            Toast.makeText(this, "Fecha: " + formatterDate.format(c),
//                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }


    }

    public void processTimePickerResult(int hour, int min) {
        hourLocal = hour;
        minLocal = min;
        String hour_string = Integer.toString(hour);
        String min_string = Integer.toString(min);
        String dateMessage = (hour_string +
                ":" + min_string);

        Toast.makeText(this, "Time: " + dateMessage,
                Toast.LENGTH_SHORT).show();

        try {

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
            calendarDate = new GregorianCalendar(yearLocal, monthLocal, dayLocal, hour, min);
//            System.out.println(sdf.format(calendar.getTime()));
            strHour = formatterHour.format(calendarDate.getTime());
//            Toast.makeText(this, "Fecha: " + formatterDate.format(calendar.getTime()), Toast.LENGTH_SHORT).show();

            textViewHora.setText(String.format("Hora: %s", strHour));

//
//            //        System.out.println(year);
//            SimpleDateFormat formatterDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
////
//            Calendar c = Calendar.getInstance();
//            c.set(year, month, day, 0, 0);
////
////        System.out.println(formatterDate.format(c));
//            Toast.makeText(this, "Fecha: " + formatterDate.format(c),
//                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }

    }
}