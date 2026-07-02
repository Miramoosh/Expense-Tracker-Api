package com.example.expensetracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserSettingsRepository settingsRepository; // Injected our new database

    @GetMapping("/")
    public String viewDashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "index";
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        // 1. Fetch or Create Settings (Defaults: $5000 Income, $1000 Target)
        UserSettings settings = settingsRepository.findById(email)
                .orElse(new UserSettings(email, 5000.0, 1000.0));

        // 2. Fetch Expenses & Calculate Total
        YearMonth currentMonth = YearMonth.now();
        List<Expense> monthlyExpenses = expenseRepository.findByUserEmailAndExpenseDateBetween(
                email, currentMonth.atDay(1), currentMonth.atEndOfMonth());

        double monthlyTotal = monthlyExpenses.stream().mapToDouble(Expense::getAmount).sum();

        // 3. Mathematical Operations for UI
        double remainingBalance = settings.getMonthlyIncome() - monthlyTotal;
        double spendingPercentage = (settings.getMonthlyIncome() > 0) ? (monthlyTotal / settings.getMonthlyIncome()) * 100 : 0;

        double savingsProgress = (remainingBalance >= settings.getSavingsTarget()) ? 100.0 :
                (remainingBalance > 0 ? (remainingBalance / settings.getSavingsTarget()) * 100 : 0);

        // 4. Aggregate Categories for the Distribution Bars
        Map<String, Double> categoryTotals = monthlyExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        // Inject variables into Thymeleaf
        model.addAttribute("userName", name);
        model.addAttribute("settings", settings);
        model.addAttribute("expenses", monthlyExpenses);
        model.addAttribute("newExpense", new Expense());
        model.addAttribute("total", monthlyTotal);
        model.addAttribute("currentMonth", currentMonth.getMonth().toString());
        model.addAttribute("remainingBalance", remainingBalance);
        model.addAttribute("spendingPercentage", spendingPercentage);
        model.addAttribute("savingsProgress", savingsProgress);
        model.addAttribute("categoryTotals", categoryTotals);

        return "index";
    }

    @PostMapping("/add")
    public String addExpense(@AuthenticationPrincipal OAuth2User principal, @ModelAttribute("newExpense") Expense expense) {
        if (principal != null) {
            expense.setUserEmail(principal.getAttribute("email"));
            expense.setExpenseDate(LocalDate.now());
            expenseRepository.save(expense);
        }
        return "redirect:/";
    }

    // New Endpoint to handle the Goal Updates
    @PostMapping("/updateSettings")
    public String updateSettings(@AuthenticationPrincipal OAuth2User principal, @ModelAttribute("settings") UserSettings settings) {
        if (principal != null) {
            settings.setUserEmail(principal.getAttribute("email"));
            settingsRepository.save(settings);
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "redirect:/";
    }
}