/*
 * Copyright (C) 2017 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.settings.backup;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.PrimarySwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;

public class BackupSettingsPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private static final String BACKUP_SETTINGS = "backup_settings";
    private static final String MANUFACTURER_SETTINGS = "manufacturer_backup";

    private final PackageManager packageManager;
    private final String backupPkgName;
    private final int userId;

    private PrimarySwitchPreference mBackupSettings;
    private Preference mManufacturerSettings;
    private Intent mBackupSettingsIntent;
    private CharSequence mBackupSettingsTitle;
    private String mBackupSettingsSummary;
    private Intent mManufacturerIntent;
    private String mManufacturerLabel;

    public BackupSettingsPreferenceController(Context context) {
        super(context);
        BackupSettingsHelper settingsHelper = new BackupSettingsHelper(context);
        mBackupSettingsIntent = settingsHelper.getIntentForBackupSettings();
        mBackupSettingsTitle = settingsHelper.getLabelForBackupSettings();
        mBackupSettingsSummary = settingsHelper.getSummaryForBackupSettings();
        mManufacturerIntent = settingsHelper.getIntentProvidedByManufacturer();
        mManufacturerLabel = settingsHelper.getLabelProvidedByManufacturer();
        packageManager = context.getPackageManager();
        backupPkgName = mBackupSettingsIntent.getComponent().getPackageName();
	userId = context.getUserId();
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mBackupSettings = screen.findPreference(BACKUP_SETTINGS);
        mBackupSettings.setChecked(isBackupAppEnabled());
        mManufacturerSettings = screen.findPreference(MANUFACTURER_SETTINGS);
        mBackupSettings.setIntent(mBackupSettingsIntent);
        mBackupSettings.setTitle(mBackupSettingsTitle);
        mBackupSettings.setSummary(mBackupSettingsSummary);
        mManufacturerSettings.setIntent(mManufacturerIntent);
        mManufacturerSettings.setTitle(mManufacturerLabel);
    }

    private boolean isBackupAppEnabled() {
        try {
            int state = packageManager.getApplicationEnabledSetting(backupPkgName);
            if (state != COMPONENT_ENABLED_STATE_ENABLED) {
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String key = preference.getKey();
        if (BACKUP_SETTINGS.equals(key)) {
            boolean newChecked = !isBackupAppEnabled();
            int state = newChecked ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
            try {
                packageManager.setApplicationEnabledSetting(backupPkgName, state, userId);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Returns true if preference is available (should be displayed)
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Returns the key for this preference.
     */
    @Override
    public String getPreferenceKey() {
        return null;
    }
}
