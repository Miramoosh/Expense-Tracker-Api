package com.example.expensetracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // This exact signature tells Spring how to build the SQL query
    List<Expense> findByUserEmailAndExpenseDateBetween(String userEmail, LocalDate startDate, LocalDate endDate);

}