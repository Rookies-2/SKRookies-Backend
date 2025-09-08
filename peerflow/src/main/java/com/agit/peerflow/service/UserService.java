package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.domain.enums.UserStatus;
import com.agit.peerflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author    Do
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 *  - 사용자 관련 비즈니스 로직을 담당하는 서비스 클래스
 *  - 사용자 조회
 *  - 사용자 등록
 *  - 사용자 상태 변경
 *  - 인증된 사용자 정보 반환
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    //username으로 사용자 조회
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // nickname으로 사용자 조회
    public User getByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("닉네임에 해당하는 사용자가 없습니다."));
    }

    // 사용자 상태 변경
    @Transactional
    public void changeStatus(User user, UserStatus status) {
        user.changeStatus(status);
    }

    @Transactional
    public User register(String username, String rawPassword, String nickname, UserRole role) {
        // 이미 존재하는 유저인지 확인
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }

        // 비밀번호 암호화
        //String encodedPassword = passwordEncoder.encode(rawPassword);

        // 기본 권한/상태 설정
        UserRole finalRole = (role != null) ? role : UserRole.ANONYMOUS;

        // 네가 만든 createUser() 사용
        User user = User.createUser(
                username,
                rawPassword,
                nickname,
                finalRole,
                UserStatus.ACTIVE
        );

        return userRepository.save(user);
    }




}
