package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses;

    // ── Constructors ──────────────────────────────────────────
    public User() {}

    public User(Long id, String username, String email,
                String password, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    // ── Getters ───────────────────────────────────────────────
    public Long getId()                  { return id; }
    public String getUsername()          { return username; }
    public String getEmail()             { return email; }
    public String getPassword()          { return password; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public List<Expense> getExpenses()   { return expenses; }

    // ── Setters ───────────────────────────────────────────────
    public void setId(Long id)                      { this.id = id; }
    public void setUsername(String username)        { this.username = username; }
    public void setEmail(String email)              { this.email = email; }
    public void setPassword(String password)        { this.password = password; }
    public void setCreatedAt(LocalDateTime c)       { this.createdAt = c; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
}