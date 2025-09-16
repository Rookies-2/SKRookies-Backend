package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.MessageType;
import com.agit.peerflow.dto.message.ChatMessageDTO;

import java.util.List;

public interface MessageService {

    /**
     * 메시지를 DB에 저장하고, 발신자 정보를 포함하여 반환합니다.
     */
    Message sendMessage(ChatRoom room, User sender, User receiver, String content, MessageType type);

    /**
     * 채팅방 전체에 메시지 브로드캐스트 (예: /topic/rooms/{id})
     */
    void broadcastMessage(Long roomId, ChatMessageDTO dto);

    /**
     * 특정 사용자에게 1:1 메시지 전송 (예: /queue/messages/{id})
     */
    void privateMessage(String userName, ChatMessageDTO dto);

    /**
     * 특정 채팅방의 모든 메시지 조회 (오래된 순)
     */
    List<Message> getMessages(ChatRoom chatRoom);

    /**
     * 메시지 읽음 처리 및 읽음 확인 브로드캐스트
     */
    void markAsRead(Long roomId, String username, Long lastMessageId);
}