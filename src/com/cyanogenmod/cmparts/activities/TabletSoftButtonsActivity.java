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

public class TabletSoftButtonsActivity extends PreferenceActivity {
    private static final String PREF_SOFT_BUTTONS_HOME = "pref_soft_buttons_home";
    private static final String PREF_SOFT_BUTTONS_MENU = "pref_soft_buttons_menu";
    private static final String PREF_SOFT_BUTTONS_BACK = "pref_soft_buttons_back";
    private static final String PREF_SOFT_BUTTONS_SEARCH = "pref_soft_buttons_search";
    private static final String PREF_SOFT_BUTTONS_QUICK_NA = "pref_soft_buttons_quick_na";

    private CheckBoxPreference mSoftButtonsHome;
    private CheckBoxPreference mSoftButtonsMenu;
    private CheckBoxPreference mSoftButtonsBack;
    private CheckBoxPreference mSoftButtonsSearch;
    private CheckBoxPreference mSoftButtonsQuickNa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_soft_buttons_list_head);
        addPreferencesFromResource(R.xml.tablet_soft_buttons);

        PreferenceScreen prefSet = getPreferenceScreen();

        mSoftButtonsHome = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_HOME);
        mSoftButtonsMenu = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_MENU);
        mSoftButtonsBack = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_BACK);
        mSoftButtonsSearch = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_SEARCH);
        mSoftButtonsQuickNa = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_QUICK_NA);

        int defHome=getResources().getBoolean(R.bool.default_soft_button_home) ? 1 : 0;
        int defMenu=getResources().getBoolean(R.bool.default_soft_button_menu) ? 1 : 0;
        int defBack=getResources().getBoolean(R.bool.default_soft_button_back) ? 1 : 0;
        int defSearch=getResources().getBoolean(R.bool.default_soft_button_search) ? 1 : 0;
        int defQuickNa=getResources().getBoolean(R.bool.default_soft_button_quick_na) ? 1 : 0;

        mSoftButtonsHome.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTON_SHOW_HOME, defHome) == 1));
        mSoftButtonsMenu.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTON_SHOW_MENU, defMenu) == 1));
        mSoftButtonsBack.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTON_SHOW_BACK, defBack) == 1));
        mSoftButtonsSearch.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTON_SHOW_SEARCH, 0) == 1));
        mSoftButtonsQuickNa.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTON_SHOW_QUICK_NA, defQuickNa) == 1));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mSoftButtonsHome) {
            value = mSoftButtonsHome.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTON_SHOW_HOME,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSoftButtonsMenu) {
            value = mSoftButtonsMenu.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTON_SHOW_MENU,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSoftButtonsBack) {
            value = mSoftButtonsBack.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTON_SHOW_BACK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSoftButtonsSearch) {
            value = mSoftButtonsSearch.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTON_SHOW_SEARCH,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSoftButtonsQuickNa) {
            value = mSoftButtonsQuickNa.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTON_SHOW_QUICK_NA,
                    value ? 1 : 0);
            return true;
        }

        return false;
    }
}
