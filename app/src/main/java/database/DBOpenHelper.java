package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/25.
 */

public class DBOpenHelper extends SQLiteOpenHelper
{


    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL("create table if not exists BillDB(_id integer primary key autoincrement," +
                "name text not null," +
                "money integer not null," +
                "descripe text not null," +
                "pic BLOB not null," +
                "date text not null," +
                "oldpic BLOB not null," +
                "picadress text not null)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
