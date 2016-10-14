/**
 * Copyright (C) 2015-2016 The CyanogenMod Project
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

package org.cyanogenmod.cmparts.applications;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.WindowManagerPolicyControl;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

public class ExpandedDesktopPrefs extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_EXPANDED_DESKTOP_STYLE = "expanded_desktop_style";

    private ListPreference mExpandedDesktopStylePref;
    private int mExpandedDesktopStyle;
    private final SettingsObserver mSettingsObserver = new SettingsObserver(new Handler());

    public static ExpandedDesktopPrefs newInstance() {
        ExpandedDesktopPrefs expandedDesktopPrefs = new ExpandedDesktopPrefs();
        return expandedDesktopPrefs;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasNavigationBar = true;
        try {
            hasNavigationBar = WindowManagerGlobal.getWindowManagerService().hasNavigationBar();
        } catch (RemoteException e) {
            // Do nothing
        }
        if (hasNavigationBar) {
            addPreferencesFromResource(R.xml.expanded_desktop_settings);
            mExpandedDesktopStyle = getExpandedDesktopStyle();
            createPreferences();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSettingsObserver.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingsObserver.unregister();
    }

    private void createPreferences() {
        mExpandedDesktopStylePref = (ListPreference) findPreference(KEY_EXPANDED_DESKTOP_STYLE);
        mExpandedDesktopStylePref.setOnPreferenceChangeListener(this);
        updateExpandedDesktopStyle();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final int val = Integer.parseInt((String) value);
        WindowManagerPolicyControl.saveStyleToSettings(getActivity(), val);
        return true;
    }

    private void updateExpandedDesktopStyle() {
        if (mExpandedDesktopStylePref == null) {
            return;
        }
        mExpandedDesktopStyle = getExpandedDesktopStyle();
        mExpandedDesktopStylePref.setValueIndex(mExpandedDesktopStyle);
        mExpandedDesktopStylePref.setSummary(getDesktopSummary(mExpandedDesktopStyle));
        // We need to visually show the change
        // TODO: This is hacky, but it works
        writeValue("");
        writeValue("immersive.full=*");
    }

    private int getDesktopSummary(final int state) {
        switch (state) {
            case WindowManagerPolicyControl.ImmersiveDefaultStyles.IMMERSIVE_STATUS:
                return R.string.expanded_desktop_style_hide_status;
            case WindowManagerPolicyControl.ImmersiveDefaultStyles.IMMERSIVE_NAVIGATION:
                return R.string.expanded_desktop_style_hide_navigation;
            case WindowManagerPolicyControl.ImmersiveDefaultStyles.IMMERSIVE_FULL:
            default:
                return R.string.expanded_desktop_style_hide_both;
        }
    }

    private int getExpandedDesktopStyle() {
        return Settings.Global.getInt(getContentResolver(),
                Settings.Global.POLICY_CONTROL_STYLE,
                WindowManagerPolicyControl.ImmersiveDefaultStyles.IMMERSIVE_FULL);
    }

    private void writeValue(final String value) {
        Settings.Global.putString(getContentResolver(), Settings.Global.POLICY_CONTROL, value);
    }

    // === Window Policy Style Callbacks ===

    private final class SettingsObserver extends ContentObserver {
        private final Uri DEFAULT_WINDOW_POLICY_STYLE =
                Settings.Global.getUriFor(Settings.Global.POLICY_CONTROL_STYLE);

        SettingsObserver(final Handler handler) {
            super(handler);
        }

        void register() {
            getContentResolver().registerContentObserver(DEFAULT_WINDOW_POLICY_STYLE, false, this);
        }

        void unregister() {
            getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(final boolean selfChange) {
            super.onChange(selfChange);
            updateExpandedDesktopStyle();
        }
    }
}
