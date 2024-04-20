package com.example.socialmediaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.adapter.HomeAdapter;
import com.example.socialmediaapp.chat.ChatActivity;
import com.example.socialmediaapp.chat.ChatUserActivity;
import com.example.socialmediaapp.fragments.Profile;
import com.example.socialmediaapp.model.HomeModel;
import com.example.socialmediaapp.model.PostImageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.example.socialmediaapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostViewActivity extends AppCompatActivity {

    FirebaseUser user;
    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    Profile adapter;
    HomeAdapter homeAdapter;
    RecyclerView recyclerView;
    private List<HomeModel> list;
    Activity activity;
    String postID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_items);

        init();

        Intent intent = getIntent();

        Uri uri = intent.getData();

        loadPostsFromUsers();

        list = new ArrayList<>();
        homeAdapter = new HomeAdapter(list, this);
        recyclerView.setAdapter(homeAdapter);

        homeAdapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid()); // unlike
                } else {
                    likeList.add(user.getUid()); // like
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", likeList);

                reference.update(map);

            }

            @Override
            public void setCommentCount(final TextView textView) {

                commentCount.observe((LifecycleOwner) activity, integer -> {

                    assert commentCount.getValue() != null;

                    if (commentCount.getValue() == 0) {
                        textView.setVisibility(View.GONE);
                    } else
                        textView.setVisibility(View.VISIBLE);

                    StringBuilder builder = new StringBuilder();
                    builder.append("See all ")
                            .append(commentCount.getValue()-2)
                            .append(" comments");

                    textView.setText(builder);
//                    textView.setText("See all " + commentCount.getValue() + " comments");
                });
            }
        });
    }

    void init() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        postID = getIntent().getStringExtra("id");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(PostViewActivity.this, MainActivity.class));
        }else
            startActivity(new Intent(PostViewActivity.this, ReplacerActivity.class));
    }

    private void loadPostsFromUsers() {
        // Lấy bài post từ tất cả người dùng
        FirebaseFirestore.getInstance().collectionGroup("Post Images")
                .whereEqualTo("uid", user)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("Error: ", error.getMessage());
                        return;
                    }

                    if (value == null)
                        return;

                    list.clear();

                    for (QueryDocumentSnapshot snapshot : value) {
                        if (!snapshot.exists())
                            return;

                        HomeModel model = snapshot.toObject(HomeModel.class);
                        list.add(new HomeModel(
                                model.getName(),
                                model.getProfileImage(),
                                model.getImageUrl(),
                                model.getUid(),
                                model.getDescription(),
                                model.getId(),
                                model.getTimestamp(),
                                model.getLikes()));

                        snapshot.getReference().collection("Comments").get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> map = new HashMap<>();
                                        for (QueryDocumentSnapshot commentSnapshot : task.getResult()) {
                                            map = commentSnapshot.getData();
                                        }
                                        commentCount.setValue(map.size());
                                    }
                                });
                    }
                    homeAdapter.notifyDataSetChanged();
                });
    }
//
//    void clickListener() {
//
//        adapter.OnPostView((position, uids, chatID) -> {
//
//            String oppositeUID;
//            if (!uids.get(0).equalsIgnoreCase(user.getUid())) {
//                oppositeUID = uids.get(0);
//            } else {
//                oppositeUID = uids.get(1);
//            }
//
//            Intent intent = new Intent(ChatUserActivity.this, ChatActivity.class);
//            intent.putExtra("uid", oppositeUID);
//            intent.putExtra("id", chatID);
//            startActivity(intent);
//        });
//
//    }
}