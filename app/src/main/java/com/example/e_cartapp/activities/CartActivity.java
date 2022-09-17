package com.example.e_cartapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_cartapp.adapters.CartAdapter;
import com.example.e_cartapp.databinding.ActivityCartBinding;
import com.example.e_cartapp.model.Product;
import com.hishd.tinycart.exceptions.ProductNotFoundException;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    CartAdapter cartAdapter;
    ArrayList<Product> products;
    Product product;
    AlertDialog dialog;
    final double tax = 0.11; // 11 %
    double discount, total;
    Cart cart;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        products = new ArrayList<>();
        cart = TinyCartHelper.getCart();

        if (cart.isCartEmpty()) {
            binding.cartList.setVisibility(View.GONE);
            binding.emptyCartAnim.setVisibility(View.VISIBLE);
            binding.totalPrice.setText(0);
            binding.subTotal.setText(0);
        }
        // getAllItemsWithQty returns a map

        /*for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
            Log.e("err", "Quantity" + product.getQuantity());
        }*/
        Log.d("err", cart.toString());
        binding.subTotal.setText(String.format("Rs. %.2f", cart.getTotalPrice()));
        discount = getIntent().getDoubleExtra("discount", 0.0);
        binding.discountPrice.setText(String.format("%.2f", discount));

        cartAdapter = new CartAdapter(this, (ArrayList<Product>) getCartItems(), new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subTotal.setText(String.format("Rs. %.2f", cart.getTotalPrice()));
                total = ((cart.getTotalPrice().doubleValue() * tax) + cart.getTotalPrice().doubleValue()) - discount;
                binding.totalPrice.setText(String.format("Rs. %.2f",
                        ((cart.getTotalPrice().doubleValue() * tax) +
                                cart.getTotalPrice().doubleValue()) - discount));
            }
            public void onDeleteItem() {
                try {
                    cartAdapter.notifyDataSetChanged();
                    binding.subTotal.setText(String.format("Rs. %.2f", cart.getTotalPrice()));
                    total = ((cart.getTotalPrice().doubleValue() * tax) + cart.getTotalPrice().doubleValue()) - discount;
                    binding.totalPrice.setText(String.format("Rs. %.2f",
                            ((cart.getTotalPrice().doubleValue() * tax) +
                                    cart.getTotalPrice().doubleValue()) - discount));
                    startActivity(new Intent(CartActivity.this, CartActivity.class));
                } catch (ProductNotFoundException ex) {
                    Toasty.error(CartActivity.this, "The Product is not found on the cart", Toast.LENGTH_SHORT, true).show();
                }
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
            onBackPressed();
        });

        //   Continue
        binding.continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.e_cartapp.activities.Checkout.class);
            intent.putExtra("tax", tax);
            intent.putExtra("total", total);
            startActivity(intent);
        });
    }
    private List<Product> getCartItems() {
        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
            Log.e("err", "Quantity" + product.getQuantity());
        }
        return products;
    }

}