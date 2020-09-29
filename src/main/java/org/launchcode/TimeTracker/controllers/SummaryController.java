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
import java.net.http.HttpHeaders;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public String getSummary(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());

        return "summary/summary";
    }

    @PostMapping
    public String processSummary(@RequestParam(required = false) Integer activityId,
                                 @RequestParam(required=false) String submitType,
                                 HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        if (submitType != null){
            if (submitType.equals("Clear Hours")) {
                for (Activity activity: user.getActivities()) {
                    activity.clearHours();
                    activityRepository.save(activity);
                }
            } else if (submitType.equals("Export to .csv")){
                return "redirect:summary/export";
            } else if (submitType.equals("Export to E-mail")) {
                String email = "placeholder";
            }
        }

        model.addAttribute("title", "All Activities");
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("user", userRepository.findById(user.getId()).get());
        return "redirect:summary";
    }

    @GetMapping("export")
    public void exportToCSV(HttpServletResponse response, HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        String fileName = "Activities_Export_" + user.getUsername() + "_" + new Date().toString();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        StatefulBeanToCsv<Activity> writer = new StatefulBeanToCsvBuilder<Activity>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        writer.write(user.getActivities());
    }

}
