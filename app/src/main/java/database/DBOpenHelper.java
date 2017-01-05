package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/9/25.
 */

public class DBOpenHelper extends SQLiteOpenHelper
{
    private String TABLENAME;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,String TableName)
    {
        super(context, name, factory, version);
        TABLENAME = TableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {


        db.execSQL("create table if not exists "+TABLENAME+"(_id integer primary key autoincrement," +
                "name text not null," +
                "money integer not null," +
                "descripe text not null," +
                "pic BLOB not null," +
                "date text not null," +
                "oldpic BLOB not null," +
                "picadress text not null)");
        Log.d("haha", "创建了表" + TABLENAME);

    }

    public void createTable(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists "+TABLENAME+"(_id integer primary key autoincrement," +
                "name text not null," +
                "money integer not null," +
                "descripe text not null," +
                "pic BLOB not null," +
                "date text not null," +
                "oldpic BLOB not null," +
                "picadress text)");
        Log.d("haha", "创建了表" + TABLENAME);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
