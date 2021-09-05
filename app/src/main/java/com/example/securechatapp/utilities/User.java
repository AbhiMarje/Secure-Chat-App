package com.example.securechatapp.utilities;

import java.io.Serializable;

public class User implements Serializable {

    public String imageUri, name, uid, email;

    public User(String imageUri, String name, String uid, String email) {
        this.imageUri = imageUri;
        this.name = name;
        this.uid = uid;
        this.email = email;
    }

    public User() {
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
}
