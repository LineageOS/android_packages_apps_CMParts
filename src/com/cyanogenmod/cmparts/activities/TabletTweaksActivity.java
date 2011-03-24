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

public class TabletTweaksActivity extends PreferenceActivity implements OnPreferenceChangeListener{
    private static final String PREF_STATUS_BAR_BOTTOM = "pref_status_bar_bottom";
    private static final String PREF_STATUS_BAR_DEAD_ZONE = "pref_status_bar_dead_zone";
    private static final String PREF_SOFT_BUTTONS_LEFT = "pref_soft_buttons_left";
    private static final String PREF_DISABLE_LOCKSCREEN = "pref_disable_lockscreen";
    private static final String PREF_DISABLE_FULLSCREEN = "pref_disable_fullscreen";
    private static final String PREF_UNHIDE_BUTTON = "pref_unhide_button";
    private static final String PREF_EXTEND_PM = "pref_extend_pm";
    private static final String PREF_REVERSE_VOLUME_BEHAVIOR = "pref_reverse_volume_behavior";

    private CheckBoxPreference mStatusBarBottom;
    private CheckBoxPreference mStatusBarDeadZone;
    private CheckBoxPreference mSoftButtonsLeft;
    private CheckBoxPreference mDisableLockscreen;
    private CheckBoxPreference mDisableFullscreen;
    private CheckBoxPreference mExtendPm;
    private CheckBoxPreference mReverseVolumeBehavior;
    private ListPreference mUnhideButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_title_head);
        addPreferencesFromResource(R.xml.tablet_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarBottom = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_BOTTOM);
        mStatusBarDeadZone = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_DEAD_ZONE);
        mSoftButtonsLeft = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_LEFT);
        mDisableLockscreen = (CheckBoxPreference) prefSet.findPreference(PREF_DISABLE_LOCKSCREEN);
        mDisableFullscreen = (CheckBoxPreference) prefSet.findPreference(PREF_DISABLE_FULLSCREEN);
        mUnhideButton = (ListPreference) prefSet.findPreference(PREF_UNHIDE_BUTTON);
        mExtendPm = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM);
        mReverseVolumeBehavior = (CheckBoxPreference) prefSet.findPreference(PREF_REVERSE_VOLUME_BEHAVIOR);

        int defBottom=getResources().getBoolean(R.bool.default_status_bar_bottom) ? 1 : 0;
        int defLeft=getResources().getBoolean(R.bool.default_soft_buttons_left) ? 1 : 0;
        int defExtendPm=getResources().getBoolean(R.bool.default_extend_pm) ? 1 : 0;

        mStatusBarBottom.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_BOTTOM, defBottom) == 1));
        mStatusBarDeadZone.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_DEAD_ZONE, 0) == 1));
        mSoftButtonsLeft.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTONS_LEFT, defLeft) == 1));
        mDisableLockscreen.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_DISABLED, 0) == 1));
        mDisableFullscreen.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FULLSCREEN_DISABLED, 0) == 1));
        mExtendPm.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM, defExtendPm) == 1));
        mReverseVolumeBehavior.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.REVERSE_VOLUME_BEHAVIOR, 0) == 1));

        mUnhideButton.setOnPreferenceChangeListener(this);
        mUnhideButton.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.UNHIDE_BUTTON, 0));

        updateDependencies();
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
        } else if (preference == mDisableLockscreen) {
            value = mDisableLockscreen.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_DISABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mDisableFullscreen) {
            value = mDisableFullscreen.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.FULLSCREEN_DISABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mExtendPm) {
            value = mExtendPm.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXTEND_PM,
                    value ? 1 : 0);
            return true;
        } else if (preference == mReverseVolumeBehavior) {
            value = mReverseVolumeBehavior.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.REVERSE_VOLUME_BEHAVIOR,
                    value ? 1 : 0);
            updateDependencies();
            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUnhideButton) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.UNHIDE_BUTTON, value);
            return true;
        }
        return false;
    }

    private void updateDependencies() {
        if(!mStatusBarBottom.isChecked()){
            mStatusBarDeadZone.setChecked(false);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_DEAD_ZONE, 0);
        }

        if(mReverseVolumeBehavior.isChecked())
            mReverseVolumeBehavior.setSummary(R.string.tablet_tweaks_reverse_volume_behavior_summary_on);
        else
            mReverseVolumeBehavior.setSummary(R.string.tablet_tweaks_reverse_volume_behavior_summary_off);
    }
}
