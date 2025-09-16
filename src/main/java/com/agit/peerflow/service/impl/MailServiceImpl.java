package com.agit.peerflow.service.impl; // [변경] impl 패키지로 이동

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.exception.BusinessException; // [추가]
import com.agit.peerflow.exception.ErrorCode; // [추가]
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.MailService; // [추가]
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // [추가]

import java.time.LocalDateTime;

@Service
public class MailServiceImpl implements MailService { // [변경] 인터페이스 구현

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // [유지] 생성자 주입
    public MailServiceImpl(JavaMailSender mailSender, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 1️⃣ 인증번호 생성 및 이메일 발송
     */
    @Override
    @Transactional // [추가] DB 저장과 메일 발송을 하나의 트랜잭션으로 묶음
    public void sendVerificationCode(String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                // [수정] RuntimeException -> BusinessException
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        // 6자리 인증번호 생성
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        // [유지] 엔티티 setter 사용 (최소 변경 원칙)
        user.setVerificationCode(code);
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(1)); // 1분 유효
        userRepository.save(user); // DB에 인증번호 저장

        // 실제 메일 발송
        String subject = "비밀번호 재설정 인증번호";
        String text = "인증번호: " + code + "\n1분 안에 입력해주세요.";
        sendMail(email, subject, text); // 메일 발송 실패 시, @Transactional에 의해 DB 롤백됨
    }

    /**
     * 2️⃣ 인증번호 기반 비밀번호 변경
     */
    @Override
    @Transactional // [추가] DB 저장이 포함되므로 트랜잭션 적용
    public void resetPasswordByCode(String email, String code, String newPassword) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                // [수정] RuntimeException -> BusinessException
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        // 인증번호 검증
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            // [수정] RuntimeException -> BusinessException
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "인증번호가 올바르지 않습니다.");
        }

        // 인증번호 만료 시간 검증
        if (user.getVerificationCodeExpiration() == null ||
                user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            // [수정] RuntimeException -> BusinessException
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "인증번호가 만료되었습니다.");
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newPassword));

        // 사용한 인증번호 초기화
        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);

        userRepository.save(user); // 변경된 내용 DB에 저장
    }

    /**
     * 3️⃣ 실제 메일 전송 (private 헬퍼)
     */
    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}