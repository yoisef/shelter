package com.life.shelter.people.homeless.recycleadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.manager.TargetTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.life.shelter.people.homeless.About;
import com.life.shelter.people.homeless.Databeas.Product;
import com.life.shelter.people.homeless.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class listadapter extends RecyclerView.Adapter<listadapter.viewholder> {

    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ArrayList<Product> mylist = new ArrayList<>();
    ArrayList<Product> listnew;
    ShareDialog shareDialog;
    Target target;
    CallbackManager callbackManager;
    Bitmap bitmap;


    public listadapter(Context con) {
        this.context = con;
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        shareDialog = new ShareDialog((Activity) context);
        callbackManager = CallbackManager.Factory.create();

        reference.child("shelter").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Product info = dataSnapshot.getValue(Product.class);
                mylist.add(info);
                notifyDataSetChanged();
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






    }

    public ArrayList<Product> getmylist()
    {
       return this.mylist;
    }
    public void setmylist(ArrayList<Product> se)
    {
        this.mylist=se;

    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.list_item_shelters,parent,false);
        viewholder holder=new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final viewholder holder,  int position) {

        holder.city.setText(mylist.get(position).getCity());
        holder.name.setText(mylist.get(position).getName());
        holder.address.setText(mylist.get(position).getAddress());
        if (mylist.get(position).getDownloadimgurl() != null) {
            Glide.with(context)
                    .load(mylist.get(position).getDownloadimgurl())
                    .into(holder.image);
        } else {
            holder.image.setBackgroundResource(R.drawable.user);
        }
        holder.fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 int pos = holder.getAdapterPosition();

                   if (mylist.get(pos).getDownloadimgurl()!=null)
                   {
                       if (ShareDialog.canShow(ShareLinkContent.class)) {
                       ShareLinkContent linkContent = new ShareLinkContent.Builder()
                               .setImageUrl(Uri.parse(mylist.get(pos).getDownloadimgurl()))
                               .setContentUrl(Uri.parse(mylist.get(pos).getDownloadimgurl()))


                               .setQuote("حالة تحتاج الي مساعدة"+"\n" +mylist.get(pos).getName() + "\n" +
                                       mylist.get(pos).getAddress() +"\n" +
                                       mylist.get(pos).getCity())
                               .build();
                       shareDialog.show(linkContent);
                   }
                   }
                   else {
                       if (ShareDialog.canShow(ShareLinkContent.class)) {
                           ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                  // .setImageUrl(Uri.parse(mylist.get(pos).getDownloadimgurl()))
                                   .setContentUrl(Uri.parse("https://goo.gl/images/Ajo63W"))


                                   .setQuote("حالة تحتاج الي مساعدة"+"\n" +mylist.get(pos).getName() + "\n" +
                                           mylist.get(pos).getAddress() + "\n" +
                                           mylist.get(pos).getCity())
                                   .build();
                           shareDialog.show(linkContent);
                       }

                   }



                }


        });


        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }





    @Override
    public int getItemCount() {
        return mylist.size();
    }




        public static void activityResult(int requestCode, int resultCode, Intent data){

        //right your code here .
    }

    class viewholder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name , address ,city;
        ImageButton fb,tw,email,donate;

        public viewholder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.homname);
            image=itemView.findViewById(R.id.homimg);
            address=itemView.findViewById(R.id.homeaddress);
            city=itemView.findViewById(R.id.homecity);
            fb=itemView.findViewById(R.id.facebook);

        }
    }
}
