package org.launchcode.TimeTracker.models.dto;

import org.launchcode.TimeTracker.models.Activity;

import java.util.HashMap;
import java.util.Map;

public class BatchEntryDTO {

    private Map<Activity, Double> activities = new HashMap<>();

    public BatchEntryDTO(Map<Activity, Double> activities) {
        this.activities = activities;
    }

    public BatchEntryDTO(){}


    public void addActivity(Activity activity) {
        this.activities.put(activity, 0.0);
    }

    public void setHours(Activity activity, Double hours) {
        this.activities.remove(activity);
        this.activities.put(activity, hours);
    }

    public void processBatch() {
        for (Map.Entry<Activity, Double> entry : this.activities.entrySet() ) {
            entry.getKey().addHours(entry.getValue());
        }
    }

    public Map<Activity, Double> getActivities() {
        return activities;
    }

    public void setActivities(Map<Activity, Double> activities) {
        this.activities = activities;
    }
}
