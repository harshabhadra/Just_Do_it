package com.technoidtintin.justdoit.SignUpLogIn;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.technoidtintin.justdoit.Activity.ScrollingActivity;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.FragmentCreateAccountBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {

    private static final String TAG = CreateAccountFragment.class.getSimpleName();
    private FragmentCreateAccountBinding createAccountBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private AlertDialog loadingDialog;
    private String userName, email, password, confirmPassword;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    //Confirm if it is a valid email
    public final static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initializing DataBinding class
        createAccountBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_account, container, false);

        //Initializing FireBase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Adding text watcher to userName text input
        createAccountBinding.signUpUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                createAccountBinding.textInputLayoutSignUpUserName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                createAccountBinding.textInputLayoutSignUpUserName.setErrorEnabled(true);
            }
        });

        //Add text watcher to email address text input
        createAccountBinding.signUpEmailTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                createAccountBinding.textInputLayoutSingUpEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                createAccountBinding.textInputLayoutSingUpEmail.setErrorEnabled(true);
            }
        });

        //Adding text watcher to password input text
        createAccountBinding.signUpPasswordTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                createAccountBinding.textInputLayoutSignUpPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                createAccountBinding.textInputLayoutSignUpPassword.setErrorEnabled(true);
            }
        });

        //Adding text watcher to confirm password text input
        createAccountBinding.signUpConfirmPasswordInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                createAccountBinding.textInputLayoutSignUpConfirmPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                createAccountBinding.textInputLayoutSignUpConfirmPassword.setErrorEnabled(true);
            }
        });

        //Set on click listener to create account button
        createAccountBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = createAccountBinding.signUpUserName.getText().toString();
                email = createAccountBinding.signUpEmailTextInput.getText().toString();
                password = createAccountBinding.signUpPasswordTextInput.getText().toString();
                confirmPassword = createAccountBinding.signUpConfirmPasswordInputText.getText().toString();

                if (userName.isEmpty()) {
                    createAccountBinding.textInputLayoutSignUpUserName.setError("Enter a User Name");
                } else if (!isValidEmail(email)) {
                    createAccountBinding.textInputLayoutSingUpEmail.setError("Enter a valid email address");
                } else if (password.isEmpty()) {
                    createAccountBinding.textInputLayoutSignUpPassword.setError("Enter Password");
                } else if (confirmPassword.isEmpty()) {
                    createAccountBinding.textInputLayoutSignUpConfirmPassword.setError("Confirm Your Password");
                } else if (!password.equals(confirmPassword)) {
                    createAccountBinding.textInputLayoutSignUpConfirmPassword.setError("Password Mismatch");
                } else {
                    loadingDialog = createLoadingDialog(getContext());
                    loadingDialog.show();
                    createNewAccount(userName, email, password);
                }
            }
        });

        //Close Create Account Fragment
        createAccountBinding.signUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,new LogInFragment());
                fragmentTransaction.commit();
            }
        });
        return createAccountBinding.getRoot();
    }

    //Create a new Account
    private void createNewAccount(final String uName, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Verification Email is send to " + firebaseUser.getEmail(),
                                                            Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getActivity(), ScrollingActivity.class);
                                                    startActivity(intent);
                                                    getActivity().overridePendingTransition(R.anim.slide_in_up,R.anim.slide_in_down);
                                                    getActivity().finish();
                                                } else {
                                                    Toast.makeText(getContext(),
                                                            "Failed to send Verification Email to " + firebaseUser.getEmail(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Error: " + task.getException());
                                    }

                                }
                            });

                } else {
                    Log.e(TAG, "Account Creation failed: " + task.getException());
                    Toast.makeText(getContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
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
}
