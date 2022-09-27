package com.marlon.apolo.tfinal2022.herramientasAsíncronas;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class TemporizadorAyncTask extends AsyncTask<Void, Integer, String> {
    // The TextView where we will show results
    private WeakReference<TextView> mTextView;
    private WeakReference<TextView> textViewWeakReferenceContador;
    private WeakReference<Button> buttonWeakReferenceVrificar;
    private WeakReference<Button> buttonWeakReferenceEnviar;
    private WeakReference<Button> buttonWeakReferenceSolicitar;
    private WeakReference<ProgressBar> progressBarWeakReference;
    private static ClickListener clickListener;

    // Constructor that provides a reference to the TextView from the MainActivity
    public TemporizadorAyncTask(TextView tv) {
        mTextView = new WeakReference<>(tv);
    }

    public TemporizadorAyncTask(TextView tv, Button buttonEnviar, Button buttonSolicitar, Button buttonVerificar, ProgressBar progressBar, TextView textView) {
        mTextView = new WeakReference<>(tv);
        mTextView.get().setText("");
        mTextView.get().setVisibility(View.VISIBLE);
        buttonWeakReferenceVrificar = new WeakReference<>(buttonVerificar);
        buttonWeakReferenceEnviar = new WeakReference<>(buttonEnviar);
        buttonWeakReferenceSolicitar = new WeakReference<>(buttonSolicitar);
        progressBarWeakReference = new WeakReference<>(progressBar);
        progressBarWeakReference = new WeakReference<>(progressBar);
        textViewWeakReferenceContador = new WeakReference<>(textView);

    }


    /**
     * Runs on the background thread.
     *
     * @param voids No parameters in this use case.
     * @return Returns the string including the amount of time that
     * the background thread slept.
     */
    @Override
    protected String doInBackground(Void... voids) {

        // Generate a random number between 0 and 10.
        int n = 2;

        // Make the task take long enough that we have
        // time to rotate the phone while it is running.
        int s = n * 60000; // 2 minutos


        progressBarWeakReference.get().setProgress(0);   // Main Progress
        progressBarWeakReference.get().setMax(60); // Maximum Progress


        int pStatus = 60;
        while (pStatus >= 0) {
            progressBarWeakReference.get().setProgress(pStatus);
            textViewWeakReferenceContador.get().setText(String.format("%s s", pStatus));

            try {
                publishProgress(pStatus);
                if (isCancelled()) {
                    mTextView.get().setText("");
                    mTextView.get().setVisibility(View.GONE);
                    break;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pStatus--;
        }


//        for (int i = 60; i > 0; i--) {
//            // Escape early if cancel() is called
//
//            // Sleep for the random amount of time.
//            try {
//                publishProgress(i);
//                if (isCancelled()) {
//                    mTextView.get().setText("");
//                    mTextView.get().setVisibility(View.GONE);
//                    break;
//                }
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }


        // Return a String result.
//        return "Awake at last after sleeping for " + n + " minutes!";
        return "Por favor solicite un nuevo código de verificación!";
    }

    /**
     * Does something with the result on the UI thread; in this case
     * updates the TextView.
     */
    protected void onPostExecute(String result) {
        clickListener.onTokenListener(result);
        mTextView.get().setText(result);
        try {
            buttonWeakReferenceSolicitar.get().setVisibility(View.VISIBLE);
            buttonWeakReferenceEnviar.get().setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    public void setOnItemClickListener(ClickListener varClickListener) {
        clickListener = varClickListener;
    }

    public interface ClickListener {
        void onTokenListener(String token);
    }

    protected void onProgressUpdate(Integer... progress) {
        mTextView.get().setText(String.format("Su código tiene una vigencia de %s segundos", progress[0]));
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
