package com.technoidtintin.justdoit.SignUpLogIn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.technoidtintin.justdoit.Activity.ScrollingActivity;
import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.FragmentSignUpBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private static final String TAG = SignUpFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;
    private FragmentSignUpBinding fragmentSignUpBinding;
    private FirebaseAuth firebaseAuth;

    private GoogleSignInClient googleSignInClient;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        fragmentSignUpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);

        //Setting up google sign up button
        fragmentSignUpBinding.signUpButton.setSize(SignInButton.SIZE_WIDE);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        //Initializing Google Sign in client
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        //Set on click listener to GoogleSignIn button
        fragmentSignUpBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        //Set on click listener to create account button
        fragmentSignUpBinding.signUpCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,new CreateAccountFragment());
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up);
                fragmentTransaction.commit();
            }
        });

        return fragmentSignUpBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    //Google Sign in Intent
    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    //Exchange data between google and frebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.e(TAG,"User: " + user.getDisplayName());
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.IS_NEW_USER,Context.MODE_PRIVATE).edit();
                            editor.putBoolean(Constants.NEW_USER,false);
                            editor.apply();
                            startScrollingActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(),"Sign in failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //Start ScrollActivity
    private void startScrollingActivity() {
        Intent intent = new Intent(getActivity(), ScrollingActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
