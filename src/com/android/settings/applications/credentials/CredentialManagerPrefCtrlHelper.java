package com.android.settings.applications.credentials;

import android.content.Context;
import android.os.UserHandle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settings.accounts.AccountPrivateDashboardFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.privatespace.PrivateSpaceMaintainer;

class CredentialManagerPrefCtrlHelper {

    public interface DelegateIface {
        default boolean isPrivateProfile() {
            return false;
        }
    }

    @NonNull
    static CredentialManagerPrefCtrlHelper createInstanceFromFragment(
            @NonNull DashboardFragment initHostFragment) {
        if (initHostFragment instanceof AccountPrivateDashboardFragment) {
            return createInstance(new DelegateIface() {
                @Override
                public boolean isPrivateProfile() {
                    return true;
                }
            });
        }

        return createInstance(null);
    }

    @NonNull
    static CredentialManagerPrefCtrlHelper createInstance(@Nullable DelegateIface delegateIface) {
        DelegateIface defIface = new DelegateIface() {
        };
        return new CredentialManagerPrefCtrlHelper(delegateIface != null ? delegateIface : defIface);
    }

    @NonNull
    final DelegateIface delegateIface;

    private CredentialManagerPrefCtrlHelper(@NonNull DelegateIface delegateIface) {
        this.delegateIface = delegateIface;
    }

    static UserHandle getPrivateSpaceUserHandle(Context ctx) {
        return PrivateSpaceMaintainer.getInstance(ctx).getPrivateProfileHandle();
    }
}
