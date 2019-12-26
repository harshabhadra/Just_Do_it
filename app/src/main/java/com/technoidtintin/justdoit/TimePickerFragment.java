package com.technoidtintin.justdoit;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.technoidtintin.justdoit.Activity.AddTaskActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String timeHr, timeMIn,am_pm;
    private String time;
    private onTimepickerClickListener timepickerClickListener;
    private static final String TAG = TimePickerFragment.class.getSimpleName();

    public interface onTimepickerClickListener{
        void onTimePickerClick(String time);
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
        Log.e(TAG,"strhrstoshow: " + strHrsToShow);
        int mMinute = datetime.get(Calendar.MINUTE);
        if (mMinute<10){
          timeMIn = "0" + mMinute;
        }else {
            timeMIn = String.valueOf(mMinute);
        }

        time = strHrsToShow + ":" + timeMIn + " " + am_pm;
        timepickerClickListener.onTimePickerClick(time);
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        timepickerClickListener = (onTimepickerClickListener)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(),this,
                hour, minute,false);
    }

    public String getTimeHr() {
        return timeHr;
    }

    public String getTimeMIn() {
        return timeMIn;
    }

    public String getTime() {
        return time;
    }
}
