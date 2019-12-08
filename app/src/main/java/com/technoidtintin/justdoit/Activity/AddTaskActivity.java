package com.technoidtintin.justdoit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.ActivityAddTaskBinding;

public class AddTaskActivity extends AppCompatActivity {

    private ActivityAddTaskBinding addTaskBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        addTaskBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_task);

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
            }
        });
    }
}
