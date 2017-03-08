package com.program.gyf.simplecount;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import View.SlidingMenu;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database.DBOpenHelper;
import database.TableListDBHelper;
import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;
import tool.AcivityHelper;
import tool.BitmapHandler;
import tool.CardViewAdapter;
import tool.ItemAnimition;
import tool.SharedPreferenceHelper;
import tool.WonderfulDialog;

import static android.content.ContentValues.TAG;
import static tool.AcivityHelper.finishThisActivity;
import static tool.SharedPreferenceHelper.SaveNameToSharedPreference;
import static tool.SharedPreferenceHelper.getNameStringFromSharedPreferences;
import static tool.SharedPreferenceHelper.getRealBillNameFromSharedPreferences;
import static tool.SharedPreferenceHelper.saveRealBillNameToSharedPreferences;

/**
 * 主界面Activity
 */
public class AverageBillActivity extends Activity implements View.OnClickListener
{
    public String USERNAME;
    private SlidingMenu mMenu;
    private FloatingActionButton addBillButton;
    private FloatingActionButton numOfManButton;
    private EditText editText;//这个编辑框是在初始化的对话框的
    private DBOpenHelper dbHelper;
    private TableListDBHelper tableNameDBHelper;
    private List<BillBean> beanList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText moneyEditText;//创建账单中的金额输入框
    private EditText descripeEditText;//创建账单中的描述输入框
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int CROP_PHOTO = 3;
    private static final int CROP_PHOTO2 = 4;
    private static String SharedPreferenceName;
    private final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/ASimpleCount/";
    private Uri imageUri;
    private tool.WonderfulDialog materialDialog;//添加人数的浮动框
    final BillBean bean = new BillBean();
    private File outputImage;
    private Uri imgUri;
    private int i;
    private Toolbar toolbar;
    private final String postDataUri = "http://192.168.253.1:8070/postdata";
    private final String getBillsNameUri = "http://192.168.253.1:8070/getbillsname";
    private final String postBillNameListUri = "http://192.168.253.1:8070/postBillList";
    private final String getDataUri = "http://192.168.253.1:8070/getdata";
    private final String testUri = "http://192.168.253.1:8070";
    private TextView titleText;
    private Button finish_btn;
    private TextView viewAllBillText;
    private boolean refreshFinish;
    private boolean isdisappear = true;

