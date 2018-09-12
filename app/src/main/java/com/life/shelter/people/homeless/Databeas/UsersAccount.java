package com.life.shelter.people.homeless.Databeas;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.life.shelter.people.homeless.Account;
import com.life.shelter.people.homeless.R;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAccount extends AppCompatActivity {

    CircleImageView homelessPic;
    ProgressBar progressBar;
    String downloadedurl;

    EditText homelessName, homelessAddress, homelessCity;
    Button cancelHomelessInfo, submitHomelessInfo;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Uri img_uri;
    private static final int image = 101;
    private ProgressDialog progressDialog;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_accounts);
        progressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        progressBar=findViewById(R.id.myprogressbar);

        homelessPic = (CircleImageView) findViewById(R.id.profile_image);



        homelessName = (EditText) findViewById(R.id.homeless_name);
        homelessAddress = (EditText) findViewById(R.id.homeless_address);
        homelessCity = (EditText) findViewById(R.id.homeless_city);
        cancelHomelessInfo = (Button) findViewById(R.id.push_button);
        cancelHomelessInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent = new Intent(UsersAccount.this, Account.class);
                homelessName.getText().clear();
                homelessAddress.getText().clear();
                homelessCity.getText().clear();
                homelessPic.setBackgroundResource(R.drawable.user);
                startActivity(cancelIntent);
            }
        });

        submitHomelessInfo = (Button) findViewById(R.id.push_button1);
        submitHomelessInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameText,priceText,quantityText,countryname;
                nameText=homelessName.getText().toString();
                priceText=homelessAddress.getText().toString();
                quantityText=homelessCity.getText().toString();


                if(nameText.isEmpty()||nameText.equals(" "))
                {
                    homelessName.setError("Fill here please !");
                    return;
                }

                if(priceText.isEmpty()||priceText.equals(" "))
                {
                    homelessAddress.setError("Fill here please !");
                    return;
                }

                if(quantityText.isEmpty()||quantityText.equals(" "))
                {
                    homelessCity.setError("Fill here please !");
                    return;
                }

                    Product product =new Product(nameText , priceText , quantityText,downloadedurl);
                    database.getReference().child("shelter").push().setValue(product);








             //   String key = myRef.child("shelter").push().getKey();
             //   product.setId(key);

               // myRef.child("shelter").child(key).setValue(product);

                homelessName.setText("");
                homelessAddress.setText("");
                homelessCity.setText("");

            }

        });
        homelessPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, image);
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == image && resultCode == RESULT_OK){

            //  try {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriprofileimage);
            // uriprofileimage= getImageUri(this,imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataimg = baos.toByteArray();

            homelessPic.setBackground(null);
            homelessPic.setImageBitmap(imageBitmap);
            StorageReference profileimageref = FirebaseStorage.getInstance().getReference("homelesspic/" + System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = profileimageref.putBytes(dataimg);
            progressBar.setVisibility(View.VISIBLE);
            uploadTask.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UsersAccount.this, "not upload", Toast.LENGTH_LONG).show();
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    downloadedurl = downloadUrl.toString();
                    Toast.makeText(UsersAccount.this, "photo uploaded", Toast.LENGTH_LONG).show();
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
            //    uriprofileimage = getImageUri(this, imageBitmap);
            // uploadimagetofirebase();
        }
    }

    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(UsersAccount.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}