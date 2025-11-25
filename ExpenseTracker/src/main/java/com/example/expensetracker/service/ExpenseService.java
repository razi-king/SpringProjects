package com.example.expensetracker.service;

import java.util.ArrayList;
import java.util.List;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.Users;

public interface ExpenseService {
	Expense addExpense(ExpenseDto expenseDto);

	ArrayList<Expense> getAllExpense();

	List<Expense> findByUser(Users user);
}
