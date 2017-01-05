package com.program.gyf.simplecount;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import View.SlidingMenu;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database.DBOpenHelper;
import id.zelory.compressor.Compressor;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;
import tool.BillViewAdapter;
import tool.BitmapHandler;
import tool.ItemAnimition;
import tool.SharedPreferenceHelper;

import static android.content.ContentValues.TAG;

public class AverageBillActivity extends Activity implements View.OnClickListener
{
    public  String TABLENAME;
    private SlidingMenu mMenu;
    private FloatingActionButton addBillButton;
    private FloatingActionButton numOfManButton;
    private EditText editText;//这个编辑框是在初始化的对话框的
    private DBOpenHelper dbHelper;
    private List<BillBean> beanList=new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BillViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText moneyEditText;//创建账单中的金额输入框
    private EditText descripeEditText;//创建账单中的描述输入框
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    private static final int CROP_PHOTO = 3;
    private static final int CROP_PHOTO2 = 4;
    private static  String SharedPreferenceName;
    private final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/ASimpleCount/";
    private Uri imageUri;
    MaterialDialog materialDialog = new MaterialDialog(this);//添加人数的浮动框
    final BillBean bean = new BillBean();
    private File outputImage;
    private Uri imgUri;
    private Toolbar toolbar;
    private final String postDataUri="http://192.168.253.1:8080/postdata";
    private final String getDataUri="http://192.168.253.1:8080/getdata";
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPathIfNotExits(PHOTO_PATH);
        TABLENAME=SharedPreferenceHelper.getTableNameBySP(this);//以用户名作为表名
        SharedPreferenceName = TABLENAME;
        dbHelper = new DBOpenHelper(this, "friends.db", null, 1,TABLENAME);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        dbHelper.createTable(db);
        Typeface titleFont = Typeface.createFromAsset(this.getAssets(), "GenBasR.ttf");
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setTypeface(titleFont);
        mMenu = (SlidingMenu) findViewById(R.id.Menu);
        addBillButton = (FloatingActionButton) findViewById(R.id.floatbutton);
        addBillButton.setOnClickListener(this);
        Button addPersonButton = (Button) findViewById(R.id.add_people_button);
        addPersonButton.setOnClickListener(this);
        Button settlementButton = (Button) findViewById(R.id.settlemoney);
        Button returnLogButton = (Button) findViewById(R.id.returnLog);
        returnLogButton.setOnClickListener(this);
        settlementButton.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                List<BillBean> beanList = getBeanFromDataBase();
                postToRemoteDB(beanList);
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        showRecyclerView();
        /*if(SharedPreferenceHelper.getNameFromSharedPreferences(this,SharedPreferenceName).size()==0)
        {
            showMateriaDialog();
        }*/
    }


    /**
     * 此函数作用是将本地数据库与服务器数据库进行同步，更新服务器端数据库
     */
    private void postToRemoteDB(List<BillBean> beanList)
    {
        OkHttpClient client = new OkHttpClient();
        final int size = beanList.size();
        for(int i=0;i<size;i++)
        {
            final int count=i;
            FormBody.Builder formBuilder=new FormBody.Builder();
            formBuilder.add("username",TABLENAME);//TABLENAME就是username
            formBuilder.add("name", beanList.get(i).getName());
            formBuilder.add("money",beanList.get(i).getMoneyString());
            formBuilder.add("describe",beanList.get(i).getDescripInfo());
            formBuilder.add("date",beanList.get(i).getDateInfo());
            formBuilder.add("picinfo", Base64.encodeToString(beanList.get(i).getPicInfo(), Base64.DEFAULT));
            formBuilder.add("oldpicinfo", Base64.encodeToString(beanList.get(i).getOldpicInfo(), Base64.DEFAULT));
            Request request = new Request.Builder().url(postDataUri).post(formBuilder.build()).build();
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
                            Toast.makeText(AverageBillActivity.this, "未连接服务器", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    Log.d("haha", "同步第" + count + "条数据成功");
                    if(count==size-1)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                swipeRefreshLayout.setRefreshing(false);
                                Snackbar.make(recyclerView,"同步完成",Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });

        }


    }

    private void showRecyclerView()
    {
        beanList=getBeanFromDataBase();
        if(beanList.size()!=0)
        {
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new BillViewAdapter(beanList, this);
            adapter.setOnItemClickListener(new BillViewAdapter.onRecyclerViewItemClickListen()
            {
                @Override
                public void onItemClick(View view, BillBean bean,ImageView imageView)
                {
                    Intent i = new Intent(AverageBillActivity.this, DetailActivity.class);
                    i.putExtra("TheBeanInfo", bean.getDateInfo());
                    String transitionName = "PicShare";
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AverageBillActivity.this, imageView, transitionName);
                    startActivity(i, transitionActivityOptions.toBundle());
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
                    if(dy>20)
                    {
                        ItemAnimition.translationToDisapper(addBillButton);
                        ItemAnimition.toolBarDisappear(toolbar);

                    }else if(dy<-10)
                    {
                        ItemAnimition.translationToAppear(addBillButton);
                        ItemAnimition.toolBarAppear(toolbar);
                    }
                }
            });

        }
        else//如果本地数据库没有数据，则尝试从服务器下载数据
        {
            Log.d("haha", "从服务器拉取数据");
            getBeanFromServer();
        }
    }

    private void getBeanFromServer()
    {
        final SweetAlertDialog pDialog = new SweetAlertDialog(AverageBillActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在服务器端获取数据");
        pDialog.setCancelable(false);
        pDialog.show();
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", TABLENAME);
        Request request = new Request.Builder().url(getDataUri).post(formBuilder.build()).build();
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
                        pDialog.dismiss();
                        SweetAlertDialog dialog = new SweetAlertDialog(AverageBillActivity.this, SweetAlertDialog.WARNING_TYPE);
                        dialog.setTitleText("服务器未连接！！");
                        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String data = response.body().string();
                if(data.equals(""))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pDialog.dismiss();
                            SweetAlertDialog dialog = new SweetAlertDialog(AverageBillActivity.this, SweetAlertDialog.NORMAL_TYPE);
                            dialog.setTitleText("服务器上没有数据");
                            dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            dialog.setCancelable(true);
                            dialog.show();
                        }
                    });
                    return;
                }
                Log.d("haha", data);
                try
                {
                    JSONObject object = new JSONObject(data);
                    Log.d("haha", "从服务器获取到了" + object.length() + "条数据");
                    for(int i=1;i<=object.length();i++)
                    {
                        JSONObject itemObject = object.getJSONObject(String.valueOf(i));
                        String name = (String) itemObject.get("name");
                        int money = Integer.parseInt((String) itemObject.get("money"));
                        String describe=(String) itemObject.get("describe");
                        String date=(String) itemObject.get("date");
                        String oldpic=(String) itemObject.get("oldpic");
                        String pic=(String) itemObject.get("pic");
                        //将图片转换为二进制
                        ByteString byteString = ByteString.decodeBase64(pic);
                        byte[] picInfo = byteString.toByteArray();
                        ByteString byteString2 = ByteString.decodeBase64(oldpic);
                        byte[] oldpicInfo = byteString2.toByteArray();
                        BillBean newBean = new BillBean(date, picInfo, oldpicInfo, describe, name, money,"kong");
                        saveBeanToDataBase(newBean);
                        Log.d("haha", "从JSON中获得的数据是" + name + "的" + ",money is " + money + " describe is " + describe + " date is " + date);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("haha", "解析JSON时出现错误");
                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showRecyclerView();
                        Log.d("haha", "改变对话框文字");
                        pDialog.dismiss();
                        SweetAlertDialog dialog = new SweetAlertDialog(AverageBillActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        dialog.setTitleText("从服务器获取数据成功");
                        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                });
            }
        });
    }

    /**
     * 获取当前账单创建时间
     */
    private String getTime()
    {
        java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
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
                showMateriaDialog();
                break;
            case R.id.returnLog:
                final MaterialDialog confirmReturnDialog = new MaterialDialog(this);
                confirmReturnDialog.setTitle("你确认要退出吗");
                confirmReturnDialog.setCanceledOnTouchOutside(false);
                confirmReturnDialog.setPositiveButton("确定", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(AverageBillActivity.this, SignAndLogActivity.class);
                        startActivity(intent);
                        AverageBillActivity.this.finish();
                    }
                });
                confirmReturnDialog.setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        confirmReturnDialog.dismiss();
                    }
                });
                confirmReturnDialog.show();
                break;
            case R.id.floatbutton://此按钮即是添加账单按钮
                if(!(SharedPreferenceHelper.IsNameNULL(this,SharedPreferenceName)))
                {
                    addBill();
                }else
                {
                    ItemAnimition.confirmAndBigger(addBillButton);
                    Snackbar.make(addBillButton,"请先打开侧滑菜单添加朋友!",Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                        }
                    }).show();
                }

                break;
            case R.id.settlemoney:
                Intent intent = new Intent(this, SettleMoneyActivity.class);
                intent.putExtra("transition", "fade");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AverageBillActivity.this).toBundle());
                    new Thread(new Runnable()//在后台线程中关闭此活动
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(1000);
                                AverageBillActivity.this.finish();
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else
                {
                    startActivity(intent);

                }

        }
    }

    private void addBill()
    {
        //按钮旋转并消失
        ItemAnimition.rotationAndGone(addBillButton);
        InputDescripe();
    }

    private void InputDescripe()//在对话框里输入账单信息
    {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setCanceledOnTouchOutside(true);
        final LayoutInflater layoutInflater = LayoutInflater.from(AverageBillActivity.this);
        final View view = layoutInflater.inflate(R.layout.bill_descripe_layout, (ViewGroup) findViewById(R.id.wrapper_text_edit));
        materialDialog.setView(view);
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

        final Button confrimButton = (Button)view.findViewById(R.id.confirmButton);
        Spinner spinner = (Spinner) view.findViewById(R.id.name_spinner);
        moneyEditText = (EditText) view.findViewById(R.id.money_num_edit);
        descripeEditText = (EditText) view.findViewById(R.id.des_bill_edit);

        final List<String> nameList = SharedPreferenceHelper.getNameFromSharedPreferences(AverageBillActivity.this, SharedPreferenceName);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AverageBillActivity.this,android.R.layout.simple_spinner_item,nameList);
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
                if("".equals(descripeEditText.getText().toString().trim())||"".equals(moneyEditText.getText().toString().trim()))
                {
                    ItemAnimition.confirmAndBigger(confrimButton);
                    Toast.makeText(AverageBillActivity.this, "请先往输入框输入信息", Toast.LENGTH_SHORT).show();
                }else
                {

                    ItemAnimition.confirmAndBigger(confrimButton);
                    bean.setDateInfo(getTime());
                    bean.setDescripInfo(descripeEditText.getText().toString());
                    bean.setMoney(Integer.parseInt(moneyEditText.getText().toString()));
                    //下面拍照或者从相册中选取图片
                    View photoView = layoutInflater.inflate(R.layout.catch_photo, (ViewGroup) findViewById(R.id.wrapper));
                    materialDialog.setTitle("为你的日志选取图片");
                    materialDialog.setView(photoView);
                    Button takePhotoButton = (Button) photoView.findViewById(R.id.take_picture);
                    Button chooseGalleryButton = (Button) photoView.findViewById(R.id.choose_from_gallery);
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
                            if(!hasPermission("android.permission.CAMERA"))
                            {
                                requestPermissions(perms, permsRequestCode);
                            }else
                            {
                                imageUri = Uri.fromFile(outputImage);//将文件路径转化为Uri对象
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                intent.putExtra("name", bean.getName());
                                startActivityForResult(intent, TAKE_PHOTO);

                            }
                            materialDialog.dismiss();
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
                            materialDialog.dismiss();
                        }
                    });
                }
            }
        });

    }
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults)
    {
        switch(permsRequestCode)
        {

            case 200:
                boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted)
                {
                    //授权成功之后，调用系统相机进行拍照操作等
                    imageUri = Uri.fromFile(outputImage);//将文件路径转化为Uri对象
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra("name", bean.getName());
                    startActivityForResult(intent, TAKE_PHOTO);
                    materialDialog.dismiss();
                }else
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                Bitmap bitmap = null;
                    if(resultCode==RESULT_OK)
                    {
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(imageUri, "image/*");
                        intent.putExtra("scale", true);
                        intent.putExtra("aspectX", 6);//裁切的宽比例
                        intent.putExtra("aspectY", 3);//裁切的高比例
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        bitmap = tool.ImageUtil.getScaledBitmap(this, imageUri, 612.0f,  816.0f);//保存原图
                        bean.setOldpicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                        bean.setPicadress(imageUri.toString());
                        startActivityForResult(intent,CROP_PHOTO);
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
                            addNewCard();
                    }
                break;
            case CHOOSE_PHOTO:

                imgUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imgUri, "image/*");
                bitmap = tool.ImageUtil.getScaledBitmap(this, imgUri, 612.0f,  816.0f);//保存原图
                bean.setOldpicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                bean.setPicadress(imgUri.toString());
                intent.putExtra("scale", true);
                intent.putExtra("aspectX", 4);//裁切的宽比例
                intent.putExtra("aspectY", 3);//裁切的高比例
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent,CROP_PHOTO2);
                break;
            case CROP_PHOTO2://从相册来裁剪照片
                if(resultCode==RESULT_OK)
                {
                    bitmap= tool.ImageUtil.getScaledBitmap(this, imgUri, 612.0f,  816.0f);
                    bean.setPicInfo(BitmapHandler.convertBitmapToByte(bitmap));
                }
                addNewCard();
                break;
        }
    }

    private void addNewCard()
    {
        saveBeanToDataBase();
        Log.d("haha", "在addcard方法中"+bean.toString());
        showRecyclerView();
    }

    /**
     **将一个完整的bean存入数据库,注意要把bean的pic的bitmap格式转换为String
     * "name text not null," +
     "money integer not null," +
     "descripe text not null," +
     "pic text not null," +
     "date text not null)"
     */
    private void saveBeanToDataBase()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",bean.getName());
        values.put("money",bean.getMoney());
        values.put("descripe",bean.getDescripInfo());
        values.put("pic", bean.getPicInfo());
        values.put("oldpic", bean.getOldpicInfo());
        values.put("date",bean.getDateInfo());
        values.put("picadress", bean.getPicadress());
        Log.d("haha", "成功存入" + bean.toString());
        db.insert(TABLENAME, null, values);
        values.clear();
        db.close();

    }
    private void saveBeanToDataBase(BillBean bean)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",bean.getName());
        values.put("money",bean.getMoney());
        values.put("descripe",bean.getDescripInfo());
        values.put("pic", bean.getPicInfo());
        values.put("oldpic", bean.getOldpicInfo());
        values.put("date",bean.getDateInfo());
        values.put("picadress", bean.getPicadress());
        db.insert(TABLENAME, null, values);
        Log.d("haha", "成功存入" + bean.toString());
        values.clear();
        db.close();

    }

    private void showMateriaDialog()
    {

        //浮动按钮的动画
        materialDialog.setTitle("此次出行的朋友的名字?");
        materialDialog.setCanceledOnTouchOutside(true);
        final LayoutInflater layoutInflater = LayoutInflater.from(AverageBillActivity.this);
        final View view = layoutInflater.inflate(R.layout.dialog_layout, (ViewGroup) findViewById(R.id.dialog_lauout));
        materialDialog.setContentView(view);
        materialDialog.setBackgroundResource(R.color.dialogColor);
        materialDialog.show();

        numOfManButton = (FloatingActionButton) view.findViewById(R.id.numofManButton);
        editText = (EditText) view.findViewById(R.id.editText);
        numOfManButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                AnimationSet animationSet = new AnimationSet(AverageBillActivity.this, null);
                animationSet.addAnimation(new AlphaAnimation(1f,0f));
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
                        if("".equals(editText.getText().toString().trim()))
                        {
                            ItemAnimition.confirmAndBigger(numOfManButton);
                            Toast.makeText(AverageBillActivity.this, "请先往输入框输入信息", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            SaveNameToSharedPreference(v);
                            materialDialog.dismiss();
                            Snackbar.make(addBillButton, "保存成功", Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener()
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
                //提取输入框中人名,并存入数据库
                v.startAnimation(animationSet);
            }
        });

    }


    private void deleteAllData()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLENAME, null, null);
        db.close();
    }


    public void SaveNameToSharedPreference(View v)
    {
        String nameString = editText.getText().toString();
        if(nameString.contains("，"))
        {
            nameString =nameString.replace("，", ",");
            Log.d("haha", "名字字符串中有中文逗号，已修改为英文逗号");
        }
        String[] nameList = nameString.split(",");//将输入的人名保存进字符串数组
        saveToSharedPreferences(nameList);//将名字存入本地
    }

    private void saveToSharedPreferences(String[] nameList)
    {
        SharedPreferences preference = getSharedPreferences(SharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        for (int i = 0; i < nameList.length; i++)
        {
            editor.putString(nameList[i], nameList[i]);
        }
        editor.commit();
    }




    /**
     * name text not null," +
     "money integer not null," +
     "descripe text not null," +
     "pic text not null," +
     "date text not null)"
     * @return
     */
    public List<BillBean> getBeanFromDataBase()
    {
        List<BillBean> beanList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
                Log.d("haha", "从数据库获得了"+bean.toString());
                beanList.add(bean);
            }
            cursor.close();
        }else
        {
            Toast.makeText(this, "database is null", Toast.LENGTH_SHORT).show();
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

    private boolean OverLollipop(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private boolean hasPermission(String permission){

        if(OverLollipop()){

            return(checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED);

        }

        return true;

    }
    private boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try{
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        }catch (Exception e){
            Log.e(TAG,"checkColumnExists..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }

}
