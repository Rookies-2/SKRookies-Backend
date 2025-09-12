package com.agit.peerflow.dto.message;

public record ReadReceiptDTO(
        Long userId,
        Long lastReadMessageId
) {
}
