package com.antonaks.expenseincoming;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.antonaks.expenseincoming.database.DBHelperExpense;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // идентификаторы полей контекстного меню
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private static final int CM_INFO = 3;

    ListView listView;

    DBHelperExpense dbHelperExpense;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        // открываем подключение к БД
        dbHelperExpense = new DBHelperExpense(this);

        // формируем столбцы сопоставления
        String[] from = new String[] { dbHelperExpense.COLUMN_DATE, dbHelperExpense.COLUMN_CATEGORY,dbHelperExpense.COLUMN_SUM };
        int[] to = new int[] { R.id.tvText1, R.id.tvText2, R.id.tvText3 };

        // создаем адаптер и настраиваем список
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.sqlite_list, null, from, to, 0);
        listView = (ListView) findViewById(R.id.lvExpense);
        listView.setAdapter(simpleCursorAdapter);

        // присваиваем контекстное меню для элементов списка
        registerForContextMenu(listView);

        // получаем новый курсор с данными
        getLoaderManager().initLoader(0,null,this);
    }

    // Главное меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Создаем контекстное меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_EDIT_ID, 0, getResources().getString(R.string.context_edit));
        menu.add(0, CM_DELETE_ID, 0, getResources().getString(R.string.context_delete));
        menu.add(0,CM_INFO,0 ,getResources().getString(R.string.context_info));
    }

    // Создаем обработчик нажатия на поле контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        SQLiteDatabase db = dbHelperExpense.getWritableDatabase();

        if (item.getItemId() == CM_DELETE_ID){
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            dbHelperExpense.delSelectedItem(db,adapterContextMenuInfo.id);
            // получаем новый курсор с данными
            getLoaderManager().getLoader(0).forceLoad();

            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_delete_exp),Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == CM_EDIT_ID){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
        }

        else if (item.getItemId() == CM_INFO){

            String title1 = getResources().getString(R.string.dialog_category);
            String title2 = getResources().getString(R.string.dialog_sum);
            String title3 = getResources().getString(R.string.dialog_comment);
            String title4 = getResources().getString(R.string.dialog_date_create);

            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            Cursor cursor = dbHelperExpense.querySelectedItem(db,adapterContextMenuInfo.id);
            cursor.moveToFirst();

            String value1 = cursor.getString(cursor.getColumnIndex(dbHelperExpense.COLUMN_CATEGORY));
            String value2 = String.valueOf(cursor.getDouble(cursor.getColumnIndex(dbHelperExpense.COLUMN_SUM)));
            String value3 = cursor.getString(cursor.getColumnIndex(dbHelperExpense.COLUMN_COMMENT));
            String value4 = cursor.getString(cursor.getColumnIndex(dbHelperExpense.COLUMN_CREATE_DATE));

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_info)
                    .setMessage(title1 + ":  " + value1 + "\n" +
                            title2 + ":  " + value2 + "\n" +
                            title3 + ":  " + value3 + "\n" +
                            title4 + ":  " + value4)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int id) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        return super.onContextItemSelected(item);
    }

    // Переход в активность внесения затрат
    public void newExp(View view) {
        Intent intent = new Intent(this,ActivityExpense.class);
        startActivity(intent);
        finish();
    }

    // Переход в активность для тестирования
    public void testAct(View view) {
        Intent intent = new Intent(this,ActivityTest.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, dbHelperExpense);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class MyCursorLoader extends CursorLoader{

        DBHelperExpense dbHelperExpense;

        public MyCursorLoader(Context context, DBHelperExpense dbHelperExpense) {
            super(context);
            this.dbHelperExpense = dbHelperExpense;
        }

        public Cursor loadInBackground(){
            SQLiteDatabase db = dbHelperExpense.getWritableDatabase();
            Cursor cursor = dbHelperExpense.getAllData(db);
            return cursor;
        }
    }

    // Закрываем базу при выключении
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelperExpense.close();
    }
}
