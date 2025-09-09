package com.agit.peerflow.service.Impl;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User signup(UserDTO.Request requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "username", requestDTO.getUsername());
        }
        if (userRepository.existsByNickname(requestDTO.getNickname())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "nickname", requestDTO.getNickname());
        }
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "email", requestDTO.getEmail());
        }

        User newUser = User.builder()
                .username(requestDTO.getUsername())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .nickname(requestDTO.getNickname())
                .email(requestDTO.getEmail())
                .role(requestDTO.getRole())
                .build();
        return userRepository.save(newUser);
    }

    @Override
    public User getMyInfo(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "username", username));
    }

    @Override
    @Transactional
    public void updateMyInfo(String username, UserDTO.Request requestDTO) {
        User user = getMyInfo(username);
        user.updateProfile(null, requestDTO.getNickname()); // 본인은 닉네임만 변경 가능
    }

    @Override
    @Transactional
    public void deleteMyAccount(String username) {
        User user = getMyInfo(username);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = getMyInfo(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Password", "current password", "불일치");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_CURRENT);
        }
        user.changePassword(passwordEncoder.encode(newPassword));
    }
}