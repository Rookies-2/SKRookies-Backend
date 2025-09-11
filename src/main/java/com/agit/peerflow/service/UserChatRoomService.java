package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.UserChatRoom;
import com.agit.peerflow.repository.UserChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserChatRoomService {
    private final UserChatRoomRepository userChatRoomRepository;

    @Transactional
    public void toggleMute(String username, Long roomId) {
        UserChatRoom link = userChatRoomRepository
                .findByUserUsernameAndChatRoomId(username, roomId)
                .orElseThrow(() -> new EntityNotFoundException("참여 정보가 없습니다."));
        link.toggleMute();
    }

    @Transactional
    public void togglePin(String username, Long roomId) {
        UserChatRoom link = userChatRoomRepository
                .findByUserUsernameAndChatRoomId(username, roomId)
                .orElseThrow(() -> new EntityNotFoundException("참여 정보가 없습니다."));
        link.togglePinned();
    }

}
