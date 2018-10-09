package com.life.shelter.people.homeless;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class trampPhotoActivity extends AppCompatActivity {
    ImageView myImage;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramp_photo);
        url = getIntent().getStringExtra("image_url");

        myImage = findViewById(R.id.mylargeImage);
        Glide.with(this).load(url)
                .into(myImage);
    }
}
