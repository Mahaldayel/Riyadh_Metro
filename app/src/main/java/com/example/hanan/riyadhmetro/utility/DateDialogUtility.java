package com.example.hanan.riyadhmetro.utility;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;


import java.util.Calendar;
@SuppressLint("ValidFragment")
public class DateDialogUtility extends android.app.DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText date;

    public DateDialogUtility(View view){

        date = (EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);



    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        String dateStr = day+"/"+(month+1)+"/"+year;
        date.setText(dateStr);
    }



}

