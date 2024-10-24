package com.android.settings.users;

import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;

import java.util.List;

public final class UserRestrictions {

    private final UserManager userManager;
    public final UserInfo userInfo;

    public UserRestrictions(UserManager userManager, UserInfo userInfo) {
        this.userManager = userManager;
        this.userInfo = userInfo;
    }

    public boolean isSet(String restrictionKey) {
        final boolean isSetFromUser = userManager.hasUserRestriction(restrictionKey, userInfo.getUserHandle());
        if (userInfo.isGuest()) {
            return isSetFromUser || userManager.getDefaultGuestRestrictions().getBoolean(restrictionKey);
        }

        return isSetFromUser;
    }

    public void set(String restrictionKey, boolean enableRestriction) {
        if (userInfo.isGuest()) {
            Bundle defaultGuestRestrictions = userManager.getDefaultGuestRestrictions();
            defaultGuestRestrictions.putBoolean(restrictionKey, enableRestriction);
            userManager.setDefaultGuestRestrictions(defaultGuestRestrictions);
        } else {
            userManager.setUserRestriction(restrictionKey, enableRestriction, userInfo.getUserHandle());
        }
    }
}
