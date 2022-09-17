package com.example.e_cartapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.R;
import com.example.e_cartapp.adapters.CategoryAdapter;
import com.example.e_cartapp.adapters.PopularAdapter;
import com.example.e_cartapp.databinding.ActivityHomePageBinding;
import com.example.e_cartapp.model.Categories;
import com.example.e_cartapp.model.PopularProducts;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Home_Page extends AppCompatActivity { // to inherit some methods i.e., onCreate(), setContentView() etc

    ActivityHomePageBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Categories> categories;
    FirebaseFirestore db;

    GoogleSignInClient mGoogleSignInClient;

    PopularAdapter productAdapter;
    ArrayList<PopularProducts> products;
    String id, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());   // // is used fill the window with the UI provided from layout file

        id = getIntent().getStringExtra("id");
        userName = getIntent().getStringExtra("username");

        db = FirebaseFirestore.getInstance();

        initCategories();
        initProducts();
        initSlider();
        googleSignIn();
        getFirebaseDatabase(id);

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(Home_Page.this, SearchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

    }

    private void initSlider() {
        getSlider();
    }

    void initCategories() {
        categories = new ArrayList<>();
        /*categories.add(new Categories("Sports & Outdoor", "https://www.iconsdb.com/icons/preview/white/soccer-3-xxl.png", "#fef4e5", "Some Description", 1));
        categories.add(new Categories("Women's Fashion", "https://tutorials.mianasad.com/ecommerce/uploads/category/1651894486743.png", "#F5E5FE", "Some Description", 1));
        categories.add(new Categories("Men's Clothing", "https://tutorials.mianasad.com/ecommerce/uploads/category/1651894486743.png", "#E5F1FE", "Some Description", 1));
        categories.add(new Categories("Home & Lifestyle", "https://tutorials.mianasad.com/ecommerce/uploads/category/1651894486743.png", "#E8FEE5", "Some Description", 1));
        categories.add(new Categories("Food & Groceries", "https://tutorials.mianasad.com/ecommerce/uploads/category/1651894486743.png", "#F9E4E4", "Some Description", 1));*/
        categoryAdapter = new CategoryAdapter(this, categories);

        getCategories();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }

    void getCategories() {
        /*RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, (Response.Listener<String>) response -> {
            try {
                Log.e("response", response);
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")) {
                    JSONArray categoryArray = mainObj.getJSONArray("categories");
                    for (int i = 0; i < categoryArray.length(); i++) {
                        JSONObject object = categoryArray.getJSONObject(i);
                        Categories category = new Categories(
                                object.getString("name"),
                                Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                object.getString("color"),
                                object.getString("brief")
                        );
                        categories.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    // Do Nothing
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        queue.add(request);*/
        db.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categories category = document.toObject(Categories.class);
                                categories.add(category);
                                categoryAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toasty.error(Home_Page.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    void getProducts() {
        /*RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?count=8";
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
                    // Do Nothing
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(request);*/
        db.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularProducts popularProducts = document.toObject(PopularProducts.class);
                                //popularProducts.setId(document.getId());
                                products.add(popularProducts);
                                productAdapter.notifyDataSetChanged();
                            }
                        } else {
                             Toasty.error(Home_Page.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    void getSlider() {
        /*RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        JSONArray offerArray = mainObject.getJSONArray("news_infos");
                        for (int i = 0; i < offerArray.length(); i++) {
                            JSONObject object = offerArray.getJSONObject(i);
                            binding.carousel.addData(new CarouselItem(
                                            Constants.NEWS_IMAGE_URL + object.getString("image"),
                                            object.getString("title")
                                    )
                            );
                        }
                    } else {
                        // Do Nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do Nothing
            }
        });
        queue.add(request);*/
        db.collection("Sliders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //String image = document.get("image").toString();
                                binding.carousel.addData(new CarouselItem(
                                        document.getString("image"), document.getString("title")
                                        )
                                );
                            }
                        } else {
                            Toasty.error(Home_Page.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new PopularAdapter(this, products);

        getProducts();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
    }

    void googleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            binding.profileName.setText(personName);
            binding.profileEmail.setText(personEmail);
            Glide.with(this)
                    .load(String.valueOf(personPhoto))
                    .into(binding.profileImage);
        }
    }

    void getFirebaseDatabase(String id) {

        if (id != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            binding.profileName.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                            binding.profileEmail.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                        } else {
                            Toasty.error(Home_Page.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.error(Home_Page.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                }
            });
        }
    }
}