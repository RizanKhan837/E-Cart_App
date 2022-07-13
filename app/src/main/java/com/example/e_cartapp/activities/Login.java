package com.example.e_cartapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_cartapp.databinding.ActivityLoginBinding;
import com.example.e_cartapp.utils.GoogleSignin;
import com.example.e_cartapp.utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseUser mUser;
    String useremail, userpassword;
    LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());   // is used fill the window with the UI provided from layout file

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

        loading = new LoadingDialog(Login.this, "Signing In");

        binding.forget.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Forget.class));
        });

        binding.signinBtn.setOnClickListener(v -> {
            login();
        });

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
        binding.googleBtn.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, GoogleSignin.class));
        });
    }

    private void login() {
        if (TextUtils.isEmpty(binding.userName.getText())) {
            Toasty.warning(Login.this, "Please enter your email!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(binding.password.getText())) {
            Toasty.warning(Login.this, "Please enter your password!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.password.length() < 6) {
            Toasty.warning(Login.this, "Password length must be greater than 6 letters.", Toast.LENGTH_SHORT, true).show();
            return;
        }
        loading.show();
        auth.signInWithEmailAndPassword(binding.userName.getText().toString(), binding.password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(Login.this, "Logged In Successfully!", Toast.LENGTH_SHORT, true).show();
                            startActivity(new Intent(Login.this, Home_Page.class));
                            loading.dismiss();
                        } else {
                            Toasty.error(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                            loading.dismiss();
                        }
                    }
                });
    }
}