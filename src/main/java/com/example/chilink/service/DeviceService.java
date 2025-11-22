package com.example.chilink.service;

import com.example.chilink.domain.Device;
import com.example.chilink.domain.DeviceLog;
import com.example.chilink.infrastructure.netty.DeviceEventPublisher;
import com.example.chilink.repository.DeviceLogRepository;
import com.example.chilink.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Device 관련 비즈니스 로직 처리 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceLogRepository deviceLogRepository;
    private final DeviceEventPublisher eventPublisher;

    /**
     * 모든 장치 조회
     */
    public List<Device> getAllDevices() {
        log.info("[DeviceService] 모든 장치 조회");
        return deviceRepository.findAll();
    }

    /**
     * 장치 ID로 조회
     */
    public Device getDevice(Long id) {
        log.info("[DeviceService] 장치 조회, id={}", id);
        return deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장치 id=" + id));
    }

    /**
     * 장치 상태 변경 (ON/OFF)
     */
    @Transactional
    public Device toggleDeviceStatus(Long id) {
        Device device = getDevice(id);
        device.setStatus(!device.getStatus());
        deviceRepository.save(device);

        log.info("[DeviceService] 장치 상태 변경, id={}, newStatus={}", id, device.getStatus());

        // 상태 변경 로그 기록
        DeviceLog logEntry = DeviceLog.builder()
                .device(device)
                .status(device.getStatus())
                .build();
        deviceLogRepository.save(logEntry);
        log.info("[DeviceService] 장치 로그 기록, deviceId={}, status={}", id, device.getStatus());

        return device;
    }

    public Device setDeviceStatus(Long deviceId, boolean status) {
        Device device = deviceRepository.findById(Long.valueOf(deviceId))
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setStatus(status);
        deviceRepository.save(device);

        // 상태 변경 이벤트 발행
        eventPublisher.publish(device);
        return device;
    }

    public boolean getDeviceStatus(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"))
                .getStatus();
    }
}