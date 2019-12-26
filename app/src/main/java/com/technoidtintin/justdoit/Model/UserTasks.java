package com.technoidtintin.justdoit.Model;

public class UserTasks {

    private String title;
    private String description;
    private String time;
    private String date;
    private String duration;
    private String label;

    public UserTasks(String title, String description, String time, String date, String duration, String label) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.date = date;
        this.duration = duration;
        this.label = label;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getLabel() {
        return label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
