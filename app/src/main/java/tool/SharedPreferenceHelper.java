package tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/10/23.
 */

public class SharedPreferenceHelper
{

    public static void SaveDeleteBillNameToSP(Context context,String billName,String USERNAME)
    {
        SharedPreferences sp = context.getSharedPreferences("deleteBillName"+USERNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> stringSet = sp.getStringSet("billNameString", new ArraySet<String>());
        Log.d("haha", "set 原来有" + stringSet.size());
        stringSet.add(billName);
        editor.remove("billNameString");
        editor.apply();
        editor.putStringSet("billNameString", stringSet);
        editor.apply();
    }

    public static Set<String> getDeleteBillNameFromSP(Context context,String USERNAME)
    {
        SharedPreferences sp = context.getSharedPreferences("deleteBillName"+USERNAME, MODE_PRIVATE);
        Set<String> stringSet = sp.getStringSet("billNameString", new ArraySet<String>());
        return stringSet;
    }

    public static boolean delSingleDeletedBillNameFromSP(Context context, String USERNAME, String billName)
    {
        SharedPreferences sp = context.getSharedPreferences("deleteBillName" + USERNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> stringSet = sp.getStringSet("billNameString", new ArraySet<String>());
        boolean flag= stringSet.remove(billName);
        Log.d("service", "删除后还剩" + stringSet.toString());
        editor.remove("billNameString");
        editor.apply();
        editor.putStringSet("billNameString", stringSet);
        editor.apply();
        return flag;
    }


    /**账单存出游人调用*/
    public static void SaveNameToSharedPreference(Context context,String nameString,String SPName,String billName)
    {
        SharedPreferences preference = context.getSharedPreferences(SPName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(billName, nameString);//以billName为键存放出行人名字
        Log.d("haha", nameString + "保存完毕");
        editor.apply();
    }


    public static  List<String> getNameFromSharedPreferences(Context context,String SPName,String billName)//从SharedPreferences取出名字
    {
        SharedPreferences preference = context.getSharedPreferences(SPName, MODE_PRIVATE);
        List<String> nameList=new ArrayList<>();
        String nameString=preference.getString(billName,"");
        if(nameString.contains("，"))
        {
            nameString =nameString.replace("，", ",");
            Log.d("haha", "名字字符串中有中文逗号，已修改为英文逗号");
        }
        String[] nameStringArray = nameString.split(",");
        for (String name : nameStringArray)
        {
            Log.d("haha", "取出了出游人" + name);
            nameList.add(name);
        }
        return nameList;
    }

    public static String getNameStringFromSharedPreferences(Context context,String SPName,String billName)
    {
        SharedPreferences preference = context.getSharedPreferences(SPName, MODE_PRIVATE);
        String nameString=preference.getString(billName,"");
        if(nameString.equals(""))
        {
            Log.d("haha", "获取出游人为空");
        }
        return nameString;
    }

    public static void saveRealBillNameToSharedPreferences(Context context,String BillName)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("realBillNameSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BillName", BillName);
        editor.apply();
    }

    public static String getRealBillNameFromSharedPreferences(Context context)
    {
        SharedPreferences preference = context.getSharedPreferences("realBillNameSP", MODE_PRIVATE);
        String billName = preference.getString("BillName", "");
        return billName;
    }

    public static boolean IsNameNULL(Context context,String SPName,String billName)
    {
        if(getNameFromSharedPreferences(context,SPName,billName).size()==0)
        {
            return true;
        }
        return false;
    }

    public static boolean deleteAllName(Context context,String SPName)
    {
         SharedPreferences preference = context.getSharedPreferences(SPName, MODE_PRIVATE);
         SharedPreferences.Editor editor = preference.edit();
         editor.clear();//清空之前的数据，这很重要
         editor.apply();
         return true;
    }
    public static String getTableNameBySP(Context context)
    {
        SharedPreferences preference = context.getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
        String username = preference.getString("username", "");
        return username;
    }

    public static boolean isLogging(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences("userLogStatus", MODE_PRIVATE);
        return sp.getBoolean("LogStatus", false);
    }

    public static void  setLoggingStatus(Context context,boolean status)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLogStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LogStatus", status);
        editor.apply();
    }
}
