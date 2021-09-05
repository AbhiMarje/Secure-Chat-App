package com.example.securechatapp.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.securechatapp.R;
import com.example.securechatapp.databinding.RecievedLayoutBinding;
import com.example.securechatapp.databinding.SentLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    ArrayList<ChatMessage> messageArrayList;
    final int itemSent = 1;
    final int itemRecieved = 2;
    String receiverImage;
    String senderId;
    Context context;

    public MessageAdapter(ArrayList<ChatMessage> messageArrayList, String receiverImage, String senderId, Context context) {
        this.messageArrayList = messageArrayList;
        this.receiverImage = receiverImage;
        this.senderId = senderId;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == itemSent){
            View view = LayoutInflater.from(context).inflate(R.layout.sent_layout,parent,false);
            return new senderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.recieved_layout,parent,false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (FirebaseAuth.getInstance().getUid().equals(messageArrayList.get(position).senderId)){
            return itemSent;
        }else {
            return itemRecieved;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatMessage chatMessage = messageArrayList.get(position);
        if (holder.getClass().equals(senderViewHolder.class)){
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.binding.sentText.setText(chatMessage.getMessage());
            viewHolder.binding.sentTime.setText(chatMessage.getDataTime());
        }else {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.binding.recievedText.setText(chatMessage.getMessage());
            viewHolder.binding.recievedTime.setText(chatMessage.getDataTime());
            Glide.with(context).load(receiverImage).into(viewHolder.binding.recieverImage);
        }



    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class senderViewHolder extends RecyclerView.ViewHolder {
        SentLayoutBinding binding;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SentLayoutBinding.bind(itemView);
        }
    }

    public class receiverViewHolder extends RecyclerView.ViewHolder {
        RecievedLayoutBinding binding;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecievedLayoutBinding.bind(itemView);
        }
    }

}
