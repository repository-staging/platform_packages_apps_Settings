package com.android.settings.privatespace.users;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.privatespace.PrivateSpaceMaintainer;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.ArrayList;
import java.util.List;

public class PrivateSpaceUserRestrictionsSettings extends DashboardFragment {
    private static final String TAG = "PrivateSpaceUserRestrictionSettings";

    @Override
    public void onCreate(Bundle icicle) {
        if (android.os.Flags.allowPrivateProfile()
                && android.multiuser.Flags.enablePrivateSpaceFeatures()) {
            super.onCreate(icicle);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PrivateSpaceMaintainer.getInstance(getContext()).isPrivateSpaceLocked()) {
            finish();
        }
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PRIVATE_SPACE_SETTINGS;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.privatespace_ext_user_config;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }
}

