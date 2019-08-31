package com.mcal.kotlin.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import androidx.appcompat.app.AppCompatActivity;

import com.mcal.kotlin.R;
import com.mcal.kotlin.data.NightMode;
import com.mcal.kotlin.module.Dialogs;
import com.mcal.kotlin.module.Offline;
import com.mcal.kotlin.utils.Utils;

import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.mcal.kotlin.data.Constants.IS_PREMIUM;
import static com.mcal.kotlin.data.Constants.PACKAGE_NAME;
import com.mcal.kotlin.data.*;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference font_size;
    private ListPreference lang;
    private SwitchPreference offline;
    private boolean isVip;//hasResult;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p1, String p2) {
        switch (p2) {
            case "offline":
                if (p1.getBoolean(p2, false) && isVip) {
                    if (Utils.isNetworkAvailable()) {
                        ProgressFragment progressFragment = new ProgressFragment();
                        progressFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), null);
                        new Offline(progressFragment, offline).execute();
                    } else {
                        offline.setChecked(false);
                        Dialogs.noConnectionError(getActivity());
                    }
                } else if (isVip) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                File resourcesDir = new File("data/data/" + PACKAGE_NAME + "/files/resources");
                                FileUtils.deleteDirectory(resourcesDir);
                            } catch (IOException ignored) {
                            }
                        }
                    });
                } else if (p1.getBoolean(p2, false)) {
                    offline.setChecked(false);
                    Dialogs.show(getActivity(), getString(R.string.only_prem));
                }
                break;
            case "fullscreen_mode":
                getActivity().recreate();
                break;
            case "night_mode":
                NightMode.setMode(NightMode.getCurrentMode());
                getActivity().recreate();
                break;
            case "grid_mode":
                getActivity().setResult(Activity.RESULT_OK);
                break;
            case "lang":
                lang.setSummary(lang.getEntry());
                break;
            case "font_size":
                font_size.setSummary(font_size.getValue() + "%");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        font_size = (ListPreference) findPreference(Constants.FONT_SIZE);
        font_size.setSummary(font_size.getValue() + "%");
        lang = (ListPreference) findPreference(Constants.LANG);
        lang.setSummary(lang.getEntry());
        offline = (SwitchPreference) findPreference(Constants.OFFLINE);
        isVip = getActivity().getIntent().getBooleanExtra(IS_PREMIUM, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}
