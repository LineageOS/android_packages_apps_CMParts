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

public class TabletTweaksActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    private static final String PREF_STATUS_BAR_BOTTOM = "pref_status_bar_bottom";
    private static final String PREF_EXTEND_PM = "pref_extend_pm";
    private static final String PREF_LONG_VOLP_ACTION = "pref_long_volp_action";
    private static final String PREF_LONG_VOLM_ACTION = "pref_long_volm_action";

    private CheckBoxPreference mStatusBarBottom;
    private CheckBoxPreference mExtendPm;
    private ListPreference mLongVolpAction;
    private ListPreference mLongVolmAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_title_head);
        addPreferencesFromResource(R.xml.tablet_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarBottom = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_BOTTOM);
        mExtendPm = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM);
        mLongVolpAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLP_ACTION);
        mLongVolmAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLM_ACTION);

        int defBottom=getResources().getBoolean(R.bool.default_status_bar_bottom) ? 1 : 0;
        int defExtendPm=getResources().getBoolean(R.bool.default_extend_pm) ? 1 : 0;

        mStatusBarBottom.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_BOTTOM, defBottom) == 1));
        mExtendPm.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM, defExtendPm) == 1));

        mLongVolpAction.setOnPreferenceChangeListener(this);
        mLongVolpAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOLP_ACTION, 0));

        mLongVolmAction.setOnPreferenceChangeListener(this);
        mLongVolmAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOLM_ACTION, 0));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mStatusBarBottom) {
            value = mStatusBarBottom.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_BOTTOM,
                    value ? 1 : 0);
            return true;
        } else if (preference == mExtendPm) {
            value = mExtendPm.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXTEND_PM,
                    value ? 1 : 0);
            return true;
        }

        return false;
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
        }
        return false;
    }
}
