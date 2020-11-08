package com.android.moviestreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.moviestreamer.auth.LoginScreenActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;


public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onStart() {
        super.onStart();
        user =getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AndroidNetworking.initialize(getApplicationContext());

        user =getCurrentUser();
        if (user == null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run () {
//                startActivity(new Intent(SplashScreenActivity.this,DashboardActivity.class));
                startActivity(new Intent(SplashScreenActivity.this, LoginScreenActivity.class));

                finish();
                }
            }, 3000);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run () {
                startActivity(new Intent(SplashScreenActivity.this,DashboardActivity.class));
//                startActivity(new Intent(SplashScreenActivity.this, LoginScreenActivity.class));

                finish();
                }
            }, 3000);

        }

        AndroidNetworking.get(getString(R.string.API_BASE_URL)).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Oncreate", "onResponse: "+response);
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: "+anError);
            }
        });


    }
    FirebaseUser getCurrentUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        return  user;
    }

}