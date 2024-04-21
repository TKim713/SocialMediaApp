package com.example.socialmediaapp.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.fragments.Profile;
import com.example.socialmediaapp.model.ChatUserModel;
import com.example.socialmediaapp.model.PostImageModel;

import java.util.List;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.PostViewHolder> {

    public OnPostView postView;
    Activity context;
    List<PostImageModel> list;
    private TextView postCountTv;
    public PostViewAdapter(Activity context, List<PostImageModel> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_items, parent, false);
        return new PostViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

//        Glide.with(holder.itemView.getContext().getApplicationContext())
//                .load(model.getImageUrl())
//                .timeout(6500)
//                .into(holder.imageView);
//        count = getItemCount();
//        postCountTv.setText("" + count);
//
//        holder.imageView.setOnClickListener(v ->
//                postView.clicked(position, getItem(position).getUid(), getItem(position).getId()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void OnPostView(OnPostView postView) {
        this.postView = postView;
    }

    public interface OnPostView {
        void clicked(int position, String postID);
    }
    static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
