package com.life.shelter.people.homeless.recycleadapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.life.shelter.people.homeless.Databeas.Product;
import com.life.shelter.people.homeless.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class listadapter extends RecyclerView.Adapter<listadapter.viewholder> {

    Context context;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference=database.getReference();
    ArrayList<Product> mylist=new ArrayList<>();


    public listadapter(Context con)
    {
       this.context=con;

       reference.child("shelter").addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                  Product info=dataSnapshot.getValue(Product.class);
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

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.list_item_shelters,parent,false);
        viewholder holder=new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(viewholder holder, int position) {

        holder.city.setText(mylist.get(position).getCity());
        holder.name.setText(mylist.get(position).getName());
        if (mylist.get(position).getDownloadimgurl()!=null)
        {
            Glide.with(context)
                    .load(mylist.get(position).getDownloadimgurl())
                    .into(holder.image);
        }
        else
        {
            holder.image.setBackgroundResource(R.drawable.user);
        }

    }

    @Override
    public int getItemCount() {
        return mylist.size();
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

        }
    }
}
