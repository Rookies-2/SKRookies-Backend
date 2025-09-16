package com.agit.peerflow.service;

public interface MailService {

    /**
     * 인증번호를 생성하고 이메일로 발송합니다.
     * @param email 대상 이메일
     */
    void sendVerificationCode(String email);

    /**
     * 인증번호를 검증하고 새 비밀번호로 재설정합니다.
     * @param email 대상 이메일
     * @param code 인증번호
     * @param newPassword 새 비밀번호
     */
    void resetPasswordByCode(String email, String code, String newPassword);
}