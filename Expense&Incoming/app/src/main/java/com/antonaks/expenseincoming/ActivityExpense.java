package com.antonaks.expenseincoming;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antonaks.expenseincoming.database.Categories;
import com.antonaks.expenseincoming.database.DBHelperExpense;
import com.antonaks.expenseincoming.dialog.Piker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityExpense extends Activity implements View.OnClickListener {

    Button btnAdd;
    EditText editTextSum, editTextComment;
    DBHelperExpense dbHelperExpense;
    RadioGroup radioGroupDay;
    RadioButton radioButtonYesterday, radioButtonToday, radioButtonAnotherDay;
    ArrayAdapter<String> adapter;
    TextView SelectedDate;
    SimpleDateFormat sdf;
    Spinner spinner;
    Categories categories = new Categories();
    String [] expCategories = categories.getExpCategories();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_expense);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        editTextSum = (EditText) findViewById(R.id.etSum);
        editTextComment = (EditText) findViewById(R.id.etComment);
        radioGroupDay = (RadioGroup) findViewById(R.id.radioGroupDay);
        radioButtonYesterday = (RadioButton) findViewById(R.id.radioButtonYesterday);
        radioButtonYesterday.setOnClickListener(this);
        radioButtonToday = (RadioButton) findViewById(R.id.radioToday);
        radioButtonToday.setOnClickListener(this);
        radioButtonAnotherDay = (RadioButton) findViewById(R.id.radioButtonAnotherDay);
        radioButtonAnotherDay.setOnClickListener(this);

        // создаем объект для создания и управления версиями БД
        dbHelperExpense = new DBHelperExpense(this);

        // адаптер для спинера
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,expCategories);
        spinner = (Spinner) findViewById(R.id.spinCategory);
        spinner.setAdapter(adapter);
        // заголовок для спинера
        spinner.setPrompt("Category");

        SelectedDate = (TextView) findViewById(R.id.selectedDate);
        sdf = new SimpleDateFormat("dd.MM.yyyy");

        SelectedDate.setText(String.valueOf(sdf.format(new Date(System.currentTimeMillis()))));

    }

    // Главное меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void goHome(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Обработка нажатия кнопок
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override

    public void onClick(View v) {

        // Открываем доступ к базе
        SQLiteDatabase db = dbHelperExpense.getWritableDatabase();

    switch (v.getId()){
        case R.id.btnAdd:       // нажатие кнопки ADD
            // определяем спинер
            spinner = (Spinner) findViewById(R.id.spinCategory);
            // присваиваем переменным значения
            String date = String.valueOf(SelectedDate.getText());
            String category = (String) spinner.getSelectedItem();
            String comment = editTextComment.getText().toString();

            try {
                Double sum = Double.parseDouble(editTextSum.getText().toString());
            // вызвали метод для записи данных в базу
            long id = dbHelperExpense.record(db,date,category,sum,comment);
            dbHelperExpense.close();
            Toast.makeText(this, "Добавлена запись в категорию " + id, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();

            }catch (Exception e){Toast.makeText(this,"Внесите значение суммы",Toast.LENGTH_LONG).show();}

            break;

            // обработка выбора даты
        case R.id.radioToday:
            SelectedDate.setText(String.valueOf(sdf.format(new Date(System.currentTimeMillis()))));
            break;

        case R.id.radioButtonYesterday:
            SelectedDate.setText(String.valueOf(sdf.format(new Date(System.currentTimeMillis()-86400000))));
            break;

        case R.id.radioButtonAnotherDay:
            DialogFragment dateDialog = new Piker();
            dateDialog.show(getFragmentManager(),"datePicker");
            break;
        }
    }
}


