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

import java.util.ArrayList;
import java.util.List;

public class displayOrganizations extends AppCompatActivity {
    private  FirebaseAuth mAuth;
   private   DatabaseReference databaseTramp;
    private  DatabaseReference databaseReg;
    String   type,country;
    ListView listViewTramp;
    List<HomeFirebaseClass> trampList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_organizations);
        mAuth = FirebaseAuth.getInstance();

        databaseTramp= FirebaseDatabase.getInstance().getReference("trampoos");
        listViewTramp= (ListView)findViewById(R.id.list_view_tramp_org);
        trampList=new ArrayList<>();

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
        databaseReg .addValueEventListener(postListener);
    }
    private void maketable() {

        if (isNetworkConnected()) {
            if (country != null && type != null) {
                databaseTramp.child(country).child("Organization").child("users").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        trampList.clear();

                        for (DataSnapshot userId : dataSnapshot.getChildren()) {

                            for (DataSnapshot userdataSnapshot : userId.getChildren()) {

                                String cId = userdataSnapshot.child("cId").getValue(String.class);
                                String hName = userdataSnapshot.child("cName").getValue(String.class);
                                String hAddress = userdataSnapshot.child("cAddress").getValue(String.class);
                                String hCity = userdataSnapshot.child("cCity").getValue(String.class);
                                String hUri = userdataSnapshot.child("cUri").getValue(String.class);
                                String hUserUri = userdataSnapshot.child("userUri").getValue(String.class);
                                String hUserName = userdataSnapshot.child("username").getValue(String.class);
                                String hDate = userdataSnapshot.child("pdate").getValue(String.class);
                                String hUserId = userdataSnapshot.child("userid").getValue(String.class);
                                Boolean checked = userdataSnapshot.child("checked").getValue(Boolean.class);
                                String organizationId = userdataSnapshot.child("organizationId").getValue(String.class);
                                String organizationName = userdataSnapshot.child("organizationName").getValue(String.class);



                                HomeFirebaseClass homeTramp = new HomeFirebaseClass(cId ,hName, hAddress, hCity, hUri, hUserUri, hUserName, hDate, hUserId,checked,organizationId,organizationName);
                                trampList.add(0, homeTramp);
                            }
                        }
                        TrampHomeAdapter adapter = new TrampHomeAdapter(displayOrganizations.this, trampList);
                        //adapter.notifyDataSetChanged();
                        listViewTramp.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }
        } else {
            Toast.makeText(displayOrganizations.this, "please check the network connection", Toast.LENGTH_LONG).show();

        }
    }}
