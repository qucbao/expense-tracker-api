package com.example.expensetracker.service;

import com.example.expensetracker.dto.request.ExpenseCreateRequest;
import com.example.expensetracker.dto.request.ExpenseUpdateRequest;
import com.example.expensetracker.dto.response.ExpenseResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;

    // ----------------------------------------
    // CREATE
    // ----------------------------------------
    @Transactional
    public ExpenseResponse create(Long userId, ExpenseCreateRequest req) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Category category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // category phải thuộc về user
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to user");
        }

        Expense e = Expense.builder()
                .amount(req.amount())
                .date(req.date())
                .note(req.note())
                .user(user)
                .category(category)
                .build();

        expenseRepo.save(e);

        return toResponse(e);
    }

    // ----------------------------------------
    // GET ALL
    // ----------------------------------------
    public List<ExpenseResponse> getAll(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return expenseRepo.findAllByUserOrderByDateDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ----------------------------------------
    // GET BY DATE
    // ----------------------------------------
    public List<ExpenseResponse> getByDate(Long userId, LocalDate date) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return expenseRepo.findAllByUserAndDateOrderByDateDesc(user, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ----------------------------------------
    // GET BY RANGE
    // ----------------------------------------
    public List<ExpenseResponse> getByRange(Long userId, LocalDate from, LocalDate to) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return expenseRepo.findAllByUserAndDateBetweenOrderByDateDesc(user, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------
    @Transactional
    public ExpenseResponse update(Long userId, Long expenseId, ExpenseUpdateRequest req) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Expense e = expenseRepo.findByIdAndUser(expenseId, user);
        if (e == null)
            throw new IllegalArgumentException("Expense not found");

        Category newCategory = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // category phải thuộc về user
        if (!newCategory.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to user");
        }

        e.setAmount(req.amount());
        e.setDate(req.date());
        e.setNote(req.note());
        e.setCategory(newCategory);
        expenseRepo.save(e);

        return toResponse(e);
        
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------
    @Transactional
    public void delete(Long userId, Long expenseId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Expense e = expenseRepo.findByIdAndUser(expenseId, user);
        if (e == null)
            throw new IllegalArgumentException("Expense not found");

        expenseRepo.delete(e);
    }

    // ----------------------------------------
    // MAPPER
    // ----------------------------------------
    private ExpenseResponse toResponse(Expense e) {
        return new ExpenseResponse(
                e.getId(),
                e.getAmount(),
                e.getDate(),
                e.getNote(),
                e.getCategory().getId(),
                e.getCategory().getName(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    
}
