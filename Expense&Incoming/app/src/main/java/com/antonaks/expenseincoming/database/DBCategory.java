package com.antonaks.expenseincoming.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by AntonAks on 30.08.2015.
 * База данных для категорий затрат
 */
public class DBCategory {

    private final Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    // Имя базы данных, версия, и имя таблицы
    private static final String DB_CATEGORY_NAME = "database_category";
    private static final int DB_CATEGORY_VERSION = 1;

    // Таблица для категорий DB_CATEGORY_TABLE
    private static final String DB_CATEGORY_TABLE = "category_table";

    // Названия полей таблицы DB_EXP_TABLE
    private static final String COLUMN_CATEGORY_ID = "_id";
    private static final String COLUMN_CATEGORY_NAME = "category";

    public static String getColumnCategory() {
        return COLUMN_CATEGORY_NAME;
    }
    public static String getDbCategoryCreate() {
        return DB_CATEGORY_CREATE;
    }


    // Скрипт для создания таблицы и полей DB_CATEGORY_TABLE
    private static final String DB_CATEGORY_CREATE =
            "create table " + DB_CATEGORY_TABLE + "(" +
                    COLUMN_CATEGORY_ID + " integer primary key autoincrement, " +
                    COLUMN_CATEGORY_NAME + " text" +
                    ");";


    // конструктор класса базы данных
    public DBCategory(Context context){
        this.context = context;
    }


    // добавить новую категорию в DB_CATEGORY_TABLE
    public long addNewCategory(String NewCategory){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY_NAME, NewCategory);
        long id = database.insert(DB_CATEGORY_TABLE, null, cv);
        return id;
    }

    // удалить запись из DB_CATEGORY_TABLE
    public void delSelectedCategory(long id) {
        database.delete(DB_CATEGORY_TABLE, COLUMN_CATEGORY_ID + " = " + id, null);
    }

    public Cursor getAllDataCategory(){
        return database.query(DB_CATEGORY_TABLE,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor getCategoryById(long id){
        return database.query(DB_CATEGORY_TABLE,
                null,
                COLUMN_CATEGORY_ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null);
    }




    // открыть подключение к базе
    public void open(){
        dbHelper = new DBHelper(context,DB_CATEGORY_NAME,null,DB_CATEGORY_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    // закрыть подключение к базе
    public void close() {
        if (dbHelper !=null) dbHelper.close();
    }


}
