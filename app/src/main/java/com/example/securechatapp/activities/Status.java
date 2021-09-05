package com.example.securechatapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Status extends AppCompatActivity {

    private DocumentReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        reference = firestore.collection("users").document(auth.getUid());

    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.update("status", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.update("status", 1);
    }
}
