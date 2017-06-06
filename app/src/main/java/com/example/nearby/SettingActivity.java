package com.example.nearby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
    }


    @Override
    public void onBackPressed() {
        String key = getResources().getString(R.string.pref_key);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean(key, false)){
            startActivity(new Intent(SettingActivity.this, DetailActivity.class));
            finish();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        if(s.equals(getResources().getString(R.string.pref_key))){
//            Preference preference = findPreference(s);
//            preference.setSummary(""+sharedPreferences.getBoolean(s, false));
//            Log.i(SettingActivity.class.getSimpleName(), "preference is "+sharedPreferences.getBoolean(s, false));
//        }else{
//            Log.i(SettingActivity.class.getSimpleName(), "preference is not favorite");
//        }
    }
}
