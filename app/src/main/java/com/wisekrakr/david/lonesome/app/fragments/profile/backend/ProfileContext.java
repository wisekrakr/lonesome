package com.wisekrakr.david.lonesome.app.fragments.profile.backend;
import com.wisekrakr.david.lonesome.app.fragments.AbstractFragment;

public class ProfileContext extends AbstractFragment {

    public ProfileContext() {
    }

    private ImageHandler imageHandler;
    private ImageHandler.ImageUri imageUriClass;
    private ImageHandler.ProfileAvatarOrCover profileAvatarOrCover;
    private ImageHandler.ImagePicker imagePicker;

    public void initImageHelperClasses(){
        imageHandler = new ImageHandler();
        imageUriClass = new ImageHandler.ImageUri();
        profileAvatarOrCover = new ImageHandler.ProfileAvatarOrCover();
        imagePicker = new ImageHandler.ImagePicker();
    }

    public ImageHandler.ImageUri getImageUriClass() {
        return imageUriClass;
    }

    public ImageHandler getImageHandler() {
        return imageHandler;
    }

    public ImageHandler.ProfileAvatarOrCover getProfileAvatarOrCover() {
        return profileAvatarOrCover;
    }

    public ImageHandler.ImagePicker getImagePicker() {
        return imagePicker;
    }
}
