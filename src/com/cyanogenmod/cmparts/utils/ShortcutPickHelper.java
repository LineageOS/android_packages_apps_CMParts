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

package com.cyanogenmod.cmparts.utils;

import com.cyanogenmod.cmparts.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ShortcutPickHelper {

    private Activity mParent;
    private OnPickListener mListener;

    private static final int REQUEST_PICK_SHORTCUT = 100;
    private static final int REQUEST_PICK_APPLICATION = 101;
    private static final int REQUEST_CREATE_SHORTCUT = 102;

    public interface OnPickListener {
        void shortcutPicked(String uri, String friendlyName, boolean isApplication);
    }

    public ShortcutPickHelper(Activity parent, OnPickListener listener) {
        mParent = parent;
        mListener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_APPLICATION:
                    completeSetCustomApp(data);
                    break;
                case REQUEST_CREATE_SHORTCUT:
                    completeSetCustomShortcut(data);
                    break;
                case REQUEST_PICK_SHORTCUT:
                    processShortcut(data, REQUEST_PICK_APPLICATION, REQUEST_CREATE_SHORTCUT);
                    break;
            }
        }
    }

    public void pickShortcut() {
        Bundle bundle = new Bundle();

        ArrayList<String> shortcutNames = new ArrayList<String>();
        shortcutNames.add(mParent.getString(R.string.group_applications));
        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);

        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
        shortcutIcons.add(ShortcutIconResource.fromContext(mParent, R.drawable.ic_launcher_application));
        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);

        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, mParent.getText(R.string.select_custom_app_title));
        pickIntent.putExtras(bundle);

        mParent.startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
    }

    private void processShortcut(Intent intent, int requestCodeApplication, int requestCodeShortcut) {
        // Handle case where user selected "Applications"
        String applicationName = mParent.getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            mParent.startActivityForResult(pickIntent, requestCodeApplication);
        } else {
            mParent.startActivityForResult(intent, requestCodeShortcut);
        }
    }

    private void completeSetCustomApp(Intent data) {
        mListener.shortcutPicked(data.toUri(0), getFriendlyAppName(data), true);
    }

    private void completeSetCustomShortcut(Intent data) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        /* preserve shortcut name, we want to restore it later */
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME));
        String appUri = intent.toUri(0);
        appUri = appUri.replaceAll("com.android.contacts.action.QUICK_CONTACT", "android.intent.action.VIEW");
        mListener.shortcutPicked(appUri, getFriendlyShortcutName(data), false);
    }

    private String getFriendlyAppName(Intent intent) {
        String friendlyName = null;

        try {
            PackageManager pm = mParent.getPackageManager();
            ComponentName component = intent.getComponent();
            ActivityInfo ai = pm.getActivityInfo(component, PackageManager.GET_META_DATA);

            friendlyName = ai.loadLabel(pm).toString();
            if (friendlyName == null) {
                friendlyName = ai.name;
            }
        } catch (NameNotFoundException e) {
        }

        return friendlyName != null ? friendlyName : intent.toUri(0);
    }

    private String getFriendlyShortcutName(Intent intent) {
        String name = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        return name != null ? name : intent.toUri(0);
    }

    public String getFriendlyNameForUri(String uri) {
        try {
            Intent intent = Intent.parseUri(uri, 0);
            if (Intent.ACTION_MAIN.equals(intent.getAction())) {
                return getFriendlyAppName(intent);
            }
            return getFriendlyShortcutName(intent);
        } catch (URISyntaxException e) {
        }

        return uri;
    }
}
