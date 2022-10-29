package com.example.e_cartapp.activities;

import static com.example.e_cartapp.activities.SignUp_Page.USER_ID;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.databinding.ActivityProfileBinding;
import com.example.e_cartapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;

import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding binding;
    String id, personEmail;
    FirebaseDatabase database;
    Uri filepath;
    UserModel userModel;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);
        id = preferences.getString("id", null);
        userModel = getIntent().getExtras().getParcelable("userModel");

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
                startActivity(new Intent(Profile.this, Home_Page.class));
            }
        });

        binding.browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Dexter.withActivity(Profile.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response){
                                Intent intent=new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Please select Image"),1);
                               uploadtofirebase();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null)
        {
            filepath = data.getData();
            try
            {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.profileImage.setImageBitmap(bitmap);
                userModel = new UserModel();
                userModel.setProfileUrl(filepath);
                Glide.with(Profile.this)
                        .load(String.valueOf(filepath))
                        .into(binding.profileImage);
            }catch (Exception ex)
            {
                //Toasty.info(Profile.this, "" + filepath, Toast.LENGTH_SHORT, true).show();
                Toasty.error(Profile.this, "Problem Is Here" + ex.getMessage(), Toast.LENGTH_LONG, true).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void uploadtofirebase(){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("File Uploader");
        dialog.show();

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference uploader=storage.getReference().child(id);
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                        dialog.dismiss();
                        Toasty.success(Profile.this, "File Uploaded", Toast.LENGTH_SHORT, true).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        float percent=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded :"+(int)percent+" %");
                    }
                });
    }
}