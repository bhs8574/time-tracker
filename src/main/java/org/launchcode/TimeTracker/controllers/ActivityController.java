package org.launchcode.TimeTracker.controllers;

import org.launchcode.TimeTracker.data.ActivityRepository;
import org.launchcode.TimeTracker.models.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("activities")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @GetMapping
    public String displayActivities (Model model) {
        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        return "activities/view";
    }

    @PostMapping
    public String processActivityTimerClick(@RequestParam Integer activityId, Model model) {
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isPresent()) {
            Activity anActivity = activityRepository.findById(activityId).get();
            if (anActivity.isWorking()) {
                anActivity.endWork();
                anActivity.setWorking(false);
            } else {
                anActivity.setWorkStarted(new Date());
                anActivity.setWorking(true);
            }
            activityRepository.save(anActivity);
        }

        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        return "activities/view";
    }

    @GetMapping("create")
    public String displayCreateActivityForm(Model model) {
        model.addAttribute("title", "Create Activity");
        model.addAttribute(new Activity());
        return "activities/create";
    }

    @PostMapping("create")
    public String processCreateActivityForm(@ModelAttribute @Valid Activity newActivity,
                                          Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("title", "Create Activity");
            return "activities/create";
        }

        activityRepository.save(newActivity);
        return "redirect:";
    }


    @GetMapping("details")
    public String displayActivityDetails(@RequestParam Integer activityId, Model model) {

        Optional<Activity> result = activityRepository.findById(activityId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Activity ID: " + activityId);
            model.addAttribute("noActivity", true);
        } else {
            Activity activity = result.get();
            model.addAttribute("title", activity.getName() + " Details");
            model.addAttribute("activity", activity);
        }

        return "activities/details";
    }

}
