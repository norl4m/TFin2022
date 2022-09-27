package com.marlon.apolo.tfinal2022.individualChat.roomDatabase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.marlon.apolo.tfinal2022.individualChat.model.Mensajito;


/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */

//@Database(entities = {Mensajito.class}, version = 1, exportSchema = false)
//@Database(entities = {Mensajito.class}, version = 2, exportSchema = false)
//@Database(entities = {Mensajito.class}, version = 3, exportSchema = false)
//@Database(entities = {Mensajito.class}, version = 4, exportSchema = false)
//@Database(entities = {Mensajito.class}, version = 5, exportSchema = false)
@Database(entities = {Mensajito.class}, version = 1, exportSchema = false)
public abstract class MensajitoRoomDatabase extends RoomDatabase {
    public abstract MensajitoDAO wordDao();

    private static MensajitoRoomDatabase INSTANCE;

    public static MensajitoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MensajitoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MensajitoRoomDatabase.class, "message_database")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this codelab.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     */
    private static Callback sRoomDatabaseCallback = new Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // If you want to keep the data through app restarts,
            // comment out the following line.
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    /**
     * Populate the database in the background.
     * If you want to start with more words, just add them.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final MensajitoDAO mDao;
        String[] words = {"dolphin", "crocodile", "cobra"};
        private String TAG = PopulateDbAsync.class.getSimpleName();

        PopulateDbAsync(MensajitoRoomDatabase db) {
            mDao = db.wordDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
//            mDao.deleteAll();
//
//            for( int i = 0; i <= words.length - 1; i++) {
//                Mensajito word = new Mensajito(words[i]);
//                mDao.insert(word);
//            }


            Log.e(TAG, "PopulateDbAsync");
            // If we have no words, then create the initial list of words
//            if (mDao.getAnyWord().length < 1) {
//                for (int i = 0; i <= words.length - 1; i++) {
//                    Mensajito word = new Mensajito(words[i]);
//                    mDao.insert(word);
//                }
//            }
            return null;
        }
    }
}
