package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.service.EmailService;
import com.expensetracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    public AuthController(UserService userService,
                          EmailService emailService) {
        this.userService  = userService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error  != null) model.addAttribute("error",
                "Invalid username or password.");
        if (logout != null) model.addAttribute("logout",
                "You have been logged out.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error",
                    "Passwords do not match!");
            return "redirect:/auth/register";
        }
        try {
            User user = userService.registerUser(
                    username, email, password);
            // Send welcome email
            emailService.sendWelcomeEmail(email, username);
            redirectAttributes.addFlashAttribute("success",
                    "Account created! Please login.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
            return "redirect:/auth/register";
        }
    }
}