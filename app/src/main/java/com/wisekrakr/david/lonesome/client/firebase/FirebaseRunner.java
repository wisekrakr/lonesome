package com.wisekrakr.david.lonesome.client.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FirebaseRunner implements FirebaseContext {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDb;
    private StorageReference firebaseStorage;

    @Override
    public void initFirebaseAssistant() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }


    @Override
    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDb;
    }

    @Override
    public StorageReference getFirebaseStorage() {
        return firebaseStorage;
    }
}
