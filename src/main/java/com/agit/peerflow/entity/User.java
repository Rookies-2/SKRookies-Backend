package com.agit.peerflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자(User) Entity
 * - 학생, 강사, 시스템관리자 구분
 * - 회원가입 승인 상태 관리
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK, 자동 생성

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (로그인 ID)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String username; // 이름

    @Column(nullable = false, unique = true)
    private String nickname; // 닉네임

    /**
     * 역할(role)
     * - STUDENT : 학생
     * - INSTRUCTOR : 강사
     * - ADMIN : 시스템 관리자
     */
    @Column(nullable = false)
    private String role;

    /**
     * 상태(status)
     * - PENDING : 승인 대기
     * - ACTIVE : 승인 완료
     * - REJECTED : 승인 거부
     */
    @Column(nullable = false)
    private String status;
}
