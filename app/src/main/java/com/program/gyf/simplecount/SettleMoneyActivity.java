package com.program.gyf.simplecount;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import database.DBOpenHelper;
import tool.AcivityHelper;

import static tool.SharedPreferenceHelper.getNameFromSharedPreferences;
import static tool.SharedPreferenceHelper.getTableNameBySP;
import static tool.SharedPreferenceHelper.saveRealBillNameToSharedPreferences;

/**
 * Created by Administrator on 2016/10/21.
 */

public class SettleMoneyActivity extends Activity implements View.OnClickListener
{
    private DBOpenHelper dbHelper;
    private TextView moneynumText;
    private int moneySum;
    private double moneyAver;
    private String TABLENAME ;
    private String SPName ;
    private Map<String, Integer> personBillMap;
    private FloatingActionButton FAB;
    private String BillName;
    private Button finishBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        String transition = getIntent().getStringExtra("transition");
        BillName = getIntent().getStringExtra("BillName");

        switch (transition)
        {
            case "fade":
                Fade fade = new Fade();
                fade.setDuration(1000);
                getWindow().setEnterTransition(fade);
                break;
        }
        setContentView(R.layout.settlemoney_layout);
        finishBtn = (Button) findViewById(R.id.finish_btn);
        finishBtn.setOnClickListener(this);
        FAB = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        FAB.setOnClickListener(this);
            personBillMap = new HashMap<>();
        TABLENAME = getTableNameBySP(SettleMoneyActivity.this);
        SPName = TABLENAME;
        dbHelper = new DBOpenHelper(this, "BillData.db", null, 1,TABLENAME);
        moneynumText = (TextView) findViewById(R.id.bill_sum);
        showInformation();
        Log.d("haha", "person num is " + personBillMap.size());
        showCountEveryBody();
        //AverageEveryBody();
    }

    private void AverageEveryBody()
    {
        LinearLayout layoutwrapper = (LinearLayout) findViewById(R.id.textAddWrapper);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this);
        tv.setText("平均每人支出"+moneyAver);
        tv.setPadding(30,10,0,0);
        tv.setTextSize(30);
        layoutwrapper.addView(tv,params);
    }

    /**
     * 此方法对所有参与此次旅行的人计算每个人金额
     * 先从SharedPreference中提取名字,再在数据库中按名字检索计算金额
     */
    private void showCountEveryBody()
    {
        List<String> nameLsit;
        int money=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        nameLsit = getNameFromSharedPreferences(SettleMoneyActivity.this, SPName,BillName);
        moneyAver = moneySum / nameLsit.size();
        for (String name: nameLsit)
        {
            Cursor cursor=db.query(BillName,null,"name = ?",new String[]{name},null, null, null);
            while (cursor.moveToNext())
            {
                money+=cursor.getInt(cursor.getColumnIndex("money"));

            }
            LinearLayout layoutwrapper = (LinearLayout) findViewById(R.id.textAddWrapper);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            String AAString;
            TextView tv = new TextView(this);
            if(money>moneyAver)
            {
                AAString=",还需收入"+(money-moneyAver)+"￥";
            }else if(money<moneyAver)
            {
                AAString=",还需支出"+(moneyAver-money)+"￥";
            }else
            {
                AAString="";
            }
            tv.setText(name+"目前支出了"+money+"元"+AAString);
            tv.setPadding(30,10,0,0);
            tv.setTextSize(20);
            tv.setTypeface(Typeface.SANS_SERIF);
            layoutwrapper.addView(tv,params);
            personBillMap.put(name, money);
            money=0;
            cursor.close();
        }
        db.close();

    }

    /**
     *  // 第一个参数String：表名
     // 第二个参数String[]:要查询的列名
     // 第三个参数String：查询条件
     // 第四个参数String[]：查询条件的参数
     // 第五个参数String:对查询的结果进行分组
     // 第六个参数String：对分组的结果进行限制
     // 第七个参数String：对查询的结果进行排序
     */
    private void showInformation()
    {
         moneySum=0;
        int billcount=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BillName, null, null, null, null, null, null);
        billcount=cursor.getCount();
        cursor.close();
        cursor = db.query(BillName,null, null, null, null, null, null);
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                Log.d("haha", cursor.getInt(cursor.getColumnIndex("money"))+"");
                moneySum+=cursor.getInt(cursor.getColumnIndex("money"));
            }
        }else
        {
            Toast.makeText(this, "查询总金额失败", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        Log.d("haha", "money sum is " + moneySum);
        moneynumText.setText(billcount+"笔支出,共"+"￥"+moneySum);
        db.close();
    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.floatingActionButton:
                ObjectAnimator animator = ObjectAnimator.ofFloat(FAB, "rotation", 0F,360F);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(FAB, "scaleX", 1F,0.1F);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(FAB, "scaleY", 1F,0.1F);
                AnimatorSet animatorSet = new AnimatorSet();
                animator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        Intent intent = new Intent(SettleMoneyActivity.this,AverageBillActivity.class);
                        startActivity(intent);
                        SettleMoneyActivity.this.finish();

                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });
                animatorSet.playTogether(animator, animator1, animator2);
                animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                animator.start();

                break;
            case R.id.finish_btn:
                final SweetAlertDialog pDialog = new SweetAlertDialog(SettleMoneyActivity.this, SweetAlertDialog.NORMAL_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("你确定要结算了吗");
                pDialog.setCancelable(true);
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        pDialog.dismiss();
                        finishBill();
                    }
                });
                pDialog.show();
                break;
        }
    }

    private void finishBill()
    {
        saveRealBillNameToSharedPreferences(this,"");
        Intent intent = new Intent(this, AverageBillActivity.class);
        startActivity(intent);
        AcivityHelper.finishThisActivity(this);
    }

    @Override
    public void onBackPressed()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(FAB, "rotation", 0F,360F);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(FAB, "scaleX", 1F,0.1F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(FAB, "scaleY", 1F,0.1F);
        AnimatorSet animatorSet = new AnimatorSet();
        animator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                Intent intent = new Intent(SettleMoneyActivity.this,AverageBillActivity.class);
                startActivity(intent);
                SettleMoneyActivity.this.finish();

            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
        animatorSet.playTogether(animator, animator1, animator2);
        animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        animator.start();
    }
}

