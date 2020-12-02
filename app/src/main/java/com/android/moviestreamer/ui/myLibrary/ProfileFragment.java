package com.android.moviestreamer.ui.myLibrary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.moviestreamer.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    FirebaseAuth mAuth;
    FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;

    MutableLiveData<JSONObject> userDataJSON = new MutableLiveData<JSONObject>();


    ShapeableImageView profile_image;
    TextView tv_username, tv_full_name_done, tv_full_name_edit, tv_email_edit, tv_email_done, tv_full_phone_number_edit, tv_full_phone_number_done, tv_password_edit, tv_password_done;
    TextInputLayout til_full_name, til_email, til_full_phone_number, til_password;

    ConstraintLayout cl_progressBar_Container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()

                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_image = root.findViewById(R.id.profile_image);

        tv_username = root.findViewById(R.id.tv_username);

        tv_full_name_done = root.findViewById(R.id.tv_full_name_done);
        tv_full_name_edit = root.findViewById(R.id.tv_full_name_edit);
        tv_email_edit = root.findViewById(R.id.tv_email_edit);
        tv_email_done = root.findViewById(R.id.tv_email_done);
        tv_full_phone_number_edit = root.findViewById(R.id.tv_full_phone_number_edit);
        tv_full_phone_number_done = root.findViewById(R.id.tv_full_phone_number_done);
        tv_password_edit = root.findViewById(R.id.tv_password_edit);
        tv_password_done = root.findViewById(R.id.tv_password_done);

        til_full_name = root.findViewById(R.id.til_full_name);
        til_email = root.findViewById(R.id.til_email);
        til_full_phone_number = root.findViewById(R.id.til_full_phone_number);
        til_password = root.findViewById(R.id.til_password);
        cl_progressBar_Container = root.findViewById(R.id.cl_progressBar_Container);


        cl_progressBar_Container.setVisibility(View.VISIBLE);

        fetchUserDetailsFromCoreDB();

        editEmail();
        editFullName();

        userDataJSON.observe(getActivity(), new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {

                try {
                    tv_username.setText(jsonObject.getString("name"));
                    til_full_name.getEditText().setText(jsonObject.getString("name"));
                    til_email.getEditText().setText(jsonObject.getString("email"));
                    til_full_phone_number.getEditText().setText(jsonObject.getString("full_phone_number"));
                    til_password.getEditText().setText("your can only change password here");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        loadProfilePicture();
        return root;
    }

    void editFullName() {

        tv_full_name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_full_name.setEnabled(true);

                til_full_name.getEditText().requestFocus();
                til_full_name.getEditText().setSelection(til_full_name.getEditText().length());

                tv_full_name_done.setVisibility(View.VISIBLE);
                tv_full_name_edit.setVisibility(View.GONE);
            }
        });

        tv_full_name_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_full_name.setEnabled(false);

                String Fullname = til_full_name.getEditText().getText().toString();

                if (!(Fullname.isEmpty())) {
                    updateFullName(Fullname);
                }
                tv_full_name_done.setVisibility(View.GONE);
                tv_full_name_edit.setVisibility(View.VISIBLE);

            }
        });
    }

    void editEmail() {

        tv_email_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_email.setEnabled(true);

                til_email.getEditText().requestFocus();
                til_email.getEditText().setSelection(til_email.getEditText().length());

                tv_email_done.setVisibility(View.VISIBLE);
                tv_email_edit.setVisibility(View.GONE);
            }
        });

        tv_email_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_email.setEnabled(false);


                String email = til_email.getEditText().getText().toString();

                if (!(email.isEmpty())) {
                    updateEmail(email);
                }
                tv_email_done.setVisibility(View.GONE);
                tv_email_edit.setVisibility(View.VISIBLE);

            }
        });
    }

    void updateEmail(String email) {

        Log.d(TAG, "updateEmail: " + email);
        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        verifyEmail();

//        user.sendEmailVerification();

    }

    void verifyEmail() {
        String BaseURL = getString(R.string.API_BASE_URL) + getString(R.string.API_USER_EMAIL_VERIFY);

        user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {

                String url = BaseURL + "?token=" + getTokenResult.getToken();

                Log.d(TAG, "onSuccess: url=" + BaseURL);
                Log.d(TAG, "onSuccess: url=" + url);

                ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder().setUrl(url).setAndroidPackageName( getString(R.string.PACKAGE_NAME), false, null).build();



                user.sendEmailVerification(actionCodeSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: "+task.getResult());
                            Log.d(TAG, "onComplete: Sent Email");
                        }else{
                            task.getException().printStackTrace();
                        }

                        Log.d(TAG, "onComplete: Sent Email out");
                        Log.d(TAG, "onComplete:  Email Verified "+user.isEmailVerified());


                    }
                }).addOnFailureListener(new OnFailureListener(){

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        });
    }

    void updateFullName(String FullName) {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(FullName).build();
        user.updateProfile(userProfileChangeRequest);
        updateDB();
        fetchUserDetailsFromCoreDB();


    }

    void loadProfilePicture() {
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Glide.with(getContext())
                .load(user.getPhotoUrl())
                .placeholder(shimmerDrawable)
                .error(R.drawable.poster_placeholder_dark)
                .centerCrop()
                .fitCenter()
                .into(profile_image);
    }

    void updateDB() {
        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_UPDATE_USER);

        user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {

            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                AndroidNetworking.put(url)
                        .addQueryParameter("token", getTokenResult.getToken())
                        .build().getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                        userDataJSON.setValue(response);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

            }
        });

    }

    void fetchUserDetailsFromCoreDB() {

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_FETCH_USER);

        cl_progressBar_Container.setVisibility(View.VISIBLE);

        user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                AndroidNetworking.get(url)
                        .addQueryParameter("token", getTokenResult.getToken())
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userDataJSON.setValue(response);
                        cl_progressBar_Container.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


    }

}