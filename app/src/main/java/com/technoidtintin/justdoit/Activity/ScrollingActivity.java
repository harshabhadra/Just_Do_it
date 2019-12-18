package com.technoidtintin.justdoit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.MainUIFragments.HomeFragment;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.MainUIFragments.ReportFragment;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = ScrollingActivity.class.getSimpleName();

    HomeFragment homeFragment = new HomeFragment();
    ReportFragment reportFragment = new ReportFragment();

    private boolean isFirstLogIn;

    FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment active= homeFragment;
    private  BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                            active = homeFragment;
                            return true;
                        case R.id.nav_report:
                            fragmentManager.beginTransaction().hide(active).show(reportFragment).commit();
                            active = reportFragment;
                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Getting Intent
        Intent intent = getIntent();
        isFirstLogIn = intent.getBooleanExtra(Constants.FIRST_TIME_LOG_IN,false);
        //Initializing BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        fragmentManager.beginTransaction().add(R.id.bottom_nav_frame,reportFragment,"2").commit();
        fragmentManager.beginTransaction().add(R.id.bottom_nav_frame,homeFragment,"1").commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean isFirstLogIn() {
        return isFirstLogIn;
    }
}
