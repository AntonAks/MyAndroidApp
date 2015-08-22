package com.antonaks.expenseincoming.dialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.antonaks.expenseincoming.R;

import java.util.Calendar;

/**
 * Created by AntonAks on 14.08.2015.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Piker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // определяем текущую дату
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // создаем DatePickerDialog и возвращаем его
        Dialog picker = new DatePickerDialog(getActivity(), this, year, month, day);
        picker.setTitle(getResources().getString(R.string.choose_date));
        return picker;
    }

    @Override
    public void onStart() {
        super.onStart();
        // добавляем кастомный текст для кнопки
        Button nButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        nButton.setText(getResources().getString(R.string.ready));
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        TextView SelectedDate = (TextView) getActivity().findViewById(R.id.selectedDate);

        month++;

        String dayString = String.valueOf(day);
        String monthString = String.valueOf(month);

        if(dayString.length()<2) {dayString = "0" + dayString;}
        if(monthString.length()<2) {monthString = "0" + monthString;}

        SelectedDate.setText(dayString + "." + monthString + "." + year);
    }
}
