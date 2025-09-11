package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MailService(JavaMailSender mailSender, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1️⃣ 인증번호 생성 및 이메일 발송
    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 6자리 인증번호 생성
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        user.setVerificationCode(code);
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(1)); // 1분 유효
        userRepository.save(user);

        // 실제 메일 발송
        String subject = "비밀번호 재설정 인증번호";
        String text = "인증번호: " + code + "\n1분 안에 입력해주세요.";
        sendMail(email, subject, text);
    }

    // 2️⃣ 인증번호 기반 비밀번호 변경
    public void resetPasswordByCode(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new RuntimeException("Invalid verification code");
        }

        if (user.getVerificationCodeExpiration() == null ||
                user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired");
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newPassword));

        // 사용한 인증번호 초기화
        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);

        userRepository.save(user);
    }

    // 3️⃣ 실제 메일 전송
    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
