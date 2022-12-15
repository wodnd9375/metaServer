package com.kt.geniestore.meta.apkmanagement.repository;

import com.kt.geniestore.meta.apkmanagement.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, Long> {
    App findByPackageName(String packageName);
}
