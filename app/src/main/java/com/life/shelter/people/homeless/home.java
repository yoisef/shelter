package com.life.shelter.people.homeless;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.life.shelter.people.homeless.util.IabHelper;
import com.life.shelter.people.homeless.util.IabResult;
import com.life.shelter.people.homeless.util.Inventory;
import com.life.shelter.people.homeless.util.Purchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseTramp;
    private DatabaseReference databaseReg;
    String type,country;
    ListView listViewTramp;
    SearchView searchView;
    Button addTrampButton;
    private ProgressBar progressBar;
    List<HomeFirebaseClass> trampList;
    //-------------------التبرع--------------------------
    private static final String TAG = "InAppBilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "com.salim3dd.btnclickme";//القيمة هنا تتغير مثل حساب المطور
    //--------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addTrampButton = (Button) findViewById(R.id.add_tramp_h);
        progressBar = (ProgressBar) findViewById(R.id.home_progress_bar);
        mAuth = FirebaseAuth.getInstance();

        databaseTramp = FirebaseDatabase.getInstance().getReference("homedb");
        databaseReg = FirebaseDatabase.getInstance().getReference("reg_data");
        mStorageRef = FirebaseStorage.getInstance().getReference("trrrrr");
        listViewTramp= (ListView)findViewById(R.id.list_view_tramp);
        searchView = (SearchView) findViewById(R.id.search);
        trampList=new ArrayList<>();
        listViewTramp.setTextFilterEnabled(true);
        removeFocus();
        //--------------------------------التبرع---------------------------------
      //  TrampHomeAdapter adapter = new TrampHomeAdapter(home.this, trampList);
    // final ImageView aDonateLogo = (ImageView) listViewTramp.findViewById(R.id.donate);
        ImageView  aDonateLogo = (ImageView) findViewById(R.id.donate);
        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhW5Q0OlZ9BW3rtylifmWcLabwamc/ztz8PfrxFttxoO44gynEigZbZgczvjz2uNqjtoGMK1I83nxPH7+qZnwyOY5ih9M6o/8MicnKd6yq2/4NwLD1eQxNr9E0J0RT00mj8JWiPGrwO3rDGu61s4o99CdaJRdRVzjnY/QNs0H2idXT12cbGdnIia8OEWQvE+SuHV6QN4Ofdu/drus/REnIHNPiXyZAlXmwezrQxatL6xJ95jJnTZtG1WlDsvbvAKQsHkRFAVLJFTzflgzYkMeujjDO+gIlBQ/iUHlkKg24TBWXRZAOinSlxLN2/zEd3ERJ8ex0pCIvJkgAI3aVcF74QIDAQAB";
///القيمة هنا تتغير مثل حساب المطور
        mHelper =  new IabHelper(this,base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });

        //----------------------------------------------------------------------
        //Add to Activity
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        addTrampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent it = new Intent(home.this, trampdata.class);
                startActivity(it);
            }
        });

        //pour cela, on commence par regarder si on a déjà des éléments sauvegardés
        aDonateLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Random Rand = new Random();

                int Rndnum = Rand.nextInt(10000) + 1;
                mHelper.launchPurchaseFlow(home.this, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "token-" + Rndnum);
            }
        });

///////////////////////////////////////////////////////////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////////////////////////////////////////
    }
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        getRegData();
    }

    ///////////////////////////////////////////////////////////////////

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

        } else if (id == R.id.nav_org) {
            Intent it = new Intent(home.this, displayOrganizations.class);
            startActivity(it);

        } else if (id == R.id.nav_rate) {

        }else if (id == R.id.nav_profile) {
            Intent it = new Intent(home.this, ProfileActivity.class);
            startActivity(it);


        }else if (id == R.id.nav_out) {
            mAuth.getInstance().signOut();
            Intent it = new Intent(home.this, Login.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            finish();
            startActivity(it);
        }
        else if (id == R.id.aDonate) {
            Random Rand = new Random();

            int Rndnum = Rand.nextInt(10000) + 1;
            mHelper.launchPurchaseFlow(home.this, ITEM_SKU, 10001,
                    mPurchaseFinishedListener, "token-" + Rndnum);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
////////////////////////////////////////////////////////////////////////
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
            if(country != null &&  type != null) {


                if (type.equals("Organization")){
                    addTrampButton.setVisibility(View.GONE);
                } else {
                    addTrampButton.setVisibility(View.VISIBLE);
                }
                //databaseTramp.child(country).child("Individiual").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                databaseTramp.child(country).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        trampList.clear();

                        //for (DataSnapshot userid : dataSnapshot.getChildren()) {

                            //for (DataSnapshot userdataSnapshot : userid.getChildren()) {
                            for (DataSnapshot userdataSnapshot : dataSnapshot.getChildren()) {

                                String cId = userdataSnapshot.child("cId").getValue(String.class);
                                String hname = userdataSnapshot.child("cName").getValue(String.class);
                                String haddress = userdataSnapshot.child("cAddress").getValue(String.class);
                                String hcity = userdataSnapshot.child("cCity").getValue(String.class);
                                String huri = userdataSnapshot.child("cUri").getValue(String.class);
                                String huseruri = userdataSnapshot.child("userUri").getValue(String.class);
                                String husername = userdataSnapshot.child("username").getValue(String.class);
                                String hpdate = userdataSnapshot.child("pdate").getValue(String.class);
                                String huserid = userdataSnapshot.child("userId").getValue(String.class);
                                Boolean checked = userdataSnapshot.child("checked").getValue(Boolean.class);
                                String organizationId = userdataSnapshot.child("organizationId").getValue(String.class);
                                String organizationName = userdataSnapshot.child("organizationName").getValue(String.class);

                                HomeFirebaseClass hometramp = new HomeFirebaseClass(cId, hname, haddress, hcity, huri,
                                        huseruri, husername, hpdate,huserid, checked,organizationId,organizationName);
                                //trampList.add(hometramp);
                                trampList.add(0, hometramp);
                            }
                           // }
                        //}
                        TrampHomeAdapter adapter = new TrampHomeAdapter(home.this, trampList);
                        //adapter.notifyDataSetChanged();
                        listViewTramp.setAdapter(adapter);
                        setupSearchView();
                        progressBar.setVisibility(View.GONE);
                       // listViewTramp.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        } else {
            Toast.makeText(home.this, "please check the network connection", Toast.LENGTH_LONG).show();
        }
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    listViewTramp.clearTextFilter();
                } else {
                    listViewTramp.setFilterText(newText);
                }
                return true;
            }
        });
    }

    private void removeFocus() {
        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
    //------------------------التبرع--------------------------
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
            }

        }
    };
    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {


                    } else {
                        // handle error
                    }
                }
            };


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

//---------------------------------------------

}
