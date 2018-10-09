package com.life.shelter.people.homeless;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import custom_font.MyEditText;
import custom_font.MyTextView;

public class Login extends AppCompatActivity {
    TextView shelter;


    private MyTextView singin, create, forget;
    private MyEditText editTextemail, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
   // String TAG = "TECSTORE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        shelter = (TextView)findViewById(R.id.title_login);
        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
        shelter.setTypeface(custom_fonts);

        mAuth = FirebaseAuth.getInstance();

        editTextemail = findViewById(R.id.edit_email);
        editTextPassword = findViewById(R.id.edit_password);
        singin = findViewById(R.id.getstarted);
        create = findViewById(R.id.create);
        forget = findViewById(R.id.forget);
        progressBar= findViewById(R.id.progressbar);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this, Register.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(it);

            }
        });

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // to recover your password
               // forgetPassword();
                resetPasswordRequest();
            }
        });

        // to keep user logged in
        initAuthStateListener();
    }
// to recover your password
    private void forgetPassword() {
        String mEmail = editTextemail.getText().toString().trim();

        if (mEmail.isEmpty()) {
            editTextemail.setError("Email is required");
            editTextemail.requestFocus();
            return;}

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            editTextemail.setError("Please enter a valid email");
            editTextemail.requestFocus();
            return;}

        mAuth.sendPasswordResetEmail(mEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Chech your email", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Email sent.");
                        }else {
                            // Log.e(TAG, task.getException().getMessage());
                            //Log.e(TAG, "SIGNIN ERROR");
                            Toast.makeText(Login.this, "Something error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void userLogin() {
        String mEmail = editTextemail.getText().toString().trim();
        String mPassword = editTextPassword.getText().toString().trim();

        if (mEmail.isEmpty()) {
            editTextemail.setError("Email is required");
            editTextemail.requestFocus();
            return;}

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            editTextemail.setError("Please enter a valid email");
            editTextemail.requestFocus();
            return;}

        if (mPassword.length()<6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;}

        if (mPassword.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;}

        progressBar.setVisibility(View.VISIBLE);
// to login using email and password
        if (isNetworkConnected()) {
        mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    //Log.d(TAG, "SIGNIN SUCCESS");
                    Toast.makeText(Login.this, "SIGNIN SUCCESS", Toast.LENGTH_SHORT).show();
                    Intent intend= new Intent(Login.this, home.class);
                    intend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intend);


                } else {
                   // Log.e(TAG, task.getException().getMessage());
                    //Log.e(TAG, "SIGNIN ERROR");
                    Toast.makeText(Login.this, "SIGNIN ERROR", Toast.LENGTH_SHORT).show();

                }
            }

        });
        } else {
            Toast.makeText(this, "please check the network connection", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }
// to keep user logged when you leave app
    private void initAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent iii= new Intent(Login.this,home.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(iii);
                    // Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                   // Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //  check if network is connected
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        } else {
            return false;
        }
    }
    private void displayResetPasswordDialog(String email) {

        AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
        alertDialog.setTitle("reset_password_dialog_title");
        alertDialog.setMessage("sending_email");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void resetPasswordRequest() {

        final Dialog dialog = new Dialog(Login.this);
        dialog.setContentView(R.layout.custom_dialog);
        final EditText editTextemail = (EditText) dialog.findViewById(R.id.edit_email_tv);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv);
        final TextView errorMessage = (TextView) dialog.findViewById(R.id.error_message_tv);

        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mEmail = editTextemail.getText().toString().trim();

                if (mEmail.isEmpty()) {
                    editTextemail.setError("Email is required");
                    editTextemail.requestFocus();
                    return;}

                if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    editTextemail.setError("Please enter a valid email");
                    editTextemail.requestFocus();
                    return;}


                progressBar.setVisibility(View.VISIBLE);
// to login using email and password
                if (isNetworkConnected()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            displayResetPasswordDialog(mEmail);
                                            dialog.dismiss();
                                        } else {
                                            errorMessage.setText("reset password error message");
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(Login.this, "network connection error", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

}
