package com.example.securechatapp.utilities;

import java.io.Serializable;

public class UserList implements Serializable {

    String imageUri, name, uid, email, fcmToken;

    public UserList() {
    }

    public UserList(String imageUri, String name, String uid, String email, String fcmToken) {
        this.imageUri = imageUri;
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
