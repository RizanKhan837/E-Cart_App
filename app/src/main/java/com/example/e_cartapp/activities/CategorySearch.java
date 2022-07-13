package com.example.e_cartapp.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_cartapp.adapters.ProductAdapter;
import com.example.e_cartapp.databinding.ActivityCategorySearchBinding;
import com.example.e_cartapp.model.Product;
import com.example.e_cartapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class CategorySearch extends AppCompatActivity {

    ActivityCategorySearchBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    int catId;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategorySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        catId = getIntent().getIntExtra("id", 0);
        title = getIntent().getStringExtra("name");
        binding.txtTitle.setText(title);

        initProducts();

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);

        getProducts(catId);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.categoryList.setLayoutManager(layoutManager);
        binding.categoryList.setAdapter(productAdapter);
    }

    void getProducts(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
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
                                object.getString("status"),
                                object.getDouble("price"),
                                object.getDouble("price_discount"),
                                object.getInt("id"),
                                object.getInt("stock")
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
        queue.add(request);
    }

}