package com.android.moviestreamer.auth;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.android.moviestreamer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = "SignUpDialogFragment";
    private BottomSheetBehavior bottomSheetBehavior;
    TextInputLayout til_email, til_password, til_cnf_password, til_full_name;
    Button btn_sign_up;
    private FirebaseAuth mAuth;
    CoordinatorLayout coordinatorLayout;

    static SignUpDialogFragment newInstance() {
        return new SignUpDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottom_sheet_sign_up, null);
        dialog.setContentView(view);

        coordinatorLayout= dialog.findViewById(R.id.root);
        LinearLayout linearLayout = dialog.findViewById(R.id.bottomSheet);
        linearLayout.setMinimumHeight(getScreenHeight());

        View v_bg_login_screen = dialog.findViewById(R.id.v_bg_login_screen);


        btn_sign_up = dialog.findViewById(R.id.btn_sign_up);
        til_email = dialog.findViewById(R.id.til_email);
        til_password = dialog.findViewById(R.id.til_password);
        til_cnf_password = dialog.findViewById(R.id.til_cnf_password);
        til_full_name = dialog.findViewById(R.id.til_full_name);

        mAuth = FirebaseAuth.getInstance();

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                boolean isEmail = isEmailValid();
                boolean isFullName = isFullNameValid();
                boolean isPass = isPasswordValid();
                boolean isCnfPass = isConfPasswordValid();


                if (isEmail && isFullName && isPass && isCnfPass) {
                    String email = til_email.getEditText().getText().toString();
                    String password = til_password.getEditText().getText().toString();
                    final String FullName = til_full_name.getEditText().getText().toString();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(FullName).build();
                            Log.d(TAG, "onComplete: Login Succesfull uid: " + user.getUid());
                            user.updateProfile(userProfileChangeRequest);

                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: " + "EMail Sent");
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Email Sent. Please Verify to Continue", Snackbar.LENGTH_SHORT);
                                        snackbar.show();

                                       new Handler().postDelayed(new Runnable() {
                                           @Override
                                           public void run() {
                                                                                       dialog.dismiss();

                                           }
                                       },4000);
                                    } else {
                                        task.getException().printStackTrace();
                                        Snackbar.make(coordinatorLayout,task.getException().getLocalizedMessage(),Snackbar.LENGTH_LONG).show();

                                    }
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthUserCollisionException){
                                View snackView = dialog.getWindow().getDecorView();
                                Snackbar.make(coordinatorLayout,e.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();

                                til_email.setError(e.getLocalizedMessage());
                                til_email.setErrorEnabled(true);
                            }
                            Snackbar.make(coordinatorLayout,e.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();

                            Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            Log.d(TAG, "onFailure: " + e.getCause());
                            Log.d(TAG, "onFailure: " + e.getStackTrace());
                            Log.d(TAG, "onFailure: " + e.getClass());

                        }
                    });

                }

            }
        });
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    boolean isEmailValid() {
        String email = til_email.getEditText().getText().toString();
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            til_email.setErrorEnabled(false);
            return true;
        } else {

            til_email.setErrorEnabled(true);

            til_email.setError(getString(R.string.email_invalid_msg));
            return false;

        }


    }

    boolean isPasswordValid() {
        final String password = til_password.getEditText().getText().toString();

        /*
                    ^                 # start-of-string
            (?=.*[0-9])       # a digit must occur at least once
            (?=.*[a-z])       # a lower case letter must occur at least once
            (?=.*[A-Z])       # an upper case letter must occur at least once
            (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
            (?=\\S+$)          # no whitespace allowed in the entire string
            .{4,}             # anything, at least six places though
            $                 # end-of-string
            ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$

        */

        final String PASSWORD_PATTERN = "[a-zA-Z0-9\\\\!\\\\@\\\\#\\\\$]{8,24}";
        Log.d(TAG, "isPasswordValid: " + password);
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        Log.d(TAG, "isPasswordValid: " + pattern.matcher(password).matches());

        if (!TextUtils.isEmpty(password) && pattern.matcher(password).matches()) {
            til_password.setErrorEnabled(false);

            return true;
        } else {
            til_password.setErrorEnabled(true);
            til_password.setError("Password Invalid");
            til_password.setErrorIconDrawable(null);
            return false;

        }

    }

    boolean isConfPasswordValid() {

        final String password = til_password.getEditText().getText().toString();
        final String confPassword = til_cnf_password.getEditText().getText().toString();
        if (!TextUtils.isEmpty(confPassword) && password.equals(confPassword)) {
            til_cnf_password.setErrorEnabled(false);
            return true;
        } else {
            til_cnf_password.setErrorEnabled(true);
            til_cnf_password.setError("Password Mismatch");
            til_cnf_password.setErrorIconDrawable(null);
            return false;
        }

    }

    boolean isFullNameValid() {
        final String fullname = til_full_name.getEditText().getText().toString();
        if (!TextUtils.isEmpty(fullname)) {

            til_full_name.setErrorEnabled(false);
            return true;
        } else {
            til_full_name.setErrorEnabled(true);
            til_full_name.setError("Please Enter Name");
            til_full_name.setErrorIconDrawable(null);
            return false;
        }


    }
}
