package com.android.moviestreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("http://www.test.diljotsingh.com/").build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Oncreate", "onResponse: "+response);
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: "+anError);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run () {
                startActivity(new Intent(SplashScreenActivity.this,DashboardActivity.class));
                finish();
            }
        }, 3000);

    }
}