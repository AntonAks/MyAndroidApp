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

import com.antonaks.expenseincoming.database.DBExpense;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // идентификаторы полей контекстного меню
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private static final int CM_INFO = 3;

    ListView listView;

    DBExpense dbExpense;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        // открываем подключение к БД
        dbExpense = new DBExpense(this);
        dbExpense.open();

        listView = (ListView) findViewById(R.id.lvCategory);

        // формируем столбцы сопоставления
        String[] from = new String[] { dbExpense.getColumnDate(), dbExpense.getColumnCategory(), dbExpense.getColumnSum() };
        int[] to = new int[] { R.id.tvText1, R.id.tvText2, R.id.tvText3 };

        // создаем адаптер и настраиваем список
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.sqlite_list, null, from, to, 0);
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

    // Обрабатываем нажатие на поля контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        dbExpense.open();

        if (item.getItemId() == CM_DELETE_ID){
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            dbExpense.delSelectedItem(adapterContextMenuInfo.id);
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
            Cursor cursor = dbExpense.querySelectedItem(adapterContextMenuInfo.id);
            cursor.moveToFirst();

            String value1 = cursor.getString(cursor.getColumnIndex(dbExpense.getColumnCategory()));
            Double value2 = cursor.getDouble(cursor.getColumnIndex(dbExpense.getColumnSum()));
            String value3 = cursor.getString(cursor.getColumnIndex(dbExpense.getColumnComment()));
            String value4 = cursor.getString(cursor.getColumnIndex(dbExpense.getColumnCreateDate()));

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
        Intent intent = new Intent(this,ExpenseActivity.class);
        startActivity(intent);
        finish();
    }

    // Переход в активность для тестирования
    public void testAct(View view) {
        Intent intent = new Intent(this,TestActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, dbExpense);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class MyCursorLoader extends CursorLoader{

        DBExpense dbExpense;

        public MyCursorLoader(Context context, DBExpense dbExpense) {
            super(context);
            this.dbExpense = dbExpense;
        }

        public Cursor loadInBackground(){
            Cursor cursor = dbExpense.getAllData();
            return cursor;
        }
    }

    // Закрываем базу при выключении
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExpense.close();
    }
}
