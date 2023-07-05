package com.marlon.apolo.tfinal2022.ui.cerrarSesion;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.CrazyService;
import com.marlon.apolo.tfinal2022.ui.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;


import java.io.File;
import java.util.Objects;

public class CerrarSesionFragment extends Fragment {

    private static final String TAG = CerrarSesionFragment.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;
    private int usuario;

    //    public static CerrarSesionFragment newInstance() {
//        return new CerrarSesionFragment();
//    }
    public void showProgress(String title, String message) {
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }


    private void setTempFlags() {
        SharedPreferences myPreferences = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editorPref = myPreferences.edit();
        editorPref.putInt("methodTemp", -1);
        editorPref.putString("emailTemp", null);
        editorPref.putString("passTemp", null);
        editorPref.putString("celularTemp", null);
        editorPref.apply();

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cerrar_sesion_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTempFlags();
        String title = "Cerrando sesión";
        String message = "Por favor espere...";
        showProgress(title, message);

        myPreferences = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);

        usuario = myPreferences.getInt("usuario", -1);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


//        Intent stopIntent = new Intent(requireActivity(), ForegroundCustomService.class);
        Intent stopIntent = new Intent(requireActivity(), CrazyService.class);
        requireActivity().stopService(stopIntent);


        if (firebaseUser != null) {

//            firebaseAuth.signOut();
            if (usuario == 0) {/*only admin*/
                cleanLocalAdminDevice();
            }
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());

                        }


                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                            NotificationManager notificationManagerX = requireActivity().getSystemService(NotificationManager.class);
                            notificationManagerX.cancelAll();

                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                NotificationManager notificationManagerX = requireActivity().getSystemService(NotificationManager.class);
                                notificationManagerX.cancelAll();

                            } else {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireActivity());
                                notificationManager.cancelAll();

                            }
                        }

                        SharedPreferences.Editor editor = myPreferences.edit();
                        editor.putInt("usuario", -1);
                        editor.apply();


                        firebaseAuth.signOut();

                        clearApplicationData();
                        try {
                            clearApplicationData();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                        Intent intent = new Intent(requireActivity(), MainNavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        requireActivity().startActivity(intent);
                    }
                }, 1000);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }
    }

    public void cleanLocalAdminDevice() {
        Log.d(TAG, "Clean local admin device");
        boolean adminFlag = myPreferences.getBoolean("adminFlag", false);
        if (adminFlag) {
            editorPref = myPreferences.edit();
            editorPref.putBoolean("adminFlag", false);
            editorPref.putString("key", null);
            editorPref.putString("email", null);
            editorPref.apply();
        }

    }

    public void clearApplicationData() {
        Log.d(TAG, "clearApplicationData");
//        File cache = this.getActivity().getCacheDir();
//        File appDir = new File(cache.getParent());

        File cache = requireActivity().getCacheDir();
        File appDir = new File(Objects.requireNonNull(cache.getParent()));

        Log.d(TAG, cache.toString());
        Log.d(TAG, appDir.toString());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    private void clearAppData() {
        Log.d(TAG, "clearAppData");
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) requireActivity().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = requireActivity().getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}