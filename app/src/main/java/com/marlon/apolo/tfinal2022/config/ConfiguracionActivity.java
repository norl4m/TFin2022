package com.marlon.apolo.tfinal2022.config;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.NetworkTool;

public class ConfiguracionActivity extends AppCompatActivity {


    private NetworkTool networkTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SharedPreferences mPreferences;
        private String sharedPrefFile = "com.marlon.apolo.tesis2021.ui.configuraciones.SETTING_FILE";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPreferences = this.getActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
            Preference preferenceNightMode = this.findPreference("sync_theme");
            Preference preferenceNetwork = this.findPreference("sync_network");
            NetworkTool networkTool = new NetworkTool(requireActivity());

            preferenceNightMode
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            if ((Boolean) newValue) {
                                preference.setSummary("Oscuro");
                                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                                preferencesEditor.putString("summary", "Oscuro").apply();
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);/* recrea las actividades*/

                            } else {
                                preference.setSummary("Claro");
                                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                                preferencesEditor.putString("summary", "Claro").apply();
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);/* recrea las actividades*/

                            }
                            return true;
                        }
                    });
            preferenceNetwork.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    if ((Boolean) newValue) {
//                        preference.setSummary("Oscuro");
                        preferencesEditor.putBoolean("summary", true).apply();
                        networkTool.cambiadaSoloWifi();
                    } else {
//                        preference.setSummary("Claro");
                        networkTool.cambiadaDatosMovyWifi();
                        preferencesEditor.putBoolean("summary", false).apply();
                    }
                    return true;
                }
            });
        }
    }

}