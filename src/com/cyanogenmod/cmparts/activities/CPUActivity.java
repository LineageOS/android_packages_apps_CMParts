package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Process;

//
// CPU Related Settings
//
public class CPUActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String GOV_PREF = "pref_cpu_gov";
    private static final String GOVERNORS_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    private static final String GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

    private static final String MIN_FREQ_PREF = "pref_freq_min";
    private static final String MAX_FREQ_PREF = "pref_freq_max";
    private static final String FREQ_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static final String FREQ_MAX_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    private static final String FREQ_MIN_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    private static final String LOGTAG = "CPUSettings";
    private String GOV_FMT;
    private String MIN_FMT;
    private String MAX_FMT;

    private ListPreference GovPref;
    private ListPreference MinFreqPref;
    private ListPreference MaxFreqPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GOV_FMT = getString(R.string.cpu_governors_list);
        MIN_FMT = getString(R.string.cpu_min_freq);
        MAX_FMT = getString(R.string.cpu_max_freq);

        String[] Governors = ReadOneLine(GOVERNORS_LIST_FILE).split(" ");
        String[] FreqValues = ReadOneLine(FREQ_LIST_FILE).split(" ");
        String[] Freqs;
        String temp;

        Freqs = new String[FreqValues.length];
        for(int i=0; i <Freqs.length; i++) {
            Freqs[i] = MHerzed(FreqValues[i]);
        }

        //
        // UI
        //
        setTitle(R.string.cpu_title);
        addPreferencesFromResource(R.xml.cpu_settings);
        
        PreferenceScreen PrefScreen = getPreferenceScreen();

        temp = ReadOneLine(GOVERNOR);

        GovPref = (ListPreference) PrefScreen.findPreference(GOV_PREF);
        GovPref.setEntryValues(Governors);
        GovPref.setEntries(Governors);
        GovPref.setValue(temp);
        GovPref.setSummary(String.format(GOV_FMT, temp));
        GovPref.setOnPreferenceChangeListener(this);

        temp = ReadOneLine(FREQ_MIN_FILE);

        MinFreqPref = (ListPreference) PrefScreen.findPreference(MIN_FREQ_PREF);
        MinFreqPref.setEntryValues(FreqValues);
        MinFreqPref.setEntries(Freqs);
        MinFreqPref.setValue(temp);
        MinFreqPref.setSummary(String.format(MIN_FMT, MHerzed(temp)));
        MinFreqPref.setOnPreferenceChangeListener(this);

        temp = ReadOneLine(FREQ_MAX_FILE);

        MaxFreqPref = (ListPreference) PrefScreen.findPreference(MAX_FREQ_PREF);
        MaxFreqPref.setEntryValues(FreqValues);
        MaxFreqPref.setEntries(Freqs);
        MaxFreqPref.setValue(temp);
        MaxFreqPref.setSummary(String.format(MAX_FMT, MHerzed(temp)));
        MaxFreqPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        String temp;

        super.onResume();

        temp = ReadOneLine(FREQ_MAX_FILE);
        MaxFreqPref.setValue(temp);
        MaxFreqPref.setSummary(String.format(MAX_FMT, MHerzed(temp)));

        temp = ReadOneLine(FREQ_MIN_FILE);
        MinFreqPref.setValue(temp);
        MinFreqPref.setSummary(String.format(MIN_FMT, MHerzed(temp)));

        temp = ReadOneLine(GOVERNOR);
        GovPref.setSummary(String.format(GOV_FMT, temp));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == GovPref) {
                fname = GOVERNOR;
                //rootcmd = "echo " + (String) newValue + " > " + GOVERNOR + "\n";
            } else if (preference == MinFreqPref) {
                fname = FREQ_MIN_FILE;
                //rootcmd = "echo " + (String) newValue + " > " + FREQ_MIN_FILE + "\n";
            } else if (preference == MaxFreqPref) {
                fname = FREQ_MAX_FILE;
                //rootcmd = "echo " + (String) newValue + " > " + FREQ_MAX_FILE + "\n";
            }
            try {
                FileWriter fw = new FileWriter(fname);
                try {
                    fw.write((String) newValue);
                } finally {
                    fw.close();
                }
            } catch(IOException e) {
                Log.e(LOGTAG, "Error writing to " + fname + ". Exception: ", e);
                return false;
            }
            if (preference == GovPref) {
                GovPref.setSummary(String.format(GOV_FMT, (String) newValue));
            } else if (preference == MinFreqPref) {
                MinFreqPref.setSummary(String.format(MIN_FMT, MHerzed((String) newValue)));
            } else if (preference == MaxFreqPref) {
                MaxFreqPref.setSummary(String.format(MAX_FMT, MHerzed((String) newValue)));
            }
            return true;
        } else {
            return false;
        }
    }

    private String ReadOneLine(String fname) {
        BufferedReader br;
        String line = null;

        try {
            br = new BufferedReader (new FileReader(fname), 512);
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "IO Exception when reading /sys/ file", e);
        }
        return line;
    }

    private String MHerzed(String str) {
        String temp;

        temp = str.substring(0, str.length() - 3);

        return (temp + " MHz");
    }
}
