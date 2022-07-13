package com.example.e_cartapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_cartapp.adapters.CheckoutAdapter;
import com.example.e_cartapp.databinding.ActivityCheckoutBinding;
import com.example.e_cartapp.model.Product;
import com.example.e_cartapp.utils.Constants;
import com.example.e_cartapp.utils.LoadingDialog;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Checkout extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    CheckoutAdapter adapter;
    ArrayList<Product> products;
    Cart cart;
    double tax, total;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new LoadingDialog(Checkout.this, "Processing...");

        tax = getIntent().getDoubleExtra("tax", 0.0) * 100;
        total = getIntent().getDoubleExtra("total", 0.0);

        products = new ArrayList<>();
        cart = TinyCartHelper.getCart();

        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }
        adapter = new CheckoutAdapter(this, products);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.checkoutList.setLayoutManager(layoutManager);
        binding.checkoutList.setAdapter(adapter);

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.checkOutBtn.setOnClickListener(v -> {
            processCheckout();
        });

        /*binding.shippingDate.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    binding.shippingDate.setText(selection.toString());
                }
            });
        });*/


    }

    void processCheckout() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject productOrder = new JSONObject();
        JSONObject dataObject = new JSONObject();
        if (binding.userName.getText().toString().isEmpty()) {
            Toasty.warning(Checkout.this, "Please enter your name!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.email.getText().toString().isEmpty()) {
            Toasty.warning(Checkout.this, "Please enter your email!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.phoneNo.getText().toString().isEmpty()) {
            Toasty.warning(Checkout.this, "Please enter your number!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.address.getText().toString().isEmpty()) {
            Toasty.warning(Checkout.this, "Please enter your address!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.shippingDate.getText().toString().isEmpty()) {
            Toasty.warning(Checkout.this, "Please enter shipping date!", Toast.LENGTH_SHORT, true).show();
            return;
        } else {
            try {
                dialog.show();
                productOrder.put("buyer", binding.userName.getText().toString());
                productOrder.put("address", binding.address.getText().toString());
                productOrder.put("email", binding.email.getText().toString());
                productOrder.put("shipping", "asdasdasd");
                productOrder.put("shipping_rate", 0.0);
                productOrder.put("shipping_location", "Pakistan");
                productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
                productOrder.put("phone", "+92 " + binding.phoneNo.getText().toString());
                productOrder.put("comment", binding.comments.getText().toString());
                productOrder.put("status", "WAITING");
                productOrder.put("total_fees", total);
                productOrder.put("tax", tax);
                productOrder.put("serial", "123sd");
                productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
                productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());

                JSONArray productOrderDetail = new JSONArray();
                for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
                    Product product = (Product) item.getKey();
                    int quantity = item.getValue();
                    product.setQuantity(quantity);

                    JSONObject productObj = new JSONObject();
                    productObj.put("product_id", product.getId());
                    productObj.put("product_name", product.getName());
                    productObj.put("amount", quantity);
                    productObj.put("price_item", product.getPrice());
                    productObj.put("created_at", Calendar.getInstance().getTimeInMillis());
                    productObj.put("last_update", Calendar.getInstance().getTimeInMillis());
                    productOrderDetail.put(productObj);
                }
                dataObject.put("product_order", productOrder);
                dataObject.put("product_order_detail", productOrderDetail);

                Log.e("err", dataObject.toString());
            } catch (JSONException e) {
                dialog.dismiss();
            }
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("success")) {
                        Toasty.success(Checkout.this, "Success!", Toast.LENGTH_SHORT, true).show();
                        String codeNumber = response.getJSONObject("data").getString("code");
                        dialog.dismiss();
                        new AlertDialog.Builder(Checkout.this)
                                .setTitle("Order Successful")
                                .setCancelable(false)
                                .setMessage("Your Order Number is: " + codeNumber)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Checkout.this, Payment.class);
                                        intent.putExtra("code", codeNumber);
                                        startActivity(intent);
                                    }
                                }).show();
                        Log.e("err", response.toString());
                    } else {
                        Toasty.error(Checkout.this, "This is an error toast.", Toast.LENGTH_SHORT, true).show();
                        Log.e("err", response.toString());
                        dialog.dismiss();
                        new AlertDialog.Builder(Checkout.this)
                                .setTitle("Order Failed")
                                .setMessage("Something Went Wrong, Please Try Again Later")
                                .setCancelable(false)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ///
                                    }
                                }).show();
                    }
                    Log.e("err", response.toString());
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security", "secure_code");
                return headers;
            }
        };

        queue.add(request);
    }
}