package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.MessageType;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.message.ReadReceiptDTO;
import com.agit.peerflow.repository.ChatParticipantRepository;
import com.agit.peerflow.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * @author    백두현
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * - 채팅 메시지 처리 로직을 담당하는 서비스 클래스
 * - 메시지 생성 및 저장
 * - 실시간 메시지 브로드캐스트
 * - 채팅방별 메시지 목록 조회
 * - 기본 트랜잭션은 읽기 전용이며, 메시지 전송 시에는 명시적 쓰기 트랜잭션 적용
 * - 메시지 마킹 표시
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate; // STOMP 메시지 전송을 위한 템플릿
    private final ChatParticipantRepository chatParticipantRepository;

    @Transactional
    public Message sendMessage(ChatRoom chatRoom, User sender, String content, MessageType type) {
        Message message = Message.createMessage(sender, chatRoom, content, type);
        return messageRepository.save(message);
    }

    public void broadcastMessage(Long roomId, ChatMessageDTO dto) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, dto);
    }

    public List<Message> getMessages(ChatRoom chatRoom) {
        return messageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);
    }

    @Transactional
    public void markAsRead(Long roomId, String username, Long lastMessageId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserUsername(roomId, username)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        // 마지막으로 읽은 메시지 ID를 세팅
        participant.updateLastReadMessageId(lastMessageId);

        // 다른 참여자들에게 "읽음" 정보를 브로드캐스트
        ReadReceiptDTO readReceipt = new ReadReceiptDTO(participant.getUser().getId(), lastMessageId);
        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId + "/read", readReceipt);
    }
}
