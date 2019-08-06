package com.example.user.ble;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "SettingActivity";
    EditTextPreference spo2_edit_high,spo2_edit_low,heartRate_edit_high,heartRate_edit_low,pi_edit_high,pi_edit_low;
    PreferenceScreen screen_preference_spo2_high,screen_preference_spo2_low,screen_preference_heartRate_high,screen_preference_heartRate_low,screen_preference_pi_high,screen_preference_pi_low;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spo2_edit_high = (EditTextPreference)getPreferenceScreen().findPreference("spo2_edit_high");
        screen_preference_spo2_high = (PreferenceScreen)getPreferenceScreen().findPreference("screen_preference_spo2_high");
        spo2_edit_low = (EditTextPreference)getPreferenceScreen().findPreference("spo2_edit_low");
        screen_preference_spo2_low = (PreferenceScreen)getPreferenceScreen().findPreference("screen_preference_spo2_low");

        heartRate_edit_high = (EditTextPreference)getPreferenceScreen().findPreference("heartRate_edit_high");
        screen_preference_heartRate_high = (PreferenceScreen)getPreferenceScreen().findPreference("screen_preference_heartRate_high");
        heartRate_edit_low = (EditTextPreference)getPreferenceScreen().findPreference("heartRate_edit_low");
        screen_preference_heartRate_low = (PreferenceScreen)getPreferenceScreen().findPreference("screen_preference_heartRate_low");

        pi_edit_high = (EditTextPreference)getPreferenceScreen().findPreference("pi_edit_high");
        screen_preference_pi_high = (PreferenceScreen)getPreferenceScreen().findPreference("screen_preference_pi_high");
        pi_edit_low = (EditTextPreference)getPreferenceScreen().findPreference("pi_edit_low");
        screen_preference_pi_low = (PreferenceScreen)getPreferenceScreen().findPreference("screen_preference_pi_low");

        init_summary();
        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String userId = sharedPreferences.getString("spo2_edit_high","");
        Log.e("getSharedPreferences",userId);
        Toast.makeText(this,userId,Toast.LENGTH_LONG).show();*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /* get preference */

        Log.e("Key",key);
        if(key.equals("spo2_edit_high"))
        {
            spo2_edit_high.setSummary(spo2_edit_high.getText());
            screen_preference_spo2_high.setSummary(spo2_edit_high.getText());
        }else if(key.equals("spo2_edit_low")){
            spo2_edit_low.setSummary(spo2_edit_low.getText());
            screen_preference_spo2_low.setSummary(spo2_edit_low.getText());
        }else if(key.equals("heartRate_edit_high"))
        {
            heartRate_edit_high.setSummary(heartRate_edit_high.getText());
            screen_preference_heartRate_high.setSummary(heartRate_edit_high.getText());
        }else if(key.equals("heartRate_edit_low")){
            heartRate_edit_low.setSummary(heartRate_edit_low.getText());
            screen_preference_heartRate_low.setSummary(heartRate_edit_low.getText());
        }else if(key.equals("pi_edit_high"))
        {
            pi_edit_high.setSummary(pi_edit_high.getText());
            screen_preference_pi_high.setSummary(pi_edit_high.getText());
        }else if(key.equals("pi_edit_low")){
            pi_edit_low.setSummary(pi_edit_low.getText());
            screen_preference_pi_low.setSummary(pi_edit_low.getText());
        }
    }

    public void init_summary()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        spo2_edit_high.setSummary(sharedPreferences.getString("spo2_edit_high",""));
        screen_preference_spo2_high.setSummary(sharedPreferences.getString("spo2_edit_high",""));

        spo2_edit_low.setSummary(sharedPreferences.getString("spo2_edit_low",""));
        screen_preference_spo2_low.setSummary(sharedPreferences.getString("spo2_edit_low",""));

        heartRate_edit_high.setSummary(sharedPreferences.getString("heartRate_edit_high",""));
        screen_preference_heartRate_high.setSummary(sharedPreferences.getString("heartRate_edit_high",""));

        heartRate_edit_low.setSummary(sharedPreferences.getString("heartRate_edit_low",""));
        screen_preference_heartRate_low.setSummary(sharedPreferences.getString("heartRate_edit_low",""));

        //tv_bpmPR_boundary.setText(str_heartRate_edit_low+"/"+str_heartRate_edit_high);

        pi_edit_high.setSummary(sharedPreferences.getString("pi_edit_high",""));
        screen_preference_pi_high.setSummary(sharedPreferences.getString("pi_edit_high",""));

        pi_edit_low.setSummary(sharedPreferences.getString("pi_edit_low",""));
        screen_preference_pi_low.setSummary(sharedPreferences.getString("pi_edit_low",""));

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume()");

        //spo2_edit_high.setSummary(spo2_edit_high.getText());
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        Log.e("onPause()","true");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
