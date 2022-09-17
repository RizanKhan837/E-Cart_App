package com.example.e_cartapp.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.adapters.ProductAdapter;
import com.example.e_cartapp.databinding.ActivityProductDetailBinding;
import com.example.e_cartapp.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Product_Detail extends AppCompatActivity {

    ActivityProductDetailBinding binding;
    Product currentProduct;
    Product product;
    ProductAdapter productAdapter;
    FirebaseFirestore db;
    double totalDiscount;
    Cart cart;
    ArrayList<Product> products;
    int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        cart = TinyCartHelper.getCart();
        products = new ArrayList<Product>();

        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        String description = getIntent().getStringExtra("description");
        double discount = getIntent().getDoubleExtra("discount", 0.0);
        double price = getIntent().getDoubleExtra("price", 0.0);
        int id = getIntent().getIntExtra("id", 0);

        db.collection("Products").whereEqualTo("id", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                            //currentProduct = (Product) snapshot.getData();
                           /* products.add(currentProduct);
                            productAdapter.notifyDataSetChanged();;*/
                            currentProduct = snapshot.toObject(Product.class);
                            Log.d(TAG, ""+currentProduct.getName());
                        }
                    }
                });
        //getProductDetails(id);

        Log.e("err", ""+id);

        binding.productDescription.setText(description);
        binding.txtTitle.setText(name);
        binding.txtPrice.setText(String.format("Rs. %.2f", price));

        Glide.with(this)
                .load(image)
                .into(binding.productImage);

        binding.cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.e_cartapp.activities.CartActivity.class);
            intent.putExtra("discount", totalDiscount);
            startActivity(intent);
        });
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        binding.addToCartBtn.setOnClickListener(v -> {
            cart.addItem(currentProduct, 1);
            totalDiscount += currentProduct.getDiscount();
            binding.addToCartBtn.setEnabled(false);
            binding.addToCartBtn.setText("Added To Cart...");
        });

        binding.minusCartBtn.setOnClickListener(v -> {
            binding.minusCartBtn.playAnimation();
            if (quantity > 1) {
                quantity--;
            }
            binding.quantity.setText(String.valueOf(quantity));
            //Update Values
        });
        binding.plusCartBtn.setOnClickListener(v -> {
            binding.plusCartBtn.playAnimation();
            quantity++;
            if (quantity < (currentProduct.getStock())) {
                binding.quantity.setText(String.valueOf(quantity));
            } else {
                Toasty.info(Product_Detail.this, "Out Of Stock", Toast.LENGTH_SHORT, true).show();
            }
            //Update Values
        });
    }

    void getProductDetails(int id) {
        /*RequestQueue queue = Volley.newRequestQueue(this);
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
                            product.getDouble("price"),
                            product.getDouble("price_discount"),
                            product.getInt("id"),
                            product.getInt("stock"),
                            product.getString("type"),
                            product.getString("description")
                    );
                    currentProduct.setDescription(description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });
        queue.add(request);*/
       /* db.collection("Products").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //product = (Product) document.getData();
                                product = document.toObject(Product.class);
                                productAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Toasty.error(Product_Detail.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });*/
        /*db.collection("Products").whereEqualTo("id", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                            currentProduct = snapshot.toObject(Product.class);
                        }
                    }
                });*/
        db.collection("Products").whereEqualTo("id", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                            //currentProduct = (Product) snapshot.getData();
                           /* products.add(currentProduct);
                            productAdapter.notifyDataSetChanged();;*/
                            currentProduct = snapshot.toObject(Product.class);
                            Log.d(TAG, ""+currentProduct.getName());
                        }
                    }
                });
    }
}