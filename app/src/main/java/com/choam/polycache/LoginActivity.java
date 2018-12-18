package com.choam.polycache;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.choam.polycache.Fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        TextView signUp = findViewById(R.id.signUpTextView);

        signUp.setOnClickListener(v -> {
            Fragment fragment = new SignUpFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          //  transaction.replace(R.id.sign_up_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
      //  updateUI(currentUser);
     //   startActivity(new Intent(this, MainActivity.class));
    }

    public FirebaseUser getFirebaseUser() {
        return mAuth.getCurrentUser();
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

}
