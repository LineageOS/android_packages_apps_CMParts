/*
 * Copyright (C) 2016 The CyanogenMod Project
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
package org.cyanogenmod.cmparts;

import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import org.cyanogenmod.internal.cmparts.PartInfo;

public class PartsPreference extends Preference {

    public PartsPreference(Context context, AttributeSet attrs) {
        super(context, attrs, com.android.internal.R.attr.preferenceScreenStyle);

        Intent i = new Intent(PartsActivity.ACTION_PART);
        i.putExtra(PartsActivity.EXTRA_PART, getKey());
        setIntent(i);

        PartInfo info = PartsCatalog.getPartInfo(getContext().getResources(), getKey());
        if (info == null) {
            throw new RuntimeException("Part " + getKey() + " not found in catalog!");
        }

        setTitle(info.getTitle());
        setSummary(info.getSummary());
    }
}
