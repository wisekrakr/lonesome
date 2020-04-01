package com.wisekrakr.david.lonesome.app.fragments.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wisekrakr.david.lonesome.R;
import com.wisekrakr.david.lonesome.app.fragments.profile.backend.ImageHandler;
import com.wisekrakr.david.lonesome.app.fragments.profile.backend.ProfileContext;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;
import com.wisekrakr.david.lonesome.models.ProfileModel;
import com.wisekrakr.david.lonesome.utils.CheckForProfileAttrs;
import com.wisekrakr.david.lonesome.utils.Constants;
import com.wisekrakr.david.lonesome.client.PermissionHandler;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends ProfileContext {
    public static final String TAG = "ProfileFragment";

    //firebase
    private FirebaseRunner firebaseRunner;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;

    //views
    private ImageView avatarImage, coverImage;
    private TextView nameText, locationText, emailText, websiteText;
    private FloatingActionButton faBtn;

    //arrays of permissions to be requested
    private String cameraPermissions[];
    private String storagePermissions[];

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init firebase
        firebaseRunner = new FirebaseRunner();
        firebaseRunner.initFirebaseAssistant();
        user = firebaseRunner.getFirebaseAuth().getCurrentUser();
        db = firebaseRunner.getFirebaseDatabase();
        databaseReference = db.getReference("Users"); //we refer to users to create a link with a profile
        storageReference = firebaseRunner.getFirebaseStorage();

        //init storage

        //init imageUriClass
        initImageHelperClasses();

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarImage = view.findViewById(R.id.avatar_image);
        coverImage = view.findViewById(R.id.profile_cover_image);
        nameText = view.findViewById(R.id.profile_name_header_text);
        emailText = view.findViewById(R.id.profile_email_text);
        locationText = view.findViewById(R.id.profile_location_text);
        websiteText = view.findViewById(R.id.profile_website_text);
        faBtn = view.findViewById(R.id.profile_action_btn);


        //get current user data
        getCurrentUserData();

        //handle edit profile click
        faBtnClick();

        return view;
    }


    /**
     * Get info of current user by email.
     * orderByChild query will show details from a node whose key named email has the value equal
     * to current user's email.
     * It searches all nodes and where the key matches it will get that data.
     */
    //todo change this whole part first get it working and than refactor it
    private void getCurrentUserData(){
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data is found
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //get data
                    ProfileModel profile = snapshot.getValue(ProfileModel.class);

                    if(profile != null){
                        nameText.setText(profile.username);
                        emailText.setText(profile.email);
                        locationText.setText(profile.location);
                        websiteText.setText(profile.website);

                        try {
                            //if image is received then set that image
                            Picasso.get().load(profile.avatar).into(avatarImage);
                        }catch (Exception e){
                            //if there is no image, set Image View to default
                            Picasso.get().load(R.drawable.ic_default_img).into(avatarImage);
                        }

                        try {
                            //if image is received then set that image
                            Picasso.get().load(profile.cover).into(coverImage);
                        }catch (Exception e){
                            //if there is no image, set Image View to default
                            Picasso.get().load(R.drawable.ic_default_bg).into(coverImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * This is called when the user presses Allow or Deny from permission request dialog
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //here we will handle permission cases allowed and denied

        if(grantResults.length > 0){
            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean writeStorageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;

            switch (requestCode) {
                case Constants.CAMERA_REQUEST_CODE:
                    //check if camera and storage permissions are allowed or not

                    if(cameraAccepted && writeStorageAccepted){
                        //permissions enabled
                        getImagePicker().chooseFromCamera(ProfileFragment.this);

                    }else{
                        //permissions denied
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.STORAGE_REQUEST_CODE:
                    //check if storage permissions are allowed or not

                    if(writeStorageAccepted){
                        //permissions enabled
                        getImagePicker().chooseFromGallery(ProfileFragment.this);

                    }else{
                        //permissions denied
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    }

    /**
     * Called after picking image from Camera of Gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case Constants.IMAGE_CHOOSE_GALLERY_CODE:
                    //image picked from gallery, get uri of image
                    try {
                        getImageUriClass().setImageUri(data.getData());
                        uploadProfileImage();
                    }catch (NullPointerException e){
                        Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case Constants.IMAGE_CHOOSE_CAMERA_CODE:
                    //image picked from camera, get uri of image
                    uploadProfileImage();
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Floating Action Button click
     */
    private void faBtnClick(){
        faBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    /**
     * Handles creating/ editing the profile
     */
    private void showEditProfileDialog() {

        //options to show in dialog
        String[] options = {"Edit username", "Edit Profile Picture","Edit Cover Photo", "Edit Website", "Edit Location"};
        //alert dialog
        alertDialogBuilder( options, "Choose what to edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //handle dialog item clicks
                switch (which){
                    case 0:
                        //Edit Username Clicked
                        showTextDialog("username");
                        break;
                    case 1:
                        //Edit Profile Pic Clicked
                        getProfileAvatarOrCover().setChoice("avatar");
                        showImageDialog();
                        break;
                    case 2:
                        //Edit Cover Photo Clicked
                        getProfileAvatarOrCover().setChoice("cover");
                        showImageDialog();
                        break;
                    case 3:
                        //Edit Website Clicked
                        showTextDialog("website");
                        break;
                    case 4:
                        //Edit Location Clicked
                        showTextDialog("location");

                        break;
                }
            }
        });


    }

    /**
     * Show custom dialog:
     * @param key profile database key to update profile database value
     */
    private void showTextDialog(final String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key);

        //set dialog layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add dialog buttons
        //update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from EditText
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if(!TextUtils.isEmpty(value)){
                    HashMap<String, Object>result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),"Updated: " + key , Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Error occurred " + e.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(),"Please Enter " + key , Toast.LENGTH_SHORT).show();

                }
            }
        });
        //cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    /**
     * Show dialog containing options Camera and Gallery to pick image
     */
    private void showImageDialog() {

        //options to show in dialog
        String[] options = {"Camera", "Gallery"};

        //alert dialog
        alertDialogBuilder( options, "Take a brand new photo, or choose one", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                switch (which){
                    case 0:
                        //Pick camera
                        if(!PermissionHandler.checkForPermission(getActivity(), Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED) &&
                                !PermissionHandler.checkForPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)){
                            PermissionHandler.requestPermission(getActivity(), cameraPermissions, Constants.CAMERA_REQUEST_CODE);
                        }else{
                            try {
                                getImagePicker().chooseFromCamera(ProfileFragment.this);
                            }catch (Exception e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case 1:
                        //Pick gallery
                        if(!PermissionHandler.checkForPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)){
                            PermissionHandler.requestPermission(getActivity(), storagePermissions, Constants.STORAGE_REQUEST_CODE);
                        }else{
                            try {
                                getImagePicker().chooseFromGallery(ProfileFragment.this);

                            }catch (NullPointerException e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            }
        });
    }

    /**
     * "avatar" refers to "Edit Profile Pic", "cover" refers to "Edit Cover Photo"
     *  Both are keys in each user, containing url of user's profile pic or cover
     */
    private void uploadProfileImage(){
        //path and name of image to be stored in firebase storage
        //e.g. "Users_Profile_Cover_Imgs/{filename}.jpg
        String filePathAndName = Constants.STORAGE_PROFILE_IMGS + ""+ getProfileAvatarOrCover().getChoice() + "_" + user.getUid();

        StorageReference sr = storageReference.child(filePathAndName);
        sr.putFile(getImageUriClass().getImageUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //get image url and store in profile database
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful());

                Uri downloadUri = uriTask.getResult();

                //check if image is uploaded or not and url is received
                if(uriTask.isSuccessful()){

                    //image uploaded. Add/update url in profile database
                    HashMap<String, Object>results = new HashMap<>();
                    //first para is our choice "avatar" or "cover" (key in profile db), second para is image url in firebase storage (value in profile db)
                    results.put(getProfileAvatarOrCover().getChoice(), downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Image updated... ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error adding image url in database
                            Log.d(getTag(), e.getMessage());
                            Toast.makeText(getActivity(), "Error updating image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    //error
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(getTag(), e.getMessage());
                Toast.makeText(getActivity(), "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}

