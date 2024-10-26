package com.android.settings.privatespace.users;

import static com.android.settings.dashboard.DashboardFragment.CATEGORY;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;

public class PrivateSpaceUserRestrictionsPrefController extends BasePreferenceController {
    private static final String TAG = "PrivateSpaceUserRestrictionsPrefCtrl";

    private final PrivateSpacePrefControllerHelper prefCtrlHelper;

    public PrivateSpaceUserRestrictionsPrefController(@NonNull Context context, @NonNull String preferenceKey) {
        super(context, preferenceKey);
        prefCtrlHelper = PrivateSpacePrefControllerHelper.createInstance(context);
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
        updatePreferenceVisibilityDelegate(preference, isAvailable());
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!prefCtrlHelper.hasPrivateSpaceUserId()) {
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
