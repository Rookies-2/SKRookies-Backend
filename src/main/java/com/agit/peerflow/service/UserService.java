package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.dto.UserDTO;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 업로드 저장 위치(프로젝트 루트 기준). 디렉터리는 없으면 자동 생성됨 */
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars";

    // 회원가입
    public User signupUser(UserDTO.Request request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword())) // 암호화
                .role(request.getRole()) // STUDENT, TEACHER 등
                .status(UserStatus.PENDING)
                // avatarUrl 은 가입 시엔 null 이어도 됨
                .build();
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화
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

     // (ID로) 프로필 이미지 업로드 후 DB에 avatarUrl 저장

    public User uploadAvatarById(Long id, MultipartFile file) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String savedPath = saveAvatarFile(file, id);
        user.setAvatarUrl(savedPath);            // User 엔티티에 avatarUrl 필드가 있어야 함
        return userRepository.save(user);
    }


     //(이메일로) 프로필 이미지 업로드 후 DB에 avatarUrl 저장

    public User uploadAvatarByEmail(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String savedPath = saveAvatarFile(file, user.getUserId());
        user.setAvatarUrl(savedPath);
        return userRepository.save(user);
    }


    //실제 파일 저장 처리 (폴더 자동 생성 + 고유 파일명 생성)
    private String saveAvatarFile(MultipartFile file, Long key) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        // 저장 디렉터리 보장
        Path uploadDir = Paths.get(AVATAR_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
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

        // 저장
        Files.write(target, file.getBytes());

        // 브라우저에서 접근할 때를 고려하여 상대경로 저장
        // (정적 리소스 매핑이 '/uploads/**' 로 설정되어 있다면 그대로 사용 가능)
        return AVATAR_UPLOAD_DIR + "/" + fileName;
    }
}

