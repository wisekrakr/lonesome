package com.wisekrakr.david.lonesome.client;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wisekrakr.david.lonesome.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterUsers {



    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        private ImageView avatarImage;
        private TextView nameText, emailText, locationText;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarImage = itemView.findViewById(R.id.avatar_image);
            nameText = itemView.findViewById(R.id.name_text);
            emailText = itemView.findViewById(R.id.email_text);
            locationText = itemView.findViewById(R.id.location_text);


        }
    }
}
