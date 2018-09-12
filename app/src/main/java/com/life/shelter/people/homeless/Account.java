package com.life.shelter.people.homeless;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.life.shelter.people.homeless.Databeas.UsersAccount;
import com.life.shelter.people.homeless.recycleadapter.listadapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Account extends AppCompatActivity {


    ImageView uploadphoto;
    private static final int image = 101;
    Uri uriprofileimage,myuri;
    String downloadedurl;
    ProgressBar progressBar;
    RecyclerView mylist;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        uploadphoto=findViewById(R.id.takepic);
        progressBar=findViewById(R.id.myprogressbar);
        mylist=findViewById(R.id.listhomelesinform);
        mylist.setHasFixedSize(true);


        mLayoutManager=new LinearLayoutManager(this);
        mylist.setLayoutManager(mLayoutManager);
        mylist.setAdapter(new listadapter(this));



        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Account.this, UsersAccount.class));

            }
        });
    }




         /*   } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/

}