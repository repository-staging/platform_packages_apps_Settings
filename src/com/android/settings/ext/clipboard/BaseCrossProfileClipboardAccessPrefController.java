package com.android.settings.ext.clipboard;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;

import com.android.settings.ext.AbstractTogglePrefController;
import com.android.settings.users.UserRestrictions;

import java.util.Objects;

public class BaseCrossProfileClipboardAccessPrefController extends AbstractTogglePrefController {

    protected final UserRestrictions userRestrictions;

    public BaseCrossProfileClipboardAccessPrefController(Context ctx, String key, int userId) {
        super(ctx, key);
        UserManager userManager = Objects.requireNonNull(ctx.getSystemService(UserManager.class));
        userRestrictions = new UserRestrictions(userManager, userManager.getUserInfo(userId));
    }

    @Override
    public final boolean isChecked() {
        return !userRestrictions.isSet(UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE);
    }

    @Override
    public final boolean setChecked(boolean isChecked) {
        userRestrictions.set(UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE, !isChecked);
        return true;
    }

    @Override
    public int getAvailabilityStatus() {
        return getBaselineAvailabilityStatus();
    }

    protected final int getBaselineAvailabilityStatus() {
        UserInfo userInfo = userRestrictions.userInfo;
        if (userInfo == null) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        if (userInfo.id == UserHandle.USER_NULL) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        return AVAILABLE;
    }
}
