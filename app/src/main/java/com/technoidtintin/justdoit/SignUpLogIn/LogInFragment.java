package com.technoidtintin.justdoit.SignUpLogIn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.technoidtintin.justdoit.Activity.ScrollingActivity;
import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.ActivityLogInBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogInFragment extends Fragment {

    private static final String TAG = LogInFragment.class.getSimpleName();
    private static final int G_SIGN_IN = 1234;
    private String userEmail, userPassword;

    private ActivityLogInBinding activityLogInBinding;
    private FirebaseAuth firebaseAuth;
    private AlertDialog loadingDialog;

    private GoogleSignInClient googleSignInClient;

    public LogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activityLogInBinding = DataBindingUtil.inflate(inflater,R.layout.activity_log_in,container,false);

        //Initializing Firebae auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Setting Size of google sign in button
        activityLogInBinding.googleSignInButton.setSize(SignInButton.SIZE_WIDE);

        //Configure Google Sign In
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(),signInOptions);

        //Set on click listener to google sign in button
        activityLogInBinding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog = createLoadingDialog(getContext());
                loadingDialog.show();
                googleSignIn();
            }
        });

        //Setting text watcher to email text input
        activityLogInBinding.emailTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                activityLogInBinding.textInputLayoutEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                activityLogInBinding.textInputLayoutEmail.setErrorEnabled(true);
            }
        });

        //Setting Text watcher to password text input
        activityLogInBinding.passwordTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                activityLogInBinding.textInputLayoutEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                activityLogInBinding.textInputLayoutEmail.setErrorEnabled(true);
            }
        });

        //Set onClickListener to Log In button
        activityLogInBinding.logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = activityLogInBinding.emailTextInput.getText().toString().trim();
                userPassword = activityLogInBinding.passwordTextInput.getText().toString().trim();

                if (userEmail.isEmpty()){
                    activityLogInBinding.textInputLayoutEmail.setError("Enter Valid Email Address");
                }else if (userPassword.isEmpty()){
                    activityLogInBinding.textInputLayoutPassword.setError("Enter Password");
                }else {
                    loadingDialog = createLoadingDialog(getContext());
                    loadingDialog.show();
                    logInUser(userEmail,userPassword);
                }
            }
        });

        //Set onClickListener to create new account tv
        activityLogInBinding.logInCreateNewAccTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,new SignUpFragment());
                fragmentTransaction.commit();
            }
        });

        return activityLogInBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        loadingDialog = createLoadingDialog(getContext());
        loadingDialog.show();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            loadingDialog.dismiss();
            Log.e(TAG,"Name: " + firebaseUser.getDisplayName());
            Intent intent = new Intent(getActivity(), ScrollingActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            loadingDialog.dismiss();
            Toast.makeText(getContext(),"Log In and Just Do It", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == G_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                loadingDialog.dismiss();
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getContext(),"Sign In Failed, Try again",Toast.LENGTH_LONG).show();
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    //Log in User
    private void logInUser(final String email, String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    startScrollingActivity();
                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
            }
        });
    }

    //Start Google sign in
    private void googleSignIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,G_SIGN_IN);
    }

    //Exchange data between google and frebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingDialog.dismiss();
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
            }
        });
    }

    //Create Loading dialog
    private AlertDialog createLoadingDialog(Context context) {
        View layout = getLayoutInflater().inflate(R.layout.loading_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(layout);
        return builder.create();
    }

    //Start Scrolling Activity
    private void startScrollingActivity(){
        Intent intent = new Intent(getActivity(),ScrollingActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
