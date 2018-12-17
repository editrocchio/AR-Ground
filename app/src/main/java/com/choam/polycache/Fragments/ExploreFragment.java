package com.choam.polycache.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.choam.polycache.ARActivity;
import com.choam.polycache.R;

public class ExploreFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        Button btn = view.findViewById(R.id.btnCamera);

        btn.setOnClickListener(v -> startActivity(new Intent(view.getContext(), ARActivity.class)));
        // Inflate the layout for this fragment
        return view;
    }


}
