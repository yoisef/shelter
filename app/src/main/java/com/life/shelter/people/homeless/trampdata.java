package com.life.shelter.people.homeless;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class trampdata extends AppCompatActivity {
    EditText nameEditText, addressEditText, cityEditText;
    Button buttonSave;
    ImageView photoEdit;
    private ProgressBar progressBar;
    private Uri imagePath;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private StorageReference mStorageRef;
    DatabaseReference databaseTramp;
    DatabaseReference databaseHome;
    DatabaseReference databaseReg;

    String mName, mAddress, mCity, mUserName, mUserPhotoUri, mDate, mUserId;
    String type, country;
    private FirebaseAuth mAuth;

    String mTrampPhotoUrl = "";
    int reloadCount = 0;
    byte[] byteImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trampdata);
        mAuth = FirebaseAuth.getInstance();
        databaseTramp = FirebaseDatabase.getInstance().getReference("trampoos");
        databaseHome = FirebaseDatabase.getInstance().getReference("homedb");
        mStorageRef = FirebaseStorage.getInstance().getReference("trrrrr");
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        nameEditText = (EditText) findViewById(R.id.edit_name);
        addressEditText = (EditText) findViewById(R.id.edit_address);
        cityEditText = (EditText) findViewById(R.id.edit_city);
        buttonSave = (Button) findViewById(R.id.button);
        photoEdit = (ImageView) findViewById(R.id.edit_photo);
        progressBar = findViewById(R.id.progressbar);


        photoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image
                displayImportImageDialog();
            }
        });


        buttonSave.setEnabled(false);

        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonSave.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                Log.v("Data","Start get data");
                getRegData();
            }
        });
        /////////////////////////////////////


    }

    private void getRegData() {
        databaseReg = FirebaseDatabase.getInstance().getReference("reg_data");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                type = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
                country = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ccountry").getValue(String.class);

                if (country != null) {
                    try {
                        addTramp();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (reloadCount < 4) {
                        getRegData();
                        reloadCount++;
                    } else {
                        Toast.makeText(trampdata.this, "an error occurred", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseReg.addValueEventListener(postListener);
    }


    private void addTramp() throws IOException {  //throws IOException because of try & catch vith method above
        mName = nameEditText.getText().toString();
        mAddress = addressEditText.getText().toString();
        mCity = cityEditText.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mUserId = user.getUid();
            Log.v("Data"," 1-User id :"+ mUserId);
            if (user.getPhotoUrl() != null) {
                mUserPhotoUri = user.getPhotoUrl().toString();
            }
            if (user.getDisplayName() != null) {
                mUserName = user.getDisplayName();
            }

            //user name has space not null so i need to reset it to null
            if (user.getDisplayName() == "") {
                mUserName = null;
            }
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mDate = sdf.format(calendar.getTime());


            if ((!TextUtils.isEmpty(mName)) && (!TextUtils.isEmpty(mAddress)) && (!TextUtils.isEmpty(mCity))) {
                Log.v("Image","Upload start");
                uploadImage();

            } else {
                Toast.makeText(this, "you should fill all fields", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                buttonSave.setEnabled(true);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (data.getData() != null) {
                    imagePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                        Bitmap compressedBitmap = getScaledBitmap(bitmap);
                        photoEdit.setImageBitmap(compressedBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byteImageData = baos.toByteArray();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    buttonSave.setEnabled(true);
                }

            } else if (requestCode == CAMERA_REQUEST_CODE) {
                if (data.getData() != null) {
                    imagePath = data.getData();
                   // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                        Bitmap compressedBitmap = getScaledBitmap(bitmap);
                        photoEdit.setImageBitmap(compressedBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byteImageData = baos.toByteArray();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    buttonSave.setEnabled(true);
                }

            }
        }


    }

    private void uploadImage() {

        if (isNetworkConnected()) {

            if (imagePath != null) {
                progressBar.setVisibility(View.VISIBLE);

                StorageReference trampsRef = mStorageRef.child(imagePath.getLastPathSegment());


                trampsRef.putBytes(byteImageData)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBar.setVisibility(View.GONE);

                                mTrampPhotoUrl = taskSnapshot.getDownloadUrl().toString();
                                if (!mTrampPhotoUrl.equals("")) {
                                    Log.v("Image","Upload end");
                                    uploadData();
                                }


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(trampdata.this, "an error occurred while  uploading image", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                buttonSave.setEnabled(true);

                            }
                        });
            }
        } else {
            Toast.makeText(trampdata.this, "please check the network connection", Toast.LENGTH_LONG).show();
        }
    }

    private void displayImportImageDialog() {

        final Dialog dialog = new Dialog(trampdata.this);
        dialog.setContentView(R.layout.import_image_dialog);
        dialog.setTitle("Import image");
        dialog.setCanceledOnTouchOutside(false);

        TextView gallery = (TextView) dialog.findViewById(R.id.gallery_tv);
        TextView openCamera = (TextView) dialog.findViewById(R.id.open_camera_tv);
        TextView cancel = (TextView) dialog.findViewById(R.id.dismiss_dialog);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGalleryAction();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCameraAction();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private void openCameraAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

    }

    private void openGalleryAction() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    private void uploadData() {

        //we here replace code with one has the same id for both home and account activity
        DatabaseReference reference = databaseTramp.push();
        String id = reference.getKey();
        Log.v("Data"," 2-User id :"+ mUserId);
        HomeFirebaseClass homefirebaseclass = new HomeFirebaseClass(id, mName, mAddress, mCity, mTrampPhotoUrl,
                mUserPhotoUri, mUserName, mDate, mUserId);

        // Database for Account Activity
        databaseTramp.child(country).child("Individiual")
                .child("users").child(mAuth.getCurrentUser().getUid())
                .child(id).setValue(homefirebaseclass);
        // Database for Home Activity
        databaseHome.child(country).child(id).setValue(homefirebaseclass);
        Log.v("Data"," 3-User id :"+ mUserId);

        Toast.makeText(this, "tramp data saved", Toast.LENGTH_LONG).show();
        nameEditText.setText("");
        addressEditText.setText("");
        cityEditText.setText("");

        Intent intent2 = new Intent(trampdata.this, home.class);
        progressBar.setVisibility(View.GONE);

        // Start the new activity
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent2);

    }

    //resize image
    private Bitmap getScaledBitmap(Bitmap bm) {

        int width = 0;

        try {
            width = bm.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bm.getHeight();
        int bounding = dpToPx(250);

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoEdit.getLayoutParams();
        params.width = width;
        params.height = height;
        photoEdit.setLayoutParams(params);

        return scaledBitmap;

    }

    // convert dp to pixel
    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        } else {
            return false;
        }
    }
}

