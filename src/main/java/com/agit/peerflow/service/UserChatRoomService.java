package com.agit.peerflow.service;

public interface UserChatRoomService {

    /**
     * 채팅방 알림 끄기/켜기 상태를 토글합니다.
     */
    void toggleMute(String username, Long roomId);

    /**
     * 채팅방 상단 고정/해제 상태를 토글합니다.
     */
    void togglePin(String username, Long roomId);
}