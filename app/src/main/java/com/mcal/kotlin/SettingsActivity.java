package com.mcal.kotlin;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.mcal.kotlin.model.BaseActivity;
import com.yarolegovich.mp.MaterialSwitchPreference;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, new SettingsFragment())
                .commit();
    }


}
