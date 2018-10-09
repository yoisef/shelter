package com.life.shelter.people.homeless;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditDataActivity extends AppCompatActivity {
    private EditText nameEditText, addressEditText, cityEditText;
    private Button updateBtn;
    private ImageView photo;
    private ProgressBar progressBar;
    private DatabaseReference databaseTramp;
    private DatabaseReference databaseHome;
    private HomeFirebaseClass data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_activity);
        // get intent data
        Intent intent = getIntent();
        data = (HomeFirebaseClass) intent.getSerializableExtra("data");
        databaseTramp = FirebaseDatabase.getInstance().getReference("trampoos");
        databaseHome = FirebaseDatabase.getInstance().getReference("homedb");

        // link view
        nameEditText = findViewById(R.id.edit_data_name);
        addressEditText = findViewById(R.id.edit_data_address);
        cityEditText = findViewById(R.id.edit_data_city);
        updateBtn = findViewById(R.id.edit_data_button);
        photo = findViewById(R.id.edit_data_photo);
        progressBar= findViewById(R.id.progressbar);

        // set view data
        nameEditText.setText(data.getcName());
        addressEditText.setText(data.getcAddress());
        cityEditText.setText(data.getcCity());
        Glide.with(getApplicationContext())
                .load(data.getcUri())
                .apply(RequestOptions.circleCropTransform())
                .into(photo);

        // update button listener
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show progress Bar
                progressBar.setVisibility(View.VISIBLE);
                //
                getRegData();
            }
        });
    }

    private void getRegData() {
        ////import data of country and tope
        DatabaseReference databaseReg = FirebaseDatabase.getInstance().getReference("reg_data");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String type = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
               String country = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ccountry").getValue(String.class);
                    updateData(type,country);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseReg .addValueEventListener(postListener);
    }

    private void updateData(String type, String country){
        // get view data
        String mName = nameEditText.getText().toString();
        String mAddress = addressEditText.getText().toString();
        String mCity = cityEditText.getText().toString();
        // set edit date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String postdate = sdf.format(calendar.getTime());
        // check if all field filled
        if ((!TextUtils.isEmpty(mName)) && (!TextUtils.isEmpty(mAddress)) && (!TextUtils.isEmpty(mCity))) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String id = data.getcId();
           // String ownerId = data.getUserId();
            String ownerId = mAuth.getCurrentUser().getUid();
            String userName = data.getcName();
            String userphotoUri = data.getUserUri();
            String photoUrl = data.getcUri();
            HomeFirebaseClass homefirebaseclass = new HomeFirebaseClass(id,mName, mAddress, mCity, photoUrl,
                    userphotoUri, userName, postdate, ownerId);
            // save data to database
            // Tramp database for Account Activity
            databaseTramp.child(country).child(type)
                    //.child("users").child(homefirebaseclass.getUserId())
                    .child("users").child(mAuth.getCurrentUser().getUid())
                    .child(id).setValue(homefirebaseclass);
            // Home database for Home Activity
            databaseHome.child(country).child(id).setValue(homefirebaseclass);
            // hide progress Bar
            progressBar.setVisibility(View.GONE);
            // success message
            Toast.makeText(EditDataActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
            // clean view data
            nameEditText.setText("");
            addressEditText.setText("");
            cityEditText.setText("");
            // move to home Page
            Intent intent = new Intent(EditDataActivity.this, home.class);
            // Start the new activity
            startActivity(intent);
        }
        else
            Toast.makeText(EditDataActivity.this, "you should fill all fields", Toast.LENGTH_SHORT).show();

    }
}
