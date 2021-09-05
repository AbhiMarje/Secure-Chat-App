package com.example.securechatapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.securechatapp.R;
import com.example.securechatapp.utilities.ChatAdapter;
import com.example.securechatapp.utilities.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private ArrayList<ChatMessage> arrayList;
    private ChatAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public HomeFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.homeScreenRecyclerView);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        adapter = new ChatAdapter(arrayList, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
        progressBar = (ProgressBar) view.findViewById(R.id.homeScreenProgressBar);

        listenConversation();


        return view;
    }

    private void listenConversation() {
        firestore.collection("conversation").whereEqualTo("senderId", auth.getUid())
                .addSnapshotListener(eventListener);
        firestore.collection("conversation").whereEqualTo("receiverId", auth.getUid())
                .addSnapshotListener(eventListener);
    }

    private EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange change : value.getDocumentChanges()) {
                if (change.getType() == DocumentChange.Type.ADDED) {
                    String senderId = change.getDocument().getString("senderId");
                    String receiverId = change.getDocument().getString("receiverId");
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (auth.getUid().equals(senderId)) {
                        chatMessage.chatImage = change.getDocument().getString("receiverImage");
                        chatMessage.chatName = change.getDocument().getString("receiverName");
                        chatMessage.chatId = change.getDocument().getString("receiverId");
                    }else {
                        chatMessage.chatImage = change.getDocument().getString("senderImage");
                        chatMessage.chatName = change.getDocument().getString("senderName");
                        chatMessage.chatId = change.getDocument().getString("senderId");
                    }
                    chatMessage.message = change.getDocument().getString("recentMsg");
                    chatMessage.dateObject = change.getDocument().getDate("timeStamp");
                    arrayList.add(chatMessage);
                } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        String senderId = change.getDocument().getString("senderId");
                        String receiverId = change.getDocument().getString("receiverId");
                        if (arrayList.get(i).senderId.equals(senderId) && arrayList.get(i).receiverId.equals(receiverId)) {
                            arrayList.get(i).message = change.getDocument().getString("recentMsg");
                            arrayList.get(i).dateObject = change.getDocument().getDate("timeStamp");
                            break;
                        }
                    }
                }
            }
            Collections.sort(arrayList, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };
}