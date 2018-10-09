package com.life.shelter.people.homeless;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    EditText nameEditUser;
    Button buttonsaveuser;
    ImageView photoUserEdit;
    private ProgressBar progressBarUser;
    private Uri imagepathUser;
    public static final int PICK_IMAGE_user = 1;

    private FirebaseAuth mAuth;

    String ss="a";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();


        nameEditUser= (EditText)findViewById(R.id.user_name);

        buttonsaveuser= (Button) findViewById(R.id.button_save_user);
        photoUserEdit=(ImageView) findViewById(R.id.user_photo);
        progressBarUser= findViewById(R.id.progressbar_user);


        photoUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image
                showFileShooserForUser();
            }
        });


        buttonsaveuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                saveuserinfo();
            }
        });

        loadUserInfo();
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if(user.getPhotoUrl() != null){
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(photoUserEdit);
            }
            if(user.getDisplayName() != null){
                nameEditUser.setText(user.getDisplayName());
            }

        }
    }

    private void saveuserinfo() {
        String displayiedusername = nameEditUser.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null  && imagepathUser != null) {
            if (displayiedusername.isEmpty()) {
                nameEditUser.setError("User name is required");
                nameEditUser.requestFocus();
                return;}
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayiedusername )
                    .setPhotoUri(Uri.parse(ss))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "profile updated", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });

        }else{}
    }

    private void showFileShooserForUser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_user);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_user && resultCode == RESULT_OK && data != null&& data.getData() != null) {
            imagepathUser=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepathUser);
                photoUserEdit.setImageBitmap(bitmap);
                uploadimage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadimage(){
        if (imagepathUser != null) {
            progressBarUser.setVisibility(View.VISIBLE);

            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("profilepics/pro.jpg");

            mStorageRef.putFile(imagepathUser)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBarUser.setVisibility(View.GONE);

                            ss = taskSnapshot.getDownloadUrl().toString();

                            Toast.makeText(ProfileActivity.this, "image uploaded", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressBarUser.setVisibility(View.GONE);

                            Toast.makeText(ProfileActivity.this, "error ocoured while  uploading image", Toast.LENGTH_LONG).show();

                        }
                    });
        }else {                            Toast.makeText(ProfileActivity.this, "error ocoured while  uploading image", Toast.LENGTH_LONG).show();
        }
    }
}

    