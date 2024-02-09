package com.appdev.moodapp.ModelClasses;

public class UserProfile {
    String userImage, UserName;

    public UserProfile() {}

    public UserProfile(String userImage, String userName) {
        this.userImage = userImage;
        UserName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
