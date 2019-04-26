package com.choam.arground;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.exceptions.NotTrackingException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrivateARActivity extends AppCompatActivity {

    private CustomArFragment fragment;
    private Anchor cloudAnchor;

    private DatabaseReference database;
    private static final String ANCHOR_ID_START = "anchor:";
    private static final String ANCHOR_NODE_NAME = "cloud_anchors_private";

    private ProgressBar progressBar;
    private TextView progressText;

    private String shortCode;
    private String longCode;
    private String url;

    Button resolveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_ar);

        Intent i = getIntent();
        shortCode = i.getExtras().getString("code");

        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getArSceneView().getPlaneRenderer().setEnabled(false);
        fragment.getPlaneDiscoveryController().hide();
        fragment.getPlaneDiscoveryController().setInstructionView(null);
       // fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
         //   fragment.onUpdate(frameTime);
      //  });

        database = FirebaseDatabase.getInstance().getReference();
        getFireBaseData();

        resolveButton = findViewById(R.id.resolve_button);
        resolveButton.setOnClickListener(view -> {
            onResolvePressed();
        });

        progressBar = findViewById(R.id.progressBar_cyclic);
        progressBar.setVisibility(View.INVISIBLE);
        progressText = findViewById(R.id.progress_text);
    }


    private void setCloudAnchor (Anchor newAnchor){
        cloudAnchor = newAnchor;
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
                .setSource(fragment.getContext(), RenderableSource.builder().setSource(
                        fragment.getContext(),
                        model,
                        RenderableSource.SourceType.GLTF2)
                        .setScale(0.3f)
                        .build())
                .setRegistryId(model)
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

        progressBar.setVisibility(View.INVISIBLE);
        progressText.setText("");
    }

    private void getFireBaseData() {
        //Get the full code from firebase
        database.child(ANCHOR_NODE_NAME).child(ANCHOR_ID_START + shortCode)
                .child("code").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    longCode = dataSnapshot.getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get the url of the object
        database.child(ANCHOR_NODE_NAME).child(ANCHOR_ID_START + shortCode)
                .child("url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    url = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onResolvePressed(){

        if(longCode != null && url != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setText(R.string.rendering_load);
            try {
                Anchor resolvedAnchor = fragment.getArSceneView().getSession().resolveCloudAnchor(longCode);
                setCloudAnchor(resolvedAnchor);
            } catch (NotTrackingException e) {
                Toast.makeText(this, "Camera tracking error.. try again", Toast.LENGTH_LONG).show();
            }
            placeObject(fragment, cloudAnchor, Uri.parse(url));
        } else {
            Toast t = Toast.makeText(this, "Code not found", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            finish();
        }
    }

}
