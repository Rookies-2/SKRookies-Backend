package com.agit.peerflow.dto.chatroom;

import com.agit.peerflow.domain.enums.ChatRoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
        @NotBlank String roomName,
        @NotNull ChatRoomType type
) {}
