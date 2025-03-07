package com.jmd0.events.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jmd0.events.models.UserModel;
import com.jmd0.events.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserModel());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserModel user, Model model) {
        UserModel existingUser = userService.findByLoginName(user.getusername());
        if (existingUser != null) {
            model.addAttribute("error", "User already exists!");
            model.addAttribute("user", user);
            return "register";
        }
      
        setDefaultValues(user);
        userService.save(user);
        logger.info("User registered: {}", user.getusername());
        model.addAttribute("user", user);
        return "redirect:/users/loginForm";
    }

    private void setDefaultValues(UserModel user) {
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
    }

    @GetMapping("/loginForm")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("pageTitle", "Login");
        return "login";
    }
 
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/loginForm";
    }
}
