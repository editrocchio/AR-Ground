package com.choam.arground;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.choam.arground.GoogleClasses.ResolveDialogFragment;
import com.choam.arground.GoogleClasses.SnackbarHelper;
import com.choam.arground.GoogleClasses.StorageManager;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class PrivateARActivity extends AppCompatActivity {

    private CustomArFragment fragment;
    private Anchor cloudAnchor;
    //NONE by default, HOSTING when hosting the Anchor and HOSTED when the anchor is done hosting.
    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }

    private AppAnchorState appAnchorState = AppAnchorState.NONE;
    private SnackbarHelper snackbarHelper = new SnackbarHelper();
    private StorageManager storageManager;

    private String url;

    private DatabaseReference database;
    private static final String ANCHOR_ID_START = "anchor:";
    private static final String ANCHOR_NODE_NAME = "cloud_anchors";

    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        Intent i = getIntent();
        url = i.getExtras().getString("code");

        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdateFrame();
        });


        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(view -> setCloudAnchor(null));

        Button resolveButton = findViewById(R.id.resolve_button);
        resolveButton.setOnClickListener(view -> {
            if (cloudAnchor != null){
                snackbarHelper.showMessageWithDismiss(getParent(), "Please clear Anchor");
                return;            }
            ResolveDialogFragment dialog = new ResolveDialogFragment();
            dialog.setOkListener(PrivateARActivity.this::onResolveOkPressed);
            dialog.show(getSupportFragmentManager(), "Resolve");

        });

        progressBar = findViewById(R.id.progressBar_cyclic);
        progressBar.setVisibility(View.INVISIBLE);
        progressText = findViewById(R.id.progress_text);

        storageManager = new StorageManager(this);
        database = FirebaseDatabase.getInstance().getReference();
    }

    //Ensures there's only one cloud anchor at a time.
    private void setCloudAnchor (Anchor newAnchor){
        if (cloudAnchor != null){
            cloudAnchor.detach();
        }

        cloudAnchor = newAnchor;
        appAnchorState = AppAnchorState.NONE;
        snackbarHelper.hide(this);
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

        progressBar.setVisibility(View.INVISIBLE);
        progressText.setText("");

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
    private void onUpdateFrame(){
        checkUpdatedAnchor();
    }

    private synchronized void checkUpdatedAnchor(){
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING){
            return;
        }
        Anchor.CloudAnchorState cloudState = cloudAnchor.getCloudAnchorState();
        if (appAnchorState == AppAnchorState.HOSTING) {
            if (cloudState.isError()) {
                snackbarHelper.showMessageWithDismiss(this, "Error hosting anchor.. "
                        + cloudState);
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                //Only do this if we're hosting privately. Generate short code and show to user with
                //alert dialog. If it's public then we don't need to give user code.
                if(PreviewActivity.getChoice().equals("private")) {
                    String code = generateCode();
                    //TODO: Check if code already exists in firebase
                    database.child(ANCHOR_NODE_NAME).child(code).setValue(cloudAnchor.getCloudAnchorId());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Your shareable code is: " + code.substring(7));
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.copy, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("code", code);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(PrivateARActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setText("");

                }

                appAnchorState = AppAnchorState.HOSTED;
            }
        }

        else if (appAnchorState == AppAnchorState.RESOLVING){
            if (cloudState.isError()) {
                snackbarHelper.showMessageWithDismiss(this, "Error resolving anchor.. "
                        + cloudState);
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS){
                snackbarHelper.showMessageWithDismiss(this, "Anchor resolved successfully");
                appAnchorState = AppAnchorState.RESOLVED;
            }
        }

    }

    /*This function takes the shortCode as the input, retrieves the resolved anchor, and places
      our 3D object on the Resolved Anchor. It changes the appAnchorState to RESOLVING
    */
    private void onResolveOkPressed(String dialogValue){
        progressBar.setVisibility(View.VISIBLE);
        progressText.setText(R.string.rendering_load);
        int shortCode = Integer.parseInt(dialogValue);
        storageManager.getCloudAnchorID(shortCode,(cloudAnchorId) -> {
            Anchor resolvedAnchor = fragment.getArSceneView().getSession().resolveCloudAnchor(cloudAnchorId);
            setCloudAnchor(resolvedAnchor);
            placeObject(fragment, cloudAnchor, Uri.parse(url));
            snackbarHelper.showMessage(this, "Now Resolving Anchor...");
            appAnchorState = AppAnchorState.RESOLVING;
        });
    }

    /**
     * Generate a random 4 digit code for private cloud anchors.
     * @return  Pseudo random 4 digit code (each digit between 0 and 10).
     */
    public String generateCode() {
        Random r = new Random();

        return ANCHOR_ID_START + r.nextInt(10) + r.nextInt(10) + r.nextInt(10)
        + r.nextInt(10);
    }
}
