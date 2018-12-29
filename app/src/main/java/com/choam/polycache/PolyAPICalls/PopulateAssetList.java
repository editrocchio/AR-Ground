package com.choam.polycache.PolyAPICalls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.choam.polycache.R;

/**
 * This does the setup for the RecyclerView.
 */
public class PopulateAssetList extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_list);

        RecyclerView rvAssets = findViewById(R.id.rvAssets);
        rvAssets.setLayoutManager(new LinearLayoutManager(this));
        AssetAdapter assetAdapter = new AssetAdapter(PolyObject.getPolyObjects());
        rvAssets.setAdapter(assetAdapter);
    }
}
