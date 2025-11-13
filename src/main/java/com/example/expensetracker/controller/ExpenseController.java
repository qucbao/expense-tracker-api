package com.example.expensetracker.controller;

import com.example.expensetracker.dto.request.ExpenseCreateRequest;
import com.example.expensetracker.dto.request.ExpenseUpdateRequest;
import com.example.expensetracker.dto.response.ExpenseResponse;
import com.example.expensetracker.security.CustomUserDetails;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    private Long currentUserId() {
        return ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getId();
    }

    // ---------------- CREATE ----------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@Valid @RequestBody ExpenseCreateRequest req) {
        return expenseService.create(currentUserId(), req);
    }

    // ---------------- READ (LIST + FILTER) ----------------

    /**
     * Lấy danh sách expense với optional filter:
     * - Không truyền gì -> tất cả expense của user (mới nhất trước)
     * - date=yyyy-MM-dd -> filter theo 1 ngày
     * - from & to -> filter theo khoảng ngày [from, to]
     */
    @GetMapping
    public List<ExpenseResponse> list(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        Long userId = currentUserId();

        if (date != null) {
            return expenseService.getByDate(userId, date);
        }

        if (from != null && to != null) {
            return expenseService.getByRange(userId, from, to);
        }

        // mặc định: lấy tất cả
        return expenseService.getAll(userId);
    }

    // ---------------- UPDATE ----------------

    @PutMapping("/{id}")
    public ExpenseResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseUpdateRequest req) {
        return expenseService.update(currentUserId(), id, req);
    }

    // ---------------- DELETE ----------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        expenseService.delete(currentUserId(), id);
    }
}
