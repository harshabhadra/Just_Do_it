package com.technoidtintin.justdoit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    LogInFragment logInFragment;
    SignUpFragment signUpFragment;

    FrameLayout mainContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInFragment = new LogInFragment();
        signUpFragment = new SignUpFragment();

        mainContainer = findViewById(R.id.main_container);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,signUpFragment).commit();
    }
}
