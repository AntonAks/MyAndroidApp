package com.antonaks.expenseincoming.dialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;

import com.antonaks.expenseincoming.R;

/**
 * Created by AntonAks on 19.08.2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ExpenseInfo extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_info);

        return builder.create();
    }
}
