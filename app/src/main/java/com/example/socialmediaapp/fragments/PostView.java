package com.example.socialmediaapp.fragments;

import static com.example.socialmediaapp.MainActivity.POST_ID;
import static com.example.socialmediaapp.MainActivity.USER_ID;
import static com.example.socialmediaapp.MainActivity.VIEW_POST;

import android.app.Activity;
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
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.adapter.HomeAdapter;
import com.example.socialmediaapp.model.HomeModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostView extends Fragment {

    String userUID, postID;
    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    HomeAdapter homeAdapter;
    RecyclerView recyclerView;
    private List<HomeModel> list;
    Activity activity;

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
        homeAdapter = new HomeAdapter(list, getActivity());
        recyclerView.setAdapter(homeAdapter);

        loadPostsFromUsers();

        homeAdapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likeList.contains(userUID)) {
                    likeList.remove(userUID); // unlike
                } else {
                    likeList.add(userUID); // like
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

    void init(View view) {

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
                                    homeAdapter.notifyDataSetChanged();
                                });
                    }
                });
    }
}