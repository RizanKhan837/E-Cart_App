package com.example.e_cartapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.e_cartapp.databinding.ActivityProductDetailBinding;
import com.example.e_cartapp.model.Product;
import com.example.e_cartapp.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Product_Detail extends AppCompatActivity {

    ActivityProductDetailBinding binding;
    Product currentProduct;
    double totalDiscount;
    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cart = TinyCartHelper.getCart();

        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        double discount = getIntent().getDoubleExtra("discount", 0.0);
        int id = getIntent().getIntExtra("id", 0);
        double price = getIntent().getDoubleExtra("price", 0.0);
        binding.txtTitle.setText(name);
        binding.txtPrice.setText(String.format("Rs. %.2f", price));

        Glide.with(this)
                .load(image)
                .into(binding.productImage);

        getProductDetails(id);

        //Back Button
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.e_cartapp.activities.CartActivity.class);
            intent.putExtra("discount", totalDiscount);
            startActivity(intent);
        });
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.addToCartBtn.setOnClickListener(v -> {
            cart.addItem(currentProduct, 1);
            totalDiscount += currentProduct.getDiscount();
            binding.addToCartBtn.setEnabled(false);
            binding.addToCartBtn.setText("Added To Cart...");
        });
    }

    void getProductDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCT_DETAILS_URL + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")) {
                    JSONObject product = mainObj.getJSONObject("product");
                    String description = product.getString("description");
                    binding.productDescription.setText(Html.fromHtml(description));

                    currentProduct = new Product(
                            product.getString("name"),
                            Constants.PRODUCTS_IMAGE_URL + product.getString("image"),
                            product.getString("status"),
                            product.getDouble("price"),
                            product.getDouble("price_discount"),
                            product.getInt("id"),
                            product.getInt("stock")
                    );
                    currentProduct.setDescription(description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });
        queue.add(request);
    }
}