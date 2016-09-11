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

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.cyanogenmod.cmparts.profiles.NFCProfileTagCallback;
import org.cyanogenmod.internal.cmparts.IPartsCatalog;
import org.cyanogenmod.internal.cmparts.PartInfo;

public class PartsActivity extends Activity {

    private static final String TAG = "PartsActivity";

    public static final String EXTRA_PART = "part";
    public static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    public static final String ACTION_PART = "org.cyanogenmod.cmparts.PART";

    private IPartsCatalog mCatalog;

    private NFCProfileTagCallback mNfcProfileCallback;

    private ActionBar mActionBar;
    private SwitchBar mSwitchBar;

    private int mMainContentId = R.id.main_content;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.cmparts);

        connectCatalog();

        Log.d(TAG, "Launched with: " + getIntent().toString() + " action: " +
                getIntent().getAction() + " component: " + getIntent().getComponent().getClassName() +
                " extras: " + getIntent().getExtras().toString());

        PartInfo info = null;
        String extra = getIntent().getStringExtra(EXTRA_PART);
        if (ACTION_PART.equals(getIntent().getAction()) && extra != null) {
            info = PartsCatalog.getPartInfo(getResources(), extra);
        } else {
            info = PartsCatalog.getPartInfoForClass(getResources(),
                    getIntent().getComponent().getClassName());
        }

        if (info == null) {
            throw new UnsupportedOperationException("Unable to get part: " + getIntent().toString());
        }

        mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }
        mSwitchBar = (SwitchBar) findViewById(R.id.switch_bar);

        startPreferencePanel(info.getFragmentClass(), null, info.getTitle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectCatalog();
    }

    private void connectCatalog() {
        Intent i = new Intent(this, PartsCatalog.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void disconnectCatalog() {
        unbindService(mConnection);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mCatalog = IPartsCatalog.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCatalog = null;
        }
    };

    public void setNfcProfileCallback(NFCProfileTagCallback callback) {
        mNfcProfileCallback = callback;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (mNfcProfileCallback != null) {
                mNfcProfileCallback.onTagRead(detectedTag);
            }
            return;
        }
        super.onNewIntent(intent);
    }

    public SwitchBar getSwitchBar() {
        return mSwitchBar;
    }

    public ActionBar getActionBar() {
        return mActionBar;
    }

    public void startPreferencePanel(String fragmentClass, Bundle args, String title) {
        Fragment fragment = Fragment.instantiate(this, fragmentClass);
        if (fragment == null) {
            Log.e(TAG, "Invalid fragment! " + fragmentClass);
            return;
        }

        Log.d(TAG, "Launching fragment: " + fragmentClass);

        if (title != null) {
            mActionBar.setTitle(title);
        }

        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();

    }

}

