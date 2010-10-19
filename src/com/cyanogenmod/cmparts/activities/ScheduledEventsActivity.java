/*
 * Copyright (C) 2006 The Android Open Source Project
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

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

/**
 * Phone goggles allows the user to indicate that he wants his communications to
 * be filtered. When filtering communications, phone goggles will ask for a
 * confirmation before performing a professional communication (or simply cancel
 * it) during a given period of the day. This should allow the user to avoid any
 * inconveniance like calling his boss in the middle of a party...
 *
 * 'Professional communications' refers to any communication where the phone
 * number is in the ContactProvider and it type is setted as TYPE_WORK,
 * TYPE_WORK_MOBILE or TYPE_WORK_PAGER.
 *
 */
public class ScheduledEventsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_scheduled_events);
        addPreferencesFromResource(R.xml.scheduled_events_settings);
    }
}
