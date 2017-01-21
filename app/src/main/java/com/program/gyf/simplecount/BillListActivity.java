package com.program.gyf.simplecount;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import database.DBOpenHelper;
import database.TableListDBHelper;
import tool.AcivityHelper;
import tool.BillViewAdapter;
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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billlist_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        USERNAME = SharedPreferenceHelper.getTableNameBySP(this);
        tableListDBHelper=new TableListDBHelper(this, "TableNameList.db", null, 1, USERNAME);
        itemList=getItemList();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        BillViewAdapter adapter = new BillViewAdapter(this, itemList);
        adapter.setOnItemClickListener(new BillViewAdapter.onRecyclerViewItemClickListen()
        {
            @Override
            public void onitemView(View view, String billName)
            {
                intentToHomeActivity(billName);
            }
        });
        recyclerView.setAdapter(adapter);


    }

    private void intentToHomeActivity(String billName)
    {
        Intent intent = new Intent(this, AverageBillActivity.class);
        Log.d("haha", "像主页传输billName is " + billName);
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
            byte[] picInfo=getOnePicFromBill(name);
            BillItem item = new BillItem();
            item.setBillName(name);
            item.setBillPic(picInfo);
            list.add(item);
        }
        cursor.close();
        db.close();
        return list;
    }

    private byte[] getOnePicFromBill(String name)
    {
        byte[] picInfo=null;
        dbHelper = new DBOpenHelper(BillListActivity.this, "BillData.db", null, 1, name);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(name, null, "_id = ?", new String[]{"1"}, null, null, null);
        while (cursor.moveToNext())
        {
             picInfo = cursor.getBlob(cursor.getColumnIndex("oldpic"));
        }
        return picInfo;
    }
}
