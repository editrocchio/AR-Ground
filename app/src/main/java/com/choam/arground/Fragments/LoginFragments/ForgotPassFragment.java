package com.choam.arground.Fragments.LoginFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.choam.arground.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    EditText email;
    AppCompatButton btnNewPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgot_pass, container, false);

        email = v.findViewById(R.id.forgot_email);
        btnNewPass = v.findViewById(R.id.forgot_pass_btn);

        btnNewPass.setOnClickListener(v1 -> {
            String emailText = email.getText().toString();

            if(TextUtils.isEmpty(emailText)){
                Toast.makeText(v1.getContext(),"Please fill email",Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.sendPasswordResetEmail(emailText)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(v1.getContext(),"Password reset link was sent your email address",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(v1.getContext(),"No account with that email found",Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        return v;
    }
}