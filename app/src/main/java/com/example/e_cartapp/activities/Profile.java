package com.example.e_cartapp.activities;

import static com.example.e_cartapp.activities.SignUp_Page.USER_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.databinding.ActivityProfileBinding;
import com.example.e_cartapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding binding;
    String id, personEmail;
    FirebaseDatabase database;
    Uri personPhoto;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);
        id = preferences.getString("id", null);

        getFirebaseDatabase();

        binding.addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.plusBtn.playAnimation();
                Intent intent = new Intent(Profile.this, ProfileEdit.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        database.getReference("Users").child(id)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        userModel = dataSnapshot.getValue(UserModel.class);
                        if (!userModel.getName().isEmpty()){
                            binding.profileName.setText(userModel.getName());
                        }
                        if (!userModel.getEmail().isEmpty()){
                            binding.email.setText(userModel.getEmail());
                        }
                        if (userModel.getProfileUrl() != null){
                            Glide.with(Profile.this)
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