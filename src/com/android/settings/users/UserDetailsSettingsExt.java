package com.android.settings.users;

import android.os.UserManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.android.settings.R;

/**
 * Class for initializing preferences, and attaching listeners for downstream-added settings.
 */
class UserDetailsSettingsExt
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final int SWITCH_USER_PREF_ORDER = -999;
    private static final int SWITCH_USER_PREF_ORDER_AFTER = SWITCH_USER_PREF_ORDER + 1;
    private static final int APP_COPYING_PREF_ORDER = 499;
    private static final int APP_COPYING_PREF_ORDER_BEFORE = APP_COPYING_PREF_ORDER - 1;
    private static final int APP_COPYING_PREF_ORDER_AFTER = APP_COPYING_PREF_ORDER + 1;
    private static final int MAX_PREF_ORDER = 998;

    private final PreferenceFragmentCompat prefFragment;
    private final UserRestrictions userRestrictions;

    // Add preference fields here
    private Preference appInstallsPref;
    private SwitchPreference runInBackgroundPref;
    private SwitchPreference allowConfigPrivateDnsPref;

    UserDetailsSettingsExt(PreferenceFragmentCompat prefFragment, UserRestrictions userRestrictions) {
        this.prefFragment = prefFragment;
        this.userRestrictions = userRestrictions;
        initializePreferences();
    }

    private void initializePreferences() {
        if (userRestrictions.userManager.isSystemUser()) {
            if (!userRestrictions.userInfo.isGuest()) {
                runInBackgroundPref = initializeSwitchPreference(R.string.user_run_in_background_pref,
                        R.string.user_run_in_background_title, R.drawable.ic_sync, null);
            }
            allowConfigPrivateDnsPref = initializeSwitchPreference(R.string.user_config_private_dns_pref,
                    R.string.user_config_private_dns_title, null, SWITCH_USER_PREF_ORDER_AFTER + 1);
            appInstallsPref = initializePreference(R.string.user_app_install_pref,
                    R.string.user_app_install_title, R.drawable.ic_settings_install,  null);
        }
        updatePreferences();
    }

    void updatePreferences() {
        if (appInstallsPref != null) {
            appInstallsPref.setSummary(UserAppsInstallSettings.getDescription(prefFragment.requireContext(), userRestrictions));
        }
        if (runInBackgroundPref != null) {
            runInBackgroundPref.setChecked(!userRestrictions.isSet(UserManager.DISALLOW_RUN_IN_BACKGROUND));
        }
        if (allowConfigPrivateDnsPref != null) {
            allowConfigPrivateDnsPref.setChecked(!userRestrictions.isSet(UserManager.DISALLOW_CONFIG_PRIVATE_DNS));
        }
    }

    private SwitchPreference initializeSwitchPreference(final int keyRes, final int titleRes,
            final Integer iconRes, final Integer order) {
        SwitchPreference switchPref =
                new SwitchPreference(prefFragment.getPreferenceManager().getContext());
        switchPref.setOrder(order != null ? order : SWITCH_USER_PREF_ORDER_AFTER);
        switchPref.setOnPreferenceChangeListener(this);
        initializePreferenceCommon(switchPref, keyRes, titleRes, iconRes);
        return switchPref;
    }

    private Preference initializePreference(final int keyRes, final int titleRes,
            final Integer iconRes, final Integer order) {
        Preference pref =
                new Preference(prefFragment.getPreferenceManager().getContext());
        pref.setOrder(order != null ? order : APP_COPYING_PREF_ORDER_BEFORE);
        pref.setOnPreferenceClickListener(this);
        initializePreferenceCommon(pref, keyRes, titleRes, iconRes);
        return pref;
    }

    private void initializePreferenceCommon(final Preference pref, final int keyRes,
            final int titleRes, final Integer iconRes) {
        pref.setKey(prefFragment.requireActivity().getString(keyRes));
        pref.setTitle(titleRes);
        pref.setPersistent(false);
        pref.setSingleLineTitle(false);
        if (iconRes != null) {
            pref.setIcon(iconRes);
        }
        prefFragment.getPreferenceScreen().addPreference(pref);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object res) {
        if (preference == runInBackgroundPref) {
            userRestrictions.set(UserManager.DISALLOW_RUN_IN_BACKGROUND, !((boolean) res));
            return true;
        }
        if (preference == allowConfigPrivateDnsPref) {
            userRestrictions.set(UserManager.DISALLOW_CONFIG_PRIVATE_DNS, !((boolean) res));
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == appInstallsPref) {
            UserAppsInstallSettings.launch(appInstallsPref, userRestrictions.userInfo.id);
            return true;
        }
        return false;
    }
}
