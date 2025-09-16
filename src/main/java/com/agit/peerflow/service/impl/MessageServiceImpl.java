package com.agit.peerflow.service.impl; // [변경] impl 패키지로 이동

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.MessageType;
import com.agit.peerflow.dto.message.ChatMessageDTO;
import com.agit.peerflow.dto.message.ReadReceiptDTO;
import com.agit.peerflow.exception.BusinessException; // [추가]
import com.agit.peerflow.exception.ErrorCode; // [추가]
import com.agit.peerflow.repository.ChatParticipantRepository;
import com.agit.peerflow.repository.MessageRepository;
import com.agit.peerflow.service.MessageService; // [추가]
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * @author    백두현 [김현근 리팩토링]
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
@Transactional(readOnly = true) // [유지] 기본 트랜잭션은 읽기 전용
public class MessageServiceImpl implements MessageService { // [변경] 인터페이스 구현

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate; // STOMP 메시지 전송을 위한 템플릿
    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    @Transactional // 쓰기 작업이므로 readOnly 해제
    public Message sendMessage(ChatRoom room, User sender, User receiver, String content, MessageType type) {
        // 1. 메시지를 DB에 저장
        Message saved = messageRepository.save(
                Message.createMessage(sender, room, receiver, content, type)
        );

        // 2. 저장된 메시지를 발신자 정보와 함께 다시 조회 (Lazy Loading 방지)
        return messageRepository.findByIdWithSender(saved.getId())
                // [수정] EntityNotFoundException -> BusinessException
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Message", "id", String.valueOf(saved.getId())));
    }

    @Override
    public void broadcastMessage(Long roomId, ChatMessageDTO dto) {
        // 채팅방 토픽으로 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, dto);
    }

    @Override
    public void privateMessage(String userName, ChatMessageDTO dto) {
        // 사용자 개인 큐로 메시지 전송
        messagingTemplate.convertAndSendToUser(userName, "/queue/messages/" + dto.roomId(), dto);
    }

    @Override
    public List<Message> getMessages(ChatRoom chatRoom) {
        // 채팅방의 모든 메시지를 시간순으로 조회
        return messageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);
    }

    @Override
    @Transactional // 쓰기 작업이므로 readOnly 해제
    public void markAsRead(Long roomId, String username, Long lastMessageId) {
        // 1. 읽음 처리할 참여자 조회
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserUsername(roomId, username)
                // [수정] EntityNotFoundException -> BusinessException
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ChatParticipant", "roomId/username", roomId + "/" + username));

        // 2. 마지막으로 읽은 메시지 ID 업데이트
        participant.updateLastReadMessageId(lastMessageId);

        // 3. 다른 참여자들에게 "읽음" 정보 브로드캐스트
        ReadReceiptDTO readReceipt = new ReadReceiptDTO(participant.getUser().getId(), lastMessageId);
        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId + "/read", readReceipt);
    }
}