package com.agit.peerflow.domain.enums;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-08
 * @description 사용자 계정 상태를 정의하는 Enum 클래스
 */
public enum UserStatus {
    PENDING_APPROVAL, // 가입 승인 대기
    ACTIVE,           // 활성 상태
    SUSPENDED         // 정지 상태
}