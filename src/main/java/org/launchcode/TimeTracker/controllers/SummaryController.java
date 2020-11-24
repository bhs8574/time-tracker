package org.launchcode.TimeTracker.controllers;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.launchcode.TimeTracker.AuthenticationFilter;
import org.launchcode.TimeTracker.data.ActivityRepository;
import org.launchcode.TimeTracker.data.CategoryRepository;
import org.launchcode.TimeTracker.data.UserRepository;
import org.launchcode.TimeTracker.models.Activity;
import org.launchcode.TimeTracker.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;


@Controller
@RequestMapping("summary")
public class SummaryController {


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

    //Display a summary page of activities for the current user
    @GetMapping
    public String getSummary(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());

        return "summary/summary";
    }

    //Processes user actions from the summary page.
    @PostMapping
    public String processSummary(@RequestParam(required = false) Integer activityId,
                                 @RequestParam(required=false) String submitType,
                                 HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        if (submitType != null){
            //If the input is to clear the form, loop through the activities for the user and clear
            //the hours for the current work period.
            if (submitType.equals("Clear Hours")) {
                for (Activity activity: user.getActivities()) {
                    activity.clearHours();
                    activityRepository.save(activity);
                }
            //If export is desired, redirect to export handler
            } else if (submitType.equals("Export to .csv")){
                return "redirect:summary/export";
            //If Email summary is desired, redirect to email controller.
            } else if (submitType.equals("Export to E-mail")) {
                return "redirect:email";
            }
        }

        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());
        return "redirect:summary";
    }

    //Handles exporting summary data to a .csv file
    @GetMapping("export")
    public void exportToCSV(HttpServletResponse response, HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        //Creates a file name from the data and the logged in user
        String fileName = "Activities_Export_" + user.getUsername() + "_" + new Date().toString();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        //Sets up the file to be a normal .csv file
        StatefulBeanToCsv<Activity> writer = new StatefulBeanToCsvBuilder<Activity>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        //Writes all the data from the activity objects worked on by the current user to the file
        //TODO Look at separating out more useful data from activity rather than all of the data.
        writer.write(user.getActivities());
    }

}
