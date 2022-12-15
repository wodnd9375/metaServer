package com.kt.geniestore.meta.apkmanagement.repository;

import com.kt.geniestore.meta.apkmanagement.entity.App;
import com.kt.geniestore.meta.apkmanagement.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Developer findByApp(App app);
}
