package com.example.securechatapp.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.securechatapp.R;
import com.example.securechatapp.activities.ChatActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    ArrayList<ChatMessage> arrayList;
    Context context;

    public ChatAdapter(ArrayList<ChatMessage> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(arrayList.get(position).chatName);
        holder.recentMsg.setText(arrayList.get(position).message);
        Glide.with(context).load(arrayList.get(position).chatImage).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserList user = new UserList();
                user.uid = arrayList.get(holder.getAdapterPosition()).chatId;
                user.name = arrayList.get(holder.getAdapterPosition()).chatName;
                user.imageUri = arrayList.get(holder.getAdapterPosition()).chatImage;
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("user", user);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, recentMsg;
        RoundedImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.homeScreenName);
            imageView = itemView.findViewById(R.id.homeScreenImage);
            recentMsg = itemView.findViewById(R.id.homeScreenRecentMsg);

        }
    }
}
