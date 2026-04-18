package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recurring_expenses")
public class RecurringExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String category;

    @Column
    private String description;

    @Column(nullable = false)
    private String frequency; // "DAILY", "WEEKLY", "MONTHLY"

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RecurringExpense() {}

    public Long getId()                  { return id; }
    public Double getAmount()            { return amount; }
    public String getCategory()          { return category; }
    public String getDescription()       { return description; }
    public String getFrequency()         { return frequency; }
    public LocalDate getNextDueDate()    { return nextDueDate; }
    public Boolean getIsActive()         { return isActive; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public User getUser()                { return user; }

    public void setId(Long id)                   { this.id = id; }
    public void setAmount(Double a)              { this.amount = a; }
    public void setCategory(String c)            { this.category = c; }
    public void setDescription(String d)         { this.description = d; }
    public void setFrequency(String f)           { this.frequency = f; }
    public void setNextDueDate(LocalDate d)      { this.nextDueDate = d; }
    public void setIsActive(Boolean a)           { this.isActive = a; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }
    public void setUser(User u)                  { this.user = u; }
}