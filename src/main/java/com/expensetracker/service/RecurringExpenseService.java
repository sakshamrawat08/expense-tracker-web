package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.RecurringExpense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.RecurringExpenseRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RecurringExpenseService {

    private final RecurringExpenseRepository recurringRepo;
    private final ExpenseRepository expenseRepository;

    public RecurringExpenseService(
            RecurringExpenseRepository recurringRepo,
            ExpenseRepository expenseRepository) {
        this.recurringRepo     = recurringRepo;
        this.expenseRepository = expenseRepository;
    }

    public RecurringExpense addRecurring(User user, Double amount,
            String category, String description,
            String frequency, LocalDate startDate) {
        RecurringExpense r = new RecurringExpense();
        r.setUser(user);
        r.setAmount(amount);
        r.setCategory(category);
        r.setDescription(description);
        r.setFrequency(frequency);
        r.setNextDueDate(startDate);
        r.setIsActive(true);
        return recurringRepo.save(r);
    }

    public List<RecurringExpense> getRecurring(User user) {
        return recurringRepo.findByUserAndIsActiveTrue(user);
    }

    public void deleteRecurring(Long id, User user) {
        RecurringExpense r = recurringRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Not found"));
        if (!r.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        r.setIsActive(false);
        recurringRepo.save(r);
    }

    // Runs every day at midnight — auto-adds due recurring expenses
    @Scheduled(cron = "0 0 0 * * *")
    public void processRecurringExpenses() {
        LocalDate today = LocalDate.now();
        List<RecurringExpense> dueList = recurringRepo
                .findByIsActiveTrueAndNextDueDateLessThanEqual(today);

        for (RecurringExpense r : dueList) {
            // Create actual expense
            Expense expense = new Expense();
            expense.setUser(r.getUser());
            expense.setAmount(r.getAmount());
            expense.setCategory(r.getCategory());
            expense.setDescription(
                    "[Auto] " + (r.getDescription() != null
                            ? r.getDescription() : r.getCategory()));
            expense.setDate(today);
            expense.setExpenseTime(LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("hh:mm a")));
            expenseRepository.save(expense);

            // Set next due date
            LocalDate next;
            switch (r.getFrequency()) {
                case "DAILY"   -> next = today.plusDays(1);
                case "WEEKLY"  -> next = today.plusWeeks(1);
                case "MONTHLY" -> next = today.plusMonths(1);
                default        -> next = today.plusMonths(1);
            }
            r.setNextDueDate(next);
            recurringRepo.save(r);
        }
    }
}