package com.android.settings.privatespace.users;

import android.content.Context;

import com.android.settings.ext.clipboard.BaseCrossProfileClipboardAccessPrefController;

public class PrivateSpaceCrossProfileClipboardAccessPrefController extends
        BaseCrossProfileClipboardAccessPrefController {

    private final PrivateSpacePrefControllerHelper prefCtrlHelper;

    public PrivateSpaceCrossProfileClipboardAccessPrefController(Context ctx, String key) {
        this(ctx, key, PrivateSpacePrefControllerHelper.createInstance(ctx));
    }

    private PrivateSpaceCrossProfileClipboardAccessPrefController(
            Context ctx, String key, PrivateSpacePrefControllerHelper prefCtrlHelper) {
        super(ctx, key, prefCtrlHelper.getPrivateSpaceUserIdOrUserNull());
        this.prefCtrlHelper = prefCtrlHelper;
    }

    @Override
    public int getAvailabilityStatus() {
        int baseAvailabilityStatus = getBaselineAvailabilityStatus();
        if (baseAvailabilityStatus != AVAILABLE) {
            return baseAvailabilityStatus;
        }

        if (prefCtrlHelper.isPrivateSpaceLocked()) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        return AVAILABLE;
    }
}
