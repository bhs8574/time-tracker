package org.launchcode.TimeTracker.controllers;

import org.launchcode.TimeTracker.AuthenticationFilter;
import org.launchcode.TimeTracker.data.ActivityRepository;
import org.launchcode.TimeTracker.data.CategoryRepository;
import org.launchcode.TimeTracker.data.UserRepository;
import org.launchcode.TimeTracker.models.Activity;
import org.launchcode.TimeTracker.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("activities")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private AuthenticationFilter authenticationFilter;


    @GetMapping
    public String displayActivities (HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());
        return "activities/view";
    }


    //Figure out if there's a way to put this logic in activity instead of below for true/false
    @PostMapping
    public String processActivityTimerClick(@RequestParam Integer activityId, HttpServletRequest request, Model model) {
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isPresent()) {
            Activity anActivity = activityRepository.findById(activityId).get();
            if (anActivity.isWorking()) {
                anActivity.endWork();
                //try to put this in activity
                //anActivity.setWorking(false);
            } else {
                anActivity.setWorkStarted(new Date());
                anActivity.setWorking(true);
            }
            activityRepository.save(anActivity);
        }

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());
        return "activities/view";
    }

    @GetMapping("create")
    public String displayCreateActivityForm(HttpServletRequest request, HttpServletResponse response ,Model model) {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);
        Activity activity = new Activity();
        activity.setUser(user);
        model.addAttribute("title", "Create Activity");
        //model.addAttribute(new Activity());
        model.addAttribute(activity);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());
        return "activities/create";
    }

    @PostMapping("create")
    public String processCreateActivityForm(@ModelAttribute @Valid Activity newActivity,
                                          Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("title", "Create Activity");
            model.addAttribute("categories", categoryRepository.findAll());
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


    @PostMapping("details")
    public String processAddTimeFromDetails(@RequestParam Integer activityId, double timeToAdd, Model model) {

        Optional<Activity> optActivity = activityRepository.findById(activityId);
        if (optActivity.isPresent()) {
            optActivity.get().addHours(timeToAdd);
        }
        activityRepository.save(optActivity.get());
        return "redirect:";
    }

}
