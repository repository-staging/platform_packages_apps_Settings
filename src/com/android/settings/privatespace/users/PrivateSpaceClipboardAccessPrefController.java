package com.android.settings.privatespace.users;

import static com.android.settings.dashboard.DashboardFragment.CATEGORY;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.users.UserRestrictions;

public class PrivateSpaceClipboardAccessPrefController extends BasePreferenceController {

    private final PrivateSpacePrefControllerHelper prefCtrlHelper;

    public PrivateSpaceClipboardAccessPrefController(Context ctx, String key) {
        super(ctx, key);
        prefCtrlHelper = PrivateSpacePrefControllerHelper.createInstance(ctx);
    }

    @Override
    public int getAvailabilityStatus() {
        int baselineAvailability = prefCtrlHelper.getBaselineAvailability();
        if (baselineAvailability != AVAILABLE) {
            return baselineAvailability;
        }

        return AVAILABLE;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }

        final UserRestrictions privateSpaceUserRestrictions =
                prefCtrlHelper.createUserRestrictionsInstanceForPrivateSpaceUser();
        final UserInfo privateSpaceUserInfo = privateSpaceUserRestrictions.userInfo;
        if (privateSpaceUserInfo == null) {
            return false;
        }

        if (prefCtrlHelper.isPrivateSpaceLocked()) {
            return false;
        }

        final Bundle extras = preference.getExtras();

        new SubSettingLauncher(preference.getContext())
                .setDestination(preference.getFragment())
                .setSourceMetricsCategory(extras.getInt(CATEGORY, SettingsEnums.PAGE_UNKNOWN))
                .setArguments(extras)
                .launch();
        return true;
    }
}