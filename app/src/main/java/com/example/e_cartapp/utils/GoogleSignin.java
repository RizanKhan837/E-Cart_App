package com.example.e_cartapp.utils;

import static com.example.e_cartapp.activities.SignUp_Page.USER_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.e_cartapp.R;
import com.example.e_cartapp.activities.Home_Page;
import com.example.e_cartapp.activities.Login;
import com.example.e_cartapp.databinding.ActivityGoogleSigninBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class GoogleSignin extends Login {

    ActivityGoogleSigninBinding binding;
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    LoadingDialog loadingDialog;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleSigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new LoadingDialog(GoogleSignin.this, "Google Sign In...");
        loadingDialog.show();
        database = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                 account = GoogleSignIn.getLastSignedInAccount(this);
                firebaseAuthWithGoogle(account.getIdToken());

                //startActivity(new Intent(GoogleSignin.this, Home_Page.class));
                //Toasty.success(GoogleSignin.this, "Success!", Toast.LENGTH_SHORT, true).show();
                loadingDialog.dismiss();
            } catch (ApiException e) {
                Toasty.error(GoogleSignin.this, "" + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                finish();
                loadingDialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    SharedPreferences.Editor sharedPreferences = getSharedPreferences(USER_ID, MODE_PRIVATE).edit();
                    id = task.getResult().getUser().getUid();
                    sharedPreferences.putString("id", id);
                    sharedPreferences.commit();

                    Toasty.success(GoogleSignin.this, "" +id, Toast.LENGTH_SHORT, true).show();

                    Intent intent = new Intent(GoogleSignin.this, Home_Page.class);
                    startActivity(intent);
                    Toasty.success(GoogleSignin.this, "Success!", Toast.LENGTH_SHORT, true).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Toasty.warning(GoogleSignin.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                    finish();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        startActivity(new Intent(GoogleSignin.this, Home_Page.class));
        user.getDisplayName();
    }

}