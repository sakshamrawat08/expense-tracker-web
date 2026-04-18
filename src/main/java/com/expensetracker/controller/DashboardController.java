package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.*;
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
    private final BudgetService budgetService;
    private final CurrencyService currencyService;

    public DashboardController(ExpenseService expenseService,
                                UserService userService,
                                BudgetService budgetService,
                                CurrencyService currencyService) {
        this.expenseService  = expenseService;
        this.userService     = userService;
        this.budgetService   = budgetService;
        this.currencyService = currencyService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "INR") String currency) {

        User user = userService.findByUsername(principal.getName());
        List<Expense> expenses = (search != null && !search.isBlank())
                ? expenseService.searchExpenses(user, search)
                : expenseService.getAllExpenses(user);

        String currentMonth = LocalDate.now().toString()
                .substring(0, 7);
        double monthTotal = expenses.stream()
                .filter(e -> e.getDate() != null &&
                        e.getDate().toString().startsWith(currentMonth))
                .mapToDouble(Expense::getAmount).sum();

        double total = expenseService.getTotal(user);

        // Convert to selected currency
        double convertedTotal = currencyService.convert(total, currency);
        double convertedMonth = currencyService.convert(monthTotal, currency);
        String symbol = currencyService.getSymbol(currency);

        model.addAttribute("user",        user);
        model.addAttribute("expenses",    expenses);
        model.addAttribute("total",       convertedTotal);
        model.addAttribute("monthTotal",  convertedMonth);
        model.addAttribute("symbol",      symbol);
        model.addAttribute("currency",    currency);
        model.addAttribute("currencies",  currencyService.getAllCurrencies());
        model.addAttribute("budgets",     budgetService.getBudgetStatus(user));
        model.addAttribute("search",      search);
        model.addAttribute("today",       LocalDate.now().toString());
        model.addAttribute("categories",  List.of(
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
                .orElseThrow(() ->
                        new RuntimeException("Not found"));
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
            Principal principal,
            @RequestParam(defaultValue = "INR") String currency) {
        User user = userService.findByUsername(principal.getName());
        Map<String, Double> catData = expenseService.getCategoryData(user);
        Map<String, Double> monData = expenseService.getMonthlyData(user);

        // Convert values
        Map<String, Double> convertedCat = new LinkedHashMap<>();
        catData.forEach((k, v) -> convertedCat.put(k,
                currencyService.convert(v, currency)));

        Map<String, Double> convertedMon = new LinkedHashMap<>();
        monData.forEach((k, v) -> convertedMon.put(k,
                currencyService.convert(v, currency)));

        Map<String, Object> data = new HashMap<>();
        data.put("categoryData", convertedCat);
        data.put("monthlyData",  convertedMon);
        data.put("total", currencyService.convert(
                expenseService.getTotal(user), currency));
        data.put("symbol", currencyService.getSymbol(currency));
        return ResponseEntity.ok(data);
    }
}