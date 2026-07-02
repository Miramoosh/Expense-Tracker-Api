package com.example.expensetracker;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserSettings {

    @Id
    private String userEmail;

    private double monthlyIncome;
    private double savingsTarget;

    public UserSettings() {}

    public UserSettings(String userEmail, double monthlyIncome, double savingsTarget) {
        this.userEmail = userEmail;
        this.monthlyIncome = monthlyIncome;
        this.savingsTarget = savingsTarget;
    }

    // Getters and Setters
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public double getSavingsTarget() { return savingsTarget; }
    public void setSavingsTarget(double savingsTarget) { this.savingsTarget = savingsTarget; }
}