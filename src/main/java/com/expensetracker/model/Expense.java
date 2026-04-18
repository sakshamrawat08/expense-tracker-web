package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    // Store time as plain String "HH:mm" — avoids Hibernate/Postgres bug
    @Column(name = "expense_time")
    private String expenseTime;

    @Column
    private String description;

    @Column(name = "bill_image_url")
    private String billImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ── Constructors ──────────────────────────────────────────
    public Expense() {}

    // ── Getters ───────────────────────────────────────────────
    public Long getId()                 { return id; }
    public Double getAmount()           { return amount; }
    public String getCategory()         { return category; }
    public LocalDate getDate()          { return date; }
    public String getExpenseTime()      { return expenseTime; }
    public String getDescription()      { return description; }
    public String getBillImageUrl()     { return billImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getUser()               { return user; }

    // ── Setters ───────────────────────────────────────────────
    public void setId(Long id)                { this.id = id; }
    public void setAmount(Double amount)      { this.amount = amount; }
    public void setCategory(String category)  { this.category = category; }
    public void setDate(LocalDate date)       { this.date = date; }
    public void setExpenseTime(String t)      { this.expenseTime = t; }
    public void setDescription(String desc)   { this.description = desc; }
    public void setBillImageUrl(String url)   { this.billImageUrl = url; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
    public void setUser(User user)            { this.user = user; }
}