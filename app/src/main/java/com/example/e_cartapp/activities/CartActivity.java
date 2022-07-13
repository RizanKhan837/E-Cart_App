package com.example.e_cartapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_cartapp.adapters.CartAdapter;
import com.example.e_cartapp.databinding.ActivityCartBinding;
import com.example.e_cartapp.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    CartAdapter cartAdapter;
    ArrayList<Product> products;
    Product product;
    AlertDialog dialog;
    final double tax = 0.11; // 11 %
    double discount, total;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        products = new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();
        if (cart.isCartEmpty()) {
            binding.cartList.setVisibility(View.GONE);
            binding.emptyCartAnim.setVisibility(View.VISIBLE);
        }
        // getAllItemsWithQty returns a map
        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
            Log.e("err", "Quantity" + product.getQuantity());
        }
        binding.subTotal.setText(String.format("Rs. %.2f", cart.getTotalPrice()));
        discount = getIntent().getDoubleExtra("discount", 0.0);
        binding.discountPrice.setText(String.format("%.2f", discount));

        cartAdapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subTotal.setText(String.format("Rs. %.2f", cart.getTotalPrice()));
                binding.totalPrice.setText(String.format("Rs. %.2f",
                        ((cart.getTotalPrice().doubleValue() * tax) +
                                cart.getTotalPrice().doubleValue()) - discount));
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDeleteItem() {
                cartAdapter.notifyDataSetChanged();
                startActivity(new Intent(CartActivity.this, CartActivity.class));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.setAdapter(cartAdapter);

        total = ((cart.getTotalPrice().doubleValue() * tax) + cart.getTotalPrice().doubleValue()) - discount;
        binding.totalPrice.setText(String.format("Rs. %.2f",
                ((cart.getTotalPrice().doubleValue() * tax) +
                        cart.getTotalPrice().doubleValue()) - discount));

        //   BackBtn
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        //   Continue
        binding.continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.e_cartapp.activities.Checkout.class);
            intent.putExtra("tax", tax);
            intent.putExtra("total", total);
            startActivity(intent);
        });
    }

}