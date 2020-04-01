package com.wisekrakr.david.lonesome.utils;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wisekrakr.david.lonesome.R;


public class CheckForProfileAttrs {

    public static void image(String imageFile, ImageView image){
        try {
            //if image is received then set that image
            Picasso.get().load(imageFile).into(image);
        }catch (Exception e){
            //if there is no image, set Image View to default
            Picasso.get().load(R.drawable.ic_default_img).into(image);
        }
    }
}
