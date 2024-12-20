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
import android.widget.Toast;

import com.dbm.task2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Personal_Details_Fragment extends Fragment {

    View fragmentview;

    EditText usernametxt , phonenumber;

    Button createaccount;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentview = inflater.inflate(R.layout.onboarding_personal_details, container, false);

        define_elements();
        return fragmentview;

    }

    private void define_elements() {

        usernametxt = fragmentview.findViewById(R.id.usernametxt);
        phonenumber = fragmentview.findViewById(R.id.phone);


        createaccount= fragmentview.findViewById(R.id.create_account);

        mDatabase = FirebaseDatabase.getInstance().getReference();
         mAuth = FirebaseAuth.getInstance();

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernametxt.getText().toString();
                String phone = phonenumber.getText().toString();

                if (username.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getActivity(), "Error ! Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    createuser(userId, username, phone);
                }
            }
        });


    }

    private void createuser (String userId, String username, String phone) {

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", username);
        userDetails.put("phone", phone);

        mDatabase.child("Users").child(userId).setValue(userDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            NavController navController = NavHostFragment.findNavController(Personal_Details_Fragment.this);
                            navController.navigate(R.id.home_Screen4);

                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}