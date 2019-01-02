package com.choam.arground;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class PreviewActivity extends AppCompatActivity {

    private static boolean host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        host = false;
        ImageView preview = findViewById(R.id.expand_thumbnail);
        Button cancel = findViewById(R.id.cancel_preview);
        Button place = findViewById(R.id.place_button);
        Switch hostSwitch = findViewById(R.id.cloud_switch);

        Intent i = getIntent();
        Glide.with(this)
                .load(i.getExtras().getString("thumbUrl"))
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.drawable.baseline_explore_black_24dp))
                .into(preview);

        String assetUrl = i.getExtras().getString("gltfFileUrl");

        place.setOnClickListener(v -> {
            Intent arIntent = new Intent(this, ARActivity.class);
            arIntent.putExtra("gltfFileUrl", assetUrl);
            this.startActivity(arIntent);
        });

        hostSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            host = isChecked;
        });

        //Kill current activity on cancel
        cancel.setOnClickListener(v -> finish());

    }

    public static boolean getHost() {
        return host;
    }
}
