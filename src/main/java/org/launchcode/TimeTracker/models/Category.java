package org.launchcode.TimeTracker.models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends AbstractEntity{

    @NotBlank(message = "Name is Required!")
    @Size(min=3, max=50, message = "Name must be between 3 and 50 characters!")
    private String name;

    @NotNull
    @OneToMany(mappedBy = "category")
    private final List<Activity> activities = new ArrayList<>();

    public Category (String name) {
        this.name = name;
    }

    public Category(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
