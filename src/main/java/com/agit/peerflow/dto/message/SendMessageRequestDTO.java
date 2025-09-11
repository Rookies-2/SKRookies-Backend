package com.agit.peerflow.dto.message;

import com.agit.peerflow.domain.enums.MessageType;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record SendMessageRequestDTO(
   @NotBlank String roomId,
   @NotBlank String content,
   String receiverId,
   String fileUrl,
   @NotBlank MessageType type
) {}
