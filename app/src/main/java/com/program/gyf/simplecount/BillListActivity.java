package com.program.gyf.simplecount;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.List;

import database.DBOpenHelper;
import database.TableListDBHelper;
import tool.AcivityHelper;
import tool.BitmapHandler;
import tool.HorizontalPagerAdapter;
import tool.SharedPreferenceHelper;

/**
 * Created by Administrator on 2017/1/18.
 */

public class BillListActivity extends Activity
{
    private RecyclerView recyclerView;
    private List<BillItem> itemList;
    private TableListDBHelper tableListDBHelper;
    private DBOpenHelper dbHelper;
    private String USERNAME;
    private ImageView blurImage;
    private int Oldpostion = 0;
    private List<Bitmap> blurList = new ArrayList<>();
    private Drawable dpyDrawable=null;
    private boolean blurOK=false;
    private android.os.Handler handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            if (!blurOK)
            {
                return false;
            }
            int curPosition = msg.arg1;
            Bitmap bitmap = blurList.get((curPosition)%blurList.size());
            TransitionDrawable td;
            if (dpyDrawable == null)
            {
                 td = new TransitionDrawable(new Drawable[] { new ColorDrawable(0xfffcfcfc),
                        new BitmapDrawable(getResources(), bitmap) });
            }
            else
            {
                td = new TransitionDrawable(new Drawable[] { dpyDrawable, new BitmapDrawable(getResources(), bitmap) });
            }
            td.startTransition(500);
            blurImage.setImageDrawable(td);
            dpyDrawable = new BitmapDrawable(bitmap);
            Log.d("testLog", curPosition + "当前位置");
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billlist_layout);
        USERNAME = SharedPreferenceHelper.getTableNameBySP(this);
        tableListDBHelper = new TableListDBHelper(this, "TableNameList.db", null, 1, USERNAME);
        blurImage = (ImageView) findViewById(R.id.blur_pic);
        itemList = getItemList();
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                blurAllBmp();
                blurOK=true;
            }
        });
        thread.start();
        blurImage.setImageBitmap(BitmapHandler.blur(this, itemList.get(0).getBillBitmapPic(this)));
        final HorizontalInfiniteCycleViewPager infiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        final HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(itemList, this);
        infiniteCycleViewPager.setAdapter(adapter);
        adapter.setOnItemClickViewListener(new HorizontalPagerAdapter.onItemClickViewListener()
        {
            @Override
            public void clickItem(View view, String billName)
            {
                intentToHomeActivity(billName);
            }
        });
        infiniteCycleViewPager.setScrollDuration(500);
        infiniteCycleViewPager.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.overshoot_interpolator));
        infiniteCycleViewPager.setMediumScaled(true);
        infiniteCycleViewPager.setMaxPageScale(0.9F);
        infiniteCycleViewPager.setMinPageScale(0.8F);
        infiniteCycleViewPager.setCenterPageScaleOffset(30.0F);
        infiniteCycleViewPager.setMinPageScaleOffset(5.0F);

        Thread  thread2 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    if (infiniteCycleViewPager.getCurrentItem() != Oldpostion)
                    {
                        Message message = new Message();
                        message.arg1 =infiniteCycleViewPager.getCurrentItem() ;
                        Oldpostion=message.arg1;
                        handler.sendMessage(message);
                        Log.d("haha", "位置改变,发送handler");
                    }
                }
            }
        });
        thread2.start();

    }

    private void blurAllBmp()
    {
        for (BillItem item : itemList)
        {
            if(item.getBillBitmapPic(this)!=null)
            {
                blurList.add(BitmapHandler.blur(this, item.getBillBitmapPic(this)));
            }else
            {
                Bitmap  bitmap = BitmapFactory.decodeResource(this.getResources() ,R.drawable.ic_launcher);
                blurList.add(BitmapHandler.blur(this, bitmap));
            }
        }
    }

    private void intentToHomeActivity(String billName)
    {
        Intent intent = new Intent(this, AverageBillActivity.class);
        Log.d("haha", "向主页传输billName is " + billName);
        intent.putExtra("billName", billName);
        startActivity(intent);
        AcivityHelper.finishThisActivity(this);
    }

    public List<BillItem> getItemList()
    {
        List<BillItem> list = new ArrayList<>();
        SQLiteDatabase db = tableListDBHelper.getWritableDatabase();
        Cursor cursor = db.query(USERNAME + "TableList", null, null, null, null, null, null);
        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex("tableName"));
            Log.d("haha", "账本名为" + name);
            String picInfo = getOnePicFromBill(name);
            BillItem item = new BillItem();
            item.setBillName(name);
            item.setBillPic(picInfo);
            list.add(item);
        }
        cursor.close();
        db.close();
        return list;
    }

    private String getOnePicFromBill(String name)
    {
        String picInfo = null;
        dbHelper = new DBOpenHelper(BillListActivity.this, "BillData.db", null, 1, name);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(name, null, "_id = ?", new String[]{"1"}, null, null, null);
        while (cursor.moveToNext())
        {
            picInfo = cursor.getString(cursor.getColumnIndex("picadress"));
        }
        cursor.close();
        db.close();

        return picInfo;
    }
}
