package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class DashboardController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public DashboardController(ExpenseService expenseService,
                                UserService userService) {
        this.expenseService = expenseService;
        this.userService    = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model,
            @RequestParam(required = false) String search) {

        User user = userService.findByUsername(principal.getName());

        List<Expense> expenses = (search != null && !search.isBlank())
                ? expenseService.searchExpenses(user, search)
                : expenseService.getAllExpenses(user);

        // ── Calculate this month's total in Java (not Thymeleaf) ──
        String currentMonth = LocalDate.now().toString().substring(0, 7);
        double monthTotal = expenses.stream()
                .filter(e -> e.getDate() != null &&
                        e.getDate().toString().startsWith(currentMonth))
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("user",       user);
        model.addAttribute("expenses",   expenses);
        model.addAttribute("total",      expenseService.getTotal(user));
        model.addAttribute("monthTotal", monthTotal);
        model.addAttribute("search",     search);
        model.addAttribute("today",      LocalDate.now().toString());
        model.addAttribute("categories", List.of(
                "Food", "Transport", "Shopping", "Health",
                "Entertainment", "Utilities", "Education", "Other"));

        return "dashboard/index";
    }

    @PostMapping("/expenses/add")
    public String addExpense(Principal principal,
            @RequestParam Double amount,
            @RequestParam String category,
            @RequestParam String date,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile billImage,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            expenseService.addExpense(user, amount, category,
                    LocalDate.parse(date), description, billImage);
            redirectAttributes.addFlashAttribute("success",
                    "Expense added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            expenseService.deleteExpense(id, user);
            redirectAttributes.addFlashAttribute("success", "Deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/expenses/edit/{id}")
    public String editPage(@PathVariable Long id,
            Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        Expense expense = expenseService.getAllExpenses(user).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("expense", expense);
        model.addAttribute("categories", List.of(
                "Food", "Transport", "Shopping", "Health",
                "Entertainment", "Utilities", "Education", "Other"));
        return "expenses/edit";
    }

    @PostMapping("/expenses/edit/{id}")
    public String updateExpense(@PathVariable Long id,
            Principal principal,
            @RequestParam Double amount,
            @RequestParam String category,
            @RequestParam String date,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile billImage,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            expenseService.updateExpense(id, user, amount, category,
                    LocalDate.parse(date), description, billImage);
            redirectAttributes.addFlashAttribute("success", "Updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/api/chart-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chartData(
            Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Map<String, Object> data = new HashMap<>();
        data.put("categoryData", expenseService.getCategoryData(user));
        data.put("monthlyData",  expenseService.getMonthlyData(user));
        data.put("total",        expenseService.getTotal(user));
        return ResponseEntity.ok(data);
    }
}