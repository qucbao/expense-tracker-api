package com.example.expensetracker.repository;

import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserOrderByCreatedAtDesc(User user);

    boolean existsByUserAndName(User user, String name);

    Optional<Category> findByIdAndUser(Long id, User user);
}
