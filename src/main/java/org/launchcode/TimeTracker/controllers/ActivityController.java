package org.launchcode.TimeTracker.controllers;

import org.launchcode.TimeTracker.data.ActivityRepository;
import org.launchcode.TimeTracker.models.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

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

}
