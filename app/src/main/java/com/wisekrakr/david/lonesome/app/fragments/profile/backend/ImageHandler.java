package com.wisekrakr.david.lonesome.app.fragments.profile.backend;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.wisekrakr.david.lonesome.utils.Constants;

import androidx.fragment.app.Fragment;


public class ImageHandler {

    /**
     * imageUri contains the uri of chosen image from camera or gallery.
     * We use the user UID as name of the image, so that every profile has only one avatar image
     * and one cover image.
     */
    public static class ImageUri{

        private Uri imageUri;

        public Uri getImageUri() {
            return imageUri;
        }

        public  void setImageUri(Uri imageUri) {
            this.imageUri = imageUri;
        }
    }



    public static class ProfileAvatarOrCover {
        private String choice;

        public String getChoice() {
            return choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
        }
    }


    public static class ImagePicker{
        /**
         * Intent of choosing camera to get a picture
         * Putting the image uri in HashMap
         */
        public void chooseFromCamera(Fragment fragment) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

            ImageHandler.ImageUri imageUriClass = new ImageHandler.ImageUri();
            //put image uri
            try{
                imageUriClass.setImageUri(fragment.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));//todo this get null exception

                //intent to start camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriClass.getImageUri());
                fragment.startActivityForResult(intent, Constants.IMAGE_CHOOSE_CAMERA_CODE);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        /**
         * Intent of choosing gallery to get a picture
         */
        public void chooseFromGallery(Fragment fragment) {

            //intent to start gallery pick
            try {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                fragment.startActivityForResult(intent, Constants.IMAGE_CHOOSE_GALLERY_CODE);
            }catch (ActivityNotFoundException e){
                System.out.println(e.getMessage());
            }

        }
    }
}
