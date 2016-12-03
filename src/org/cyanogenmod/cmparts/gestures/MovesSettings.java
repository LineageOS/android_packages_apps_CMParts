/**
 * Copyright (C) 2016 The CyanogenMod project
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

package org.cyanogenmod.cmparts.gestures;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.hardware.TouchscreenGesture;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

public class MovesSettings extends SettingsPreferenceFragment {

    private static final String TAG = "MovesSettings";

    private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
    private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";
    private static final String TOUCHSCREEN_GESTURE_SUMMARY =
            KEY_TOUCHSCREEN_GESTURE + "_%s_summary";

    private TouchscreenGesture[] mGestures;
    private CMHardwareManager mManager;

    public static final SummaryProvider SUMMARY_PROVIDER = new SummaryProvider() {
        @Override
        public String getSummary(final Context context, final String key) {
            return context.getString(R.string.moves_settings_summary);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.moves_settings);

        mManager = CMHardwareManager.getInstance(getContext());
        mGestures = mManager.getTouchscreenGestures();

        final PreferenceCategory category =
                (PreferenceCategory) findPreference("touchscreen_gestures_category");
        for (final TouchscreenGesture gesture : mGestures) {
            category.addPreference(new TouchscreenGesturePreference(getContext(), gesture));
        }
    }

    private String getStringForResourceName(final Resources res, final String resourceName,
                                                   final String defaultValue) {
        final int resId = res.getIdentifier(resourceName, "string", "org.cyanogenmod.cmparts");
        if (resId <= 0) {
            Log.e(TAG, "No resource found for " + resourceName);
            return defaultValue;
        } else {
            return res.getString(resId);
        }
    }

    private String getLocalizedGestureTitle(final Resources res, final String title) {
        final String name = title.toLowerCase().replace(" ", "_");
        final String nameRes = String.format(TOUCHSCREEN_GESTURE_TITLE, name);
        return getStringForResourceName(res, nameRes, title);
    }

    private class TouchscreenGesturePreference extends ListPreference
            implements Preference.OnPreferenceChangeListener {

        private Context mContext;
        private TouchscreenGesture mGesture;

        public TouchscreenGesturePreference(final Context context,
                                            final TouchscreenGesture gesture) {
            super(context);
            mContext = context;
            mGesture = gesture;

            setLayoutResource(R.layout.touchscreen_gesture_list_item);

            final Resources res = mContext.getResources();
            setTitle(getLocalizedGestureTitle(res, mGesture.name));
            setSummary("%s");
            // TODO: Set the icon to that of the respective action
            setIcon(R.drawable.ic_settings_touchscreen_gesture);
            setKey(String.valueOf(mGesture.id));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            if (getValue() == null) {
                // Set default value to 0
                setValueIndex(0);
            }
            setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            final boolean enabled = Integer.parseInt(String.valueOf(newValue)) > 0;
            return mManager.setTouchscreenGestureEnabled(mGesture, enabled);
        }
    }
}
