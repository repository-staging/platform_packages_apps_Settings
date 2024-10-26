package com.android.settings.privatespace.users;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.ext.RadioButtonPickerFragment2;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.users.UserAppsInstallSettingsPrefController;

public class PrivateSpaceAppInstallRestrictionPrefController extends UserAppsInstallSettingsPrefController {

    private final PrivateSpacePrefControllerHelper prefCtrlHelper;

    public PrivateSpaceAppInstallRestrictionPrefController(Context ctx, String key) {
        super(ctx, key);
        prefCtrlHelper = PrivateSpacePrefControllerHelper.createInstance(ctx);
        populateUserRestrictionOrFinish();
    }

    @Override
    public int getAvailabilityStatus() {
        populateUserRestrictionOrFinish();
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

        return super.handlePreferenceTreeClick(preference);
    }

    private void populateUserRestrictionOrFinish() {
        int targetUserId = prefCtrlHelper.getPrivateSpaceUserIdOrUserNull();
        populateUserRestriction(targetUserId);
        finishIfNoUserInfo(fragment);
    }
}
