package org.launchcode.TimeTracker.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Activity extends AbstractEntity {

    @NotBlank(message = "Name is Required!")
    @Size(min=3, max=50, message = "Name must be between 3 and 50 characters!")
    private String name;

    @NotNull
    @ManyToOne
    private Category category;

    @ManyToOne
    private User user;

    @Size(max=500, message = "Description can be a maximum of 500 characters!")
    private String description;

    private double hoursToAdd;

    private double hours;

    private double lifeTimeHours;

    private boolean working;

    private Date workStarted;

    public Activity(String name, double hoursAccumulated, boolean working, Date workStarted, double lifeTimeHours) {
        this.name = name;
        this.hours = hoursAccumulated;
        this.working = working;
        this.workStarted = workStarted;
        this.lifeTimeHours = hoursAccumulated;
    }

    public Activity() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hoursAccumulated) {
        this.hours = hoursAccumulated;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public Date getWorkStarted() {
        return workStarted;
    }

    public void setWorkStarted(Date workStarted) {
        this.workStarted = workStarted;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHoursToAdd() {
        return hoursToAdd;
    }

    public void setHoursToAdd(double hoursToAdd) {
        this.hoursToAdd = hoursToAdd;
    }

    public double getLifeTimeHours() {
        return lifeTimeHours;
    }

    public void setLifeTimeHours(double lifeTimeHours) {
        this.lifeTimeHours = lifeTimeHours;
    }

    public void endWork() {
        if (! (this.workStarted == null)) {
            Date timeNow = new Date();
            double hoursToAdd = (timeNow.getTime() - this.workStarted.getTime())/(60.0*60.0*1000);
            this.hours += hoursToAdd;
            this.lifeTimeHours += hoursToAdd;
            this.workStarted = null;
            this.working = false;
        }
    }

    public void addHours(double hoursToAdd) {
        this.setHours(this.getHours()+hoursToAdd);
        this.setLifeTimeHours(this.getLifeTimeHours()+hoursToAdd);
    }

    public void clearHours() {
        this.setHours(0.0);
    }


}
