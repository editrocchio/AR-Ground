package com.choam.polycache;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.choam.polycache.Fragments.ExploreFragment;
import com.choam.polycache.Fragments.LogFragment;
import com.choam.polycache.Fragments.MapsFragment;
import com.choam.polycache.Fragments.ProfileFragment;
import com.choam.polycache.Fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    public static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

        actionBar.setTitle("Home");
        loadFragment(new ProfileFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_explore:
                    actionBar.setTitle("Explore");
                    fragment = new ExploreFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_map:
                    actionBar.setTitle("Map");
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_logs:
                    actionBar.setTitle("Logs");
                    fragment = new LogFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_settings:
                    actionBar.setTitle("Settings");
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
