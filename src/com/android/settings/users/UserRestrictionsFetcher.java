package com.android.settings.users;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class UserRestrictionsFetcher {

    @NonNull
    private final UserManager mUserManager;
    @NonNull
    private final UserInfo mUserInfo;

    @Nullable
    public static UserRestrictionsFetcher create(@NonNull Context ctx, int userId) {
        UserManager userManager = ctx.getSystemService(UserManager.class);
        if (userManager == null) {
            return null;
        }

        return create(userManager, userId);
    }

    @Nullable
    public static UserRestrictionsFetcher create(@NonNull UserManager userManager, int userId) {
        UserInfo userInfo = userManager.getUserInfo(userId);
        if (userInfo == null) {
            return null;
        }

        return new UserRestrictionsFetcher(userManager, userInfo);
    }

    @NonNull
    public static UserRestrictionsFetcher create(@NonNull UserManager userManager, @NonNull UserInfo userInfo) {
        return new UserRestrictionsFetcher(userManager, userInfo);
    }

    private UserRestrictionsFetcher(@NonNull UserManager userManager, @NonNull UserInfo userInfo) {
        this.mUserManager = userManager;
        this.mUserInfo = userInfo;
    }

    public boolean isSet(String restrictionKey) {
        final boolean isSetFromUser = mUserManager.hasUserRestriction(restrictionKey, mUserInfo.getUserHandle());
        if (mUserInfo.isGuest()) {
            return isSetFromUser || mUserManager.getDefaultGuestRestrictions().getBoolean(restrictionKey);
        }

        return isSetFromUser;
    }

    public void set(String restrictionKey, boolean enableRestriction) {
        if (!canSet(restrictionKey)) {
            return;
        }

        if (mUserInfo.isGuest()) {
            Bundle defaultGuestRestrictions = mUserManager.getDefaultGuestRestrictions();
            defaultGuestRestrictions.putBoolean(restrictionKey, enableRestriction);
            mUserManager.setDefaultGuestRestrictions(defaultGuestRestrictions);
        } else {
            mUserManager.setUserRestriction(restrictionKey, enableRestriction, mUserInfo.getUserHandle());
        }
    }

    public boolean canSet(@NonNull String restrictionKey) {
        return getImmutableValue(restrictionKey) == null;
    }

    private Boolean getImmutableValue(String restrictionKey) {
        return switch (restrictionKey) {
            case UserManager.DISALLOW_INSTALL_APPS -> {
                UserInfo parentUserInfo = getParentUserInfo();
                if (parentUserInfo != null) {
                    if (create(mUserManager, parentUserInfo).isSet(restrictionKey)) {
                        yield true;
                    }
                }
                yield null;
            }
            case UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES -> {
                // Do not allow unsetting default user restrictions for guest users.
                if (getUserInfo().isGuest()) {
                    yield true;
                }

                UserInfo parentUserInfo = getParentUserInfo();
                if (parentUserInfo != null) {
                    if (create(mUserManager, parentUserInfo).isSet(restrictionKey)) {
                        yield true;
                    }
                }
                yield null;
            }
            default -> null;
        };
    }

    @NonNull
    private UserInfo getUserInfo() {
        return mUserInfo;
    }

    @Nullable
    private UserInfo getParentUserInfo() {
        return mUserManager.getProfileParent(getUserInfo().id);
    }
}
