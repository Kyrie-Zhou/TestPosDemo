package com.example.mydemopos.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_RECEIPT = "create table Receipt ("
            + "id integer primary key autoincrement,"
            + "entry_mode text,"
            + "trans_no text,"
            + "account text,"
            + "pinblock text,"
            + "base_amount real,"
            + "tip_amount real,"
            + "total_amount real,"
            + "expiry_date text,"
            + "auth_code text,"
            + "response text)";
    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, 24);
    }

    @Override
    //第一次创建数据库的时候就会回调这个方法
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECEIPT);
    }

    @Override
    //一般用于drop 数据表
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Receipt");
        onCreate(db);
    }
}
