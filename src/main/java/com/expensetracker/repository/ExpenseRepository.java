package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserOrderByDateDesc(User user);

    List<Expense> findByUserAndCategoryContainingIgnoreCaseOrUserAndDescriptionContainingIgnoreCase(
            User user, String category, User user2, String description);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user")
    Double getTotalByUser(@Param("user") User user);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user " +
           "GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryTotals(@Param("user") User user);

    @Query("SELECT FUNCTION('TO_CHAR', e.date, 'YYYY-MM'), SUM(e.amount) " +
           "FROM Expense e WHERE e.user = :user " +
           "GROUP BY FUNCTION('TO_CHAR', e.date, 'YYYY-MM') " +
           "ORDER BY FUNCTION('TO_CHAR', e.date, 'YYYY-MM')")
    List<Object[]> getMonthlyTotals(@Param("user") User user);
}