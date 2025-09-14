package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.Message;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.ChatRoomType;
import com.agit.peerflow.dto.chatroom.ChatRoomResponseDTO;
import com.agit.peerflow.dto.chatroom.CreateRoomRequestDTO;
import com.agit.peerflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author    백두현
 * @version   2.0
 * @since     2025-09-11
 * @description
 * - 채팅방 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final MessageRepository messageRepository;

    public ChatRoom createRoom(CreateRoomRequestDTO request, User creator) {
        // getter 대신 record의 접근자 메소드를 사용합니다.
        ChatRoom newRoom = ChatRoom.create(request.roomName(), request.type());
        chatRoomRepository.save(newRoom);

        ChatParticipant creatorParticipant = ChatParticipant.create(creator, newRoom);
        chatParticipantRepository.save(creatorParticipant);

        if (request.type() == ChatRoomType.ONE_TO_ONE && request.targetUserId() != null) {
            User targetUser = userRepository.findById(request.targetUserId())
                    .orElseThrow(() -> new IllegalArgumentException("상대방 사용자를 찾을 수 없습니다."));
            ChatParticipant targetParticipant = ChatParticipant.create(targetUser, newRoom);
            chatParticipantRepository.save(targetParticipant);
        }
        return newRoom;
    }

    @Transactional(readOnly = true)
    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(User user) {
        return chatParticipantRepository.findByUser(user)
                .stream()
                .map(ChatParticipant::getChatRoom)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDTO> findUnreadMessagesPerRoom(String userName) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByUserUsername(userName);

        return participants.stream().map(participant-> {
           ChatRoom room = participant.getChatRoom();
           Long lastReadMessageId = participant.getLastReadMessageId() == null ? 0L : participant.getLastReadMessageId();

           // 마지막으로 읽은 메시지 ID 이후에 온 메시지 수를 계산
           long unreadCount = messageRepository.countByChatRoomIdAndIdGreaterThan(room.getId(), lastReadMessageId);

           // 채팅방의 가장 최근 메시지 시각
            LocalDateTime updatedAt = messageRepository.findTopByChatRoomIdOrderBySentAtDesc(room.getId())
                                                       .map(Message::getSentAt)
                                                       .orElse(null);

            // 최근 메시지가 오늘이면 오전/오후 몇시 몇분, 아니면 yyyy-mm-dd
            String formattedDate = null;
            if(updatedAt != null) {
                if(updatedAt.toLocalDate().isEqual(LocalDate.now())) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm")
                            .withLocale(Locale.KOREA);
                } else {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    formattedDate = updatedAt.format(dateFormatter);
                }
            }

            return new ChatRoomResponseDTO(
                room.getId(),
                room.getRoomName(),
                room.getType(),
                room.getUserChatRooms().size(),
                unreadCount,
                formattedDate
           );
        }).collect(Collectors.toList());
    }

    // 모든 채팅방 리스트 조회
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDTO> findAllChatRooms() {
        List<ChatRoom> rooms = userChatRoomRepository.findAllChatRooms();

        return rooms.stream()
                .map(room -> {
                    // 각 방의 최근 메시지 시간을 조회하여 포맷팅합니다.
                    String lastMessageDate = messageRepository.findTopByChatRoomIdOrderBySentAtDesc(room.getId())
                            .map(message -> {
                                // 기존 로직을 재활용하여 날짜 포맷팅
                                LocalDateTime sentAt = message.getSentAt();
                                if (sentAt.toLocalDate().isEqual(LocalDate.now())) {
                                    return sentAt.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA));
                                } else {
                                    return sentAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                }
                            })
                            .orElse(null); // 메시지가 없는 방은 null
                    return ChatRoomResponseDTO.from(room, 0, lastMessageDate);
                })
                .collect(Collectors.toList());
    }

    // 참여자의 방 삭제
    @Transactional
    public void deleteRoom(ChatRoom chatRoom) {
        // 채팅 메시지 삭제
        messageRepository.deleteByChatRoom(chatRoom);

        // 참여자 삭제
        chatParticipantRepository.deleteByChatRoom(chatRoom);

        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }
}