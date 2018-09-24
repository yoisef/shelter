package com.life.shelter.people.homeless;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
<<<<<<< HEAD
import android.text.Editable;
import android.text.TextWatcher;
=======
>>>>>>> 3e9189e4bae020362d99ead6361c585f7fce3bca
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

<<<<<<< HEAD
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.life.shelter.people.homeless.Databeas.Product;
=======
>>>>>>> 3e9189e4bae020362d99ead6361c585f7fce3bca
import com.life.shelter.people.homeless.recycleadapter.listadapter;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

<<<<<<< HEAD
    RecyclerView serchrecycle;
    RecyclerView.LayoutManager mLayoutManager;
    EditText serchesit ;
    listadapter myadapter;
    SearchView searchView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ArrayList<Product> mylistfinal;

=======
    RecyclerView mylist;
    private RecyclerView.LayoutManager mLayoutManager;
>>>>>>> 3e9189e4bae020362d99ead6361c585f7fce3bca

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mylistfinal=new ArrayList<>();


        myadapter=new listadapter(this);

        reference.child("shelter").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Product info = dataSnapshot.getValue(Product.class);
                mylistfinal.add(info);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        printhasjkey();
        serchrecycle=findViewById(R.id.listhomelessinfo);
        searchView =  findViewById(R.id.search); // inititate a search view
        CharSequence query = searchView.getQuery();
        searchView.setQueryHint("Search for homeless by city");
      //  boolean isIconfied=searchView.isIconfiedByDefault();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
             final   ArrayList<Product> example= mylistfinal;
               ArrayList<Product> example2=new ArrayList<>();

                    text = text.toLowerCase(Locale.getDefault());

                    if (text.length()==0)
                    {
                        myadapter.setmylist(example);
                        myadapter.notifyDataSetChanged();

                    }

                  else {
                        for (Product wp : example) {
                            if (wp.getCity().toLowerCase(Locale.getDefault()).contains(text)) {
                                example2.add(wp);

                            }
                            myadapter.setmylist(example2);
                            myadapter.notifyDataSetChanged();
                        }
                    }
                  //  myadapter.notifyDataSetChanged();



                return false;
            }
        });

        serchrecycle.setHasFixedSize(true);


        mLayoutManager=new LinearLayoutManager(this);
        serchrecycle.setLayoutManager(mLayoutManager);
        serchrecycle.setAdapter(myadapter);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mylist=findViewById(R.id.listhomelessinfo);
        mylist.setHasFixedSize(true);
        mLayoutManager=new LinearLayoutManager(this);
        mylist.setLayoutManager(mLayoutManager);
        mylist.setAdapter(new listadapter(this));

        if(Build.VERSION.SDK_INT>22){
            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[] {READ_EXTERNAL_STORAGE}, 1);

        }
    }


    public void printhasjkey()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.life.shelter.people.homeless",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_account) {
            Intent it = new Intent(home.this, Account.class);
            startActivity(it);
        } else if (id == R.id.nav_about) {
                    Intent it = new Intent(home.this, About.class);
                    startActivity(it);
        } else if (id == R.id.nav_faq) {

            Intent it = new Intent(home.this, FAQ.class);
            startActivity(it);
        } else if (id == R.id.nav_charitable) {

            Intent it = new Intent(home.this, CharitableOrganizations.class);
            startActivity(it);
        } else if (id == R.id.nav_supporting) {

            Intent it = new Intent(home.this, Supporting.class);
            startActivity(it);
        } else if (id == R.id.nav_developers) {

            Intent it = new Intent(home.this, Developers.class);
            startActivity(it);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_rate) {



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    }
