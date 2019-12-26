package com.technoidtintin.justdoit.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.Model.SaveTask;
import com.technoidtintin.justdoit.Model.UserTasks;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.TimePickerFragment;
import com.technoidtintin.justdoit.databinding.ActivityAddTaskBinding;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddTaskActivity extends AppCompatActivity implements TimePickerFragment.onTimepickerClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = AddTaskActivity.class.getSimpleName();

    private List<UserTasks>userTasksList = new ArrayList<>();

    private Date newDate;

    int date, month, year;
    private ActivityAddTaskBinding addTaskBinding;
    private String timeByUser;
    private String taskTitle;
    private String taskDes = "";
    private String taskLabel;
    private SimpleDateFormat simpleDateFormat;
    private String currentDate, currentTime;
    private boolean isSaveTime, isEndTime;
    private long delay;
    private long timeInMilli;
    private String selectedDate;
    private String timeLeft;
    private long taskNo = 1;

    private String userEmail;

    private AlertDialog dialog;

    //Declaring FirebaseFirestore
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //Getting Intent
        userEmail = getIntent().getStringExtra(Constants.USER_EMAIL);

        //Getting task no.
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SAVE_TASK_NO,MODE_PRIVATE);
        taskNo = sharedPreferences.getLong(Constants.TASK_NO,1);

        //Getting current date
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        currentDate = simpleDateFormat.format(calendar.getTime());
        selectedDate = currentDate;

        //Getting integer values of date, month, year
        String[] parts = currentDate.split("/");
        date = Integer.parseInt(parts[0]);
        month = Integer.parseInt(parts[1]) - 1;
        year = Integer.parseInt(parts[2]);

        //Getting current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm aa", Locale.getDefault());
        Calendar calendar1 = Calendar.getInstance();
        currentTime = dateFormat.format(calendar1.getTime());
        Log.e(TAG, "Current Time: " + currentTime);
        timeByUser = currentTime;

        //Initializing DataBinding
        addTaskBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_task);

        addTaskBinding.addTaskSetDateEdit.setText(currentDate);
        addTaskBinding.startTimeText.setText(currentTime);
        addTaskBinding.addTaskSetTimeEdit.setText(currentTime);

        //Initializing FireBaseFireStore
        initFireStore();

        //Setting the toolbar
        setSupportActionBar(addTaskBinding.addTaskToolbar);

        //Set On Click Listener to start button
        addTaskBinding.addTaskStartButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                isEndTime = true;
                isSaveTime = false;
                addTaskBinding.startGroup.setVisibility(View.VISIBLE);
                addTaskBinding.saveGroup.setVisibility(View.GONE);
                addTaskBinding.addTaskButtonLayout.setVisibility(View.GONE);
                addTaskBinding.taskSaveFab.setVisibility(View.VISIBLE);
                addTaskBinding.taskSaveFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
            }
        });

        //Set On Click listener to save button
        addTaskBinding.addTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                isSaveTime = true;
                isEndTime = false;
                addTaskBinding.saveGroup.setVisibility(View.VISIBLE);
                addTaskBinding.startGroup.setVisibility(View.GONE);
                addTaskBinding.taskSaveFab.setVisibility(View.VISIBLE);
                addTaskBinding.addTaskButtonLayout.setVisibility(View.GONE);
            }
        });

        //Set On Click listener to floating action button
        addTaskBinding.taskSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isSaveTime){
                    taskTitle = addTaskBinding.taskTitleEditText.getText().toString();
                    taskDes = addTaskBinding.addTaskDesctiption.getText().toString();

                    if (taskTitle.isEmpty()){
                        addTaskBinding.taskTitleEditText.setError("Task must have a title");
                    }else if (delay<5){
                        Toast.makeText(getApplicationContext(),"Minimum delay to the Task is 5 min",Toast.LENGTH_SHORT).show();
                    }else {
                        dialog = createLoadingDialog(AddTaskActivity.this);
                        dialog.show();
                        addItemsToDataBase(taskTitle,taskDes,timeByUser,selectedDate,timeLeft,taskLabel);
                    }
                }
            }
        });

        //Set on click listener to set time textView
        addTaskBinding.addTaskSetTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), timePickerFragment.getTag());
            }
        });

        //Set on click listener to set date textView
        addTaskBinding.addTaskSetDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate(year, month, date, R.style.DatePickerSpinner);
            }
        });

        //Set onClickListener to end time Click listener
        addTaskBinding.endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), timePickerFragment.getTag());
            }
        });
    }

    //Initializing FireBaseFireStore
    private void initFireStore() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    //Add items to the FireBase
    private void addItemsToDataBase(String title, String description, String time, String date, String duration, String label) {

        UserTasks userTasks = new UserTasks(title,description,time,date,duration,label);
        userTasksList.add(userTasks);
        firebaseFirestore.collection("User Tasks")
                .document(userEmail)
                .collection("Saved Tasks")
                .document(changeDateFormat(selectedDate) + "Task")
                .set(new SaveTask(userTasksList))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Setting up the date picker
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    //Radio button click listener
    public void onLabelSelected(View view) {

        boolean isChecked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.personal_label:
                if (isChecked) {
                    addTaskBinding.personalLabel.setTextColor(Color.WHITE);
                    taskLabel = "Personal";
                    Log.e(TAG, "TaskLabel: " + taskLabel);
                }
                break;
            case R.id.home_label:
                if (isChecked) {
                    addTaskBinding.homeLabel.setTextColor(Color.WHITE);
                    taskLabel = "Home";
                    Log.e(TAG, "TaskLabel: " + taskLabel);
                }
                break;
            case R.id.work_label:
                if (isChecked) {
                    addTaskBinding.workLabel.setTextColor(Color.WHITE);
                    taskLabel = "Work";
                    Log.e(TAG, "TaskLabel: " + taskLabel);
                }
                break;
            default:
                taskLabel = "Personal";
                break;
        }
    }

    //Create AlertDialog
    private AlertDialog createLoadingDialog(Context context) {

        View layout = getLayoutInflater().inflate(R.layout.loading_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(layout);
        return builder.create();
    }

    //Check if the time entry is valid
    private long getDelay(String timeByUser) {
        long usertimeInMilli = getMilliseconds(selectedDate + " " + timeByUser);
        long currentInMilli = getMilliseconds(currentDate + " " + currentTime);
        Log.e(TAG, "user in milli : " + usertimeInMilli);
        Log.e(TAG, "Current in milli: " + currentInMilli);
        long difference = TimeUnit.MILLISECONDS.toMinutes(usertimeInMilli - currentInMilli);
        Log.e(TAG, "Difference: " + difference);
        return difference;
    }

    //Convert Time to miliseconds
    private long getMilliseconds(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(time);
            timeInMilli = date.getTime();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return timeInMilli;
    }

    //Get Tomorrow
    private String getTomorrow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return simpleDateFormat.format(calendar.getTime());
    }

    //Change date format
    private String changeDateFormat(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            newDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
        String formatedDate = format.format(newDate);
        Log.e(TAG, "Formatted date: " + formatedDate);
        return formatedDate;
    }
    //Convert minutes to hour and minute
    private String convertMinTohr(long min) {

        int hr = (int) (min / 60);
        int minutes = (int) (min % 60);

        return hr + "hr " + minutes + "min";
    }

    //Convert minutes to days and hour
    private  String convertMinToDay(long min){

        int day = (int) (min/1440);
        long left = min%1440;
        int hr = (int) (left/60);
        int minutes = (int)(left%60);

        return day + "Day " + hr + "hr " + minutes + "min";
    }

    //Get the duration
    private void getTaskDuration(long time){

        if (time>=1440){
            timeLeft = convertMinToDay(time);
            Toast.makeText(getApplicationContext(),"Duration is " + timeLeft, Toast.LENGTH_SHORT).show();
        }else {
            timeLeft = convertMinTohr(time);
            Toast.makeText(getApplicationContext(),"Duration is " + timeLeft, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimePickerClick(String time) {
        Log.e(TAG, "User time: " + time);

        timeByUser = time;
        delay = getDelay(timeByUser);
        if (delay < 0) {
            selectedDate = getTomorrow();
            addTaskBinding.addTaskSetDateEdit.setText(selectedDate);
            delay = getDelay(time);
            getTaskDuration(delay);
            Log.e(TAG,"Delay: " + delay);
        } else {
            getTaskDuration(delay);
            Log.e(TAG, "Delay : " + delay);
        }
        if (isSaveTime) {
            addTaskBinding.addTaskSetTimeEdit.setText(time);
        } else if (isEndTime) {
            addTaskBinding.endTimeText.setText(time);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        selectedDate = simpleDateFormat.format(calendar.getTime());
        addTaskBinding.addTaskSetDateEdit.setText(selectedDate);

        delay = getDelay(timeByUser);
        Log.e(TAG, "Delay is " + delay);
        getTaskDuration(delay);
    }
}
