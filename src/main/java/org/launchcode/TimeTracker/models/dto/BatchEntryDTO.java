package org.launchcode.TimeTracker.models.dto;

import org.launchcode.TimeTracker.models.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchEntryDTO {

    private List<Activity> activities = new ArrayList<>();

    private List<Double> hoursToAdd = new ArrayList<>();

    private Map<Activity, Double> activityHours = new HashMap<>();

    public BatchEntryDTO(List<Activity> activities, List<Double> hoursToAdd, Map<Activity, Double> activityHours) {
        this.activities = activities;
        this.hoursToAdd = hoursToAdd;
        this.activityHours = activityHours;
    }

    public BatchEntryDTO(){}

    public void processTime() {
        for (int i=0; i < this.activities.size(); i++) {
            this.activities.get(i).addHours(this.hoursToAdd.get(i));
        }
    }

    public void processTimeMap() {
        //
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Double> getHoursToAdd() {
        return hoursToAdd;
    }

    public void setHoursToAdd(List<Double> hoursToAdd) {
        this.hoursToAdd = hoursToAdd;
    }

    public Map<Activity, Double> getActivityHours() {
        return activityHours;
    }

    public void setActivityHours(Map<Activity, Double> activityHours) {
        this.activityHours = activityHours;
    }
}
