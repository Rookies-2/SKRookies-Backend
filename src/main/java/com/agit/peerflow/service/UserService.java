package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.UserDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 (기본 role=STUDENT or TEACHER, 상태=PENDING)
    @Transactional
    public User signupUser(UserDTO.Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "email", request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .userName(request.getUsername())
                .nickName(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword())) // 암호화
                .role(request.getRole()) // STUDENT, TEACHER 등
                .status(UserStatus.PENDING)
                .build();
        return userRepository.save(user);
    }//signupUser

    // 본인 정보 조회 (이메일)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));
    }//getUserByEmail

    // 본인 정보 수정 (이메일)
    @Transactional
    public User updateUserByEmail(String email, UserDTO.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        user.setUserName(request.getUsername());
        user.setNickName(request.getNickname());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화
        }

        return userRepository.save(user);
    }//updateUserByEmail

    // 사용자 수정 (id)
    @Transactional
    public User updateUserById(Long id, UserDTO.Request request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));

        user.setUserName(request.getUsername());
        user.setNickName(request.getNickname());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }// 암호화

        return userRepository.save(user);
    }//updateUserById

    // 본인 계정 삭제 (이메일)
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));
        userRepository.delete(user);
    }//deleteUserByEmail

    // id로 사용자 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));
    }//getUserById

    // id로 사용자 삭제
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));
        userRepository.delete(user);
    }//deleteUserById

    // 비밀번호 초기화 요청
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("등록된 사용자가 없습니다."));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 1시간 유효

        userRepository.save(user);

        // TODO: 이메일 전송 로직 추가 (MailService 등)
        System.out.println("Reset Token (임시): " + token);
    }

    // 비밀번호 변경 (본인 확인 필요)
    @Transactional
    public User changePassword(String email, String oldPassword, String newPassword) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        // 2. 입력된 현재 비밀번호(oldPassword) 확인
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Password", "current password", "불일치");
        }

        // 3. 새 비밀번호가 기존 비밀번호와 동일하면 거부
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_CURRENT);
        }

        // 4. 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));

        // 5. @Transactional 이므로 save 호출 시점에 DB update 발생
        return userRepository.save(user);
    }//changePassword
}
