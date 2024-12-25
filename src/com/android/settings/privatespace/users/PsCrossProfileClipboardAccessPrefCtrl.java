package com.android.settings.privatespace.users;

import android.content.Context;
import android.database.ContentObserver;
import android.os.UserHandle;
import android.os.UserManager;

import com.android.settings.R;
import com.android.settings.ext.AbstractListPreferenceController;
import com.android.settings.ext.RadioButtonPickerFragment2;
import com.android.settings.privatespace.PrivateSpaceMaintainer;

import java.util.Objects;
import java.util.function.Consumer;

import static android.ext.settings.ExtSettings.CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS;
import static android.os.UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.PreferenceScreen;

public class PsCrossProfileClipboardAccessPrefCtrl extends AbstractListPreferenceController {

    private static final int ALLOW = 0;
    private static final int IMPORT_ONLY = 1;
    private static final int EXPORT_ONLY = 2;
    private static final int DISALLOW = 3;

    private final Context mContextForProfile;
    private final UserManager mUm;

    public PsCrossProfileClipboardAccessPrefCtrl(Context ctx, String key) {
        super(ctx, key);
        mContextForProfile = Objects.requireNonNull(ctx.createContextAsUser(userHandle(), 0));
        mUm = Objects.requireNonNull(mContextForProfile.getSystemService(UserManager.class));
    }

    @Override
    public void addPrefsAfterList(RadioButtonPickerFragment2 fragment, PreferenceScreen screen) {
        addFooterPreference(screen, R.string.cross_profile_clipboard_access_footer);
    }

    @Override
    protected void getEntries(Entries entries) {
        boolean parentUserImportAccessAllowed =
                CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.get(mContext, mContext.getUserId());
        boolean parentUserExportAccessAllowed =
                !mUm.hasUserRestrictionForUser(DISALLOW_CROSS_PROFILE_COPY_PASTE, mContext.getUser());
        entries.add(R.string.cross_profile_clipboard_access_allow_title,
                R.string.cross_profile_clipboard_access_allow_summary, ALLOW,
                parentUserImportAccessAllowed && parentUserExportAccessAllowed);
        entries.add(R.string.cross_profile_clipboard_access_import_only_title,
                R.string.cross_profile_clipboard_access_import_only_summary, IMPORT_ONLY,
                parentUserExportAccessAllowed);
        entries.add(R.string.cross_profile_clipboard_access_export_only_title,
                R.string.cross_profile_clipboard_access_export_only_summary, EXPORT_ONLY,
                parentUserImportAccessAllowed);
        entries.add(R.string.cross_profile_clipboard_access_disallow_title,
                R.string.cross_profile_clipboard_access_disallow_summary, DISALLOW);
    }

    @Override
    protected int getCurrentValue() {
        boolean importAccessAllowed = CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.get(mContextForProfile, userHandle().getIdentifier());
        boolean exportAccessAllowed = !mUm.hasUserRestrictionForUser(DISALLOW_CROSS_PROFILE_COPY_PASTE, userHandle());
        if (importAccessAllowed) {
            return exportAccessAllowed ? ALLOW : IMPORT_ONLY;
        } else if (exportAccessAllowed) {
            return EXPORT_ONLY;
        } else {
            return DISALLOW;
        }
    }

    @Override
    protected boolean setValue(int val) {
        UserHandle userHandle = userHandle();
        if (userHandle == null) {
            return false;
        }

        return switch (val) {
            case ALLOW -> {
                mUm.setUserRestriction(DISALLOW_CROSS_PROFILE_COPY_PASTE, false);
                yield CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.put(mContextForProfile, true);
            }
            case IMPORT_ONLY -> {
                mUm.setUserRestriction(DISALLOW_CROSS_PROFILE_COPY_PASTE, true);
                yield CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.put(mContextForProfile, true);
            }
            case EXPORT_ONLY -> {
                mUm.setUserRestriction(DISALLOW_CROSS_PROFILE_COPY_PASTE, false);
                yield CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.put(mContextForProfile, false);
            }
            case DISALLOW -> {
                mUm.setUserRestriction(DISALLOW_CROSS_PROFILE_COPY_PASTE, true);
                yield CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.put(mContextForProfile, false);
            }
            default -> false;
        };
    }

    private UserHandle userHandle() {
        return PrivateSpaceMaintainer.getInstance(mContext).getPrivateProfileHandle();
    }

    @Override
    public int getAvailabilityStatus() {
        return userHandle() != null ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    private final Consumer<android.ext.settings.BoolSetting> observer = setting -> updatePreference();
    private ContentObserver parentUserObserver;
    private ContentObserver profileUserObserver;

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        super.onResume(owner);
        var parentUserObserverObj = CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.registerObserver(
                mContext, mContext.getUserId(), mContext.getMainThreadHandler(), observer);
        if (parentUserObserverObj instanceof ContentObserver parentUserObserverChecked) {
            this.parentUserObserver = parentUserObserverChecked;
        }
        var profileUserObserverObj = CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.registerObserver(
                mContextForProfile, mContextForProfile.getUserId(), mContext.getMainThreadHandler(), observer);
        if (profileUserObserverObj instanceof ContentObserver profileUserObserverChecked) {
            this.profileUserObserver = profileUserObserverChecked;
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        super.onPause(owner);
        if (parentUserObserver != null) {
            CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.unregisterObserver(mContext, parentUserObserver);
        }
        if (profileUserObserver != null) {
            CROSS_PROFILE_CLIPBOARD_IMPORT_ACCESS.unregisterObserver(mContextForProfile, profileUserObserver);
        }
    }
}
