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

import java.util.Optional;

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
                .userName(request.getUserName())
                .nickName(request.getNickName())
                .password(passwordEncoder.encode(request.getPassword())) // 암호화
                .role(request.getRole()) // STUDENT, TEACHER 등
                .status(UserStatus.PENDING)
                .build();
        return userRepository.save(user);
    }

    // 본인 정보 조회 (이메일)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));
    }

    // 본인 정보 수정 (이메일)
    @Transactional
    public User updateUserByEmail(String email, UserDTO.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        user.setUserName(request.getUserName());
        user.setNickName(request.getNickName());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화
        }

        return userRepository.save(user);
    }

    // 사용자 수정 (id)
    @Transactional
    public User updateUserById(Long id, UserDTO.Request request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));

        user.setUserName(request.getUserName());
        user.setNickName(request.getNickName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }// 암호화

        return userRepository.save(user);
    }

    // 본인 계정 삭제 (이메일)
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));
        userRepository.delete(user);
    }

    // id로 사용자 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));
    }

    // id로 사용자 삭제
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "id", id));
        userRepository.delete(user);
    }

    // 비밀번호 변경 (본인 확인 필요)
    @Transactional
    public User changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", email));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Password", "current password", "불일치");
        }

        // 현재 비밀번호와 새 비밀번호 동일 여부 체크
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_CURRENT);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
