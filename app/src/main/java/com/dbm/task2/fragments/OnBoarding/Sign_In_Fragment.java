package com.dbm.task2.fragments.OnBoarding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dbm.task2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Sign_In_Fragment extends Fragment {

    View fragmentview;

    EditText email , password;
    TextView email_err , password_err, donthave_account;

    Button sign_up;

    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentview = inflater.inflate(R.layout.fragment_sign__in_, container, false);

        define_elements();
        login();

        return fragmentview;

    }

    private void define_elements() {

        email = fragmentview.findViewById(R.id.emailtxt);
        password = fragmentview.findViewById(R.id.passwordtxt);

        email_err = fragmentview.findViewById(R.id.email_err);
        password_err = fragmentview.findViewById(R.id.password_err);

        sign_up= fragmentview.findViewById(R.id.sign_up);

        donthave_account = fragmentview.findViewById(R.id.donthave_account);

        donthave_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(Sign_In_Fragment.this);
                navController.navigate(R.id.sign_up);
            }
        });

    }


    private void login() {

        mAuth = FirebaseAuth.getInstance();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasError = false;

                if (email.getText().toString().isEmpty()) {
                    email_err.setVisibility(View.VISIBLE);
                    hasError = true;
                } else {
                    email_err.setVisibility(View.INVISIBLE);
                }

                if (password.getText().toString().isEmpty()) {
                    password_err.setVisibility(View.VISIBLE);
                    hasError = true;
                } else {
                    password_err.setVisibility(View.INVISIBLE);
                }

                if (!hasError) {

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        NavController navController = NavHostFragment.findNavController(Sign_In_Fragment.this);
                                        navController.navigate(R.id.home_Screen4);

                                    } else {
                                        Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


    }


}