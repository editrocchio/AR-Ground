package com.choam.polycache.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.choam.polycache.MainActivity;
import com.choam.polycache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    EditText email;
    EditText pass;
    AppCompatButton loginButton;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        email = v.findViewById(R.id.input_email);
        pass = v.findViewById(R.id.input_password);
        loginButton = v.findViewById(R.id.btn_login);

        setCreateLink(v);

        loginButton.setOnClickListener(view -> {
            String emailText = email.getText().toString();
            String passText = pass.getText().toString();

            signIn(emailText, passText, view);
        });

        return v;
    }

    public void setCreateLink(View view) {
        SpannableString signUp = new SpannableString(getResources().getText(R.string.create_account));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                Fragment fragment = new SignUpFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.login_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE);
        signUp.setSpan(clickableSpan, 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setSpan(fcs, 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = view.findViewById(R.id.link_signup);
        textView.setText(signUp);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    public void signIn(String e, String p, View view) {
        firebaseAuth.signInWithEmailAndPassword(e, p)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(view.getContext(), MainActivity.class));
                    } else {
                        Toast.makeText(view.getContext(), "Incorrect email/password combination",
                                Toast.LENGTH_SHORT).show();
                    }

                });

    }


}
