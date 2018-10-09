package com.life.shelter.people.homeless;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Account extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseTramp;
    private DatabaseReference databaseReg;
    String type, country;
    ListView listViewTrampA;
    List<HomeFirebaseClass> trampList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();


        databaseTramp = FirebaseDatabase.getInstance().getReference("trampoos");
        mStorageRef = FirebaseStorage.getInstance().getReference("trrrrr");

        listViewTrampA = (ListView) findViewById(R.id.list_view_tramp_count);
        trampList = new ArrayList<>();


    }



    @Override
    protected void onStart() {
        super.onStart();
        getRegData();


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
    private void getRegData() {
////import data of country and tope
        databaseReg = FirebaseDatabase.getInstance().getReference("reg_data");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
                country = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ccountry").getValue(String.class);
                maketable();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseReg.addValueEventListener(postListener);
    }
    private void maketable() {

                if (isNetworkConnected()) {
                    if (country != null && type != null) {
                        databaseTramp.child(country).child(type).child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                trampList.clear();

                                for (DataSnapshot acountSnapshot : dataSnapshot.getChildren()) {
                                    HomeFirebaseClass homeTramp = acountSnapshot.getValue(HomeFirebaseClass.class);
                                    trampList.add(0, homeTramp);


                                }
                                TrampHomeAdapter adaptera = new TrampHomeAdapter(Account.this, trampList);
                                listViewTrampA.setAdapter(adaptera);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(Account.this, "please check the network connection", Toast.LENGTH_LONG).show();
                }

    }
}
