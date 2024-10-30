package com.android.settings.users;

import android.annotation.UserIdInt;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settingslib.users.AppCopyHelper;
import com.android.settingslib.widget.AppSwitchPreference;

import java.util.ArrayList;
import java.util.List;

public class AppCopyFragmentHelperExt {

    public static final String EXTRA_FOR_PRIVATE_SPACE = "com.android.settings.users.extra.FOR_PRIVATE_SPACE";
    public static final String EXTRA_SHOW_USER_APPS_ONLY = "com.android.settings.users.extra.SHOW_USER_APPS_ONLY";

    static boolean maybeSkipOrModifyAppSwitchPref(
            @NonNull AppCopyFragment appCopyFragment,
            @NonNull AppCopyHelper.SelectableAppInfo selectableAppInfo,
            @NonNull AppSwitchPreference appSwitchPreference) {
        Bundle args = appCopyFragment.getArguments();
        if (args == null) {
            return false;
        }

        maybeModifyAppSwitchPref(appCopyFragment, selectableAppInfo, appSwitchPreference);
        boolean forPrivateSpace = args.getBoolean(EXTRA_FOR_PRIVATE_SPACE, false);
        boolean showUserAppsOnly = args.getBoolean(EXTRA_SHOW_USER_APPS_ONLY, false);
        if (!showUserAppsOnly) {
            return false;
        }

        Context ctx = appCopyFragment.requireContext();
        PackageManager pm = ctx.getPackageManager();
        String pkgName = selectableAppInfo.packageName;
        List<String> allowedSystemApps = new ArrayList<>();
        if (forPrivateSpace) {
            allowedSystemApps.add("com.android.gallery3d");
        }

        final PackageInfo pi;
        try {
            PackageManager.PackageInfoFlags flags = PackageManager.PackageInfoFlags.of(
                    PackageManager.MATCH_UNINSTALLED_PACKAGES
                            | PackageManager.GET_SIGNING_CERTIFICATES
            );
            pi = pm.getPackageInfo(pkgName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        if (pi == null) {
            return false;
        }

        final ApplicationInfo ai = pi.applicationInfo;
        if (ai == null) {
            return false;
        }


        if (ai.isSystemApp()) {
            if (!allowedSystemApps.contains(pkgName)) {
                return true;
            }
        }

        return false;
    }

    private static void maybeModifyAppSwitchPref(
            @NonNull AppCopyFragment appCopyFragment,
            @NonNull AppCopyHelper.SelectableAppInfo selectableAppInfo,
            @NonNull AppSwitchPreference appSwitchPreference) {
    }

    public static Bundle appendArgs(
            @Nullable Bundle bundle, @UserIdInt int userId,
            boolean forPrivateSpace, boolean showUserAppsOnly) {
        Bundle res = bundle != null ? bundle : new Bundle();
        res.putInt(AppCopyFragment.EXTRA_USER_ID, userId);
        res.putBoolean(EXTRA_FOR_PRIVATE_SPACE, forPrivateSpace);
        res.putBoolean(EXTRA_SHOW_USER_APPS_ONLY, showUserAppsOnly);
        return res;
    }

    private AppCopyFragmentHelperExt() {
    }
}
