package com.agit.peerflow.dto;

public record ChatMessage(
    String roomId,      // 채팅방 ID
    String senderId,    // 보낸 사람 ID
    String content,     // 메시지 내용
    Long sentAtEpochMs  // 보낸 시각
) {}
