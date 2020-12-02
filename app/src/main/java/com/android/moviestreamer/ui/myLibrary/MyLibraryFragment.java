package com.android.moviestreamer.ui.myLibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.moviestreamer.R;
import com.android.moviestreamer.auth.LoginScreenActivity;
import com.android.moviestreamer.ui.search.RecentSearchFragment;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyLibraryFragment extends Fragment {
    private static final String TAG = "NotificationsFragment";

    FirebaseAuth mAuth;
    FirebaseUser user;
    Button btn_history, btn_watch_later, btn_favriotes, btn_downloads, btn_your_profile, btn_logout;
    ShapeableImageView profile_image;
    TextView tv_username;
    private GoogleSignInClient mGoogleSignInClient;
    private ProfileFragment profileFragment;
    NavController navController;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_library, container, false);

        profile_image = root.findViewById(R.id.profile_image);
        tv_username = root.findViewById(R.id.tv_username);

        btn_history = root.findViewById(R.id.btn_history);
        btn_watch_later = root.findViewById(R.id.btn_watch_later);
        btn_favriotes = root.findViewById(R.id.btn_favriotes);
        btn_downloads = root.findViewById(R.id.btn_downloads);
        btn_your_profile = root.findViewById(R.id.btn_your_profile);
        btn_logout = root.findViewById(R.id.btn_logout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()

                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        loadProfilePicture();
        loadUsername();

        btn_logout();
        btn_your_profile();

        btn_watch_later();
        btn_history();
        btn_favriotes();
        btn_downloads();

        return root;
    }

    private void btn_watch_later() {
        btn_watch_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_navigation_my_library_to_navigation_watch_later);
            }
        });
    }

    private void btn_downloads() {
        btn_downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_navigation_my_library_to_navigation_downloads);
            }
        });
    }

    private void btn_history() {
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_navigation_my_library_to_navigation_history);
            }
        });
    }

    private void btn_favriotes() {
        btn_favriotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_navigation_my_library_to_navigation_favorites);
            }
        });
    }

    void swapRecentFragment() {
        profileFragment = new ProfileFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, profileFragment).addToBackStack(null).commit();
    }

    private void btn_your_profile() {
        btn_your_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_navigation_my_library_to_navigation_profile);
            }
        });
    }

    void btn_logout() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                getActivity().finish();
                Intent intent = new Intent(getContext(), LoginScreenActivity.class);
                startActivity(intent);

            }
        });
    }

    void loadUsername() {
        String username = user.getDisplayName();
        String email = user.getEmail();
        String phone = user.getPhoneNumber();
        Log.d(TAG, "loadUsername: email" + email);
        Log.d(TAG, "loadUsername: username" + username);
        Log.d(TAG, "loadUsername: phone" + phone);
        if (!(phone == null)) if (!phone.equalsIgnoreCase("")) {
            tv_username.setText(phone);
        }
        if (!(email == null)) if (!email.equalsIgnoreCase("")) {
            tv_username.setText(email);
        }
        if (!(username == null)) if (!username.equalsIgnoreCase("")) {
            tv_username.setText(username);
        }
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
}