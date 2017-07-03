package com.kweisa.screenlock;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static String databaseName = "pattern.db";

    public MySQLiteOpenHelper(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE pattern (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "prepare_time INTEGER," +
                "start_time INTEGER," +
                "end_time INTEGER," +
                "cate TEXT," +
                "pattern TEXT" +
                ")";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(long prepareTime, long startTime, long endTime, String cate, String pattern) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("prepare_time", prepareTime);
        contentValues.put("start_time", startTime);
        contentValues.put("end_time", endTime);
        contentValues.put("cate", cate);
        contentValues.put("pattern", pattern);
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        sqLiteDatabase.insert("pattern", null, contentValues);
        sqLiteDatabase.close();
    }
}
