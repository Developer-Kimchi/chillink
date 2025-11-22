package com.example.chilink.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 장치를 나타내는 엔티티 클래스
 * 장치 ID, 이름, 상태, 생성/수정일을 관리
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 장치 이름 */
    @Column(nullable = false)
    private String name;

    /** 장치 상태 (ON/OFF) */
    @Column(nullable = false)
    private Boolean status;

    /** 장치 생성 시간 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 장치 수정 시간 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
