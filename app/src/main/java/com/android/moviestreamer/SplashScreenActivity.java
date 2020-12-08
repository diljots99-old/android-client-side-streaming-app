package com.android.moviestreamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.moviestreamer.auth.LoginScreenActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.List;

import okhttp3.Response;


public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        user = getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AndroidNetworking.initialize(getApplicationContext());

        db = FirebaseFirestore.getInstance();

        db.collection("app_settings").document("api").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> URL_LISTS = (List<String>) documentSnapshot.get("BASE_URL");
                Log.d(TAG, "api onSuccess: " + URL_LISTS);

                for (String URL : URL_LISTS) {
                    Log.d(TAG, "onSuccess: " + URL);
                    AndroidNetworking.get(URL).build().getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            Log.d(TAG, "onResponse: " + response);
                            Log.d(TAG, "onResponse: " +response.code());
                            if (response.code() == 200){
                                getString(R.string.API_BASE_URL);
                                
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.d(TAG, "onError: " + anError.getErrorDetail());
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


        user = getCurrentUser();
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
                Log.d("Oncreate", "onResponse: " + response);
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: " + anError);
            }
        });


    }

    FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        return user;
    }

}