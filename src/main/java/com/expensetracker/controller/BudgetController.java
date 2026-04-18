package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.service.BudgetService;
import com.expensetracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    public BudgetController(BudgetService budgetService,
                             UserService userService) {
        this.budgetService = budgetService;
        this.userService   = userService;
    }

    @GetMapping
    public String budgetPage(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("budgets",
                budgetService.getBudgetStatus(user));
        model.addAttribute("categories", List.of(
                "Food", "Transport", "Shopping", "Health",
                "Entertainment", "Utilities", "Education", "Other"));
        model.addAttribute("currentMonth",
                LocalDate.now().toString().substring(0, 7));
        model.addAttribute("user", user);
        return "budget/index";
    }

    @PostMapping("/set")
    public String setBudget(Principal principal,
            @RequestParam String category,
            @RequestParam Double limitAmount,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            String month = LocalDate.now().toString().substring(0, 7);
            budgetService.setBudget(user, category, limitAmount, month);
            redirectAttributes.addFlashAttribute("success",
                    "Budget set for " + category + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/budget";
    }

    @PostMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            budgetService.deleteBudget(id, user);
            redirectAttributes.addFlashAttribute("success",
                    "Budget removed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/budget";
    }
}