package com.program.gyf.jianji;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;

import database.DBOpenHelper;
import database.TableListDBHelper;
import tool.AcivityHelper;
import tool.ItemAnimition;
import tool.ScrollViewAdapter;
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
    private Button returnBtn;
    private TextView titleTxt;
    private TextView moneyTxt;
    private TextView billNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billlist_layout);
        USERNAME = SharedPreferenceHelper.getTableNameBySP(this);
        returnBtn = (Button) findViewById(R.id.back_btn);
        returnBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemAnimition.confirmAndBigger(v);
                onBackPressed();
            }
        });
        titleTxt = (TextView) findViewById(R.id.title_txt);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Pacifico.ttf");
        titleTxt.setTypeface(typeface);
        moneyTxt = (TextView) findViewById(R.id.money_txt);
        billNameTxt = (TextView) findViewById(R.id.bill_name_txt);
        tableListDBHelper = new TableListDBHelper(this, "TableNameList.db", null, 1, USERNAME);
        itemList = getItemList();
        final DiscreteScrollView scrollView =
                (DiscreteScrollView) findViewById(R.id.picker);
        ScrollViewAdapter adapter = new ScrollViewAdapter(this, itemList);
        scrollView.setAdapter(adapter);
        adapter.setOnCardItemClickListenner(new ScrollViewAdapter.OnCardItemClickListenner()
        {
            @Override
            public void onItemClick(View v, int position)
            {
                String billName = itemList.get(position).getBillName();
                intentToHomeActivity(billName);
                BillListActivity.this.finish();
            }
        });
        scrollView.setOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>()
        {
            @Override
            public void onCurrentItemChanged(@NonNull RecyclerView.ViewHolder viewHolder, int adapterPosition)
            {
                billNameTxt.setText(itemList.get(adapterPosition).getBillName()+" 支出 :");
                moneyTxt.setText("￥"+getBillSumMoney(itemList.get(adapterPosition).getBillName())+"");
            }
        });
    }



    private void intentToHomeActivity(String billName)
    {
        Intent intent = new Intent(this, AverageBillActivity.class);
        Log.d("haha", "向主页传输billName is " + billName);
        intent.putExtra("billName", billName);
        startActivity(intent);
        AcivityHelper.finishThisActivity(this);
    }


    private int getBillSumMoney(String BillName)
    {
        int moneySum=0;
        dbHelper = new DBOpenHelper(BillListActivity.this, "BillData.db", null, 1, BillName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BillName,null, null, null, null, null, null);
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
        if (cursor != null)
        {
            cursor.close();
        }
        Log.d("haha", "money sum is " + moneySum);
        db.close();
        return moneySum;
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
