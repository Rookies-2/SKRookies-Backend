package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;

public interface UserService {
    // 회원가입
    User signup(UserDTO.Request requestDTO);
    // 내 정보 조회
    User getMyInfo(String username);
    // 내 정보 수정
    void updateMyInfo(String username, UserDTO.Request requestDTO);
    // 회원 탈퇴
    void deleteMyAccount(String username);
    // 비밀번호 변경
    void changePassword(String username, String oldPassword, String newPassword);
}