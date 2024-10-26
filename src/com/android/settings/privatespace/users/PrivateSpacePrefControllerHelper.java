package com.android.settings.privatespace.users;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;

import androidx.annotation.NonNull;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.privatespace.PrivateSpaceMaintainer;
import com.android.settings.users.UserRestrictions;

import java.util.Objects;

public final class PrivateSpacePrefControllerHelper {

    private final Context context;
    private final PrivateSpaceMaintainer privateSpaceMaintainer;
    private final UserManager userManager;

    public static PrivateSpacePrefControllerHelper createInstance(Context ctx) {
        return new PrivateSpacePrefControllerHelper(ctx);
    }

    private PrivateSpacePrefControllerHelper(Context ctx) {
        context = ctx;
        privateSpaceMaintainer = PrivateSpaceMaintainer.getInstance(ctx);
        userManager = Objects.requireNonNull(ctx.getSystemService(UserManager.class));
    }

    @BasePreferenceController.AvailabilityStatus
    public int getBaselineAvailability() {
        boolean privateUserSupported = android.os.Flags.allowPrivateProfile()
                && android.multiuser.Flags.enablePrivateSpaceFeatures();
        if (!privateUserSupported) {
            return BasePreferenceController.UNSUPPORTED_ON_DEVICE;
        }

        if (getPrivateSpaceUserIdOrUserNull() == UserHandle.USER_NULL) {
            return BasePreferenceController.CONDITIONALLY_UNAVAILABLE;
        }

        if (privateSpaceMaintainer.isPrivateSpaceLocked()) {
            return BasePreferenceController.CONDITIONALLY_UNAVAILABLE;
        }

        return BasePreferenceController.AVAILABLE;
    }

    @NonNull
    public UserRestrictions createUserRestrictionsInstanceForParentUser() {
        return createUserRestrictionsInstance(context.getUserId());
    }

    @NonNull
    public UserRestrictions createUserRestrictionsInstanceForPrivateSpaceUser() {
        return createUserRestrictionsInstance(getPrivateSpaceUserIdOrUserNull());
    }

    @NonNull
    private UserRestrictions createUserRestrictionsInstance(int userId) {
        UserInfo userInfo = userManager.getUserInfo(userId);
        return new UserRestrictions(userManager, userInfo);
    }

    public int getPrivateSpaceUserIdOrUserNull() {
        UserHandle privateUserHandle = privateSpaceMaintainer.getPrivateProfileHandle();
        if (privateUserHandle != null) {
            return  privateUserHandle.getIdentifier();
        }

        return UserHandle.USER_NULL;
    }

    public boolean hasPrivateSpaceUserId() {
        return getPrivateSpaceUserIdOrUserNull() != UserHandle.USER_NULL;
    }

    public boolean isPrivateSpaceLocked() {
        return privateSpaceMaintainer.isPrivateSpaceLocked();
    }
}
