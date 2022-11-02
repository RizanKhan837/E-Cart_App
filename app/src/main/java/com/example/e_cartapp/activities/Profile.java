package com.example.e_cartapp.activities;

import static com.example.e_cartapp.activities.SignUp_Page.USER_ID;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.e_cartapp.databinding.ActivityProfileBinding;
import com.example.e_cartapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
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
import java.io.Serializable;

import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity implements Serializable {

    ActivityProfileBinding binding;
    String id, personEmail;
    FirebaseDatabase database;
    public static Uri filepath;
    UserModel userModel;
    Bitmap bitmap;
    FirebaseStorage mstorageRef;
    public Uri profileUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);
        id = preferences.getString("id", null);
        //userModel = (UserModel) getIntent().getSerializableExtra("userModel");

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        database = FirebaseDatabase.getInstance();

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
                Intent intent = new Intent(Profile.this, Home_Page.class);
                intent.putExtra("profileUrl", filepath);
                startActivity(intent);
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
                                openFileChooser();
                                Log.e("err", "Running");
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
                Glide.with(Profile.this)
                        .load(String.valueOf(filepath))
                        .into(binding.profileImage);
                uploadFile();
            }catch (Exception ex)
            {
                //Toasty.info(Profile.this, "" + filepath, Toast.LENGTH_SHORT, true).show();
                Toasty.error(Profile.this, "Problem Is Here" + ex.getMessage(), Toast.LENGTH_LONG, true).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void getFirebaseDatabase() {
        mDatabaseRef.get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        }if (!userModel.getEmail().isEmpty()){
                            binding.profileEmail.setText(userModel.getEmail());
                        }
                        if (userModel.getProfileUrl() != null){
                            profileUrl = Uri.parse(userModel.getProfileUrl());
                            Glide.with(Profile.this)
                                    .load(String.valueOf(profileUrl))
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

    private void uploadFile() {
        final ProgressDialog mProgressBar = new ProgressDialog(this);
        //Toasty.error(Profile.this, "Upload Method Run" , Toast.LENGTH_SHORT, true).show();
        Log.e("err", "Running");

        if (filepath != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(filepath));
            //Toasty.error(Profile.this, "Upload Method Not Running" , Toast.LENGTH_SHORT, true).show();
            //Log.e("err", " Not Running");


            fileReference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            mProgressBar.dismiss();
                            Toasty.success(Profile.this, "File Uploaded", Toast.LENGTH_SHORT, true).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference("Users").child(id).child("profileUrl").setValue(uri.toString());
                                    //userModel.setProfileUrl(downloadUrl);
                                    Toasty.success(Profile.this, "File Uploaded", Toast.LENGTH_SHORT, true).show();
                                }
                            });
                            //String uploadId = mDatabaseRef.push().getKey();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(Profile.this, ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toasty.warning(Profile.this, "No File Selected", Toast.LENGTH_SHORT, true).show();
        }
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