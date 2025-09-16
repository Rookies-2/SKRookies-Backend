package com.agit.peerflow.service.impl; // [변경] impl 패키지로 이동

import com.agit.peerflow.domain.entity.UserChatRoom;
import com.agit.peerflow.exception.BusinessException; // [추가]
import com.agit.peerflow.exception.ErrorCode; // [추가]
import com.agit.peerflow.repository.UserChatRoomRepository;
import com.agit.peerflow.service.UserChatRoomService; // [추가]
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserChatRoomServiceImpl implements UserChatRoomService { // [변경] 인터페이스 구현

    private final UserChatRoomRepository userChatRoomRepository;

    @Override
    @Transactional // [유지] 쓰기 작업이므로 트랜잭션 적용
    public void toggleMute(String username, Long roomId) {
        // 1. 참여 정보 조회 (헬퍼 메서드 사용)
        UserChatRoom link = findLink(username, roomId);

        // 2. 알림 끄기 상태 변경
        link.toggleMute();
    }

    @Override
    @Transactional // [유지] 쓰기 작업이므로 트랜잭션 적용
    public void togglePin(String username, Long roomId) {
        // 1. 참여 정보 조회 (헬퍼 메서드 사용)
        UserChatRoom link = findLink(username, roomId);

        // 2. 상단 고정 상태 변경
        link.togglePinned();
    }

    /**
     * [추가] 참여 정보 조회 헬퍼 메서드 (중복 제거)
     */
    private UserChatRoom findLink(String username, Long roomId) {
        return userChatRoomRepository
                .findByUserUsernameAndChatRoomId(username, roomId)
                // [수정] EntityNotFoundException -> BusinessException
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "UserChatRoom", "username/roomId", username + "/" + roomId));
    }
}