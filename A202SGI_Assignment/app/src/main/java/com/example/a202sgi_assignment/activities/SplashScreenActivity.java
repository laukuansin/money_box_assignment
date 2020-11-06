package com.example.a202sgi_assignment.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;


import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.cores.AppController;

import androidx.annotation.Nullable;

public class SplashScreenActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private Handler _handler;
    private Runnable _runnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _handler = new Handler();

        _runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(AppController.getInstance().getSessionHandler().isSetPassCode())
                {
                 intent=new Intent(SplashScreenActivity.this,PassCodeActivity.class);
                }
                else{
                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        };

        _handler.postDelayed(_runnable, SPLASH_DISPLAY_LENGTH);

    }

}
