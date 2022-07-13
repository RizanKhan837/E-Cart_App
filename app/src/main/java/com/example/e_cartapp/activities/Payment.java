package com.example.e_cartapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.e_cartapp.databinding.ActivityPaymentBinding;
import com.example.e_cartapp.utils.Constants;

public class Payment extends AppCompatActivity {

    ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String orderCode = getIntent().getStringExtra("code");

        binding.webview.setMixedContentAllowed(true);        //2 Ways Of Payment One Is Through Integrating Paypal and VISA SDK's
        binding.webview.loadUrl(Constants.PAYMENT_URL + orderCode);       //Second One Is Through Server Side Just Like Here

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }
}