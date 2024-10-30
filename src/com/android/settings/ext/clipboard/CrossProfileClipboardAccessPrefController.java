package com.android.settings.ext.clipboard;

import android.content.Context;

public class CrossProfileClipboardAccessPrefController extends
        BaseCrossProfileClipboardAccessPrefController {

    public CrossProfileClipboardAccessPrefController(Context ctx, String key) {
        super(ctx, key, ctx.getUserId());
    }

    @Override
    public int getAvailabilityStatus() {
        int baselineAvailability = getBaselineAvailabilityStatus();
        if (baselineAvailability != AVAILABLE) {
            return baselineAvailability;
        }

        // Only expose this setting for users that can have profiles.
        if (!userRestrictions.userInfo.canHaveProfile()) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        return AVAILABLE;
    }
}
