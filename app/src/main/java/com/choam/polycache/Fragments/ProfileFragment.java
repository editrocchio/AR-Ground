package com.choam.polycache.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.choam.polycache.LoginActivity;
import com.choam.polycache.R;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageView profileImg = view.findViewById(R.id.ivProfile);
        Button profileUpdate = view.findViewById(R.id.btnUpdate);
        Button logout = view.findViewById(R.id.logout_btn);

        //This changes the image into a bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.funny_profile_pictures_imag);
        //This takes the profile pic as a bitmap and makes it circular
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        //Change the background color
        //mDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.DST_OVER);
        //Set the img with a circle shape
        profileImg.setImageDrawable(mDrawable);

        profileUpdate.setOnClickListener(view1 -> Toast.makeText(view1.getContext(),
                "Updated!", Toast.LENGTH_SHORT).show());

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(v.getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });


        // Inflate the layout for this fragment
        return view;
    }

}
