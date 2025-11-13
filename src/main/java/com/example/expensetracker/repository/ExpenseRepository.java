package com.example.expensetracker.repository;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;

import jakarta.transaction.Transactional;

import com.example.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Lấy tất cả expense của 1 user (sort mới nhất)
    List<Expense> findAllByUserOrderByDateDesc(User user);

    // Lọc theo category
    List<Expense> findAllByUserAndCategoryOrderByDateDesc(User user, Category category);

    // Lọc theo ngày cụ thể
    List<Expense> findAllByUserAndDateOrderByDateDesc(User user, LocalDate date);

    // Lọc theo khoảng ngày
    List<Expense> findAllByUserAndDateBetweenOrderByDateDesc(
            User user,
            LocalDate from,
            LocalDate to);

    // Kiểm tra expense thuộc user
    Expense findByIdAndUser(Long id, User user);

    boolean existsByCategory_Id(Long categoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
            UPDATE Expense e
            SET e.category = :newCategory
            WHERE e.user = :user AND e.category = :oldCategory
            """)
    void updateCategoryForUser(
            @Param("user") User user,
            @Param("oldCategory") Category oldCategory,
            @Param("newCategory") Category newCategory);

}
