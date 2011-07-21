/*
 * Copyright (C) 2011 The CyanogenMod Project
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

package com.cyanogenmod.cmparts.activities;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class SystemActivity extends PreferenceActivity implements
OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.system_settings_title_subhead);
        addPreferencesFromResource(R.xml.system_settings);
        findPreference("changelog").setSummary(getString(R.string.changelog_version) 
                + ": " + SystemProperties.get("ro.modversion", 
                getResources().getString(R.string.changelog_unknown)));
        ListPreference btpref = (ListPreference) findPreference("pref_ext_bt_gps");
        if (btpref != null) {
            // add known bonded BT devices
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
                entries.add("Internal GPS");
                ArrayList<CharSequence> values = new ArrayList<CharSequence>();
                values.add("0");
                ArrayList<BluetoothDevice> tmp = 
                    new ArrayList<BluetoothDevice>(mBluetoothAdapter.getBondedDevices());
                for (BluetoothDevice d : tmp) {
                    String dname = d.getName() + " - " + d.getAddress();
                    entries.add(dname);
                    values.add(d.getAddress());
                }
                btpref.setEntries(entries.toArray(new CharSequence[entries.size()]));
                btpref.setEntryValues(values.toArray(new CharSequence[values.size()]));
                btpref.setOnPreferenceChangeListener(this);
            }
        }
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String newPref = (String) newValue;
        // "0" represents the internal GPS.
        Settings.System.putString(getContentResolver(), Settings.System.EXTERNAL_GPS_BT_DEVICE,
                newPref == null ? "0" : newPref);
        return true;
    }

}