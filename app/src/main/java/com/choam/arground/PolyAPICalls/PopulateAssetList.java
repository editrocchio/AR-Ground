package com.choam.arground.PolyAPICalls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.choam.arground.R;

/**
 * This does the setup for the RecyclerView.
 */
public class PopulateAssetList extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_list);

        RecyclerView rvAssets = findViewById(R.id.rvAssets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAssets.setLayoutManager(layoutManager);
        rvAssets.addItemDecoration(new DividerItemDecoration(rvAssets.getContext(),
                layoutManager.getOrientation()));

        AssetAdapter assetAdapter = new AssetAdapter(PolyObject.getPolyObjects());
        rvAssets.setAdapter(assetAdapter);
    }
}
