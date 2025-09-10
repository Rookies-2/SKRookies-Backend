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
        if (userRepository.existsByUserName(requestDTO.getUserName())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "username", requestDTO.getUserName());
        }
        if (userRepository.existsByNickName(requestDTO.getNickName())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "nickname", requestDTO.getNickName());
        }
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "User", "email", requestDTO.getEmail());
        }

        User newUser = User.builder()
                .userName(requestDTO.getUserName())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .nickName(requestDTO.getNickName())
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
    public User updateMyInfo(String username, UserDTO.Request requestDTO) {
        User user = getMyInfo(username);
<<<<<<< HEAD
        user.updateProfile(null, requestDTO.getNickname());
        return user;
=======
        user.updateProfile(null, requestDTO.getNickName()); // 본인은 닉네임만 변경 가능
>>>>>>> f6a98f1fa00588fe08cfc97f653c4ca10eb2e422
    }

    @Override
    @Transactional
    public void deleteMyAccount(String username) {
        User user = getMyInfo(username);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User changePassword(String username, String oldPassword, String newPassword) {
        User user = getMyInfo(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Password", "current password", "불일치");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_CURRENT);
        }
        user.changePassword(passwordEncoder.encode(newPassword));
        return user;
    }
}