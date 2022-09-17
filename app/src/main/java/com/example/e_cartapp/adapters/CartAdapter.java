package com.example.e_cartapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.R;
import com.example.e_cartapp.activities.CartActivity;
import com.example.e_cartapp.databinding.CartItemsBinding;
import com.example.e_cartapp.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    CartAdapter cartAdapter;
    Cart cart = TinyCartHelper.getCart();

    public interface CartListener {
        public void onQuantityChanged();
        public void onDeleteItem();
    }

    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        CartItemsBinding binding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartItemsBinding.bind(itemView);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_items, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.cartImage);
        holder.binding.cartTitle.setText(product.getName());
        holder.binding.productTotal.setText(String.format("%.2f", product.getPrice()));
        holder.binding.quantity.setText(String.valueOf(product.getQuantity()));
        holder.binding.txtDescription.setText(Html.fromHtml(product.getDescription()));

        holder.binding.minusCartBtn.setOnClickListener(v -> {
            int quantity = product.getQuantity();
            if (quantity > 1) {
                quantity--;
            }
            product.setQuantity(quantity);
            holder.binding.quantity.setText(String.valueOf(quantity));

            //Update Values
            notifyDataSetChanged();
            cart.updateItem(product, product.getQuantity());
            cartListener.onQuantityChanged();
        });
        holder.binding.plusCartBtn.setOnClickListener(v -> {
            int quantity = product.getQuantity();
            quantity++;
            if (quantity < (product.getStock())) {
                product.setQuantity(quantity);
                holder.binding.quantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(context, "Out Of Stock...", Toast.LENGTH_SHORT).show();
            }
            //Update Values
            notifyDataSetChanged();
            cart.updateItem(product, product.getQuantity());
            cartListener.onQuantityChanged();
        });

        holder.binding.deleteItem.setOnClickListener(v -> {
            cart.removeItem(products.get(position));
            products.remove(position);
            cartListener.onDeleteItem();
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
