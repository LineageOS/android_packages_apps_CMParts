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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.cyanogenmod.internal.cmparts.PartInfo;
import org.cyanogenmod.internal.cmparts.PartsList;

import java.lang.reflect.Field;

import static org.cyanogenmod.internal.cmparts.PartsList.ACTION_PART_CHANGED;
import static org.cyanogenmod.internal.cmparts.PartsList.ACTION_REFRESH_PART;
import static org.cyanogenmod.internal.cmparts.PartsList.EXTRA_PART;
import static org.cyanogenmod.internal.cmparts.PartsList.EXTRA_PART_KEY;

/**
 * PartsRefresher keeps remote UI clients up to date with any changes in the
 * state of the Part which should be reflected immediately. For preferences,
 * the clear use case is refreshing the summary.
 *
 * This works in conjunction with CMPartsPreference, which will send an
 * ordered broadcast requesting updated information. The part will be
 * looked up, and checked for a static SUMMARY_INFO field. If an
 * instance of SummaryInfo is found in this field, the result of the
 * broadcast will be updated with the new information.
 *
 * Parts can also call refreshPart to send an asynchronous update to any
 * active remote components via broadcast.
 */
public class PartsRefresher extends BroadcastReceiver {

    private static final String TAG = PartsRefresher.class.getSimpleName();

    public static final String EXTRA_KEY = "key";

    public static final String FIELD_NAME_SUMMARY_INFO = "SUMMARY_INFO";

    public PartsRefresher() {
        super();
    }

    /**
     * Receiver which handles clients requesting a summary update. A client may send
     * the REFERSH_PART action via sendOrderedBroadcast, and we will reply immediately.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_REFRESH_PART.equals(intent.getAction()) && isOrderedBroadcast()) {
            final String key = intent.getStringExtra(EXTRA_KEY);
            if (key != null && updateExtras(context, key, getResultExtras(true))) {
                setResultCode(Activity.RESULT_OK);
                return;
            }
        }
        abortBroadcast();
    }

    private static SummaryProvider.SummaryInfo getPartSummary(PartInfo pi) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(pi.getFragmentClass());
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Cannot find class: " + pi.getFragmentClass());
            return null;
        }

        if (clazz == null || !SummaryProvider.class.isAssignableFrom(clazz)) {
            return null;
        }

        try {
            final Field f = clazz.getField(FIELD_NAME_SUMMARY_INFO);
            return (SummaryProvider.SummaryInfo) f.get(null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static boolean updateExtras(Context context, String key, Bundle bundle) {
        final PartInfo pi = PartsList.getPartInfo(context, key);
        if (pi == null) {
            return false;
        }

        final SummaryProvider.SummaryInfo si = getPartSummary(pi);
        if (si == null) {
            return false;
        }

        String summary = si.getSummary(context, key);
        Log.d(TAG, "updateExtras: part=" + pi.toString() + " summary=" + summary);

        pi.setSummary(si.getSummary(context, key));
        bundle.putString(EXTRA_PART_KEY, key);
        bundle.putParcelable(EXTRA_PART, pi);
        return true;
    }

    public static void refreshPart(Context context, String key) {
        final Intent i = new Intent(ACTION_PART_CHANGED);

        // URI for receivers to filter on
        Uri uri = new Uri.Builder().scheme("cmparts")
                .authority("cyanogenmod").appendPath(key).build();
        i.setData(uri);

        if (updateExtras(context, key, i.getExtras())) {
            context.sendBroadcast(i);
        }
    }
}
