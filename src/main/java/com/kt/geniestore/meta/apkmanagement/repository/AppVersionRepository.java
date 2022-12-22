package com.kt.geniestore.meta.apkmanagement.repository;

import com.kt.geniestore.meta.apkmanagement.entity.App;
import com.kt.geniestore.meta.apkmanagement.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
    List<AppVersion> findByApp(App app);
    List<AppVersion> findAllByApp(App app);
}
