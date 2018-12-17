package com.choam.polycache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.google.ar.sceneform.ux.ArFragment;


public class ARActivity extends AppCompatActivity {

    private ArFragment arFragment;

    private boolean isTracking = false;
    private boolean isHitting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        arFragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        // Adds a listener to the ARSceneView
        // Called before processing each frame
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            arFragment.onUpdate(frameTime);
            onUpdate();
        });
        }

    }

}
