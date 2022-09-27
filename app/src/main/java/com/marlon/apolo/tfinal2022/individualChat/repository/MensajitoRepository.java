package com.marlon.apolo.tfinal2022.individualChat.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;


import com.marlon.apolo.tfinal2022.individualChat.model.Mensajito;
import com.marlon.apolo.tfinal2022.individualChat.roomDatabase.MensajitoDAO;
import com.marlon.apolo.tfinal2022.individualChat.roomDatabase.MensajitoRoomDatabase;

import java.util.List;

public class MensajitoRepository {
    private MensajitoDAO mWordDao;
    private LiveData<List<Mensajito>> mAllWords;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public MensajitoRepository(Application application) {
        MensajitoRoomDatabase db = MensajitoRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getMessages();
    }


    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Mensajito>> getAllWords() {
        return mAllWords;
    }

    public LiveData<List<Mensajito>> getFilterMessages(String from, String to) {
        return mWordDao.getFilterMessages(from, to);
    }

    public LiveData<List<Mensajito>> getFilterMessagesByChat(String idChat) {
        return mWordDao.getFilterMessagesByChat(idChat);
    }

    public Mensajito getLocalMessage(String id) {
        return mWordDao.getLocalMessage(id);
    }

    public void deleteFilterMessage(String from, String to) {
        new deleteFilterAsyncTask(mWordDao).execute(from, to);
    }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    public void insert(Mensajito word) {
        new insertAsyncTask(mWordDao).execute(word);
    }

    public void update(Mensajito word) {
        new updateWordAsyncTask(mWordDao).execute(word);
    }

    public void deleteAll() {
        new deleteAllWordsAsyncTask(mWordDao).execute();
    }

    public void deleteWord(Mensajito word) {
        new deleteWordAsyncTask(mWordDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<Mensajito, Void, Void> {

        private MensajitoDAO mAsyncTaskDao;

        insertAsyncTask(MensajitoDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Mensajito... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private MensajitoDAO mAsyncTaskDao;

        deleteAllWordsAsyncTask(MensajitoDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteFilterAsyncTask extends AsyncTask<String, Void, Void> {
        private MensajitoDAO mAsyncTaskDao;

        deleteFilterAsyncTask(MensajitoDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... voids) {
            mAsyncTaskDao.deleteFilterMessages(voids[0], voids[1]);
            return null;
        }
    }

    private static class deleteWordAsyncTask extends AsyncTask<Mensajito, Void, Void> {
        private MensajitoDAO mAsyncTaskDao;

        deleteWordAsyncTask(MensajitoDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Mensajito... params) {
            mAsyncTaskDao.deleteMensajito(params[0]);
            return null;
        }
    }

    /**
     * Updates a word in the database.
     */
    private static class updateWordAsyncTask extends AsyncTask<Mensajito, Void, Void> {
        private MensajitoDAO mAsyncTaskDao;

        updateWordAsyncTask(MensajitoDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Mensajito... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
