package com.dbm.task2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dbm.task2.models.Product;
import com.dbm.task2.R;

import java.util.ArrayList;

public class Product_Adapter extends RecyclerView.Adapter<Product_Adapter.ProductViewHolder> {

    private ArrayList<Product> productList;
    private OnProductActionListener productActionListener;

    public Product_Adapter(ArrayList<Product> productList, OnProductActionListener productActionListener) {
        this.productList = productList;
        this.productActionListener = productActionListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productQuantity.setText(String.valueOf(product.getQuantity()));

        holder.plusButton.setOnClickListener(v -> {
            long newQuantity = product.getQuantity() + 1;
            product.setQuantity(newQuantity);
            holder.productQuantity.setText(String.valueOf(newQuantity));
            productActionListener.onQuantityChanged(product.getProductId(), newQuantity);
        });

        holder.minusButton.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                long newQuantity = product.getQuantity() - 1;
                product.setQuantity(newQuantity);
                holder.productQuantity.setText(String.valueOf(newQuantity));
                productActionListener.onQuantityChanged(product.getProductId(), newQuantity);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            productActionListener.onProductDeleted(product.getProductId());
            productList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity;
        Button plusButton, minusButton, deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productQuantity = itemView.findViewById(R.id.productQuantityTextView);
            plusButton = itemView.findViewById(R.id.plusButton);
            minusButton = itemView.findViewById(R.id.minusButton);
            deleteButton = itemView.findViewById(R.id.delete);
        }
    }

    public interface OnProductActionListener {
        void onQuantityChanged(String productId, long newQuantity);

        void onProductDeleted(String productId);
    }
}
