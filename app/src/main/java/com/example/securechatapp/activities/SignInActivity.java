package com.example.securechatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.securechatapp.R;
import com.example.securechatapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        setListeners();

    }


    private void getDeviceInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        String[] array = {
        "Product: " + Build.PRODUCT,
        "Brand: " + Build.BRAND,
        "Device: " + Build.DEVICE,
        "Board: " + Build.BOARD,
        "Bootloader: " + Build.BOOTLOADER,
        "Display: " + Build.DISPLAY,
        "Fingerprint: " + Build.FINGERPRINT,
        "Hardware: " + Build.HARDWARE,
        "Host: " + Build.HOST,
        "ID: " + Build.ID,
        "Manufacturer: " + Build.MANUFACTURER,
        "Model: " + Build.MODEL,
        "Tags: " + Build.TAGS,
        "Type: " + Build.TYPE,
        "User: " + Build.USER,
        "Time: " + Build.TIME};
        builder.setTitle("Device Information")
                .setItems(array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails(){
        if (binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString().trim()).matches()){
            showToast("Enter valid email");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        }else {
            return true;
        }
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()){
                signIn();
            }
        });

        binding.deviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceInfo();
            }
        });
    }

    private void signIn() {
        loading(true);

        auth.signInWithEmailAndPassword(
                binding.inputEmail.getText().toString().trim(),
                binding.inputPassword.getText().toString().trim()).addOnSuccessListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading(false);
                showToast(e.getMessage());
            }
        });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }
    
}