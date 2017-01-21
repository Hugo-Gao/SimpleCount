package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017/1/17.
 */

public class TableListDBHelper extends SQLiteOpenHelper
{
    private String TABLENAME;
    public TableListDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,String UserName)
    {
        super(context, name, factory, version);
        TABLENAME = UserName + "TableList";
    }


    /**
     * 以UserName作为表名
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists "+TABLENAME+"(_id integer primary key autoincrement," +
                "tableName text)");
        Log.d("haha", "创建了表" + TABLENAME);
    }

    public void create(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists "+TABLENAME+"(_id integer primary key autoincrement," +
                "tableName text)");
        Log.d("haha", "创建了表" + TABLENAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
