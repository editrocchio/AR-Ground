package com.choam.polycache;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_explore:
                        return true;
                    case R.id.navigation_map:
                        startActivity(new Intent(this, MapsActivity.class));
                        return true;
                    case R.id.navigation_logs:
                        return true;
                    case R.id.navigation_settings:
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ImageView profileImg = findViewById(R.id.ivProfile);
        Button profileUpdate = findViewById(R.id.btnUpdate);

        //This changes the image into a bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.funny_profile_pictures_imag);
        //This takes the profile pic as a bitmap and makes it circular
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        //Change the background color
        //mDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), PorterDuff.Mode.DST_OVER);
        //Set the img with a circle shape
        profileImg.setImageDrawable(mDrawable);

        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
