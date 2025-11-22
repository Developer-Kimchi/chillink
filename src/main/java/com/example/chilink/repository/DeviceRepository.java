package com.example.chilink.repository;


import com.example.chilink.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Device 엔티티 DB 접근용 Repository
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}

