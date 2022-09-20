package com.example.e_cartapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_cartapp.databinding.ActivitySignUpPageBinding;
import com.example.e_cartapp.model.UserModel;
import com.example.e_cartapp.utils.GoogleSignin;
import com.example.e_cartapp.utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class SignUp_Page extends AppCompatActivity {

    ActivitySignUpPageBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser mUser;
    UserModel userModel;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        loadingDialog = new LoadingDialog(SignUp_Page.this, "Signing Up...");

        binding.signupBtn.setOnClickListener(v -> {
            createUsers();
        });
        binding.gotoSignin.setOnClickListener(v -> {
            startActivity(new Intent(SignUp_Page.this, Login.class));
        });

        binding.googleBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUp_Page.this, GoogleSignin.class));
        });
    }

    private void createUsers() {
        if (TextUtils.isEmpty(binding.userName.getText())) {
            Toasty.warning(SignUp_Page.this, "Please enter your name!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(binding.email.getText())) {
            Toasty.warning(SignUp_Page.this, "Please enter your email!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(binding.password.getText())) {
            Toasty.warning(SignUp_Page.this, "Please enter your password!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(binding.phone.getText())) {
            Toasty.warning(SignUp_Page.this, "Please enter your number!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (binding.password.length() < 6) {
            Toasty.warning(SignUp_Page.this, "Password length must be greater than 6 letters!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        //LOGIN AUTHENTICATION VIA EMAIL
        //Create user
        loadingDialog.show();
        auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userModel = new UserModel(
                            binding.userName.getText().toString(),
                            binding.email.getText().toString(),
                            binding.phone.getText().toString(),
                            "House # 1234 Your Town Etc",
                            "City",
                            "Country",
                            null
                            );
                    database.getReference("Users").child(task.getResult().getUser().getUid()).setValue(userModel);
                    Toasty.success(SignUp_Page.this, "Registration Successful", Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(SignUp_Page.this, Home_Page.class);
                    intent.putExtra("email", task.getResult().getUser().getEmail());
                    intent.putExtra("uid", task.getResult().getUser().getUid());
                    startActivity(intent);
                    loadingDialog.dismiss();
                } else {
                    Toasty.error(SignUp_Page.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                    loadingDialog.dismiss();
                }
            }
        });
    }
}
