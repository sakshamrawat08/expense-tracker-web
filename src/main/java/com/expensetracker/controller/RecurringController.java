package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.service.RecurringExpenseService;
import com.expensetracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/recurring")
public class RecurringController {

    private final RecurringExpenseService recurringService;
    private final UserService userService;

    public RecurringController(RecurringExpenseService recurringService,
                                UserService userService) {
        this.recurringService = recurringService;
        this.userService      = userService;
    }

    @GetMapping
    public String recurringPage(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("recurringList",
                recurringService.getRecurring(user));
        model.addAttribute("categories", List.of(
                "Food", "Transport", "Shopping", "Health",
                "Entertainment", "Utilities", "Education", "Other"));
        model.addAttribute("user", user);
        return "recurring/index";
    }

    @PostMapping("/add")
    public String addRecurring(Principal principal,
            @RequestParam Double amount,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String frequency,
            @RequestParam String startDate,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            recurringService.addRecurring(user, amount, category,
                    description, frequency, LocalDate.parse(startDate));
            redirectAttributes.addFlashAttribute("success",
                    "Recurring expense added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/recurring";
    }

    @PostMapping("/delete/{id}")
    public String deleteRecurring(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            recurringService.deleteRecurring(id, user);
            redirectAttributes.addFlashAttribute("success",
                    "Recurring expense removed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/recurring";
    }
}