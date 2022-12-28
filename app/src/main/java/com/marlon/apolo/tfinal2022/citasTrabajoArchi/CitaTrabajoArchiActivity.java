package com.marlon.apolo.tfinal2022.citasTrabajoArchi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.CitaViewModel;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observer;

public class CitaTrabajoArchiActivity extends AppCompatActivity {

    private static final String TAG = CitaTrabajoArchiActivity.class.getSimpleName();
    private CitaTrabajoArchiViewModel mWordViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_WORD_ACTIVITY_REQUEST_CODE = 2;

    public static final String EXTRA_DATA_UPDATE_WORD = "extra_word_to_be_updated";
    public static final String EXTRA_DATA_ID = "extra_data_id";
    private ChildEventListener childEventListenerCitas;
    private CircularProgressIndicator circularProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita_trabajo_archi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up the RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerviewCitas);
        circularProgressIndicator = findViewById(R.id.circularProgressbar);
        final CitaTrabajoArchiListAdapter adapter = new CitaTrabajoArchiListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the WordViewModel.
        mWordViewModel = new ViewModelProvider(this).get(CitaTrabajoArchiViewModel.class);
        // Get all the words from the database
        // and associate them to the adapter.
        mWordViewModel.getAllWords().observe(this, citaTrabajoArchis -> {
//            adapter.setWords(citaTrabajoArchis);

        });

        loadCitas(adapter);


        // Floating action button setup
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CitaTrabajoArchiActivity.this, NuevaCitaTrabajoArchiActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        // Add the functionality to swipe items in the
        // RecyclerView to delete the swiped item.
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    // We are not implementing onMove() in this app.
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    // When the use swipes a word,
                    // delete that word from the database.
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        CitaTrabajoArchi myWord = adapter.getWordAtPosition(position);
                        Toast.makeText(CitaTrabajoArchiActivity.this, "R.string.delete_word_preamble" + " " +
                                myWord.getObservaciones(), Toast.LENGTH_LONG).show();
                        // Delete the word.
//                        mWordViewModel.deleteWord(myWord);
                        adapter.removeItemFromRecyclerViewWithSwiped(position);


                    }
                });
        // Attach the item touch helper to the recycler view.
        // helper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new CitaTrabajoArchiListAdapter.ClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                CitaTrabajoArchi word = adapter.getWordAtPosition(position);
                launchUpdateWordActivity(word);
            }
        });
        adapter.setOnItemClickListenerDelete(new CitaTrabajoArchiListAdapter.ClickListenerDelete() {
            @Override
            public void onItemClick(View v, int position) {
                CitaTrabajoArchi word = adapter.getWordAtPosition(position);
                launchDeleteCita(word);
            }
        });
    }

    private void launchDeleteCita(CitaTrabajoArchi word) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                //.setTitle("Confirmar")
                .setMessage("Está seguro que desea eliminar?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
                        mWordViewModel.deleteWord(word);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void loadCitas(CitaTrabajoArchiListAdapter adapter) {
//        ArrayList<CitaTrabajoArchi> citaTrabajoArchis = new ArrayList<>();
        childEventListenerCitas = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "---------------" + "onChildAdded" + "---------------");
                CitaTrabajoArchi citaTrabajoArchi = snapshot.getValue(CitaTrabajoArchi.class);
                Log.d(TAG, citaTrabajoArchi.toString());
//                citaTrabajoArchis.add(citaTrabajoArchi);
//                adapter.setWords(citaTrabajoArchis);
                adapter.setWordsByWord(citaTrabajoArchi);
                circularProgressIndicator.setVisibility(View.GONE);
//                linearProgressIndicator.setVisibility(View.GONE);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "---------------" + "onChildChanged" + "---------------");
                CitaTrabajoArchi citaTrabajoArchiChanged = snapshot.getValue(CitaTrabajoArchi.class);
                Log.d(TAG, citaTrabajoArchiChanged.toString());
                int index = 0;
                for (CitaTrabajoArchi ct : adapter.getmWords()) {
                    if (ct.getId().equals(citaTrabajoArchiChanged.getId())) {
                        ct.setObservaciones(citaTrabajoArchiChanged.getObservaciones());
                        break;
                    }
                    index++;
                }
                adapter.setUpdateWord(index, adapter.getmWords().get(index));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "---------------" + "onChildRemoved" + "---------------");
                CitaTrabajoArchi citaTrabajoArchiRemoved = snapshot.getValue(CitaTrabajoArchi.class);
                Log.d(TAG, citaTrabajoArchiRemoved.toString());
                int index = 0;
                for (CitaTrabajoArchi ct : adapter.getmWords()) {
                    if (ct.getId().equals(citaTrabajoArchiRemoved.getId())) {
//                        citaTrabajoArchis.remove(index);
//                        adapter.getmWords().remove(index);
                        break;
                    }
                    index++;
                }
                adapter.setRemoveWord(index);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("citas")
                .addChildEventListener(childEventListenerCitas);

    }

    public void launchUpdateWordActivity(CitaTrabajoArchi word) {
        Intent intent = new Intent(this, NuevaCitaTrabajoArchiActivity.class);
        intent.putExtra(EXTRA_DATA_UPDATE_WORD, word.getObservaciones());
        intent.putExtra(EXTRA_DATA_ID, word.getId());
        startActivityForResult(intent, UPDATE_WORD_ACTIVITY_REQUEST_CODE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cita_trab, menu);
        return true;
    }

    // The options menu has a single item "Clear all data now"
    // that deletes all the entries in the database.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, as long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_data) {
            // Add a toast just for confirmation
            Toast.makeText(this, "R.string.clear_data_toast_text", Toast.LENGTH_LONG).show();

            // Delete the existing data.
            mWordViewModel.deleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When the user enters a new word in the NewWordActivity,
     * that activity returns the result to this activity.
     * If the user entered a new word, save it in the database.
     *
     * @param requestCode ID for the request
     * @param resultCode  indicates success or failure
     * @param data        The Intent sent back from the NewWordActivity,
     *                    which includes the word that the user entered
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CitaTrabajoArchi word = new CitaTrabajoArchi(data.getStringExtra(NuevaCitaTrabajoArchiActivity.EXTRA_REPLY));
            // Save the data.
            word.setFechaCita(Calendar.getInstance().getTime());
            mWordViewModel.insert(word, CitaTrabajoArchiActivity.this);
        } else if (requestCode == UPDATE_WORD_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            String word_data = data.getStringExtra(NuevaCitaTrabajoArchiActivity.EXTRA_REPLY);
            String id = data.getStringExtra(NuevaCitaTrabajoArchiActivity.EXTRA_REPLY_ID);

            if (!id.equals("-1")) {
                mWordViewModel.update(new CitaTrabajoArchi(id, word_data));
            } else {
                Toast.makeText(this, "R.string.unable_to_update",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(
                    this, "R.string.empty_not_saved", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            FirebaseDatabase.getInstance().getReference().child("citas")
                    .removeEventListener(childEventListenerCitas);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        super.onDestroy();
    }
}