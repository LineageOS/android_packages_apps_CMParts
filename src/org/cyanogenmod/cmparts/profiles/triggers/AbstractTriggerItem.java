/*
 * Copyright (C) 2014 The CyanogenMod Project
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
package org.cyanogenmod.cmparts.profiles.triggers;

import cyanogenmod.app.Profile;

public class AbstractTriggerItem {
    private int mIcon;
    private String mSummary;
    private String mTitle;

    private int mTriggerState = Profile.TriggerState.DISABLED;

    public void setTriggerState(int trigger) {
        mTriggerState = trigger;
    }

    public int getTriggerState() {
        return mTriggerState;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public int getIcon() {
        return mIcon;
    }
}
