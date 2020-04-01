package com.wisekrakr.david.lonesome.app.actions;

import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;

public interface ProfileActionsContext {
    void dbCreateProfile(FirebaseRunner firebaseRunner, String username, String location, String website, String avatar, String cover);
}
