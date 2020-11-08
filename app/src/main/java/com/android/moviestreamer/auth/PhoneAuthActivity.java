package com.android.moviestreamer.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.moviestreamer.DashboardActivity;
import com.android.moviestreamer.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class PhoneAuthActivity extends AppCompatActivity {
    private static String TAG = "PhoneAuthActivity";
    CountryCodePicker ccp;
    TextInputLayout til_phone;
    BottomSheetDialog mBottomSheetDialog;
    TextView tv_phoneNumber_otp_verify, tv_Timer;
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    long RESEND_TIMEOT = 90000;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    Button btn_resend;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private BottomSheetBehavior bottomSheetBehavior;


    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    CountDownTimer resendCountdowmTimer;
    Button btn_send_otp,btn_verify_otp;
    OtpTextView otp_text_view;
    TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        ccp = findViewById(R.id.ccp_ccp);
        til_phone = findViewById(R.id.til_phone);
        btn_send_otp = findViewById(R.id.btn_send_otp);

        mAuth = FirebaseAuth.getInstance();

        ccp.registerCarrierNumberEditText(til_phone.getEditText());

        initializeOTPDialog();

        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + ccp.isValidFullNumber());
                PhoneVerification(ccp.getFullNumberWithPlus());

            }
        });


    }

    void PhoneVerification(String phoneNumber) {
        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        Log.d(TAG, "PhoneVerification: " + phoneAuthProvider);
        Log.d(TAG, "PhoneVerification: " + phoneNumber);

        phoneAuthProvider.verifyPhoneNumber(phoneNumber, RESEND_TIMEOT, TimeUnit.MILLISECONDS, PhoneAuthActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: success" + phoneAuthCredential.toString());
                Log.d(TAG, "onVerificationCompleted: success" + phoneAuthCredential.getSmsCode());
                Log.d(TAG, "onVerificationCompleted: success" + phoneAuthCredential.getSignInMethod());
                Log.d(TAG, "onVerificationCompleted: success" + phoneAuthCredential.getProvider());
                try {
                    String smsCode = phoneAuthCredential.getSmsCode();
                    if (smsCode.length() == 6) {
                        otp_text_view.setOTP(phoneAuthCredential.getSmsCode());

                        btn_verify_otp.performClick();


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }



            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: "+ e.getLocalizedMessage());
                Log.d(TAG, "onVerificationFailed: "+e.getMessage());
                otp_text_view.showError();
                setErrorMessage(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationID, forceResendingToken);
                Log.d(TAG, "onCodeSent: s: " + verificationID);
                Log.d(TAG, "onCodeSent: resendtoken: " + forceResendingToken);
                mVerificationId = verificationID;

                tv_phoneNumber_otp_verify.setText(ccp.getFormattedFullNumber());
                mBottomSheetDialog.show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.d(TAG, "onCodeAutoRetrievalTimeOut: " + s);
            }
        });
    }

    void initializeOTPDialog() {
        mBottomSheetDialog = new BottomSheetDialog(PhoneAuthActivity.this);
        View sheetView = PhoneAuthActivity.this.getLayoutInflater().inflate(R.layout.bottom_sheet_opt_verify, null);
        mBottomSheetDialog.setContentView(sheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) sheetView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        tv_phoneNumber_otp_verify = mBottomSheetDialog.findViewById(R.id.tv_phoneNumber_otp_verify);
        tv_Timer = mBottomSheetDialog.findViewById(R.id.tv_Timer);

        otp_text_view = mBottomSheetDialog.findViewById(R.id.otp_text_view);

        btn_resend = mBottomSheetDialog.findViewById(R.id.btn_resend_otp);
        btn_verify_otp = mBottomSheetDialog.findViewById(R.id.btn_verify_otp);
        tv_message = mBottomSheetDialog.findViewById(R.id.tv_message);

        btn_resend.setEnabled(false);
        btn_resend.setClickable(false);

        resendCountdowmTimer = new CountDownTimer(RESEND_TIMEOT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds_to_finish = (int) (millisUntilFinished / 1000);
                int minutes = seconds_to_finish / 60;
                int seconds = seconds_to_finish % 60;
                tv_Timer.setText(minutes + ":" + seconds);

            }

            @Override
            public void onFinish() {
                tv_Timer.setText("00:00");
                btn_resend.setEnabled(true);
                btn_resend.setClickable(true);
                btn_resend.setTextColor(getColor(R.color.colorPrimary));
            }
        };
        resendCountdowmTimer.start();

        btn_verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_message.setVisibility(View.GONE);
                String smsCode  = otp_text_view.getOTP();


                if(smsCode.length() == 6){
                    otp_text_view.showSuccess();

                    verifyPhoneNumberWithCode(mVerificationId, smsCode);


                }else{
                    setErrorMessage("Enter Valid OTP");
                    otp_text_view.showError();
                }

            }
        });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    Log.d(TAG, "onComplete: "+task.getResult().getToken());
                                    addNewUSerToDbAndStartDashboard(task.getResult().getToken());
                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.d(TAG, "signInWithCredential:failure"+ task.getException().getLocalizedMessage());
                            Log.d(TAG, "signInWithCredential:failure"+ task.getException().getCause());
                            Log.d(TAG, "signInWithCredential:failure"+ task.getException().getClass());
                            otp_text_view.showError();
                            setErrorMessage(task.getException().getLocalizedMessage());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {


                            }

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
                Intent intent = new Intent(PhoneAuthActivity.this, DashboardActivity.class);
                startActivity(intent);
                PhoneAuthActivity.this.finish();
                PhoneAuthActivity.this.finishAffinity();
            }

            @Override
            public void onError(ANError anError) {

            }
        });


    }

    void setErrorMessage(String msg){

        tv_message.setText(msg);
        tv_message.setTextColor(getColor(R.color.red));
        tv_message.setVisibility(View.VISIBLE);
    }



}