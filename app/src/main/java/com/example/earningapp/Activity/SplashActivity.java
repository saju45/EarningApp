package com.example.earningapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.earningapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

             startActivity(new Intent(SplashActivity.this,LoginActivity.class));
             finish();
            }
        },1500);

    }
}