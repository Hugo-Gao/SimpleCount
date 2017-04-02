package com.program.gyf.simplecount;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import static tool.SharedPreferenceHelper.isLogging;

/**
 * Created by Administrator on 2016/9/27.
 */

public class WelcomeActivity extends Activity
{
    private final int SPLASH_DISPLAY_LENGHT = 1000; // 延迟01秒
    private TextView Title;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wel_layout);
        Title = (TextView) findViewById(R.id.textView);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Pacifico.ttf");
        Title.setTypeface(typeface);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(isLogging(WelcomeActivity.this))//检验用户是否已经登陆了
                {
                    Intent intent = new Intent(WelcomeActivity.this, AverageBillActivity.class);
                    startActivity(intent);
                }else
                {
                    Intent i = new Intent(WelcomeActivity.this, SignAndLogActivity.class);
                    String transitionName = "titleShare";
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, Title, transitionName);
                    startActivity(i, transitionActivityOptions.toBundle());
                }
                new Thread(new Runnable()//在后台线程中关闭此活动
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(1000);
                            WelcomeActivity.this.finish();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
