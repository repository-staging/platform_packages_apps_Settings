package com.android.settings.users;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;

public class UserProfileDetailsSettings extends SettingsPreferenceFragment {

    public static void launch(Context ctx, int userId) {
        Bundle args = new Bundle();
        UserManager userManager = ctx.getSystemService(UserManager.class);
        if (userManager == null) {
            return;
        }

        UserInfo userInfo = userManager.getUserInfo(userId);
        if (userInfo == null) {
            return;
        }

        if (!userInfo.isProfile()) {
            return;
        }

        args.putParcelable(UserProfilesListSettings.EXTRA_USER_INFO, userInfo);

        new SubSettingLauncher(ctx)
                .setDestination(UserProfileDetailsSettings.class.getName())
                .setSourceMetricsCategory(args.getInt(DashboardFragment.CATEGORY,
                        SettingsEnums.PAGE_UNKNOWN))
                .setTitleText(userInfo.name)
                .setArguments(args)
                .launch();
    }

    private UserRestrictionsFetcher mUserRestrictionsFetcher;
    private Preference mAppCopyingPref;
    private Preference mAppsInstallsPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        Context ctx = requireContext();
        UserManager userManager = ctx.getSystemService(UserManager.class);
        if (userManager == null) {
            throw new IllegalStateException("UserManager must be non-null");
        }

        Bundle args = requireArguments();
        UserInfo userInfo = args.getParcelable(UserProfilesListSettings.EXTRA_USER_INFO, UserInfo.class);
        if (userInfo == null) {
            throw new IllegalStateException("Arguments to this fragment must contain the user info");
        }

        this.mUserRestrictionsFetcher = UserRestrictionsFetcher.create(userManager, userInfo);

        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(ctx);

        mAppCopyingPref = new Preference(ctx);
        mAppCopyingPref.setTitle(R.string.user_copy_apps_menu_title);
        mAppCopyingPref.setIcon(R.drawable.ic_apps);
        ps.addPreference(mAppCopyingPref);
        mAppCopyingPref.setOnPreferenceClickListener(p -> {
            final Bundle extras = AppCopyFragmentHelperExt.appendArgs(new Bundle(),
                    userInfo.id, userInfo.isPrivateProfile(), userInfo.isPrivateProfile());
            new SubSettingLauncher(getContext())
                    .setDestination(AppCopyFragment.class.getName())
                    .setArguments(extras)
                    .setTitleRes(R.string.user_copy_apps_menu_title)
                    .setSourceMetricsCategory(getMetricsCategory())
                    .launch();
            return true;
        });

        mAppsInstallsPref = new Preference(ctx);
        mAppsInstallsPref.setTitle(R.string.user_app_install);
        mAppsInstallsPref.setIcon(R.drawable.ic_settings_install);
        mAppsInstallsPref.setSummary(UserAppsInstallSettings.getDescription(
                ctx, mUserRestrictionsFetcher));
        ps.addPreference(mAppsInstallsPref);
        mAppsInstallsPref.setOnPreferenceClickListener(p -> {
            UserAppsInstallSettings.launch(mAppsInstallsPref, userInfo.id);
            return true;
        });
        setPreferenceScreen(ps);
        updatePreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreference();
    }

    private void updatePreference() {
        if (mUserRestrictionsFetcher.isSet(UserManager.DISALLOW_INSTALL_APPS)) {
            mAppCopyingPref.setEnabled(false);
        }

        if (!mUserRestrictionsFetcher.canSet(UserManager.DISALLOW_INSTALL_APPS)
                && !mUserRestrictionsFetcher.canSet(
                UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)) {
            mAppsInstallsPref.setEnabled(false);
        }

        mAppsInstallsPref.setSummary(UserAppsInstallSettings.getDescription(
                requireContext(), mUserRestrictionsFetcher));
    }

    @Override
    public int getMetricsCategory() {
        return METRICS_CATEGORY_UNKNOWN;
    }
}
