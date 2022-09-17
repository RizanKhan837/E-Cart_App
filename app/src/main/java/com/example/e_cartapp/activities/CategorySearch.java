package com.example.e_cartapp.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.e_cartapp.adapters.ProductAdapter;
import com.example.e_cartapp.databinding.ActivityCategorySearchBinding;
import com.example.e_cartapp.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategorySearch extends AppCompatActivity {

    ActivityCategorySearchBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    FirebaseFirestore db;
    String type;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategorySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        type = getIntent().getStringExtra("type");
        title = type.substring(0, 1).toUpperCase() + type.substring(1);
        binding.txtTitle.setText(title);

        initProducts();

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);

        getProducts();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.categoryList.setLayoutManager(layoutManager);
        binding.categoryList.setAdapter(productAdapter);
    }

    void getProducts() {
        /*RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?category_id=" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")) {
                    JSONArray productsArray = mainObj.getJSONArray("products");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject object = productsArray.getJSONObject(i);
                        Product product = new Product(
                                object.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + object.getString("image"),
                                object.getDouble("price"),
                                object.getDouble("price_discount"),
                                object.getInt("id"),
                                object.getInt("stock"),
                                object.getString("type"),
                                object.getString("description")
                        );
                        products.add(product);
                        Log.e("err", "Stock " + product.getStock());
                    }
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toasty.error(CategorySearch.this, "Something Went Wrong...", Toasty.LENGTH_SHORT, true);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        queue.add(request);*/

        //   Getting Clothes
        if (type != null && type.equalsIgnoreCase("clothes")){
            db.collection("Products").whereEqualTo("type", "clothes")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                        Product product = snapshot.toObject(Product.class);
                        products.add(product);
                        productAdapter.notifyDataSetChanged();;
                    }
                }
            });
        }

        //   Getting Electronics
        if (type != null && type.equalsIgnoreCase("electronics")){
            db.collection("Products").whereEqualTo("type", "electronics")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                                Product product = snapshot.toObject(Product.class);
                                products.add(product);
                                productAdapter.notifyDataSetChanged();;
                            }
                        }
                    });
        }
        //   Getting Clothes
        if (type != null && type.equalsIgnoreCase("food")){
            db.collection("Products").whereEqualTo("type", "food")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                                Product product = snapshot.toObject(Product.class);
                                products.add(product);
                                productAdapter.notifyDataSetChanged();;
                            }
                        }
                    });
        }
        //   Getting Clothes
        if (type != null && type.equalsIgnoreCase("lifestyle")){
            db.collection("Products").whereEqualTo("type", "lifestyle")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                                Product product = snapshot.toObject(Product.class);
                                products.add(product);
                                productAdapter.notifyDataSetChanged();;
                            }
                        }
                    });
        }
        //   Getting Clothes
        if (type != null && type.equalsIgnoreCase("medicine")){
            db.collection("Products").whereEqualTo("type", "medicine")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                                Product product = snapshot.toObject(Product.class);
                                products.add(product);
                                productAdapter.notifyDataSetChanged();;
                            }
                        }
                    });
        }
        //   Getting Clothes
        if (type != null && type.equalsIgnoreCase("sports")){
            db.collection("Products").whereEqualTo("type", "sports")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                                Product product = snapshot.toObject(Product.class);
                                products.add(product);
                                productAdapter.notifyDataSetChanged();;
                            }
                        }
                    });
        }
    }

}