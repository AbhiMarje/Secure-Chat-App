package com.example.securechatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.securechatapp.R;
import com.example.securechatapp.utilities.User;
import com.example.securechatapp.utilities.UserAdapter;
import com.example.securechatapp.utilities.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchView search;
    private ArrayList<UserList> arrayList = new ArrayList<>();
    private UserAdapter  adapter;
    private ArrayList<UserList> arrayListAll;

    public UsersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.userFragRecyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.fragUserProgressBar);
        search = (SearchView) view.findViewById(R.id.userSearch);
        adapter = new UserAdapter(arrayList, getActivity().getApplicationContext());
        arrayListAll = new ArrayList<>();
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        displayUsers();
        searchUsers();

        return view;

    }

    private void searchUsers() {

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<UserList> tempArray = new ArrayList<>();
                if (!newText.isEmpty()){
                    for (UserList list : arrayList){
                        if (list.getName().toLowerCase().contains(newText) ||
                                list.getEmail().toLowerCase().contains(newText)){
                            tempArray.add(list);
                        }
                    }
                }else {
                    tempArray.addAll(arrayListAll);
                }

                arrayList.clear();
                arrayList.addAll(tempArray);
                adapter.notifyDataSetChanged();
                tempArray.clear();

                return false;
            }
        });

//        search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                ArrayList<UserList> tempArray = new ArrayList<>();
//                if (!TextUtils.isEmpty(charSequence) || !charSequence.toString().isEmpty()){
//                    for (UserList list : arrayList){
//                        if (list.getName().toLowerCase().contains(charSequence) ||
//                                list.getEmail().toLowerCase().contains(charSequence)){
//                            tempArray.add(list);
//                        }
//                    }
//                }else {
//                    tempArray.addAll(arrayListAll);
//                }
//
//                arrayList.clear();
//                arrayList.addAll(tempArray);
//                adapter.notifyDataSetChanged();
//                tempArray.clear();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

    }

    private void displayUsers() {

        setProgressBar(true);
        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {

                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        if (!auth.getUid().equals(snapshot.getId())) {

                            String name = snapshot.getString("name");
                            String email = snapshot.getString("email");
                            String imageUri = snapshot.getString("imageUri");
                            String uid = snapshot.getString("uid");
                            String token = snapshot.getString("fcmToken");

                            UserList userList = new UserList(imageUri, name, uid, email, token);
                            arrayList.add(userList);

                        }
                    }
                    arrayListAll.addAll(arrayList);
                    adapter.notifyDataSetChanged();
                    setProgressBar(false);
                }
            }
        });

    }

    private void setProgressBar(Boolean isLoading) {

        if (!isLoading) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

    }
}