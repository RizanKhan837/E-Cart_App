package com.example.e_cartapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.databinding.ActivityProfileBinding;
import com.example.e_cartapp.model.UserModel;
import com.example.e_cartapp.utils.GoogleSignin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding binding;
    String id, personEmail;
    Uri personPhoto;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getIntent().getStringExtra("uid");

        getFirebaseDatabase();

        if (userModel.equals(null)){
            Toasty.warning(Profile.this, "User Doesn't Exist", Toast.LENGTH_SHORT, true).show();
        }
        if (!userModel.getName().isEmpty()){
            binding.profileName.setText(userModel.getName());
        }
        if (!userModel.getEmail().isEmpty()){
            binding.email.setText(userModel.getEmail());
        }
        if (userModel.getProfileUrl() != null){
            Glide.with(this)
                    .load(String.valueOf(userModel.getProfileUrl()))
                    .into(binding.profileImage);
        }
        if (!userModel.getAddress().isEmpty()){
            binding.streetAddress.setText(userModel.getAddress());
        }
        if (!userModel.getPhone().isEmpty()){
            binding.phone.setText(userModel.getPhone());
        }
        if (!userModel.getCity().isEmpty()){
            binding.city.setText(userModel.getCity());
        }
        if (!userModel.getCountry().isEmpty()){
            binding.country.setText(userModel.getCountry());
        }

        binding.addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.plusBtn.playAnimation();
                startActivity(new Intent(Profile.this, ProfileEdit.class));
            }
        });


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

    void getFirebaseDatabase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        userModel = dataSnapshot.getValue(UserModel.class);
                    } else {
                        Toasty.error(Profile.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.error(Profile.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }
}