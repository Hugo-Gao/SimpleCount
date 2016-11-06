package com.program.gyf.simplecount;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import database.DBOpenHelper;

/**
 * Created by Administrator on 2016/11/6.
 */

public class DetailActivity extends Activity
{
    private ImageView oldPicView;
    private DBOpenHelper dbHelper;
    final String TABLENAME = "BillDB";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view_layout);
        oldPicView = (ImageView) findViewById(R.id.origin_image);
        dbHelper = new DBOpenHelper(this, "friends.db", null, 1);
        Intent intent = getIntent();
        String beanInfo = intent.getStringExtra("TheBeanInfo");
        BillBean bean=SearchBeanByDateInfo(beanInfo);
        if (bean.getPicadress() != null)
        {
            oldPicView.setImageURI(Uri.parse(bean.getPicadress()));
        }


    }

    /**
     *  // 第一个参数String：表名
     // 第二个参数String[]:要查询的列名
     // 第三个参数String：查询条件
     // 第四个参数String[]：查询条件的参数
     // 第五个参数String:对查询的结果进行分组
     // 第六个参数String：对分组的结果进行限制
     // 第七个参数String：对查询的结果进行排序
     "create table if not exists BillDB(_id integer primary key autoincrement," +
     "name text not null," +
     "money integer not null," +
     "descripe text not null," +
     "pic BLOB not null," +
     "date text not null," +
     "oldpic BLOB not null)"
     */
    private BillBean SearchBeanByDateInfo(String beanInfo)
    {
        BillBean bean = new BillBean();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLENAME, null, "date=?", new String[]{beanInfo}, null, null,
                null);
        if(cursor!=null)
        {
            String[] columns = cursor.getColumnNames();
            while (cursor.moveToNext())
            {
                for (String name : columns)
                {
                    switch(name)
                    {
                        case "name":
                            bean.setName(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                        case "money":
                            bean.setMoney(cursor.getInt(cursor.getColumnIndex(name)));
                            break;
                        case "descripe":
                            bean.setDescripInfo(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                        case "pic" :
                            bean.setPicInfo(cursor.getBlob(cursor.getColumnIndex(name)));
                            break;
                        case "date":
                            bean.setDateInfo(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                        case "oldpic":
                            bean.setOldpicInfo(cursor.getBlob(cursor.getColumnIndex(name)));
                            break;
                        case "picadress":
                            bean.setPicadress(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                    }
                }
                Log.d("haha", "在Detail中获得了"+bean.toString());
            }
            cursor.close();
        }else
        {
            Log.d("haha", "cursor is null");
            bean = null;
        }
        cursor.close();
        db.close();
        return bean;
    }
    public List<BillBean> getBeanFromDataBase()
    {
        List<BillBean> beanList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS BillDB(_id integer,name text,money integer,descripe text,pic BLOB,date text);");
        Cursor cursor = db.query(TABLENAME, null, null, null, null, null, null);
        if(cursor!=null)
        {
            String[] columns = cursor.getColumnNames();
            while (cursor.moveToNext())
            {
                BillBean bean = new BillBean();
                for (String name : columns)
                {
                    switch(name)
                    {
                        case "name":
                            bean.setName(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                        case "money":
                            bean.setMoney(cursor.getInt(cursor.getColumnIndex(name)));
                            break;
                        case "descripe":
                            bean.setDescripInfo(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                        case "pic" :
                            bean.setPicInfo(cursor.getBlob(cursor.getColumnIndex(name)));
                            break;
                        case "date":
                            bean.setDateInfo(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                        case "oldpic":
                            bean.setOldpicInfo(cursor.getBlob(cursor.getColumnIndex(name)));
                            break;
                        case "picadress":
                            bean.setPicadress(cursor.getString(cursor.getColumnIndex(name)));
                            break;
                    }
                }
                Log.d("haha", "从detail获得了"+bean.toString());
                beanList.add(bean);
            }
            cursor.close();
        }else
        {
            Toast.makeText(this, "database is null", Toast.LENGTH_SHORT).show();
        }
        return beanList;
    }
}
