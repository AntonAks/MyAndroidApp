package com.antonaks.expenseincoming;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.antonaks.expenseincoming.database.DBCategory;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CategoryActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // идентификаторы полей контекстного меню
    private static final int CM_DELETE_ID = 1;

    ListView CategoryList;
    EditText editCategory;

    DBCategory dbCategory;
    SimpleCursorAdapter simpleCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity_layout);

        dbCategory = new DBCategory(this);
        dbCategory.open();

        editCategory = (EditText) findViewById(R.id.editTextCategory);
        CategoryList = (ListView) findViewById(R.id.lvCategory);

        // формируем столбцы сопоставления
        String[] from = new String[] { dbCategory.getColumnCategory()};
        int[] to = new int[] { R.id.tvCategory};

        // создаем адаптер и настраиваем список
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.category_list, null, from, to, 0);
        CategoryList.setAdapter(simpleCursorAdapter);

        // присваиваем контекстное меню для элементов списка
        registerForContextMenu(CategoryList);

        // получаем новый курсор с данными
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Создаем контекстное меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, getResources().getString(R.string.context_delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // получаем из пункта контекстного меню данные по пункту списка
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // извлекаем id записи и удаляем соответствующую запись в БД
        dbCategory.delSelectedCategory(adapterContextMenuInfo.id);
        getLoaderManager().getLoader(0).forceLoad();

        return super.onContextItemSelected(item);
    }

    // Сохраняем категорию
    public void onSaveCategory(View view) {

        dbCategory.open();
        String category = editCategory.getText().toString();
        long id = dbCategory.addNewCategory(category);
        Cursor cursor = dbCategory.getCategoryById(id);
        cursor.moveToFirst();
        String NewCategory = cursor.getString(cursor.getColumnIndex(dbCategory.getColumnCategory())).toString();
        Toast.makeText(this,"Создана новая кагегория затрат - " + NewCategory,Toast.LENGTH_SHORT).show();
        editCategory.setText("");
        getLoaderManager().getLoader(0).forceLoad();
    }

    // Закрываем активность
    public void onCancel(View view) {
        dbCategory.close();
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this,dbCategory);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void resetToDefault(View view) {
        dbCategory.open();
        dbCategory.resetCategory();
        editCategory.setText("");
        getLoaderManager().getLoader(0).forceLoad();
    }


    static class MyCursorLoader extends CursorLoader {

        DBCategory dbCategory;

        public MyCursorLoader(Context context, DBCategory dbCategory) {
            super(context);
            this.dbCategory = dbCategory;
        }

        public Cursor loadInBackground(){
            Cursor cursor = dbCategory.getAllDataCategory();
            return cursor;
        }
    }

    // Закрываем базу при выключении
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbCategory.close();
    }
}
