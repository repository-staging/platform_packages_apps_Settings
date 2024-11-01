package com.android.settings.privatespace.users;

import android.app.settings.SettingsEnums;
import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.privatespace.PrivateSpaceMaintainer;

public class PrivateSpaceClipboardAccessSettings extends DashboardFragment {

    private static final String TAG = "PrivateSpaceClipboardAccessSettings";

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
    protected int getPreferenceScreenResId() {
        return R.xml.privatespace_ext_user_config_clipboard_access;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PRIVATE_SPACE_SETTINGS;
    }
}
