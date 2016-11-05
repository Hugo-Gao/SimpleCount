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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import View.SlidingMenu;
import database.DBOpenHelper;
import id.zelory.compressor.Compressor;
import me.drakeet.materialdialog.MaterialDialog;
import tool.BillViewAdapter;
import tool.BitmapHandler;
import tool.ItemAnimition;
import tool.SharedPreferenceHelper;

public class AverageBillActivity extends Activity implements View.OnClickListener
{
    private SlidingMenu mMenu;
    private FloatingActionButton addBillButton;
    private FloatingActionButton numOfManButton;
    private EditText editText;//这个编辑框是在初始化的对话框的
    private DBOpenHelper dbHelper;
    final String TABLENAME = "BillDB";
    private List<BillBean> beanList=new ArrayList<>();
    private RecyclerView recyclerView;
    private BillViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText moneyEditText;//创建账单中的金额输入框
    private EditText descripeEditText;//创建账单中的描述输入框
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    private static final int CROP_PHOTO = 3;
    private static final int CROP_PHOTO2 = 4;
    private static final String SharedPreferenceName = "NameList";
    private final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/ASimpleCount/";
    private Uri imageUri;
    MaterialDialog materialDialog = new MaterialDialog(this);//添加人数的浮动框
    final BillBean bean = new BillBean();
    private File outputImage;
    private Uri imgUri;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPathIfNotExits(PHOTO_PATH);
        Typeface titleFont = Typeface.createFromAsset(this.getAssets(), "GenBasR.ttf");
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setTypeface(titleFont);
        dbHelper = new DBOpenHelper(this, "friends.db", null, 1);
        mMenu = (SlidingMenu) findViewById(R.id.Menu);
        addBillButton = (FloatingActionButton) findViewById(R.id.floatbutton);
        addBillButton.setOnClickListener(this);
        Button billButton1 = (Button) findViewById(R.id.bill_1);
        billButton1.setOnClickListener(this);
        Button addPersonButton = (Button) findViewById(R.id.add_people_button);
        addPersonButton.setOnClickListener(this);
        Button settlementButton = (Button) findViewById(R.id.settlemoney);
        settlementButton.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        showRecyclerView();
        if(SharedPreferenceHelper.getNameFromSharedPreferences(this,SharedPreferenceName).size()==0)
        {
            showMateriaDialog();
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
                public void onItemClick(View view, BillBean bean)
                {
                    MaterialDialog materialCardDialog = new MaterialDialog(AverageBillActivity.this);
                    materialCardDialog.setCanceledOnTouchOutside(true);
                    LayoutInflater layoutInflater = LayoutInflater.from(AverageBillActivity.this);
                    View cardView = layoutInflater.inflate(R.layout.card_dialog_layout, (ViewGroup) findViewById(R.id.card_dialogWrapper));
                    ImageView oldPic = (ImageView) cardView.findViewById(R.id.old_pic);
                    oldPic.setImageBitmap(BitmapHandler.convertByteToBitmap(bean.getOldpicInfo()));
                    materialCardDialog.setContentView(cardView);
                    materialCardDialog.show();
                }
            });
            recyclerView.setAdapter(adapter);

        }
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
            case R.id.bill_1:

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
                        intent.putExtra("aspectX", 450);//裁切的宽比例
                        intent.putExtra("aspectY", 199);//裁切的高比例
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        bitmap = tool.ImageUtil.getScaledBitmap(this, imageUri, 612.0f,  816.0f);//保存原图
                        bean.setOldpicInfo(BitmapHandler.convertBitmapToByte(bitmap));
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
                intent.putExtra("scale", true);
                intent.putExtra("aspectX", 450);//裁切的宽比例
                intent.putExtra("aspectY", 199);//裁切的高比例
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
        values.put("oldpic", bean.getPicInfo());
        values.put("date",bean.getDateInfo());
        Log.d("haha", "将原图存入数据库" + bean.getOldpicInfo());
        db.insert(TABLENAME, null, values);
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

}
