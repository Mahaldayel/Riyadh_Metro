package com.example.hanan.riyadhmetro;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;

public class LogoActivity extends AppCompatActivity{


    private final int SPLASH_DISPLAY_LENGTH = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

        // create logo animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(2000);

        final ImageView splash = (ImageView) findViewById(R.id.logoimg);
        splash.startAnimation(animation);
        //make the activity fullscreen
        hideSystemUI();

        // Start home activity
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-JoinActivity. */
                Intent loginActivity = new Intent(LogoActivity.this,TripListViewActivity.class);
                LogoActivity.this.startActivity(loginActivity);

                LogoActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    public void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


}
