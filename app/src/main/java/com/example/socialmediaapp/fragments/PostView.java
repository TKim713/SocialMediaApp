package com.example.socialmediaapp.fragments;

import static com.example.socialmediaapp.MainActivity.POST_ID;
import static com.example.socialmediaapp.MainActivity.USER_ID;
import static com.example.socialmediaapp.MainActivity.VIEW_POST;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.ReplacerActivity;
import com.example.socialmediaapp.adapter.HomeAdapter;
import com.example.socialmediaapp.adapter.PostViewAdapter;
import com.example.socialmediaapp.chat.ChatUserActivity;
import com.example.socialmediaapp.model.HomeModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostView extends Fragment {

    String userUID, postID;
    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    PostViewAdapter postViewAdapter;
    RecyclerView recyclerView;
    private List<HomeModel> list;
    Activity activity;
    ImageButton commentBtn;

    public PostView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        init(view);

        if (VIEW_POST) {
            userUID = USER_ID;
            postID = POST_ID;
        }

        list = new ArrayList<>();
        postViewAdapter = new PostViewAdapter(list, getActivity());
        recyclerView.setAdapter(postViewAdapter);

        loadPostsFromUsers();

        postViewAdapter.OnPressed(new PostViewAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {
                // Kiểm tra xem người dùng hiện tại đã like bài viết trước đó hay không
                boolean isPreviouslyLiked = likeList.contains(userUID);

                // Cập nhật danh sách like trên Firestore
                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (isChecked) {
                    // Người dùng thích bài viết
                    likeList.add(userUID); // Thêm người dùng vào danh sách like
                    if (!isPreviouslyLiked && !uid.equals(userUID)) {
                        // Nếu bài viết chưa được người dùng hiện tại like trước đó và người dùng không phải là chủ sở hữu của bài viết
                        createNotification(uid, id); // Tạo thông báo
                    }
                } else {
                    // Người dùng bỏ thích bài viết
                    likeList.remove(userUID); // Xóa người dùng khỏi danh sách like
                }

                // Cập nhật danh sách like trên Firestore
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

                    //textView.setText(builder);
//                    textView.setText("See all " + commentCount.getValue() + " comments");
                });
            }
        });
        view.findViewById(R.id.sendBtn).setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ChatUserActivity.class);
            startActivity(intent);

        });
    }

    void init(View view) {

        commentBtn = view.findViewById(R.id.commentBtn);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadPostsFromUsers() {
        // Lấy bài post từ tất cả người dùng
        CollectionReference reference= FirebaseFirestore.getInstance().collection("Users");
        reference.whereEqualTo("uid", userUID)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance().collectionGroup("Post Images")
                                .whereEqualTo("uid", userUID)
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) {
                                        Log.d("Error: ", error.getMessage());
                                        return;
                                    }

                                    if (value == null)
                                        return;

                                    list.clear();
                                    HomeModel selectedPost = null;

                                    for (QueryDocumentSnapshot snapshot : value) {
                                        if (!snapshot.exists())
                                            return;

                                        HomeModel model = snapshot.toObject(HomeModel.class);
                                        if (model.getId().equals(postID)) {
                                            selectedPost = model;
                                        } else {
                                            list.add(new HomeModel(
                                                    model.getName(),
                                                    model.getProfileImage(),
                                                    model.getImageUrl(),
                                                    model.getUid(),
                                                    model.getDescription(),
                                                    model.getId(),
                                                    model.getTimestamp(),
                                                    model.getLikes()));
                                        }

                                        snapshot.getReference().collection("Comments").get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Map<String, Object> map = new HashMap<>();
                                                        for (QueryDocumentSnapshot commentSnapshot : task1.getResult()) {
                                                            map = commentSnapshot.getData();
                                                        }
                                                        commentCount.setValue(map.size());
                                                    }
                                                });
                                    }
                                    if (selectedPost != null) {
                                        list.add(0, selectedPost);
                                    }
                                    postViewAdapter.notifyDataSetChanged();
                                });
                    }
                });
    }

    void createNotification(String uid, String postid) {

        FirebaseFirestore.getInstance().collection("Users").document(userUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");

                        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

                        String id = reference.document().getId();
                        Map<String, Object> map = new HashMap<>();
                        map.put("time", FieldValue.serverTimestamp());
                        map.put("notification", userName + " liked your post.");
                        map.put("id", id);
                        map.put("uid", uid);
                        map.put("postId", postid);

                        reference.document(id).set(map);
                    }
                });
    }
}