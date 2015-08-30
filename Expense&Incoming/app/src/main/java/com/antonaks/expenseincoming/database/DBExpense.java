package com.antonaks.expenseincoming.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AntonAks on 29.08.2015.
 */
public class DBExpense {

    private final Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    // Имя базы данных, версия, и имя таблицы
    private static final String DB_EXP_NAME = "database_expense";
    private static final int DB_EXP_VERSION = 1;

    // Таблица для расходов DB_EXP_TABLE
    private static final String DB_EXP_TABLE = "expense_table";


    // Названия полей таблицы DB_EXP_TABLE
    private static final String COLUMN_EXP_ID = "_id";
    private static final String COLUMN_CREATE_DATE = "create_date";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_SUM = "sum";
    private static final String COLUMN_COMMENT = "comment";


    public static String getColumnCreateDate() {
        return COLUMN_CREATE_DATE;
    }
    public static String getColumnDate() {
        return COLUMN_DATE;
    }
    public static String getColumnSum() {
        return COLUMN_SUM;
    }
    public static String getColumnComment() {
        return COLUMN_COMMENT;
    }
    public static String getColumnCategory() {
        return COLUMN_CATEGORY;
    }
    public static String getDbExpCreate() {
        return DB_EXP_CREATE;
    }

    // Скрипт для создания таблицы и полей DB_EXP_TABLE
    private static final String DB_EXP_CREATE =
            "create table " + DB_EXP_TABLE + "(" +
                    COLUMN_EXP_ID + " integer primary key autoincrement, " +
                    COLUMN_CREATE_DATE + " text, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_DAY + " int, " +
                    COLUMN_MONTH + " int, " +
                    COLUMN_YEAR + " int, " +
                    COLUMN_CATEGORY + " text, " +
                    COLUMN_SUM + " real, " +
                    COLUMN_COMMENT + " text" +
                    ");";


    // конструктор класса базы данных
    public DBExpense(Context context){
        this.context = context;
    }

    // открыть подключение к базе
    public void open(){
        dbHelper = new DBHelper(context,DB_EXP_NAME,null,DB_EXP_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    // закрыть подключение к базе
    public void close() {
        if (dbHelper !=null) dbHelper.close();
    }

    // добавляем в таблицу DB_EXP_TABLE запись о расходах
    public void addExpense(String date, String category, double sum, String comment){
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

        database.insert(DB_EXP_TABLE,null,cv);
    }

    // удалить запись из DB_EXP_TABLE
    public void delSelectedItem(long id) {
        database.delete(DB_EXP_TABLE, COLUMN_EXP_ID + " = " + id, null);
    }

    // Вернуть запись из DB_EXP_TABLE по ID
    public Cursor querySelectedItem(long id){

        return database.query(DB_EXP_TABLE,
                null,
                COLUMN_EXP_ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null);
    }

    // получить все данные из таблицы DB_EXP_TABLE (+ сортировка по дате)
    public Cursor getAllData(){
        return database.query(DB_EXP_TABLE,
                null,
                null,
                null,
                null,
                null,
                COLUMN_YEAR + " DESC" + " ,"
                        + COLUMN_MONTH + " DESC" + " ,"
                        + COLUMN_DAY + " DESC");
    }

}
