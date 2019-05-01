package com.choam.arground;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class PreviewActivity extends AppCompatActivity {

    private static String choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ImageView preview = findViewById(R.id.expand_thumbnail);
        Button cancel = findViewById(R.id.cancel_preview);
        Button place = findViewById(R.id.place_button);
        RadioGroup radioGroup = findViewById(R.id.share_radio_group);

        //Set choice to the initial radio button choice
        if(radioGroup.getCheckedRadioButtonId() == R.id.share_priv) {
            choice = "private";
        } else {
            choice = "noshare";
        }

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

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.share_priv) {
                choice = "private";
            } else {
                choice = "noshare";
            }
        });

        //Kill current activity on cancel
        cancel.setOnClickListener(v -> finish());

    }

    public static String getChoice() {
        return choice;
    }

    public static void setChoice(String setChoice) {
        if(choice != null) {
            choice = setChoice;
        }
    }
}
