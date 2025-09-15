package com.agit.peerflow.dto.message;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record SendMessageRequestDTO(
   @NotBlank String roomId,
   @NotBlank String content,
   String receiverId,
   String fileUrl,
   @NotNull(message="메시지 타입은 필수입니다. 기본타입: TEXT") MessageType type
) {}
