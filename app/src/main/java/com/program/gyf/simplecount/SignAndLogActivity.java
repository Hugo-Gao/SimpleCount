package com.program.gyf.simplecount;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_log_layout);
        logButton = (Button) findViewById(R.id.log_Button);
        signButton = (Button) findViewById(R.id.Sign_Button);
        userNameEdit = (TextInputEditText) findViewById(R.id.username_Password);
        passWordEdit = (TextInputEditText) findViewById(R.id.password_EditText);
        Title = (TextView) findViewById(R.id.textView);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Pacifico.ttf");
        Title.setTypeface(typeface);
        sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        logButton.setOnClickListener(this);
        signButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.log_Button:
                break;
            case R.id.Sign_Button:
                String userName = userNameEdit.getText().toString();
                String passWord = passWordEdit.getText().toString();
                Toast.makeText(this, "用户名"+userName+"   "+"密码"+passWord, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
