package com.android.settings.users;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.UserIcons;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.drawable.CircleFramedDrawable;

import java.util.List;

import static android.os.UserHandle.USER_NULL;

public class UserProfilesListSettings extends SettingsPreferenceFragment {

    public static final String EXTRA_USER_INFO = "user_info";
    public static final String EXTRA_USER_ID = UserDetailsSettings.EXTRA_USER_ID;
    static final String PREF_NAME = "profile_list";

    public static void launch(Context ctx, int userId) {
        Bundle args = new Bundle();
        UserManager userManager = ctx.getSystemService(UserManager.class);
        if (userManager == null) {
            return;
        }

        // noinspection missingpermission
        UserInfo userInfo = userManager.getUserInfo(userId);
        if (userInfo == null) {
            return;
        }

        switch (userInfo.userType) {
            case UserManager.USER_TYPE_FULL_SYSTEM,
                 UserManager.USER_TYPE_FULL_SECONDARY -> {
            }
            default -> {
                return;
            }
        }

        args.putParcelable(EXTRA_USER_INFO, userInfo);

        new SubSettingLauncher(ctx)
                .setDestination(UserProfilesListSettings.class.getName())
                .setSourceMetricsCategory(args.getInt(DashboardFragment.CATEGORY,
                        SettingsEnums.PAGE_UNKNOWN))
                .setTitleText(userInfo.name)
                .setArguments(args)
                .launch();
    }

    private UserManager userManager;
    private UserInfo userInfo;
    private PreferenceCategory profilePrefList;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        Context ctx = requireContext();
        UserManager userManager = ctx.getSystemService(UserManager.class);
        if (userManager == null) {
            throw new IllegalStateException("UserManager must be non-null");
        }

        Bundle args = requireArguments();
        UserInfo userInfo = args.getParcelable(EXTRA_USER_INFO, UserInfo.class);
        if (userInfo == null) {
            throw new IllegalStateException("Arguments to this fragment must contain the user info");
        }

        this.userManager = userManager;
        this.userInfo = userInfo;

        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(ctx);
        profilePrefList = new PreferenceCategory(ctx);
        profilePrefList.setTitle(R.string.user_profile_list_settings);
        ps.addPreference(profilePrefList);
        refreshProfileList(ctx);
        setPreferenceScreen(ps);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfileList(requireContext());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void refreshProfileList(Context ctx) {
        profilePrefList.removeAll();
        List<UserInfo> profiles = userManager.getProfiles(userInfo.id);
        Resources resources = ctx.getResources();
        Drawable icon = new CircleFramedDrawable(
                UserIcons.convertToBitmapAtUserIconSize(resources,
                        UserIcons.getDefaultUserIcon(resources, USER_NULL, false)),
                resources.getDimensionPixelSize(
                        R.dimen.multiple_users_user_icon_size));
        for (UserInfo profile: profiles) {
            if (profile.id == userInfo.id) {
                continue;
            }

            Preference p = new Preference(ctx);
            p.setTitle(profile.name);
            p.setIcon(icon);
            p.setOnPreferenceClickListener(unused -> {
                UserProfileDetailsSettings.launch(ctx, profile.id);
                return true;
            });
            profilePrefList.addPreference(p);
        }
    }

    @Override
    public int getMetricsCategory() {
        return METRICS_CATEGORY_UNKNOWN;
    }
}
