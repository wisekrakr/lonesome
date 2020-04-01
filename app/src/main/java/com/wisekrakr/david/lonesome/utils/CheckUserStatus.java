package com.wisekrakr.david.lonesome.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class CheckUserStatus {

    public static final String TAG = "CheckUserStatus";

    /**
     * Checks if there is a user
     * @param user @com.google.firebase.auth.FirebaseUser
     * @return boolean
     */
    public static boolean userStatus(FirebaseUser user){
        if(user != null){
            return true;
        }
        return false;
    }

    /**
     * Checks if this is the user's first time signing in with google account
     * @param task authentication task performed in @LoginActivity
     */
    public static boolean firstSignInWithGoogle(Task<AuthResult> task){
        if(task.getResult().getAdditionalUserInfo().isNewUser() ){
            return true;
        }
        return false;
    }
}
