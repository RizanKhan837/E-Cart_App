package com.example.e_cartapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.example.e_cartapp.model.UserModel;
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

import java.io.Serializable;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Home_Page extends AppCompatActivity implements Serializable { // to inherit some methods i.e., onCreate(), setContentView() etc

    ActivityHomePageBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Categories> categories;
    FirebaseFirestore db;
    FirebaseDatabase database;

    GoogleSignInClient mGoogleSignInClient;

    PopularAdapter productAdapter;
    ArrayList<PopularProducts> products;
    UserModel userModel;
    String personName, personEmail, id, userName, personPhone;
    Uri personPhoto;
   // BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());   // // is used fill the window with the UI provided from layout file

        id = getIntent().getStringExtra("uid");
        personEmail = getIntent().getStringExtra("email");

        //bottomNavigationView = (BottomNavigationView) binding.bubbleTabBar.getRootView();

        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        initCategories();
        initProducts();
        initSlider();
        googleSignIn();
        getFirebaseDatabase();

        if (userModel.equals(null)){
            Toasty.warning(Home_Page.this, "User Doesn't Exist" , Toast.LENGTH_SHORT, true).show();
        }
        if (!userModel.getName().isEmpty()){
            binding.profileName.setText(userModel.getName());
        }
        if (!userModel.getEmail().isEmpty()){
            binding.profileEmail.setText(userModel.getEmail());
        }
        if (userModel.getProfileUrl() != null){
            Glide.with(this)
                    .load(String.valueOf(userModel.getProfileUrl()))
                    .into(binding.profileImage);
        }

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

        /*binding.bubbleTabBar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {
                switch (i){
                    case R.id.home:
                        break;
                    case R.id.search:
                        binding.searchBar.openSearch();
                        break;
                    case R.id.cart:
                        Toasty.warning(getApplicationContext(), "Go to Cart", Toast.LENGTH_SHORT, true).show();
                        break;
                    case R.id.user:
                        finish();
                        Intent intent = new Intent(Home_Page.this, SearchActivity.class);
                        overridePendingTransition(0,0);
                        startActivity(intent);
                        break;
                }
            }
        });*/

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Page.this, Profile.class);
                intent.putExtra("username", userModel.getName());
                startActivity(intent);
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
                userModel = new UserModel(
                        acct.getDisplayName(),
                        acct.getEmail(),
                        "+XX XXX XXXXXXX",
                        "House # 1234 Your Town Etc",
                        "City",
                        "Country",
                        acct.getPhotoUrl());
            database.getReference("Users").child(id).setValue(userModel);
            binding.profileName.setText(userModel.getName());
            binding.profileEmail.setText(userModel.getEmail());
            Glide.with(this)
                    .load(String.valueOf(userModel.getProfileUrl()))
                    .into(binding.profileImage);
        }
    }

    void getFirebaseDatabase() {
            database.getReference("Users").child(id)
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            userModel = dataSnapshot.getValue(UserModel.class);
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