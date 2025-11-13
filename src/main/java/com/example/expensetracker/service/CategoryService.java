package com.example.expensetracker.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.expensetracker.dto.request.CategoryCreateRequest;
import com.example.expensetracker.dto.request.CategoryUpdateRequest;
import com.example.expensetracker.dto.response.CategoryResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

        private final CategoryRepository categoryRepo;
        private final UserRepository userRepo;
        private final ExpenseRepository expenseRepo;
        private final PasswordEncoder encoder;

        @Transactional
        public CategoryResponse create(Long userId, CategoryCreateRequest req) {
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                if (categoryRepo.existsByUserAndName(user, req.name().trim()))
                        throw new IllegalArgumentException("Category name already exists");

                Category c = Category.builder()
                                .name(req.name().trim())
                                .description(req.description())
                                .user(user)
                                .build();

                categoryRepo.save(c);

                return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getCreatedAt());
        }

        public List<CategoryResponse> getAll(Long userId) {
                User u = userRepo.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                return categoryRepo.findAllByUserOrderByCreatedAtDesc(u)
                                .stream()
                                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription(),
                                                c.getCreatedAt()))
                                .toList();
        }

        @Transactional
        public CategoryResponse update(Long userId, Long categoryId, CategoryUpdateRequest req) {
                User u = userRepo.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Category c = categoryRepo.findByIdAndUser(categoryId, u)
                                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

                if (!c.getName().equals(req.name())
                                && categoryRepo.existsByUserAndName(u, req.name().trim())) {
                        throw new IllegalArgumentException("Category name already exists");
                }

                c.setName(req.name().trim());
                c.setDescription(req.description());

                return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getCreatedAt());
        }

        @Transactional
        public void delete(Long userId, Long categoryId) {
                User u = userRepo.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Category c = categoryRepo.findByIdAndUser(categoryId, u)
                                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

                // Phase 3: Check if expense exists → block delete
                categoryRepo.delete(c);
        }

        @Transactional
        public void deleteWithTransfer(Long userId, Long categoryId, Long transferToId) {

                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Category oldCategory = categoryRepo.findByIdAndUser(categoryId, user)
                                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

                boolean hasExpenses = expenseRepo.existsByCategory_Id(oldCategory.getId());

                if (hasExpenses) {
                        if (transferToId == null) {
                                throw new IllegalArgumentException("Category is in use. Please specify transferTo.");
                        }

                        Category newCategory = categoryRepo.findByIdAndUser(transferToId, user)
                                        .orElseThrow(() -> new IllegalArgumentException("New category not found"));

                        if (newCategory.getId().equals(oldCategory.getId())) {
                                throw new IllegalArgumentException("Cannot transfer to the same category.");
                        }

                        // STEP 1: update trước
                        expenseRepo.updateCategoryForUser(user, oldCategory, newCategory);
                }

                // STEP 2: delete sau
                categoryRepo.delete(oldCategory);

        }

}
