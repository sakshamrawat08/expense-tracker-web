package com.expensetracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String to, String username) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Welcome to Expense Tracker!");
            msg.setText(
                "Hi " + username + ",\n\n" +
                "Welcome to Expense Tracker!\n\n" +
                "You can now track your expenses, set budgets, " +
                "upload bills and view detailed charts.\n\n" +
                "Visit: https://expense-tracker-web.up.railway.app\n\n" +
                "Happy tracking!\n" +
                "Expense Tracker Team"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    public void sendBudgetAlert(String to, String username,
                                 String category, Double limit,
                                 Double spent) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Budget Alert: " + category + " limit exceeded!");
            msg.setText(
                "Hi " + username + ",\n\n" +
                "You have exceeded your budget for " + category + "!\n\n" +
                "Budget Limit : Rs." + String.format("%.2f", limit) + "\n" +
                "Amount Spent : Rs." + String.format("%.2f", spent) + "\n" +
                "Exceeded by  : Rs." + String.format("%.2f", spent - limit) + "\n\n" +
                "Visit your dashboard to review your expenses.\n\n" +
                "Expense Tracker Team"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("Budget alert email failed: " + e.getMessage());
        }
    }

    public void sendWeeklySummary(String to, String username,
                                   Double totalSpent, int totalExpenses) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Your Weekly Expense Summary");
            msg.setText(
                "Hi " + username + ",\n\n" +
                "Here is your weekly expense summary:\n\n" +
                "Total Spent   : Rs." + String.format("%.2f", totalSpent) + "\n" +
                "Total Entries : " + totalExpenses + "\n\n" +
                "Visit your dashboard for detailed charts and analysis.\n" +
                "https://expense-tracker-web.up.railway.app\n\n" +
                "Expense Tracker Team"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("Weekly summary email failed: " + e.getMessage());
        }
    }
}