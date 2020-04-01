package com.wisekrakr.david.lonesome.app.actions;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;
import com.wisekrakr.david.lonesome.models.ProfileModel;

public class ProfileActions implements ProfileActionsContext {
    public static final String TAG = "ProfileActions";

    @Override
    public void dbCreateProfile(FirebaseRunner firebaseRunner, String username, String location, String website, String avatar, String cover) {

        if(firebaseRunner != null){

            //set new profile
            ProfileModel profile = new ProfileModel(username, location, website, avatar, cover);

            FirebaseUser user = firebaseRunner.getFirebaseAuth().getCurrentUser();

            //path to store user data: "Profiles"
            DatabaseReference reference = firebaseRunner.getFirebaseDatabase().getReference("Profiles");

            //put data in database
            reference.child(user.getUid()).setValue(profile);
        }
    }



}
