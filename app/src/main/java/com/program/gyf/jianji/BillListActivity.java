package com.program.gyf.jianji;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tomer.fadingtextview.FadingTextView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import database.DBOpenHelper;
import database.TableListDBHelper;
import service.DeleteBillService;
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
    private FadingTextView moneyTxt;
    private FadingTextView billNameTxt;
    private FloatingActionButton fab;
    private String currentBillName;
    private ScrollViewAdapter adapter;
    private String fromBillName;

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
        fab = (FloatingActionButton) findViewById(R.id.delete_btn);
        Intent intent = getIntent();
        fromBillName = intent.getStringExtra("fromName");
        moneyTxt = (FadingTextView) findViewById(R.id.money_txt);
        billNameTxt = (FadingTextView) findViewById(R.id.bill_name_txt);
        tableListDBHelper = new TableListDBHelper(this, "TableNameList.db", null, 1, USERNAME);
        itemList = getItemList();
        final DiscreteScrollView scrollView = (DiscreteScrollView) findViewById(R.id.picker);
        adapter = new ScrollViewAdapter(this, itemList);
        scrollView.setAdapter(adapter);
        adapter.setOnCardItemClickListenner(new ScrollViewAdapter.OnCardItemClickListenner()
        {
            @Override
            public void onItemClick(final View v, final int position)
            {
                ((CardView) v).setCardElevation(30.0f);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d("haha", "点击了position " + position + " size is " + itemList.size());

                        String billName = itemList.get(scrollView.getCurrentItem()).getBillName();

                        intentToHomeActivity(billName);
                        BillListActivity.this.finish();
                    }
                }).start();

            }
        });

        scrollView.setOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>()
        {
            @Override
            public void onCurrentItemChanged(@NonNull RecyclerView.ViewHolder viewHolder, int adapterPosition)
            {
                currentBillName = itemList.get(scrollView.getCurrentItem()).getBillName();
                billNameTxt.setTexts(new String[]{currentBillName + " 支出 :"});
                moneyTxt.setTexts(new String[]{"￥" + getBillSumMoney(itemList.get(adapterPosition).getBillName()) + ""});
            }
        });
        if (itemList.size() == 0)
        {
            fab.setImageResource(R.mipmap.back);
        }
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemAnimition.confirmAndBigger(v);
                if (itemList.size() > 0)
                {
                    final SweetAlertDialog dialog = new SweetAlertDialog(BillListActivity.this, SweetAlertDialog.WARNING_TYPE);
                    dialog.setTitleText("你真的要删除账本 " + currentBillName + " 吗?\n\n");
                    dialog.show();
                    dialog.setCancelable(true);
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            deleteCurrentBill(currentBillName);
                            dialog.dismiss();
                        }
                    });
                } else//如果已经删除完了
                {
                    onBackPressed();
                }
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        if (fromBillName.equals(""))//如果来的时候的账单已经被删除了
        {
            Intent intent = new Intent(this, AverageBillActivity.class);
            intent.putExtra("billName", "SimpleCount");
            startActivity(intent);
            finishAfterTransition();
        } else
        {
            super.onBackPressed();
        }

    }

    /**
     * 删除当前指向的账本
     *
     * @param billName 账本名称
     */
    private void deleteCurrentBill(String billName)
    {
        if (billName.equals(fromBillName))//如果删除的账单是后台首页账单，则返回时清空任务栈
        {
            fromBillName = "";
        }
        for (int i = 0; i < itemList.size(); i++)
        {
            if (itemList.get(i).getBillName().equals(billName))
            {
                if (itemList.size() == 1)
                {
                    billNameTxt.setTexts(new String[]{""});
                    moneyTxt.setTexts(new String[]{""});
                } else if (i == 0)
                {
                    billNameTxt.setTexts(new String[]{itemList.get((i + 1) % itemList.size()).getBillName() + " 支出 :"});
                    currentBillName = itemList.get((i + 1) % itemList.size()).getBillName();
                    moneyTxt.setTexts(new String[]{"￥" + getBillSumMoney(itemList.get((i + 1) % itemList.size()).getBillName()) + ""});
                } else
                {

                    billNameTxt.setTexts(new String[]{itemList.get((i - 1) % itemList.size()).getBillName() + " 支出 :"});
                    currentBillName = itemList.get((i - 1) % itemList.size()).getBillName();
                    moneyTxt.setTexts(new String[]{"￥" + getBillSumMoney(itemList.get((i - 1) % itemList.size()).getBillName()) + ""});
                }
                break;
            }
        }
        if (!adapter.deleteBill(billName))
        {
            SweetAlertDialog dialog = new SweetAlertDialog(BillListActivity.this, SweetAlertDialog.ERROR_TYPE);
            dialog.setTitleText("删除账本" + billName + "失败\n\n");
            dialog.show();
            return;
        }
        deleteBillName(billName);
        deleteAllBillBean(billName);
        Log.d("haha", "*************size is " + itemList.size());
        notifyServerToDelete();
        if (itemList.size() == 0)
        {
            fab.setImageResource(R.mipmap.back);
        }
    }



    /**
     * 删除用户账本名数据库的数据
     *
     * @param billName
     */
    private void deleteBillName(String billName)
    {
        tableListDBHelper = new TableListDBHelper(this, "TableNameList.db", null, 1, USERNAME);
        SQLiteDatabase db = tableListDBHelper.getWritableDatabase();
        try
        {
            String tableName = USERNAME + "TableList";
            db.execSQL("delete from " + tableName + " where tableName = ?", new Object[]{billName});
        } finally
        {
            db.close();
        }
        SharedPreferenceHelper.SaveDeleteBillNameToSP(this, billName,USERNAME);
    }


    /**
     * 删除用户专属数据库中的所有数据
     *
     * @param billName 账本名
     */
    private void deleteAllBillBean(String billName)
    {
        dbHelper = new DBOpenHelper(BillListActivity.this, "BillData.db", null, 1, billName, USERNAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            String tableName = billName + USERNAME;
            db.execSQL("DROP TABLE " + tableName);
        } finally
        {
            db.close();
        }
    }

    /**
     * 启动服务通知服务器删除账本
     */
    private void notifyServerToDelete()
    {
        Set<String> billNameSet = SharedPreferenceHelper.getDeleteBillNameFromSP(this,USERNAME);
        Log.d("haha", billNameSet.toString());
        if (billNameSet.size() != 0)
        {
            Intent intent = new Intent(BillListActivity.this, DeleteBillService.class);
            startService(intent);
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


    private int getBillSumMoney(String BillName)
    {
        int moneySum = 0;
        dbHelper = new DBOpenHelper(BillListActivity.this, "BillData.db", null, 1, BillName, USERNAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BillName + USERNAME, null, null, null, null, null, null);
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                Log.d("haha", cursor.getInt(cursor.getColumnIndex("money")) + "");
                moneySum += cursor.getInt(cursor.getColumnIndex("money"));
            }
        } else
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
        dbHelper = new DBOpenHelper(BillListActivity.this, "BillData.db", null, 1, name, USERNAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(name + USERNAME, null, "_id = ?", new String[]{"1"}, null, null, null);
        while (cursor.moveToNext())
        {
            picInfo = cursor.getString(cursor.getColumnIndex("picadress"));
        }
        cursor.close();
        db.close();

        return picInfo;
    }
}
