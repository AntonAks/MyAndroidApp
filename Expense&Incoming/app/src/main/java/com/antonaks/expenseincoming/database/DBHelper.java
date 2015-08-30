package com.antonaks.expenseincoming.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AntonAks on 14.08.2015.
 */
public class DBHelper extends SQLiteOpenHelper {


    DBExpense dbExpense;
    DBCategory dbCategory;

    // конструктор суперкласса
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // создали базу данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(dbExpense.getDbExpCreate());
        db.execSQL(dbCategory.getDbCategoryCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
