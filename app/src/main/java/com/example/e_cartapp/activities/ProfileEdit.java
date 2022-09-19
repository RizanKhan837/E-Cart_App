package com.example.e_cartapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.e_cartapp.databinding.ActivityProfileBinding;
import com.example.e_cartapp.databinding.ActivityProfileEditBinding;

public class ProfileEdit extends AppCompatActivity {

    ActivityProfileEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}