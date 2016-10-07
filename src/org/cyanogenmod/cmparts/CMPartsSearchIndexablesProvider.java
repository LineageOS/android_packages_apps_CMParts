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

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexablesProvider;

import org.cyanogenmod.internal.cmparts.PartInfo;
import org.cyanogenmod.internal.cmparts.PartsList;
import org.cyanogenmod.platform.internal.R;

import java.util.Set;

import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_ICON_RESID;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_INTENT_ACTION;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_INTENT_TARGET_CLASS;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_INTENT_TARGET_PACKAGE;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_KEY;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_KEYWORDS;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_RANK;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_SUMMARY_ON;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_TITLE;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_RAW_USER_ID;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_CLASS_NAME;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_ICON_RESID;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_ACTION;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RANK;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RESID;
import static android.provider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS;
import static android.provider.SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS;
import static android.provider.SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS;

/**
 * Provides search metadata to the Settings app
 */
public class CMPartsSearchIndexablesProvider extends SearchIndexablesProvider {

    @Override
    public Cursor queryXmlResources(String[] strings) {
        MatrixCursor cursor = new MatrixCursor(INDEXABLES_XML_RES_COLUMNS);
        final Set<String> keys = PartsList.getPartsList(getContext());

        // return all of the xml resources listed in the resource: attribute
        // from parts_catalog.xml for indexing
        for (String key : keys) {
            PartInfo i = PartsList.getPartInfo(getContext(), key);
            if (i == null || i.getResource() == 0) {
                continue;
            }

            Object[] ref = new Object[7];
            ref[COLUMN_INDEX_XML_RES_RANK] = 2;
            ref[COLUMN_INDEX_XML_RES_RESID] = i.getResource();
            ref[COLUMN_INDEX_XML_RES_CLASS_NAME] = null;
            ref[COLUMN_INDEX_XML_RES_ICON_RESID] = R.drawable.ic_launcher_cyanogenmod;
            ref[COLUMN_INDEX_XML_RES_INTENT_ACTION] = PartsList.ACTION_PART;
            ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] = "org.cyanogenmod.cmparts";
            ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = PartsActivity.class.getName();
            cursor.addRow(ref);
        }
        return cursor;

    }

    @Override
    public Cursor queryRawData(String[] strings) {
        MatrixCursor cursor = new MatrixCursor(INDEXABLES_RAW_COLUMNS);
        final Set<String> keys = PartsList.getPartsList(getContext());

        // we also submit keywords and metadata for all top-level items
        // which don't have an associated XML resource
        for (String key : keys) {
            PartInfo i = PartsList.getPartInfo(getContext(), key);
            if (i == null || i.getResource() > 0) {
                continue;
            }
            Object[] ref = new Object[14];
            ref[COLUMN_INDEX_RAW_RANK] = 2;
            ref[COLUMN_INDEX_RAW_TITLE] = i.getTitle();
            ref[COLUMN_INDEX_RAW_SUMMARY_ON] = i.getSummary();
            ref[COLUMN_INDEX_RAW_KEYWORDS] = i.getKeywords();
            ref[COLUMN_INDEX_RAW_ICON_RESID] = i.getIconRes() > 0 ? i.getIconRes() :
                    R.drawable.ic_launcher_cyanogenmod;
            ref[COLUMN_INDEX_RAW_INTENT_ACTION] = PartsList.ACTION_PART;
            ref[COLUMN_INDEX_RAW_INTENT_TARGET_PACKAGE] = "org.cyanogenmod.cmparts";
            ref[COLUMN_INDEX_RAW_INTENT_TARGET_CLASS] = PartsActivity.class.getName();
            ref[COLUMN_INDEX_RAW_KEY] = i.getName();
            ref[COLUMN_INDEX_RAW_USER_ID] = -1;
            cursor.addRow(ref);
        }
        return cursor;
    }

    @Override
    public Cursor queryNonIndexableKeys(String[] strings) {
        MatrixCursor cursor = new MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS);
        return cursor;
    }

    @Override
    public boolean onCreate() {
        return true;
    }
}
