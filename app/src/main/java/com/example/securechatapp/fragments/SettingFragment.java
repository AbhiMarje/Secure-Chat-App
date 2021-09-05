package com.example.securechatapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.securechatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

public class SettingFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private EditText name, email;
    private RoundedImageView imageView;
    private String imageUri;
    private String updatedUri;
    private Boolean isUriChanged = false;
    private MaterialButton button;
    private Uri mUri;
    private FirebaseStorage storage;
    private StorageReference reference;
    private ProgressBar progressBar;

    public SettingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        name = (EditText) view.findViewById(R.id.fragSettingName);
        email = (EditText) view.findViewById(R.id.fragSettingEmail);
        imageView = (RoundedImageView) view.findViewById(R.id.fragSettingImage);
        button = (MaterialButton) view.findViewById(R.id.fragSettingSave);
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference().child("profile").child(auth.getUid());
        progressBar = (ProgressBar) view.findViewById(R.id.fragSettingProgressBar);

        loadUserDetail();
        editDetails();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (data.getData() != null){
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                mUri = data.getData();
                imageView.setImageURI(mUri);
                isUriChanged = true;
                reference.putFile(mUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    button.setVisibility(View.VISIBLE);
                                    imageUri = uri.toString();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isUriChanged = false;
                                    progressBar.setVisibility(View.INVISIBLE);
                                    button.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity().getApplicationContext(), "Failed to update profile, Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isUriChanged = false;
                        progressBar.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity().getApplicationContext(), "Failed to update profile, Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    private void editDetails() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(i,1);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mName = name.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageUri", imageUri);
                hashMap.put("name", mName);
                hashMap.put("email", mEmail);
                firestore.collection("users").document(auth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isUriChanged = false;
                        Toast.makeText(getActivity().getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isUriChanged = false;
                        Toast.makeText(getActivity().getApplicationContext(), "Profile Updating Failed, Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void loadUserDetail() {

        firestore.collection("users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                name.setText(value.getString("name"));
                email.setText(value.getString("email"));
                imageUri = value.getString("imageUri");
                Glide.with(getActivity().getApplicationContext()).load(value.getString("imageUri")).into(imageView);

            }
        });
    }
}