package com.technoidtintin.justdoit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding signUpBinding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private AlertDialog loadingDialog;
    private String userName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initializing DataBinding
        signUpBinding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up);

        //Initializing Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //On Click cancel button
        signUpBinding.signUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //On Click sign up button
        signUpBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            Toast.makeText(getApplicationContext(),"Enter Your Details to Create Account",Toast.LENGTH_SHORT).show();
        }else {
            Log.e(TAG,"User is not null");
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

    //Start ScrollActivity
    private void startScrollingActivity() {
        Intent intent = new Intent(SignUpActivity.this, ScrollingActivity.class);
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
}
