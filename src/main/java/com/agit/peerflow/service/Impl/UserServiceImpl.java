package com.agit.peerflow.service.Impl;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User register(String username, String rawPassword, String nickname, String email, String profileImageUrl) {
        User user = User.createUser(
                username,
                passwordEncoder.encode(rawPassword),
                nickname,
                email,
                UserRole.STUDENT,
                UserStatus.ACTIVE
        );
        return userRepository.save(user);
    }

    // 회원가입
    public User signupUser(UserDTO.Request request) {
        User user = User.createUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // 암호화
                request.getNickname(),
                request.getEmail(),
                request.getRole(), // STUDENT, TEACHER 등
                UserStatus.PENDING
        );
        return userRepository.save(user);
    }

    // 본인 정보 조회 (이메일)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 본인 정보 수정 (이메일)
    public User updateUserByEmail(String email, UserDTO.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화
        }

        return userRepository.save(user);

    }

    // 사용자 수정 (id)
    public User updateUserById(Long id, UserDTO.Request request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화
        return userRepository.save(user);
    }

    // 본인 계정 삭제 (이메일)
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // id로 사용자 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // id로 사용자 삭제
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}
