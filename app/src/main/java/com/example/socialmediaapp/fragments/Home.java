package com.example.socialmediaapp.fragments;

import android.os.Bundle;

<<<<<<< Updated upstream
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
=======
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
>>>>>>> Stashed changes
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.adapter.HomeAdapter;
import com.example.socialmediaapp.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
<<<<<<< Updated upstream
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
=======
>>>>>>> Stashed changes

import java.util.ArrayList;
import java.util.List;

<<<<<<< Updated upstream
public class Home extends Fragment {

    private RecyclerView recyclerView;
    HomeAdapter adapter;
    private List<HomeModel> list;
    private FirebaseUser user;

    DocumentReference reference;

    public Home() {
        // Required empty public constructor
    }

=======

public class Home extends Fragment {
    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    HomeAdapter adapter;
    RecyclerView storiesRecyclerView;
    private RecyclerView recyclerView;
    private List<HomeModel> list;
    private FirebaseUser user;


    public Home()
    {

    }


>>>>>>> Stashed changes
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

<<<<<<< Updated upstream
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        reference = FirebaseFirestore.getInstance().collection("Posts").document(user.getUid());

        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getContext());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();
    }

    private void init(View view) {
=======
    private void loadDataFromFirestore()
    {
        list.add(new HomeModel("Marsad", "01/11/2020","", "","123456", 12 ));
        list.add(new HomeModel("Marsad", "01/11/2020","", "","321654", 20 ));
        list.add(new HomeModel("Marsad", "01/11/2020","", "","452165", 11 ));
        list.add(new HomeModel("Marsad", "01/11/2020","", "","888811", 5 ));

        adapter.notifyDataSetChanged();

    }

    private void init(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
>>>>>>> Stashed changes

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

<<<<<<< Updated upstream
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadDataFromFirestore() {
=======
        storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView);
        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView
                .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
>>>>>>> Stashed changes

    }
}