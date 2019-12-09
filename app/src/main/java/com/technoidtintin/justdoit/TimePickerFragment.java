package com.technoidtintin.justdoit;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.technoidtintin.justdoit.Activity.AddTaskActivity;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String timeHr, timeMIn;
    private String time;
    private AddTaskActivity addTaskActivity;
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        timeHr = String.valueOf(hourOfDay);
        timeMIn = String.valueOf(minute);
        time = timeHr + ":" + timeMIn;
        addTaskActivity.setTime(time);
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        addTaskActivity = (AddTaskActivity)getActivity();
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                !(DateFormat.is24HourFormat(getActivity())));
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
