/*
 * Copyright (C) 2011 The CyanogenMod Project
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

package com.cyanogenmod.cmparts.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

import com.cyanogenmod.cmparts.R;

public class UIActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    /* Preference Screens */
    private static final String NOTIFICATION_SCREEN = "notification_settings";

    private static final String NOTIFICATION_TRACKBALL = "trackball_notifications";

    private static final String EXTRAS_SCREEN = "tweaks_extras";

    private static final String GENERAL_CATEGORY = "general_category";

    private PreferenceScreen mStatusBarScreen;

    private PreferenceScreen mNotificationScreen;

    private PreferenceScreen mTrackballScreen;;

    private PreferenceScreen mExtrasScreen;

    /* Other */
    private static final String BOOTANIMATION_PREF = "pref_bootanimation";

    private static final String BOOTANIMATION_RESET_PREF = "pref_bootanimation_reset";

    private static final String BOOTANIMATION_PREVIEW_PREF = "pref_bootanimation_preview";

    private static final String PINCH_REFLOW_PREF = "pref_pinch_reflow";

    private static final String RENDER_EFFECT_PREF = "pref_render_effect";

    private static final String POWER_PROMPT_PREF = "power_dialog_prompt";

    private static final String OVERSCROLL_PREF = "pref_overscroll_effect";

    private static final String OVERSCROLL_WEIGHT_PREF = "pref_overscroll_weight";

    private CheckBoxPreference mPinchReflowPref;

    private CheckBoxPreference mPowerPromptPref;

    private ListPreference mRenderEffectPref;

    private ListPreference mOverscrollPref;

    private ListPreference mOverscrollWeightPref;

    private PreferenceScreen mBootPref;

    private PreferenceScreen mBootReset;

    private PreferenceScreen mBootPreview;

    private static final int REQUEST_CODE_PICK_FILE = 999;

    private static final int REQUEST_CODE_MOVE_FILE = 1000;

    private static final int REQUEST_CODE_PREVIEW_FILE = 1001;

    private static boolean mBootPreviewRunning;

    private static int prevOrientation;

    private static final String MOVE_BOOT_INTENT = "com.cyanogenmod.cmbootanimation.MOVE_BOOTANIMATION";

    private static final String BOOT_RESET = "com.cyanogenmod.cmbootanimation.RESET_DEFAULT";

    private static final String BOOT_PREVIEW_FILE = "preview_bootanim";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.interface_settings_title_head);
        addPreferencesFromResource(R.xml.ui_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Preference Screens */
        mNotificationScreen = (PreferenceScreen) prefSet.findPreference(NOTIFICATION_SCREEN);
        mTrackballScreen = (PreferenceScreen) prefSet.findPreference(NOTIFICATION_TRACKBALL);
        mExtrasScreen = (PreferenceScreen) prefSet.findPreference(EXTRAS_SCREEN);

        boolean hasLed = getResources().getBoolean(R.bool.has_rgb_notification_led)
                || getResources().getBoolean(R.bool.has_dual_notification_led)
                || getResources().getBoolean(R.bool.has_single_notification_led);

        if (!hasLed) {
            ((PreferenceCategory) prefSet.findPreference(GENERAL_CATEGORY))
                    .removePreference(mTrackballScreen);
        }

        /* Boot Animation Chooser */
        mBootPref = (PreferenceScreen) prefSet.findPreference(BOOTANIMATION_PREF);
        mBootReset = (PreferenceScreen) prefSet.findPreference(BOOTANIMATION_RESET_PREF);
        mBootPreview = (PreferenceScreen) prefSet.findPreference(BOOTANIMATION_PREVIEW_PREF);

        /* Pinch reflow */
        mPinchReflowPref = (CheckBoxPreference) prefSet.findPreference(PINCH_REFLOW_PREF);
        mPinchReflowPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.WEB_VIEW_PINCH_REFLOW, 0) == 1);

        mPowerPromptPref = (CheckBoxPreference) prefSet.findPreference(POWER_PROMPT_PREF);
        mRenderEffectPref = (ListPreference) prefSet.findPreference(RENDER_EFFECT_PREF);
        mRenderEffectPref.setOnPreferenceChangeListener(this);
        updateFlingerOptions();

        /* Overscroll Effect */
        mOverscrollPref = (ListPreference) prefSet.findPreference(OVERSCROLL_PREF);
        int overscrollEffect = Settings.System.getInt(getContentResolver(),
                Settings.System.OVERSCROLL_EFFECT, 1);
        mOverscrollPref.setValue(String.valueOf(overscrollEffect));
        mOverscrollPref.setOnPreferenceChangeListener(this);

        mOverscrollWeightPref = (ListPreference) prefSet.findPreference(OVERSCROLL_WEIGHT_PREF);
        int overscrollWeight = Settings.System.getInt(getContentResolver(),
                Settings.System.OVERSCROLL_WEIGHT, 5);
        mOverscrollWeightPref.setValue(String.valueOf(overscrollWeight));
        mOverscrollWeightPref.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mStatusBarScreen) {
            startActivity(mStatusBarScreen.getIntent());
            return true;
        } else if (preference == mNotificationScreen) {
            startActivity(mNotificationScreen.getIntent());
            return true;
        } else if (preference == mTrackballScreen) {
            startActivity(mTrackballScreen.getIntent());
            return true;
        } else if (preference == mExtrasScreen) {
            startActivity(mExtrasScreen.getIntent());
            return true;
        } else if (preference == mPinchReflowPref) {
            value = mPinchReflowPref.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.WEB_VIEW_PINCH_REFLOW,
                    value ? 1 : 0);
            return true;
        } else if (preference == mPowerPromptPref) {
            value = mPowerPromptPref.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.POWER_DIALOG_PROMPT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mBootPref) {
            Intent intent = new Intent("org.openintents.action.PICK_FILE");
            intent.setData(Uri.parse("file:///sdcard/"));
            intent.putExtra("org.openintents.extra.TITLE", "Please select a file");
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
            return true;
        } else if (preference == mBootReset) {
            Intent intent = new Intent(BOOT_RESET);
            sendBroadcast(intent);
            return true;
        }  else if (preference == mBootPreview) {
            Intent intent = new Intent("org.openintents.action.PICK_FILE");
            intent.setData(Uri.parse("file:///sdcard/"));
            intent.putExtra("org.openintents.extra.TITLE", "Please select a file to preview");
            startActivityForResult(intent, REQUEST_CODE_PREVIEW_FILE);
            return true;
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRenderEffectPref) {
            writeRenderEffect(Integer.valueOf((String) newValue));
            return true;
        } else if (preference == mOverscrollPref) {
            int overscrollEffect = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_EFFECT,
                    overscrollEffect);
            return true;
        } else if (preference == mOverscrollWeightPref) {
            int overscrollWeight = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT,
                    overscrollWeight);
            return true;
        }
        return false;
    }

    // Taken from DevelopmentSettings
    private void updateFlingerOptions() {
        // magic communication with surface flinger.
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                flinger.transact(1010, data, reply, 0);
                int v;
                v = reply.readInt();
                // mShowCpuCB.setChecked(v != 0);
                v = reply.readInt();
                // mEnableGLCB.setChecked(v != 0);
                v = reply.readInt();
                // mShowUpdatesCB.setChecked(v != 0);
                v = reply.readInt();
                // mShowBackgroundCB.setChecked(v != 0);

                v = reply.readInt();
                mRenderEffectPref.setValue(String.valueOf(v));

                reply.recycle();
                data.recycle();
            }
        } catch (RemoteException ex) {
        }

    }

    private void writeRenderEffect(int id) {
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                data.writeInt(id);
                flinger.transact(1014, data, null, 0);
                data.recycle();
            }
        } catch (RemoteException ex) {
        }
    }

    ColorPickerDialog.OnColorChangedListener mWidgetColorListener = new ColorPickerDialog.OnColorChangedListener() {
        public void colorChanged(int color) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET_COLOR, color);
        }

        public void colorUpdate(int color) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context = getApplicationContext();
        switch (requestCode) {
            case REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK && data != null) {
                    // obtain the filename
                    Uri fileUri = data.getData();
                    if (fileUri != null) {
                        String filePath = fileUri.getPath();
                        if (filePath != null) {
                            Intent mvBootIntent = new Intent();
                            mvBootIntent.setAction(MOVE_BOOT_INTENT);
                            mvBootIntent.putExtra("fileName", filePath);
                            sendBroadcast(mvBootIntent);
                        }
                    }
                }
            break;
            case REQUEST_CODE_PREVIEW_FILE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri fileUri = data.getData();
                    if (fileUri != null) {
                        String filePath = fileUri.getPath();
                        if (filePath != null) {
                            try {
                                FileOutputStream outfile = context.openFileOutput(BOOT_PREVIEW_FILE, Context.MODE_WORLD_READABLE);
                                outfile.write(filePath.getBytes());
                                outfile.close();
                            } catch (Exception e) { }
                            mBootPreviewRunning = true;
                            prevOrientation = getRequestedOrientation();
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            SystemProperties.set("ctl.start", "bootanim");
                        }
                    }
                }
            break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        if(mBootPreviewRunning) {
	    File rmFile = new File(context.getFilesDir(), BOOT_PREVIEW_FILE);
            if (rmFile.exists()) {
                try {
                    rmFile.delete();
                } catch (Exception e) { }
            }
            setRequestedOrientation(prevOrientation);
            SystemProperties.set("ctl.stop", "bootanim");
            mBootPreviewRunning = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        Context context = getApplicationContext();
        if (keyCode == KeyEvent.KEYCODE_BACK && mBootPreviewRunning) {
            File rmFile = new File(context.getFilesDir(), BOOT_PREVIEW_FILE);
            if (rmFile.exists()) {
                try {
                    rmFile.delete();
                } catch (Exception e) { }
            }
            SystemProperties.set("ctl.stop", "bootanim");
            mBootPreviewRunning = false;
            setRequestedOrientation(prevOrientation);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
