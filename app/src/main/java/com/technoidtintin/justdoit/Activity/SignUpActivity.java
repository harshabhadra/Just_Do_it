package com.technoidtintin.justdoit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.FragmentCreateAccountBinding;


public class SignUpActivity extends AppCompatActivity {

    FragmentCreateAccountBinding signUpBinding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private AlertDialog loadingDialog;
    private String userName, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_account);

        //Initializing Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Initializing DataBinding
        signUpBinding = DataBindingUtil.setContentView(this,R.layout.fragment_create_account);

        //On Click cancel button
        signUpBinding.signUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Adding text watcher to userName text input
        signUpBinding.signUpUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                signUpBinding.textInputLayoutSignUpUserName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUpBinding.textInputLayoutSignUpUserName.setErrorEnabled(true);
            }
        });

        //Add text watcher to email address text input
        signUpBinding.signUpEmailTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                signUpBinding.textInputLayoutSingUpEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUpBinding.textInputLayoutSingUpEmail.setErrorEnabled(true);
            }
        });

        //Adding text watcher to password input text
        signUpBinding.signUpPasswordTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                signUpBinding.textInputLayoutSignUpPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUpBinding.textInputLayoutSignUpPassword.setErrorEnabled(true);
            }
        });

        //Adding text watcher to confirm password text input
        signUpBinding.signUpConfirmPasswordInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                signUpBinding.textInputLayoutSignUpConfirmPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUpBinding.textInputLayoutSignUpConfirmPassword.setErrorEnabled(true);
            }
        });

        //On Click sign up button
        signUpBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = signUpBinding.signUpUserName.getText().toString();
                email = signUpBinding.signUpEmailTextInput.getText().toString();
                password = signUpBinding.signUpPasswordTextInput.getText().toString();
                confirmPassword = signUpBinding.signUpConfirmPasswordInputText.getText().toString();

                if (userName == null){
                    signUpBinding.textInputLayoutSignUpUserName.setError("Enter a User Name");
                }else if (!isValidEmail(email)){
                    signUpBinding.textInputLayoutSingUpEmail.setError("Enter a valid email address");
                }else if (password == null){
                    signUpBinding.textInputLayoutSignUpPassword.setError("Enter Password");
                }else if (confirmPassword == null){
                    signUpBinding.textInputLayoutSignUpConfirmPassword.setError("Confirm Your Password");
                }else if (!password.equals(confirmPassword)){
                    signUpBinding.textInputLayoutSignUpConfirmPassword.setError("Password Mismatch");
                }else {
                    loadingDialog = createLoadingDialog(SignUpActivity.this);
                    loadingDialog.show();
                    createNewAccount(userName,email,password);
                }
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
                                        firebaseUser = firebaseAuth.getCurrentUser();
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    firebaseUser = firebaseAuth.getCurrentUser();
                                                    Toast.makeText(getApplicationContext(),"Verification Email is send to " + firebaseUser.getEmail(),
                                                            Toast.LENGTH_LONG).show();
                                                    backtoLogInActivity();
                                                }else {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Failed to send Verification Email to " + firebaseUser.getEmail(),
                                                            Toast.LENGTH_LONG).show();
                                                    firebaseUser.delete();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG,"Error: " + task.getException());
                                    }

                                }
                            });

                } else {
                    Log.e(TAG, "Account Creation failed: " + task.getException());
                    Toast.makeText(getApplicationContext(),"Error: " + task.getException(),Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    //Start ScrollActivity
    private void backtoLogInActivity() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        intent.putExtra(Constants.NEW_USER,"new user");
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

    public final static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
