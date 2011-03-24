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

public class TabletExtendPmActivity extends PreferenceActivity {
    private static final String PREF_EXTEND_PM_HOME = "pref_extend_pm_home";
    private static final String PREF_EXTEND_PM_MENU = "pref_extend_pm_menu";
    private static final String PREF_EXTEND_PM_BACK = "pref_extend_pm_back";

    private CheckBoxPreference mExtendPmHome;
    private CheckBoxPreference mExtendPmMenu;
    private CheckBoxPreference mExtendPmBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_extend_pm_list_head);
        addPreferencesFromResource(R.xml.tablet_extend_pm);

        PreferenceScreen prefSet = getPreferenceScreen();

        mExtendPmHome = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM_HOME);
        mExtendPmMenu = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM_MENU);
        mExtendPmBack = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM_BACK);

        int defHome=getResources().getBoolean(R.bool.default_extend_pm_home) ? 1 : 0;
        int defMenu=getResources().getBoolean(R.bool.default_extend_pm_menu) ? 1 : 0;
        int defBack=getResources().getBoolean(R.bool.default_extend_pm_back) ? 1 : 0;

        mExtendPmHome.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM_SHOW_HOME, defHome) == 1));
        mExtendPmMenu.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM_SHOW_MENU, defMenu) == 1));
        mExtendPmBack.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM_SHOW_BACK, defBack) == 1));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mExtendPmHome) {
            value = mExtendPmHome.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXTEND_PM_SHOW_HOME,
                    value ? 1 : 0);
            return true;
        } else if (preference == mExtendPmMenu) {
            value = mExtendPmMenu.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXTEND_PM_SHOW_MENU,
                    value ? 1 : 0);
            return true;
        } else if (preference == mExtendPmBack) {
            value = mExtendPmBack.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXTEND_PM_SHOW_BACK,
                    value ? 1 : 0);
            return true;
        }

        return false;
    }
}
