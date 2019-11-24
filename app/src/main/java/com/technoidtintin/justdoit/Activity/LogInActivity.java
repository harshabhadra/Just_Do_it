package com.technoidtintin.justdoit.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.ActivityLogInBinding;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = LogInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;
    ActivityLogInBinding logInBinding;
    FirebaseAuth firebaseAuth;
    private boolean isLogIn;
    private FirebaseUser firebaseUser;
    private String email, password, userName, confirmPassword;
    private AlertDialog loadingDialog;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        isLogIn = true;

        //Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Initializing DataBinding
        logInBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        //On Create Account text View click
        logInBinding.createAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Make confirm password and user name input layout visible
                logInBinding.textInputLayoutConfirmPassword.setVisibility(View.VISIBLE);
                logInBinding.textInputLayoutUsername.setVisibility(View.VISIBLE);

                logInBinding.logInButton.setText("Create Account");

                //Hide sign in set up
                logInBinding.signInLayout.setVisibility(View.INVISIBLE);

                isLogIn = false;
            }
        });

        //On Log in or Create new Account button click
        logInBinding.logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog = createLoadingDialog(LogInActivity.this);
                loadingDialog.show();
                if (!isLogIn) {
                    email = logInBinding.emailTextInput.getText().toString().trim();
                    password = logInBinding.passwordTextInput.getText().toString().trim();
                    userName = logInBinding.userNameTextInput.getText().toString();
                    confirmPassword = logInBinding.confirmPasswordTextInput.getText().toString();
                    createNewAccount(userName, email, password);
                } else {
                    email = logInBinding.emailTextInput.getText().toString().trim();
                    password = logInBinding.passwordTextInput.getText().toString().trim();
                    logInUser(email, password);
                }
            }
        });

        //On Google sing in button click
        logInBinding.googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        logInBinding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog = createLoadingDialog(this);
        loadingDialog.show();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            loadingDialog.dismiss();
            Log.e(TAG, "Name: " + currentUser.getDisplayName());
            startScrollingActivity();
        } else {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
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

    //Create a new Account
    private void createNewAccount(final String uName, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "Account Created Successfully");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(uName)
                            .build();
                    firebaseUser.updateProfile(profileChangeRequest)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "Name: " + firebaseUser.getDisplayName());
                                        startScrollingActivity();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                } else {
                    Log.e(TAG, "Account Creation failed: " + task.getException());
                }
            }
        });
    }

    //Log in User
    private void logInUser(String email, String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    startScrollingActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Start ScrollActivity
    private void startScrollingActivity() {
        Intent intent = new Intent(LogInActivity.this, ScrollingActivity.class);
        startActivity(intent);
        finish();
    }

    //Create Loading dialog
    private AlertDialog createLoadingDialog(Context context) {
        View layout = getLayoutInflater().inflate(R.layout.loading_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(layout);
        return builder.create();
    }

    //Google Sign in Intent
    private void googleSignIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startScrollingActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Sign in failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
