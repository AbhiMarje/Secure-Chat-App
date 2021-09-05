package com.example.securechatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.securechatapp.R;
import com.example.securechatapp.databinding.ActivityChatBinding;
import com.example.securechatapp.utilities.ApiClient;
import com.example.securechatapp.utilities.ApiService;
import com.example.securechatapp.utilities.ChatMessage;
import com.example.securechatapp.utilities.Global;
import com.example.securechatapp.utilities.MessageAdapter;
import com.example.securechatapp.utilities.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.SimpleFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends Status {

    private UserList receiverUser;
    private ActivityChatBinding binding;
    private ArrayList<ChatMessage> arrayList;
    private FirebaseFirestore firestore;
    private MessageAdapter adapter;
    private FirebaseAuth auth;
    private String conversationId = null;
    private Boolean receiverStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        arrayList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadReceiverData();
        listenMessage();
        setListeners();

        adapter = new MessageAdapter(arrayList, receiverUser.getImageUri(),auth.getUid(),getApplicationContext());
        binding.chatScreenRecyclerView.setAdapter(adapter);

    }

    private void listenMessage() {
        firestore.collection("chat").whereEqualTo("senderId", auth.getUid())
                .whereEqualTo("receiverId", receiverUser.getUid()).addSnapshotListener(eventListener);
        firestore.collection("chat").whereEqualTo("senderId", receiverUser.getUid())
                .whereEqualTo("receiverId", auth.getUid()).addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = arrayList.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("senderId");
                    chatMessage.receiverId = documentChange.getDocument().getString("receiverId");
                    chatMessage.message = documentChange.getDocument().getString("message");
                    chatMessage.dataTime = Date(documentChange.getDocument().getDate("timeStamp"));
                    chatMessage.dateObject = documentChange.getDocument().getDate("timeStamp");
                    arrayList.add(chatMessage);
                }
            }
            Collections.sort(arrayList, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                adapter.notifyDataSetChanged();
            }else {
                adapter.notifyItemRangeInserted(arrayList.size(), arrayList.size());
                binding.chatScreenRecyclerView.smoothScrollToPosition(arrayList.size() - 1);
            }
            binding.chatScreenRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.chatScreenProgressbar.setVisibility(View.GONE);
        if (conversationId == null) {
            checkForConversation();
        }
    };

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put("senderId", auth.getUid());
        message.put("receiverId", receiverUser.getUid());
        message.put("message", binding.chatScreenInput.getText().toString().trim());
        message.put("timeStamp", new Date());
        String msg = binding.chatScreenInput.getText().toString().trim();
        firestore.collection("chat").add(message);
        if (conversationId != null) {
            updateConversation(binding.chatScreenInput.getText().toString().trim());
        }else {
            HashMap<String , Object> conversation = new HashMap<>();
            conversation.put("senderId", auth.getUid());
            firestore.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    conversation.put("senderName", documentSnapshot.getString("name"));
                    conversation.put("senderImage", documentSnapshot.getString("imageUri"));
                    conversation.put("receiverId", receiverUser.getUid());
                    conversation.put("receiverImage", receiverUser.getImageUri());
                    conversation.put("receiverName", receiverUser.getName());
                    conversation.put("recentMsg", msg);
                    conversation.put("timeStamp", new Date());
                    addConversation(conversation);
                }
            });
        }
        if (!receiverStatus) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.getFcmToken());

                JSONObject data = new JSONObject();
                data.put("userId", auth.getUid());
                data.put("userName", Global.USER_NAME);
                data.put("fcmToken", Global.FCM_TOKEN);
                data.put("message", msg);

                JSONObject body = new JSONObject();
                body.put(Global.MSG_DATA, data);
                body.put(Global.MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());

            }catch (Exception e) {
                showToast(e.getMessage());
            }
        }
        binding.chatScreenInput.setText(null);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Global.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if (response.isSuccessful()){

                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast("Notification sent successfully");
                }else {
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void checkStatus() {
        firestore.collection("users").document(receiverUser.getUid()).addSnapshotListener(ChatActivity.this,
                (value, error) -> {
            if (error != null){
                return;
            }
            if (value != null){
                if (value.getLong("status") != null){
                    int status = Objects.requireNonNull(value.getLong("status")).intValue();
                    receiverStatus = status == 1;
                }
                receiverUser.setFcmToken(value.getString("fcmToken"));
            }
            if (receiverStatus) {
                binding.status.setVisibility(View.VISIBLE);
            }else {
                binding.status.setVisibility(View.GONE);
            }
        });
    }

    private void setListeners() {

        binding.chatScreenBackBtn.setOnClickListener(v -> onBackPressed());

        binding.chatScreenSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.chatScreenInput.getText().toString().trim().isEmpty()){
                    sendMessage();
                }
            }
        });

    }

    private void addConversation(HashMap<String, Object> conversation) {
        firestore.collection("conversation").add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private void updateConversation(String message) {
        DocumentReference reference = firestore.collection("conversation").document(conversationId);
        reference.update("recentMsg", message, "timeStamp", new Date());
    }

    private String Date(Date date) {
        return new SimpleDateFormat("MM,dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void loadReceiverData() {

        receiverUser = (UserList) getIntent().getSerializableExtra("user");
        binding.chatScreenReceiverName.setText(receiverUser.getName());

    }

    private void checkForConversation () {
        if (arrayList.size() != 0) {
            checkForConversations(auth.getUid(), receiverUser.getUid());
            checkForConversations(receiverUser.getUid(), auth.getUid());
        }
    }

    private void checkForConversations(String senderId, String receiverId) {
        firestore.collection("conversation").whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", receiverId).get().addOnCompleteListener(conversationOnCompleteListener);

    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus();
    }
}