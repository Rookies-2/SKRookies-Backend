package com.agit.peerflow.service.impl;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ChatRoomType;
import com.agit.peerflow.domain.enums.ParticipantType;
import com.agit.peerflow.dto.chatroom.ChatRoomResponseDTO;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;
import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.repository.*;
import com.agit.peerflow.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 클래스 레벨에서 readOnly 트랜잭션 적용
public class ChatRoomServiceImpl implements ChatRoomService { // 인터페이스 구현

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional // 쓰기 작업(CUD)이므로 readOnly 해제
    public ChatRoom createRoom(CreateRoomRequestDTO request, User creator) {
        // ChatRoom 엔티티 생성 및 저장
        ChatRoom newRoom = ChatRoom.create(request.roomName(), request.type());
        chatRoomRepository.save(newRoom);

        // 방 생성자 참여 처리
        ChatParticipant creatorParticipant = ChatParticipant.create(creator, newRoom);
        chatParticipantRepository.save(creatorParticipant);

        // 1:1 채팅인 경우 상대방도 참여 처리
        if (request.type() == ChatRoomType.ONE_TO_ONE && request.receiverId() != null) {
            User targetUser = userRepository.findByEmail(request.receiverId())
                    // 예외 처리: RuntimeException -> BusinessException
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User", "email", request.receiverId()));
            ChatParticipant targetParticipant = ChatParticipant.create(targetUser, newRoom);
            chatParticipantRepository.save(targetParticipant);
        }
        return newRoom;
    }

    @Override
    @Transactional(readOnly = true) // 조회 메서드이므로 readOnly 명시
    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                // 예외 처리: RuntimeException -> BusinessException
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ChatRoom", "id", String.valueOf(roomId)));
    }

    @Override
    @Transactional(readOnly = true) // 조회 메서드이므로 readOnly 명시
    public List<ChatRoom> getMyChatRooms(User user) {
        return chatParticipantRepository.findByUser(user)
                .stream()
                .map(ChatParticipant::getChatRoom)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDTO> findUnreadMessagesPerRoomIncludingGroups(String userName) {
        // (성능 경고: 이 메서드는 N+1 쿼리 문제가 발생할 수 있습니다.)

        // 사용자가 active한 상태인 방 조회
        List<ChatParticipant> participants = chatParticipantRepository.findAllByUserUsernameAndStatus(userName, ParticipantType.ACTIVE);
        // 모든 그룹채팅방 조회
        List<ChatRoom> groupRooms = chatRoomRepository.findByType(ChatRoomType.GROUP);

        // Set으로 중복 제거하여 합치기
        Set<ChatRoom> allRooms = new HashSet<>();
        participants.forEach(p -> allRooms.add(p.getChatRoom()));
        allRooms.addAll(groupRooms);

        return allRooms.stream().map(room -> {
            Long lastReadMessageId = participants.stream()
                    .filter(p -> p.getChatRoom().equals(room))
                    .map(ChatParticipant::getLastReadMessageId)
                    .findFirst()
                    .orElse(0L);

            // (stream 내부 N+1 쿼리 1) 안 읽은 메시지 카운트
            long unreadCount = messageRepository.countByChatRoomIdAndIdGreaterThan(room.getId(), lastReadMessageId);

            // (stream 내부 N+1 쿼리 2) 마지막 메시지 시간 조회
            LocalDateTime updatedAt = messageRepository.findTopByChatRoomIdOrderBySentAtDesc(room.getId())
                    .map(Message::getSentAt)
                    .orElse(null);

            // 날짜 포맷팅 (헬퍼 메서드 사용)
            String formattedDate = formatLastMessageDate(updatedAt);

            return new ChatRoomResponseDTO(
                    room.getId(),
                    room.getRoomName(),
                    room.getType(),
                    room.getUserChatRooms().size(),
                    unreadCount,
                    formattedDate
            );
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDTO> findAllChatRooms() {
        // (성능 경고: 이 메서드는 N+1 쿼리 문제가 발생할 수 있습니다.)

        List<ChatRoom> rooms = userChatRoomRepository.findAllChatRooms();

        return rooms.stream()
                .map(room -> {
                    // (stream 내부 N+1 쿼리) 마지막 메시지 시간 조회
                    String lastMessageDate = messageRepository.findTopByChatRoomIdOrderBySentAtDesc(room.getId())
                            .map(message -> {
                                // 날짜 포맷팅 (헬퍼 메서드 사용)
                                return formatLastMessageDate(message.getSentAt());
                            })
                            .orElse(null); // 메시지가 없는 방은 null
                    return ChatRoomResponseDTO.from(room, 0, lastMessageDate);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // 쓰기 작업(CUD)이므로 readOnly 해제
    public void deleteRoom(ChatRoom chatRoom) {
        // 채팅 메시지 삭제
        messageRepository.deleteByChatRoom(chatRoom);
        // 참여자 정보 삭제
        chatParticipantRepository.deleteByChatRoom(chatRoom);
        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }

    /**
     * 날짜 포맷팅 헬퍼 메서드 (중복 제거)
     */
    private String formatLastMessageDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        // 오늘 날짜면 시간 표시
        if (dateTime.toLocalDate().isEqual(LocalDate.now())) {
            return dateTime.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA));
        } else { // 이전 날짜면 날짜 표시
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}