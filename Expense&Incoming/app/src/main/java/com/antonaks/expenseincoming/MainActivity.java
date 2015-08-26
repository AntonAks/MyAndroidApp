package com.antonaks.expenseincoming;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
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
        menu.add(0, CM_EDIT_ID, 0, "Редактировать");
        menu.add(0, CM_DELETE_ID, 0, "Удалить");
    }
    // Создаем обработчик нажатия на поле меню
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

            Toast.makeText(MainActivity.this, "Запись удалена",Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == CM_EDIT_ID){
            Toast.makeText(MainActivity.this,"Редактирование пока не возможно",Toast.LENGTH_SHORT).show();
            getLoaderManager().getLoader(0).forceLoad();
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
