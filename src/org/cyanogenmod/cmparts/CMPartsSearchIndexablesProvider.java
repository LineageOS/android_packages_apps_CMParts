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
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesProvider;

import org.cyanogenmod.internal.cmparts.PartInfo;
import org.cyanogenmod.internal.cmparts.PartsList;

import java.util.ArrayList;
import java.util.Set;

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

    private final ArrayList<SearchIndexableResource> sIndexables = new ArrayList<>();

    @Override
    public Cursor queryXmlResources(String[] strings) {
        MatrixCursor cursor = new MatrixCursor(INDEXABLES_XML_RES_COLUMNS);
        final int count = sIndexables.size();
        for (int n = 0; n < count; n++) {
            Object[] ref = new Object[7];
            SearchIndexableResource res = sIndexables.get(n);
            ref[COLUMN_INDEX_XML_RES_RANK] = res.rank;
            ref[COLUMN_INDEX_XML_RES_RESID] = res.xmlResId;
            ref[COLUMN_INDEX_XML_RES_CLASS_NAME] = null;
            ref[COLUMN_INDEX_XML_RES_ICON_RESID] = res.iconResId;
            ref[COLUMN_INDEX_XML_RES_INTENT_ACTION] = "android.intent.action.MAIN";
            ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] = "org.cyanogenmod.cmparts";
            ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = res.className;
            cursor.addRow(ref);
        }
        return cursor;

    }

    @Override
    public Cursor queryRawData(String[] strings) {
        MatrixCursor cursor = new MatrixCursor(INDEXABLES_RAW_COLUMNS);
        return cursor;

    }

    @Override
    public Cursor queryNonIndexableKeys(String[] strings) {
        MatrixCursor cursor = new MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS);
        return cursor;
    }

    @Override
    public boolean onCreate() {
        final Set<String> parts = PartsList.getPartsList(getContext());

        for (String part : parts) {
            final PartInfo partInfo = PartsList.getPartInfo(getContext(), part);
            if (partInfo.getResource() > 0) {
                sIndexables.add(new SearchIndexableResource(1, partInfo.getResource(),
                        partInfo.getFragmentClass(), 0));
            }
        }

        return true;
    }
}
