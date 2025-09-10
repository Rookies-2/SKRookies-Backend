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

    // 1. 비밀번호 재설정 토큰 생성 및 이메일 발송
    public void sendPasswordResetToken(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) throw new RuntimeException("User not found");

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiration(LocalDateTime.now().plusMinutes(30)); // 30분 유효
        userRepository.save(user);

        // 이메일 발송
        String subject = "비밀번호 재설정 안내";
        String text = "안녕하세요!\n\n아래 링크를 클릭하여 비밀번호를 재설정하세요:\n"
                + "http://localhost:8080/api/auth/password/update?token=" + token
                + "\n\n(30분 동안 유효합니다)";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    // 2. 토큰 검증 후 비밀번호 변경
    public void resetPassword(String token, String newPassword) {
        Optional<User> optionalUser = userRepository.findByPasswordResetToken(token);
        if(optionalUser.isEmpty()) throw new RuntimeException("Invalid token");

        User user = optionalUser.get();

        if(user.getPasswordResetTokenExpiration() == null ||
                user.getPasswordResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiration(null);
        userRepository.save(user);
    }
}
