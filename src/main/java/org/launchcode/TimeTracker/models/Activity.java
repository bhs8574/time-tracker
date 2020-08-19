package org.launchcode.TimeTracker.models;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Activity extends AbstractEntity {

    @NotBlank(message = "Name is Required!")
    @Size(min=3, max=50, message = "Name must be between 3 and 50 characters!")
    private String name;

    private double hours;

    public Activity(String name, double hoursAccumulated, boolean working, Date workStarted) {
        this.name = name;
        this.hours = hoursAccumulated;
    }

    public Activity() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHoursAccumulated() {
        return hours;
    }

    public void setHoursAccumulated(double hoursAccumulated) {
        this.hours = hoursAccumulated;
    }


}
