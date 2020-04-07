package com.example.hanan.riyadhmetro.utility;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeDialogUtility implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

    private EditText time;
    private Calendar myCalendar;
    private Context context;

    public TimeDialogUtility(EditText editText, Context context){
        this.time = editText;
        this.
        time.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.context = context;

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            new TimePickerDialog(context, this, hour, minute, true).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.time.setText( hourOfDay + ":" + minute);
    }

}