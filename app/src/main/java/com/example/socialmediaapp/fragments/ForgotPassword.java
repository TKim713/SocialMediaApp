package com.example.socialmediaapp.fragments;

import static com.example.socialmediaapp.fragments.CreateAccountFragment.EMAIL_REGEX;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapp.LoginActivity;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.ReplacerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends Fragment {

    private TextView loginTv;
    private Button recoverBtn;
    private EditText emailEt;

    private FirebaseAuth auth;

    private ProgressBar progressBar;

    public ForgotPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();

    }

    private void init(View view){

        loginTv = view.findViewById(R.id.loginTV);
        emailEt = view.findViewById(R.id.emailET);
        recoverBtn = view.findViewById(R.id.recoverBtn);
        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

    }

    private void clickListener(){

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEt.getText().toString().trim();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX)){
                    emailEt.setError("Invalid email format");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Password reset email sent successfully",
                                            Toast.LENGTH_SHORT).show();
                                    emailEt.setText("");
                                }else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getContext(), "Error: "+errMsg, Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);

                            }
                        });
            }
        });


    }
}