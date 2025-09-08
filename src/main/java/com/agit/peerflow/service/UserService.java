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
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword())) // 암호화
                .role(request.getRole()) // STUDENT, TEACHER 등
                .status(UserStatus.PENDING)
                .build();
        return userRepository.save(user);
    }

    // 본인 정보 조회 (이메일)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    // 본인 정보 수정 (이메일)
    @Transactional
    public User updateUserByEmail(String email, UserDTO.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화
        }

        return userRepository.save(user);
    }

    // 사용자 수정 (id)
    @Transactional
    public User updateUserById(Long id, UserDTO.Request request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }// 암호화

        return userRepository.save(user);
    }

    // 본인 계정 삭제 (이메일)
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
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
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    // 비밀번호 변경 (본인 확인 필요)
    public User changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
