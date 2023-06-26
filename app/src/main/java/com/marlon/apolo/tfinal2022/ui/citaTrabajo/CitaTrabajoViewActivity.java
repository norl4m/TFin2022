package com.marlon.apolo.tfinal2022.ui.citaTrabajo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.adapters.CitaListAdapter;
import com.marlon.apolo.tfinal2022.citasTrabajo.viewModel.CitaViewModel;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.SeleccionarEmpleadorActivity;
import com.marlon.apolo.tfinal2022.model.Cita;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class CitaTrabajoViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CitaViewModel mViewModel;
    private ArrayList<Cita> citasDB;
    private ArrayList<Cita> citas;
    private CitaListAdapter citaListAdapter;
    private String TAG = CitaTrabajoViewActivity.class.getSimpleName();
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita_trabajo_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        Toast.makeText(getApplicationContext(), "aaaaaa", Toast.LENGTH_LONG).show();

        mViewModel = new ViewModelProvider(this).get(CitaViewModel.class);
        recyclerView = findViewById(R.id.recyclerViewCitas);

        citasDB = new ArrayList<>();
        citas = new ArrayList<>();

        citaListAdapter = new CitaListAdapter(CitaTrabajoViewActivity.this);
        recyclerView.setAdapter(citaListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CitaTrabajoViewActivity.this));

        mViewModel
                .getCitas()
                .observe(this, new Observer<ArrayList<Cita>>() {
                    @Override
                    public void onChanged(ArrayList<Cita> citas) {
                        if (citas != null) {
                            citasDB.clear();
//                            citasDB = citas;
                            citasDB.addAll(citas);

                            citaListAdapter.setCitas(citas);
                        }
                    }

                });

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

//        startActivity(new Intent(this, PoCActivity.class));
        editorPref = myPreferences.edit();
        int usuario = myPreferences.getInt("usuario", -1);
        if (usuario == 2) {

            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CitaTrabajoViewActivity.this, SeleccionarEmpleadorActivity.class);
                    startActivity(intent);
                }
            });
        }

//        mViewModel = new ViewModelProvider(this).get(CitaTrabajoViewModel.class);
        // TODO: Use the ViewModel
//        requireActivity().startActivity(new Intent(requireActivity(), CitaTrabajoActivity.class));
    }

//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        inflater.inflate(R.menu.menu_citas, menu);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_citas, menu);
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_filter_completados:
//                Toast.makeText(getActivity(), "Completados!", Toast.LENGTH_SHORT).show();
                citas.clear();
                for (Cita cita : citasDB) {
                    Log.e(TAG, cita.toString());
                    if (cita.isState()) {
                        citas.add(cita);
                    }
                }
                citaListAdapter.setCitas(citas);
                return true;
            case R.id.menu_filter_pendientes:
//                Toast.makeText(getActivity(), "Pendientes!", Toast.LENGTH_SHORT).show();
                citas.clear();
                for (Cita cita : citasDB) {
                    Log.e(TAG, cita.toString());
                    if (!cita.isState()) {
                        citas.add(cita);
                    }
                }
                citaListAdapter.setCitas(citas);
                return true;

            case R.id.menu_order_asc:
//                Toast.makeText(getActivity(), "Por fecha!", Toast.LENGTH_SHORT).show();
//                Collections.sort(citasDB, new Comparator<Cita>() {
//                    public int compare(Calendar o1, Calendar o2) {
//                        return o1.getDateTime().compareTo(o2.getDateTime());
//                    }
//                });
                try {
                    Collections.sort(citaListAdapter.getCitaArrayList(), (o1, o2) -> o1.getCalendaHoraCita().compareTo(o2.getCalendaHoraCita()));
//                Collections.sort(citasDB, (o1, o2) -> o1.getCalendaHoraCita().compareTo(o2.getCalendaHoraCita()));
                    Collections.reverse(citaListAdapter.getCitaArrayList());

                    citaListAdapter.setCitas(citaListAdapter.getCitaArrayList());
                } catch (Exception e) {

                }

                return true;
            case R.id.menu_order_desc:
//                Toast.makeText(getActivity(), "Por fecha!", Toast.LENGTH_SHORT).show();
//                Collections.sort(citasDB, new Comparator<Cita>() {
//                    public int compare(Calendar o1, Calendar o2) {
//                        return o1.getDateTime().compareTo(o2.getDateTime());
//                    }
//                });
                try {
                    Collections.sort(citaListAdapter.getCitaArrayList(), (o1, o2) -> o1.getCalendaHoraCita().compareTo(o2.getCalendaHoraCita()));
//                Collections.sort(citasDB, (o1, o2) -> o1.getCalendaHoraCita().compareTo(o2.getCalendaHoraCita()));
                    Collections.reverse(citaListAdapter.getCitaArrayList());
                    Collections.reverse(citaListAdapter.getCitaArrayList());
                    citaListAdapter.setCitas(citaListAdapter.getCitaArrayList());
                } catch (Exception e) {

                }

                return true;


            case R.id.menu_all_citas:
//                Toast.makeText(getActivity(), "Todos!", Toast.LENGTH_SHORT).show();
                citaListAdapter.setCitas(citasDB);
                return true;

            case R.id.menu_filter_by_date:
//                Toast.makeText(getActivity(), "Todos!", Toast.LENGTH_SHORT).show();
                showDatePicker();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void processDatePickerResult(
            int year, int month, int day) {
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (month_string + "/"
                + day_string + "/" + year_string);
        // ... Code to do some action with dateMessage.
        //Toast.makeText(getApplicationContext(), dateMessage, Toast.LENGTH_LONG).show();


        //   Toast.makeText(this, "Fecha seleccionada: " + dateSelected, Toast.LENGTH_LONG).show();


        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Log.d(TAG, format.format(calendar.getTime()));
        //Toast.makeText(getApplicationContext(), format.format(calendar.getTime()), Toast.LENGTH_LONG).show();
        ArrayList<Cita> citaArrayListFilter = new ArrayList<>();
        for (Cita cita : citasDB) {
            if (cita.getFechaCita().contains(format.format(calendar.getTime()))) {
                citaArrayListFilter.add(cita);
            }
        }
        try {
            //Collections.sort(citaArrayListFilter, (o1, o2) -> o1.getCalendaHoraCita().compareTo(o2.getCalendaHoraCita()));
//                Collections.sort(citasDB, (o1, o2) -> o1.getCalendaHoraCita().compareTo(o2.getCalendaHoraCita()));
            //Collections.reverse(citaArrayListFilter);

            citaListAdapter.setCitas(citaArrayListFilter);
        } catch (Exception e) {

        }
//        citaListAdapter.setCitas(citaArrayListFilter);
        if (citaArrayListFilter.size() == 0) {
            Toast.makeText(getApplicationContext(), "No existen resultados para la fecha seleccionada", Toast.LENGTH_LONG)
                    .show();
        }

    }
}