    @Override
    protected void onStop()
    {
        super.onStop();
        if (isdisappear)
        {
            AcivityHelper.finishThisActivity(this);
        } else
        {
            isdisappear = true;
        }
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPathIfNotExits(PHOTO_PATH);
        Intent intent = getIntent();
        refreshFinish = false;
        if (intent.hasExtra("billName"))//判断从哪个Activity跳转过来
        {
            saveRealBillNameToSharedPreferences(this, intent.getStringExtra("billName"));
        } else
        {
            Log.d("haha", "没有传入数据");
        }
        USERNAME = SharedPreferenceHelper.getTableNameBySP(this);//以用户名作为表名
        Log.d("haha", "用户名" + USERNAME);
        SharedPreferenceName = USERNAME;


        tableNameDBHelper = new TableListDBHelper(this, "TableNameList.db", null, 1, USERNAME);
        SQLiteDatabase db = tableNameDBHelper.getWritableDatabase();
        tableNameDBHelper.create(db);
        db.close();
        Typeface titleFont = Typeface.createFromAsset(this.getAssets(), "GenBasR.ttf");

        titleText = (TextView) findViewById(R.id.title);
        titleText.setTypeface(titleFont);
        titleText.setText("");
        if (!getRealBillNameFromSharedPreferences(AverageBillActivity.this).equals(""))
        {
            titleText.setText(getRealBillNameFromSharedPreferences(AverageBillActivity.this));
        }

        finish_btn = (Button) findViewById(R.id.finish_bill);
        finish_btn.setOnClickListener(this);
        viewAllBillText = (TextView) findViewById(R.id.view_all_bills);
        viewAllBillText.setOnClickListener(this);
        mMenu = (SlidingMenu) findViewById(R.id.Menu);
        addBillButton = (FloatingActionButton) findViewById(R.id.floatbutton);
        addBillButton.setOnClickListener(this);
        TextView addPersonButton = (TextView) findViewById(R.id.add_people_button);
        addPersonButton.setOnClickListener(this);
        TextView settlementButton = (TextView) findViewById(R.id.settlemoney);
        TextView returnLogButton = (TextView) findViewById(R.id.returnLog);
        returnLogButton.setOnClickListener(this);
        settlementButton.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);
                postToRemoteDB();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (checkFirstLog())
        {
            getBeanFromServer();
        }
        if (SharedPreferenceHelper.getNameFromSharedPreferences(this, SharedPreferenceName, getRealBillNameFromSharedPreferences(this)).size() == 0)
        {
            showMateriaDialog();
        }
        showRecyclerView();
    }

    private boolean checkFirstLog()
    {
        SQLiteDatabase db = tableNameDBHelper.getWritableDatabase();
        Cursor cursor = db.query(USERNAME + "TableList", null, null, null, null, null, null);
        if (cursor.getCount() == 0)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * 此函数作用是将本地数据库与服务器数据库进行同步，更新服务器端数据库,分两轮传送
     * 第一轮postBillNameListToDB()传送该用户的所有账单名称,每个账单的出行人
     * 第二轮将每个账本的每一条信息依次传送
     */
    private void postToRemoteDB()
    {

        checkServerConnect();
        List<String> billNameList = getBillList();//获取所有账单名称
        postBillNameListToDB(billNameList, USERNAME);
        final int[] count = {0};
        int sumOfBill = getAllBillNum();
        for (int index = 0; index < billNameList.size(); index++)
        {
            final String billName = billNameList.get(index);
            final List<BillBean> beanitemList = getBeanFromDataBase(billName);
            final int size = beanitemList.size();
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (int i = 0; i < size; i++)
                    {
                        OkHttpClient client = new OkHttpClient();
                        final int count2 = i + 1;
                        FormBody.Builder formBuilder = new FormBody.Builder();
                        formBuilder.add("username", USERNAME);//USERNAME就是username
                        formBuilder.add("billname", billName);
                        formBuilder.add("name", beanitemList.get(i).getName());
                        formBuilder.add("money", beanitemList.get(i).getMoneyString());
                        formBuilder.add("describe", beanitemList.get(i).getDescripInfo());
                        formBuilder.add("date", beanitemList.get(i).getDateInfo());
                        long startTime = System.currentTimeMillis();
                        formBuilder.add("picinfo", Base64.encodeToString(beanitemList.get(i).getPicInfo(), Base64.DEFAULT));
                        formBuilder.add("oldpicinfo", Base64.encodeToString(beanitemList.get(i).getOldpicInfo(), Base64.DEFAULT));
                        long endTime = System.currentTimeMillis();
                        Log.d("timelog", "billname is " + billName + " 第" + i + "条" + " 的图片转换时间为" + String.valueOf(endTime - startTime));
                        Request request = new Request.Builder().url(postDataUri).post(formBuilder.build()).build();
                        Call call = client.newCall(request);
                        call.enqueue(new Callback()
                        {
                            @Override
                            public void onFailure(Call call, final IOException e)
                            {
                                count[0]++;
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException
                            {
                                count[0]++;
                                String responseString = response.body().string();
                                Log.d("net", "同步" + billName + "第" + count2 + "条数据成功");
                                Log.d("net", "同步" + billName + "第" + count2 + "条数据状态码为" + responseString);
                            }
                        });

                    }
                }
            });
            thread.start();
        }
        final int finalSumOfBill = sumOfBill;
        final Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (finalSumOfBill != count[0])
                {

                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (swipeRefreshLayout.isRefreshing())
                        {
                            Snackbar.make(recyclerView, "同步成功", Snackbar.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

            }
        });
        thread.start();
    }

    private void checkServerConnect()
    {
        final boolean[] isConnect = {false};
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(testUri).build();
        Call call = okHttpClient.newCall(request);
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
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(AverageBillActivity.this, "未连接服务器", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String JSONString = response.body().string();
                Log.d("net", JSONString);
                if (!JSONString.equals("success"))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(AverageBillActivity.this, "未连接服务器", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void postBillNameListToDB(final List<String> billNameList, String UserName)//一次多波向服务器传送数据
    {

        final int[] count = {0};
        for (final String billName : billNameList)
        {
            OkHttpClient client = new OkHttpClient();
            String touristsString = getNameStringFromSharedPreferences(this, UserName, billName);
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("billName", billName);
            formBuilder.add("userName", UserName);
            formBuilder.add("touristsString", touristsString);
            Request request = new Request.Builder().url(postBillNameListUri).post(formBuilder.build()).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    Log.d("net", "传送billNameList和tourists失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    synchronized (count)
                    {
                        count[0]++;
                    }
                    if (count[0] == billNameList.size())
                    {
                        Log.d("net", "billNameList和tourists传送完毕");
                    } else
                    {
                        Log.d("net", "现在count是" + count[0] + " 共有" + billNameList.size());
                    }
                }
            });
        }

    }

    private List<String> getBillList()
    {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = tableNameDBHelper.getWritableDatabase();
        Cursor cursor = db.query(USERNAME + "TableList", null, null, null, null, null, null);
        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex("tableName"));
            Log.d("haha", "账本名为" + name);
            list.add(name);
        }
        cursor.close();
        db.close();
        return list;
    }

    private void showRecyclerView()
    {
        beanList = getBeanFromDataBase(getRealBillNameFromSharedPreferences(AverageBillActivity.this));
        titleText.setText(getRealBillNameFromSharedPreferences(AverageBillActivity.this));
        Log.d("haha", "数据库里有" + beanList.size() + "条数据");
        if (beanList.size() != 0)
        {
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CardViewAdapter(beanList, this);
            adapter.setOnItemClickListener(new CardViewAdapter.onRecyclerViewItemClickListen()
            {
                @Override
                public void onItemClick(View view, BillBean bean, ImageView imageView)
                {
                    intentToDetailActivity(bean, imageView);

                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 20)
                    {
                        ItemAnimition.translationToDisapper(addBillButton);
                        ItemAnimition.toolBarDisappear(toolbar);

                    } else if (dy < -10)
                    {
                        ItemAnimition.translationToAppear(addBillButton);
                        ItemAnimition.toolBarAppear(toolbar);
                    }
                }
            });

        }
        /*else//如果本地数据库没有数据，则尝试从服务器下载数据
        {
            Log.d("haha", "从服务器拉取数据");
            getBeanFromServer();
        }*/
    }

    private void intentToDetailActivity(BillBean bean, ImageView imageView)
    {
        Intent i = new Intent(AverageBillActivity.this, DetailActivity.class);
        i.putExtra("TheBeanInfo", bean.getDateInfo());
        i.putExtra("BillName", getRealBillNameFromSharedPreferences(AverageBillActivity.this));
        String transitionName = "PicShare";
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AverageBillActivity.this, imageView, transitionName);
        startActivity(i, transitionActivityOptions.toBundle());
        isdisappear = false;
    }


    /**
     * 此方法要重写!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    private void getBeanFromServer()
    {
        final SweetAlertDialog pDialog = new SweetAlertDialog(AverageBillActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在从服务器端获取数据");
        pDialog.show();
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", USERNAME);
        Request request = new Request.Builder().url(getBillsNameUri).post(formBuilder.build()).build();
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
                        try
                        {
                            pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setTitleText("服务器未连接");
                            pDialog.show();
                        } catch (Exception e)
                        {
                            pDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                try
                {
                    String jsonString = response.body().string();
                    Log.d("haha", "收到了JSON数据" + jsonString);
                    if (jsonString.equals("新用户"))
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("欢迎你新用户");
                                pDialog.setTitleText("欢迎你新用户");
                                pDialog.show();
                            }
                        });
                        return;
                    }

                    JSONObject object = new JSONObject(jsonString);
                    JSONArray jsonArray = object.getJSONArray("billsname");
                    List<String> billsName = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        billsName.add((String) jsonArray.get(i));
                    }
                    HashMap<String, String> touristsMap = new HashMap<>();
                    for (String billName : billsName)
                    {
                        String tourists = object.getString(billName);
                        touristsMap.put(billName, tourists);
                        Log.d("haha", billName + "的出游人有" + tourists);
                        saveBillNameToDB(billName);
                        SaveNameToSharedPreference(AverageBillActivity.this, tourists, SharedPreferenceName, billName);
                    }
                    getEachBillBeanFromServer(billsName, touristsMap, pDialog);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    pDialog.dismiss();
                }
            }
        });
    }


    /**
     * 获取到了所有帐单名，此方法将每个账单每条信息从服务器中取出来
     *
     * @param billsName
     * @param touristsMap
     * @param pDialog
     */
    private void getEachBillBeanFromServer(final List<String> billsName, HashMap<String, String> touristsMap, final SweetAlertDialog pDialog)
    {
        final int[] count = {0};
        for (final String billName : billsName)
        {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", USERNAME);
            formBuilder.add("billname", billName);
            Request request = new Request.Builder().url(getDataUri).post(formBuilder.build()).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    count[0]++;
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setTitleText("服务器未连接");
                            pDialog.show();

                        }
                    });
                    Log.d("haha", "接受billBean出错");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    String JSONString = response.body().string();
                    if (JSONString.equals("没有数据"))
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("云端没有数据");
                                pDialog.show();
                            }
                        });
                        return;
                    }
                    try
                    {
                        JSONObject object = new JSONObject(JSONString);
                        for (int i = 1; i <= object.length(); i++)
                        {
                            JSONObject beanObject = object.getJSONObject(String.valueOf(i));
                            Log.d("haha", "从JSon中取出" + beanObject.getString("date"));
                            BillBean bean = new BillBean();
                            bean.setName(beanObject.getString("name"));
                            bean.setDateInfo(beanObject.getString("date"));
                            bean.setMoney(beanObject.getInt("money"));
                            bean.setDescripInfo(beanObject.getString("describe"));
                            bean.setPicInfo(ByteString.decodeBase64(beanObject.getString("pic")).toByteArray());
                            bean.setOldpicInfo(ByteString.decodeBase64(beanObject.getString("oldpic")).toByteArray());
                            saveBeanToDataBase(billName, bean);

                        }
                        count[0]++;
                        if (count[0] == billsName.size())
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText("获取数据成功");
                                    pDialog.show();
                                    saveRealBillNameToSharedPreferences(AverageBillActivity.this, billsName.get(0));
                                    showRecyclerView();
                                }
                            });
                        } else
                        {
                            Log.d("haha", "count 为" + count[0]);
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            });
        }

    }


    /**
     * 获取当前账单创建时间
     */


    private String getTime()
    {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String text = formatter.format(date);
        Log.d("info", text);
        return text;
    }

    public void toggleMenu(View view)//与切换菜单按钮关联,点击切换菜单按钮的点击事件
    {

        mMenu.toggleMenu();//此方法可以自动打开或隐藏菜单
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.add_people_button:
                mMenu.toggleMenu();
                if (!showMateriaDialog())
                {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                    sweetAlertDialog.setTitleText("请先结算再创建账单！");
                    sweetAlertDialog.setCancelable(true);
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            sweetAlertDialog.dismiss();
                        }
                    });
                    sweetAlertDialog.show();
                }
                break;
            case R.id.returnLog:
                final SweetAlertDialog confirmReturnDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                confirmReturnDialog.setTitleText("你确认要退出吗");
                confirmReturnDialog.setConfirmText("确定");
                confirmReturnDialog.setCancelText("再留一会儿");
                confirmReturnDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        saveRealBillNameToSharedPreferences(AverageBillActivity.this, "");
                        Intent intent = new Intent(AverageBillActivity.this, SignAndLogActivity.class);
                        startActivity(intent);
                        AverageBillActivity.this.finish();
                    }
                });
                confirmReturnDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        confirmReturnDialog.dismiss();
                    }
                });
                confirmReturnDialog.show();
                break;
            case R.id.floatbutton://此按钮即是添加账单按钮
                if (!getRealBillNameFromSharedPreferences(this).equals(""))
                {
                    addBill();
                } else
                {
                    ItemAnimition.confirmAndBigger(addBillButton);
                    Snackbar.make(addBillButton, "请先打开侧滑菜单添加朋友!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            toggleMenu(v);
                        }
                    }).show();
                }

                break;
            case R.id.settlemoney:
                isdisappear = false;
                Intent intent = new Intent(this, SettleMoneyActivity.class);
                intent.putExtra("transition", "fade");
                intent.putExtra("BillName", getRealBillNameFromSharedPreferences(AverageBillActivity.this));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AverageBillActivity.this).toBundle());
                    finishThisActivity(this);
                } else
                {
                    startActivity(intent);
                }
                mMenu.toggleMenu();
                break;
            case R.id.finish_bill:
                finishBill();
                mMenu.toggleMenu();
                break;
            case R.id.view_all_bills:
                intentToBillListActivity();
                mMenu.toggleMenu();
                break;
        }
    }


    private void intentToBillListActivity()
    {
        Intent intent = new Intent(AverageBillActivity.this, BillListActivity.class);
        startActivity(intent);
    }

    /**
     * 完成一个账单
     */
    private void finishBill()
    {

    }

    private void addBill()
    {
        //按钮旋转并消失
        ItemAnimition.rotationAndGone(addBillButton);
        InputDescripe();
    }

    private void InputDescripe()//在对话框里输入账单信息
    {
        isdisappear = false;
        final WonderfulDialog materialDialog = new tool.WonderfulDialog(this, R.style.Dialog, R.layout.bill_descripe_layout, 300, 300);
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                ItemAnimition.rotationAndAppear(addBillButton);
            }
        });
        materialDialog.show();
        final FloatingActionButton confrimButton = (FloatingActionButton) materialDialog.findViewById(R.id.confirmButton);
        Spinner spinner = (Spinner) materialDialog.findViewById(R.id.name_spinner);
        moneyEditText = (EditText) materialDialog.findViewById(R.id.money_num_edit);
        descripeEditText = (EditText) materialDialog.findViewById(R.id.des_bill_edit);

        final List<String> nameList = SharedPreferenceHelper.getNameFromSharedPreferences(AverageBillActivity.this, SharedPreferenceName, getRealBillNameFromSharedPreferences(this));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AverageBillActivity.this, android.R.layout.simple_spinner_item, nameList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()//设置spinner的点击事件
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                bean.setName(nameList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                //Nothing
            }

        });

        confrimButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //存入数据库,并更新UI
                if ("".equals(descripeEditText.getText().toString().trim()) || "".equals(moneyEditText.getText().toString().trim()))
                {
                    ItemAnimition.confirmAndBigger(confrimButton);
                    Toast.makeText(AverageBillActivity.this, "请先往输入框输入信息", Toast.LENGTH_SHORT).show();
                } else
                {

                    ItemAnimition.confirmAndBigger(confrimButton);
                    bean.setDateInfo(getTime());
                    bean.setDescripInfo(descripeEditText.getText().toString());
                    bean.setMoney(Integer.parseInt(moneyEditText.getText().toString()));
                    //下面拍照或者从相册中选取图片
                    materialDialog.dismiss();
                    final WonderfulDialog wonderfulDialog = new WonderfulDialog(AverageBillActivity.this, R.style.Dialog, R.layout.catch_photo, 200, 300);
                    wonderfulDialog.setCancelable(false);
                    wonderfulDialog.show();
                    Button takePhotoButton = (Button) wonderfulDialog.findViewById(R.id.take_picture);
                    Button chooseGalleryButton = (Button) wonderfulDialog.findViewById(R.id.choose_from_gallery);
                    takePhotoButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            String photoName = bean.getDateInfo() + "_image.jpg";
                            Log.d("haha", "photoName is " + photoName);
                            outputImage = new File(Environment.getExternalStorageDirectory() + "/ASimpleCount/", photoName);
                            try
                            {
                                if (outputImage.exists())
                                {
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();
                                Log.d("haha", "创建图片存储目录");
                            } catch (IOException e)
                            {
                                Log.d("haha", "创建目录抛出异常");
                                e.printStackTrace();
                            }
                            String[] perms = {"android.permission.CAMERA"};
                            int permsRequestCode = 200;
                            if (!hasPermission("android.permission.CAMERA"))
                            {
                                requestPermissions(perms, permsRequestCode);
                            } else
                            {
                                imageUri = Uri.fromFile(outputImage);//将文件路径转化为Uri对象
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                intent.putExtra("name", bean.getName());
                                startActivityForResult(intent, TAKE_PHOTO);

                            }
                            wonderfulDialog.dismiss();
                        }
                    });
                    chooseGalleryButton.setOnClickListener(new View.OnClickListener()//选择从相册选择相片
                    {
                        @Override
                        public void onClick(View v)
                        {
                            String photoName = bean.getDateInfo() + "_image.jpg";
                            outputImage = new File(Environment.getExternalStorageDirectory() + "/ASimpleCount/", photoName);
                            try
                            {
                                if (outputImage.exists())
                                {
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            imageUri = Uri.fromFile(outputImage);//将文件路径转化为Uri对象
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            intent.putExtra("name", bean.getName());
                            startActivityForResult(intent, CHOOSE_PHOTO);
                            wonderfulDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults)
    {
        isdisappear = false;
        switch (permsRequestCode)
        {

            case 200:
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted)
                {
                    //授权成功之后，调用系统相机进行拍照操作等
                    imageUri = Uri.fromFile(outputImage);//将文件路径转化为Uri对象
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra("name", bean.getName());
                    startActivityForResult(intent, TAKE_PHOTO);
                    materialDialog.dismiss();
                } else
                {
                    Toast.makeText(this, "需要相机权限才能拍照哦", Toast.LENGTH_SHORT).show();
                    //用户授权拒绝之后，友情提示一下就可以了
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        isdisappear = false;
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PHOTO:
                Bitmap bitmap = null;
                if (resultCode == RESULT_OK)
                {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra("aspectX", 6);//裁切的宽比例
                    intent.putExtra("aspectY", 3);//裁切的高比例
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    bitmap = tool.ImageUtil.getScaledBitmap(this, imageUri, 612.0f, 816.0f);//保存原图
                    bean.setOldpicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                    bean.setPicadress(imageUri.toString());
                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO://从相机过来裁剪照片
                if (resultCode == RESULT_OK)
                {
                    bitmap = Compressor.getDefault(this).compressToBitmap(outputImage);
                    bean.setPicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                    /**
                     * 数据获取完毕，增加卡片
                     */
                    addNewCard(getRealBillNameFromSharedPreferences(AverageBillActivity.this));
                }
                break;
            case CHOOSE_PHOTO:
                imgUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imgUri, "image/*");
                bitmap = tool.ImageUtil.getScaledBitmap(this, imgUri, 612.0f, 816.0f);//保存原图
                bean.setOldpicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                bean.setPicadress(imgUri.toString());
                intent.putExtra("scale", true);
                intent.putExtra("aspectX", 4);//裁切的宽比例
                intent.putExtra("aspectY", 3);//裁切的高比例
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent, CROP_PHOTO2);
                break;
            case CROP_PHOTO2://从相册来裁剪照片
                if (resultCode == RESULT_OK)
                {
                    bitmap = tool.ImageUtil.getScaledBitmap(this, imgUri, 612.0f, 816.0f);
                    bean.setPicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                }
                addNewCard(getRealBillNameFromSharedPreferences(AverageBillActivity.this));
                break;
        }
    }

    private void addNewCard(String billName)
    {
        saveBeanToDataBase(billName);
        Log.d("haha", "在addcard方法中" + bean.toString());
        showRecyclerView();
    }

    /**
     * *将一个完整的bean存入数据库,注意要把bean的pic的bitmap格式转换为String
     * "name text not null," +
     * "money integer not null," +
     * "descripe text not null," +
     * "pic text not null," +
     * "date text not null)"
     */
    private void saveBeanToDataBase(String billName)
    {
        dbHelper = new DBOpenHelper(AverageBillActivity.this, "BillData.db", null, 1, billName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.createTable(db);
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        values.put("money", bean.getMoney());
        values.put("descripe", bean.getDescripInfo());
        values.put("pic", bean.getPicInfo());
        values.put("oldpic", bean.getOldpicInfo());
        values.put("date", bean.getDateInfo());
        values.put("picadress", bean.getPicadress());
        db.insert(billName, null, values);
        Log.d("haha", "成功存入" + bean.toString());
        values.clear();
        db.close();

    }


    private boolean showMateriaDialog()
    {
        if (!checkHaveSettle())
        {
            return false;
        }
        //浮动按钮的动画
        materialDialog = new tool.WonderfulDialog(this, R.style.Dialog, R.layout.dialog_layout, 230, 300);
        materialDialog.setMyTitle("此次出行的朋友的名字?");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.show();
        numOfManButton = (FloatingActionButton) materialDialog.findViewById(R.id.numofManButton);
        editText = (EditText) materialDialog.findViewById(R.id.editText);
        numOfManButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                /*
                //提取输入框中人名,并存入数据库
               */
                if ("".equals(editText.getText().toString().trim()))
                {
                    ItemAnimition.confirmAndBigger(numOfManButton);
                    Toast.makeText(AverageBillActivity.this, "请先往输入框输入信息", Toast.LENGTH_SHORT).show();
                } else
                {
                    final String nameString = editText.getText().toString();
                    materialDialog.dismiss();
                    materialDialog = new tool.WonderfulDialog(AverageBillActivity.this, R.style.Dialog, R.layout.dialog_layout, 230, 300);
                    materialDialog.setMyTitle("旅行日志名字？");
                    editText.setText("");
                    editText.setHint("");
                    materialDialog.show();
                    numOfManButton = (FloatingActionButton) materialDialog.findViewById(R.id.numofManButton);
                    editText = (EditText) materialDialog.findViewById(R.id.editText);
                    numOfManButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {
                            AnimationSet animationSet = new AnimationSet(AverageBillActivity.this, null);
                            animationSet.addAnimation(new AlphaAnimation(1f, 0f));
                            animationSet.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                            animationSet.setAnimationListener(new Animation.AnimationListener()
                            {
                                @Override
                                public void onAnimationStart(Animation animation)
                                {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation)
                                {
                                    if ("".equals(editText.getText().toString().trim()))
                                    {
                                        ItemAnimition.confirmAndBigger(numOfManButton);
                                        Toast.makeText(AverageBillActivity.this, "请先往输入框输入信息", Toast.LENGTH_SHORT).show();
                                    } else
                                    {
                                        String billName = editText.getText().toString();
                                        saveRealBillNameToSharedPreferences(AverageBillActivity.this, billName);
                                        saveBillNameToDB(billName);
                                        SharedPreferenceHelper.SaveNameToSharedPreference(AverageBillActivity.this
                                                , nameString, SharedPreferenceName, billName);
                                        materialDialog.dismiss();
                                        titleText.setText(billName);
                                        Snackbar.make(addBillButton, "保存成功" + billName, Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                SharedPreferenceHelper.deleteAllName(AverageBillActivity.this, SharedPreferenceName);
                                            }
                                        }).show();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation)
                                {

                                }
                            });
                            v.startAnimation(animationSet);
                        }
                    });

                }

            }
        });
        return true;
    }

    private boolean checkHaveSettle()
    {
        if (titleText.getText() != "")
        {
            return false;
        }
        return true;
    }

    private void saveBeanToDataBase(String BillName, BillBean bean)
    {
        dbHelper = new DBOpenHelper(AverageBillActivity.this, "BillData.db", null, 1, BillName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.createTable(db);
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        values.put("money", bean.getMoney());
        values.put("descripe", bean.getDescripInfo());
        values.put("pic", bean.getPicInfo());
        values.put("oldpic", bean.getOldpicInfo());
        values.put("date", bean.getDateInfo());
        values.put("picadress", bean.getPicadress());
        db.insert(BillName, null, values);
        Log.d("haha", "成功存入" + bean.toString());
        values.clear();
        db.close();

    }

    private void saveBillNameToDB(String name)
    {
        SQLiteDatabase db = tableNameDBHelper.getWritableDatabase();
        Cursor cursor = db.query(USERNAME + "TableList", null, "tableName = ?", new String[]{name}, null, null, null);
        if (cursor.getCount() == 0)
        {
            ContentValues values = new ContentValues();
            values.put("tableName", name);
            db.insert(USERNAME + "TableList", null, values);
            Log.d("haha", "成功将表名" + name + "存入数据库");
            values.clear();
        } else
        {
            Log.d("haha", name + "重复了");
        }
        cursor.close();
        db.close();
    }


    /**
     * name text not null," +
     * "money integer not null," +
     * "descripe text not null," +
     * "pic text not null," +
     * "date text not null)"
     *
     * @return
     */
    public List<BillBean> getBeanFromDataBase(String BillName)
    {
        dbHelper = new DBOpenHelper(AverageBillActivity.this, "BillData.db", null, 1, BillName);
        List<BillBean> beanList = new ArrayList<>();
        try
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(BillName, null, null, null, null, null, null);
            if (cursor != null)
            {
                String[] columns = cursor.getColumnNames();
                while (cursor.moveToNext())
                {
                    BillBean bean = new BillBean();
                    for (String name : columns)
                    {
                        switch (name)
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
                            case "pic":
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
                    Log.d("haha", "从数据库获得了" + bean.toString());
                    beanList.add(bean);
                }
                cursor.close();
            } else
            {
                Toast.makeText(this, "database is null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            return beanList;
        }
        return beanList;
    }

    private void createPathIfNotExits(String path)
    {
        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }
    }

    private boolean OverLollipop()
    {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private boolean hasPermission(String permission)
    {

        if (OverLollipop())
        {

            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);

        }

        return true;

    }

    private boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName)
    {
        boolean result = false;
        Cursor cursor = null;

        try
        {
            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName, "%" + columnName + "%"});
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception e)
        {
            Log.e(TAG, "checkColumnExists..." + e.getMessage());
        } finally
        {
            if (null != cursor && !cursor.isClosed())
            {
                cursor.close();
            }
        }

        return result;
    }

    public int getAllBillNum()
    {
        List<String> billNameList = getBillList();//获取所有账单名称
        int sumofbill = 0;
        for (int index = 0; index < billNameList.size(); index++)
        {
            final String billName = billNameList.get(index);
            final List<BillBean> beanitemList = getBeanFromDataBase(billName);
            sumofbill += beanitemList.size();
        }
        Log.d("haha", "共有" + sumofbill + "个bill");
        return sumofbill;
    }
}
