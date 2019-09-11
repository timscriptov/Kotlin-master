package com.mcal.kotlin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.mcal.kotlin.data.Constants;
import com.mcal.kotlin.data.NightMode;
import com.mcal.kotlin.module.Dialogs;
import com.mcal.kotlin.module.Offline;
import com.mcal.kotlin.utils.Utils;

import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.mcal.kotlin.data.Constants.IS_PREMIUM;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean isPremium;
    private SwitchPreference offline;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        isPremium = requireActivity().getIntent().getBooleanExtra(IS_PREMIUM, false);
        offline = findPreference("offline");

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        switch (key) {
            case "offline":
                if (preferences.getBoolean(key, true) && isPremium) {
                    if (Utils.isNetworkAvailable()) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setTitle(getString(R.string.downloading));
                        new Offline(getActivity()).execute();
                    } else {
                        Dialogs.noConnectionError(getContext());
                    }
                } else if (isPremium) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                File resourcesDir = new File(requireActivity().getFilesDir(), Constants.RESOURCES);
                                FileUtils.deleteDirectory(resourcesDir);
                            } catch (IOException ignored) {
                            }
                        }
                    });
                } else if (preferences.getBoolean(key, false)) {
                    offline.performClick();
                    Dialogs.show(getContext(), getString(R.string.only_prem));
                }
                break;
            case "fullscreen_mode":
                restartPerfect(requireActivity().getIntent());
                break;
            case "night_mode":
                NightMode.setMode(NightMode.getCurrentMode());
                restartPerfect(requireActivity().getIntent());
                break;
            case "grid_mode":
                requireActivity().setResult(RESULT_OK);
                break;
        }
    }

    private void restartPerfect(Intent intent){
        requireActivity().finish();
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
    }
}
