package com.cyanogenmod.cmparts.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class TabletInputActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    private static final String PREF_LONG_VOLP_ACTION = "pref_long_volp_action";
    private static final String PREF_LONG_VOLM_ACTION = "pref_long_volm_action";
    private static final String PREF_LONG_VOL_BOTH_ACTION = "pref_long_vol_both_action";
    private static final String PREF_VOL_BOTH_ACTION = "pref_vol_both_action";

    private ListPreference mLongVolpAction;
    private ListPreference mLongVolmAction;
    private ListPreference mVolBothAction;
    private ListPreference mLongVolBothAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_input_head);
        addPreferencesFromResource(R.xml.tablet_input_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mLongVolpAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLP_ACTION);
        mLongVolmAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLM_ACTION);
        mVolBothAction = (ListPreference) prefSet.findPreference(PREF_VOL_BOTH_ACTION);
        mLongVolBothAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOL_BOTH_ACTION);

        int defBottom=getResources().getBoolean(R.bool.default_status_bar_bottom) ? 1 : 0;
        int defLeft=getResources().getBoolean(R.bool.default_soft_buttons_left) ? 1 : 0;
        int defExtendPm=getResources().getBoolean(R.bool.default_extend_pm) ? 1 : 0;

        mLongVolpAction.setOnPreferenceChangeListener(this);
        mLongVolpAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOLP_ACTION, 0));

        mLongVolmAction.setOnPreferenceChangeListener(this);
        mLongVolmAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOLM_ACTION, 0));

        mVolBothAction.setOnPreferenceChangeListener(this);
        mVolBothAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.VOL_BOTH_ACTION, 0));

        mLongVolBothAction.setOnPreferenceChangeListener(this);
        mLongVolBothAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOL_BOTH_ACTION, 0));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLongVolpAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOLP_ACTION,
                    value);
            return true;
        } else if (preference == mLongVolmAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOLM_ACTION,
                    value);
            return true;
        } else if (preference == mVolBothAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.VOL_BOTH_ACTION,
                    value);
            return true;
        } else if (preference == mLongVolBothAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOL_BOTH_ACTION,
                    value);
            return true;
        }
        return false;
    }
}
