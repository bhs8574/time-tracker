package org.launchcode.TimeTracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class HomeController {

    //Return landing page if the user is logged in.
    @GetMapping
    public String displayHome(Model model) {
        return "welcome";
    }
}
