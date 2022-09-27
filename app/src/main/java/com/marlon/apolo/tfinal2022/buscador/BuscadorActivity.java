package com.marlon.apolo.tfinal2022.buscador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoTrabajadorListAdapter;
import com.marlon.apolo.tfinal2022.ui.bienvenido.BienvenidoViewModel;
import com.marlon.apolo.tfinal2022.ui.chats.ChatViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class BuscadorActivity extends AppCompatActivity {

    private static final String TAG = BuscadorActivity.class.getSimpleName();
    private int offset;
    private BienvenidoViewModel bienvenidoViewModel;
    private ArrayList<Oficio> oficiosArrayList;
    private ArrayList<Trabajador> trabajadorsArrayList;
    //    private BienvenidoTrabajadorListAdapter bienvenidoTrabajadorListAdapter;
    private RecyclerView recyclerViewResultados;
    private RecyclerView recyclerViewBuscador;
    private TextView textViewResults;
    private TextView textViewNoResults;
    private TrabajadorListAdapterBuscador trabajadorListAdapterBuscador;
    private TrabajadorListAdapterResultados trabajadorListAdapterResultados;
    private int searchMode;
    private Usuario usuarioLocal;

    private void loadLocalUser() {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                usuarioLocal = administrador;
                                trabajadorListAdapterBuscador.setUsuarioFrom(usuarioLocal);
                                trabajadorListAdapterResultados.setUsuarioFrom(usuarioLocal);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                                trabajadorListAdapterBuscador.setUsuarioFrom(usuarioLocal);
                                trabajadorListAdapterResultados.setUsuarioFrom(usuarioLocal);


                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                                trabajadorListAdapterBuscador.setUsuarioFrom(usuarioLocal);
                                trabajadorListAdapterResultados.setUsuarioFrom(usuarioLocal);

                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchMode = 0;

        bienvenidoViewModel = new ViewModelProvider(this).get(BienvenidoViewModel.class);

//        bienvenidoTrabajadorListAdapter = new BienvenidoTrabajadorListAdapter(this);


        trabajadorListAdapterBuscador = new TrabajadorListAdapterBuscador(this);
        trabajadorListAdapterResultados = new TrabajadorListAdapterResultados(this);

//        try {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadLocalUser();
        }
//        } catch (Exception e) {

//        }


        ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.getAllChats().observe(this, chats -> {
            trabajadorListAdapterBuscador.setChatList(chats);
            trabajadorListAdapterResultados.setChatList(chats);
        });

        bienvenidoViewModel.getAllOficios().observe(this, oficios -> {
            if (oficios != null) {
                trabajadorListAdapterBuscador.setOficioList(oficios);
                trabajadorListAdapterResultados.setOficioList(oficios);
            }
        });


//        trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
//        trabajadorViewModel.getAllTrabajadores().observe(getViewLifecycleOwner(), trabajadors -> {
//            if (trabajadors != null) {
//                trabajadorListAdapter.setTrabajadores(trabajadors);
//                progressBar1.setVisibility(View.GONE);
//            }
//        });


        textViewResults = findViewById(R.id.textViewResultados);
        textViewNoResults = findViewById(R.id.textViewNoResultados);

        recyclerViewResultados = findViewById(R.id.recyclerViewTrabajdoresResultados);
        recyclerViewBuscador = findViewById(R.id.recyclerViewTrabajdoresBuscador);

        textViewNoResults.setVisibility(View.GONE);
        textViewResults.setVisibility(View.GONE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        offset = getIntent().getIntExtra("offset", 0);
        handleIntent(getIntent());

        recyclerViewBuscador.setAdapter(trabajadorListAdapterBuscador);
        recyclerViewBuscador.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewResultados.setAdapter(trabajadorListAdapterResultados);
        recyclerViewResultados.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewBuscador.setVisibility(View.GONE);
        recyclerViewResultados.setVisibility(View.GONE);


        loadTrabajadores();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.mnu_nav_buscador).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.onActionViewExpanded();
        MenuItem menuItem = menu.findItem(R.id.mnu_nav_buscador);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textViewResults.setText("");
                textViewNoResults.setText("");
                textViewResults.setVisibility(View.GONE);
                textViewNoResults.setVisibility(View.GONE);
                try {
                    //textViewResults.setText(String.format(Locale.US, "%d resultados", trabajadorListAdapter.getItemCount()));
                    //
                    // textViewResults.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }
                recyclerViewBuscador.setVisibility(View.VISIBLE);
                recyclerViewResultados.setVisibility(View.GONE);
                switch (searchMode) {
                    case 0:

                        trabajadorListAdapterBuscador.filtradoByOficio(newText);
                        break;
                    case 1:
                        trabajadorListAdapterBuscador.filtrado(newText);
                        break;
                }
                return false;
            }
        });

        if (offset == 1) {
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    finish();
                    return false;
                }
            });
        } else {
            textViewNoResults.setText("");
            textViewNoResults.setVisibility(View.GONE);
            textViewResults.setText("");
            textViewResults.setVisibility(View.GONE);
            menuItem.expandActionView();
            searchView.setQuery("", true);
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    //Toast.makeText(getApplicationContext(), "plinplin", Toast.LENGTH_SHORT).show();
                    /*Para que se expanda sin necesidad de escribir una previa consulta*//*idea similar al buscador de youtube*/
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
//                Toast.makeText(getApplicationContext(), "GGWP", Toast.LENGTH_SHORT).show();
                    /*Para que se cierre la ventana actual sin necesidad de presionar doble atras <--*//*idea similar al buscador de youtube*/
                    /*onBackPressed se comporta de manera diferente*/
                    finish();
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_by_name:
                //Toast.makeText(getApplicationContext(), "by name", Toast.LENGTH_LONG).show();
                searchMode = 1;
                textViewResults.setText("");
                textViewNoResults.setText("");
                textViewResults.setVisibility(View.GONE);
                textViewNoResults.setVisibility(View.GONE);
                //loadTrabajadores();

                break;
            case R.id.search_by_oficio:
                //Toast.makeText(getApplicationContext(), "by oficio", Toast.LENGTH_LONG).show();
                searchMode = 0;
                textViewResults.setText("");
                textViewNoResults.setText("");
                textViewResults.setVisibility(View.GONE);
                textViewNoResults.setVisibility(View.GONE);
                //loadTrabajadores();
//                recyclerView1.setAdapter(trabajadorListAdapter);
//                recyclerView1.setLayoutManager(new LinearLayoutManager(this));
//                trabajadorListAdapter.setTrabajadores(new ArrayList<>());
//                trabajadorListAdapter.setTrabajadors(new ArrayList<>());
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void loadTrabajadores() {

        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Trabajador trabajador = data.getValue(Trabajador.class);
                            trabajadorArrayList.add(trabajador);
                        }
                        Collections.sort(trabajadorArrayList, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

                        trabajadorListAdapterBuscador.setTrabajadores(trabajadorArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
//        progressDialog.show();
        boolean oficioFound = false;
        switch (searchMode) {
            case 0:/* default por oficios*/
                searchByOficio(query);
                break;
            case 1:/* por nombres*/
                searchByName(query);
                break;
        }


//        oficioViewModel.getGetAllOficios().observe(this, oficios -> {
//            if (oficios != null) {
//                oficiosDB = oficios;
//
//                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
//                //Toast.makeText(getApplicationContext(), String.valueOf(oficiosDB.size()), Toast.LENGTH_SHORT).show();
//                //Toast.makeText(getApplicationContext(), String.valueOf(trabajadorsDB.size()), Toast.LENGTH_SHORT).show();
//
//                if (query.length() > 0) {
//
////        Toast.makeText(this, query, Toast.LENGTH_LONG).show();
//                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
//                            MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
//                    suggestions.saveRecentQuery(query, null);
//                    boolean flag = false;
//                    if (oficiosDB != null) {
//                        for (Oficio of : oficiosDB) {
//                            if (of.getNombre().toUpperCase().equals(query.toUpperCase())) {
//                                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
//                                flag = true;
//                                filterTrabjadores(of, true);
//                                break;
//                            }
//                        }
//                        if (!flag) {
//                            try {
//                                progressDialog.dismiss();
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
//                } else {
//                    try {
//                        progressDialog.dismiss();
//                    } catch (Exception e) {
//                    }
//                }
//
//            }
//        });

    }

    private void searchByName(String query) {
        if (!(trabajadorListAdapterBuscador.getTrabajadors().size() > 0)) {
            Toast.makeText(getApplicationContext(), "Lo sentimos, no encontramos registrado a ningún trabajador con el nombre buscado.", Toast.LENGTH_LONG).show();
//            bienvenidoTrabajadorListAdapter.setTrabajadores(new ArrayList<>());
            textViewNoResults.setText("No se encontraron trabajadores");
            textViewNoResults.setVisibility(View.VISIBLE);
            textViewResults.setVisibility(View.GONE);
            textViewResults.setText("");
        }

        textViewNoResults.setText("");
        textViewNoResults.setVisibility(View.GONE);
        textViewResults.setText(String.format(Locale.US, "%d resultados", trabajadorListAdapterBuscador.getTrabajadors().size()));
        textViewResults.setVisibility(View.VISIBLE);

        recyclerViewResultados.setAdapter(trabajadorListAdapterResultados);
        recyclerViewResultados.setLayoutManager(new LinearLayoutManager(this));
//        trabajadorListAdapter.setTrabajadores(trabajadorArrayList);
        Collections.sort(trabajadorListAdapterBuscador.getTrabajadors(), (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

        trabajadorListAdapterResultados.setTrabajadores(trabajadorListAdapterBuscador.getTrabajadors());

        recyclerViewBuscador.setVisibility(View.GONE);
        recyclerViewResultados.setVisibility(View.VISIBLE);
//        FirebaseDatabase.getInstance().getReference()
//                .child("trabajadores")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            Trabajador trabajador = data.getValue(Trabajador.class);
//                            trabajadorArrayList.add(trabajador);
//                        }
//                        trabajadorListAdapter.setTrabajadores(trabajadorArrayList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }

    private void searchByOficio(String query) {

        FirebaseDatabase.getInstance().getReference().child("oficios")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        oficiosArrayList = new ArrayList<>();
                        boolean flagFoundOficio = false;
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Oficio oficio = data.getValue(Oficio.class);
                            Log.d(TAG, oficio.toString());
                            if (oficio.getNombre().toUpperCase().equals(query.toUpperCase())) {
//                                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                                flagFoundOficio = true;
//                                ArrayList<Oficio> oficioArrayListAux = new ArrayList<>();
//                                oficioArrayListAux.add(oficio);
//                                trabajadorListAdapter.setOficioList(oficioArrayListAux);
//                                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(BuscadorActivity.this,
//                                        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
//
//                                suggestions.saveRecentQuery(query, null);
                                searchTrabajadores(oficio.getIdOficio());
                                break;
                            }
                        }
                        if (!flagFoundOficio) {
                            Toast.makeText(getApplicationContext(), "Lo sentimos el oficio no se encuentra registrado.", Toast.LENGTH_LONG).show();
//                            bienvenidoTrabajadorListAdapter.setTrabajadores(new ArrayList<>());
                            textViewNoResults.setText("No se encontraron trabajadores");
                            textViewNoResults.setVisibility(View.VISIBLE);
                            textViewResults.setVisibility(View.GONE);
                            textViewResults.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void searchTrabajadores(String idOficio) {
        FirebaseDatabase.getInstance().getReference().child("trabajadores")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Trabajador trabajador = data.getValue(Trabajador.class);
                            for (String idOf : trabajador.getIdOficios()) {
                                if (idOf.equals(idOficio)) {
                                    trabajadorArrayList.add(trabajador);
                                    break;
                                }
                            }
                        }
                        showTrabajadores(trabajadorArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showTrabajadores(ArrayList<Trabajador> trabajadorArrayList) {

        textViewNoResults.setText("");
        textViewNoResults.setVisibility(View.GONE);
        textViewResults.setText(String.format(Locale.US, "%d resultados", trabajadorArrayList.size()));
        textViewResults.setVisibility(View.VISIBLE);
//        ProgressBar progressBar1 = root.findViewById(R.id.fragHomeProgressBar1);
//        recyclerView1.setAdapter(bienvenidoTrabajadorListAdapter);
//        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
//        TrabajadorViewModel trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
//
//
//
//        bienvenidoTrabajadorListAdapter.setTrabajadores(trabajadorArrayList);


        recyclerViewResultados.setAdapter(trabajadorListAdapterResultados);
        recyclerViewResultados.setLayoutManager(new LinearLayoutManager(this));
//        trabajadorListAdapter.setTrabajadores(trabajadorArrayList);
        Collections.sort(trabajadorArrayList, (o1, o2) -> Double.compare(o2.getCalificacion(), o1.getCalificacion()));

        trabajadorListAdapterResultados.setTrabajadores(trabajadorArrayList);

        recyclerViewBuscador.setVisibility(View.GONE);
        recyclerViewResultados.setVisibility(View.VISIBLE);


//        trabajadorViewModel.getAllTrabajadores().observe(this, trabajadors -> {
//            if (trabajadors != null) {
//                trabajadorListAdapter.setTrabajadores(trabajadors);
////                progressBar1.setVisibility(View.GONE);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();

    }
}