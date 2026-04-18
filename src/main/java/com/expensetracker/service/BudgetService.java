package com.expensetracker.service;

import com.expensetracker.model.Budget;
import com.expensetracker.model.User;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final EmailService emailService;

    public BudgetService(BudgetRepository budgetRepository,
                         ExpenseRepository expenseRepository,
                         EmailService emailService) {
        this.budgetRepository  = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.emailService      = emailService;
    }

    public void setBudget(User user, String category,
                          Double limit, String month) {
        Optional<Budget> existing = budgetRepository
                .findByUserAndCategoryAndMonth(user, category, month);
        Budget budget = existing.orElse(new Budget());
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(limit);
        budget.setMonth(month);
        budgetRepository.save(budget);
    }

    public List<Budget> getBudgets(User user) {
        String month = LocalDate.now().toString().substring(0, 7);
        return budgetRepository.findByUserAndMonth(user, month);
    }

    // Returns map of category -> {limit, spent, percent}
    public List<Map<String, Object>> getBudgetStatus(User user) {
        String month = LocalDate.now().toString().substring(0, 7);
        List<Budget> budgets = budgetRepository
                .findByUserAndMonth(user, month);
        Map<String, Double> categorySpent =
                new LinkedHashMap<>();

        expenseRepository.getCategoryTotals(user)
                .forEach(row -> categorySpent.put(
                        (String) row[0],
                        ((Number) row[1]).doubleValue()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Budget b : budgets) {
            Map<String, Object> item = new LinkedHashMap<>();
            double spent = categorySpent
                    .getOrDefault(b.getCategory(), 0.0);
            double percent = (spent / b.getLimitAmount()) * 100;
            item.put("category",  b.getCategory());
            item.put("limit",     b.getLimitAmount());
            item.put("spent",     spent);
            item.put("percent",   Math.min(percent, 100));
            item.put("exceeded",  spent > b.getLimitAmount());
            item.put("id",        b.getId());
            result.add(item);

            // Send email alert if exceeded
            if (spent > b.getLimitAmount()) {
                emailService.sendBudgetAlert(
                        user.getEmail(),
                        user.getUsername(),
                        b.getCategory(),
                        b.getLimitAmount(),
                        spent);
            }
        }
        return result;
    }

    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Budget not found"));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        budgetRepository.delete(budget);
    }
}