package com.program.gyf.simplecount;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static tool.ServerIP.LOGURL;
import static tool.ServerIP.SIGNURL;


/**
 * Created by Administrator on 2016/12/25.
 */

public class SignAndLogActivity extends Activity implements View.OnClickListener
{
    private TextView Title;
    private Button logButton;
    private Button signButton;
    private TextInputEditText userNameEdit;
    private TextInputEditText passWordEdit;
    private SharedPreferences sharedPreferences;
    private CardView cardView;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_log_layout);
        logButton = (Button) findViewById(R.id.log_Button);
        signButton = (Button) findViewById(R.id.Sign_Button);
        userNameEdit = (TextInputEditText) findViewById(R.id.username_Password);
        passWordEdit = (TextInputEditText) findViewById(R.id.password_EditText);
        cardView = (CardView) findViewById(R.id.logSignCard);
        Title = (TextView) findViewById(R.id.textView);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Pacifico.ttf");
        Title.setTypeface(typeface);
        logButton.setOnClickListener(this);
        signButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        String userName = userNameEdit.getText().toString();
        username = userName;
        String passWord = passWordEdit.getText().toString();
        if(userName.equals("")||passWord.equals(""))
        {
            showWarnSweetDialog("账号密码不能为空");
            return;
        }
        switch (v.getId())
        {
            case R.id.log_Button:
                String url = LOGURL;/*在此处改变你的服务器地址*/
                getCheckFromServer(url,userName,passWord);
                break;
            case R.id.Sign_Button:
                String url2 = SIGNURL;/*在此处改变你的服务器地址*/
                registeNameWordToServer(url2,userName,passWord);
                break;
        }
    }

    /**
     * 将用户名和密码发送到服务器进行比对，若成功则跳转到app主界面，若错误则刷新UI提示错误登录信息
     * @param url 服务器地址
     * @param userName 用户名
     * @param passWord 密码
     */
    private void getCheckFromServer(String url,final String userName,String passWord)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                     {
                         showWarnSweetDialog("服务器错误");
                      }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (res.equals("0"))
                        {
                            showWarnSweetDialog("请先注册");
                        }
                        else if(res.equals("1"))
                        {
                            showWarnSweetDialog("密码不正确");
                        }
                        else//成功
                        {
                            showSuccessSweetDialog(res);
                            sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", userName);
                            editor.commit();
                        }

                    }
                });
            }
        });

    }

    /**
     * 将用户名与密码发送给服务器进行注册活动
     * @param url 服务器地址
     * @param userName 用户名
     * @param passWord 密码
     */
    private void registeNameWordToServer(String url,final String userName,String passWord)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showWarnSweetDialog("服务器错误");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException
            {
                final String res = response.body().string();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (res.equals("0"))
                        {
                            showWarnSweetDialog("该用户名已被注册");
                        }
                        else
                        {
                            showSuccessSweetDialog(res);
                            sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", userName);
                            editor.apply();
                        }

                    }
                });
            }
        });

    }

    private void showWarnSweetDialog(String info)
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(SignAndLogActivity.this, SweetAlertDialog.WARNING_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    private void showSuccessSweetDialog(String info)
    {
        final SweetAlertDialog pDialog = new SweetAlertDialog(SignAndLogActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
            {
                pDialog.dismiss();
                playAndIntent(cardView);
            }
        });
    }

    private void playAndIntent(View view)
    {
//        DBOpenHelper dbHelper = new DBOpenHelper(this, "friends.db", null, 1,username);
//        dbHelper.getWritableDatabase();
        saveLogStatus();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",-1000f);
        animator.setDuration(800);
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                Intent intent = new Intent(SignAndLogActivity.this, AverageBillActivity.class);
                startActivity(intent);
                new Thread(new Runnable()//在后台线程中关闭此活动
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(1000);
                            SignAndLogActivity.this.finish();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        animator.start();

    }

    /**
     * 记录用户是否登录
     */
    private void saveLogStatus()
    {
        SharedPreferences sps = getSharedPreferences("userLogStatus",MODE_PRIVATE);
        SharedPreferences.Editor editor = sps.edit();
        editor.putBoolean("LogStatus", true);
        editor.apply();

    }
}
