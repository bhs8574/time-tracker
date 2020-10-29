package org.launchcode.TimeTracker.controllers;

import org.launchcode.TimeTracker.data.UserRepository;
import org.launchcode.TimeTracker.models.User;
import org.launchcode.TimeTracker.models.dto.LoginFormDTO;
import org.launchcode.TimeTracker.models.dto.RegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;

    private static final String userSessionKey = "user";

    //Retrieve the User object from an HttpSession.
    public User getUserFromSession(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(userSessionKey);
        if (userId == null) {
            return null;
        }

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            return null;
        }

        return user.get();
    }

    //Sets the User for an HttpSession
    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey, user.getId());
    }

    //Displays the new user registration form
    @GetMapping("register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute(new RegisterFormDTO());
        model.addAttribute("title", "Create an Account");
        return "accounts/register";
    }

    //validates and processes the new user registration form.
    @PostMapping("register")
    public String processRegistrationForm(@ModelAttribute @Valid RegisterFormDTO registerFormDTO, Errors errors,
                                          HttpServletRequest request, Model model) {
        //If the form data has errors, reload the page and tell the user
        if (errors.hasErrors()) {
            model.addAttribute("title", "Create an Account");
            return "accounts/register";
        }

        User existingUser = userRepository.findByUsername(registerFormDTO.getUsername());

        //If the user already exists, reload page and tell the user to choose a new name
        if (existingUser != null) {
            errors.rejectValue("username", "username.alreadyexists", "A user with that username already exists");
            model.addAttribute("title", "Create an Account");
            return "accounts/register";
        }

        String password = registerFormDTO.getPassword();
        String verifyPassword = registerFormDTO.getVerifyPassword();

        //If the passwords do not match, reload page and inform the user.
        if (!password.equals(verifyPassword)) {
            errors.rejectValue("password", "passwords.mismatch", "Passwords do not match");
            model.addAttribute("title", "Create an Account");
            return "accounts/register";
        }

        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword(), registerFormDTO.getEmail());
        userRepository.save(newUser);
        setUserInSession(request.getSession(), newUser);

        return "redirect:/login";
    }

    //Displays the login form
    @GetMapping("login")
    public String displayLoginForm(Model model) {
        model.addAttribute(new LoginFormDTO());
        model.addAttribute("title", "Sign In");
        return "accounts/login";
    }

    //checks user login information and logs user in if valid.
    @PostMapping("login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO, Errors errors, HttpServletRequest request,
                                   Model model) {
        //reloads the page if there errors entering data
        if (errors.hasErrors()) {
            model.addAttribute("title", "Sign In");
            return "accounts/login";
        }

        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());

        //Makes sure a username exists, otherwise reload and inform user.
        if (theUser == null) {
            errors.rejectValue("username", "user.invalid", "The given username does not exist");
            model.addAttribute("title", "Sign In");
            return "accounts/login";
        }

        String password = loginFormDTO.getPassword();

        //Make sure the password is valid, otherwise reload and inform user.
        if (!theUser.isMatchingPassword(password)) {
            errors.rejectValue("password", "password.invalid", "Invalid password");
            model.addAttribute("title", "Sign In");
            return "accounts/login";
        }

        setUserInSession(request.getSession(), theUser);

        return "redirect:";

    }

    //Invalidates the current user session, logging them out.
    @GetMapping("logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:accounts/login";
    }

}
