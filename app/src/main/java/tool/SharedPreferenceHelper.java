package tool;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/10/23.
 */

public class SharedPreferenceHelper
{
    public static  List<String> getNameFromSharedPreferences(Context context,String SPName)//从SharedPreferences取出名字
    {
        SharedPreferences preference = context.getSharedPreferences(SPName, MODE_PRIVATE);
        HashMap<String,String> map= (HashMap<String, String>) preference.getAll();
        Collection<String> collection = map.values();
        List<String> nameList=new ArrayList<String>();
        for (String name : collection)
        {
            nameList.add(name);
        }
        return nameList;
    }
    public static boolean IsNameNULL(Context context,String SPName)
    {
        if(getNameFromSharedPreferences(context,SPName).size()==0)
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
}
