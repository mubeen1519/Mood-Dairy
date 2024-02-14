package com.appdev.moodapp.ModelClasses;

public class UserProfile {
    String UserName, UserPassword, UserEmail;

    public UserProfile() {
    }


    public UserProfile(String userName, String userPassword, String userEmail) {
        UserName = userName;
        UserPassword = userPassword;
        UserEmail = userEmail;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }
}
