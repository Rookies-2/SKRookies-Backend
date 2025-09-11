package com.agit.peerflow.dto.message;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record SendMessageRequestDTO(
   @NotBlank String roomId,
   @NotBlank String content
) {}
