package com.technoidtintin.justdoit.SignUpLogIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.R;

public class MainActivity extends AppCompatActivity {

    LogInFragment logInFragment;
    SignUpFragment signUpFragment;

    FrameLayout mainContainer;

    SharedPreferences sharedPreferences;
    private boolean isNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInFragment = new LogInFragment();
        signUpFragment = new SignUpFragment();

        //Getting SharedPreference
        sharedPreferences = getSharedPreferences(Constants.IS_NEW_USER,MODE_PRIVATE);
        isNewUser = sharedPreferences.getBoolean(Constants.NEW_USER,true);

        //Initializing main container
        mainContainer = findViewById(R.id.main_container);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isNewUser) {
            fragmentTransaction.replace(R.id.main_container, signUpFragment).commit();
        }else {
            fragmentTransaction.replace(R.id.main_container,new LogInFragment()).commit();
        }
    }
}
