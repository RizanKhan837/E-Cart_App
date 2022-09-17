package com.example.e_cartapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_cartapp.adapters.ProductAdapter;
import com.example.e_cartapp.databinding.ActivitySearchBinding;
import com.example.e_cartapp.model.Product;
import com.example.e_cartapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    FirebaseFirestore db;
    String query;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        query = getIntent().getStringExtra("query");
        title = query.substring(0, 1).toUpperCase() + query.substring(1);
        binding.txtTitle.setText(title);
        db = FirebaseFirestore.getInstance();

        initProducts();
        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, Home_Page.class));
        });
    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);

        getProducts(query);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.searchList.setLayoutManager(layoutManager);
        binding.searchList.setAdapter(productAdapter);
    }

    void getProducts(String query) {
       /* RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?q=" + query;
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
                    Toasty.error(SearchActivity.this, "Something Went Wrong...", Toasty.LENGTH_SHORT, true);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        queue.add(request);*/

        db.collection("Products")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                            Product product = snapshot.toObject(Product.class);
                            String name = product.getName().toLowerCase();
                            if (name.contains(query.toLowerCase())){
                                products.add(product);
                                productAdapter.notifyDataSetChanged();
                            }
                        }
                        if (products.isEmpty()){
                            binding.animationView.setVisibility(View.VISIBLE);
                            binding.txtEmpty.setVisibility(View.VISIBLE);
                            binding.searchList.setVisibility(View.GONE);
                        }
                    }
                });
    }
}