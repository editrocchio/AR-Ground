package com.choam.polycache;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.GuardedBy;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


public class ARActivity extends AppCompatActivity {

    private CustomArFragment fragment;
    private Anchor cloudAnchor;

    //NONE by default, HOSTING when hosting the Anchor and HOSTED when the anchor is done hosting.
    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED
    }

    @GuardedBy("singleTapAnchorLock")
    private AppAnchorState appAnchorState = AppAnchorState.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        //hide plane dicovery controller to disable the hand gesture
        fragment.getPlaneDiscoveryController().hide();

        Button clearButton = findViewById(R.id.clear_button);
        //Set cloud anchor to null when we click clear.
        clearButton.setOnClickListener(view -> setCloudAnchor(null));

        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING ||
                            appAnchorState != AppAnchorState.NONE){
                        return;
                    }
                    //Start hosting and change state.
                    Anchor newAnchor = fragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                    setCloudAnchor(newAnchor);
                    appAnchorState = AppAnchorState.HOSTING;
                    Toast.makeText(this, "Hosting!", Toast.LENGTH_SHORT).show();
                    Log.d("ArActivity", "hosting..");
                    placeObject(fragment, cloudAnchor, Uri.parse("model.sfb"));

                }
        );
    }

    //Ensures there's only one cloud anchor at a time.
    private void setCloudAnchor (Anchor newAnchor){
        if (cloudAnchor != null){
            cloudAnchor.detach();
        }

        cloudAnchor = newAnchor;
        appAnchorState = AppAnchorState.NONE;
    }



    /**
     * @param fragment our fragment
     * @param anchor ARCore anchor from the hit test
     * @param model our 3D model of choice
     *
     * Uses the ARCore anchor from the hitTest result and builds the Sceneform nodes.
     * It starts the asynchronous loading of the 3D model using the ModelRenderable builder.
     */
    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));

    }

    /**
     * @param fragment our fragment
     * @param anchor ARCore anchor
     * @param renderable our model created as a Sceneform Renderable
     *
     * This method builds two nodes and attaches them to our scene
     * The Anchor nodes is positioned based on the pose of an ARCore Anchor. They stay positioned in the sample place relative to the real world.
     * The Transformable node is our Model
     * Once the nodes are connected we select the TransformableNode so it is available for interactions
     */
    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    /*Check if the anchor has finished hosting every time a frame is updated. To do this, we can
      create a function onUpdateFrame. This function will call another function checkUpdatedAnchor
      which will check the state of the anchor and update appAnchorState.
    */
    private void onUpdateFrame(FrameTime frameTime){
        checkUpdatedAnchor();
    }

    private synchronized void checkUpdatedAnchor(){
        if (appAnchorState != AppAnchorState.HOSTING){
            return;
        }
        Anchor.CloudAnchorState cloudState = cloudAnchor.getCloudAnchorState();

        if (appAnchorState == AppAnchorState.HOSTING) {
            if (cloudState.isError()) {
                Toast.makeText(this, "Error hosting anchor.", Toast.LENGTH_SHORT).show();
                Log.d("ArActivity", "error");
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                Toast.makeText(this, "Anchor hosted with ID " + cloudAnchor.getCloudAnchorId(), Toast.LENGTH_SHORT).show();
                Log.d("ArActivity", "success");
                appAnchorState = AppAnchorState.HOSTED;
            }
        }
    }

}
