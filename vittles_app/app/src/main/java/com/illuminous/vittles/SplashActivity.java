package com.illuminous.vittles;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.VideoView;

//The logic in this class came from a post on stack overflow about having a movie splash screen
public class SplashActivity extends AppCompatActivity {


    private void jump() {
//it is safe to use this code even if you
//do not intend to allow users to skip the splash
        if(isFinishing())
            return;
        startActivity(new Intent(this, ModeSelectionActivity.class));
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            VideoView videoHolder = new VideoView(this);
            setContentView(videoHolder);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.splash);
            videoHolder.setVideoURI(video);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    jump();
                }

            });
            videoHolder.start();
        } catch(Exception ex) {
            jump();
        }
    }
}
