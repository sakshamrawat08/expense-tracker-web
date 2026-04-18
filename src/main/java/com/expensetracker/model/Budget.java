package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double limitAmount;

    @Column(nullable = false)
    private String month; // "2026-04"

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Budget() {}

    public Long getId()              { return id; }
    public String getCategory()      { return category; }
    public Double getLimitAmount()   { return limitAmount; }
    public String getMonth()         { return month; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getUser()            { return user; }

    public void setId(Long id)                   { this.id = id; }
    public void setCategory(String c)            { this.category = c; }
    public void setLimitAmount(Double l)         { this.limitAmount = l; }
    public void setMonth(String m)               { this.month = m; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }
    public void setUser(User u)                  { this.user = u; }
}