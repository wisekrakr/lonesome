package com.wisekrakr.david.lonesome.models;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ProfileModel {

    public String username;
    public String email;
    public String location;
    public String website;
    public String avatar;
    public String cover;


    public ProfileModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public ProfileModel(String username, String location, String website, String avatar, String cover) {

        this.username = username;
        this.location = location;
        this.website = website;
        this.avatar = avatar;
        this.cover = cover;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("location", location);
        result.put("website", website);
        result.put("avatar", avatar);
        result.put("cover", cover);
        return result;
    }

}