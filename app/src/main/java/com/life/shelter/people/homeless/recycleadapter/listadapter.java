package com.life.shelter.people.homeless.recycleadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class listadapter extends RecyclerView.Adapter<listadapter.viewholder> {

    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ArrayList<Product> mylist = new ArrayList<>();
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

        holder.tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();

                if (mylist.get(pos).getDownloadimgurl()!=null) {
                    new DownloadImage().execute(mylist.get(pos).getDownloadimgurl());
                    File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file = new File(path, "DemoPicture.jpg");
                    String imageFullPath = file.getAbsolutePath();

                    TweetComposer.Builder builder = new TweetComposer.Builder(context)
                            .text("حالة تحتاج الي مساعدة" + "\n"
                                    + mylist.get(pos).getName()+ "\n"
                                    + mylist.get(pos).getAddress() + "\n"
                                    + mylist.get(pos).getCity())//any sharing text here
                            .image(Uri.parse(imageFullPath));
                    builder.show();

                } else {

                    String tweetUrl = "https://twitter.com/intent/tweet?text="
                            + "حالة تحتاج الي مساعدة" + "\n"
                            + mylist.get(pos).getName()+ "\n"
                            + mylist.get(pos).getAddress()+ "\n"
                            + mylist.get(pos).getCity()+ "\n"
                            + "&url="
                            + Uri.parse("https://goo.gl/images/Ajo63W");
                    Uri uri = Uri.parse(tweetUrl);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));

                }
            }
        });

        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();

                if (mylist.get(pos).getDownloadimgurl()!=null) {
                    new DownloadImage().execute(mylist.get(pos).getDownloadimgurl());
                    File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file = new File(path, "DemoPicture.jpg");
                    Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this

                    emailIntent.setType("image/*");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "حالة تحتاج الي مساعدة");
                    // + "\n\r" + "\n\r" +
                    // feed.get(Selectedposition).DETAIL_OBJECT.IMG_URL
                    emailIntent.putExtra(Intent.EXTRA_TEXT,  mylist.get(pos).getName()+ "\n"
                            + mylist.get(pos).getAddress()+ "\n"
                            + mylist.get(pos).getCity());
                    emailIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(emailIntent);
                    }
//                    context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }else{
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this

                    emailIntent.setType("image/*");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "حالة تحتاج الي مساعدة");
                    // + "\n\r" + "\n\r" +
                    // feed.get(Selectedposition).DETAIL_OBJECT.IMG_URL
                    emailIntent.putExtra(Intent.EXTRA_TEXT,  mylist.get(pos).getName()+ "\n"
                            + mylist.get(pos).getAddress()+ "\n"
                            + mylist.get(pos).getCity());
                    if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(emailIntent);
                    }
                }
            }
        });

    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                //Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            //Toast.makeText(context, "onPostExecute", Toast.LENGTH_SHORT).show();
            createExternalStoragePrivatePicture(result);
        }
    }


    void createExternalStoragePrivatePicture(Bitmap b) {

        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");

        try {
            //@SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.camera);
            OutputStream os = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.flush();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
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
            tw=itemView.findViewById(R.id.twitter);
            email=itemView.findViewById(R.id.email);

        }
    }
}
