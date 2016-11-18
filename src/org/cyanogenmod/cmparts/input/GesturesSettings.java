/*
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

package org.cyanogenmod.cmparts.input;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.util.Log;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.hardware.TouchscreenGesture;
import cyanogenmod.providers.CMSettings;

public class GesturesSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_FLIP_TO_MUTE = "flip_to_mute_gesture";
    private static final String KEY_GESTURE_CIRCLE = "circle_gesture";
    private static final String KEY_GESTURE_TWO_FINGERS_DOWN = "two_fingers_down_gesture";
    private static final String KEY_GESTURE_LEFTWARDS_ARROW = "leftwards_arrow_gesture";
    private static final String KEY_GESTURE_RIGHTWARDS_ARROW = "rightwards_arrow_gesture";
    private static final String KEY_GESTURE_LETTER_C = "letter_c_gesture";
    private static final String KEY_GESTURE_LETTER_E = "letter_e_gesture";
    private static final String KEY_GESTURE_LETTER_S = "letter_s_gesture";
    private static final String KEY_GESTURE_LETTER_V = "letter_v_gesture";
    private static final String KEY_GESTURE_LETTER_W = "letter_w_gesture";
    private static final String KEY_GESTURE_LETTER_Z = "letter_z_gesture";

    private Context mContext;
    private CMHardwareManager mManager;
    private PreferenceScreen mPrefScreen;
    private TouchscreenGesture[] mSupportedGestures;

    private SwitchPreference mFlipToMuitePref;
    private GesturePreference mCircleGesture = new GesturePreference();
    private GesturePreference mTwoFingersDownGesture = new GesturePreference();
    private GesturePreference mRightWardsArrowGesture = new GesturePreference();
    private GesturePreference mLeftwardsArrowGesture = new GesturePreference();
    private GesturePreference mLetterCGesture = new GesturePreference();
    private GesturePreference mLetterEGesture = new GesturePreference();
    private GesturePreference mLetterSGesture = new GesturePreference();
    private GesturePreference mLetterVGesture = new GesturePreference();
    private GesturePreference mLetterWGesture = new GesturePreference();
    private GesturePreference mLetterZGesture = new GesturePreference();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gestures_settings);

        mContext = getActivity();
        mPrefScreen = getPreferenceScreen();
        mManager = CMHardwareManager.getInstance(getActivity());

        mFlipToMuitePref = (SwitchPreference) findPreference(KEY_FLIP_TO_MUTE);
        mCircleGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_CIRCLE);
        mTwoFingersDownGesture.preference =
                (SwitchPreference) findPreference(KEY_GESTURE_TWO_FINGERS_DOWN);
        mLeftwardsArrowGesture.preference =
                (SwitchPreference) findPreference(KEY_GESTURE_LEFTWARDS_ARROW);
        mRightWardsArrowGesture.preference =
                (SwitchPreference) findPreference(KEY_GESTURE_RIGHTWARDS_ARROW);
        mLetterCGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_LETTER_C);
        mLetterEGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_LETTER_E);
        mLetterSGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_LETTER_S);
        mLetterVGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_LETTER_V);
        mLetterWGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_LETTER_W);
        mLetterZGesture.preference = (SwitchPreference) findPreference(KEY_GESTURE_LETTER_Z);

        // Check supported touchscreen gestures
        if (mManager.isSupported("FEATURE_TOUCHSCREEN_GESTURES")) {
            mSupportedGestures = mManager.getTouchscreenGestures();
            for (TouchscreenGesture mGesture : mSupportedGestures) {
                Log.d("OHAI", mGesture.path);
                switch (mGesture.id) {
                    case TouchscreenGesture.ID_CIRCLE:
                        mCircleGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_TWO_FINGERS_DOWNWARDS:
                        mTwoFingersDownGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LEFTWARDS_ARROW:
                        mLeftwardsArrowGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_RIGHTWARDS_ARROW:
                        mRightWardsArrowGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LETTER_C:
                        mLetterCGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LETTER_E:
                        mLetterEGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LETTER_S:
                        mLetterSGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LETTER_V:
                        mLetterVGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LETTER_W:
                        mLetterWGesture.gesture = mGesture;
                        break;
                    case TouchscreenGesture.ID_LETTER_Z:
                        mLetterZGesture.gesture = mGesture;
                        break;
                }
            }
        } else {
            // TODO: hide prefcategory
        }

        mFlipToMuitePref.setOnPreferenceChangeListener(this);

        if (mCircleGesture.gesture != null) {
            mCircleGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mCircleGesture.preference);
        }

        if (mTwoFingersDownGesture.gesture != null) {
            mTwoFingersDownGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mTwoFingersDownGesture.preference);
        }

        if (mLeftwardsArrowGesture.gesture != null) {
            mLeftwardsArrowGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLeftwardsArrowGesture.preference);
        }

        if (mRightWardsArrowGesture.gesture != null) {
            mRightWardsArrowGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mRightWardsArrowGesture.preference);
        }

        if (mLetterCGesture.gesture != null) {
            mLetterCGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLetterCGesture.preference);
        }

        if (mLetterEGesture.gesture != null) {
            mLetterEGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLetterEGesture.preference);
        }

        if (mLetterSGesture.gesture != null) {
            mLetterSGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLetterSGesture.preference);
        }

        if (mLetterVGesture.gesture != null) {
            mLetterVGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLetterVGesture.preference);
        }

        if (mLetterWGesture.gesture != null) {
            mLetterWGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLetterWGesture.preference);
        }

        if (mLetterZGesture.gesture != null) {
            mLetterZGesture.preference.setOnPreferenceChangeListener(this);
        } else {
            mPrefScreen.removePreference(mLetterZGesture.preference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isToggledOn = (Boolean) newValue;

        if (preference == mFlipToMuitePref) {
            int mValue = isToggledOn ?
                    CMSettings.Secure.MOTION_BEHAVIOR_FLIP_TO_MUTE_INCOMING_CALL :
                    CMSettings.Secure.MOTION_BEHAVIOR_NOTHING;
            CMSettings.Secure.putInt(mContext.getContentResolver(),
                    CMSettings.Secure.MOTION_BEHAVIOR, mValue);
        } else if (preference == mCircleGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mCircleGesture.gesture, isToggledOn);
        } else if (preference == mTwoFingersDownGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mTwoFingersDownGesture.gesture,
                    isToggledOn);
        } else if (preference == mLeftwardsArrowGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLeftwardsArrowGesture.gesture,
                    isToggledOn);
        } else if (preference == mRightWardsArrowGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mRightWardsArrowGesture.gesture,
                    isToggledOn);
        } else if (preference == mLetterCGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLetterCGesture.gesture, isToggledOn);
        } else if (preference == mLetterEGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLetterEGesture.gesture, isToggledOn);
        } else if (preference == mLetterSGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLetterSGesture.gesture, isToggledOn);
        } else if (preference == mLetterVGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLetterVGesture.gesture, isToggledOn);
        } else if (preference == mLetterWGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLetterWGesture.gesture, isToggledOn);
        } else if (preference == mLetterZGesture.preference) {
            return mManager.setTouchscreenGestureEnabled(mLetterZGesture.gesture, isToggledOn);
        }

        return true;
    }

    private class GesturePreference {
        private SwitchPreference preference;
        private TouchscreenGesture gesture;

        GesturePreference() {
            gesture = null;
        }
    }
}
