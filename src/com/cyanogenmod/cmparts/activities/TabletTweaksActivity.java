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
    private static final String PREF_STATUS_BAR_DEAD_ZONE = "pref_status_bar_dead_zone";
    private static final String PREF_SOFT_BUTTONS_LEFT = "pref_soft_buttons_left";
    private static final String PREF_EXTEND_PM = "pref_extend_pm";
    private static final String PREF_LONG_VOLP_ACTION = "pref_long_volp_action";
    private static final String PREF_LONG_VOLM_ACTION = "pref_long_volm_action";
    private static final String PREF_LONG_VOL_BOTH_ACTION = "pref_long_vol_both_action";
    private static final String PREF_VOL_BOTH_ACTION = "pref_vol_both_action";

    private CheckBoxPreference mStatusBarBottom;
    private CheckBoxPreference mStatusBarDeadZone;
    private CheckBoxPreference mSoftButtonsLeft;
    private CheckBoxPreference mExtendPm;
    private ListPreference mLongVolpAction;
    private ListPreference mLongVolmAction;
    private ListPreference mVolBothAction;
    private ListPreference mLongVolBothAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_title_head);
        addPreferencesFromResource(R.xml.tablet_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarBottom = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_BOTTOM);
        mStatusBarDeadZone = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_DEAD_ZONE);
        mSoftButtonsLeft = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_LEFT);
        mExtendPm = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM);
        mLongVolpAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLP_ACTION);
        mLongVolmAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLM_ACTION);
        mVolBothAction = (ListPreference) prefSet.findPreference(PREF_VOL_BOTH_ACTION);
        mLongVolBothAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOL_BOTH_ACTION);

        int defBottom=getResources().getBoolean(R.bool.default_status_bar_bottom) ? 1 : 0;
        int defLeft=getResources().getBoolean(R.bool.default_soft_buttons_left) ? 1 : 0;
        int defExtendPm=getResources().getBoolean(R.bool.default_extend_pm) ? 1 : 0;

        mStatusBarBottom.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_BOTTOM, defBottom) == 1));
        mStatusBarDeadZone.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_DEAD_ZONE, 0) == 1));
        mSoftButtonsLeft.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTONS_LEFT, defLeft) == 1));
        mExtendPm.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM, defExtendPm) == 1));

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

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mStatusBarBottom) {
            value = mStatusBarBottom.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_BOTTOM,
                    value ? 1 : 0);
            updateDependencies();
            return true;
        } else if (preference == mStatusBarDeadZone) {
            value = mStatusBarDeadZone.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_DEAD_ZONE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSoftButtonsLeft) {
            value = mSoftButtonsLeft.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTONS_LEFT,
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
        } else if (preference == mLongVolBothAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOL_BOTH_ACTION,
                    value);
            return true;
        }
        return false;
    }

    private void updateDependencies() {
        if(!mStatusBarBottom.isChecked()){
            mStatusBarDeadZone.setChecked(false);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_DEAD_ZONE, 0);
        }
    }
}
