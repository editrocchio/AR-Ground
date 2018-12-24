package com.choam.polycache.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
        Button map = view.findViewById(R.id.btnMap);

        btn.setOnClickListener(v -> startActivity(new Intent(view.getContext(), ARActivity.class)));
        map.setOnClickListener(v -> {
            Fragment fragment = new MapsFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }


}
