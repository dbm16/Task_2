package com.dbm.task2.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbm.task2.models.Product;
import com.dbm.task2.adapters.Product_Adapter;
import com.dbm.task2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home_Screen extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private View fragview;
    private RecyclerView recyclerView;
    private Product_Adapter adapter;
    private ArrayList<Product> productList = new ArrayList<>();
    private LinearLayout addProductButton;
    private TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragview = inflater.inflate(R.layout.fragment_home__screen, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = fragview.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Product_Adapter(productList, new Product_Adapter.OnProductActionListener() {
            @Override
            public void onQuantityChanged(String productId, long newQuantity) {
                updateProductQuantity(productId, newQuantity);
            }

            @Override
            public void onProductDeleted(String productId) {
                deleteProduct(productId);
            }
        });
        recyclerView.setAdapter(adapter);

        addProductButton = fragview.findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(v -> showAddProductDialog());

        username = fragview.findViewById(R.id.username);
        loadusername();
        loadUserProducts();

        return fragview;
    }

    private void loadusername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            mDatabase.child("Users").child(uid).child("username")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String username_st = snapshot.getValue(String.class);
                                username.setText("היי, " + username_st + "!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Failed to load username", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        EditText prodname_field = dialogView.findViewById(R.id.productNameEditText);
        EditText prodQuan_field = dialogView.findViewById(R.id.productQuantityEditText);
        Button addButton = dialogView.findViewById(R.id.addButton);

        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String productName = prodname_field.getText().toString();
            String productQuantity = prodQuan_field.getText().toString();

            if (productName.isEmpty() || productQuantity.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            addNewProduct(productName, Integer.parseInt(productQuantity));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addNewProduct(String productName, int quantity) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            String productId = mDatabase.child("Users").child(userId).child("Products").push().getKey();
            Map<String, Object> productData = new HashMap<>();
            productData.put("name", productName);
            productData.put("quantity", quantity);

            if (productId != null) {
                mDatabase.child("Users").child(userId).child("Products").child(productId)
                        .setValue(productData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
                                loadUserProducts();
                            } else {
                                Toast.makeText(getActivity(), "Failed to add product", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void loadUserProducts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            mDatabase.child("Users").child(userId).child("Products")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productList.clear();
                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                String productId = productSnapshot.getKey();
                                String name = productSnapshot.child("name").getValue(String.class);
                                Long quantity = productSnapshot.child("quantity").getValue(Long.class);

                                if (productId != null && name != null && quantity != null) {
                                    productList.add(new Product(productId, name, quantity));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Failed To Fetch Products", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateProductQuantity(String productId, long newQuantity) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            mDatabase.child("Users").child(userId).child("Products").child(productId).child("quantity")
                    .setValue(newQuantity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Home_Screen.this.getActivity(), "Error While Update", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void deleteProduct(String productId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            mDatabase.child("Users").child(userId).child("Products").child(productId)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Home_Screen.this.getActivity(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                Home_Screen.this.loadUserProducts();
                            } else {
                                Toast.makeText(Home_Screen.this.getActivity(), "Failed to delete product", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
