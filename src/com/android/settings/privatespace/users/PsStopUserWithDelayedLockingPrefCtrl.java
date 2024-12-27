package com.android.settings.privatespace.users;

import android.content.Context;
import android.ext.settings.StopUserWithDelayedStorageLockingSetting;
import android.os.UserHandle;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.ext.AbstractTogglePrefController;
import com.android.settings.privatespace.PrivateSpaceMaintainer;

public class PsStopUserWithDelayedLockingPrefCtrl extends AbstractTogglePrefController {

    public PsStopUserWithDelayedLockingPrefCtrl(Context ctx, String key) {
        super(ctx, key);
    }

    @Override
    public boolean isChecked() {
        return StopUserWithDelayedStorageLockingSetting.get(userHandle().getIdentifier());
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return StopUserWithDelayedStorageLockingSetting.set(userHandle().getIdentifier(), isChecked);
    }

    private UserHandle userHandle() {
        return PrivateSpaceMaintainer.getInstance(mContext).getPrivateProfileHandle();
    }

    @Override
    public int getAvailabilityStatus() {
        return userHandle() != null ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }
}
