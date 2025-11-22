package com.example.chilink.repository;

import com.example.chilink.domain.DeviceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DeviceLog 엔티티 DB 접근용 Repository
 */
@Repository
public interface DeviceLogRepository extends JpaRepository<DeviceLog, Long> {
}
