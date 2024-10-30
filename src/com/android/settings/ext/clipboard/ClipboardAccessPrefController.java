package com.android.settings.ext.clipboard;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserManager;

import com.android.settings.core.BasePreferenceController;

import java.util.Objects;

public class ClipboardAccessPrefController extends BasePreferenceController {

    public ClipboardAccessPrefController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        Context ctx = mContext;
        UserManager userManager = Objects.requireNonNull(ctx.getSystemService(UserManager.class));
        UserInfo userInfo = userManager.getUserInfo(ctx.getUserId());
        if (userInfo == null) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        return AVAILABLE;
    }
}
