package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CloudinaryService cloudinaryService;

    public ExpenseService(ExpenseRepository expenseRepository,
                          CloudinaryService cloudinaryService) {
        this.expenseRepository = expenseRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Expense addExpense(User user, Double amount, String category,
                               LocalDate date, String description,
                               MultipartFile billImage) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);
        expense.setDescription(description);

        String timeNow = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("hh:mm a"));
        expense.setExpenseTime(timeNow);

        if (billImage != null && !billImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(billImage);
            expense.setBillImageUrl(imageUrl);
        }
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses(User user) {
        return expenseRepository.findByUserOrderByDateDesc(user);
    }

    public List<Expense> searchExpenses(User user, String query) {
        return expenseRepository
                .findByUserAndCategoryContainingIgnoreCaseOrUserAndDescriptionContainingIgnoreCase(
                        user, query, user, query);
    }

    public void deleteExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        expenseRepository.delete(expense);
    }

    public Expense updateExpense(Long id, User user, Double amount,
                                  String category, LocalDate date,
                                  String description,
                                  MultipartFile billImage) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);
        expense.setDescription(description);

        if (billImage != null && !billImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(billImage);
            expense.setBillImageUrl(imageUrl);
        }
        return expenseRepository.save(expense);
    }

    public Double getTotal(User user) {
        return expenseRepository.getTotalByUser(user);
    }

    public Map<String, Double> getCategoryData(User user) {
        Map<String, Double> data = new LinkedHashMap<>();
        expenseRepository.getCategoryTotals(user)
                .forEach(row -> data.put(
                        (String) row[0],
                        ((Number) row[1]).doubleValue()));
        return data;
    }

    public Map<String, Double> getMonthlyData(User user) {
        Map<String, Double> data = new LinkedHashMap<>();
        expenseRepository.getMonthlyTotals(user)
                .forEach(row -> data.put(
                        (String) row[0],
                        ((Number) row[1]).doubleValue()));
        return data;
    }
}