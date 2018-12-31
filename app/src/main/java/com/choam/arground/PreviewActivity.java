package com.choam.arground;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ImageView preview = findViewById(R.id.expand_thumbnail);
        Button cancel = findViewById(R.id.cancel_preview);
        Button place = findViewById(R.id.place_button);

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

        //Kill current activity on cancel TODO: delete the file.
        cancel.setOnClickListener(v -> finish());

    }



}
