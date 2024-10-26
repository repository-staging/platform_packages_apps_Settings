package com.android.settings.privatespace.users;

import static com.android.settings.dashboard.DashboardFragment.CATEGORY;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.users.AppCopyFragment;
import com.android.settings.users.AppCopyFragmentHelperExt;
import com.android.settings.users.UserRestrictions;

public class PrivateSpaceAppCopyPrefController extends BasePreferenceController {

    private static final String  TAG = "PrivateSpaceAppCopyPrefCtrl";

    private final PrivateSpacePrefControllerHelper prefCtrlHelper;

    public PrivateSpaceAppCopyPrefController(Context ctx, String key) {
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
    public void updateState(Preference preference) {
        super.updateState(preference);
        UserRestrictions userRestrictions = prefCtrlHelper.createUserRestrictionsInstanceForPrivateSpaceUser();
        preference.setEnabled(!userRestrictions.isSet(UserManager.DISALLOW_INSTALL_APPS));
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        final UserRestrictions privateSpaceUserRestrictions =
                prefCtrlHelper.createUserRestrictionsInstanceForPrivateSpaceUser();
        final UserInfo privateSpaceUserInfo = privateSpaceUserRestrictions.userInfo;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }

        if (privateSpaceUserInfo == null) {
            return false;
        }

        if (prefCtrlHelper.isPrivateSpaceLocked()) {
            return false;
        }

        String destName = preference.getFragment();
        if (destName == null || destName.isEmpty()) {
            return false;
        }

        boolean forPrivateSpace = true;
        boolean showUserAppsOnly = true;
        final Bundle args = AppCopyFragmentHelperExt.appendArgs(preference.getExtras(),
                privateSpaceUserInfo.id, forPrivateSpace, showUserAppsOnly);

        new SubSettingLauncher(preference.getContext())
                .setDestination(destName)
                .setSourceMetricsCategory(args.getInt(CATEGORY, SettingsEnums.PAGE_UNKNOWN))
                .setArguments(args)
                .launch();
        return true;
    }
}
