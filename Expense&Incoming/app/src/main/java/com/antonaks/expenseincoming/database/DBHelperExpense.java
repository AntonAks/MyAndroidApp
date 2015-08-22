package com.antonaks.expenseincoming.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AntonAks on 14.08.2015.
 */
public class DBHelperExpense extends SQLiteOpenHelper {

    String LOG = "myLOG";

    private static final String DB_EXP_NAME = "database_new";
    private static final int DB_EXP_VERSION = 1;
    private static final String DB_EXP_TABLE = "exp_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATE_DATE = "create_date";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_SUM = "sum";
    public static final String COLUMN_COMMENT = "comment";

    private static final String DB_EXP_CREATE =
            "create table " + DB_EXP_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_CREATE_DATE + " text, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_DAY + " int, " +
                    COLUMN_MONTH + " int, " +
                    COLUMN_YEAR + " int, " +
                    COLUMN_CATEGORY + " text, " +
                    COLUMN_SUM + " real, " +
                    COLUMN_COMMENT + " text" +
                    ");";

    // конструктор суперкласса
    public DBHelperExpense(Context context) {
        super(context, DB_EXP_NAME, null, DB_EXP_VERSION);
    }

    // создали базу данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_EXP_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Метод добавляет запись в таблицу
    public long record (SQLiteDatabase db, String date, String category, double sum, String comment){

        // Устанавливаем дату создания записи
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy  hh:mm");
        String createDate = simpleDateFormat.format(new Date());

        // Разбиваем дату по эллементам для сортировки
        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5));
        int year = Integer.parseInt(date.substring(6,10));

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CREATE_DATE, createDate);
        cv.put(COLUMN_DATE,date);
        cv.put(COLUMN_CATEGORY,category);
        cv.put(COLUMN_SUM,sum);
        cv.put(COLUMN_DAY,day);
        cv.put(COLUMN_MONTH,month);
        cv.put(COLUMN_YEAR,year);
        cv.put(COLUMN_COMMENT,comment);

        db.insert(DB_EXP_TABLE,null,cv);

        return year;
    }

    // Удаляет запись по ID
    public void delSelectedItem(SQLiteDatabase db, long id){
        db.delete(DB_EXP_TABLE,COLUMN_ID + " = " + id, null);
    }

    // получить все данные из таблицы DB_EXP_TABLE (+ сортировка по дате)
    public Cursor getAllData(SQLiteDatabase db){
        return db.query(DB_EXP_TABLE, null,
                null,
                null,
                null,
                null,
                COLUMN_YEAR + " DESC" + " ,"
                        + COLUMN_MONTH + " DESC" + " ,"
                        + COLUMN_DAY + " DESC");
    }
}
