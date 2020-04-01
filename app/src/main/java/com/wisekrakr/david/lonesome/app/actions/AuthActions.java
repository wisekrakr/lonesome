package com.wisekrakr.david.lonesome.app.actions;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;
import com.wisekrakr.david.lonesome.models.UserModel;

public class AuthActions implements AuthActionsContext{
    public static final String TAG = "AuthActions";


    /**
     * Firebase database has to be initialized in the class before using this method.
     * @param firebaseRunner runner of all firebase modules
     */
    @Override
    public void dbCreateUser(FirebaseRunner firebaseRunner){
        //when user is registered store user data in firebase realtime database

        FirebaseUser user = firebaseRunner.getFirebaseAuth().getCurrentUser();

        if(user != null){

            //set user email and uid from auth
            UserModel newUser = new UserModel(user.getUid(), user.getEmail());

            //path to store user data: "Users"
            DatabaseReference reference = firebaseRunner.getFirebaseDatabase().getReference("Users");

            //put data in database
            reference.child(user.getUid()).setValue(newUser);
        }
    }

}
