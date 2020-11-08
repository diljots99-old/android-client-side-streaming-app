package com.android.moviestreamer.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moviestreamer.DashboardActivity;
import com.android.moviestreamer.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

public class LoginScreenActivity extends AppCompatActivity {
    private static final String TAG = "LoginScreenActivity";
    TextInputLayout til_email,til_password;
    TextView tv_google,tv_facebook,tv_phone;
    LinearLayout ll_signup;
    Button btn_login;


    CoordinatorLayout coordinatorLayout;
    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);

        tv_google = findViewById(R.id.tv_google);
        tv_facebook = findViewById(R.id.tv_facebook);
        tv_phone = findViewById(R.id.tv_phone);

        btn_login = findViewById(R.id.btn_login);
        ll_signup = findViewById(R.id.ll_signup);


        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()

                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        tv_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreenActivity.this,PhoneAuthActivity.class);
                startActivity(intent);
            }
        });
        tv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(tv_facebook,"Facebook Login is currently unable use other methods",Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getColor(R.color.black));
                snackbar.show();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = til_email.getEditText().getText().toString();
                String password = til_password.getEditText().getText().toString();
                if (email.isEmpty()){
                    til_email.setErrorEnabled(true);
                    til_email.setError("Enter Valid Email");
                }
                if (password.isEmpty()){
                    til_password.setErrorEnabled(true);
                    til_password.setError("This Field is mandatory");
                    til_password.setErrorIconDrawable(null);
                }
                if(!email.isEmpty() && !password.isEmpty()) {
                    signInUsingEmail(email, password);
                }
            }
        });

        ll_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpBottomSheet();
            }
        });



    }

    void signUpBottomSheet(){

        DialogFragment dialogFragment = SignUpDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(),"TAG");


    }

    private void signInUsingEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()){
                                Intent intent = new Intent(LoginScreenActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            }else {
                                Snackbar.make(btn_login,"Please Verify Email First",Snackbar.LENGTH_LONG).show();
                            }
                            Log.d(TAG, "onComplete: user profile"+   task.getResult().getAdditionalUserInfo().getProfile());
                            Log.d(TAG, "onComplete: user profile"+   task.getResult().getAdditionalUserInfo().getProfile());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Log.d(TAG, "onComplete: message "+task.getException().getMessage());
                            Log.d(TAG, "onComplete: cause "+task.getException().getCause());
                            Log.d(TAG, "onComplete: localizedMessage "+task.getException().getLocalizedMessage());

                            Toast.makeText(LoginScreenActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: user profile"+   task.getResult().getAdditionalUserInfo().getProfile());
                            Log.d(TAG, "onComplete: uid "+   user.getUid());
                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    Log.d(TAG, "onComplete: "+task.getResult().getToken());

                                    addNewUSerToDbAndStartDashboard(task.getResult().getToken());



                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            
                        }

                    }
                });
    }

    private void addNewUSerToDbAndStartDashboard(String token) {
        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_CREATE_NEW_USER);

        AndroidNetworking.post(url).addQueryParameter("token",token).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                Intent intent = new Intent(LoginScreenActivity.this, DashboardActivity.class);
                startActivity(intent);
                LoginScreenActivity.this.finish();
            }

            @Override
            public void onError(ANError anError) {

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }

}