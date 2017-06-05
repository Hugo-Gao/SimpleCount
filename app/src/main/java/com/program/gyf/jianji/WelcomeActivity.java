package com.program.gyf.jianji;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import tool.PermissionUtil;

import static tool.SharedPreferenceHelper.isLogging;

/**
 * Created by Administrator on 2016/9/27.
 */

public class WelcomeActivity extends Activity
{
    private TextView Title;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wel_layout);
        Title = (TextView) findViewById(R.id.textView);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Pacifico.ttf");
        Title.setTypeface(typeface);
        PermissionUtil.getPermission(this);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if (PermissionUtil.hasPermission(WelcomeActivity.this))
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                intentToMain();
                                finishAfterTransition();
                            }
                        });
                        break;
                    }else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(WelcomeActivity.this, "需要权限才能正常工作哦", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        }).start();


    }


    private void intentToMain()
    {
        if (isLogging(WelcomeActivity.this))//检验用户是否已经登陆了
        {
            Intent intent = new Intent(WelcomeActivity.this, AverageBillActivity.class);
            startActivity(intent);
            finishAfterTransition();
        } else
        {

            Intent i = new Intent(WelcomeActivity.this, SignAndLogActivity.class);
            if (OverLollipop())
            {
                Log.d("haha", OverLollipop() + "");
                Log.d("haha", "系统5.0以上");
                String transitionName = "titleShare";
                ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, Title, transitionName);
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, Title, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                finishAfterTransition();
            } else
            {
                Log.d("haha", "系统5.0以下");
                startActivity(i);
                finishAfterTransition();
            }

        }
    }

    private boolean OverLollipop()
    {
        return (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
    }

}
