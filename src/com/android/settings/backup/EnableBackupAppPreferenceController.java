package com.android.settings.backup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

public class EnableBackupAppPreferenceController extends TogglePreferenceController {
    private final PackageManager packageManager;
    private final String backupPkgName;
    private final Context ctx;

    public EnableBackupAppPreferenceController(Context context, String key) {
        super(context, key);
        packageManager = context.getPackageManager();
        BackupSettingsHelper settingsHelper = new BackupSettingsHelper(context);
        Intent backupAppIntent = settingsHelper.getIntentForBackupSettings();
        backupPkgName = backupAppIntent.getComponent().getPackageName();
        ctx = context;
    }

    @Override
    public boolean isChecked() {
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
    public int getAvailabilityStatus() {
        if (backupPkgName == null) {
            return DISABLED_FOR_USER;
        }
        return AVAILABLE;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        try {
            setBackupAppAndSettingsEnabled(isChecked);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setBackupAppAndSettingsEnabled(boolean shouldEnable) {
        final int userId = ctx.getUserId();
        final int state = shouldEnable ? COMPONENT_ENABLED_STATE_ENABLED
                                 : COMPONENT_ENABLED_STATE_DISABLED;
        ComponentName backupComponent = new ComponentName(ctx,
                UserBackupSettingsActivity.class.getName());
        packageManager.setApplicationEnabledSetting(backupPkgName, state, userId);
        packageManager.setComponentEnabledSetting(backupComponent, state,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }
}
