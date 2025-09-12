package com.agit.peerflow.service.impl;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("userServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars";

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
                .avatarUrl(requestDTO.getAvatarUrl())
                .build();
        return userRepository.save(newUser);
    }

    public User uploadAvatarById(Long id, MultipartFile file)  {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String savedPath = saveAvatarFile(file, id);
        user.setAvatarUrl(savedPath);            // User 엔티티에 avatarUrl 필드가 있어야 함
        return userRepository.save(user);
    }

    //실제 파일 저장 처리 (폴더 자동 생성 + 고유 파일명 생성)
    public String saveAvatarFile(MultipartFile file, Long key) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        // 저장 디렉터리 보장
        Path uploadDir = Paths.get(AVATAR_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                System.out.println("Error creating directory");
            }
        }

        // 확장자 추출
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.lastIndexOf('.') != -1) {
            ext = originalName.substring(originalName.lastIndexOf('.')); // ".png"
        }

        // "userId_타임스탬프.ext" 형태의 파일명
        String fileName = key + "_" + System.currentTimeMillis() + ext;
        Path target = uploadDir.resolve(fileName);

        try {
            // 저장
            Files.write(target, file.getBytes());
        } catch (IOException e) {
            System.out.println("Error writing file");
        }

        // 브라우저에서 접근할 때를 고려하여 상대경로 저장
        // (정적 리소스 매핑이 '/uploads/**' 로 설정되어 있다면 그대로 사용 가능)
        return AVATAR_UPLOAD_DIR + "/" + fileName;
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
        user.updateProfile(null, requestDTO.getNickname());
        return user;

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

    @Override
    public User getById(String id) {
        return userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "User", "id", String.valueOf(id)
                ));
    }
}