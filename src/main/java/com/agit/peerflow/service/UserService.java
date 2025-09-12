package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.user.UserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User signup(UserDTO.Request requestDTO);

    User getMyInfo(String username);

    User updateUsername(String email, String newUsername);
    User updateNickname(String email, String newNickname);

    void deleteMyAccount(String username);

    User changePassword(String username, String oldPassword, String newPassword);

    User getById(String id);

    User uploadAvatarById(Long id, MultipartFile file);

    String saveAvatarFile(MultipartFile file, Long key);

    User deleteAvatarById(Long id, String fileName);
}