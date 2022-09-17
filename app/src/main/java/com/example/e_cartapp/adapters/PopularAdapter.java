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
import com.example.e_cartapp.databinding.ActivityProductDetailBinding;
import com.example.e_cartapp.databinding.ItemProductBinding;
import com.example.e_cartapp.model.PopularProducts;
import com.example.e_cartapp.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ProductViewHolder> {
    Context context;
    ArrayList<PopularProducts> products;
    ActivityProductDetailBinding bind;
    Product currentProduct;
    Cart cart;

    public PopularAdapter(Context context, ArrayList<PopularProducts> products) {
        this.context = context;
        this.products = products;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        PopularProducts product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);
        holder.binding.label.setText(product.getName());
        holder.binding.price.setText(String.format("Rs. %s", product.getPrice()));

        holder.binding.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.e_cartapp.activities.Product_Detail.class);
            intent.putExtra("name", product.getName());
            intent.putExtra("image", product.getImage());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("id", product.getId());
            context.startActivity(intent);
        });

       /* holder.binding.addToCartChip.setOnClickListener(v -> {
            holder.binding.addToCartChip.setChipIconVisible(false);
            holder.binding.addToCartChip.setCheckedIconVisible(true);
            holder.binding.addToCartChip.setText("Added");
            cart.addItem((Item) product, 1);
            notifyDataSetChanged();
        });
        holder.binding.addToCartChip.setOnCloseIconClickListener(v -> {
            holder.binding.addToCartChip.setCheckedIconVisible(false);
            holder.binding.addToCartChip.setChipIconVisible(true);
            cart.removeItem((Item) product);
            notifyDataSetChanged();
        });*/
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductBinding.bind(itemView);
        }
    }
}
