package com.android.settings.ext.clipboard;

import android.app.settings.SettingsEnums;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;

public class ClipboardAccessSettings extends DashboardFragment {

    private static final String TAG = "ClipboardAccessSettings";

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.clipboard_access_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PAGE_UNKNOWN;
    }
}