package com.example.securechatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.securechatapp.R;
import com.example.securechatapp.databinding.ActivityMainBinding;
import com.example.securechatapp.fragments.HomeFragment;
import com.example.securechatapp.fragments.SettingFragment;
import com.example.securechatapp.fragments.UsersFragment;
import com.example.securechatapp.utilities.Global;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends Status {

    private long pressedTime;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ActivityMainBinding binding;
    String token = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUserDetails();
        getToken();
        signOut();
        floatingActionBtn();


    }

    private void floatingActionBtn() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frameLayout, new HomeFragment());
        transaction.commit();

        binding.fabHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                transaction.addToBackStack(null);
                transaction.replace(R.id.frameLayout, new HomeFragment());
                transaction.commit();
            }
        });

        binding.fabSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                transaction.addToBackStack(null);
                transaction.replace(R.id.frameLayout, new UsersFragment());
                transaction.commit();
            }
        });

        binding.fabSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                transaction.addToBackStack(null);
                transaction.replace(R.id.frameLayout, new SettingFragment());
                transaction.commit();
            }
        });

    }

    private void signOut() {

        binding.imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> update = new HashMap<>();
                update.put("fcmToken", FieldValue.delete());
                firestore.collection("users").document(auth.getUid()).update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        auth.signOut();
                        showToast("Signed Out");
                        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
            }
        });

    }

    private void getToken() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token = s;
                Global.FCM_TOKEN = token;
                firestore.collection("users").document(auth.getUid()).update("fcmToken",token).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Token Updating Failed, Please Sign out and Sign In again!");
                    }
                });
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadUserDetails () {

        firestore.collection("users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                binding.textName.setText(value.getString("name"));
                Global.USER_NAME = value.getString("name");
                Glide.with(getApplicationContext()).load(value.getString("imageUri")).into(binding.imageProfile);

            }
        });

    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}