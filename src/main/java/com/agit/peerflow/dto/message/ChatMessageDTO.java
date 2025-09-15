package com.agit.peerflow.dto.message;

import com.agit.peerflow.domain.entity.Message;

import java.time.ZoneId;

public record ChatMessageDTO(
        String roomId,
        String senderId,
        String content,
        SenderDTO sender,
        String receiverId,
        Long sentAtEpochMs
) {
    // 개인채팅 메시지 전송 용
    public static ChatMessageDTO fromOneToOne(Message message) {
        return new ChatMessageDTO(
                String.valueOf(message.getChatRoom().getId()),
                String.valueOf(message.getSender().getId()),
                message.getContent(),
                SenderDTO.from(message.getSender()),
                String.valueOf(message.getReceiver().getId()), // 1:1은 receiver가 반드시 존재
                message.getSentAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        );
    }

    // 그룹채팅 메시지 전송 용
    public static ChatMessageDTO fromGroup(Message message) {
        return new ChatMessageDTO(
                String.valueOf(message.getChatRoom().getId()),
                String.valueOf(message.getSender().getId()),
                message.getContent(),
                SenderDTO.from(message.getSender()),
                null, // 그룹 채팅은 수신자 없음
                message.getSentAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        );
    }

    // 메시지 목록 조회 용
    public static ChatMessageDTO from(Message message) {
        return new ChatMessageDTO(
                String.valueOf(message.getChatRoom().getId()),
                String.valueOf(message.getSender().getId()),
                message.getContent(),
                SenderDTO.from(message.getSender()),
                null,
                message.getSentAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        );
    }
}

