package com.technoidtintin.justdoit.Model;

import java.util.List;

public class SaveTask {

    private List<UserTasks>userTasksList;

    public SaveTask(List<UserTasks> userTasksList) {
        this.userTasksList = userTasksList;
    }

    public List<UserTasks> getUserTasksList() {
        return userTasksList;
    }

    public void setUserTasksList(List<UserTasks> userTasksList) {
        this.userTasksList = userTasksList;
    }
}
