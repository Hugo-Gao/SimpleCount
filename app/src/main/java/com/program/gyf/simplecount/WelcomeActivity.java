package com.program.gyf.simplecount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/9/27.
 */

public class WelcomeActivity extends Activity
{
    private final int SPLASH_DISPLAY_LENGHT = 1000; // 延迟01秒
    private ImageView welView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        welView = (ImageView) findViewById(R.id.wel_pic);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this,AverageBillActivity.class);
                WelcomeActivity.this.startActivity(mainIntent);
                WelcomeActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
