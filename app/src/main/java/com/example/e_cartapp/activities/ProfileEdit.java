package com.example.e_cartapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.e_cartapp.databinding.ActivityProfileEditBinding;
import com.example.e_cartapp.model.UserModel;

import es.dmoral.toasty.Toasty;

public class ProfileEdit extends AppCompatActivity {

    ActivityProfileEditBinding binding;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            userModel = new UserModel(
                    binding.userName.getText().toString(),
                    binding.email.getText().toString(),
                    binding.phoneNo.getText().toString(),
                    binding.address.getText().toString(),
                    binding.city.getText().toString(),
                    binding.country.getText().toString(),
                    null
            );
        }
    }
}