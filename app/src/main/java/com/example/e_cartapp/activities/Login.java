package com.example.e_cartapp.activities;

import static com.example.e_cartapp.activities.SignUp_Page.USER_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseUser mUser;
    String id;
    FirebaseDatabase database;
    LoadingDialog loading;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());   // is used fill the window with the UI provided from layout file

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);

        email = preferences.getString("email", null);
        password = preferences.getString("password", null);

        binding.email.setText(email);
        binding.password.setText(password);

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

        binding.gotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp_Page.class));
            }
        });
    }

    private void login() {
        if (TextUtils.isEmpty(binding.email.getText())) {
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

        auth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(Login.this, "Logged In Successfully!", Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(Login.this, Home_Page.class);
                            String id = task.getResult().getUser().getUid();
                            SharedPreferences.Editor sharedPreferences = getSharedPreferences(USER_ID, MODE_PRIVATE).edit();
                            sharedPreferences.putString("id", id);
                            sharedPreferences.apply();
                            //Toasty.warning(Login.this, ""+ task.getResult().getUser().getUid(), Toast.LENGTH_SHORT, true).show();
                            startActivity(intent);
                            loading.dismiss();
                        } else {
                            Toasty.error(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                            loading.dismiss();
                        }
                    }
                });
    }
}