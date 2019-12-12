package com.technoidtintin.justdoit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technoidtintin.justdoit.Model.UserTasks;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.TimePickerFragment;
import com.technoidtintin.justdoit.databinding.ActivityAddTaskBinding;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = AddTaskActivity.class.getSimpleName();
    private ActivityAddTaskBinding addTaskBinding;
    private String time = "00:00";

    private String taskTitle, taskDes;

    private AlertDialog dialog;

    //Declaring FirebaseFirestore
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //Initializing DataBinding
        addTaskBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_task);

        //Initializing FireBaseFireStore
        initFireStore();

        //Setting the toolbar
        setSupportActionBar(addTaskBinding.addTaskToolbar);

        //Set On Click Listener to start button
        addTaskBinding.addTaskStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskBinding.startGroup.setVisibility(View.VISIBLE);
                addTaskBinding.saveGroup.setVisibility(View.GONE);
            }
        });

        //Set On Click listener to save button
        addTaskBinding.addTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskBinding.saveGroup.setVisibility(View.VISIBLE);
                addTaskBinding.startGroup.setVisibility(View.GONE);
                taskTitle = addTaskBinding.taskTitleEditText.getText().toString();
                taskDes = addTaskBinding.addTaskDesctiption.getText().toString();

                if (taskTitle != null && !taskDes.isEmpty()){
                    dialog = createLoadingDialog(AddTaskActivity.this);
                    dialog.show();
                    addItemsToDataBase(taskTitle,taskDes);
                }
            }
        });

        addTaskBinding.addTaskSetTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(),timePickerFragment.getTag());
                addTaskBinding.addTaskSetTimeEdit.setText(time);
            }
        });
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //Initializing FireBaseFireStore
    private void initFireStore(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    //Add items to the FireBase
    private void addItemsToDataBase(String title, String description){

        CollectionReference tasks = firebaseFirestore.collection("tasks");
        UserTasks userTasks = new UserTasks(title,description);
        tasks.add(userTasks).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                Log.e(TAG,"Id is : " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.e(TAG,"Failure: " + e.getMessage());
            }
        });
    }

    //Create AlertDialog
    private AlertDialog createLoadingDialog(Context context){

        View layout = getLayoutInflater().inflate(R.layout.loading_dialog_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(layout);
        return builder.create();
    }
}
