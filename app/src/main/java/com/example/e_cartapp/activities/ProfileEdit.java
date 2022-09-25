package com.example.e_cartapp.activities;

import static com.example.e_cartapp.activities.SignUp_Page.USER_ID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.e_cartapp.databinding.ActivityProfileEditBinding;
import com.example.e_cartapp.model.UserModel;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class ProfileEdit extends AppCompatActivity {

    ActivityProfileEditBinding binding;
    UserModel userModel;
    FirebaseDatabase database;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);
        id = preferences.getString("id", null);

        database = FirebaseDatabase.getInstance();

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUser();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void EditUser(){
        if (binding.userName.getText().toString().isEmpty()) {
            Toasty.warning(ProfileEdit.this, "Please enter your name!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.email.getText().toString().isEmpty()) {
            Toasty.warning(ProfileEdit.this, "Please enter your email!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.phoneNo.getText().toString().isEmpty()) {
            Toasty.warning(ProfileEdit.this, "Please enter phone number!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.address.getText().toString().isEmpty()) {
            Toasty.warning(ProfileEdit.this, "Please enter your address!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.country.getText().toString().isEmpty()) {
            Toasty.warning(ProfileEdit.this, "Please enter your country!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.city.getText().toString().isEmpty()) {
            Toasty.warning(ProfileEdit.this, "Please enter city name!", Toast.LENGTH_SHORT, true).show();
            return;
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            userModel = new UserModel(
                                    binding.userName.getText().toString(),
                                    binding.email.getText().toString(),
                                    binding.phoneNo.getText().toString(),
                                    binding.address.getText().toString(),
                                    binding.city.getText().toString(),
                                    binding.country.getText().toString(),
                                    userModel.getProfileUrl()
                            );
                            database.getReference("Users").child(id).setValue(userModel);
                            Toasty.success(ProfileEdit.this, "Editing Successful", Toast.LENGTH_SHORT, true).show();
                            startActivity(new Intent(ProfileEdit.this, Profile.class));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();


        }
    }
}