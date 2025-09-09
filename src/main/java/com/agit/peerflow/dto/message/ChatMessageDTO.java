package com.agit.peerflow.dto.message;

import com.agit.peerflow.domain.entity.Message;

import java.time.ZoneId;

public record ChatMessageDTO(
        String roomId,
        String senderId,
        String content,
        Long sentAtEpochMs
) {
    public static ChatMessageDTO from(Message message) {
        return new ChatMessageDTO(
                String.valueOf(message.getChatRoom().getId()),
                String.valueOf(message.getSender().getId()),
                message.getContent(),
                message.getSentAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        );
    }
}

