package com.example.e_cartapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.R;
import com.example.e_cartapp.activities.CartActivity;
import com.example.e_cartapp.databinding.CheckoutItemsBinding;
import com.example.e_cartapp.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    Context context;
    ArrayList<Product> products;
    Cart cart = TinyCartHelper.getCart();

    public CheckoutAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        cart = TinyCartHelper.getCart();
    }

    public class CheckoutViewHolder extends RecyclerView.ViewHolder {
        CheckoutItemsBinding binding;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CheckoutItemsBinding.bind(itemView);
        }
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckoutViewHolder(LayoutInflater.from(context).inflate(R.layout.checkout_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);
        holder.binding.name.setText(product.getName());
        holder.binding.price.setText(String.format("Rs. %.2f", product.getPrice()));
        holder.binding.quantity.setText(product.getQuantity() + " item(s)");
        holder.binding.checkOut.setOnClickListener(v -> {
            context.startActivity(new Intent(context, CartActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
