package org.launchcode.TimeTracker.controllers;

import org.launchcode.TimeTracker.AuthenticationFilter;
import org.launchcode.TimeTracker.data.ActivityRepository;
import org.launchcode.TimeTracker.data.CategoryRepository;
import org.launchcode.TimeTracker.data.UserRepository;
import org.launchcode.TimeTracker.models.Activity;
import org.launchcode.TimeTracker.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@RequestMapping("email")
public class EmailController {

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

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping
    public String sendSummaryEmail(HttpServletRequest request, Model model, final Locale locale) throws MessagingException {

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        //Here the email body is built.  Currently it is just used with a string builder.
        //Later I want to implement a template system.
        //TODO set up email template
        StringBuilder emailBody = new StringBuilder("Hello, " + user.getUsername() + "!\n" +
                "Here is a summary of your work so far: \n\n");
        for (Activity activity: user.getActivities()) {
            emailBody.append("Activity: ");
            emailBody.append(activity.getName());
            emailBody.append("\n");
            emailBody.append("Hours worked: ");
            emailBody.append(activity.getHours());
            emailBody.append("\n\n");
        }

        emailBody.append("\nThank you!");

        //Creates and sends the email using the javaMail package and data from the current logged user.
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Activity Summary for " + user.getUsername());
        message.setFrom("TimeTrackerNotifications@gmail.com");
        message.setTo(user.getEmail());
        message.setText(emailBody.toString());

        this.mailSender.send(mimeMessage);

        return "redirect:summary";
    }

}
