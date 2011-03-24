package com.cyanogenmod.cmparts.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.CmSystem;
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

        int defValue;

        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_POWER_MENU_HOME)==true ? 1 : 0;
        mExtendPmHome.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM_SHOW_HOME, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_POWER_MENU_MENU)==true ? 1 : 0;
        mExtendPmMenu.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM_SHOW_MENU, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_POWER_MENU_BACK)==true ? 1 : 0;
        mExtendPmBack.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM_SHOW_BACK, defValue) == 1));
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
