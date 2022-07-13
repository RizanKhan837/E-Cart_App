package com.example.e_cartapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_cartapp.databinding.ActivityForgetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class Forget extends AppCompatActivity {

    ActivityForgetBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.forgetBtn.setOnClickListener(v -> {
            validateData();
        });
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void validateData() {
        if (binding.forgetEmail.toString().isEmpty()) {
            Toasty.info(Forget.this, "Email is required.", Toast.LENGTH_SHORT, true).show();
        } else {
            forgetPassword();
        }
    }

    private void forgetPassword() {
        auth.sendPasswordResetEmail(binding.forgetEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toasty.success(Forget.this, "Check Your Mail.", Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(Forget.this, Login.class));
                    finish();
                } else
                    Toasty.warning(Forget.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}