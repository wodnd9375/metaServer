package com.kt.geniestore.meta.apkmanagement.repository;

import com.kt.geniestore.meta.apkmanagement.entity.App;
import com.kt.geniestore.meta.apkmanagement.entity.AppCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppCategoryMapRepository extends JpaRepository<AppCategoryMap, Long> {
    List<AppCategoryMap> findByApp(App app);
}
