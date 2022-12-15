package com.kt.geniestore.meta.apkmanagement.repository;

import com.kt.geniestore.meta.apkmanagement.entity.App;
import com.kt.geniestore.meta.apkmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategory(String category);
}
