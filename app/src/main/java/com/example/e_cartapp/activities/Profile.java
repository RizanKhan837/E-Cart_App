package com.example.e_cartapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.databinding.ActivityProfileBinding;
import com.example.e_cartapp.databinding.ActivitySearchBinding;

import io.ak1.OnBubbleClickListener;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding binding;
    String personName, personEmail;
    Uri personPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        personName = getIntent().getStringExtra("name");
        personEmail = getIntent().getStringExtra("email");
        personPhoto = getIntent().getData();

        binding.profileName.setText(personName);
        binding.profileEmail.setText(personEmail);
        Glide.with(this)
                .load(String.valueOf(personPhoto))
                .into(binding.profileImage);

        /*binding.bubbleTabBar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {
                switch (i){
                    case 0:
                    case 1:
                        startActivity(new Intent(Profile.this, Home_Page.class));
                        break;
                    case 2:
                        startActivity(new Intent(Profile.this, CartActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(Profile.this, Profile.class));
                        break;
                }
            }
        });*/
    }
}