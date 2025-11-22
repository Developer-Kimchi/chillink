package com.example.chilink.service;

import com.example.chilink.domain.Device;
import com.example.chilink.domain.DeviceLog;
import com.example.chilink.repository.DeviceLogRepository;
import com.example.chilink.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DeviceService 단위 테스트
 * 장치 상태 변경, 로그 기록 동작 확인
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2 DB 사용
class DeviceServiceTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceLogRepository deviceLogRepository;

    private Device device;

    /**
     * 테스트용 장치 초기화
     */
    @BeforeEach
    void setUp() {
        device = Device.builder()
                .name("Test Device")
                .status(false) // 초기 OFF
                .build();
        deviceRepository.save(device);
        System.out.println("[Test] 초기 장치 생성: " + device.getName());
    }

    /**
     * 모든 장치 조회 테스트
     */
    @Test
    void testGetAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        assertFalse(devices.isEmpty());
        System.out.println("[Test] 모든 장치 조회 결과: " + devices.size() + "개");
    }

    /**
     * 장치 상태 토글 테스트
     */
    @Test
    void testToggleDeviceStatus() {
        Boolean oldStatus = device.getStatus();

        Device updated = deviceService.toggleDeviceStatus(device.getId());
        Boolean newStatus = updated.getStatus();

        assertNotEquals(oldStatus, newStatus);
        System.out.println("[Test] 장치 상태 토글 성공: 이전=" + oldStatus + ", 이후=" + newStatus);

        // 로그 기록 확인
        List<DeviceLog> logs = deviceLogRepository.findAll();
        assertFalse(logs.isEmpty());
        DeviceLog lastLog = logs.get(logs.size() - 1);
        assertEquals(newStatus, lastLog.getStatus());
        System.out.println("[Test] 장치 로그 기록 확인: 상태=" + lastLog.getStatus());
    }

    /**
     * 존재하지 않는 장치 조회 시 예외 테스트
     */
    @Test
    void testGetDevice_NotFound() {
        Long invalidId = 999L;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deviceService.getDevice(invalidId);
        });
        System.out.println("[Test] 존재하지 않는 장치 조회 시 예외 발생: " + exception.getMessage());
    }
}
