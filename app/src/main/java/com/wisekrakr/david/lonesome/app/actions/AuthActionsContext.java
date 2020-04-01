package com.wisekrakr.david.lonesome.app.actions;

import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;

public interface AuthActionsContext {

    void dbCreateUser(FirebaseRunner firebaseRunner);
}
