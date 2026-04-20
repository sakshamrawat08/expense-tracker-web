package com.expensetracker.repository;

import com.expensetracker.model.RecurringExpense;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringExpenseRepository
        extends JpaRepository<RecurringExpense, Long> {

    List<RecurringExpense> findByUserAndIsActiveTrue(User user);

    List<RecurringExpense> findByIsActiveTrueAndNextDueDateLessThanEqual(
            LocalDate date);
}
