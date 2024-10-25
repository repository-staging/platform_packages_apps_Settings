package com.android.settings.users;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.ext.AbstractListPreferenceController;
import com.android.settings.ext.RadioButtonPickerFragment2;
import com.android.settingslib.widget.CandidateInfo;

import java.util.ArrayList;

public class UserAppsInstallSettingsPrefController extends AbstractListPreferenceController {

    private static final int UNKNOWN = -1;
    private static final int INSTALL_ENABLED = 0;
    private static final int INSTALL_FIRST_PARTY_ENABLED = 1;
    private static final int INSTALL_DISABLED = 2;

    static void launchSettings(Context context, Preference preference, int userId) {
        preference.getExtras().putInt(AppCopyFragment.EXTRA_USER_ID, userId);
        new UserAppsInstallSettingsPrefController(context, preference.getKey())
                .handlePreferenceTreeClick(preference);
    }

    protected UserRestrictions userRestrictions;

    public UserAppsInstallSettingsPrefController(Context ctx, String key) {
        super(ctx, key);
    }

    @Override
    public int getAvailabilityStatus() {
        populateUserRestriction(UserHandle.USER_NULL);
        if (userRestrictions == null) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        if (userRestrictions.userInfo == null) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        return AVAILABLE;
    }

    @Override
    public void updateState(Preference p) {
        super.updateState(p);
    }

    @Override
    public void addPrefsBeforeList(RadioButtonPickerFragment2 fragment, PreferenceScreen screen) {
        finishIfNoUserInfo(fragment);
    }

    protected void populateUserRestriction(int fallbackUserId) {
        UserManager um = mContext.getSystemService(UserManager.class);
        if (fragment != null && fragment.getArguments() != null
                && (userRestrictions == null
                        || userRestrictions.userInfo == null
                        || fallbackUserId != UserHandle.USER_NULL
                        || userRestrictions.userInfo.id != fallbackUserId)) {
            int targetUserId = fragment.requireArguments().getInt(
                    AppCopyFragment.EXTRA_USER_ID, fallbackUserId);
            userRestrictions = new UserRestrictions(um, um.getUserInfo(targetUserId));
        } else {
            userRestrictions = new UserRestrictions(um, um.getUserInfo(fallbackUserId));
        }
    }

    protected void finishIfNoUserInfo(RadioButtonPickerFragment2 fragment) {
        if (userRestrictions.userInfo == null && fragment != null) {
            fragment.finish();
        }
    }

    @Override
    protected void getEntries(Entries entries) {
        if (userRestrictions == null) {
            populateUserRestriction(UserHandle.USER_NULL);
        }

        UserInfo userInfo = userRestrictions.userInfo;
        if (userInfo == null) {
            return;
        }

        if (!userInfo.isGuest()) {
            entries.add(R.string.user_app_install_enabled,
                    R.string.user_app_install_enabled_desc,
                    INSTALL_ENABLED);
        }

        entries.add(R.string.user_app_install_enabled_first_party_sources,
                R.string.user_app_install_enabled_first_party_sources_desc,
                INSTALL_FIRST_PARTY_ENABLED);
        entries.add(R.string.user_app_install_disabled,
                R.string.user_app_install_disabled_desc,
                INSTALL_DISABLED);
    }

    @Override
    protected final int getCurrentValue() {
        if (userRestrictions.userInfo == null) {
            return UNKNOWN;
        }

        final boolean isInstallDisallowed =
                userRestrictions.isSet(UserManager.DISALLOW_INSTALL_APPS);
        final boolean isInstallDisallowedForUnknownSources =
                userRestrictions.isSet(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
        if (isInstallDisallowed) {
            if (isInstallDisallowedForUnknownSources) {
                return INSTALL_DISABLED;
            }

            return UNKNOWN;
        }

        if (isInstallDisallowedForUnknownSources) {
            return INSTALL_FIRST_PARTY_ENABLED;
        }

        return INSTALL_ENABLED;
    }

    @Override
    protected final boolean setValue(int installEnabledValue) {
        if (userRestrictions.userInfo == null) {
            return false;
        }

        switch (installEnabledValue) {
            case INSTALL_ENABLED -> {
                userRestrictions.set(UserManager.DISALLOW_INSTALL_APPS, false);
                userRestrictions.set(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, false);
                return true;
            }
            case INSTALL_FIRST_PARTY_ENABLED ->  {
                userRestrictions.set(UserManager.DISALLOW_INSTALL_APPS, false);
                userRestrictions.set(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, true);
                return true;
            }
            case INSTALL_DISABLED -> {
                userRestrictions.set(UserManager.DISALLOW_INSTALL_APPS, true);
                userRestrictions.set(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, true);
                return true;
            }
        }

        return false;
    }
}
