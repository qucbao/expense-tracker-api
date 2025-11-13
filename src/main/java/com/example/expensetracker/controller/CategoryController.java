package com.example.expensetracker.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.expensetracker.dto.request.CategoryCreateRequest;
import com.example.expensetracker.dto.request.CategoryUpdateRequest;
import com.example.expensetracker.dto.response.CategoryResponse;
import com.example.expensetracker.security.CustomUserDetails;
import com.example.expensetracker.service.CategoryService;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private Long currentUserId() {
        return ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryCreateRequest req) {
        return categoryService.create(currentUserId(), req);
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll(currentUserId());
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest req) {
        return categoryService.update(currentUserId(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestParam(required = false) Long transferTo) {
        categoryService.deleteWithTransfer(currentUserId(), id, transferTo);
    }

}
