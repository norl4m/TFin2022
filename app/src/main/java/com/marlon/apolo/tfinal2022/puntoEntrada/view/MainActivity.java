/*
 * @(#)MainActivity.java        1.0 2022/09/30
 *
 * Copyright (C) 2022 Marlon Apolo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marlon.apolo.tfinal2022.puntoEntrada.view;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.marlon.apolo.tfinal2022.BuildConfig;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.ui.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.infoInicial.view.InformacionInicialActivity;

/**
 * Esta clase es el punto de entrada a la aplicación.
 * <p>
 * La clase permite seleccionar entre las interfaces gráficas de información inicial, inicio de sesión
 * y navegación principal de la app.
 */

public class MainActivity extends AppCompatActivity {

    private final static int TIME_SPLASH = 2500;
    private static final int MY_REQUEST_CODE = 2000;
    private AppUpdateManager appUpdateManager;


    /**
     * Este método permite iniciar los componentes de la interfaz gráfica principal
     *
     * @param savedInstanceState objeto Bundle que almacena información en el caso de que se prodruzca un cambio
     *                           de configuración en el dispositivo como: cambiar de orientación la pantalla
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_main);

        // Animations
        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView textViewWelcome = findViewById(R.id.textView1);
        TextView textViewSlogan = findViewById(R.id.textView2);
        TextView textViewVersionCode = findViewById(R.id.textViewVersionCode);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        // Setting animations
        imageViewLogo.setAnimation(topAnimation);
//        textViewWelcome.setAnimation(bottomAnimation);
        textViewWelcome.setAnimation(topAnimation);
        textViewSlogan.setAnimation(bottomAnimation);
//        textViewVersionCode.setText(String.format(Locale.getDefault(), "Versión: %d", versionCode));
        textViewVersionCode.setText(String.format("Versión: %s", versionName));


        /*Esto es una maravilla*/
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorNight = typedValue.data;
        imageViewLogo.setColorFilter(colorNight);
        /*Esto es una maravilla*/

        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getInfoInicialActivityFlag()) {
                    startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
//                            startActivity(new Intent(MainActivity.this, VideoCallMainActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, InformacionInicialActivity.class));
                }
                finish();
            }
        }, TIME_SPLASH);

        /****************************************/


// Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    // For a flexible update, use AppUpdateType.FLEXIBLE
//                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
//                // Request the update.
//                Log.d("TAG", "EXISTE UNA ACTUALIZACIÓN");
//                requestUpdate(appUpdateInfo);
//            } else {
//                Log.d("TAG", "App actualizada");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (getInfoInicialActivityFlag()) {
//                            startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
////                            startActivity(new Intent(MainActivity.this, VideoCallMainActivity.class));
//                        } else {
//                            startActivity(new Intent(MainActivity.this, InformacionInicialActivity.class));
//                        }
//                        finish();
//                    }
//                }, TIME_SPLASH);
//            }
//        });
//

        /****************************************/



//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (getInfoInicialActivityFlag()) {
//                    startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
//                } else {
//                    startActivity(new Intent(MainActivity.this, InformacionInicialActivity.class));
//                }
//                finish();
//            }
//        }, TIME_SPLASH);
        // Checks whether the platform allows the specified type of update,
// and current version staleness.
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    && appUpdateInfo.clientVersionStalenessDays() != null
//                    && appUpdateInfo.clientVersionStalenessDays() >= DAYS_FOR_FLEXIBLE_UPDATE
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
//                // Request the update.
//            }
//        });


    }

    /**
     * Este método permite saltar el Activity que coresponde a la información inicial.
     * <p>
     * El Activity de información inicial aparece solo la primera vez al instalar la aplicación, cuando
     * la bandera se encuentra en false.
     */
    public boolean getInfoInicialActivityFlag() {
        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return prefs.getBoolean("infoInicialActivityFlag", false);
    }

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void requestUpdate(AppUpdateInfo appUpdateInfo) {
        // Create a listener to track request state updates.
        InstallStateUpdatedListener listener = state -> {
            // (Optional) Provide a download progress bar.
            if (state.installStatus() == InstallStatus.DOWNLOADING) {
                long bytesDownloaded = state.bytesDownloaded();
                long totalBytesToDownload = state.totalBytesToDownload();
                // Implement progress bar.
                if (bytesDownloaded == totalBytesToDownload) {
                    //finishAffinity();
                    //startActivity(new Intent(this, MainActivity.class));
                }
            }
            onStateUpdate(state);
            // Log state or install the update.
        };

// Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener);

// Start an update.

// When status updates are no longer needed, unregister the listener.
//        appUpdateManager.unregisterListener(listener);


        try {
            appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Toast.makeText(this, this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
        }
    }


    public void onStateUpdate(InstallState state) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate();
        }

    }

    /* Displays the snackbar notification and call to action. */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.imageViewLogo),
                        "Se acaba de descargar una actualización..",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Reiniciar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
                restartApp();

            }
        });
        snackbar.setActionTextColor(
                getResources().getColor(R.color.teal_200));
        snackbar.show();
    }

    private void restartApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getInfoInicialActivityFlag()) {
                    startActivity(new Intent(MainActivity.this, MainNavigationActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, InformacionInicialActivity.class));
                }
                finish();
            }
        }, TIME_SPLASH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("TAG", "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                alertDialogConfirmar();
                //requestUpdate(appUpdateInfoRequest);

            }
        }
    }

    public void alertDialogConfirmar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Para continuar utilizando la aplicación por favor descargue la última versión.")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    // Checks that the update is not stalled during 'onResume()'.
// However, you should execute this check at all entry points into the app.
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        appUpdateManager
//                .getAppUpdateInfo()
//                .addOnSuccessListener(
//                        appUpdateInfo -> {
//                            if (appUpdateInfo.updateAvailability()
//                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                                // If an in-app update is already running, resume the update.
//                                try {
//                                    appUpdateManager.startUpdateFlowForResult(
//                                            appUpdateInfo,
//                                            IMMEDIATE,
//                                            this,
//                                            MY_REQUEST_CODE);
//                                } catch (IntentSender.SendIntentException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//    }


    // Checks that the update is not stalled during 'onResume()'.
// However, you should execute this check at all entry points into the app.
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        appUpdateManager
//                .getAppUpdateInfo()
//                .addOnSuccessListener(
//                        appUpdateInfo -> {
//
//                            if (appUpdateInfo.updateAvailability()
//                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                                // If an in-app update is already running, resume the update.
//                                try {
//                                    appUpdateManager.startUpdateFlowForResult(
//                                            appUpdateInfo,
//                                            IMMEDIATE,
//                                            this,
//                                            MY_REQUEST_CODE);
//                                } catch (IntentSender.SendIntentException e) {
//                                    e.printStackTrace();
//                                    Toast.makeText(this, this.getString(R.string.error_inesperado), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//    }

}