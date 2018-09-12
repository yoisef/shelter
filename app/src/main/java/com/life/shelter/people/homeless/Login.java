package com.life.shelter.people.homeless;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import custom_font.MyTextView;

public class Login extends AppCompatActivity {
    TextView shelter;
    MyTextView create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        create = (MyTextView)findViewById(R.id.create);
        shelter = (TextView)findViewById(R.id.title_login);

        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
        shelter.setTypeface(custom_fonts);


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this, Login.class);
                startActivity(it);
            }
        });

    }
}
