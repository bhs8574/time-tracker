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


    //Displays all activities owned by a user in a table
    @GetMapping
    public String displayActivities (HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", user.getActivities());
        model.addAttribute("user", user.getId());
        return "activities/view";
    }

    //Handles clicking the timer button on an activity
    @PostMapping
    public String processActivityTimerClick(@RequestParam Integer activityId, HttpServletRequest request, Model model) {
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        //optional check to see if an activity is present
        if (activityOptional.isPresent()) {
            Activity anActivity = activityRepository.findById(activityId).get();
            //if activity is currently working, end the work.  otherwise it starts some work
            if (anActivity.isWorking()) {
                anActivity.endWork();
            } else {
                anActivity.setWorkStarted(new Date());
                anActivity.setWorking(true);
            }
            activityRepository.save(anActivity);
        }

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", user.getActivities());
        model.addAttribute("user", user.getId());
        return "activities/view";
    }


    //handles creating the create activity page
    @GetMapping("create")
    public String displayCreateActivityForm(HttpServletRequest request, HttpServletResponse response ,Model model) {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        //creates a new empty activity and assigns it to the session user
        Activity activity = new Activity();
        activity.setUser(user);

        model.addAttribute("title", "Create Activity");
        model.addAttribute(activity);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("user", user.getId());
        return "activities/create";
    }

    //process the form and create the activity
    @PostMapping("create")
    public String processCreateActivityForm(@ModelAttribute @Valid Activity newActivity,
                                          Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("title", "Create Activity");
            model.addAttribute("categories", categoryRepository.findAll());
            return "activities/create";
        }


        newActivity.setLifeTimeHours(newActivity.getHours());
        activityRepository.save(newActivity);
        return "redirect:";
    }


    //displays a detailed information page for an activity with the option to delete the activity or add hours.
    @GetMapping("details")
    public String displayActivityDetails(@RequestParam Integer activityId, Model model) {

        Optional<Activity> result = activityRepository.findById(activityId);

        //If an activity id that does not exist is used in the url, return an error page.  Otherwise display the activity
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


    //Handles adding hours or deleting an activity from the details page.
    @PostMapping("details")
    public String processAddTimeFromDetails(@RequestParam Integer activityId, @RequestParam(required = false) String delete, double timeToAdd, Model model) {

        Optional<Activity> optActivity = activityRepository.findById(activityId);
        //ensure the optional is actually there
        if (optActivity.isPresent()) {
            //if the time to add is a positive value larger than 0, add the time to the activity.
            if(timeToAdd > 0.0) {
                optActivity.get().addHours(timeToAdd);
                activityRepository.save(optActivity.get());
            }
            //if the delete button was clicked, delete the activity
            if(delete != null) {
                if (delete.equals("Delete")) {
                    activityRepository.delete(optActivity.get());
                }
            }
        }
        return "redirect:";
    }

    /*
    Batch entry stuff below.
     */
//
//    @GetMapping("batch")
//    public String displayBatchEntry (HttpServletRequest request, Model model) {
//        HttpSession session = request.getSession();
//        User user = authenticationController.getUserFromSession(session);
////        List<Double> hoursList = new ArrayList<>();
////
////        for (int i = 0; i < user.getActivities().size(); i++) {
////            hoursList.add(0.0);
////        }
//
//        BatchEntryDTO batchEntryDTO = new BatchEntryDTO();
//        for (Activity activity: user.getActivities()) {
//            batchEntryDTO.addActivity(activity);
//        }
//
//        model.addAttribute(new Activity());
//        model.addAttribute("title", "All Activities");
//        model.addAttribute("batchEntryDTO", batchEntryDTO);
//        return "activities/batch-entry";
//    }
//
//    @PostMapping("batch")
//    public String processBatchEntry(BatchEntryDTO batchEntryDTO, HttpServletRequest request, Model model) {
//        HttpSession session = request.getSession();
//        User user = authenticationController.getUserFromSession(session);
//
//        model.addAttribute(new Activity());
//        model.addAttribute("title", "All Activities");
//        model.addAttribute("batchEntryDTO", batchEntryDTO);
//        batchEntryDTO.processBatch();
//        for (Activity activity: batchEntryDTO.getActivities().keySet()) {
//            activityRepository.save(activity);
//        }
//
//
//        //activityRepository.save(newActivity);
//        return "activities/batch-entry";
//    }

}
