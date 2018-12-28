package com.choam.polycache.Fragments.LoginFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.choam.polycache.MainActivity;
import com.choam.polycache.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpFragment extends Fragment {
    private EditText email;
    private EditText username;
    private EditText password;
    private AppCompatButton regButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        username = v.findViewById(R.id.input_name);
        email = v.findViewById(R.id.input_email);
        password = v.findViewById(R.id.input_password);
        regButton = v.findViewById(R.id.btn_signup);

        setLoginLink(v);
        createAccount(v);


        return v;
    }

    public void setLoginLink(View view) {
        SpannableString login = new SpannableString(getResources().getText(R.string.already_a_member_login));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                Fragment fragment = new LoginFragment();
                loadFragment(fragment);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE);
        login.setSpan(clickableSpan, 17, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setSpan(fcs, 17, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = view.findViewById(R.id.link_login);
        textView.setText(login);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.WHITE);
    }

    public void createAccount(View view) {
        regButton.setOnClickListener(v1 -> {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();
            String nameText = username.getText().toString();

            if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passText) || TextUtils.isEmpty(nameText)){
                Toast.makeText(view.getContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                return;
            }
            if(passText.length() < 6){
                Toast.makeText(view.getContext(),"Password must be longer than 6 characters",Toast.LENGTH_SHORT).show();
                password.setText("");
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            writeUserToDB(task.getResult().getUser().getUid(), nameText, new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault()).format(new Date()));
                            startActivity(new Intent(view.getContext(), MainActivity.class));
                        }
                        else{
                            Toast.makeText(view.getContext(),"An error occurred",Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    public void writeUserToDB(String userID, String name, String date) {
        database.child("users").child(userID).child("username").setValue(name);
        database.child("users").child(userID).child("joined").setValue(date);
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(MainActivity.BACK_STACK_ROOT_TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_container, fragment);
        transaction.addToBackStack(MainActivity.BACK_STACK_ROOT_TAG);
        transaction.commit();
    }


}
